package com.example.java_shop.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(
    tableName = "products",
    foreignKeys = @ForeignKey(
        entity = Category.class,
        parentColumns = "id",
        childColumns = "categoryId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("categoryId")}
)
public class Product {
    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int stockQuantity;
    private String categoryId;
    private boolean isFeatured;
    private double discountPercentage;
    private String specifications;
    private double rating;
    private int reviewCount;
    private long lastModified;
    private long offerValidUntilTimestamp; // Timestamp for offer validity
    private double originalPrice; // Original price before discount

    // No-args constructor for Room
    public Product() {
        this.lastModified = System.currentTimeMillis();
    }

    @Ignore
    public Product(@NonNull String id, String name, String description, String imageUrl,
                  double price, int stockQuantity, String categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.isFeatured = false;
        this.discountPercentage = 0.0;
        this.rating = 0.0;
        this.reviewCount = 0;
        this.lastModified = System.currentTimeMillis();
        this.originalPrice = 0.0;
        this.offerValidUntilTimestamp = 0;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public double getDiscountedPrice() {
        if (discountPercentage <= 0) {
            return price;
        }
        return price * (1 - (discountPercentage / 100.0));
    }
    
    public double getOriginalPrice() {
        if (originalPrice > 0) {
            return originalPrice;
        }
        // Calculate original price if not explicitly set but has discount
        if (discountPercentage > 0) {
            return price / (1 - (discountPercentage / 100.0));
        }
        return price;
    }
    
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public long getOfferValidUntilTimestamp() {
        return offerValidUntilTimestamp;
    }
    
    public void setOfferValidUntilTimestamp(long offerValidUntilTimestamp) {
        this.offerValidUntilTimestamp = offerValidUntilTimestamp;
    }
    
    public String getOfferValidUntil() {
        if (offerValidUntilTimestamp <= 0) {
            // Default to 30 days from now if not set
            long defaultValidPeriod = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            return dateFormat.format(new Date(defaultValidPeriod));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        return dateFormat.format(new Date(offerValidUntilTimestamp));
    }
}