package com.anoop.android.udacitypopularmovies.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.anoop.android.udacitypopularmovies.db.MovieContract.DiscoverEntry;
import com.anoop.android.udacitypopularmovies.db.MovieContract.MovieEntry;
import java.util.ArrayList;
import java.util.HashMap;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class MovieProvider extends ContentProvider {

    private static final int MOVIE_INT_ID = 1;
    private static final int MOVIES = 2;
    private static final int MOVIE_EXT_ID = 3;
    private static final int MOVIE_DISCOVERY = 4;
    private static final int DISCOVERIES = 5;
    private static final int DISCOVERY = 6;

    private static final int REVIEW = 7;
    private static final int REVIEWS = 8;
    private static final int MOVIE_REVIEWS = 9;
    private static final int VIDEO = 10;
    private static final int VIDEOS = 11;
    private static final int MOVIE_VIDEOS = 12;

    private static final HashMap<String, String> sMovieProjection = buildMovieProjection();
    private static final HashMap<String, String> sDiscoverProjection = buildDiscoverProjection();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/", MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_INT_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/byExtId", MOVIE_EXT_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_DISCOVERY + "/*", MOVIE_DISCOVERY);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_DISCOVERY + "/", DISCOVERIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_DISCOVERY + "/#", DISCOVERY);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/", REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEW);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_REVIEW, MOVIE_REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO + "/", VIDEOS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO + "/#", VIDEO);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_VIDEO, MOVIE_VIDEOS);

        return uriMatcher;
    }

    private static HashMap<String, String> buildMovieProjection()
    {
        HashMap<String, String> projections = new HashMap<>();
        projections.put(MovieEntry._ID, MovieEntry.FULL_ID);
        projections.put(MovieEntry.COLUMN_MOVIE_ID, MovieEntry.FULL_MOVIE_ID);
        projections.put(MovieEntry.COLUMN_ADULT, MovieEntry.FULL_ADULT);
        projections.put(MovieEntry.COLUMN_BACKDROP_PATH, MovieEntry.FULL_BACKDROP_PATH);
        projections.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, MovieEntry.FULL_ORIGINAL_LANGUAGE);
        projections.put(MovieEntry.COLUMN_ORIGINAL_TITLE, MovieEntry.FULL_ORIGINAL_TITLE);
        projections.put(MovieEntry.COLUMN_OVERVIEW, MovieEntry.FULL_OVERVIEW);
        projections.put(MovieEntry.COLUMN_RELEASE_DATE, MovieEntry.FULL_RELEASE_DATE);
        projections.put(MovieEntry.COLUMN_POSTER_PATH, MovieEntry.FULL_POSTER_PATH);
        projections.put(MovieEntry.COLUMN_POPULARITY, MovieEntry.FULL_POPULARITY);
        projections.put(MovieEntry.COLUMN_TITLE, MovieEntry.FULL_TITLE);
        projections.put(MovieEntry.COLUMN_VIDEO, MovieEntry.FULL_VIDEO);
        projections.put(MovieEntry.COLUMN_VOTE_AVERAGE, MovieEntry.FULL_VOTE_AVERAGE);
        projections.put(MovieEntry.COLUMN_VOTE_COUNT, MovieEntry.FULL_VOTE_COUNT);

        return projections;
    }

    private static HashMap<String, String> buildDiscoverProjection()
    {
        HashMap<String, String> projections = new HashMap<>();
        projections.put(DiscoverEntry._ID, DiscoverEntry.FULL_ID);
        projections.put(DiscoverEntry.COLUMN_MOVIE_ID, DiscoverEntry.FULL_MOVIE_ID);
        projections.put(DiscoverEntry.COLUMN_SORTING, DiscoverEntry.FULL_SORTING);
        projections.put(DiscoverEntry.COLUMN_ORDER, DiscoverEntry.FULL_ORDER);
        return projections;
    }

    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        Timber.tag(MovieProvider.class.getSimpleName());
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @DebugLog
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String idCol = null;
        String id = null;
        ArrayList<Uri> baseUris = new ArrayList<>();

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieEntry.COLUMN_MOVIE_ID;
                id = MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieEntry._ID;
                    id = MovieEntry.getInternalIdFromUri(uri);
                }
                builder.setTables(MovieEntry.TABLE_NAME);
                builder.setProjectionMap(sMovieProjection);
                builder.appendWhere(idCol + " = " + id);
                break;

            case MOVIES:
                builder.setTables(MovieEntry.TABLE_NAME);
                builder.setProjectionMap(sMovieProjection);
                break;

            case DISCOVERY:
                builder.setTables(DiscoverEntry.TABLE_NAME);
                builder.setProjectionMap(sDiscoverProjection);
                builder.appendWhere(DiscoverEntry.FULL_ID + " = " + DiscoverEntry.getIdFromUri(uri));
                break;

            case DISCOVERIES:
                builder.setTables(DiscoverEntry.TABLE_NAME);
                builder.setProjectionMap(sDiscoverProjection);
                break;

            case MOVIE_DISCOVERY:
                builder.setTables(MovieEntry.TABLE_NAME +
                                " INNER JOIN " + DiscoverEntry.TABLE_NAME +
                                " ON " + MovieEntry.FULL_MOVIE_ID + " = " + DiscoverEntry.FULL_MOVIE_ID
                );
                builder.setProjectionMap(sMovieProjection);
                builder.appendWhere(DiscoverEntry.FULL_SORTING + " = '" + uri.getLastPathSegment() +"'");
                if(TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = DiscoverEntry.FULL_ORDER + " ASC";
                }
                baseUris.add(DiscoverEntry.CONTENT_URI);
                break;

            case REVIEW:
                builder.appendWhere(MovieContract.ReviewEntry._ID + " = " + MovieContract.ReviewEntry.getIdFromUri(uri));
            case REVIEWS:
                builder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
                break;
            case MOVIE_REVIEWS:
                builder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
                builder.appendWhere(MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = " + MovieContract.ReviewEntry.getMovieIdFromUri(uri));
                break;

            case VIDEO:
                builder.appendWhere(MovieContract.VideoEntry._ID + " = " + MovieContract.VideoEntry.getIdFromUri(uri));
            case VIDEOS:
                builder.setTables(MovieContract.VideoEntry.TABLE_NAME);
                break;
            case MOVIE_VIDEOS:
                builder.setTables(MovieContract.VideoEntry.TABLE_NAME);
                builder.appendWhere(MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = " + MovieContract.VideoEntry.getMovieIdFromUri(uri));
                break;

            default:
                throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }

        Cursor cursor = builder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ContentResolver resolver = getContext().getContentResolver();
        cursor.setNotificationUri(resolver, uri);
        for (Uri extraUri : baseUris) {
            cursor.setNotificationUri(resolver, extraUri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE_INT_ID:
            case MOVIE_EXT_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
            case MOVIE_DISCOVERY:
                return MovieEntry.CONTENT_TYPE;
            case DISCOVERIES:
                return DiscoverEntry.CONTENT_TYPE;
            case DISCOVERY:
                return DiscoverEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
            case MOVIE_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case VIDEOS:
            case MOVIE_VIDEOS:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case VIDEO:
                return MovieContract.VideoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        ArrayList<Uri> notifyUris = new ArrayList<>();
        notifyUris.add(uri);

        long _id;
        switch(match)
        {
            case MOVIES:
                boolean replace = false;
                if(values.containsKey(MovieEntry.ACTION_REPLACE))
                {
                    replace = values.getAsBoolean(MovieEntry.ACTION_REPLACE);
                    values.remove(MovieEntry.ACTION_REPLACE);
                }

                if(replace)
                {
                    _id = db.replace(MovieEntry.TABLE_NAME, null, values);
                }
                else
                {
                    _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                }

                if( _id > 0)
                {
                    returnUri = MovieEntry.buildMovieUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case DISCOVERIES:
                _id = db.insert(DiscoverEntry.TABLE_NAME, null, values);
                if( _id > 0)
                {
                    returnUri = DiscoverEntry.buildDiscoverUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            case REVIEWS:

                _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if( _id > 0)
                {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                    long movieId = values.getAsLong(MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
                    notifyUris.add(MovieContract.ReviewEntry.buildMovieReviewsUrl(movieId));
                    
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            case VIDEOS:

                _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if( _id > 0)
                {
                    returnUri = MovieContract.VideoEntry.buildVideoUri(_id);
                    long movieId = values.getAsLong(MovieContract.VideoEntry.COLUMN_MOVIE_ID);
                    notifyUris.add(MovieContract.VideoEntry.buildMovieVideosUrl(movieId));
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        for (Uri notifyUri : notifyUris) {
            getContext().getContentResolver().notifyChange(notifyUri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int deleted;
        String idCol = null;
        String id = null;
        String where = null;
        String table;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieEntry.COLUMN_MOVIE_ID;
                id = MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                table = MovieEntry.TABLE_NAME;
                if(idCol == null)
                {
                    idCol = MovieEntry._ID;
                    id = MovieEntry.getInternalIdFromUri(uri);
                }
                where = idCol + " = " + id;

                break;

            case MOVIES:
                table = MovieEntry.TABLE_NAME;
                break;

            case DISCOVERY:
                where = DiscoverEntry.FULL_ID + " = " + DiscoverEntry.getIdFromUri(uri);
            case DISCOVERIES:
                table = DiscoverEntry.TABLE_NAME;
                break;

            case REVIEW:
                where = MovieContract.ReviewEntry._ID + " = " + MovieContract.ReviewEntry.getIdFromUri(uri);
            case REVIEWS:
                table = MovieContract.ReviewEntry.TABLE_NAME;
                break;

            case MOVIE_REVIEWS:
                table = MovieContract.ReviewEntry.TABLE_NAME;
                where = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = " + MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                break;

            case VIDEO:
                where = MovieContract.VideoEntry._ID + " = " + MovieContract.VideoEntry.getIdFromUri(uri);
            case VIDEOS:
                table = MovieContract.VideoEntry.TABLE_NAME;
                break;

            case MOVIE_VIDEOS:
                table = MovieContract.VideoEntry.TABLE_NAME;
                where = MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = " + MovieContract.VideoEntry.getMovieIdFromUri(uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(selection == null || selection.isEmpty())
        {
            selection = where;
        }
        else if(where != null && !where.isEmpty())
        {
            selection += " AND " + where;
        }

        deleted = db.delete(
                table,
                selection,
                selectionArgs
        );

        if(deleted > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int updated;
        String idCol = null;
        String id = null;
        String where = null;
        String table;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieEntry.COLUMN_MOVIE_ID;
                id = MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieEntry._ID;
                    id = MovieEntry.getInternalIdFromUri(uri);
                }
                where = idCol + " = " + id;
            case MOVIES:
                table = MovieEntry.TABLE_NAME;
                break;

            case DISCOVERY:
                where = DiscoverEntry._ID + " = " + DiscoverEntry.getIdFromUri(uri);
            case DISCOVERIES:
                table = DiscoverEntry.TABLE_NAME;
                break;

            case REVIEW:
                where = MovieContract.ReviewEntry._ID + " = " + MovieContract.ReviewEntry.getIdFromUri(uri);
            case REVIEWS:
                table = MovieContract.ReviewEntry.TABLE_NAME;
                break;

            case VIDEO:
                where = MovieContract.VideoEntry._ID + " = " + MovieContract.VideoEntry.getIdFromUri(uri);
            case VIDEOS:
                table = MovieContract.VideoEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(selection == null || selection.isEmpty())
        {
            selection = where;
        }
        else if(where != null && !where.isEmpty())
        {
            selection += " AND " + where;
        }

        updated = db.update(
                table,
                values,
                selection,
                selectionArgs
        );

        if(updated > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updated;
    }
}
