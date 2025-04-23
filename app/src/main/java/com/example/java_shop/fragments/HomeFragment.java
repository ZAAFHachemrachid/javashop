package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.java_shop.fragments.base.BaseProtectedFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.java_shop.R;
import com.example.java_shop.viewmodels.HomeViewModel;
import com.example.java_shop.adapters.FeaturedProductAdapter;
import com.example.java_shop.adapters.SpecialOfferAdapter;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.utils.CategoryCardCreator;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseProtectedFragment {

    private HomeViewModel viewModel;
    private ViewPager2 featuredProductsViewPager;
    private TabLayout featuredProductsIndicator;
    private RecyclerView specialOffersRecyclerView;
    private LinearLayout categoriesContainer;
    private MaterialCardView searchCard;
    
    private FeaturedProductAdapter featuredProductAdapter;
    private SpecialOfferAdapter specialOfferAdapter;
    private Runnable autoScrollRunnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
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
        setupViews(view);
        setupObservers();
    }

    private void setupViews(View view) {
        // Featured products carousel
        featuredProductsViewPager = view.findViewById(R.id.featured_products_viewpager);
        featuredProductsIndicator = view.findViewById(R.id.featured_products_indicator);
        
        // Initialize with empty list, will be updated by observer
        featuredProductAdapter = new FeaturedProductAdapter(new ArrayList<>(), 
            product -> viewModel.navigateToProduct(product.getId()));
        
        featuredProductsViewPager.setAdapter(featuredProductAdapter);
        
        // Special offers recycler view
        specialOffersRecyclerView = view.findViewById(R.id.special_offers_recyclerview);
        specialOfferAdapter = new SpecialOfferAdapter(new ArrayList<>(), 
            product -> viewModel.navigateToProduct(product.getId()));
        
        specialOffersRecyclerView.setAdapter(specialOfferAdapter);
        
        // Categories container
        categoriesContainer = view.findViewById(R.id.categories_container);
        
        // Search card click listener
        searchCard = view.findViewById(R.id.search_card);
        searchCard.setOnClickListener(v -> {
            // Navigate to search fragment or show search dialog
            Toast.makeText(getContext(), "Search functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        // Observe featured products
        viewModel.getFeaturedProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                featuredProductAdapter.updateProducts(products);
                
                // Set up tab indicator
                new TabLayoutMediator(featuredProductsIndicator, featuredProductsViewPager,
                        (tab, position) -> {
                            // Tab configuration (leave empty as we're using custom indicator)
                        }).attach();
                
                // Auto-scroll feature (optional)
                startAutoScroll(products.size());
            }
        });

        // Observe special offers
        viewModel.getSpecialOffers().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                specialOfferAdapter.updateProducts(products);
            }
        });
        
        // Observe categories for shortcuts
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                CategoryCardCreator.createCategoryCards(getContext(), categoriesContainer, categories);
            }
        });

        // Observe navigation commands
        viewModel.getNavigationCommand().observe(getViewLifecycleOwner(), command -> {
            if (command != null && !sessionManager.hasValidSession()) {
                // Don't navigate if not authenticated
                return;
            }
            if (command != null) {
                Bundle args = new Bundle();
                args.putString("productId", command.getArg());
                Navigation.findNavController(requireView())
                    .navigate(getResources().getIdentifier(
                        command.getActionId(),
                        "id",
                        requireContext().getPackageName()
                    ), args);
                viewModel.resetNavigation();
            }
        });
    }
    
    private void startAutoScroll(int itemCount) {
        if (itemCount > 1) {
            final long AUTO_SCROLL_INTERVAL = 5000; // 5 seconds
            
            autoScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    if (featuredProductsViewPager == null) return;
                    
                    int currentPosition = featuredProductsViewPager.getCurrentItem();
                    int nextPosition = (currentPosition + 1) % itemCount;
                    featuredProductsViewPager.setCurrentItem(nextPosition, true);
                    
                    // Schedule next scroll if fragment is still alive
                    if (isAdded() && featuredProductsViewPager != null) {
                        featuredProductsViewPager.postDelayed(this, AUTO_SCROLL_INTERVAL);
                    }
                }
            };
            
            featuredProductsViewPager.postDelayed(autoScrollRunnable, AUTO_SCROLL_INTERVAL);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (featuredProductsViewPager != null && autoScrollRunnable != null) {
            featuredProductsViewPager.removeCallbacks(autoScrollRunnable);
        }
    }

    @Override
    protected void onAuthenticationChanged(boolean isAuthenticated) {
        if (isAuthenticated) {
            // Data will be automatically refreshed through LiveData observers
            setupObservers();
        }
    }
}