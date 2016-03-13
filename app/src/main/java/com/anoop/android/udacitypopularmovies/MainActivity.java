package com.anoop.android.udacitypopularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MovieGridFragment.OnFragmentInteractionListener{

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.moviesGrid, new MovieGridFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
