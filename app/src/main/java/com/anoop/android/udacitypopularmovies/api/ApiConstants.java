package com.anoop.android.udacitypopularmovies.api;

import com.anoop.android.udacitypopularmovies.models.Movie;
import com.anoop.android.udacitypopularmovies.BuildConfig;

public final class ApiConstants {
    public static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;
    public static final String API_PARAM = "api_key";
    public static final String JSON_RESULTS = "results";
    public static final String SORT_PARAM = "sort_by";
    public static final String API_URL = "http://api.themoviedb.org";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w300";
    public static final String EXTRA_MOVIE = Movie.class.getCanonicalName();
}
