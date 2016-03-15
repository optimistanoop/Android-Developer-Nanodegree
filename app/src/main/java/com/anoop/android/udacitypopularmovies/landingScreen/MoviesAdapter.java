package com.anoop.android.udacitypopularmovies.landingScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.anoop.android.udacitypopularmovies.util.Movie;
import com.anoop.android.udacitypopularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anoop on 13/3/16.
 */
public class MoviesAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovies;

    public MoviesAdapter(Context context, ArrayList<Movie> movies)
    {
        mContext = context;
        mMovies = movies;
    }
    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Movie> getMovies()
    {
        return mMovies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView movieThumb;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            movieThumb = (ImageView) inflater.inflate(R.layout.movie, parent, false);
        }
        else
        {
            movieThumb = (ImageView) convertView;
        }
        Picasso.with(mContext).load(mMovies.get(position).getPosterLink()).into(movieThumb);
        return movieThumb;
    }
}

