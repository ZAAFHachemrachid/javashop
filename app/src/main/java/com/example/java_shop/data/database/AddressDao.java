package com.example.java_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.java_shop.data.models.Address;
import java.util.List;

@Dao
public interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Address address);

    @Update
    void update(Address address);

    @Delete
    void delete(Address address);

    @Query("SELECT * FROM addresses WHERE id = :addressId")
    LiveData<Address> getAddressById(int addressId);

    @Query("SELECT * FROM addresses WHERE userId = :userId")
    LiveData<List<Address>> getAddressesForUser(int userId);

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<Address> getDefaultAddress(int userId);

    @Transaction
    @Query("UPDATE addresses SET isDefault = CASE WHEN id = :addressId THEN 1 ELSE 0 END WHERE userId = :userId")
    void setDefaultAddress(int userId, int addressId);

    @Query("DELETE FROM addresses WHERE userId = :userId")
    void deleteAllAddressesForUser(int userId);

    @Query("SELECT COUNT(*) FROM addresses WHERE userId = :userId")
    int getAddressCountForUser(int userId);
}