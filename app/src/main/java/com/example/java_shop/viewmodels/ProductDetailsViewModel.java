package com.example.java_shop.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.data.repositories.CartRepository;
import com.example.java_shop.data.repositories.ProductRepository;

public class ProductDetailsViewModel extends AndroidViewModel {
    
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final MutableLiveData<Boolean> addToCartResult;
    private String currentProductId;

    public ProductDetailsViewModel(Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        cartRepository = new CartRepository(application);
        addToCartResult = new MutableLiveData<>();
    }

    public void setProductId(String productId) {
        currentProductId = productId;
    }

    public LiveData<Product> getProduct() {
        return productRepository.getProduct(currentProductId);
    }

    public void addToCart(Product product) {
        if (product != null) {
            cartRepository.addToCart(product.getId(), 1, product.getPrice());
            addToCartResult.setValue(true);
        } else {
            addToCartResult.setValue(false);
        }
    }

    public LiveData<Boolean> getAddToCartResult() {
        return addToCartResult;
    }

    public void resetAddToCartResult() {
        addToCartResult.setValue(null);
    }

    // Cart badge count
    public LiveData<Integer> getCartItemCount() {
        return cartRepository.getCartItemCount();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        productRepository.cleanup();
        cartRepository.cleanup();
    }
}