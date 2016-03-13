package com.anoop.android.udacitypopularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class MovieGridFragment extends Fragment implements ApiCall.MoviesApiCallResults{

    private GridView moviesGrid;
    private static final String MOVIE_LIST = "movies.list";
    private static final String MOVIE_SORT = "movies.sort";
    private String mSortKey;
    public MovieGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void moviesApiCallResultsCallback(ArrayList<Movie> movies) {
        for(Movie movie:movies)
        Log.d("anoop movies", movie.getTitle());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
         moviesGrid = (GridView) rootView.findViewById(R.id.moviesGrid);

        ApiCall apiCall = new ApiCall(this);
        apiCall.execute("");
        return rootView;
    }



}
