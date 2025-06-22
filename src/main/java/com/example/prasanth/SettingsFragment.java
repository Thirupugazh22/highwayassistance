package com.example.prasanth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.prasanth.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set up the switch listener
        binding.switchEnableFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String message = isChecked ? "Feature Enabled" : "Feature Disabled";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up other settings options click listeners
        binding.optionChangePassword.setOnClickListener(v ->
                Toast.makeText(getContext(), "Change Password Clicked", Toast.LENGTH_SHORT).show());

        binding.optionNotifications.setOnClickListener(v ->
                Toast.makeText(getContext(), "Notifications Clicked", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
