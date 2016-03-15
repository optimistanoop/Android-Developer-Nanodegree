package com.anoop.android.udacitypopularmovies.util;

import com.anoop.android.udacitypopularmovies.BuildConfig;

/**
 * Created by anoop on 13/3/16.
 */
public class Constants {
    public static final String ID="id";
    public static final String ADULT = "adult";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String POSTER_LINK = "poster_path";
    public static final String POSTER_BASE_LINK = "http://image.tmdb.org/t/p/w185";
    public static final String POPULARITY= "popularity";
    public static final String TITLE = "title";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String VOTE_COUNT = "vote_count";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String API_PARAM = "api_key";
    public static final String JSON_RESULTS = "results";
    public static final String MOVIE_LIST = "movies.list";
    public static final String MOVIE_SORT = "movies.sort";
    public static final String PREFS_NAME = "prefs_sort";
    public static final String POPULARITY_DESC = "popularity.desc";
    public static final String VOTE_AVERAGE_DESC = "vote_average.desc";
    public static final String SORT_KEY = "sort_by";
    public static final String SORT = "sort_data";
    public static final String MOVIE = "movie";
    public static final String ACTION_BAR_TITLE = "MovieDetail";
    public static final String INVALID_PARAM_EXCEP_MSG = "Invalid parameter for movie detail.";
    public static final String LOADING_MSG = "Loading...";
    public static final String API_METHOD = "GET";
    public static final String API_PATH = "http://api.themoviedb.org/3/discover/movie";
    public static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;
}
