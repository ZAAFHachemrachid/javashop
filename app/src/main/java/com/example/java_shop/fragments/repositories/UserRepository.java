package com.example.java_shop.data.repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.ComputerShopDatabase;
import com.example.java_shop.data.database.UserDao;
import com.example.java_shop.data.models.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;
    private final Handler handler;

    public UserRepository(Application application) {
        ComputerShopDatabase database = ComputerShopDatabase.getDatabase(application);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public void insert(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public void update(User user, Runnable onSuccess, OnFailureListener onFailure) {
        executorService.execute(() -> {
            try {
                userDao.update(user);
                handler.post(onSuccess);
            } catch (Exception e) {
                handler.post(() -> onFailure.onFailure(e));
            }
        });
    }

    public interface OnFailureListener {
        void onFailure(Exception e);
    }

    public void delete(User user) {
        executorService.execute(() -> userDao.delete(user));
    }

    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public LiveData<User> login(String email, String passwordHash) {
        return userDao.login(email, passwordHash);
    }

    public void updateProfilePicture(int userId, String profilePicturePath, Runnable onSuccess, OnFailureListener onFailure) {
        executorService.execute(() -> {
            try {
                userDao.updateProfilePicture(userId, profilePicturePath);
                handler.post(onSuccess);
            } catch (Exception e) {
                handler.post(() -> onFailure.onFailure(e));
            }
        });
    }

    public void updatePassword(int userId, String currentPasswordHash, String newPasswordHash, 
                             Runnable onSuccess, Runnable onFailure) {
        executorService.execute(() -> {
            int result = userDao.updatePassword(userId, currentPasswordHash, newPasswordHash);
            if (result > 0) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });
    }

    public void checkEmailExists(String email, EmailCheckCallback callback) {
        executorService.execute(() -> {
            int count = userDao.checkEmailExists(email);
            callback.onResult(count > 0);
        });
    }

    public interface EmailCheckCallback {
        void onResult(boolean exists);
    }
}