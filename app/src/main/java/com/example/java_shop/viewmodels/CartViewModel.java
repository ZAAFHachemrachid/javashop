package com.example.java_shop.viewmodels;

import android.app.Application;
import android.os.Bundle;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.java_shop.R;
import com.example.java_shop.data.models.CartItemWithProduct;
import com.example.java_shop.data.repositories.CartRepository;
import java.util.List;

public class CartViewModel extends AndroidViewModel {

    private final CartRepository cartRepository;
    private final LiveData<List<CartItemWithProduct>> cartItems;
    private final LiveData<Double> cartTotal;
    private final MutableLiveData<NavigationCommand> navigationCommand;
    private final MutableLiveData<String> toastMessage;

    public CartViewModel(Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        cartItems = cartRepository.getCartItemsWithProducts();
        navigationCommand = new MutableLiveData<>();
        toastMessage = new MutableLiveData<>();

        // Calculate cart total whenever items change
        cartTotal = Transformations.map(cartItems, items -> {
            if (items == null) return 0.0;
            return items.stream()
                .mapToDouble(CartItemWithProduct::getTotalPrice)
                .sum();
        });
    }

    // Getters
    public LiveData<List<CartItemWithProduct>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getCartTotal() {
        return cartTotal;
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    // Cart operations
    public void increaseQuantity(CartItemWithProduct item) {
        if (item.getQuantity() >= item.getProduct().getStockQuantity()) {
            toastMessage.setValue("Maximum available quantity reached");
            return;
        }
        cartRepository.updateQuantity(item.getId(), item.getQuantity() + 1);
    }

    public void decreaseQuantity(CartItemWithProduct item) {
        if (item.getQuantity() <= 1) {
            toastMessage.setValue("Minimum quantity is 1");
            return;
        }
        cartRepository.updateQuantity(item.getId(), item.getQuantity() - 1);
    }

    public void removeItem(CartItemWithProduct item) {
        cartRepository.removeFromCart(item.getId());
    }

    public void clearCart() {
        cartRepository.clearCart();
    }

    // Navigation
    public void navigateToProduct(CartItemWithProduct item) {
        Bundle args = new Bundle();
        args.putString("productId", item.getProductId());
        navigationCommand.setValue(
            new NavigationCommand(
                R.id.action_cartFragment_to_productDetailsFragment,
                args
            )
        );
    }

    public void navigateToCheckout() {
        if (cartItems.getValue() == null || cartItems.getValue().isEmpty()) {
            toastMessage.setValue("Your cart is empty");
            return;
        }
        navigationCommand.setValue(
            new NavigationCommand(
                R.id.action_cartFragment_to_checkoutFragment,
                null
            )
        );
    }

    public void resetNavigation() {
        navigationCommand.setValue(null);
    }

    // Navigation command class
    public static class NavigationCommand {
        private final int actionId;
        private final Bundle args;

        public NavigationCommand(int actionId, Bundle args) {
            this.actionId = actionId;
            this.args = args;
        }

        public int getActionId() {
            return actionId;
        }

        public Bundle getArgs() {
            return args;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cartRepository.cleanup();
    }
}