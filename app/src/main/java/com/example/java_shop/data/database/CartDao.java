package com.example.java_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.java_shop.data.models.CartItem;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.models.CartItemWithProduct;
import java.util.List;

@Dao
public interface CartDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartItem cartItem);

    // Update operations
    @Update
    void update(CartItem cartItem);

    // Delete operations
    @Delete
    void delete(CartItem cartItem);

    @Query("DELETE FROM cart_items")
    void clearCart();

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    void removeProduct(String productId);

    // Basic queries
    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItem>> getAllCartItems();

    @Query("SELECT * FROM cart_items WHERE id = :cartItemId")
    LiveData<CartItem> getCartItem(long cartItemId);

    // Cart item with product details
    @Transaction
    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItemWithProduct>> getCartItemsWithProducts();

    // Cart summary queries
    @Query("SELECT COUNT(*) FROM cart_items")
    LiveData<Integer> getCartItemCount();

    @Query("SELECT SUM(quantity) FROM cart_items")
    LiveData<Integer> getTotalItemCount();

    @Query("SELECT SUM(quantity * priceAtAddition) FROM cart_items")
    LiveData<Double> getCartTotal();

    // Quantity management
    @Query("SELECT quantity FROM cart_items WHERE productId = :productId")
    LiveData<Integer> getQuantityForProduct(String productId);

    @Query("UPDATE cart_items SET quantity = :newQuantity WHERE id = :cartItemId")
    void updateQuantity(long cartItemId, int newQuantity);

    @Transaction
    @Query("SELECT EXISTS(SELECT 1 FROM cart_items WHERE productId = :productId)")
    LiveData<Boolean> isProductInCart(String productId);

    // Time-based queries
    @Query("SELECT * FROM cart_items ORDER BY addedTimestamp DESC")
    LiveData<List<CartItem>> getCartItemsByAddTime();

    @Query("SELECT * FROM cart_items ORDER BY lastModifiedTimestamp DESC")
    LiveData<List<CartItem>> getCartItemsByLastModified();

    // Cleanup old cart items
    @Query("DELETE FROM cart_items WHERE lastModifiedTimestamp < :timestamp")
    void removeOldCartItems(long timestamp);
}