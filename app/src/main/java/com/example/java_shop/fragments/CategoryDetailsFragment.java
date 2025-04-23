package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.java_shop.R;
import com.example.java_shop.adapters.CategoryAdapter;
import com.example.java_shop.adapters.ProductGridAdapter;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.utils.ImageLoader;
import com.example.java_shop.viewmodels.CategoryDetailsViewModel;
import com.example.java_shop.viewmodels.CategoryDetailsViewModel.SortOption;

public class CategoryDetailsFragment extends Fragment implements 
        CategoryAdapter.OnCategoryClickListener,
        ProductGridAdapter.OnProductActionListener {

    private CategoryDetailsViewModel viewModel;
    
    // Views
    private ImageView categoryIcon;
    private TextView categoryName;
    private TextView categoryDescription;
    private RecyclerView subcategoriesRecyclerView;
    private RecyclerView productsRecyclerView;
    private Toolbar toolbar;
    private View emptyState;
    private ChipGroup filterChipGroup;
    private Chip sortChip;
    private Chip filterChip;
    private Chip inStockChip;
    
    // Adapters
    private CategoryAdapter subcategoriesAdapter;
    private ProductGridAdapter productsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CategoryDetailsViewModel.class);
        
        // Get category ID from navigation args
        int categoryId = CategoryDetailsFragmentArgs.fromBundle(getArguments()).getCategoryId();
        viewModel.setCategoryId(categoryId);
        
        // Initialize adapters
        subcategoriesAdapter = new CategoryAdapter(this);
        productsAdapter = new ProductGridAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setupViews();
        setupObservers();
    }

    private void findViews(View view) {
        categoryIcon = view.findViewById(R.id.category_icon);
        categoryName = view.findViewById(R.id.category_name);
        categoryDescription = view.findViewById(R.id.category_description);
        subcategoriesRecyclerView = view.findViewById(R.id.subcategories_recycler_view);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        toolbar = view.findViewById(R.id.toolbar);
        emptyState = view.findViewById(R.id.empty_state);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        sortChip = view.findViewById(R.id.sort_chip);
        filterChip = view.findViewById(R.id.filter_chip);
        inStockChip = view.findViewById(R.id.in_stock_chip);
    }

    private void setupViews() {
        // Setup toolbar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        // Set navigation icon click listener for the back button
        toolbar.setNavigationOnClickListener(v -> {
            // Navigate back
            Navigation.findNavController(requireView()).navigateUp();
        });

        // Setup RecyclerViews
        subcategoriesRecyclerView.setAdapter(subcategoriesAdapter);
        productsRecyclerView.setAdapter(productsAdapter);

        // Setup chips
        sortChip.setOnClickListener(v -> showSortDialog());
        inStockChip.setOnCheckedChangeListener((button, isChecked) -> 
            viewModel.setShowInStockOnly(isChecked));
    }

    private void setupObservers() {
        // Observe category details
        viewModel.getCategory().observe(getViewLifecycleOwner(), this::updateCategoryDetails);

        // Observe subcategories
        viewModel.getSubcategories().observe(getViewLifecycleOwner(), subcategories -> {
            subcategoriesAdapter.submitList(subcategories);
            subcategoriesRecyclerView.setVisibility(
                subcategories != null && !subcategories.isEmpty() ? View.VISIBLE : View.GONE
            );
        });

        // Observe products
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productsAdapter.submitList(products);
            updateEmptyState(products);
        });

        // Observe sort option
        viewModel.getCurrentSortOption().observe(getViewLifecycleOwner(), this::updateSortChipText);

        // Observe navigation commands
        viewModel.getNavigationCommand().observe(getViewLifecycleOwner(), command -> {
            if (command != null) {
                Navigation.findNavController(requireView())
                    .navigate(command.getActionId(), command.getArgs());
                viewModel.resetNavigation();
            }
        });
    }

    private void updateCategoryDetails(Category category) {
        if (category == null) return;
        
        categoryName.setText(category.getName());
        categoryDescription.setText(category.getDescription());
        ImageLoader.loadImage(categoryIcon, category.getIconUrl());
    }

    private void updateEmptyState(List<Product> products) {
        boolean isEmpty = products == null || products.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        productsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updateSortChipText(SortOption sortOption) {
        String sortOptionText;
        switch (sortOption) {
            case NAME_ASC:
                sortOptionText = "Name (A-Z)";
                break;
            case NAME_DESC:
                sortOptionText = "Name (Z-A)";
                break;
            case PRICE_LOW_HIGH:
                sortOptionText = "Price (Low-High)";
                break;
            case PRICE_HIGH_LOW:
                sortOptionText = "Price (High-Low)";
                break;
            case RATING:
            default:
                sortOptionText = "Rating";
                break;
        }
        String sortText = "Sort: " + sortOptionText;
        sortChip.setText(sortText);
    }

    private void showSortDialog() {
        String[] options = {
            "Rating",
            "Name (A-Z)",
            "Name (Z-A)",
            "Price (Low-High)",
            "Price (High-Low)"
        };
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sort by")
            .setItems(options, (dialog, which) -> {
                SortOption selected;
                switch (which) {
                    case 1:
                        selected = SortOption.NAME_ASC;
                        break;
                    case 2:
                        selected = SortOption.NAME_DESC;
                        break;
                    case 3:
                        selected = SortOption.PRICE_LOW_HIGH;
                        break;
                    case 4:
                        selected = SortOption.PRICE_HIGH_LOW;
                        break;
                    default:
                        selected = SortOption.RATING;
                        break;
                }
                viewModel.setSortOption(selected);
            })
            .show();
    }

    // CategoryAdapter.OnCategoryClickListener implementation
    @Override
    public void onCategoryClick(Category category) {
        viewModel.navigateToSubcategory(category);
    }

    // ProductGridAdapter.OnProductActionListener implementation
    @Override
    public void onProductClick(Product product) {
        viewModel.navigateToProduct(product);
    }

    @Override
    public void onAddToCartClick(Product product) {
        // TODO: Implement add to cart functionality
    }
}