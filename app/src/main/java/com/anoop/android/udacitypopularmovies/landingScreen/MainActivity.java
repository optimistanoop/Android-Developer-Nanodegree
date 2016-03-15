package com.anoop.android.udacitypopularmovies.landingScreen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.anoop.android.udacitypopularmovies.util.Constants;
import com.anoop.android.udacitypopularmovies.R;


public class MainActivity extends AppCompatActivity {
    
    private MovieGridFragment movieGridFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieGridFragment = (MovieGridFragment) getSupportFragmentManager().findFragmentById(R.id.movieGridFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_popularity:
                item.setChecked(!item.isChecked());
                onSortChanged(Constants.POPULARITY_DESC);
                break;
            case R.id.sort_rating:
                item.setChecked(!item.isChecked());
                onSortChanged(Constants.VOTE_AVERAGE_DESC);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSortChanged(String sort) {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.SORT, sort);
        editor.commit();
        movieGridFragment.reLoadGrid();
    }
}
