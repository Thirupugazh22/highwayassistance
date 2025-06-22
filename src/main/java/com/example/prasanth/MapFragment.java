package com.example.prasanth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {

    private Button buttonOpenMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize the button
        buttonOpenMap = view.findViewById(R.id.buttonOpenMap);

        // Set OnClickListener for the button to open the map
        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Specify the location to open on the map (latitude, longitude)
                String location = "geo:0,0?q=28.6139,77.2090 (New+Delhi)"; // Example: New Delhi

                // Create an Intent to open Google Maps with the location
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(location));
                intent.setPackage("com.google.android.apps.maps");

                // Check if there's an app to handle the intent
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
