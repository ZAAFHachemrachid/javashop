package com.example.java_shop.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Product;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.SpecialOfferViewHolder> {

    private List<Product> products;
    private final OnOfferClickListener listener;

    public interface OnOfferClickListener {
        void onOfferClick(Product product);
    }

    public SpecialOfferAdapter(List<Product> products, OnOfferClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpecialOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_special_offer, parent, false);
        return new SpecialOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialOfferViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class SpecialOfferViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView originalPrice;
        private final TextView discountedPrice;
        private final TextView discountPercentage;
        private final TextView validUntil;

        public SpecialOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.offer_product_image);
            productName = itemView.findViewById(R.id.offer_product_name);
            originalPrice = itemView.findViewById(R.id.original_price);
            discountedPrice = itemView.findViewById(R.id.discounted_price);
            discountPercentage = itemView.findViewById(R.id.discount_percentage);
            validUntil = itemView.findViewById(R.id.offer_valid_until);
        }

        public void bind(Product product, OnOfferClickListener listener) {
            productName.setText(product.getName());
            
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
            
            double originalPriceValue = product.getOriginalPrice() > 0 ? 
                    product.getOriginalPrice() : product.getPrice() * 1.25; // Fallback calculation
            
            double discountedPriceValue = product.getPrice();
            
            // Calculate discount percentage
            int discountPercentageValue = (int) (100 - (discountedPriceValue / originalPriceValue * 100));
            
            // Display prices
            originalPrice.setText(currencyFormatter.format(originalPriceValue));
            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            
            discountedPrice.setText(currencyFormatter.format(discountedPriceValue));
            discountPercentage.setText(String.format("-%d%% OFF", discountPercentageValue));
            
            // Valid until date
            validUntil.setText(String.format("Valid until %s", product.getOfferValidUntil()));
            
            // Load product image
            // Glide.with(itemView).load(product.getImageUrl()).into(productImage);
            productImage.setImageResource(R.drawable.placeholder_image);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOfferClick(product);
                }
            });
        }
    }
} 