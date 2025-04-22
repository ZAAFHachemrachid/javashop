package com.example.java_shop.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.repositories.CategoryRepository;
import com.example.java_shop.data.repositories.ProductRepository;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final LiveData<List<Product>> featuredProducts;
    private final LiveData<List<Product>> specialOffers;
    private final LiveData<List<Category>> categories;
    private final MutableLiveData<NavigationCommand> navigationCommand;

    public HomeViewModel(Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        categoryRepository = new CategoryRepository(application);
        featuredProducts = productRepository.getFeaturedProducts();
        specialOffers = productRepository.getSpecialOffers();
        categories = categoryRepository.getAllCategories();
        navigationCommand = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getFeaturedProducts() {
        return featuredProducts;
    }

    public LiveData<List<Product>> getSpecialOffers() {
        return specialOffers;
    }
    
    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    public void navigateToProduct(String productId) {
        NavigationCommand command = new NavigationCommand(
            "action_homeFragment_to_productDetailsFragment",
            productId
        );
        navigationCommand.setValue(command);
    }

    public void resetNavigation() {
        navigationCommand.setValue(null);
    }

    public static class NavigationCommand {
        private final String actionId;
        private final String arg;

        public NavigationCommand(String actionId, String arg) {
            this.actionId = actionId;
            this.arg = arg;
        }

        public String getActionId() {
            return actionId;
        }

        public String getArg() {
            return arg;
        }
    }
}