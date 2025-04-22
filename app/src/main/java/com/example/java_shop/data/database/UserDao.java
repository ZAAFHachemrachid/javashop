package com.example.java_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.java_shop.data.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    LiveData<User> login(String email, String passwordHash);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);

    @Query("UPDATE users SET profilePicture = :profilePicturePath WHERE id = :userId")
    void updateProfilePicture(int userId, String profilePicturePath);

    @Query("UPDATE users SET passwordHash = :newPasswordHash WHERE id = :userId AND passwordHash = :currentPasswordHash")
    int updatePassword(int userId, String currentPasswordHash, String newPasswordHash);
}