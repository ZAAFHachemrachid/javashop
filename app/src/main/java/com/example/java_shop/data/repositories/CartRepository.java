package com.example.java_shop.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.CartDao;
import com.example.java_shop.data.database.ComputerShopDatabase;
import com.example.java_shop.data.models.CartItem;
import com.example.java_shop.data.models.CartItemWithProduct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private final CartDao cartDao;
    private final ExecutorService executorService;

    public CartRepository(Application application) {
        ComputerShopDatabase database = ComputerShopDatabase.getDatabase(application);
        cartDao = database.cartDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Basic cart operations
    public LiveData<List<CartItemWithProduct>> getCartItemsWithProducts() {
        return cartDao.getCartItemsWithProducts();
    }

    public LiveData<Integer> getCartItemCount() {
        return cartDao.getCartItemCount();
    }

    public LiveData<Double> getCartTotal() {
        return cartDao.getCartTotal();
    }

    public void addToCart(String productId, int quantity, double price) {
        executorService.execute(() -> {
            CartItem cartItem = new CartItem(productId, quantity, price);
            cartDao.insert(cartItem);
        });
    }

    public void updateQuantity(long cartItemId, int newQuantity) {
        executorService.execute(() -> cartDao.updateQuantity(cartItemId, newQuantity));
    }

    public void removeFromCart(long cartItemId) {
        executorService.execute(() -> {
            CartItem cartItem = new CartItem("", 0, 0); // Dummy item with correct ID
            cartItem.setId(cartItemId);
            cartDao.delete(cartItem);
        });
    }

    public void clearCart() {
        executorService.execute(cartDao::clearCart);
    }

    // Helper queries
    public LiveData<Boolean> isProductInCart(String productId) {
        return cartDao.isProductInCart(productId);
    }

    public LiveData<Integer> getQuantityForProduct(String productId) {
        return cartDao.getQuantityForProduct(productId);
    }

    // Cleanup
    public void cleanup() {
        executorService.shutdown();
    }

    // Maintenance
    public void removeOldCartItems(long timestamp) {
        executorService.execute(() -> cartDao.removeOldCartItems(timestamp));
    }
}