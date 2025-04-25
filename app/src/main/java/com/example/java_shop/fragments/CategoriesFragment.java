package com.example.java_shop.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.java_shop.R;
import com.example.java_shop.adapters.CategoryWithProductsAdapter;
import com.example.java_shop.adapters.ProductGridAdapter;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.viewmodels.CategoriesViewModel;
import java.util.List;

public class CategoriesFragment extends Fragment implements
    CategoryWithProductsAdapter.OnCategoryClickListener,
    ProductGridAdapter.OnProductActionListener {

    private CategoriesViewModel viewModel;
    private RecyclerView categoriesRecyclerView;
    private CategoryWithProductsAdapter categoryAdapter;
    private TextInputEditText searchEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        categoryAdapter = new CategoryWithProductsAdapter(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupObservers();
    }

    private View emptyState;
    private View loadingIndicator;

    private void setupViews(View view) {
        // Setup RecyclerView
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Find views
        emptyState = view.findViewById(R.id.empty_state);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        searchEditText = view.findViewById(R.id.search_edit_text);

        // Setup search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.updateSearchQuery(s.toString());
            }
        });
    }

    private void setupObservers() {
        // Observe categories with products
        viewModel.getCategoriesWithProducts().observe(getViewLifecycleOwner(), categoriesWithProducts -> {
            loadingIndicator.setVisibility(View.GONE);
            if (categoriesWithProducts == null || categoriesWithProducts.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                categoriesRecyclerView.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                categoriesRecyclerView.setVisibility(View.VISIBLE);
                categoryAdapter.submitList(categoriesWithProducts);
            }
        });

        // Observe navigation commands
        viewModel.getNavigationCommand().observe(getViewLifecycleOwner(), command -> {
            if (command != null) {
                Navigation.findNavController(requireView())
                    .navigate(command.getActionId(), command.getArgs());
                viewModel.resetNavigation();
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        viewModel.navigateToCategory(category);
    }

    @Override
    public void onViewAllClick(Category category) {
        viewModel.navigateToCategory(category);
    }

    @Override
    public void onProductClick(Product product) {
        // Navigate to product details
        Bundle args = new Bundle();
        args.putString("productId", product.getId());
        Navigation.findNavController(requireView())
            .navigate(R.id.action_categoriesFragment_to_productDetailsFragment, args);
    }

    @Override
    public void onAddToCartClick(Product product) {
        // TODO: Implement add to cart functionality
    }
}