package com.anoop.android.udacitypopularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by anoop on 14/3/16.
 */
public class MovieDetailFragment extends Fragment {


    ImageView poster;
    TextView overview;
    TextView rating;
    TextView releaseYear;
    TextView title;
    Movie movie;
    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.movie_detail_frag, container, false);
        Bundle bundle = getArguments();
        movie = bundle.getParcelable(Constants.MOVIE);
        return  rootview;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        poster = (ImageView)getActivity().findViewById(R.id.poster);
        overview = (TextView)getActivity().findViewById(R.id.overview);
        rating = (TextView)getActivity().findViewById(R.id.average_rating);
        releaseYear = (TextView)getActivity().findViewById(R.id.release_year);
        title = (TextView)getActivity().findViewById(R.id.title);
        if(movie !=null){
            setMovieDetail(movie);
        }

    }

    public static MovieDetailFragment newInstance(Movie movie)
    {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
    public void setMovieDetail(Movie movie) {
        overview.setText(movie.getOverview());
        rating.setText(getString(R.string.movie_detail_rating, movie.getVoteAverage()));
        releaseYear.setText(""+movie.getReleaseDate().getYear());
        title.setText(movie.getTitle());

        Glide.with(getActivity())
                .load(movie.getPosterLink())
                .into(poster);
    }
}
