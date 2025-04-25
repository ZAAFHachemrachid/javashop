package com.example.java_shop.data.models;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class CategoryWithProducts {
    @Embedded
    private Category category;

    @Relation(
        parentColumn = "id",
        entityColumn = "category_id"
    )
    private List<Product> products;

    public CategoryWithProducts(Category category, List<Product> products) {
        this.category = category;
        this.products = products;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}