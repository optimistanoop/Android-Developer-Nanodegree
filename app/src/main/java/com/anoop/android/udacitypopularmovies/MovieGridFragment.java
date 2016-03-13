package com.anoop.android.udacitypopularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MovieGridFragment extends Fragment implements ApiCall.MoviesApiCallResults{
    private static final String MOVIE_LIST = "movies.list";
    private static final String MOVIE_SORT = "movies.sort";
    private GridView movieGrid;
    private String sort;

    @Override
    public void moviesApiCallResultsCallback(ArrayList<Movie> movies) {
        movieGrid.setAdapter(new MoviesAdapter(getActivity(), movies));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_SORT, sort);
        MoviesAdapter adapter = (MoviesAdapter) movieGrid.getAdapter();
        outState.putParcelableArrayList(MOVIE_LIST, adapter.getMovies());
    }

    public MovieGridFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshGrid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        movieGrid = (GridView) rootView.findViewById(R.id.moviesGrid);

        if(savedInstanceState != null)
        {

            ArrayList<Movie> movies =  savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            MoviesAdapter adapter = new MoviesAdapter(getActivity(), movies);
            movieGrid.setAdapter(adapter);
            sort = savedInstanceState.getString(MOVIE_SORT);
        }
        else
        {
            refreshGrid();
        }

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return rootView;
    }

    private void refreshGrid()
    {
        // to reload or refresh according to sort
    }



}
