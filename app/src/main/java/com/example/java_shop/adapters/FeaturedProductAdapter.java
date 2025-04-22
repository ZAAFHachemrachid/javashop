package com.example.java_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Product;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class FeaturedProductAdapter extends RecyclerView.Adapter<FeaturedProductAdapter.FeaturedProductViewHolder> {

    private List<Product> products;
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public FeaturedProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
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
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class FeaturedProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productDescription;
        private final TextView productPrice;
        private final RatingBar ratingBar;
        private final TextView reviewCount;

        public FeaturedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.featured_product_image);
            productName = itemView.findViewById(R.id.featured_product_name);
            productDescription = itemView.findViewById(R.id.featured_product_description);
            productPrice = itemView.findViewById(R.id.featured_product_price);
            ratingBar = itemView.findViewById(R.id.featured_product_rating);
            reviewCount = itemView.findViewById(R.id.featured_product_review_count);
        }

        public void bind(Product product, OnProductClickListener listener) {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
            productPrice.setText(currencyFormatter.format(product.getPrice()));
            
            // Convert double to float for the rating
            ratingBar.setRating((float) product.getRating());
            reviewCount.setText(String.format("(%d reviews)", product.getReviewCount()));
            
            // Load image with a library like Glide or Picasso
            // Glide.with(itemView).load(product.getImageUrl()).into(productImage);
            
            // For now, using placeholder
            productImage.setImageResource(R.drawable.placeholder_image);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}