package com.example.java_shop.data.models;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CartItemWithProduct {
    @Embedded
    private CartItem cartItem;

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    private Product product;

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Helper methods
    public double getTotalPrice() {
        return cartItem.getQuantity() * product.getPrice();
    }

    public String getProductId() {
        return product.getId();
    }

    public int getQuantity() {
        return cartItem.getQuantity();
    }

    public long getId() {
        return cartItem.getId();
    }

    public double getPrice() {
        return product.getPrice();
    }
}