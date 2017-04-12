package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LineGraphActivity extends AppCompatActivity {
    View error;
    View progress;
    ValueLineChart lineChart;
    boolean isLoaded = false;
    String symbol;
    String name;
    ArrayList<String> labels;
    ArrayList<Float> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        error = findViewById(R.id.error_text);
        progress = findViewById(R.id.progress);
        lineChart = (ValueLineChart) findViewById(R.id.line_graph);
        symbol = getIntent().getStringExtra("symbol");
        if (savedInstanceState == null) {
            getStock();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isLoaded) {
            outState.putString("company_name", name);
            outState.putStringArrayList("labels", labels);
            float[] valuesArray = new float[values.size()];
            for (int i = 0; i < valuesArray.length; i++) {
                valuesArray[i] = values.get(i);
            }
            outState.putFloatArray("values", valuesArray);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("company_name")) {
            isLoaded = true;
            name = savedInstanceState.getString("company_name");
            labels = savedInstanceState.getStringArrayList("labels");
            values = new ArrayList<>();

            float[] valuesArray = savedInstanceState.getFloatArray("values");
            for (float f : valuesArray) {
                values.add(f);
            }
            getStockCallBack();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return false;
        }
    }

    private void getStock() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://chartapi.finance.yahoo.com/instrument/1.0/" + symbol + "/chartdata;type=quote;range=5y/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String result = response.body().string();
                        if (result.startsWith("finance_charts_json_callback( ")) {
                            result = result.substring(29, result.length() - 2);
                        }
                        JSONObject object = new JSONObject(result);
                        name = object.getJSONObject("meta").getString("Company-Name");
                        labels = new ArrayList<>();
                        values = new ArrayList<>();
                        JSONArray series = object.getJSONArray("series");
                        for (int i = 0; i < series.length(); i++) {
                            JSONObject seriesItem = series.getJSONObject(i);
                            SimpleDateFormat srcFormat = new SimpleDateFormat("yyyyMMdd");
                            String date = android.text.format.DateFormat.
                                    getMediumDateFormat(getApplicationContext()).
                                    format(srcFormat.parse(seriesItem.getString("Date")));
                            labels.add(date);
                            values.add(Float.parseFloat(seriesItem.getString("close")));
                        }

                        getStockCallBack();
                    } catch (Exception e) {
                        getStockFailedCallBack();
                        e.printStackTrace();
                    }
                } else {
                    getStockFailedCallBack();
                }
            }
            @Override
            public void onFailure(Request request, IOException e) {
                    getStockFailedCallBack();
            }
        });
    }
    private void getStockCallBack() {
        LineGraphActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(name);
                progress.setVisibility(View.GONE);
                error.setVisibility(View.GONE);

                ValueLineSeries series = new ValueLineSeries();
                series.setColor(0xFF56B7F1);
                for (int i = 0; i < labels.size(); i++) {
                    series.addPoint(new ValueLinePoint(labels.get(i), values.get(i)));
                }
                if(!isLoaded)
                lineChart.startAnimation();
                lineChart.addSeries(series);
                lineChart.setVisibility(View.VISIBLE);
                isLoaded= true;
            }
        });
    }
    private void getStockFailedCallBack() {
        LineGraphActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lineChart.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                setTitle(R.string.error);
            }
        });
    }
}
