package com.anoop.android.udacitypopularmovies.api;

import com.anoop.android.udacitypopularmovies.models.Movies;
import com.anoop.android.udacitypopularmovies.models.Movie;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Api {

    @GET("/3/discover/movie?vote_count.gte=100")
    Movies discoverMovies(@Query("api_key") String apiKey, @Query("sort_by") String sorting);

    @GET("/3/movie/{id}?append_to_response=reviews,videos")
    Movie movieDetails(@Path("id") long movieId, @Query("api_key") String apiKey);
}
