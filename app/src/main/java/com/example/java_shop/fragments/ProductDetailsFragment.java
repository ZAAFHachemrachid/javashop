package com.example.java_shop.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.example.java_shop.R;
import com.example.java_shop.utils.ImageLoader;
import com.example.java_shop.viewmodels.ProductDetailsViewModel;
import com.example.java_shop.data.models.Product;

public class ProductDetailsFragment extends Fragment {

    private ProductDetailsViewModel viewModel;
    private ImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView stockStatus;
    private RatingBar ratingBar;
    private TextView ratingCount;
    private TextView productDescription;
    private TextView productSpecifications;
    private ExtendedFloatingActionButton addToCartButton;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
        
        // Get product ID from navigation args
        String productId = ProductDetailsFragmentArgs.fromBundle(getArguments()).getProductId();
        viewModel.setProductId(productId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setupToolbar();
        setupObservers();
    }

    private void findViews(View view) {
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);
        productPrice = view.findViewById(R.id.product_price);
        stockStatus = view.findViewById(R.id.stock_status);
        ratingBar = view.findViewById(R.id.rating_bar);
        ratingCount = view.findViewById(R.id.rating_count);
        productDescription = view.findViewById(R.id.product_description);
        productSpecifications = view.findViewById(R.id.product_specifications);
        addToCartButton = view.findViewById(R.id.add_to_cart_button);
        toolbar = view.findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupObservers() {
        viewModel.getProduct().observe(getViewLifecycleOwner(), this::updateUI);
        
        viewModel.getAddToCartResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    showSnackbar("Added to cart successfully");
                } else {
                    showSnackbar("Failed to add to cart");
                }
                viewModel.resetAddToCartResult();
            }
        });
    }

    private void updateUI(Product product) {
        if (product == null) return;

        // Set product details
        productName.setText(product.getName());
        productPrice.setText(String.format("$%.2f", product.getPrice()));
        
        // Set stock status
        boolean inStock = product.getStockQuantity() > 0;
        stockStatus.setText(inStock ? "In Stock" : "Out of Stock");
        stockStatus.setTextColor(inStock ? 
            Color.parseColor("#4CAF50") : // Green
            Color.parseColor("#F44336")); // Red
        
        // Set rating
        ratingBar.setRating((float) product.getRating());
        ratingCount.setText(String.format("(%d reviews)", product.getReviewCount()));
        
        // Set description and specifications
        productDescription.setText(product.getDescription());
        productSpecifications.setText(product.getSpecifications());
        
        // Load product image
        ImageLoader.loadImage(productImage, product.getImageUrl());
        
        // Setup add to cart button
        addToCartButton.setEnabled(inStock);
        addToCartButton.setAlpha(inStock ? 1.0f : 0.5f);
        addToCartButton.setOnClickListener(v -> viewModel.addToCart(product));
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}