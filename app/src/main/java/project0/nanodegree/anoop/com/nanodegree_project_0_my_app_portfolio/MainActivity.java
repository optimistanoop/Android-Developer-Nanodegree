package project0.nanodegree.anoop.com.nanodegree_project_0_my_app_portfolio;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button buttonSpotifyStreamer = (Button) this.findViewById(R.id.spotifyStreamer);
        buttonSpotifyStreamer.setOnClickListener(new ClickListener());
        Button buttonBuildItBigger = (Button) this.findViewById(R.id.buildItBigger);
        buttonBuildItBigger.setOnClickListener(new ClickListener());
        Button buttonCapstone = (Button) this.findViewById(R.id.capstone);
        buttonCapstone.setOnClickListener(new ClickListener());
        Button buttonLibraryApp = (Button) this.findViewById(R.id.libraryApp);
        buttonLibraryApp.setOnClickListener(new ClickListener());
        Button buttonScoresApp = (Button) this.findViewById(R.id.scoresApp);
        buttonScoresApp.setOnClickListener(new ClickListener());
        Button buttonXYZReader = (Button) this.findViewById(R.id.xYZReader);
        buttonXYZReader.setOnClickListener(new ClickListener());

    }

   private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String text  = ((Button)v).getText().toString();
            if( !(text.endsWith("App"))){
             text=   text.concat(" App");
            };
            showToast("This button will launch my " + text.toLowerCase()+ "!");
        }
        public void showToast(String message){
            Context context = getApplicationContext();
            CharSequence text = message;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
