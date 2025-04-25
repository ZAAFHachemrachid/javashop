package com.example.java_shop.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.java_shop.data.database.CosShopDatabase;
import com.example.java_shop.data.database.OrderDao;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.models.OrderItem;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private final OrderDao orderDao;
    private final ExecutorService executorService;

    public OrderRepository(Application application) {
        CosShopDatabase database = CosShopDatabase.getDatabase(application);
        orderDao = database.orderDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface OrderCreationCallback {
        void onOrderCreated(int orderId);
    }

    public void createOrder(Order order, List<OrderItem> items, OrderCreationCallback callback) {
        executorService.execute(() -> {
            long orderId = orderDao.createOrderWithItems(order, items);
            callback.onOrderCreated((int) orderId);
        });
    }

    public LiveData<List<Order>> getOrdersForUser(int userId) {
        return orderDao.getOrdersForUser(userId);
    }

    public LiveData<Order> getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    public LiveData<List<OrderItem>> getOrderItemsForOrder(int orderId) {
        return orderDao.getOrderItemsForOrder(orderId);
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        executorService.execute(() -> orderDao.updateOrderStatus(orderId, newStatus));
    }

    public void getOrderCount(int userId, CountCallback callback) {
        executorService.execute(() -> {
            int count = orderDao.getOrderCountForUser(userId);
            callback.onResult(count);
        });
    }

    public LiveData<List<Order>> getOrdersInDateRange(int userId, Date startDate, Date endDate) {
        return orderDao.getOrdersInDateRange(userId, startDate, endDate);
    }

    public LiveData<Double> getTotalSpentByUser(int userId) {
        return orderDao.getTotalSpentByUser(userId);
    }

    public void deleteAllOrdersForUser(int userId) {
        executorService.execute(() -> orderDao.deleteAllOrdersForUser(userId));
    }

    public interface CountCallback {
        void onResult(int count);
    }
}