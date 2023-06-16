package com.example.listedassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TopLinksFragment extends Fragment {

    private static final String API_URL = "https://api.inopenapp.com/api/v1/dashboardNew";
    private static final String ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI";

    private RecyclerView recyclerView;


    private LinkAdapter adapter;
    private List<Link> topLinksList;

    public TopLinksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_links, container, false);
        recyclerView = view.findViewById(R.id.topLinksRecyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topLinksList = new ArrayList<>();
        adapter = new LinkAdapter(topLinksList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        fetchTopLinks();
    }

    private void fetchTopLinks() {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray linksArray = response.getJSONObject("data").getJSONArray("top_links");
                            Toast.makeText(getActivity(), "Top links fetching...", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < linksArray.length(); i++) {
                                JSONObject linkObject = linksArray.getJSONObject(i);
                                String title = linkObject.getString("title");
                                String url = linkObject.getString("web_link");
                                String thumbnail = linkObject.getString("original_image");

                                Link link = new Link(title, url, thumbnail);
                                topLinksList.add(link);

                                Toast.makeText(getActivity(), "Top links fetched: "+ i, Toast.LENGTH_SHORT).show();

                            }
                            // why data is not showing in recycler view?

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(), "Error fetching top links", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ACCESS_TOKEN);
                return headers;
            }
        };

        // Adding the request to the request queue

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}
