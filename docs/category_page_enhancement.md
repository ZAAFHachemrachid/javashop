# Category Page Enhancement Implementation Plan

## Overview
This document outlines the plan for enhancing the Categories page with:
1. Search bar functionality
2. Horizontal product lists under each category
3. Maintaining existing category navigation

## Architecture Changes

### 1. UI Layer Changes

#### Layout Modifications (fragment_categories.xml)
```xml
- Add SearchView below toolbar
- Modify RecyclerView to use vertical layout
- Create new category item layout with products
```

#### New Layouts
- **item_category_with_products.xml**
  - Category header with icon and name
  - Horizontal RecyclerView for products
  - "View All" button

### 2. Data Layer Changes

#### ProductRepository
```java
public class ProductRepository {
    // Existing methods...
    
    // New methods
    public LiveData<List<Product>> searchProductsByCategory(String categoryId, String query);
    public LiveData<List<Product>> searchAllProducts(String query);
    public LiveData<Map<String, List<Product>>> getProductsByCategories(List<String> categoryIds);
}
```

#### ProductDao
```java
@Dao
public interface ProductDao {
    // New queries
    @Query("SELECT * FROM products WHERE category_id = :categoryId AND name LIKE '%' || :query || '%'")
    LiveData<List<Product>> searchProductsByCategory(String categoryId, String query);
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Product>> searchAllProducts(String query);
}
```

### 3. ViewModel Changes

#### CategoriesViewModel
```java
public class CategoriesViewModel extends ViewModel {
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Category>> categories;
    private final LiveData<Map<String, List<Product>>> productsByCategory;
    
    // Search handling
    public void updateSearchQuery(String query) {
        searchQuery.setValue(query);
    }
    
    // Transform categories and products based on search
    private LiveData<List<CategoryWithProducts>> getCategoriesWithProducts() {
        return Transformations.map(/* ... */);
    }
}
```

### 4. Adapter Changes

#### CategoryWithProductsAdapter
```java
public class CategoryWithProductsAdapter extends ListAdapter<CategoryWithProducts, CategoryWithProductsViewHolder> {
    private final OnCategoryClickListener categoryClickListener;
    private final OnProductClickListener productClickListener;
    
    // ViewHolder implementation
    // Click handling for both category and products
    // Horizontal product list handling
}
```

## Implementation Steps

1. **UI Implementation**
   - Create new layout files
   - Modify fragment_categories.xml
   - Implement adapters and view holders

2. **Data Layer**
   - Add new queries to ProductDao
   - Enhance ProductRepository with search methods
   - Create CategoryWithProducts data class

3. **ViewModel**
   - Add search query handling
   - Implement product filtering logic
   - Set up data transformations

4. **Integration**
   - Connect UI with ViewModel
   - Set up search listeners
   - Implement click handlers

## Testing Plan

1. **Unit Tests**
   - Test search functionality in ViewModel
   - Test database queries
   - Test data transformations

2. **UI Tests**
   - Verify search behavior
   - Test category and product navigation
   - Verify scroll performance

3. **Integration Tests**
   - End-to-end testing of search
   - Category-product relationship testing
   - Navigation flow testing