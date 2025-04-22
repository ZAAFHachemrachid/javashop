package com.example.java_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.CartItemWithProduct;
import com.example.java_shop.utils.ImageLoader;
import java.text.NumberFormat;
import java.util.Locale;

public class CartAdapter extends ListAdapter<CartItemWithProduct, CartAdapter.CartItemViewHolder> {

    private final CartActionListener listener;
    private final NumberFormat currencyFormat;

    public CartAdapter(CartActionListener listener) {
        super(new CartItemDiffCallback());
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItemWithProduct item = getItem(position);
        holder.bind(item, listener, currencyFormat);
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView quantity;
        private final ImageButton decreaseButton;
        private final ImageButton increaseButton;
        private final ImageButton removeButton;
        private final TextView totalPrice;

        CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity);
            increaseButton = itemView.findViewById(R.id.increase_quantity);
            removeButton = itemView.findViewById(R.id.remove_item);
            totalPrice = itemView.findViewById(R.id.total_price);
        }

        void bind(CartItemWithProduct item, CartActionListener listener, NumberFormat currencyFormat) {
            productName.setText(item.getProduct().getName());
            productPrice.setText(currencyFormat.format(item.getProduct().getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()));
            totalPrice.setText(currencyFormat.format(item.getTotalPrice()));

            // Load product image
            ImageLoader.loadImage(productImage, item.getProduct().getImageUrl());

            // Set click listeners
            decreaseButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDecreaseQuantity(item);
                }
            });

            increaseButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIncreaseQuantity(item);
                }
            });

            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            // Update button states
            decreaseButton.setEnabled(item.getQuantity() > 1);
            increaseButton.setEnabled(item.getQuantity() < item.getProduct().getStockQuantity());
        }
    }

    public interface CartActionListener {
        void onItemClick(CartItemWithProduct item);
        void onIncreaseQuantity(CartItemWithProduct item);
        void onDecreaseQuantity(CartItemWithProduct item);
        void onRemoveItem(CartItemWithProduct item);
    }

    static class CartItemDiffCallback extends DiffUtil.ItemCallback<CartItemWithProduct> {
        @Override
        public boolean areItemsTheSame(@NonNull CartItemWithProduct oldItem, @NonNull CartItemWithProduct newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItemWithProduct oldItem, @NonNull CartItemWithProduct newItem) {
            return oldItem.getProductId().equals(newItem.getProductId()) &&
                   oldItem.getQuantity() == newItem.getQuantity() &&
                   oldItem.getPrice() == newItem.getPrice();
        }
    }
}