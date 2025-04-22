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

    // Query operations
    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :productId")
    LiveData<Product> getProduct(String productId);

    @Query("SELECT * FROM products WHERE category = :categoryId")
    LiveData<List<Product>> getProductsByCategory(String categoryId);

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice);

    @Query("SELECT * FROM products WHERE stockQuantity > 0")
    LiveData<List<Product>> getAvailableProducts();

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    LiveData<List<Product>> searchProducts(String searchQuery);

    @Query("SELECT * FROM products ORDER BY rating DESC LIMIT :limit")
    LiveData<List<Product>> getTopRatedProducts(int limit);

    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId")
    void decreaseStock(String productId, int quantity);

    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantity WHERE id = :productId")
    void increaseStock(String productId, int quantity);

    // Featured products query
    @Query("SELECT * FROM products WHERE stockQuantity > 0 ORDER BY rating DESC, reviewCount DESC LIMIT :limit")
    LiveData<List<Product>> getFeaturedProducts(int limit);

    // Price range queries
    @Query("SELECT MIN(price) FROM products")
    LiveData<Double> getMinPrice();

    @Query("SELECT MAX(price) FROM products")
    LiveData<Double> getMaxPrice();
}