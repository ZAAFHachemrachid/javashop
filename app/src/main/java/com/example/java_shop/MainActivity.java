package com.example.java_shop;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.java_shop.data.repositories.CategoryRepository;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout set");

            // Initialize Categories
            CategoryRepository categoryRepository = new CategoryRepository(getApplication());
            categoryRepository.initializeDefaultCategories();
            Log.d(TAG, "Categories initialized");

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

            // Connect Bottom Navigation with Navigation Controller
            NavigationUI.setupWithNavController(bottomNav, navController);
            Log.d(TAG, "Navigation setup complete");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }
}