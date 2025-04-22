package com.example.java_shop.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(
    tableName = "cart_items",
    foreignKeys = @ForeignKey(
        entity = Product.class,
        parentColumns = "id",
        childColumns = "productId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("productId")}
)
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String productId;
    private int quantity;
    private double priceAtAddition;
    private long addedTimestamp;
    private long lastModifiedTimestamp;

    // Constructor
    public CartItem(@NonNull String productId, int quantity, double priceAtAddition) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtAddition = priceAtAddition;
        this.addedTimestamp = System.currentTimeMillis();
        this.lastModifiedTimestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.lastModifiedTimestamp = System.currentTimeMillis();
    }

    public double getPriceAtAddition() {
        return priceAtAddition;
    }

    public void setPriceAtAddition(double priceAtAddition) {
        this.priceAtAddition = priceAtAddition;
    }

    public long getAddedTimestamp() {
        return addedTimestamp;
    }

    public void setAddedTimestamp(long addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public long getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(long lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    // Helper method to calculate total price
    public double getTotalPrice() {
        return quantity * priceAtAddition;
    }
}