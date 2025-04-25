package com.example.java_shop.viewmodels;

import android.app.Application;
import android.os.Bundle;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.java_shop.R;
import com.example.java_shop.data.models.CartItemWithProduct;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.models.OrderItem;
import com.example.java_shop.data.repositories.CartRepository;
import com.example.java_shop.data.repositories.OrderRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CheckoutViewModel extends AndroidViewModel {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final LiveData<List<CartItemWithProduct>> cartItems;
    private final LiveData<Double> subtotal;
    private final MutableLiveData<String> fullName;
    private final MutableLiveData<String> streetAddress;
    private final MutableLiveData<String> city;
    private final MutableLiveData<String> zipCode;
    private final MutableLiveData<PaymentMethod> paymentMethod;
    private final MutableLiveData<NavigationCommand> navigationCommand;
    private final MutableLiveData<String> toastMessage;

    // Constants for shipping and tax
    private static final double SHIPPING_RATE = 19.99;
    private static final double TAX_RATE = 0.08; // 8%

    public enum PaymentMethod {
        CREDIT_CARD,
        PAY_ON_DELIVERY
    }

    public CheckoutViewModel(Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        orderRepository = new OrderRepository(application);
        
        // Initialize LiveData
        cartItems = cartRepository.getCartItemsWithProducts();
        fullName = new MutableLiveData<>();
        streetAddress = new MutableLiveData<>();
        city = new MutableLiveData<>();
        zipCode = new MutableLiveData<>();
        paymentMethod = new MutableLiveData<>(PaymentMethod.CREDIT_CARD);
        navigationCommand = new MutableLiveData<>();
        toastMessage = new MutableLiveData<>();

        // Calculate subtotal from cart items
        subtotal = Transformations.map(cartItems, items -> {
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

    public LiveData<Double> getSubtotal() {
        return subtotal;
    }

    public double getShippingCost() {
        return SHIPPING_RATE;
    }

    public double getTaxAmount() {
        Double currentSubtotal = subtotal.getValue();
        return currentSubtotal != null ? currentSubtotal * TAX_RATE : 0.0;
    }

    public double getOrderTotal() {
        Double currentSubtotal = subtotal.getValue();
        return (currentSubtotal != null ? currentSubtotal : 0.0) + SHIPPING_RATE + getTaxAmount();
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    // Setters for form fields
    public void setFullName(String name) {
        fullName.setValue(name);
    }

    public void setStreetAddress(String address) {
        streetAddress.setValue(address);
    }

    public void setCity(String city) {
        this.city.setValue(city);
    }

    public void setZipCode(String zipCode) {
        this.zipCode.setValue(zipCode);
    }

    public void setPaymentMethod(PaymentMethod method) {
        paymentMethod.setValue(method);
    }

    // Validation methods
    private boolean validateShippingInfo() {
        if (fullName.getValue() == null || fullName.getValue().trim().isEmpty()) {
            toastMessage.setValue("Please enter your full name");
            return false;
        }
        if (streetAddress.getValue() == null || streetAddress.getValue().trim().isEmpty()) {
            toastMessage.setValue("Please enter your street address");
            return false;
        }
        if (city.getValue() == null || city.getValue().trim().isEmpty()) {
            toastMessage.setValue("Please enter your city");
            return false;
        }
        if (zipCode.getValue() == null || !zipCode.getValue().matches("\\d{5}")) {
            toastMessage.setValue("Please enter a valid ZIP code");
            return false;
        }
        return true;
    }

    // Order processing
    public void placeOrder() {
        if (!validateShippingInfo()) {
            return;
        }

        List<CartItemWithProduct> items = cartItems.getValue();
        if (items == null || items.isEmpty()) {
            toastMessage.setValue("Your cart is empty");
            return;
        }

        // Create order
        Order order = new Order(
            1, // TODO: Get actual user ID from session
            new Date(),
            "PENDING",
            getOrderTotal()
        );

        List<OrderItem> orderItems = cartItems.getValue().stream()
            .map(cartItem -> {
                return new OrderItem(
                    0, // Order ID will be set by repository
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
                );
            })
            .collect(Collectors.toList());

        orderRepository.createOrder(order, orderItems, orderId -> {
            // Clear cart
            cartRepository.clearCart();

            // Navigate to confirmation screen
            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            navigationCommand.postValue(
                new NavigationCommand(
                    R.id.action_checkoutFragment_to_orderConfirmationFragment,
                    args
                )
            );
        });
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