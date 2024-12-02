package com.ezxuen.studytracker;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ezxuen.studytracker.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity serves as the entry point for the Study Tracker app.
 * It manages the BottomNavigationView and navigation between fragments.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            // Find the NavHostFragment that manages the navigation graph
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);

            // Ensure NavHostFragment is not null
            if (navHostFragment == null) {
                Log.e("MainActivity", "NavHostFragment is null! Check your layout.");
                return;
            }

            // Get the NavController from the NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Set up BottomNavigationView
            BottomNavigationView navView = binding.navView;

            // Define the top-level destinations for the app
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_reminder, R.id.navigation_history)
                    .build();

            // Link the NavController to the ActionBar and BottomNavigationView
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

            Log.d("MainActivity", "NavController setup completed successfully.");
        } catch (Exception e) {
            // Log any exceptions encountered during initialization
            Log.e("MainActivity", "Error initializing NavController: " + e.getMessage(), e);
        }
    }

    /**
     * Handles navigation when the up button is pressed.
     * @return true if navigation was handled, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main)).getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}