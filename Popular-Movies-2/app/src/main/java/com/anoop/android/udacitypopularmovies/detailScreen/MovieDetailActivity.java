package com.anoop.android.udacitypopularmovies.detailScreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.api.ApiConstants;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(savedInstanceState == null)
        {
            long movieId = getIntent().getExtras().getLong(ApiConstants.EXTRA_MOVIE);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movieDetailsFrame, MovieDetailFragment.newInstance(movieId))
                    .commit();
        }
    }


}
