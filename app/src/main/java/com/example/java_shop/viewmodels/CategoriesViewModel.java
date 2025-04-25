package com.example.java_shop.viewmodels;

import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.CategoryWithProducts;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.repositories.CategoryRepository;
import com.example.java_shop.data.repositories.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriesViewModel extends AndroidViewModel {
    // Keep track of product LiveData sources
    private final Map<String, LiveData<List<Product>>> productSources = new HashMap<>();
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final LiveData<List<Category>> categories;
    private final MutableLiveData<NavigationCommand> navigationCommand;
    private final MutableLiveData<String> searchQuery;
    private final MediatorLiveData<List<CategoryWithProducts>> categoriesWithProducts;
    
    private static final int PRODUCTS_PER_CATEGORY = 4;

    public CategoriesViewModel(Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        productRepository = new ProductRepository(application);
        categories = categoryRepository.getActiveCategories();
        navigationCommand = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>("");
        categoriesWithProducts = new MediatorLiveData<>();

        // Observe search query changes
        categoriesWithProducts.addSource(searchQuery, query -> {
            List<Category> currentCategories = categories.getValue();
            if (currentCategories != null) {
                refreshCategoriesWithProducts(currentCategories, query);
            }
        });

        // Observe categories changes
        categoriesWithProducts.addSource(categories, categoriesList -> {
            if (categoriesList != null) {
                refreshCategoriesWithProducts(categoriesList, searchQuery.getValue());
            }
        });
    }

    private void refreshCategoriesWithProducts(List<Category> categoriesList, String query) {
        List<CategoryWithProducts> result = new ArrayList<>();
        for (Category category : categoriesList) {
            // Start with empty product list
            result.add(new CategoryWithProducts(category, new ArrayList<>()));
            
            // Get products for category
            @NonNull String categoryId = category.getId();
            @NonNull String searchQuery = (query != null) ? query : "";
            LiveData<List<Product>> productLiveData = productRepository.getProductsByCategoryPreview(
                categoryId,
                searchQuery,
                PRODUCTS_PER_CATEGORY
            );
            
            // Keep track of sources to remove old ones
            String sourceKey = category.getId() + "_" + (query != null ? query : "");
            if (productSources.containsKey(sourceKey)) {
                categoriesWithProducts.removeSource(productSources.get(sourceKey));
            }
            productSources.put(sourceKey, productLiveData);
            
            final Category finalCategory = category;
            categoriesWithProducts.addSource(productLiveData, products -> {
                if (products != null) {
                    List<CategoryWithProducts> currentList = categoriesWithProducts.getValue();
                    if (currentList != null) {
                        List<CategoryWithProducts> newList = new ArrayList<>();
                        for (CategoryWithProducts cwp : currentList) {
                            if (cwp.getCategory().getId().equals(finalCategory.getId())) {
                                newList.add(new CategoryWithProducts(finalCategory, products));
                            } else {
                                newList.add(cwp);
                            }
                        }
                        categoriesWithProducts.setValue(newList);
                    }
                }
            });
        }
        categoriesWithProducts.setValue(result);
    }

    public LiveData<List<CategoryWithProducts>> getCategoriesWithProducts() {
        return categoriesWithProducts;
    }
    
    public void updateSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    public void navigateToCategory(Category category) {
        if (category != null) {
            Bundle args = new Bundle();
            args.putString("categoryId", category.getId());
            navigationCommand.setValue(
                new NavigationCommand(
                    R.id.action_categoriesFragment_to_categoryDetailsFragment,
                    args
                )
            );
        }
    }

    public void resetNavigation() {
        navigationCommand.setValue(null);
    }

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

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove all sources
        for (LiveData<List<Product>> source : productSources.values()) {
            categoriesWithProducts.removeSource(source);
        }
        productSources.clear();
        categoryRepository.cleanup();
        productRepository.cleanup();
    }
}