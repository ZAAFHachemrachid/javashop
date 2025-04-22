package com.example.java_shop;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavController navController;
    private BottomNavigationView bottomNav;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout set");

            // Initialize Bottom Navigation
            bottomNav = findViewById(R.id.bottom_nav);
            if (bottomNav == null) {
                Log.e(TAG, "Bottom navigation view not found");
                return;
            }
            Log.d(TAG, "Bottom navigation initialized");

            // Setup Navigation Controller
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment == null) {
                Log.e(TAG, "NavHostFragment not found");
                return;
            }
            navController = navHostFragment.getNavController();
            Log.d(TAG, "Navigation controller initialized");

            // Define top level destinations
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.homeFragment, R.id.categoriesFragment, R.id.cartFragment, R.id.accountFragment
            ).build();

            // Connect Bottom Navigation with Navigation Controller
            NavigationUI.setupWithNavController(bottomNav, navController);
            Log.d(TAG, "Navigation setup complete");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the up button in the action bar
        return NavigationUI.navigateUp(navController, appBarConfiguration) 
               || super.onSupportNavigateUp();
    }
}