package com.example.java_shop.viewmodels;

import android.app.Application;
import android.os.Bundle;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.repositories.CategoryRepository;
import com.example.java_shop.data.repositories.ProductRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryDetailsViewModel extends AndroidViewModel {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // LiveData for UI components
    private final LiveData<Category> category;
    private final LiveData<List<Category>> subcategories;
    private final MediatorLiveData<List<Product>> filteredProducts;
    private final MutableLiveData<NavigationCommand> navigationCommand;
    
    // Filter state
    private final MutableLiveData<Boolean> showInStockOnly;
    private final MutableLiveData<SortOption> currentSortOption;
    private int currentCategoryId;

    public enum SortOption {
        NAME_ASC,
        NAME_DESC,
        PRICE_LOW_HIGH,
        PRICE_HIGH_LOW,
        RATING
    }

    public CategoryDetailsViewModel(Application application) {
        super(application);
        
        productRepository = new ProductRepository(application);
        categoryRepository = new CategoryRepository(application);
        
        navigationCommand = new MutableLiveData<>();
        showInStockOnly = new MutableLiveData<>(false);
        currentSortOption = new MutableLiveData<>(SortOption.RATING);
        filteredProducts = new MediatorLiveData<>();
        
        // Initialize with empty data until category is set
        category = new MutableLiveData<>();
        subcategories = new MutableLiveData<>();
    }

    public void setCategoryId(int categoryId) {
        currentCategoryId = categoryId;
        
        // Update LiveData sources
        MutableLiveData<Category> categoryData = (MutableLiveData<Category>) category;
        categoryData.setValue(null);
        
        String categoryIdStr = String.valueOf(categoryId);
        LiveData<Category> newCategory = categoryRepository.getCategory(categoryIdStr);
        LiveData<List<Category>> newSubcategories = categoryRepository.getSubcategories(categoryIdStr);
        LiveData<List<Product>> categoryProducts = productRepository.getProductsByCategory(categoryIdStr);
        
        // Update category and subcategories
        categoryData.setValue(newCategory.getValue());
        ((MutableLiveData<List<Category>>) subcategories).setValue(newSubcategories.getValue());
        
        // Set up filtered products
        filteredProducts.addSource(categoryProducts, products -> 
            applyFiltersAndSort(products));
        
        filteredProducts.addSource(showInStockOnly, inStockOnly -> 
            applyFiltersAndSort(productRepository.getProductsByCategory(String.valueOf(categoryId)).getValue()));
        
        filteredProducts.addSource(currentSortOption, sortOption ->
            applyFiltersAndSort(productRepository.getProductsByCategory(String.valueOf(categoryId)).getValue()));
    }

    private void applyFiltersAndSort(List<Product> products) {
        if (products == null) return;
        
        List<Product> filtered = new ArrayList<>();
        
        // Apply filters
        for (Product product : products) {
            if (!showInStockOnly.getValue() || product.getStockQuantity() > 0) {
                filtered.add(product);
            }
        }

        // Apply sorting
        Collections.sort(filtered, (p1, p2) -> {
            SortOption sortOption = currentSortOption.getValue();
            if (sortOption == null) {
                sortOption = SortOption.RATING;
            }

            int result;
            switch (sortOption) {
                case NAME_ASC:
                    result = p1.getName().compareTo(p2.getName());
                    break;
                case NAME_DESC:
                    result = p2.getName().compareTo(p1.getName());
                    break;
                case PRICE_LOW_HIGH:
                    result = Double.compare(p1.getPrice(), p2.getPrice());
                    break;
                case PRICE_HIGH_LOW:
                    result = Double.compare(p2.getPrice(), p1.getPrice());
                    break;
                case RATING:
                default:
                    result = Double.compare(p2.getRating(), p1.getRating());
                    break;
            }
            return result;
        });
        
        filteredProducts.setValue(filtered);
    }

    // Getters
    public LiveData<Category> getCategory() {
        return category;
    }

    public LiveData<List<Category>> getSubcategories() {
        return subcategories;
    }

    public LiveData<List<Product>> getProducts() {
        return filteredProducts;
    }

    public LiveData<Boolean> getShowInStockOnly() {
        return showInStockOnly;
    }

    public LiveData<SortOption> getCurrentSortOption() {
        return currentSortOption;
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    // Actions
    public void setShowInStockOnly(boolean show) {
        showInStockOnly.setValue(show);
    }

    public void setSortOption(SortOption option) {
        currentSortOption.setValue(option);
    }

    public void navigateToProduct(Product product) {
        Bundle args = new Bundle();
        args.putInt("productId", Integer.parseInt(product.getId()));
        navigationCommand.setValue(
            new NavigationCommand(
                R.id.action_categoryDetailsFragment_to_productDetailsFragment,
                args
            )
        );
    }

    public void navigateToSubcategory(Category subcategory) {
        Bundle args = new Bundle();
        args.putInt("categoryId", Integer.parseInt(subcategory.getId()));
        navigationCommand.setValue(
            new NavigationCommand(
                R.id.action_categoryDetailsFragment_self,
                args
            )
        );
    }

    public void resetNavigation() {
        navigationCommand.setValue(null);
    }

    // Navigation command class
    public static class NavigationCommand {
        private final int actionId;
        private final Bundle args;

        public NavigationCommand(int actionId, Bundle args) {
            this.actionId = actionId;
            this.args = args;
        }

        public int getActionId() {
            return actionId;
        }

        public Bundle getArgs() {
            return args;
        }
    }
}