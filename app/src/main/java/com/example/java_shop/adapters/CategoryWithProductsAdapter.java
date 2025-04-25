package com.example.java_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.models.CategoryWithProducts;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.utils.ImageLoader;

public class CategoryWithProductsAdapter extends ListAdapter<CategoryWithProducts, CategoryWithProductsAdapter.ViewHolder> {

    private final OnCategoryClickListener categoryClickListener;
    private final ProductGridAdapter.OnProductActionListener productActionListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onViewAllClick(Category category);
    }

    public CategoryWithProductsAdapter(
            OnCategoryClickListener categoryClickListener,
            ProductGridAdapter.OnProductActionListener productActionListener) {
        super(new DiffUtil.ItemCallback<CategoryWithProducts>() {
            @Override
            public boolean areItemsTheSame(@NonNull CategoryWithProducts oldItem, @NonNull CategoryWithProducts newItem) {
                return oldItem.getCategory().getId().equals(newItem.getCategory().getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull CategoryWithProducts oldItem, @NonNull CategoryWithProducts newItem) {
                return oldItem.getCategory().equals(newItem.getCategory()) &&
                       oldItem.getProducts().equals(newItem.getProducts());
            }
        });
        this.categoryClickListener = categoryClickListener;
        this.productActionListener = productActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_with_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryWithProducts item = getItem(position);
        holder.bind(item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryIcon;
        private final TextView categoryName;
        private final TextView viewAllButton;
        private final RecyclerView productsRecyclerView;
        private final ProductGridAdapter productsAdapter;

        ViewHolder(View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);
            viewAllButton = itemView.findViewById(R.id.view_all_button);
            productsRecyclerView = itemView.findViewById(R.id.products_recycler_view);
            
            productsAdapter = new ProductGridAdapter(productActionListener);
            productsRecyclerView.setAdapter(productsAdapter);
        }

        void bind(CategoryWithProducts item) {
            Category category = item.getCategory();
            
            // Bind category details
            categoryName.setText(category.getName());
            ImageLoader.loadImage(categoryIcon, category.getIconUrl());

            // Set click listeners
            itemView.findViewById(R.id.category_header).setOnClickListener(v -> 
                categoryClickListener.onCategoryClick(category));
                
            viewAllButton.setOnClickListener(v -> 
                categoryClickListener.onViewAllClick(category));

            // Submit products list
            productsAdapter.submitList(item.getProducts());
        }
    }
}