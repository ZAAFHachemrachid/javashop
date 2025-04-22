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
        String cpuCategoryId = null;
        String gpuCategoryId = null;
        String ramCategoryId = null;
        String storageCategoryId = null;
        String mbCategoryId = null;
        String psuCategoryId = null;
        String caseCategoryId = null;
        String coolingCategoryId = null;
        
        for (Category category : categories) {
            switch (category.getId()) {
                case "CPU":
                    cpuCategoryId = category.getId();
                    break;
                case "GPU":
                    gpuCategoryId = category.getId();
                    break;
                case "RAM":
                    ramCategoryId = category.getId();
                    break;
                case "STORAGE":
                    storageCategoryId = category.getId();
                    break;
                case "MB":
                    mbCategoryId = category.getId();
                    break;
                case "PSU":
                    psuCategoryId = category.getId();
                    break;
                case "CASE":
                    caseCategoryId = category.getId();
                    break;
                case "COOLING":
                    coolingCategoryId = category.getId();
                    break;
            }
        }
        
        // CPU products
        if (cpuCategoryId != null) {
            Product intelHighEnd = new Product(
                    UUID.randomUUID().toString(),
                    "Intel Core i9-12900K",
                    "High-end desktop processor with exceptional performance",
                    "https://example.com/images/i9_12900k.jpg",
                    589.99,
                    10,
                    cpuCategoryId
            );
            intelHighEnd.setFeatured(true);
            intelHighEnd.setSpecifications("16 cores (8P+8E), 24 threads, Up to 5.2 GHz, 30MB Cache, 125W TDP");
            intelHighEnd.setRating(4.9);
            intelHighEnd.setReviewCount(67);
            
            Product intelMidRange = new Product(
                    UUID.randomUUID().toString(),
                    "Intel Core i5-12600K",
                    "Excellent mid-range desktop processor",
                    "https://example.com/images/i5_12600k.jpg",
                    289.99,
                    15,
                    cpuCategoryId
            );
            intelMidRange.setSpecifications("10 cores (6P+4E), 16 threads, Up to 4.9 GHz, 20MB Cache, 125W TDP");
            intelMidRange.setRating(4.7);
            intelMidRange.setReviewCount(112);
            
            Product amdHighEnd = new Product(
                    UUID.randomUUID().toString(),
                    "AMD Ryzen 9 5950X",
                    "High-end desktop processor with 16 cores",
                    "https://example.com/images/ryzen_5950x.jpg",
                    549.99,
                    8,
                    cpuCategoryId
            );
            amdHighEnd.setFeatured(true);
            amdHighEnd.setSpecifications("16 cores, 32 threads, Up to 4.9 GHz, 72MB Cache, 105W TDP");
            amdHighEnd.setRating(4.8);
            amdHighEnd.setReviewCount(89);
            
            Product amdMidRange = new Product(
                    UUID.randomUUID().toString(),
                    "AMD Ryzen 5 5600X",
                    "Mid-range desktop processor",
                    "https://example.com/images/ryzen_5600x.jpg",
                    279.99,
                    25,
                    cpuCategoryId
            );
            amdMidRange.setSpecifications("6 cores, 12 threads, Up to 4.6 GHz, 35MB Cache, 65W TDP");
            amdMidRange.setRating(4.8);
            amdMidRange.setReviewCount(156);
            amdMidRange.setDiscountPercentage(10);
            
            products.add(intelHighEnd);
            products.add(intelMidRange);
            products.add(amdHighEnd);
            products.add(amdMidRange);
        }
        
        // GPU products
        if (gpuCategoryId != null) {
            Product highEndGpu = new Product(
                    UUID.randomUUID().toString(),
                    "NVIDIA RTX 3080",
                    "High-end graphics card",
                    "https://example.com/images/rtx_3080.jpg",
                    799.99,
                    5,
                    gpuCategoryId
            );
            highEndGpu.setFeatured(true);
            highEndGpu.setSpecifications("10GB GDDR6X, 8704 CUDA Cores, 1.71 GHz Boost Clock");
            highEndGpu.setRating(4.8);
            highEndGpu.setReviewCount(98);
            
            Product midRangeGpu = new Product(
                    UUID.randomUUID().toString(),
                    "NVIDIA RTX 3060 Ti",
                    "Mid-range graphics card with excellent value",
                    "https://example.com/images/rtx_3060ti.jpg",
                    399.99,
                    15,
                    gpuCategoryId
            );
            midRangeGpu.setSpecifications("8GB GDDR6, 4864 CUDA Cores, 1.67 GHz Boost Clock");
            midRangeGpu.setRating(4.7);
            midRangeGpu.setReviewCount(145);
            midRangeGpu.setDiscountPercentage(5);
            
            Product amdHighEndGpu = new Product(
                    UUID.randomUUID().toString(),
                    "AMD Radeon RX 6800 XT",
                    "High-performance AMD graphics card",
                    "https://example.com/images/rx_6800xt.jpg",
                    649.99,
                    7,
                    gpuCategoryId
            );
            amdHighEndGpu.setSpecifications("16GB GDDR6, 4608 Stream Processors, 2.25 GHz Game Clock");
            amdHighEndGpu.setRating(4.6);
            amdHighEndGpu.setReviewCount(78);
            
            Product amdMidRangeGpu = new Product(
                    UUID.randomUUID().toString(),
                    "AMD Radeon RX 6700 XT",
                    "Mid-range graphics card",
                    "https://example.com/images/rx_6700xt.jpg",
                    479.99,
                    12,
                    gpuCategoryId
            );
            amdMidRangeGpu.setSpecifications("12GB GDDR6, 2560 Stream Processors, 2.58 GHz Game Clock");
            amdMidRangeGpu.setRating(4.6);
            amdMidRangeGpu.setReviewCount(73);
            amdMidRangeGpu.setDiscountPercentage(8);
            
            products.add(highEndGpu);
            products.add(midRangeGpu);
            products.add(amdHighEndGpu);
            products.add(amdMidRangeGpu);
        }
        
        // RAM products
        if (ramCategoryId != null) {
            Product corsairRam = new Product(
                    UUID.randomUUID().toString(),
                    "Corsair Vengeance RGB Pro 32GB",
                    "High-performance DDR4 memory with RGB lighting",
                    "https://example.com/images/corsair_rgb.jpg",
                    159.99,
                    20,
                    ramCategoryId
            );
            corsairRam.setSpecifications("32GB (2x16GB), DDR4-3600MHz, CL18, RGB, Black");
            corsairRam.setRating(4.8);
            corsairRam.setReviewCount(124);
            corsairRam.setFeatured(true);
            
            Product gskillRam = new Product(
                    UUID.randomUUID().toString(),
                    "G.Skill Trident Z Neo 16GB",
                    "RGB DDR4 memory optimized for AMD Ryzen",
                    "https://example.com/images/gskill_trident.jpg",
                    109.99,
                    15,
                    ramCategoryId
            );
            gskillRam.setSpecifications("16GB (2x8GB), DDR4-3600MHz, CL16, RGB, Black/Silver");
            gskillRam.setRating(4.7);
            gskillRam.setReviewCount(89);
            gskillRam.setDiscountPercentage(5);
            
            products.add(corsairRam);
            products.add(gskillRam);
        }
        
        // Storage products
        if (storageCategoryId != null) {
            Product samsungSsd = new Product(
                    UUID.randomUUID().toString(),
                    "Samsung 970 EVO Plus 1TB",
                    "High-performance NVMe SSD",
                    "https://example.com/images/samsung_970.jpg",
                    129.99,
                    25,
                    storageCategoryId
            );
            samsungSsd.setFeatured(true);
            samsungSsd.setSpecifications("1TB, NVMe PCIe Gen 3.0 x4, M.2 2280, Up to 3,500 MB/s Read");
            samsungSsd.setRating(4.9);
            samsungSsd.setReviewCount(203);
            
            Product wdHdd = new Product(
                    UUID.randomUUID().toString(),
                    "WD Black 4TB",
                    "Performance hard drive for gaming",
                    "https://example.com/images/wd_black.jpg",
                    99.99,
                    18,
                    storageCategoryId
            );
            wdHdd.setSpecifications("4TB, 7200 RPM, SATA 6 Gb/s, 256MB Cache");
            wdHdd.setRating(4.6);
            wdHdd.setReviewCount(76);
            wdHdd.setDiscountPercentage(10);
            
            products.add(samsungSsd);
            products.add(wdHdd);
        }
        
        // Add products for other categories if they exist
        if (mbCategoryId != null) {
            Product asusMotherboard = new Product(
                    UUID.randomUUID().toString(),
                    "ASUS ROG Strix Z690-E Gaming",
                    "High-end motherboard for Intel 12th Gen processors",
                    "https://example.com/images/asus_z690.jpg",
                    399.99,
                    8,
                    mbCategoryId
            );
            asusMotherboard.setFeatured(true);
            asusMotherboard.setSpecifications("LGA 1700, Intel Z690, ATX, DDR5, PCIe 5.0, WiFi 6E");
            asusMotherboard.setRating(4.8);
            asusMotherboard.setReviewCount(56);
            
            Product msiMotherboard = new Product(
                    UUID.randomUUID().toString(),
                    "MSI MPG B550 Gaming Edge WiFi",
                    "Mid-range motherboard for AMD processors",
                    "https://example.com/images/msi_b550.jpg",
                    189.99,
                    12,
                    mbCategoryId
            );
            msiMotherboard.setSpecifications("AM4, AMD B550, ATX, DDR4, PCIe 4.0, WiFi 6");
            msiMotherboard.setRating(4.7);
            msiMotherboard.setReviewCount(89);
            msiMotherboard.setDiscountPercentage(5);
            
            products.add(asusMotherboard);
            products.add(msiMotherboard);
        }
        
        return products;
    }
} 