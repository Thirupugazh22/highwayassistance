package com.example.prasanth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.Drawer);
        navigationView = findViewById(R.id.View);

        // Set up the toolbar with the navigation drawer
        Toolbar toolbar = findViewById(R.id.Tool);
        setSupportActionBar(toolbar);

        // Set up the NavController for fragment navigation
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);

        // Set up the AppBarConfiguration for managing drawer behavior
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile_medicaldetails, R.id.nav_sendsms,
                R.id.nav_locationtrack, R.id.nav_call, R.id.nav_map,
                R.id.nav_weather, R.id.nav_settings, R.id.nav_reportissue)
                .setDrawerLayout(drawerLayout)
                .build();

        // Set up NavigationUI to handle fragment transactions
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle item selection from the navigation menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Handle navigation menu item clicks
            if (id == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
            } else if (id == R.id.nav_profile_medicaldetails) {
                navController.navigate(R.id.nav_profile_medicaldetails);
            } else if (id == R.id.nav_sendsms) {
                navController.navigate(R.id.nav_sendsms);
            } else if (id == R.id.nav_locationtrack) {
                navController.navigate(R.id.nav_locationtrack);
            } else if (id == R.id.nav_call) {
                navController.navigate(R.id.nav_call);
            } else if (id == R.id.nav_map) {
                navController.navigate(R.id.nav_map);
            } else if (id == R.id.nav_weather) {
                navController.navigate(R.id.nav_weather);
            } else if (id == R.id.nav_settings) {
                navController.navigate(R.id.nav_settings);
            } else if (id == R.id.nav_reportissue) {
                navController.navigate(R.id.nav_reportissue);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // Handle back press to close the drawer
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
