package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.example.java_shop.fragments.base.BaseFragment;
import com.example.java_shop.R;
import com.example.java_shop.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

public class LoginFragment extends BaseFragment {

    private AuthViewModel viewModel;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private TextView signupLink;
    private TextView forgotPasswordLink;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initializeViews(view);
        setupClickListeners();
        observeViewModel();
    }

    private void initializeViews(View view) {
        emailLayout = view.findViewById(R.id.emailLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        signupLink = view.findViewById(R.id.signupLink);
        forgotPasswordLink = view.findViewById(R.id.forgotPasswordLink);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());

        signupLink.setOnClickListener(v -> 
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_loginFragment_to_signupFragment));

        forgotPasswordLink.setOnClickListener(v ->
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }

    private void observeViewModel() {
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            setLoading(false);
            
            if (result.isSuccess()) {
                navigateToHome();
            } else {
                showError(result.getError());
            }
        });

        viewModel.getValidationErrors().observe(getViewLifecycleOwner(), errors -> {
            emailLayout.setError(errors.getEmailError());
            passwordLayout.setError(errors.getPasswordError());
        });
    }

    private void attemptLogin() {
        String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();

        // Clear previous errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        setLoading(true);
        viewModel.login(email, password);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
    }

    private void navigateToHome() {
        // Check for saved destination
        int savedDestinationId = sessionManager.getSavedDestinationId();
        if (savedDestinationId != -1 && savedDestinationId != R.id.loginFragment) {
            // Get saved arguments if they exist
            String savedArgs = sessionManager.getSavedDestinationArgs();
            Bundle args = null;
            if (savedArgs != null) {
                args = new Bundle();
                // Parse saved args string and populate bundle
                // This is a simplified version - you might need more complex parsing
                args.putString("args", savedArgs);
            }
            
            // Navigate to saved destination
            navController.navigate(savedDestinationId, args);
            
            // Clear saved destination
            sessionManager.clearSavedDestination();
        } else {
            // Default navigation to home
            navController.navigate(R.id.action_loginFragment_to_homeFragment);
        }
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
        }
    }
}