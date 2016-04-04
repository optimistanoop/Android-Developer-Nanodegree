package com.anoop.android.udacitypopularmovies.detailScreen;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.adapters.MovieDetailAdapter;
import com.anoop.android.udacitypopularmovies.adapters.SyncAdapter;
import com.anoop.android.udacitypopularmovies.api.ApiConstants;
import com.anoop.android.udacitypopularmovies.db.MovieContract;
import com.anoop.android.udacitypopularmovies.models.Movie;
import com.anoop.android.udacitypopularmovies.models.Review;
import com.anoop.android.udacitypopularmovies.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hugo.weaving.DebugLog;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_LOADER = 1;
    private static final int REVIEW_LOADER = 2;
    private static final int VIDEO_LOADER = 3;
    private ImageView mBackdrop;
    private ActionBar mBar;
    private Movie mMovie;
    private FloatingActionButton mFavButton;
    private boolean mIsFavorite;
    private MovieDetailAdapter mAdapter;
    private ShareActionProvider mShareActionProvider;
    private String mShareUrl;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long movieId = args.getLong(ApiConstants.EXTRA_MOVIE);
        switch (id)
        {
            case MOVIE_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.buildMovieExternalIDUri(movieId),
                        Movie.ALL_COLUMN_PROJECTION,
                        null,
                        null,
                        null
                );

            case VIDEO_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.VideoEntry.buildMovieVideosUrl(movieId),
                        Video.ALL_COLUMN_PROJECTION,
                        null,
                        null,
                        null
                );

            case REVIEW_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.buildMovieReviewsUrl(movieId),
                        Review.ALL_COLUMN_PROJECTION,
                        null,
                        null,
                        null
                );

            default:
                throw new UnsupportedOperationException("Unsupported loader");
        }
    }

    @Override
    @DebugLog
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId())
        {
            case MOVIE_LOADER:
                data.moveToFirst();
                mMovie = new Movie(data);
                mAdapter.setMovie(mMovie);
                displayMovie();
                break;

            case VIDEO_LOADER:
                ArrayList<Video> videos = new ArrayList<>();
                while(data.moveToNext())
                {
                    Video video = new Video(data);
                    videos.add(video);
                }
                mAdapter.setVideos(videos);
                mShareUrl = null;
                for(Video video : videos)
                {
                    if(video.getType().equals("Trailer"))
                    {
                        mShareUrl = video.getVideoURI().toString();
                        break;
                    }
                }
                if(mShareUrl != null && mShareActionProvider != null)
                {
                    mShareActionProvider.setShareIntent(createShareVideoIntent());
                }
                break;

            case REVIEW_LOADER:
                ArrayList<Review> reviews = new ArrayList<>();
                while (data.moveToNext())
                {
                    Review review = new Review(data);
                    reviews.add(review);
                }
                mAdapter.setReviews(reviews);
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId())
        {
            case VIDEO_LOADER:
                mAdapter.setVideos(null);
                break;

            case REVIEW_LOADER:
                mAdapter.setReviews(null);
                break;
        }
    }

    public static MovieDetailFragment newInstance(long movieId)
    {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ApiConstants.EXTRA_MOVIE, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareUrl != null && mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareVideoIntent());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mBackdrop = (ImageView) rootView.findViewById(R.id.movieBackdrop);
        mFavButton = (FloatingActionButton) rootView.findViewById(R.id.fabFavorite);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavoriteClick();
            }
        });
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvMovieDetails);
        mAdapter = new MovieDetailAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle args = getArguments();
        long movieId = args.getLong(ApiConstants.EXTRA_MOVIE);
        SyncAdapter.syncMovieDetails(getActivity(), movieId);
        getLoaderManager().initLoader(REVIEW_LOADER, args, this);
        getLoaderManager().initLoader(VIDEO_LOADER, args, this);
        getLoaderManager().initLoader(MOVIE_LOADER, args, this);

        ViewGroup appBarLayout = (ViewGroup) rootView.findViewById(R.id.appbar);
        for (int i = 0; i < appBarLayout.getChildCount(); i++) {
            View childView = appBarLayout.getChildAt(i);
            if (!childView.isClickable()) {
                childView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }
        }

        return rootView;
    }

    private void displayMovie()
    {
        if(mBar != null)
        {
            mBar.setTitle(mMovie.getTitle());
            mBar.setElevation(0);
        }

        String path = mMovie.getBackdropPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(getActivity())
                    .load(ApiConstants.BACKDROP_BASE_URL + path)
                    .into(mBackdrop);
        }

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.DiscoverEntry.CONTENT_URI,
                null,
                MovieContract.DiscoverEntry.COLUMN_SORTING + " =  ?"
                        + " AND " + MovieContract.DiscoverEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{getActivity().getString(R.string.pref_sort_key_favorites), mMovie.getId().toString()},
                null
        );
        mIsFavorite = cursor.moveToFirst();
        cursor.close();

        setFavoriteIcon();
    }

    private void handleFavoriteClick()
    {
        if(mIsFavorite)
        {
            int deleted = getActivity().getContentResolver().delete(
                    MovieContract.DiscoverEntry.CONTENT_URI,
                    MovieContract.DiscoverEntry.COLUMN_SORTING + " =  ?"
                            + " AND " + MovieContract.DiscoverEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{getActivity().getString(R.string.pref_sort_key_favorites), mMovie.getId().toString()}
            );
            if(deleted > 0)
            {
                Toast.makeText(getActivity(),R.string.unfavorited,Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MovieContract.DiscoverEntry.COLUMN_SORTING, getActivity().getString(R.string.pref_sort_key_favorites));
            values.put(MovieContract.DiscoverEntry.COLUMN_ORDER, mMovie.getId());
            values.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, mMovie.getId());
            Uri newEntry = getActivity().getContentResolver().insert(
                    MovieContract.DiscoverEntry.CONTENT_URI,
                    values
            );
            long _id = ContentUris.parseId(newEntry);
            if(_id > 0)
            {
                Toast.makeText(getActivity(),R.string.favorited,Toast.LENGTH_SHORT).show();
            }

        }

        mIsFavorite = !mIsFavorite;
        setFavoriteIcon();
    }

    private void setFavoriteIcon()
    {
        if(mIsFavorite)
        {
            mFavButton.setImageResource(R.drawable.ic_star_black_24dp);
        }
        else
        {
            mFavButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    @DebugLog
    private Intent createShareVideoIntent()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT, getActivity().getString(R.string.sharing, mMovie.getTitle(), mShareUrl));
        intent.setType("text/plain");
        return intent;
    }
}
