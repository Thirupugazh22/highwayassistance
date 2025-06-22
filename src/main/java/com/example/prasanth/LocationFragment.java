package com.example.prasanth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private Button buttonSendLocation;
    private EditText editTextPhoneNumber;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Initialize UI elements
        buttonSendLocation = view.findViewById(R.id.buttonSendLocation);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Set OnClickListener for the Send Location button
        buttonSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the phone number from EditText
                String phoneNumber = editTextPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    // Request location and send SMS
                    sendLocationToPhone(phoneNumber);
                }
            }
        });

        return view;
    }

    // Function to request and send the location to the phone number
    private void sendLocationToPhone(final String phoneNumber) {
        // Check permissions for location and SMS
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request them
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 1);
            return;
        }

        // Get the current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Get latitude and longitude
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Prepare the message
                            String message = "My current location is: \nLatitude: " + latitude + "\nLongitude: " + longitude;

                            // Send SMS
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                            Toast.makeText(getContext(), "Location sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to get location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, try sending the location
                String phoneNumber = editTextPhoneNumber.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    sendLocationToPhone(phoneNumber);
                }
            } else {
                Toast.makeText(getContext(), "Permissions denied. Cannot send location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
