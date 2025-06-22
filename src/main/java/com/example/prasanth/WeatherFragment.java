package com.example.prasanth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.prasanth.databinding.FragmentWeatherBinding;

public class WeatherFragment extends Fragment {

    private FragmentWeatherBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set placeholder weather information
        binding.textViewWeatherInfo.setText("Weather: Sunny, 25°C");

        // Implement logic to fetch real weather data here

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
