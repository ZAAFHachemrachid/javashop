package com.example.java_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.java_shop.data.models.Product;
import java.util.List;

@Dao
public interface ProductDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    // Update operations
    @Update
    void update(Product product);

    // Delete operations
    @Delete
    void delete(Product product);

    @Query("DELETE FROM products")
    void deleteAll();

    // Basic queries
    @Query("SELECT * FROM products WHERE id = :productId")
    LiveData<Product> getProduct(String productId);

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAllProducts();

    // Category queries
    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    LiveData<List<Product>> getProductsByCategory(String categoryId);
    
    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND (name LIKE :searchQuery OR description LIKE :searchQuery) ORDER BY name ASC LIMIT :limit")
    LiveData<List<Product>> getProductsByCategoryWithLimit(String categoryId, String searchQuery, int limit);

    // Featured products
    @Query("SELECT * FROM products WHERE isFeatured = 1 ORDER BY price DESC LIMIT 5")
    LiveData<List<Product>> getFeaturedProducts();

    // Special offers
    @Query("SELECT * FROM products WHERE discountPercentage > 0 ORDER BY discountPercentage DESC LIMIT 10")
    LiveData<List<Product>> getSpecialOffers();

    // Search
    @Query("SELECT * FROM products WHERE name LIKE :query OR description LIKE :query")
    LiveData<List<Product>> searchProducts(String query);

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND (name LIKE :query OR description LIKE :query)")
    LiveData<List<Product>> searchProductsByCategory(String categoryId, String query);

    // Stock management
    @Query("SELECT (stockQuantity > 0) FROM products WHERE id = :productId")
    LiveData<Boolean> isProductInStock(String productId);

    @Query("SELECT stockQuantity FROM products WHERE id = :productId")
    LiveData<Integer> getStockQuantity(String productId);

    @Query("UPDATE products SET stockQuantity = :newQuantity WHERE id = :productId")
    void updateStock(String productId, int newQuantity);

    // Price queries
    @Query("SELECT * FROM products ORDER BY price ASC")
    LiveData<List<Product>> getProductsSortedByPriceAsc();

    @Query("SELECT * FROM products ORDER BY price DESC")
    LiveData<List<Product>> getProductsSortedByPriceDesc();

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    LiveData<List<Product>> getProductsInPriceRange(double minPrice, double maxPrice);
}