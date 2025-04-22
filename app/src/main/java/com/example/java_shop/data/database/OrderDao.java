package com.example.java_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.models.OrderItem;
import java.util.Date;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderItems(List<OrderItem> orderItems);

    @Transaction
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    LiveData<List<Order>> getOrdersForUser(int userId);

    @Query("SELECT * FROM orders WHERE id = :orderId")
    LiveData<Order> getOrderById(int orderId);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    LiveData<List<OrderItem>> getOrderItemsForOrder(int orderId);

    @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
    void updateOrderStatus(int orderId, String newStatus);

    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId")
    int getOrderCountForUser(int userId);

    @Query("SELECT * FROM orders WHERE userId = :userId AND orderDate BETWEEN :startDate AND :endDate ORDER BY orderDate DESC")
    LiveData<List<Order>> getOrdersInDateRange(int userId, Date startDate, Date endDate);

    @Query("SELECT SUM(totalAmount) FROM orders WHERE userId = :userId")
    LiveData<Double> getTotalSpentByUser(int userId);

    @Transaction
    default void createOrderWithItems(Order order, List<OrderItem> items) {
        long orderId = insert(order);
        for (OrderItem item : items) {
            item.setOrderId((int) orderId);
        }
        insertOrderItems(items);
    }

    @Query("DELETE FROM orders WHERE userId = :userId")
    void deleteAllOrdersForUser(int userId);
}