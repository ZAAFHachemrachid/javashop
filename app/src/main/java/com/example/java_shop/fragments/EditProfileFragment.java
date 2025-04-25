package com.example.java_shop.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.example.java_shop.R;
import com.example.java_shop.fragments.base.BaseProtectedFragment;
import com.example.java_shop.utils.ImageLoader;
import com.example.java_shop.viewmodels.AccountViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import androidx.navigation.Navigation;

public class EditProfileFragment extends BaseProtectedFragment {

    private AccountViewModel accountViewModel;
    private ShapeableImageView profileImage;
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;
    private EditText nameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private MaterialButton saveButton;
    private View progressBar;
    private View errorCard;
    private View profileImageProgress;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageResult);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
            Navigation.findNavController(v).navigateUp());
        
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        
        initializeViews(view);
        setupClickListeners();
        observeViewModel();
        
        // Load current user data
        accountViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameInput.setText(user.getName());
                emailInput.setText(user.getEmail());
                phoneInput.setText(user.getPhone());
                ImageLoader.loadImage(profileImage, user.getProfilePicture());
            }
        });
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        nameLayout = view.findViewById(R.id.nameLayout);
        emailLayout = view.findViewById(R.id.emailLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        nameInput = view.findViewById(R.id.nameInput);
        emailInput = view.findViewById(R.id.emailInput);
        phoneInput = view.findViewById(R.id.phoneInput);
        saveButton = view.findViewById(R.id.saveButton);
        progressBar = view.findViewById(R.id.progressBar);
        errorCard = view.findViewById(R.id.errorCard);
        profileImageProgress = view.findViewById(R.id.profileImageProgress);
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> 
            imagePickerLauncher.launch("image/*"));

        saveButton.setOnClickListener(v -> {
            clearErrors();
            if (validateInputs()) {
                updateProfile();
            }
        });
    }

    private void observeViewModel() {
        accountViewModel.getViewState().observe(getViewLifecycleOwner(), this::handleViewState);
        accountViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    private void handleViewState(AccountViewModel.ViewState state) {
        switch (state) {
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                saveButton.setEnabled(false);
                break;
            case SUCCESS:
                progressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
                Navigation.findNavController(requireView()).navigateUp();
                break;
            case ERROR:
                progressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
                break;
            case IDLE:
                progressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
                break;
        }
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            errorCard.setVisibility(View.VISIBLE);
            ((android.widget.TextView) errorCard.findViewById(R.id.errorText)).setText(error);
        } else {
            errorCard.setVisibility(View.GONE);
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        }

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            isValid = false;
        }

        if (phone.isEmpty()) {
            phoneLayout.setError("Phone number is required");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        phoneLayout.setError(null);
    }

    private void updateProfile() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        
        accountViewModel.updateProfile(name, email, phone);
    }

    private void handleImageResult(Uri imageUri) {
        if (imageUri != null) {
            profileImageProgress.setVisibility(View.VISIBLE);
            ImageLoader.loadImage(profileImage, imageUri.toString());
            accountViewModel.updateProfilePicture(imageUri.toString());
        }
    }
}