package com.example.java_shop.data.repositories;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.CosShopDatabase;
import com.example.java_shop.data.database.ProductDao;
import com.example.java_shop.data.models.Product;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {
    private final ProductDao productDao;
    private final ExecutorService executorService;

    public ProductRepository(Application application) {
        CosShopDatabase database = CosShopDatabase.getDatabase(application);
        productDao = database.productDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Basic CRUD operations
    public void insert(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        executorService.execute(() -> productDao.delete(product));
    }

    // Query operations
    public LiveData<Product> getProduct(String productId) {
        return productDao.getProduct(productId);
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<List<Product>> getProductsByCategory(String categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }

    @NonNull
    public LiveData<List<Product>> getProductsByCategoryPreview(@NonNull String categoryId,
                                                             @NonNull String query,
                                                             int limit) {
        String searchQuery = query.isEmpty() ? "%" : "%" + query + "%";
        return productDao.getProductsByCategoryWithLimit(categoryId, searchQuery, limit);
    }

    public LiveData<List<Product>> getFeaturedProducts() {
        return productDao.getFeaturedProducts();
    }

    public LiveData<List<Product>> getSpecialOffers() {
        return productDao.getSpecialOffers();
    }

    public LiveData<List<Product>> searchProducts(String query) {
        return productDao.searchProducts("%" + query + "%");
    }

    public LiveData<Boolean> isProductInStock(String productId) {
        return productDao.isProductInStock(productId);
    }

    public LiveData<Integer> getStockQuantity(String productId) {
        return productDao.getStockQuantity(productId);
    }

    public void updateStock(String productId, int newQuantity) {
        executorService.execute(() -> productDao.updateStock(productId, newQuantity));
    }

    public void cleanup() {
        executorService.shutdown();
    }
}