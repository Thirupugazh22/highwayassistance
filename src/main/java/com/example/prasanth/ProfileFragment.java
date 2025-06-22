package com.example.prasanth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View; // Import for View class
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import android.content.pm.PackageManager;  // Import for PackageManager

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;  // Request code for picking an image
    private static final int CAPTURE_IMAGE = 2;  // Request code for capturing an image
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100; // Permission request code

    private ImageView imgProfile;
    private EditText edtName;
    private EditText edtBloodGroup;
    private EditText edtPhone;
    private TextView tvName;
    private TextView tvBloodGroup;
    private TextView tvPhone;
    private Button btnUploadImage;
    private Button btnSave; // Save Button
    private Switch switchEditMode;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        imgProfile = rootView.findViewById(R.id.imgProfile);
        edtName = rootView.findViewById(R.id.edtName);
        edtBloodGroup = rootView.findViewById(R.id.edtBloodGroup);
        edtPhone = rootView.findViewById(R.id.edtPhone);
        tvName = rootView.findViewById(R.id.tvName);
        tvBloodGroup = rootView.findViewById(R.id.tvBloodGroup);
        tvPhone = rootView.findViewById(R.id.tvPhone);
        btnUploadImage = rootView.findViewById(R.id.btnUploadImage);
        btnSave = rootView.findViewById(R.id.save); // Initialize Save button
        switchEditMode = rootView.findViewById(R.id.switch1); // Initialize the Switch

        // Set default visibility for edit mode
        edtName.setVisibility(View.GONE);
        edtBloodGroup.setVisibility(View.GONE);
        edtPhone.setVisibility(View.GONE);

        // Handle Switch toggling for Edit Mode
        switchEditMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch ON: Show EditTexts, Hide TextViews
                edtName.setVisibility(View.VISIBLE);
                edtBloodGroup.setVisibility(View.VISIBLE);
                edtPhone.setVisibility(View.VISIBLE);
                tvName.setVisibility(View.GONE);
                tvBloodGroup.setVisibility(View.GONE);
                tvPhone.setVisibility(View.GONE);
            } else {
                // Switch OFF: Show TextViews, Hide EditTexts
                edtName.setVisibility(View.GONE);
                edtBloodGroup.setVisibility(View.GONE);
                edtPhone.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
                tvBloodGroup.setVisibility(View.VISIBLE);
                tvPhone.setVisibility(View.VISIBLE);
            }
        });

        // Add the functionality for uploading/capturing images
        btnUploadImage.setOnClickListener(v -> showImageOptions());

        // Add functionality for saving the details when the Save button is clicked
        btnSave.setOnClickListener(v -> saveProfileDetails());

        return rootView;
    }

    // Method to show image options (pick or capture)
    private void showImageOptions() {
        // Create an AlertDialog to display options for uploading or capturing an image
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Upload from Gallery", "Capture using Camera"},
                        (dialog, which) -> {
                            if (which == 0) {
                                // User selected "Upload from Gallery"
                                pickImageFromGallery();
                            } else if (which == 1) {
                                // User selected "Capture using Camera"
                                captureImageFromCamera();
                            }
                        })
                .show();
    }

    // Method to pick an image from the gallery
    private void pickImageFromGallery() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, PICK_IMAGE);
    }

    // Method to capture an image using the camera
    private void captureImageFromCamera() {
        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is already granted, open camera
            openCamera();
        }
    }

    // Helper method to open the camera after permission is granted
    private void openCamera() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Make sure the camera is available and then open it
            startActivityForResult(captureImageIntent, CAPTURE_IMAGE);
        } else {
            // If no camera apps are available
            Toast.makeText(getActivity(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of image selection or capture
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    imgProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(bitmap);
            }
        }
    }

    // Save Profile Details
    private void saveProfileDetails() {
        // Get the text from EditTexts
        String name = edtName.getText().toString();
        String bloodGroup = edtBloodGroup.getText().toString();
        String phone = edtPhone.getText().toString();

        // Set the TextView fields to display the entered values
        tvName.setText(name);
        tvBloodGroup.setText(bloodGroup);
        tvPhone.setText(phone);

        // Hide EditTexts and Show TextViews after saving
        edtName.setVisibility(View.GONE);
        edtBloodGroup.setVisibility(View.GONE);
        edtPhone.setVisibility(View.GONE);
        tvName.setVisibility(View.VISIBLE);
        tvBloodGroup.setVisibility(View.VISIBLE);
        tvPhone.setVisibility(View.VISIBLE);

        // Turn off EditMode (Switch)
        switchEditMode.setChecked(false);
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera();
            } else {
                // Permission denied, show a message
                Toast.makeText(getActivity(), "Camera permission is required to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
