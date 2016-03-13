package com.anoop.android.udacitypopularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anoop on 13/3/16.
 */
public class ApiCall extends AsyncTask<String, Void, ArrayList<Movie>> {


    private MoviesApiCallResults moviesApiCallResults;

    public ApiCall(MoviesApiCallResults callback) {
        moviesApiCallResults = callback;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        if (moviesApiCallResults != null) {
            moviesApiCallResults.moviesApiCallResultsCallback(movies);
        }
    }

    protected ArrayList<Movie> doInBackground(String... params) {
        if (moviesApiCallResults == null) {
            return null;
        }

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String jsonResults;

        try {
            Uri.Builder builder = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon();
            builder.appendQueryParameter(Constants.API_PARAM, Constants.API_KEY);
            String urlStr = builder.build().toString();
            URL url = new URL(urlStr);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream == null) {
                return new ArrayList<>();
            }
            StringBuffer buffer = new StringBuffer();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return new ArrayList<>();
            }
            jsonResults = buffer.toString();

        } catch (IOException e) {

            return new ArrayList<>();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {

                }
            }
        }

        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(jsonResults);
            JSONArray moviesArray = result.getJSONArray(Constants.JSON_RESULTS);
            for (int i = 0; i < moviesArray.length(); i++) {
                movies.add(new Movie(moviesArray.getJSONObject(i)));
            }
        } catch (JSONException e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
        return movies;
    }

    public interface MoviesApiCallResults {
        void moviesApiCallResultsCallback(ArrayList<Movie> movies);
    }
}


