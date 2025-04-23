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
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private LiveData<User> currentUser;
    private LiveData<List<Order>> userOrders;
    private LiveData<List<Address>> userAddresses;

    public AccountViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        orderRepository = new OrderRepository(application);
        addressRepository = new AddressRepository(application);
        sessionManager = SessionManager.getInstance(application);
    }

    public void loadUserData(int userId) {
        currentUser = userRepository.getUserById(userId);
        userOrders = orderRepository.getOrdersForUser(userId);
        userAddresses = addressRepository.getAddressesForUser(userId);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<Order>> getUserOrders() {
        return userOrders;
    }

    public LiveData<List<Address>> getUserAddresses() {
        return userAddresses;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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