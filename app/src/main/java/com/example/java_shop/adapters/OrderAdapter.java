package com.example.java_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Order;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder> {
    private final OrderClickListener listener;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public OrderAdapter(OrderClickListener listener) {
        super(new OrderDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.bind(order, listener);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderNumber;
        private final TextView orderDate;
        private final TextView orderStatus;
        private final TextView orderTotal;
        private final Button viewDetailsButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
        }

        public void bind(Order order, OrderClickListener listener) {
            orderNumber.setText("Order #" + order.getId());
            orderDate.setText(dateFormat.format(order.getOrderDate()));
            orderStatus.setText(order.getStatus());
            orderTotal.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalAmount()));

            viewDetailsButton.setOnClickListener(v -> listener.onOrderClick(order));
        }
    }

    private static class OrderDiffCallback extends DiffUtil.ItemCallback<Order> {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                   oldItem.getTotalAmount() == newItem.getTotalAmount();
        }
    }

    public interface OrderClickListener {
        void onOrderClick(Order order);
    }
}