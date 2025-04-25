package com.example.java_shop.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.CosShopDatabase;
import com.example.java_shop.data.database.AddressDao;
import com.example.java_shop.data.models.Address;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressRepository {
    private final AddressDao addressDao;
    private final ExecutorService executorService;

    public AddressRepository(Application application) {
        CosShopDatabase database = CosShopDatabase.getDatabase(application);
        addressDao = database.addressDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Address address) {
        executorService.execute(() -> addressDao.insert(address));
    }

    public void update(Address address) {
        executorService.execute(() -> addressDao.update(address));
    }

    public void delete(Address address) {
        executorService.execute(() -> addressDao.delete(address));
    }

    public LiveData<Address> getAddressById(int addressId) {
        return addressDao.getAddressById(addressId);
    }

    public LiveData<List<Address>> getAddressesForUser(int userId) {
        return addressDao.getAddressesForUser(userId);
    }

    public LiveData<Address> getDefaultAddress(int userId) {
        return addressDao.getDefaultAddress(userId);
    }

    public void setDefaultAddress(int userId, int addressId) {
        executorService.execute(() -> addressDao.setDefaultAddress(userId, addressId));
    }

    public void deleteAllAddressesForUser(int userId) {
        executorService.execute(() -> addressDao.deleteAllAddressesForUser(userId));
    }

    public void getAddressCountForUser(int userId, CountCallback callback) {
        executorService.execute(() -> {
            int count = addressDao.getAddressCountForUser(userId);
            callback.onResult(count);
        });
    }

    public interface CountCallback {
        void onResult(int count);
    }
}