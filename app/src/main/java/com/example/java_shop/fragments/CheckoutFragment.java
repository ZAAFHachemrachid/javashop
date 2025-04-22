package com.example.java_shop.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.java_shop.R;
import com.example.java_shop.viewmodels.CheckoutViewModel;
import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;
    private TextView itemsCount;
    private TextView subtotal;
    private TextInputEditText nameInput;
    private TextInputEditText addressInput;
    private TextInputEditText cityInput;
    private TextInputEditText zipInput;
    private RadioGroup paymentMethodGroup;
    private TextView orderTotal;
    private MaterialButton placeOrderButton;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setupViews();
        setupObservers();
    }

    private void findViews(View view) {
        itemsCount = view.findViewById(R.id.items_count);
        subtotal = view.findViewById(R.id.subtotal);
        nameInput = view.findViewById(R.id.name_input);
        addressInput = view.findViewById(R.id.address_input);
        cityInput = view.findViewById(R.id.city_input);
        zipInput = view.findViewById(R.id.zip_input);
        paymentMethodGroup = view.findViewById(R.id.payment_method_group);
        orderTotal = view.findViewById(R.id.order_total);
        placeOrderButton = view.findViewById(R.id.place_order_button);
    }

    private void setupViews() {
        // Set up form field listeners
        nameInput.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.setFullName(s)));
        addressInput.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.setStreetAddress(s)));
        cityInput.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.setCity(s)));
        zipInput.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.setZipCode(s)));

        // Set up payment method selection
        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.credit_card) {
                viewModel.setPaymentMethod(CheckoutViewModel.PaymentMethod.CREDIT_CARD);
            } else if (checkedId == R.id.pay_on_delivery) {
                viewModel.setPaymentMethod(CheckoutViewModel.PaymentMethod.PAY_ON_DELIVERY);
            }
        });

        // Set up place order button
        placeOrderButton.setOnClickListener(v -> viewModel.placeOrder());
    }

    private void setupObservers() {
        // Observe cart items
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            int count = items != null ? items.size() : 0;
            itemsCount.setText(count + (count == 1 ? " item" : " items"));
        });

        // Observe subtotal
        viewModel.getSubtotal().observe(getViewLifecycleOwner(), amount -> {
            subtotal.setText("Subtotal: " + currencyFormat.format(amount));
            updateOrderTotal();
        });

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

    private void updateOrderTotal() {
        double total = viewModel.getOrderTotal();
        orderTotal.setText(currencyFormat.format(total));
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final TextChangeListener listener;

        SimpleTextWatcher(TextChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            listener.onTextChanged(s.toString());
        }

        interface TextChangeListener {
            void onTextChanged(String text);
        }
    }
}