package com.example.java_shop.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.utils.ImageLoader;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductGridAdapter extends ListAdapter<Product, ProductGridAdapter.ProductViewHolder> {

    private final OnProductActionListener listener;
    private final NumberFormat currencyFormat;

    public ProductGridAdapter(OnProductActionListener listener) {
        super(new ProductDiffCallback());
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product, listener, currencyFormat);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final RatingBar ratingBar;
        private final TextView productPrice;
        private final MaterialButton addToCartButton;
        private final TextView stockStatus;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            ratingBar = itemView.findViewById(R.id.product_rating);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
            stockStatus = itemView.findViewById(R.id.stock_status);
        }

        void bind(Product product, OnProductActionListener listener, NumberFormat currencyFormat) {
            productName.setText(product.getName());
            productPrice.setText(currencyFormat.format(product.getPrice()));
            ratingBar.setRating((float) product.getRating());

            // Load product image
            ImageLoader.loadImage(productImage, product.getImageUrl());

            // Set stock status
            boolean inStock = product.getStockQuantity() > 0;
            stockStatus.setText(inStock ? "In Stock" : "Out of Stock");
            stockStatus.setTextColor(inStock ? 
                Color.parseColor("#4CAF50") : // Green
                Color.parseColor("#F44336")); // Red

            // Enable/disable add to cart button based on stock
            addToCartButton.setEnabled(inStock);
            addToCartButton.setAlpha(inStock ? 1.0f : 0.5f);

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            addToCartButton.setOnClickListener(v -> {
                if (listener != null && inStock) {
                    listener.onAddToCartClick(product);
                }
            });
        }
    }

    public interface OnProductActionListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    static class ProductDiffCallback extends DiffUtil.ItemCallback<Product> {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getPrice() == newItem.getPrice() &&
                   oldItem.getRating() == newItem.getRating() &&
                   oldItem.getStockQuantity() == newItem.getStockQuantity() &&
                   oldItem.getImageUrl().equals(newItem.getImageUrl());
        }
    }
}