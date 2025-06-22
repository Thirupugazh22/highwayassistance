package com.example.prasanth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 201;
    private static final int SAMPLE_RATE = 44100;  // Standard audio sample rate
    private static final double THRESHOLD_FREQUENCY = 10000.0;  // Threshold frequency for accident detection (in Hz)
    private static final String EMERGENCY_NUMBER = "108"; // Emergency number to call
    private static final int ACCIDENT_DETECTION_DELAY = 5000; // Delay to detect sustained frequency in milliseconds

    private ToggleButton toggleDrivingMode;
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private Thread recordingThread;
    private double lastFrequency = 0;
    private long lastCallTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize driving mode toggle button
        toggleDrivingMode = root.findViewById(R.id.toggleDrivingMode);
        requestMicrophonePermission();

        // Set up listener for driving mode toggle
        toggleDrivingMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getActivity(), "Driving Mode Activated", Toast.LENGTH_SHORT).show();
                startRecording();
            } else {
                Toast.makeText(getActivity(), "Driving Mode Deactivated", Toast.LENGTH_SHORT).show();
                stopRecording();
            }
        });

        return root;
    }

    private void requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void startRecording() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioRecord.startRecording();
        isRecording = true;

        recordingThread = new Thread(() -> {
            short[] buffer = new short[bufferSize];
            while (isRecording) {
                int readSize = audioRecord.read(buffer, 0, buffer.length);
                if (readSize > 0) {
                    double frequency = calculateFrequency(buffer, readSize);
                    if (frequency >= THRESHOLD_FREQUENCY && isSustained(frequency)) {
                        notifyAccidentDetected();
                    }
                }
            }
        });
        recordingThread.start();
    }

    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            if (recordingThread != null) {
                recordingThread.interrupt();
                try {
                    recordingThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                recordingThread = null;
            }
        }
    }

    private double calculateFrequency(short[] buffer, int readSize) {
        double[] spectrum = performFFT(buffer, readSize);
        return getMaxFrequencyFromSpectrum(spectrum);
    }

    private double[] performFFT(short[] buffer, int readSize) {
        double[] spectrum = new double[readSize];
        for (int i = 0; i < readSize; i++) {
            spectrum[i] = buffer[i]; // Placeholder, real FFT would be here
        }
        return spectrum;
    }

    private double getMaxFrequencyFromSpectrum(double[] spectrum) {
        double maxFrequency = 0;
        for (double frequency : spectrum) {
            if (frequency > maxFrequency) {
                maxFrequency = frequency;
            }
        }
        return maxFrequency;
    }

    private boolean isSustained(double frequency) {
        long currentTime = System.currentTimeMillis();
        if (frequency >= THRESHOLD_FREQUENCY) {
            if (lastFrequency == frequency && currentTime - lastCallTime >= ACCIDENT_DETECTION_DELAY) {
                return true;
            } else {
                lastFrequency = frequency;
            }
        }
        return false;
    }

    private void notifyAccidentDetected() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getActivity(), "Accident Detected! Notifying Contacts...", Toast.LENGTH_SHORT).show();
            callEmergencyNumber();
            lastCallTime = System.currentTimeMillis(); // Reset last call time
        });
    }

    private void callEmergencyNumber() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER));
                startActivity(callIntent);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Unable to make the call. Permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
            Toast.makeText(getActivity(), "Please grant call permission to enable emergency calling", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Microphone permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Microphone permission is required for accident detection", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callEmergencyNumber();
            } else {
                Toast.makeText(getActivity(), "Call permission is required for emergency calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRecording();  // Ensure recording stops when fragment view is destroyed
    }
}
