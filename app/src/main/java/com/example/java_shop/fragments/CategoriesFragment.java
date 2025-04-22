package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.adapters.CategoryAdapter;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.viewmodels.CategoriesViewModel;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private CategoriesViewModel viewModel;
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        categoryAdapter = new CategoryAdapter(this);
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
    }

    private void setupObservers() {
        // Observe categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            loadingIndicator.setVisibility(View.GONE);
            if (categories == null || categories.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                categoriesRecyclerView.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                categoriesRecyclerView.setVisibility(View.VISIBLE);
                categoryAdapter.submitList(categories);
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
}