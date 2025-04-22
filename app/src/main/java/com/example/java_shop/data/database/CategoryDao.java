package com.example.java_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.java_shop.data.models.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    // Update operations
    @Update
    void update(Category category);

    // Delete operations
    @Delete
    void delete(Category category);

    @Query("DELETE FROM categories")
    void deleteAll();

    // Basic queries
    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    LiveData<Category> getCategory(String categoryId);

    // Hierarchy queries
    @Query("SELECT * FROM categories WHERE parentCategoryId IS NULL")
    LiveData<List<Category>> getTopLevelCategories();

    @Query("SELECT * FROM categories WHERE parentCategoryId = :parentId")
    LiveData<List<Category>> getSubcategories(String parentId);

    // Active categories
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder")
    LiveData<List<Category>> getActiveCategories();

    @Query("SELECT * FROM categories WHERE isActive = 1 AND parentCategoryId IS NULL ORDER BY displayOrder")
    LiveData<List<Category>> getActiveTopLevelCategories();

    @Query("SELECT * FROM categories WHERE isActive = 1 AND parentCategoryId = :parentId ORDER BY displayOrder")
    LiveData<List<Category>> getActiveSubcategories(String parentId);

    // Category path
    @Query("WITH RECURSIVE category_path AS (" +
           "SELECT id, name, description, iconUrl, displayOrder, isActive, parentCategoryId, 1 as level " +
           "FROM categories " +
           "WHERE id = :categoryId " +
           "UNION ALL " +
           "SELECT c.id, c.name, c.description, c.iconUrl, c.displayOrder, c.isActive, c.parentCategoryId, cp.level + 1 " +
           "FROM categories c " +
           "INNER JOIN category_path cp ON c.id = cp.parentCategoryId" +
           ") " +
           "SELECT id, name, description, iconUrl, displayOrder, isActive, parentCategoryId " +
           "FROM category_path ORDER BY level DESC")
    LiveData<List<Category>> getCategoryPath(String categoryId);

    // Category counts
    @Query("SELECT COUNT(*) FROM categories WHERE parentCategoryId = :categoryId")
    LiveData<Integer> getSubcategoryCount(String categoryId);

    // Update display order
    @Query("UPDATE categories SET displayOrder = :newOrder WHERE id = :categoryId")
    void updateDisplayOrder(String categoryId, int newOrder);
}