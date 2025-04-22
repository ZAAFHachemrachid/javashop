package com.example.java_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.utils.ImageLoader;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(new CategoryDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);
        holder.bind(category, listener);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView nameView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.category_image);
            nameView = itemView.findViewById(R.id.category_name);
        }

        void bind(Category category, OnCategoryClickListener listener) {
            nameView.setText(category.getName());
            ImageLoader.loadImage(iconView, category.getIconUrl());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    static class CategoryDiffCallback extends DiffUtil.ItemCallback<Category> {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getIconUrl().equals(newItem.getIconUrl()) &&
                   oldItem.isActive() == newItem.isActive() &&
                   oldItem.getDisplayOrder() == newItem.getDisplayOrder();
        }
    }
}