package com.anoop.android.udacitypopularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    /*TODO develop for tabs
    * cross check requirement
    * test it thoroughly
    * use comments and logs
    *
    * TODO steps required to implement page one for grid view
    * call api
    * adapter to the grid view
    * make sure sorting of movies works
    *
    * TODO for second detail page
    * call api
    * define a view for detail page
    * set values for every elem
    */
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
        editor.putString("key", sort);
        editor.commit();
        movieGridFragment.reLoadGrid();
    }
}
