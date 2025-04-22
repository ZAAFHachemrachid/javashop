package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.example.java_shop.R;
import com.example.java_shop.adapters.CartAdapter;
import com.example.java_shop.data.models.CartItemWithProduct;
import com.example.java_shop.viewmodels.CartViewModel;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartActionListener {

    private CartViewModel viewModel;
    private CartAdapter adapter;
    private View emptyState;
    private RecyclerView cartRecyclerView;
    private TextView cartTotalView;
    private MaterialButton checkoutButton;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        adapter = new CartAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setupViews();
        setupObservers();
    }

    private void findViews(View view) {
        emptyState = view.findViewById(R.id.empty_state);
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        cartTotalView = view.findViewById(R.id.cart_total);
        checkoutButton = view.findViewById(R.id.checkout_button);
    }

    private void setupViews() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartRecyclerView.setAdapter(adapter);

        checkoutButton.setOnClickListener(v -> viewModel.navigateToCheckout());
    }

    private void setupObservers() {
        // Observe cart items
        viewModel.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);

        // Observe cart total
        viewModel.getCartTotal().observe(getViewLifecycleOwner(), this::updateCartTotal);

        // Observe navigation commands
        viewModel.getNavigationCommand().observe(getViewLifecycleOwner(), command -> {
            if (command != null) {
                Navigation.findNavController(requireView())
                    .navigate(command.getActionId(), command.getArgs());
                viewModel.resetNavigation();
            }
        });

        // Observe toast messages
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartItems(List<CartItemWithProduct> items) {
        adapter.submitList(items);
        updateEmptyState(items);
    }

    private void updateCartTotal(double total) {
        cartTotalView.setText(currencyFormat.format(total));
        checkoutButton.setEnabled(total > 0);
    }

    private void updateEmptyState(List<CartItemWithProduct> items) {
        boolean isEmpty = items == null || items.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        cartRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    // CartAdapter.CartActionListener implementation
    @Override
    public void onItemClick(CartItemWithProduct item) {
        viewModel.navigateToProduct(item);
    }

    @Override
    public void onIncreaseQuantity(CartItemWithProduct item) {
        viewModel.increaseQuantity(item);
    }

    @Override
    public void onDecreaseQuantity(CartItemWithProduct item) {
        viewModel.decreaseQuantity(item);
    }

    @Override
    public void onRemoveItem(CartItemWithProduct item) {
        viewModel.removeItem(item);
    }
}