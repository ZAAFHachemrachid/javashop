package com.example.java_shop.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.data.models.Address;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.models.User;
import com.example.java_shop.data.repositories.AddressRepository;
import com.example.java_shop.data.repositories.OrderRepository;
import com.example.java_shop.data.repositories.UserRepository;
import com.example.java_shop.utils.SessionManager;
import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final SessionManager sessionManager;
    
    // View state management
    public enum ViewState {
        IDLE, LOADING, SUCCESS, ERROR
    }
    
    private final MutableLiveData<ViewState> viewState = new MutableLiveData<>(ViewState.IDLE);
    
    // Observer references for cleanup
    private androidx.lifecycle.Observer<User> userObserver;
    private androidx.lifecycle.Observer<List<Order>> ordersObserver;
    private androidx.lifecycle.Observer<List<Address>> addressesObserver;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<List<Order>> userOrders = new MutableLiveData<>();
    private MutableLiveData<List<Address>> userAddresses = new MutableLiveData<>();

    public AccountViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        orderRepository = new OrderRepository(application);
        addressRepository = new AddressRepository(application);
        sessionManager = SessionManager.getInstance(application);
    }

    public void loadUserData(int userId) {
        // Clean up any existing observers
        cleanupObservers();
        
        // Create and store new observers
        userObserver = user -> currentUser.setValue(user);
        ordersObserver = orders -> userOrders.setValue(orders);
        addressesObserver = addresses -> userAddresses.setValue(addresses);
        
        // Set up new observations
        userRepository.getUserById(userId).observeForever(userObserver);
        orderRepository.getOrdersForUser(userId).observeForever(ordersObserver);
        addressRepository.getAddressesForUser(userId).observeForever(addressesObserver);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<Order>> getUserOrders() {
        return userOrders;
    }
    
    private void cleanupObservers() {
        if (userObserver != null) {
            userRepository.getUserById(sessionManager.getUserId()).removeObserver(userObserver);
        }
        if (ordersObserver != null) {
            orderRepository.getOrdersForUser(sessionManager.getUserId()).removeObserver(ordersObserver);
        }
        if (addressesObserver != null) {
            addressRepository.getAddressesForUser(sessionManager.getUserId()).removeObserver(addressesObserver);
        }
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        cleanupObservers();
    }

    public LiveData<List<Address>> getUserAddresses() {
        return userAddresses;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public void updateProfile(String name, String email, String phone) {
        if (!validateProfileData(name, email, phone)) {
            errorMessage.setValue("Invalid profile data");
            viewState.setValue(ViewState.ERROR);
            return;
        }

        viewState.setValue(ViewState.LOADING);
        User user = currentUser.getValue();
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            userRepository.update(user,
                () -> {
                    sessionManager.updateUserEmail(email);
                    viewState.setValue(ViewState.SUCCESS);
                },
                error -> {
                    errorMessage.setValue(error.getMessage());
                    viewState.setValue(ViewState.ERROR);
                }
            );
        } else {
            errorMessage.setValue("User data not available");
            viewState.setValue(ViewState.ERROR);
        }
    }

    public void updateProfilePicture(String pictureUri) {
        if (pictureUri == null || pictureUri.isEmpty()) {
            errorMessage.setValue("Invalid profile picture");
            viewState.setValue(ViewState.ERROR);
            return;
        }

        viewState.setValue(ViewState.LOADING);
        User user = currentUser.getValue();
        if (user != null) {
            user.setProfilePicture(pictureUri);
            userRepository.update(user,
                () -> viewState.setValue(ViewState.SUCCESS),
                error -> {
                    errorMessage.setValue(error.getMessage());
                    viewState.setValue(ViewState.ERROR);
                }
            );
        } else {
            errorMessage.setValue("User data not available");
            viewState.setValue(ViewState.ERROR);
        }
    }

    private boolean validateProfileData(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public void deleteAddress(Address address) {
        addressRepository.delete(address);
    }

    public void setDefaultAddress(int addressId) {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            addressRepository.setDefaultAddress(userId, addressId);
        } else {
            errorMessage.setValue("User not logged in");
        }
    }
}