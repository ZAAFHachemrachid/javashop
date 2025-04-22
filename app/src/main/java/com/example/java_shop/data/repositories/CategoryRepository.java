package com.example.java_shop.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.ComputerShopDatabase;
import com.example.java_shop.data.database.CategoryDao;
import com.example.java_shop.data.models.Category;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final ExecutorService executorService;

    // Constructor
    public CategoryRepository(Application application) {
        ComputerShopDatabase database = ComputerShopDatabase.getDatabase(application);
        categoryDao = database.categoryDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    // Insert operations
    public void insert(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void insertAll(List<Category> categories) {
        executorService.execute(() -> categoryDao.insertAll(categories));
    }

    // Update operations
    public void update(Category category) {
        executorService.execute(() -> categoryDao.update(category));
    }

    // Delete operations
    public void delete(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }

    public void deleteAll() {
        executorService.execute(categoryDao::deleteAll);
    }

    // Basic queries
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public LiveData<Category> getCategory(String categoryId) {
        return categoryDao.getCategory(categoryId);
    }

    // Hierarchy queries
    public LiveData<List<Category>> getTopLevelCategories() {
        return categoryDao.getTopLevelCategories();
    }

    public LiveData<List<Category>> getSubcategories(String parentId) {
        return categoryDao.getSubcategories(parentId);
    }

    // Active categories
    public LiveData<List<Category>> getActiveCategories() {
        return categoryDao.getActiveCategories();
    }

    public LiveData<List<Category>> getActiveTopLevelCategories() {
        return categoryDao.getActiveTopLevelCategories();
    }

    public LiveData<List<Category>> getActiveSubcategories(String parentId) {
        return categoryDao.getActiveSubcategories(parentId);
    }

    // Category path
    public LiveData<List<Category>> getCategoryPath(String categoryId) {
        return categoryDao.getCategoryPath(categoryId);
    }

    // Category counts
    public LiveData<Integer> getSubcategoryCount(String categoryId) {
        return categoryDao.getSubcategoryCount(categoryId);
    }

    // Display order management
    public void updateDisplayOrder(String categoryId, int newOrder) {
        executorService.execute(() -> categoryDao.updateDisplayOrder(categoryId, newOrder));
    }

    // Category initialization
    public void initializeDefaultCategories() {
        executorService.execute(() -> {
            // Create main product categories
            List<Category> mainCategories = List.of(
                new Category("CPU", "Processors", "Central Processing Units", "@drawable/ic_category_cpu", 1, true, null),
                new Category("GPU", "Graphics Cards", "Graphics Processing Units", "@drawable/ic_category_gpu", 2, true, null),
                new Category("MB", "Motherboards", "Motherboards", "@drawable/ic_category_motherboard", 3, true, null),
                new Category("RAM", "Memory", "RAM modules", "@drawable/ic_category_memory", 4, true, null),
                new Category("STORAGE", "Storage", "Storage devices", "@drawable/ic_category_storage", 5, true, null),
                new Category("PSU", "Power Supplies", "Power Supply Units", "@drawable/ic_category_psu", 6, true, null),
                new Category("CASE", "Cases", "Computer Cases", "@drawable/ic_category_case", 7, true, null),
                new Category("COOLING", "Cooling", "Cooling Systems", "@drawable/ic_category_cooling", 8, true, null)
            );
            categoryDao.insertAll(mainCategories);
        });
    }

    // Cleanup
    public void cleanup() {
        executorService.shutdown();
    }
}