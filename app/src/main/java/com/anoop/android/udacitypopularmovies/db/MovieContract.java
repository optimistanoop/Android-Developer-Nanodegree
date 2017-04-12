package com.anoop.android.udacitypopularmovies.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.anoop.android.udacitypopularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_DISCOVERY = "discovery";

    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final Uri CONTENT_DISCOVER_MOVIES_URI = CONTENT_URI.buildUpon().appendPath(PATH_DISCOVERY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String TABLE_NAME = "movies";
        public static final String FULL_ID = TABLE_NAME + "." + _ID;
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String FULL_MOVIE_ID = TABLE_NAME + "." + COLUMN_MOVIE_ID;
        public static final String COLUMN_ADULT = "adult";
        public static final String FULL_ADULT = TABLE_NAME + "." + COLUMN_ADULT;
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String FULL_BACKDROP_PATH = TABLE_NAME + "." + COLUMN_BACKDROP_PATH;
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String FULL_ORIGINAL_LANGUAGE = TABLE_NAME + "." + COLUMN_ORIGINAL_LANGUAGE;
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String FULL_ORIGINAL_TITLE = TABLE_NAME + "." + COLUMN_ORIGINAL_TITLE;
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String FULL_OVERVIEW = TABLE_NAME + "." + COLUMN_OVERVIEW;
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String FULL_RELEASE_DATE = TABLE_NAME + "." + COLUMN_RELEASE_DATE;
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String FULL_POSTER_PATH = TABLE_NAME + "." + COLUMN_POSTER_PATH;
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String FULL_POPULARITY = TABLE_NAME + "." + COLUMN_POPULARITY;
        public static final String COLUMN_TITLE = "title";
        public static final String FULL_TITLE = TABLE_NAME + "." + COLUMN_TITLE;
        public static final String COLUMN_VIDEO = "video";
        public static final String FULL_VIDEO = TABLE_NAME + "." + COLUMN_VIDEO;
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String FULL_VOTE_AVERAGE = TABLE_NAME + "." + COLUMN_VOTE_AVERAGE;
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String FULL_VOTE_COUNT = TABLE_NAME + "." + COLUMN_VOTE_COUNT;
        public static final String ACTION_REPLACE = "__SQL_USE_REPLACE__";
        public static final String[] PROJECTION_ALL = {
                _ID
                ,COLUMN_MOVIE_ID
                ,COLUMN_ADULT
                ,COLUMN_BACKDROP_PATH
                ,COLUMN_ORIGINAL_LANGUAGE
                ,COLUMN_ORIGINAL_TITLE
                ,COLUMN_OVERVIEW
                ,COLUMN_RELEASE_DATE
                ,COLUMN_POSTER_PATH
                ,COLUMN_POPULARITY
                ,COLUMN_TITLE
                ,COLUMN_VIDEO
                ,COLUMN_VOTE_AVERAGE
                ,COLUMN_VOTE_COUNT
        };

        public static String getExternalIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }
        public static String getInternalIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }
        public static Uri buildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieExternalIDUri(long id)
        {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).appendPath("byExtId").build();
        }

        public static Uri buildMovieDiscovery(String sortType)
        {
            return CONTENT_DISCOVER_MOVIES_URI.buildUpon().appendPath(sortType).build();
        }
    }

    public static final class DiscoverEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DISCOVERY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISCOVERY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISCOVERY;
        public static final String TABLE_NAME = "discovery";
        public static final String FULL_ID = TABLE_NAME + "." + _ID;
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String FULL_MOVIE_ID = TABLE_NAME + "." + COLUMN_MOVIE_ID;
        public static final String COLUMN_SORTING = "sort_key";
        public static final String FULL_SORTING = TABLE_NAME + "." + COLUMN_SORTING;
        public static final String COLUMN_ORDER = "sort_order";
        public static final String FULL_ORDER = TABLE_NAME + "." + COLUMN_ORDER;
        public static final String[] PROJECTION_ALL = {
                _ID,
                COLUMN_MOVIE_ID,
                COLUMN_SORTING,
                COLUMN_ORDER
        };

        public static String getIdFromUri(Uri uri)
        {

            return uri.getPathSegments().get(1);
        }

        public static Uri buildDiscoverUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VideoEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String TABLE_NAME = "video";
        public static final String COLUMN_API_ID = "api_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "video_type";

        public static final String[] PROJECTION_ALL = {
                _ID
                ,COLUMN_API_ID
                ,COLUMN_MOVIE_ID
                ,COLUMN_KEY
                ,COLUMN_NAME
                ,COLUMN_SITE
                ,COLUMN_SIZE
                ,COLUMN_TYPE
        };

        public static String getIdFromUri(Uri uri)
        {

            return uri.getPathSegments().get(1);
        }


        public static String getMovieIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildVideoUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieVideosUrl(long movieId)
        {
            return MovieEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).appendPath(PATH_VIDEO).build();
        }

    }

    public static final class ReviewEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_API_ID = "api_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static final String[] PROJECTION_ALL = {
                _ID
                ,COLUMN_API_ID
                ,COLUMN_MOVIE_ID
                ,COLUMN_AUTHOR
                ,COLUMN_CONTENT
                ,COLUMN_URL
        };

        public static String getIdFromUri(Uri uri)
        {

            return uri.getPathSegments().get(1);
        }

        public static String getMovieIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildReviewUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieReviewsUrl(long movieId)
        {
            return MovieEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).appendPath(PATH_REVIEW).build();
        }

    }
}
