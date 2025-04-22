package com.example.java_shop.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.CartItem;

@Database(
    entities = {Product.class, Category.class, CartItem.class},
    version = 1,
    exportSchema = false
)
public abstract class ComputerShopDatabase extends RoomDatabase {
    
    // DAOs
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract CartDao cartDao();

    // Singleton instance
    private static volatile ComputerShopDatabase INSTANCE;

    // Singleton pattern with double-checked locking
    public static ComputerShopDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ComputerShopDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        ComputerShopDatabase.class,
                        "computer_shop_db"
                    )
                    .fallbackToDestructiveMigration() // For development only
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}