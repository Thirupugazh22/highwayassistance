package com.example.prasanth;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.prasanth.R;

public class CallFragment extends Fragment {

    private static final int CALL_PERMISSION_REQUEST_CODE = 1;
    private ListView listViewEmergencyNumbers;
    private String selectedNumber;

    public CallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        // Initialize the ListView
        listViewEmergencyNumbers = view.findViewById(R.id.listViewEmergencyNumbers);

        // Create an ArrayAdapter to display emergency numbers
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.emergency_numbers,
                android.R.layout.simple_list_item_1
        );

        // Set the adapter for the ListView
        listViewEmergencyNumbers.setAdapter(adapter);

        // Set an OnItemClickListener to handle clicks on emergency numbers
        listViewEmergencyNumbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                // Get the selected emergency number
                selectedNumber = parentView.getItemAtPosition(position).toString();

                // Show confirmation dialog
                showConfirmationDialog(selectedNumber);
            }
        });

        return view;
    }

    // Function to show a confirmation dialog before making the call
    private void showConfirmationDialog(final String phoneNumber) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Call Emergency")
                .setMessage("Do you want to call " + phoneNumber + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Check permissions and make the call
                        checkPermissionAndMakeCall(phoneNumber);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Check permissions and make the call
    private void checkPermissionAndMakeCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, make the call
            makeCall(phoneNumber);
        } else {
            // Request permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        }
    }

    // Function to make the actual call
    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                if (selectedNumber != null) {
                    makeCall(selectedNumber);
                }
            } else {
                // Permission denied, show a toast message
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
