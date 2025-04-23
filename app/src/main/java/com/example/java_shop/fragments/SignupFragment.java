package com.example.java_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.java_shop.R;
import com.example.java_shop.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

public class SignupFragment extends Fragment {

    private AuthViewModel viewModel;
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton signupButton;
    private TextView loginLink;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
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
        nameLayout = view.findViewById(R.id.nameLayout);
        emailLayout = view.findViewById(R.id.emailLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        confirmPasswordLayout = view.findViewById(R.id.confirmPasswordLayout);
        
        nameInput = view.findViewById(R.id.nameInput);
        emailInput = view.findViewById(R.id.emailInput);
        phoneInput = view.findViewById(R.id.phoneInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        
        signupButton = view.findViewById(R.id.signupButton);
        loginLink = view.findViewById(R.id.loginLink);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> attemptSignup());

        loginLink.setOnClickListener(v ->
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_signupFragment_to_loginFragment));
    }

    private void observeViewModel() {
        viewModel.getSignupResult().observe(getViewLifecycleOwner(), result -> {
            setLoading(false);
            
            if (result.isSuccess()) {
                showSuccessAndNavigate();
            } else {
                showError(result.getError());
            }
        });

        viewModel.getValidationErrors().observe(getViewLifecycleOwner(), errors -> {
            nameLayout.setError(errors.getNameError());
            emailLayout.setError(errors.getEmailError());
            phoneLayout.setError(errors.getPhoneError());
            passwordLayout.setError(errors.getPasswordError());
            confirmPasswordLayout.setError(errors.getConfirmPasswordError());
        });
    }

    private void attemptSignup() {
        String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
        String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
        String phone = Objects.requireNonNull(phoneInput.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString().trim();

        // Clear previous errors
        clearErrors();

        setLoading(true);
        viewModel.signup(name, email, phone, password, confirmPassword);
    }

    private void clearErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        phoneLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!isLoading);
        nameInput.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        phoneInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
        confirmPasswordInput.setEnabled(!isLoading);
    }

    private void showSuccessAndNavigate() {
        Snackbar.make(requireView(), "Account created successfully!", Snackbar.LENGTH_SHORT).show();
        Navigation.findNavController(requireView())
                .navigate(R.id.action_signupFragment_to_loginFragment);
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
        }
    }
}