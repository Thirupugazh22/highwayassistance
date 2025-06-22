package com.example.prasanth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


public class SmsFragment extends Fragment {

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    private EditText phoneNumberEditText;
    private EditText messageEditText;
    private Button sendSmsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sms, container, false);

        // Initialize UI components
        phoneNumberEditText = view.findViewById(R.id.editTextPhoneNumber);
        messageEditText = view.findViewById(R.id.editTextMessage);
        sendSmsButton = view.findViewById(R.id.btnSendSMS);

        // Set up the button click listener
        sendSmsButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString();
            String message = messageEditText.getText().toString();

            // Send the SMS if both fields are not empty
            if (!phoneNumber.isEmpty() && !message.isEmpty()) {
                sendSMS(phoneNumber, message);
            } else {
                Toast.makeText(getContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Method to send SMS
    private void sendSMS(String phoneNumber, String message) {
        // Check if the app has permission to send SMS
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now send the SMS
                String phoneNumber = phoneNumberEditText.getText().toString();
                String message = messageEditText.getText().toString();
                sendSMS(phoneNumber, message);
            } else {
                // Permission denied, show a toast message
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
