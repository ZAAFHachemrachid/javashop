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
import com.example.java_shop.data.models.Product;
import com.example.java_shop.utils.ImageLoader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SpecialOffersAdapter extends ListAdapter<SpecialOffersAdapter.SpecialOffer, SpecialOffersAdapter.SpecialOfferViewHolder> {

    private final OnOfferClickListener listener;
    private final NumberFormat currencyFormat;
    private final SimpleDateFormat dateFormat;

    public SpecialOffersAdapter(OnOfferClickListener listener) {
        super(new OfferDiffCallback());
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
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
        SpecialOffer offer = getItem(position);
        holder.bind(offer, listener, currencyFormat, dateFormat);
    }

    static class SpecialOfferViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView discountPercentage;
        private final TextView productName;
        private final TextView originalPrice;
        private final TextView discountedPrice;
        private final TextView validUntil;

        SpecialOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.offer_product_image);
            discountPercentage = itemView.findViewById(R.id.discount_percentage);
            productName = itemView.findViewById(R.id.offer_product_name);
            originalPrice = itemView.findViewById(R.id.original_price);
            discountedPrice = itemView.findViewById(R.id.discounted_price);
            validUntil = itemView.findViewById(R.id.offer_valid_until);
        }

        void bind(SpecialOffer offer, OnOfferClickListener listener, 
                 NumberFormat currencyFormat, SimpleDateFormat dateFormat) {
            productName.setText(offer.product.getName());
            
            // Set prices
            String originalPriceStr = currencyFormat.format(offer.product.getPrice());
            String discountedPriceStr = currencyFormat.format(
                offer.product.getPrice() * (1 - offer.discountPercentage / 100.0));
            
            originalPrice.setText(originalPriceStr);
            discountedPrice.setText(discountedPriceStr);
            
            // Set discount percentage
            discountPercentage.setText(String.format("-%d%% OFF", (int) offer.discountPercentage));
            
            // Set validity period
            String validityText = String.format("Valid until %s", 
                dateFormat.format(offer.validUntil));
            validUntil.setText(validityText);

            // Load product image
            ImageLoader.loadImage(productImage, offer.product.getImageUrl());

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOfferClick(offer);
                }
            });
        }
    }

    public static class SpecialOffer {
        private final Product product;
        private final double discountPercentage;
        private final Date validUntil;

        public SpecialOffer(Product product, double discountPercentage, Date validUntil) {
            this.product = product;
            this.discountPercentage = discountPercentage;
            this.validUntil = validUntil;
        }

        public Product getProduct() {
            return product;
        }

        public double getDiscountPercentage() {
            return discountPercentage;
        }

        public Date getValidUntil() {
            return validUntil;
        }

        public boolean isValid() {
            return new Date().before(validUntil);
        }

        public double getDiscountedPrice() {
            return product.getPrice() * (1 - discountPercentage / 100.0);
        }
    }

    public interface OnOfferClickListener {
        void onOfferClick(SpecialOffer offer);
    }

    static class OfferDiffCallback extends DiffUtil.ItemCallback<SpecialOffer> {
        @Override
        public boolean areItemsTheSame(@NonNull SpecialOffer oldItem, @NonNull SpecialOffer newItem) {
            return oldItem.getProduct().getId().equals(newItem.getProduct().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SpecialOffer oldItem, @NonNull SpecialOffer newItem) {
            return oldItem.getProduct().equals(newItem.getProduct()) &&
                   oldItem.getDiscountPercentage() == newItem.getDiscountPercentage() &&
                   oldItem.getValidUntil().equals(newItem.getValidUntil());
        }
    }
}