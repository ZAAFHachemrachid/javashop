package com.example.java_shop.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.ComputerShopDatabase;
import com.example.java_shop.data.database.ProductDao;
import com.example.java_shop.data.models.Product;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {
    private final ProductDao productDao;
    private final ExecutorService executorService;

    // Constructor
    public ProductRepository(Application application) {
        ComputerShopDatabase database = ComputerShopDatabase.getDatabase(application);
        productDao = database.productDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Insert operations
    public void insert(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void insertAll(List<Product> products) {
        executorService.execute(() -> productDao.insertAll(products));
    }

    // Update operations
    public void update(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    // Delete operations
    public void delete(Product product) {
        executorService.execute(() -> productDao.delete(product));
    }

    public void deleteAll() {
        executorService.execute(productDao::deleteAll);
    }

    // Query operations
    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<Product> getProduct(String productId) {
        return productDao.getProduct(productId);
    }

    public LiveData<List<Product>> getProductsByCategory(String categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }

    public LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productDao.getProductsByPriceRange(minPrice, maxPrice);
    }

    public LiveData<List<Product>> getAvailableProducts() {
        return productDao.getAvailableProducts();
    }

    public LiveData<List<Product>> searchProducts(String searchQuery) {
        return productDao.searchProducts(searchQuery);
    }

    public LiveData<List<Product>> getTopRatedProducts(int limit) {
        return productDao.getTopRatedProducts(limit);
    }

    // Stock management
    public void decreaseStock(String productId, int quantity) {
        executorService.execute(() -> productDao.decreaseStock(productId, quantity));
    }

    public void increaseStock(String productId, int quantity) {
        executorService.execute(() -> productDao.increaseStock(productId, quantity));
    }

    // Featured products
    public LiveData<List<Product>> getFeaturedProducts(int limit) {
        return productDao.getFeaturedProducts(limit);
    }

    // Price range
    public LiveData<Double> getMinPrice() {
        return productDao.getMinPrice();
    }

    public LiveData<Double> getMaxPrice() {
        return productDao.getMaxPrice();
    }

    // Cleanup
    public void cleanup() {
        executorService.shutdown();
    }
}