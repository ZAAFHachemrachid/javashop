package com.example.java_shop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.card.MaterialCardView;
import com.example.java_shop.R;
import com.example.java_shop.adapters.CategoryAdapter;
import com.example.java_shop.adapters.FeaturedProductAdapter;
import com.example.java_shop.adapters.ProductGridAdapter;
import com.example.java_shop.adapters.SpecialOffersAdapter;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements 
        CategoryAdapter.OnCategoryClickListener,
        FeaturedProductAdapter.OnProductClickListener,
        ProductGridAdapter.OnProductActionListener,
        SpecialOffersAdapter.OnOfferClickListener {

    private static final String TAG = "HomeFragment";
    
    // ViewModel
    private HomeViewModel viewModel;
    
    // Views
    private MaterialCardView searchBar;
    private ViewPager2 featuredProductsPager;
    private RecyclerView categoriesRecyclerView;
    private RecyclerView popularProductsRecyclerView;
    private RecyclerView specialOffersRecyclerView;
    
    // Adapters
    private CategoryAdapter categoryAdapter;
    private FeaturedProductAdapter featuredProductAdapter;
    private ProductGridAdapter popularProductsAdapter;
    private SpecialOffersAdapter specialOffersAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        initializeAdapters();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setupViews();
        setupObservers();
    }

    private void initializeAdapters() {
        categoryAdapter = new CategoryAdapter(this);
        featuredProductAdapter = new FeaturedProductAdapter(this);
        popularProductsAdapter = new ProductGridAdapter(this);
        specialOffersAdapter = new SpecialOffersAdapter(this);
    }

    private void findViews(@NonNull View view) {
        searchBar = view.findViewById(R.id.search_bar_container);
        featuredProductsPager = view.findViewById(R.id.featured_products_pager);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        popularProductsRecyclerView = view.findViewById(R.id.popular_products_recycler_view);
        specialOffersRecyclerView = view.findViewById(R.id.special_offers_recycler_view);
    }

    private void setupViews() {
        // Set up search bar
        searchBar.setOnClickListener(v -> viewModel.navigateToSearch());

        // Set up featured products pager
        featuredProductsPager.setAdapter(featuredProductAdapter);
        featuredProductAdapter.startAutoScrolling();

        // Set up categories RecyclerView
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Set up popular products RecyclerView
        popularProductsRecyclerView.setAdapter(popularProductsAdapter);

        // Set up special offers RecyclerView
        specialOffersRecyclerView.setAdapter(specialOffersAdapter);
    }

    private void setupObservers() {
        // Observe navigation commands
        viewModel.getNavigationCommand().observe(getViewLifecycleOwner(), command -> {
            if (command != null) {
                Navigation.findNavController(requireView())
                    .navigate(command.getActionId(), command.getArgs());
                viewModel.resetNavigation();
            }
        });

        // Observe categories
        viewModel.getActiveCategories().observe(getViewLifecycleOwner(), categories -> {
            Log.d(TAG, "Received " + categories.size() + " categories");
            categoryAdapter.submitList(categories);
        });

        // Observe featured products
        viewModel.getFeaturedProducts().observe(getViewLifecycleOwner(), products -> {
            Log.d(TAG, "Received " + products.size() + " featured products");
            featuredProductAdapter.submitList(products);
        });

        // Observe popular products
        viewModel.getPopularProducts().observe(getViewLifecycleOwner(), products -> {
            Log.d(TAG, "Received " + products.size() + " popular products");
            popularProductsAdapter.submitList(products);
        });

        // Observe special offers
        viewModel.getSpecialOffers().observe(getViewLifecycleOwner(), offers -> {
            Log.d(TAG, "Received " + offers.size() + " special offers");
            specialOffersAdapter.submitList(offers);
        });
    }

    // OnCategoryClickListener implementation
    @Override
    public void onCategoryClick(Category category) {
        viewModel.navigateToCategory(category);
    }

    // OnProductClickListener implementation
    @Override
    public void onProductClick(Product product) {
        viewModel.navigateToProduct(product);
    }

    // OnProductActionListener implementation
    @Override
    public void onAddToCartClick(Product product) {
        viewModel.addToCart(product);
    }

    // OnOfferClickListener implementation
    @Override
    public void onOfferClick(SpecialOffersAdapter.SpecialOffer offer) {
        viewModel.navigateToProduct(offer.getProduct());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (featuredProductAdapter != null) {
            featuredProductAdapter.startAutoScrolling();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (featuredProductAdapter != null) {
            featuredProductAdapter.stopAutoScrolling();
        }
    }
}