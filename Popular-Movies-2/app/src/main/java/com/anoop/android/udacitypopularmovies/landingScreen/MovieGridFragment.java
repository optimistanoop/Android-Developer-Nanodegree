package com.anoop.android.udacitypopularmovies.landingScreen;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.adapters.MovieAdapter;
import com.anoop.android.udacitypopularmovies.adapters.SyncAdapter;
import com.anoop.android.udacitypopularmovies.db.MovieContract;

public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_TITLE = 3;

    private static final int MOVIE_LOADER = 1;

    private static final String MOVIE_SORT = "movies.sort";
    private String mSortKey;
    private MovieAdapter mMovieAdapter;

    private String getCurrentSort()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(getActivity().getString(R.string.pref_sort_key), getActivity().getString(R.string.pref_sort_default));
    }

    public void onPreferencesChanges()
    {
        String sortKey = getCurrentSort();
        if(sortKey.equals(mSortKey))
        {
            return;
        }
        mSortKey = sortKey;

        discoverMovies();
        // Fire up the new loader
        Bundle args = new Bundle();
        args.putString(MOVIE_SORT, mSortKey);
        getLoaderManager().restartLoader(MOVIE_LOADER, args, this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == MOVIE_LOADER)
        {
            String sortKey = args.getString(MOVIE_SORT);

            return new CursorLoader(
                    getActivity(),
                    MovieContract.MovieEntry.buildMovieDiscovery(sortKey),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = new Bundle();
        args.putString(MOVIE_SORT, getCurrentSort());
        getLoaderManager().initLoader(MOVIE_LOADER, args, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_SORT, mSortKey);
    }

    public MovieGridFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        onPreferencesChanges();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        GridView mMovieGrid = (GridView) rootView.findViewById(R.id.moviesGrid);

        if(savedInstanceState != null)
        {
            mSortKey = savedInstanceState.getString(MOVIE_SORT);
        }

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        mMovieGrid.setAdapter(mMovieAdapter);
        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    long movieId = cursor.getInt(COL_MOVIE_ID);
                    Callback callback = (Callback) getActivity();
                    callback.onItemSelected(movieId);
                }

            }
        });

        return rootView;
    }

    private void discoverMovies()
    {
        if(!mSortKey.equals(getString(R.string.pref_sort_key_favorites))) {
            SyncAdapter.syncDiscoveredMovies(getActivity(), mSortKey);
        }
    }

    public interface Callback {
        void onItemSelected(long movieId);
    }
}
