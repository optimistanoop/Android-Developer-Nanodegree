package com.anoop.android.udacitypopularmovies.landingScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.api.ApiConstants;
import com.anoop.android.udacitypopularmovies.detailScreen.MovieDetailActivity;
import com.anoop.android.udacitypopularmovies.detailScreen.MovieDetailFragment;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback {
    MovieGridFragment movieGridFragment;
    @Override
    public void onItemSelected(long movieId) {
        if(mTwoPane)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movieDetailsFrame, MovieDetailFragment.newInstance(movieId))
                    .commit();
        }
        else
        {
            Intent details = new Intent(this, MovieDetailActivity.class);
            details.putExtra(ApiConstants.EXTRA_MOVIE, movieId);
            startActivity(details);
        }
    }

    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FrameLayout detailView = (FrameLayout) findViewById(R.id.movieDetailsFrame);
        mTwoPane = (detailView != null);
        movieGridFragment = (MovieGridFragment) getSupportFragmentManager().findFragmentById(R.id.movieGrid);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_popularity) {
            onSharedPreferenceChanged("popularity.desc");
            return true;
        }

        if (id == R.id.sort_favorites) {
            onSharedPreferenceChanged("favorite.desc");
            return true;
        }

        if (id == R.id.sort_rating) {
            onSharedPreferenceChanged("vote_average.desc");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSharedPreferenceChanged(String value) {

        //SharedPreferences settings = getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.pref_sort_key), value);
        editor.commit();
        movieGridFragment.onPreferencesChanges();
       /* Preference pref = MainActivity.this.findPreference(key);
        String valueKey = sharedPreferences.getString(key,"");
        if(pref instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) pref;
            int selected = listPreference.findIndexOfValue(valueKey);

            if(selected >= 0)
            {
                listPreference.setSummary(listPreference.getEntries()[selected]);
            }
        }
        else
        {
            pref.setSummary(valueKey);
        }*/
    }

}
