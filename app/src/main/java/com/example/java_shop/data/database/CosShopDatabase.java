package com.example.java_shop.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.CartItem;
import com.example.java_shop.data.models.User;
import com.example.java_shop.data.models.Address;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.models.OrderItem;
import com.example.java_shop.utils.Converters;

@Database(
    entities = {
        Product.class,
        Category.class,
        CartItem.class,
        User.class,
        Address.class,
        Order.class,
        OrderItem.class
    },
    version = 8, // Increment version after schema changes:
    // v1: Initial schema
    // v2: Added new fields to Product
    // v3: Updated Category schema
    // v4: Added no-args constructor to Product
    // v5: Added offerValidUntilTimestamp and originalPrice fields to Product
    // v6: Added User, Address, Order, and OrderItem entities
    // v7: Added foreign key and index to Category schema
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class CosShopDatabase extends RoomDatabase {
    
    // DAOs
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract CartDao cartDao();
    public abstract UserDao userDao();
    public abstract AddressDao addressDao();
    public abstract OrderDao orderDao();

    // Singleton instance
    private static volatile CosShopDatabase INSTANCE;

    // Singleton pattern with double-checked locking
    public static CosShopDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CosShopDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        CosShopDatabase.class,
                        "cosmetics_shop_db"
                    )
                    .fallbackToDestructiveMigration() // For development only
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}