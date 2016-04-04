package com.anoop.android.udacitypopularmovies.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.api.Api;
import com.anoop.android.udacitypopularmovies.api.ApiConstants;
import com.anoop.android.udacitypopularmovies.db.MovieContract;
import com.anoop.android.udacitypopularmovies.models.Movies;
import com.anoop.android.udacitypopularmovies.models.Movie;
import com.anoop.android.udacitypopularmovies.models.Review;
import com.anoop.android.udacitypopularmovies.models.Video;
import java.util.ArrayList;
import java.util.List;
import hugo.weaving.DebugLog;
import retrofit.RestAdapter;
import timber.log.Timber;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String EXTRA_SORT = "MSA-SORT-KEY";
    private static final String EXTRA_SYNC_TYPE = "MSA-SYNC-TYPE";
    private static final String EXTRA_MOVIE_ID = "MSA-MOVIE-KEY";

    private static final String DISCOVER_MOVIES = "discover";
    private static final String MOVIE_DETAILS = "movie_details";

    private Api mMovieDBApi;

    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
    }

    @DebugLog
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if( mMovieDBApi == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(ApiConstants.API_URL)
                    .build();

            mMovieDBApi = restAdapter.create(Api.class);
        }
        String syncType = extras.getString(EXTRA_SYNC_TYPE);
        if(syncType == null || syncType.isEmpty())
        {
            syncType = DISCOVER_MOVIES;
        }
        switch (syncType)
        {
            case DISCOVER_MOVIES:
                String sortKey = extras.getString(EXTRA_SORT);
                discoverMovies(sortKey);
                break;

            case MOVIE_DETAILS:
                long movieId = extras.getLong(EXTRA_MOVIE_ID);
                getMovieDetails(movieId);
                break;

        }


    }

    private void getMovieDetails(long movieId)
    {
        Movie movie = mMovieDBApi.movieDetails(movieId, ApiConstants.API_KEY);

        ContentValues movieValues = movie.getContentValues();

        Uri updateUri = MovieContract.MovieEntry.buildMovieExternalIDUri(movieId);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ContentProviderOperation newOp = ContentProviderOperation.newUpdate(updateUri).withValues(movieValues).build();
        ops.add(newOp);

        newOp = ContentProviderOperation.newDelete(MovieContract.ReviewEntry.buildMovieReviewsUrl(movieId)).build();
        ops.add(newOp);
        newOp = ContentProviderOperation.newDelete(MovieContract.VideoEntry.buildMovieVideosUrl(movieId)).build();
        ops.add(newOp);

        int i;
        if(movie.getReviews() != null && movie.getReviews().getResults() != null)
        {
            List<Review> reviews = movie.getReviews().getResults();
            for(i = 0; i < reviews.size() ; i++)
            {
                ContentValues values = reviews.get(i).getContentValues();
                values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                newOp = ContentProviderOperation.newInsert(MovieContract.ReviewEntry.CONTENT_URI)
                        .withValues(values)
                        .build();
                ops.add(newOp);
            }
        }

        if(movie.getVideos() != null && movie.getVideos().getResults() != null)
        {
            List<Video> videos = movie.getVideos().getResults();
            for(i = 0; i < videos.size(); i++)
            {
                ContentValues values = videos.get(i).getContentValues();
                values.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
                newOp = ContentProviderOperation.newInsert(MovieContract.VideoEntry.CONTENT_URI)
                        .withValues(values)
                        .build();
                ops.add(newOp);
            }
        }

        try{
            getContext().getContentResolver().applyBatch(MovieContract.CONTENT_AUTHORITY, ops);
        }
        catch (OperationApplicationException|RemoteException e)
        {
            Timber.e(e, "Error syncing movie details");
        }

    }

    private void discoverMovies(String sortKey)
    {
        if(TextUtils.isEmpty(sortKey))
        {
            sortKey = getContext().getString(R.string.pref_sort_default);
        }

        Movies results = mMovieDBApi.discoverMovies(ApiConstants.API_KEY, sortKey);

        ContentValues[] newData = parseMovies(results);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int i;
        for( i = 0 ; i < newData.length ; i++)
        {
            ContentProviderOperation newOp = ContentProviderOperation.newInsert(MovieContract.MovieEntry.CONTENT_URI)
                    .withValues(newData[i])
                    .build();
            ops.add(newOp);
        }
        newData = parseSorting(results, sortKey);
        for( i = 0 ; i < newData.length ; i++)
        {
            ContentProviderOperation newOp = ContentProviderOperation.newInsert(MovieContract.DiscoverEntry.CONTENT_URI)
                    .withValues(newData[i])
                    .build();
            ops.add(newOp);
        }

        try{
            getContext().getContentResolver().applyBatch(MovieContract.CONTENT_AUTHORITY, ops);
        }
        catch (OperationApplicationException|RemoteException e)
        {
            Timber.e(e, "Error syncing movie discovery");
        }

        Timber.v("Inserted movies: " + results.getResults().size());
    }

    private ContentValues[] parseMovies(Movies movieDiscovery)
    {
        List<Movie> movies = movieDiscovery.getResults();
        ContentValues[] results = new ContentValues[movies.size()];

        for(int i = 0; i < movies.size(); i++)
        {
            Movie movie = movies.get(i);
            ContentValues entry = movie.getContentValues();
            entry.put(MovieContract.MovieEntry.ACTION_REPLACE, true);
            results[i] = entry;
        }

        return results;

    }

    private ContentValues[] parseSorting(Movies movieDiscovery, String sorting)
    {
        List<Movie> movies = movieDiscovery.getResults();
        ContentValues[] results = new ContentValues[movies.size()];

        for(int i = 0; i < movies.size(); i++)
        {
            Movie movie = movies.get(i);
            ContentValues entry = new ContentValues();
            entry.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, movie.getId());
            entry.put(MovieContract.DiscoverEntry.COLUMN_ORDER, i);
            entry.put(MovieContract.DiscoverEntry.COLUMN_SORTING, sorting);
            results[i] = entry;
        }

        return results;
    }

    @DebugLog
    public static void syncDiscoveredMovies(Context context, String sorting)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(EXTRA_SORT, sorting);
        bundle.putString(EXTRA_SYNC_TYPE, DISCOVER_MOVIES);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.sync_account_type), bundle);
    }

    @DebugLog
    public static void syncMovieDetails(Context context, long movieId)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putLong(EXTRA_MOVIE_ID, movieId);
        bundle.putString(EXTRA_SYNC_TYPE, MOVIE_DETAILS);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.sync_account_type), bundle);
    }

    public static Account getSyncAccount(Context context)
    {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if( accountManager.getPassword(newAccount) == null)
        {
            if(!accountManager.addAccountExplicitly(newAccount, "", null))
            {
                return null;
            }
        }
        return newAccount;
    }
}
