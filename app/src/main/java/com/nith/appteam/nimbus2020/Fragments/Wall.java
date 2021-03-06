package com.nith.appteam.nimbus2020.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nith.appteam.nimbus2020.Activities.CampusAmbassadorPost;
import com.nith.appteam.nimbus2020.Adapters.FeedRecyclerAdapter;
import com.nith.appteam.nimbus2020.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

public class Wall extends Fragment {
    private SharedPreferences sharedPreferences;
    private FloatingActionButton upload;
    private RecyclerView feed;
    private ArrayList<String> feedList = new ArrayList<>();
    private ProgressBar progressBar;

    public Wall() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wall, container, false);
        upload = rootView.findViewById(R.id.upload);
        feed = rootView.findViewById(R.id.feed);
        progressBar = rootView.findViewById(R.id.progress_bar);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        feed.setLayoutManager(layoutManager);
        sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);

        boolean caStatus = sharedPreferences.getBoolean("campusAmbassador", false);
        if (caStatus)
            upload.setVisibility(View.VISIBLE);
        else upload.setVisibility(View.GONE);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CampusAmbassadorPost.class);
                startActivity(i);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        getFeeds();
        feed.setAdapter(new FeedRecyclerAdapter(getContext(), feedList));
        return rootView;
    }

    private void getFeeds() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(getString(R.string.baseUrl) + "/views/links", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray feeds = jsonArray.getJSONObject(i).getJSONArray("image_urls");
                        for (int j = 0; i < feeds.length(); ++j) {
                            feedList.add(feeds.getString(j));
//                            Toast.makeText(getContext(), feeds.getString(j), Toast.LENGTH_SHORT).show();
                        }
                        Objects.requireNonNull(feed.getAdapter()).notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
    }
}
