package com.example.java_shop.adapters;

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
import com.example.java_shop.R;
import com.example.java_shop.data.models.Product;
import com.example.java_shop.utils.ImageLoader;
import java.text.NumberFormat;
import java.util.Locale;

public class FeaturedProductAdapter extends ListAdapter<Product, FeaturedProductAdapter.FeaturedProductViewHolder> {

    private final OnProductClickListener listener;
    private final NumberFormat currencyFormat;

    public FeaturedProductAdapter(OnProductClickListener listener) {
        super(new ProductDiffCallback());
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public FeaturedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_product, parent, false);
        return new FeaturedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product, listener, currencyFormat);
    }

    static class FeaturedProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productDescription;
        private final TextView productPrice;
        private final RatingBar ratingBar;
        private final TextView reviewCount;

        FeaturedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.featured_product_image);
            productName = itemView.findViewById(R.id.featured_product_name);
            productDescription = itemView.findViewById(R.id.featured_product_description);
            productPrice = itemView.findViewById(R.id.featured_product_price);
            ratingBar = itemView.findViewById(R.id.featured_product_rating);
            reviewCount = itemView.findViewById(R.id.featured_product_review_count);
        }

        void bind(Product product, OnProductClickListener listener, NumberFormat currencyFormat) {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(currencyFormat.format(product.getPrice()));
            ratingBar.setRating((float) product.getRating());
            reviewCount.setText(String.format("(%d reviews)", product.getReviewCount()));

            // Load product image
            ImageLoader.loadImage(productImage, product.getImageUrl());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    static class ProductDiffCallback extends DiffUtil.ItemCallback<Product> {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getDescription().equals(newItem.getDescription()) &&
                   oldItem.getPrice() == newItem.getPrice() &&
                   oldItem.getRating() == newItem.getRating() &&
                   oldItem.getReviewCount() == newItem.getReviewCount() &&
                   oldItem.getImageUrl().equals(newItem.getImageUrl());
        }
    }

    // Auto-scrolling functionality
    private boolean isAutoScrolling = false;
    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAutoScrolling) return;
            // Get the RecyclerView from any ViewHolder
            RecyclerView recyclerView = null;
            if (getCurrentList().size() > 0) {
                FeaturedProductViewHolder holder = getViewHolderAt(0);
                if (holder != null) {
                    recyclerView = (RecyclerView) holder.itemView.getParent();
                }
            }
            if (recyclerView != null) {
                int currentItem = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0)
                        .getLayoutParams()).getViewAdapterPosition();
                int nextItem = (currentItem + 1) % getCurrentList().size();
                recyclerView.smoothScrollToPosition(nextItem);
                recyclerView.postDelayed(this, 5000); // Scroll every 5 seconds
            }
        }
    };

    private FeaturedProductViewHolder getViewHolderAt(int position) {
        try {
            return (FeaturedProductViewHolder) getCurrentList().get(position).getClass()
                    .getDeclaredField("holder").get(getCurrentList().get(position));
        } catch (Exception e) {
            return null;
        }
    }

    public void startAutoScrolling() {
        isAutoScrolling = true;
        autoScrollRunnable.run();
    }

    public void stopAutoScrolling() {
        isAutoScrolling = false;
    }
}