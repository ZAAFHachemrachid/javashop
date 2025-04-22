package com.example.java_shop.viewmodels;

import android.app.Application;
import android.os.Bundle;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.repositories.CartRepository;
import com.example.java_shop.data.repositories.CategoryRepository;
import com.example.java_shop.data.repositories.ProductRepository;
import com.example.java_shop.adapters.SpecialOffersAdapter.SpecialOffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    
    // LiveData for UI components
    private final LiveData<List<Category>> activeCategories;
    private final LiveData<List<Product>> featuredProducts;
    private final LiveData<List<Product>> popularProducts;
    private final MutableLiveData<List<SpecialOffer>> specialOffers;
    private final LiveData<Integer> cartItemCount;
    private final MutableLiveData<NavigationCommand> navigationCommand;
    
    // Constants
    private static final int FEATURED_PRODUCTS_LIMIT = 5;
    private static final int POPULAR_PRODUCTS_LIMIT = 6;

    public HomeViewModel(Application application) {
        super(application);
        
        // Initialize repositories
        productRepository = new ProductRepository(application);
        categoryRepository = new CategoryRepository(application);
        cartRepository = new CartRepository(application);
        
        // Initialize LiveData
        activeCategories = categoryRepository.getActiveCategories();
        featuredProducts = productRepository.getFeaturedProducts(FEATURED_PRODUCTS_LIMIT);
        popularProducts = productRepository.getTopRatedProducts(POPULAR_PRODUCTS_LIMIT);
        specialOffers = new MutableLiveData<>(new ArrayList<>());
        cartItemCount = cartRepository.getCartItemCount();
        navigationCommand = new MutableLiveData<>();
        
        // Load initial data
        loadSpecialOffers();
    }

    // Navigation Command class
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

    // Getters for LiveData
    public LiveData<List<Category>> getActiveCategories() {
        return activeCategories;
    }

    public LiveData<List<Product>> getFeaturedProducts() {
        return featuredProducts;
    }

    public LiveData<List<Product>> getPopularProducts() {
        return popularProducts;
    }

    public LiveData<List<SpecialOffer>> getSpecialOffers() {
        return specialOffers;
    }

    public LiveData<Integer> getCartItemCount() {
        return cartItemCount;
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    // Business logic methods
    public void addToCart(Product product) {
        cartRepository.addToCart(product.getId(), 1, product.getPrice());
    }

    public void navigateToCategory(Category category) {
        if (category != null) {
            Bundle args = new Bundle();
            args.putString("categoryId", category.getId());
            navigationCommand.setValue(
                new NavigationCommand(
                    R.id.action_homeFragment_to_categoryDetailsFragment,
                    args
                )
            );
        }
    }

    public void navigateToProduct(Product product) {
        if (product != null) {
            Bundle args = new Bundle();
            args.putString("productId", product.getId());
            navigationCommand.setValue(
                new NavigationCommand(
                    R.id.action_homeFragment_to_productDetailsFragment,
                    args
                )
            );
        }
    }

    public void navigateToSearch() {
        // TODO: Implement when search feature is added
    }

    public void resetNavigation() {
        navigationCommand.setValue(null);
    }

    // Special offers handling
    private void loadSpecialOffers() {
        // TODO: In a real app, this would come from a backend service
        // For now, we'll create some mock special offers
        List<SpecialOffer> offers = new ArrayList<>();
        
        // Get some products to create offers
        List<Product> products = featuredProducts.getValue();
        if (products != null && !products.isEmpty()) {
            // Create expiration dates
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7); // 1 week from now
            Date nextWeek = calendar.getTime();
            
            calendar.add(Calendar.DAY_OF_MONTH, 7); // 2 weeks from now
            Date twoWeeks = calendar.getTime();
            
            // Create offers
            offers.add(new SpecialOffer(products.get(0), 25.0, nextWeek));
            if (products.size() > 1) {
                offers.add(new SpecialOffer(products.get(1), 30.0, twoWeeks));
            }
            if (products.size() > 2) {
                offers.add(new SpecialOffer(products.get(2), 20.0, nextWeek));
            }
        }
        
        specialOffers.setValue(offers);
    }

    // Cleanup
    @Override
    protected void onCleared() {
        super.onCleared();
        productRepository.cleanup();
        categoryRepository.cleanup();
        cartRepository.cleanup();
    }
}