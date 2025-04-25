package com.example.java_shop;

import android.app.Application;
import android.util.Log;

import com.example.java_shop.data.database.CosShopDatabase;
import com.example.java_shop.data.repositories.CategoryRepository;
import com.example.java_shop.utils.DataInitializer;

public class JavaShopApplication extends Application {
    private static final String TAG = "JavaShopApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize database
        CosShopDatabase database = CosShopDatabase.getDatabase(this);
        
        // Initialize repositories
        CategoryRepository categoryRepository = new CategoryRepository(this);
        
        // Initialize default data in a background thread
        new Thread(() -> {
            try {
                // First initialize basic categories
                categoryRepository.initializeDefaultCategories();
                
                Log.d(TAG, "Default categories initialized");
                
                // Then initialize additional sample data after a short delay
                Thread.sleep(1000); // Wait for 1 second to ensure categories are inserted
                
                // Initialize sample product data using our DataInitializer
                // Explicitly use the application instance
                Application app = JavaShopApplication.this;
                DataInitializer dataInitializer = new DataInitializer(app);
                dataInitializer.initializeData();
                
                Log.d(TAG, "Sample data initialization started");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing data", e);
            }
        }).start();
    }
}