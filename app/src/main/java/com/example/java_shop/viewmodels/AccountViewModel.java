package com.example.java_shop.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.data.models.User;
import com.example.java_shop.data.models.Address;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.repositories.UserRepository;
import com.example.java_shop.data.repositories.AddressRepository;
import com.example.java_shop.data.repositories.OrderRepository;
import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // Current user ID (would be set after login in a real app)
    private int currentUserId = 1; // Temporary for development

    public AccountViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        addressRepository = new AddressRepository(application);
        orderRepository = new OrderRepository(application);
    }

    // User operations
    public LiveData<User> getCurrentUser() {
        return userRepository.getUserById(currentUserId);
    }

    public void updateUserProfile(User user) {
        userRepository.update(user);
    }

    public void updateProfilePicture(String picturePath) {
        userRepository.updateProfilePicture(currentUserId, picturePath);
    }

    public void updatePassword(String currentPassword, String newPassword, 
                             Runnable onSuccess, Runnable onFailure) {
        userRepository.updatePassword(
            currentUserId,
            currentPassword,
            newPassword,
            onSuccess,
            onFailure
        );
    }

    // Address operations
    public LiveData<List<Address>> getUserAddresses() {
        return addressRepository.getAddressesForUser(currentUserId);
    }

    public LiveData<Address> getDefaultAddress() {
        return addressRepository.getDefaultAddress(currentUserId);
    }

    public void addAddress(Address address) {
        address.setUserId(currentUserId);
        addressRepository.insert(address);
    }

    public void updateAddress(Address address) {
        addressRepository.update(address);
    }

    public void deleteAddress(Address address) {
        addressRepository.delete(address);
    }

    public void setDefaultAddress(int addressId) {
        addressRepository.setDefaultAddress(currentUserId, addressId);
    }

    // Order operations
    public LiveData<List<Order>> getUserOrders() {
        return orderRepository.getOrdersForUser(currentUserId);
    }

    public LiveData<Order> getOrderDetails(int orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public LiveData<Double> getTotalSpent() {
        return orderRepository.getTotalSpentByUser(currentUserId);
    }

    // Error handling
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    // Clean up
    @Override
    protected void onCleared() {
        super.onCleared();
        // Add any cleanup code if needed
    }
}