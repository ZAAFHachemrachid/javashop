package com.example.java_shop;

import android.app.Application;
import com.example.java_shop.data.database.ComputerShopDatabase;
import com.example.java_shop.data.repositories.CategoryRepository;

public class JavaShopApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize database
        ComputerShopDatabase database = ComputerShopDatabase.getDatabase(this);
        
        // Initialize repositories
        CategoryRepository categoryRepository = new CategoryRepository(this);
        
        // Initialize default data in a background thread
        new Thread(() -> {
            try {
                categoryRepository.initializeDefaultCategories();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}