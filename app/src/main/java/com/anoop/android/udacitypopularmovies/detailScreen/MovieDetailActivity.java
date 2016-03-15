package com.anoop.android.udacitypopularmovies.detailScreen;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.anoop.android.udacitypopularmovies.util.Constants;
import com.anoop.android.udacitypopularmovies.util.Movie;
import com.anoop.android.udacitypopularmovies.R;

import java.security.InvalidParameterException;
/*for movie detail screen*/
public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(Constants.ACTION_BAR_TITLE);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            ViewCompat.setElevation(toolbar, 0);
        }

        if (savedInstanceState == null) {
            Movie movie = getIntent().getExtras().getParcelable(Constants.MOVIE);
            if (movie == null) {
                throw new InvalidParameterException(Constants.INVALID_PARAM_EXCEP_MSG);
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movieFrame, MovieDetailFragment.newInstance(movie))
                    .commit();
        }

    }
}
