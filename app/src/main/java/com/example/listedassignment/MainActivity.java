package com.example.listedassignment;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.listedassignment.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.inopenapp.com/api/v1/dashboardNew";
    private static final String ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI";

    private LineChart chart;
    TextView todayClickText , topLocationText , topSourceText , topTimeText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.chart);
        TextView greetingTextView = findViewById(R.id.greetingText);
        todayClickText = findViewById(R.id.todayClicksText);
        topLocationText = findViewById(R.id.topLocationText);
        topSourceText = findViewById(R.id.topSourceText);
        topTimeText = findViewById(R.id.bestTimeText);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TopLinksFragment());
        fragments.add(new RecentLinksFragment());

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Top Links");
        tabLayout.getTabAt(1).setText("Recent Links");




        // Set the IST timezone
        TimeZone istTimeZone = TimeZone.getTimeZone("Asia/Kolkata");
        Calendar calendar = Calendar.getInstance(istTimeZone);

// Get the current local time in IST
        Date currentTime = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeString = sdf.format(currentTime);

        String greeting = "";

// Define the time ranges for different greetings
        String morningStartTime = "06:00";
        String morningEndTime = "12:00";
        String afternoonStartTime = "12:00";
        String afternoonEndTime = "18:00";
        String eveningStartTime = "18:00";
        String eveningEndTime = "23:59";

        try {
            Date morningStart = sdf.parse(morningStartTime);
            Date morningEnd = sdf.parse(morningEndTime);
            Date afternoonStart = sdf.parse(afternoonStartTime);
            Date afternoonEnd = sdf.parse(afternoonEndTime);
            Date eveningStart = sdf.parse(eveningStartTime);
            Date eveningEnd = sdf.parse(eveningEndTime);

            Date currentTimeParsed = sdf.parse(timeString);

            if (currentTimeParsed.after(morningStart) && currentTimeParsed.before(morningEnd)) {
                greeting = "Good Morning";
            } else if (currentTimeParsed.after(afternoonStart) && currentTimeParsed.before(afternoonEnd)) {
                greeting = "Good Afternoon";
            } else if (currentTimeParsed.after(eveningStart) && currentTimeParsed.before(eveningEnd)) {
                greeting = "Good Evening";
            } else {
                greeting = "Good Night";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        greetingTextView.setText(greeting);


        // i want to add progress bar while loading the data

        makeAPIRequest();


    }

    private void makeAPIRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject data = response.getJSONObject("data");
                            JSONObject overallUrlChart = data.getJSONObject("overall_url_chart");


                            // Get total clicks
                            int totalClicks = response.getInt("total_clicks");
                            todayClickText.setText(String.valueOf(totalClicks));

                            String topLocation = response.getString("top_location");

                            if(topLocation.equals("")){
                                topLocationText.setText("No data in API");
                            }else{
                                topLocationText.setText(topLocation);
                            }

                            String topSource = response.getString("top_source");

                            if(topSource.equals("")){
                                topSourceText.setText("No data in API");
                            }else{
                                topSourceText.setText(topSource);

                            }

                            String bestTime = response.getString("startTime");

                            if (bestTime.equals("")){
                                topTimeText.setText("No data in API");
                            }else{
                                topTimeText.setText(bestTime);
                            }





                            // Get the dates and points from the overallUrlChart
                            ArrayList<String> dates = new ArrayList<>();
                            ArrayList<Entry> points = new ArrayList<>();
                            Iterator<String> iterator = overallUrlChart.keys();
                            int index = 0;
                            while (iterator.hasNext()) {
                                String date = iterator.next();
                                int point = overallUrlChart.getInt(date);

                                dates.add(date);
                                points.add(new Entry(index, point));
                                index++;
                            }

                            // Plot the graph
                            plotGraph(dates, points);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error retrieving data" + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("APIRequest", "Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                        Log.e("APIRequest", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new TreeMap<>();
                headers.put("Authorization", ACCESS_TOKEN);
                return headers;
            }
        };

        queue.add(request);
    }

    private void plotGraph(ArrayList<String> dates, ArrayList<Entry> points) {

        LineDataSet dataSet = new LineDataSet(points, "Points");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);


        chart.setExtraOffsets(0, 0, 0, 50);


        chart.notifyDataSetChanged();
        chart.getDescription().setEnabled(false);


        chart.setData(lineData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  // Set the position of x-axis labels
        chart.getXAxis().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false); // Hide the legend
        chart.getXAxis().setLabelCount(points.size(), true); // Set the number of x-axis labels
        chart.getXAxis().setTextSize(10f); // Set the size of x-axis labels

        // Rotate x-axis labels by 45 degrees
        chart.getXAxis().setLabelRotationAngle(42f);
        chart.getXAxis().setCenterAxisLabels(true);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setGranularity(1f);

        chart.invalidate(); // Refresh


    }
}