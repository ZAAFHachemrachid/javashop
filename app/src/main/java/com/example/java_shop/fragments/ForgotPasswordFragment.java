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

public class ForgotPasswordFragment extends Fragment {

    private AuthViewModel viewModel;
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private MaterialButton resetPasswordButton;
    private TextView backToLoginLink;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
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
        emailInput = view.findViewById(R.id.emailInput);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        backToLoginLink = view.findViewById(R.id.backToLoginLink);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        resetPasswordButton.setOnClickListener(v -> attemptPasswordReset());

        backToLoginLink.setOnClickListener(v ->
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_forgotPasswordFragment_to_loginFragment));
    }

    private void observeViewModel() {
        viewModel.getPasswordResetResult().observe(getViewLifecycleOwner(), result -> {
            setLoading(false);
            
            if (result.isSuccess()) {
                showSuccessAndNavigate();
            } else {
                showError(result.getError());
            }
        });

        viewModel.getValidationErrors().observe(getViewLifecycleOwner(), errors -> {
            emailLayout.setError(errors.getEmailError());
        });
    }

    private void attemptPasswordReset() {
        String email = Objects.requireNonNull(emailInput.getText()).toString().trim();

        // Clear previous errors
        emailLayout.setError(null);

        setLoading(true);
        viewModel.resetPassword(email);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        resetPasswordButton.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
    }

    private void showSuccessAndNavigate() {
        Snackbar.make(requireView(), 
            "Password reset instructions have been sent to your email", 
            Snackbar.LENGTH_LONG).show();
            
        Navigation.findNavController(requireView())
                .navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
        }
    }
}