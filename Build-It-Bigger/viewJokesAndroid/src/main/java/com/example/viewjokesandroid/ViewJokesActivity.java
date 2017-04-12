package com.example.viewjokesandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewJokesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jokes);
        TextView jokeTV = (TextView) findViewById(R.id.joke);
        String joke = getIntent().getStringExtra("JOKE");
        jokeTV.setText(joke);
    }
}
