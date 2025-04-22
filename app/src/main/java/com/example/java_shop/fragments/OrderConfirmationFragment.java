package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.example.java_shop.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderConfirmationFragment extends Fragment {

    private TextView orderId;
    private TextView orderDate;
    private TextView shippingDetails;
    private TextView paymentMethod;
    private MaterialButton continueShoppingButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setupViews();
        displayOrderDetails();
    }

    private void findViews(View view) {
        orderId = view.findViewById(R.id.order_id);
        orderDate = view.findViewById(R.id.order_date);
        shippingDetails = view.findViewById(R.id.shipping_details);
        paymentMethod = view.findViewById(R.id.payment_method);
        continueShoppingButton = view.findViewById(R.id.continue_shopping_button);
    }

    private void setupViews() {
        // Set up continue shopping button
        continueShoppingButton.setOnClickListener(v -> 
            Navigation.findNavController(requireView())
                .navigate(R.id.action_orderConfirmationFragment_to_homeFragment));
    }

    private void displayOrderDetails() {
        // Get order ID from arguments
        OrderConfirmationFragmentArgs args = OrderConfirmationFragmentArgs.fromBundle(getArguments());
        String orderIdString = args.getOrderId();

        // Set order ID
        orderId.setText(getString(R.string.order_id_format, orderIdString));

        // Set order date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        orderDate.setText(getString(R.string.order_date_format, dateFormat.format(new Date())));

        // Set shipping details (in a real app, these would come from the order data)
        shippingDetails.setText(String.format(
            "%s\n%s\n%s, %s %s",
            "John Doe",
            "123 Main St",
            "Anytown",
            "CA",
            "12345"
        ));

        // Set payment method (in a real app, this would come from the order data)
        paymentMethod.setText("Credit Card");
    }
}