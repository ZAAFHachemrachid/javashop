package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.adapters.OrderAdapter;
import com.example.java_shop.adapters.AddressAdapter;
import com.example.java_shop.data.models.Address;
import com.example.java_shop.data.models.Order;
import com.example.java_shop.data.models.User;
import com.example.java_shop.utils.SessionManager;
import com.example.java_shop.viewmodels.AccountViewModel;
import com.example.java_shop.viewmodels.AuthViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.java_shop.utils.ImageLoader;

public class AccountFragment extends Fragment implements 
        OrderAdapter.OrderClickListener,
        AddressAdapter.AddressClickListener {

    private AccountViewModel accountViewModel;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;
    private OrderAdapter orderAdapter;
    private AddressAdapter addressAdapter;
    private ShapeableImageView profileImage;
    private TextView userName, userEmail, userPhone;
    private View authenticatedContent;
    private View unauthenticatedContent;
    private Button loginButton;
    private Button signupButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = SessionManager.getInstance(requireContext());
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initializeViews(view);
        setupRecyclerViews(view);
        setupClickListeners(view);
        observeViewModels();
        checkAuthenticationState();
    }

    private void initializeViews(View view) {
        authenticatedContent = view.findViewById(R.id.authenticatedContent);
        unauthenticatedContent = view.findViewById(R.id.unauthenticatedContent);
        profileImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userPhone = view.findViewById(R.id.userPhone);
        loginButton = view.findViewById(R.id.loginButton);
        signupButton = view.findViewById(R.id.signupButton);
    }

    private void setupRecyclerViews(View view) {
        // Orders RecyclerView
        RecyclerView ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        orderAdapter = new OrderAdapter(this);
        ordersRecyclerView.setAdapter(orderAdapter);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Addresses RecyclerView
        RecyclerView addressesRecyclerView = view.findViewById(R.id.addressesRecyclerView);
        addressAdapter = new AddressAdapter(this);
        addressesRecyclerView.setAdapter(addressAdapter);
        addressesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupClickListeners(View view) {
        // Login/Signup buttons
        loginButton.setOnClickListener(v ->
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_accountFragment_to_loginFragment));

        signupButton.setOnClickListener(v ->
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_accountFragment_to_signupFragment));

        // Profile section
        ImageButton editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            if (sessionManager.getUserId() != -1) {
                Bundle args = new Bundle();
                args.putInt("userId", sessionManager.getUserId());
                Navigation.findNavController(v)
                        .navigate(R.id.action_accountFragment_to_editProfileFragment, args);
            }
        });

        // Address section
        Button addAddressButton = view.findViewById(R.id.addAddressButton);
        addAddressButton.setOnClickListener(v ->
            Navigation.findNavController(v)
                    .navigate(R.id.action_accountFragment_to_addEditAddressFragment));

        // Settings section
        Button changePasswordButton = view.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(v -> {
            // Show change password dialog
            // TODO: Implement password change dialog
        });

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void observeViewModels() {
        accountViewModel.getCurrentUser().observe(getViewLifecycleOwner(), this::updateUserInfo);
        accountViewModel.getUserOrders().observe(getViewLifecycleOwner(), orderAdapter::submitList);
        accountViewModel.getUserAddresses().observe(getViewLifecycleOwner(), addressAdapter::submitList);
        accountViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    private void checkAuthenticationState() {
        boolean isAuthenticated = sessionManager.hasValidSession();
        authenticatedContent.setVisibility(isAuthenticated ? View.VISIBLE : View.GONE);
        unauthenticatedContent.setVisibility(isAuthenticated ? View.GONE : View.VISIBLE);

        if (isAuthenticated) {
            accountViewModel.loadUserData(sessionManager.getUserId());
        }
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());
            userPhone.setText(user.getPhone());
            ImageLoader.loadImage(profileImage, user.getProfilePicture());
        }
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout", (dialog, which) -> {
                authViewModel.logout();
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_accountFragment_to_loginFragment);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage(error)
                .setPositiveButton("OK", null)
                .show();
        }
    }

    // OrderAdapter.OrderClickListener implementation
    @Override
    public void onOrderClick(Order order) {
        Bundle args = new Bundle();
        args.putInt("orderId", order.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_accountFragment_to_orderDetailsFragment, args);
    }

    // AddressAdapter.AddressClickListener implementation
    @Override
    public void onEditAddress(Address address) {
        Bundle args = new Bundle();
        args.putInt("addressId", address.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_accountFragment_to_addEditAddressFragment, args);
    }

    @Override
    public void onDeleteAddress(Address address) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Address")
            .setMessage("Are you sure you want to delete this address?")
            .setPositiveButton("Delete", (dialog, which) -> {
                accountViewModel.deleteAddress(address);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onSetDefaultAddress(Address address) {
        accountViewModel.setDefaultAddress(address.getId());
    }
}