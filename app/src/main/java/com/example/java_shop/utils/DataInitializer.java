package com.example.java_shop.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.java_shop.data.database.ComputerShopDatabase;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.repositories.CategoryRepository;
import com.example.java_shop.data.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataInitializer {
    private static final String TAG = "DataInitializer";
    private final Context context;
    private final Executor executor;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        ComputerShopDatabase database = ComputerShopDatabase.getDatabase(context);
        
        // Get the application context
        Context appContext = context.getApplicationContext();
        
        // Check if we can safely cast to Application
        if (appContext instanceof Application) {
            // Initialize repositories for data insertion
            Application app = (Application) appContext;
            categoryRepository = new CategoryRepository(app);
            productRepository = new ProductRepository(app);
        } else {
            // This shouldn't happen in a normal Android app, but handle it for safety
            throw new IllegalArgumentException("Context provided to DataInitializer must be or provide an Application instance");
        }
    }

    public void initializeData() {
        executor.execute(() -> {
            Log.d(TAG, "Starting sample data initialization...");
            
            try {
                // Get all existing categories
                List<Category> existingCategories = getExistingCategories();
                
                if (existingCategories != null && !existingCategories.isEmpty()) {
                    Log.d(TAG, "Found " + existingCategories.size() + " existing categories");
                    
                    // Create and insert products based on existing categories
                    List<Product> products = createProductsForExistingCategories(existingCategories);
                    for (Product product : products) {
                        productRepository.insert(product);
                    }
                    
                    Log.d(TAG, "Inserted " + products.size() + " sample products");
                } else {
                    Log.d(TAG, "No existing categories found. Sample data initialization skipped.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing sample data", e);
            }
        });
    }

    private List<Category> getExistingCategories() {
        final List<Category>[] categoriesList = (List<Category>[]) new List<?>[1];
        final CountDownLatch latch = new CountDownLatch(1);
        
        LiveData<List<Category>> categoriesLiveData = categoryRepository.getAllCategories();
        
        try {
            Observer<List<Category>> observer = new Observer<List<Category>>() {
                @Override
                public void onChanged(List<Category> categories) {
                    categoriesList[0] = categories;
                    latch.countDown();
                    categoriesLiveData.removeObserver(this);
                }
            };
            
            // Observe on the main thread
            android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
            mainHandler.post(() -> categoriesLiveData.observeForever(observer));
            
            // Wait for the result with a timeout
            latch.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, "Error getting existing categories", e);
        }
        
        return categoriesList[0];
    }

    private List<Product> createProductsForExistingCategories(List<Category> categories) {
        List<Product> products = new ArrayList<>();
        
        // Map category names to IDs
        String faceProductsId = null;
        String eyeProductsId = null;
        String lipProductsId = null;
        String skincareId = null;
        String toolsId = null;
        
        for (Category category : categories) {
            switch (category.getId()) {
                case "FACE":
                    faceProductsId = category.getId();
                    break;
                case "EYE":
                    eyeProductsId = category.getId();
                    break;
                case "LIP":
                    lipProductsId = category.getId();
                    break;
                case "SKINCARE":
                    skincareId = category.getId();
                    break;
                case "TOOLS":
                    toolsId = category.getId();
                    break;
            }
        }
        
        // Face Products
        if (faceProductsId != null) {
            // Premium Foundation
            Product premiumFoundation = new Product(
                UUID.randomUUID().toString(),
                "Luminous Silk Foundation",
                "Premium liquid foundation for a flawless finish",
                "https://example.com/images/foundation.jpg",
                42.99,
                15,
                faceProductsId
            );
            premiumFoundation.setFeatured(true);
            premiumFoundation.setSpecifications("Buildable coverage, Oil-free, 30ml");
            premiumFoundation.setRating(4.8);
            premiumFoundation.setReviewCount(256);
            
            // Concealer
            Product concealer = new Product(
                UUID.randomUUID().toString(),
                "Radiant Creamy Concealer",
                "Multi-purpose concealer for all skin types",
                "https://example.com/images/concealer.jpg",
                29.99,
                20,
                faceProductsId
            );
            concealer.setSpecifications("Medium to full coverage, Creamy texture, 6ml");
            concealer.setRating(4.7);
            concealer.setReviewCount(189);
            
            // Setting Powder
            Product settingPowder = new Product(
                UUID.randomUUID().toString(),
                "Translucent Setting Powder",
                "Lightweight setting powder for long-lasting makeup",
                "https://example.com/images/powder.jpg",
                38.99,
                12,
                faceProductsId
            );
            settingPowder.setSpecifications("Translucent finish, Oil-absorbing, 10g");
            settingPowder.setRating(4.9);
            settingPowder.setReviewCount(312);
            settingPowder.setDiscountPercentage(15);
            
            products.add(premiumFoundation);
            products.add(concealer);
            products.add(settingPowder);
        }

        // Eye Products
        if (eyeProductsId != null) {
            // Eyeshadow Palette
            Product eyeshadowPalette = new Product(
                UUID.randomUUID().toString(),
                "Nude Basics Palette",
                "Essential nude eyeshadow palette",
                "https://example.com/images/palette.jpg",
                54.99,
                8,
                eyeProductsId
            );
            eyeshadowPalette.setFeatured(true);
            eyeshadowPalette.setSpecifications("12 matte & shimmer shades, Highly pigmented");
            eyeshadowPalette.setRating(4.9);
            eyeshadowPalette.setReviewCount(423);

            // Mascara
            Product mascara = new Product(
                UUID.randomUUID().toString(),
                "Volume Boost Mascara",
                "Volumizing and lengthening mascara",
                "https://example.com/images/mascara.jpg",
                24.99,
                25,
                eyeProductsId
            );
            mascara.setSpecifications("Waterproof, Smudge-proof, 10ml");
            mascara.setRating(4.7);
            mascara.setReviewCount(567);
            
            products.add(eyeshadowPalette);
            products.add(mascara);
        }

        // Lip Products
        if (lipProductsId != null) {
            // Matte Lipstick
            Product matteLipstick = new Product(
                UUID.randomUUID().toString(),
                "Velvet Matte Lipstick",
                "Long-lasting matte lipstick",
                "https://example.com/images/lipstick.jpg",
                19.99,
                30,
                lipProductsId
            );
            matteLipstick.setFeatured(true);
            matteLipstick.setSpecifications("Highly pigmented, Non-drying formula");
            matteLipstick.setRating(4.8);
            matteLipstick.setReviewCount(345);

            // Lip Gloss
            Product lipGloss = new Product(
                UUID.randomUUID().toString(),
                "Shine Bomb Lip Gloss",
                "High-shine lip gloss",
                "https://example.com/images/lipgloss.jpg",
                16.99,
                40,
                lipProductsId
            );
            lipGloss.setSpecifications("Non-sticky formula, Moisturizing");
            lipGloss.setRating(4.6);
            lipGloss.setReviewCount(234);
            lipGloss.setDiscountPercentage(20);
            
            products.add(matteLipstick);
            products.add(lipGloss);
        }

        // Skincare Products
        if (skincareId != null) {
            // Moisturizer
            Product moisturizer = new Product(
                UUID.randomUUID().toString(),
                "Hydra-Boost Moisturizer",
                "Intense hydrating face cream",
                "https://example.com/images/moisturizer.jpg",
                48.99,
                20,
                skincareId
            );
            moisturizer.setFeatured(true);
            moisturizer.setSpecifications("For all skin types, 50ml, Oil-free");
            moisturizer.setRating(4.9);
            moisturizer.setReviewCount(678);

            // Serum
            Product serum = new Product(
                UUID.randomUUID().toString(),
                "Vitamin C Brightening Serum",
                "Antioxidant-rich brightening serum",
                "https://example.com/images/serum.jpg",
                59.99,
                15,
                skincareId
            );
            serum.setSpecifications("20% Vitamin C, 30ml");
            serum.setRating(4.8);
            serum.setReviewCount(432);
            
            products.add(moisturizer);
            products.add(serum);
        }

        // Tools & Accessories
        if (toolsId != null) {
            // Brush Set
            Product brushSet = new Product(
                UUID.randomUUID().toString(),
                "Pro Makeup Brush Set",
                "Complete set of professional makeup brushes",
                "https://example.com/images/brushset.jpg",
                79.99,
                10,
                toolsId
            );
            brushSet.setFeatured(true);
            brushSet.setSpecifications("15 pieces, Synthetic bristles, With case");
            brushSet.setRating(4.7);
            brushSet.setReviewCount(289);

            // Beauty Blender
            Product beautyBlender = new Product(
                UUID.randomUUID().toString(),
                "Pro Beauty Blender",
                "Professional makeup sponge",
                "https://example.com/images/beautyblender.jpg",
                19.99,
                35,
                toolsId
            );
            beautyBlender.setSpecifications("Latex-free, Reusable");
            beautyBlender.setRating(4.8);
            beautyBlender.setReviewCount(567);
            beautyBlender.setDiscountPercentage(15);
            
            products.add(brushSet);
            products.add(beautyBlender);
        }
        
        return products;
    }
} 