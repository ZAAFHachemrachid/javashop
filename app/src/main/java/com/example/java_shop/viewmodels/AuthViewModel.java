package com.example.java_shop.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.data.models.User;
import com.example.java_shop.data.repositories.UserRepository;
import com.example.java_shop.utils.SessionManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Patterns;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<AuthResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<AuthResult> signupResult = new MutableLiveData<>();
    private final MutableLiveData<AuthResult> passwordResetResult = new MutableLiveData<>();
    private final MutableLiveData<ValidationErrors> validationErrors = new MutableLiveData<>();

    public AuthViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        sessionManager = SessionManager.getInstance(application);
    }

    public void login(String email, String password) {
        if (!validateLoginInput(email, password)) {
            return;
        }

        String hashedPassword = hashPassword(password);
        // Since login() returns LiveData, its callback will already be on the main thread
        userRepository.login(email, hashedPassword).observeForever(user -> {
            if (user != null) {
                sessionManager.createSession(user.getId(), user.getEmail());
                loginResult.setValue(new AuthResult(true, null));
            } else {
                loginResult.setValue(new AuthResult(false, "Invalid email or password"));
            }
        });
    }

    public void signup(String name, String email, String phone, String password, String confirmPassword) {
        if (!validateSignupInput(name, email, phone, password, confirmPassword)) {
            return;
        }

        userRepository.checkEmailExists(email, exists -> {
            if (exists) {
                signupResult.postValue(new AuthResult(false, "Email already registered"));
                return;
            }

            String hashedPassword = hashPassword(password);
            User newUser = new User(name, email, phone, "", hashedPassword);
            userRepository.insert(newUser);
            signupResult.postValue(new AuthResult(true, null));
        });
    }

    public void resetPassword(String email) {
        if (!validateEmail(email)) {
            return;
        }

        userRepository.checkEmailExists(email, exists -> {
            if (!exists) {
                passwordResetResult.postValue(new AuthResult(false, "Email not found"));
                return;
            }

            // In a real app, you would:
            // 1. Generate a reset token
            // 2. Save it to the database with an expiration
            // 3. Send an email with a reset link
            // For demo purposes, we'll just simulate success
            passwordResetResult.postValue(new AuthResult(true, null));
        });
    }

    public void logout() {
        sessionManager.clearSession();
    }

    private boolean validateLoginInput(String email, String password) {
        ValidationErrors errors = new ValidationErrors();
        boolean isValid = true;

        if (!validateEmail(email)) {
            errors.setEmailError("Invalid email format");
            isValid = false;
        }

        if (password.isEmpty()) {
            errors.setPasswordError("Password is required");
            isValid = false;
        }

        if (!isValid) {
            validationErrors.setValue(errors);
        }
        return isValid;
    }

    private boolean validateSignupInput(String name, String email, String phone, 
                                      String password, String confirmPassword) {
        ValidationErrors errors = new ValidationErrors();
        boolean isValid = true;

        if (name.isEmpty()) {
            errors.setNameError("Name is required");
            isValid = false;
        }

        if (!validateEmail(email)) {
            errors.setEmailError("Invalid email format");
            isValid = false;
        }

        if (phone.isEmpty()) {
            errors.setPhoneError("Phone number is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            errors.setPasswordError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            errors.setPasswordError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            errors.setConfirmPasswordError("Passwords do not match");
            isValid = false;
        }

        if (!isValid) {
            validationErrors.setValue(errors);
        }
        return isValid;
    }

    private boolean validateEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback to plain password in case of error
        }
    }

    public LiveData<AuthResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<AuthResult> getSignupResult() {
        return signupResult;
    }

    public LiveData<AuthResult> getPasswordResetResult() {
        return passwordResetResult;
    }

    public LiveData<ValidationErrors> getValidationErrors() {
        return validationErrors;
    }

    public static class AuthResult {
        private final boolean success;
        private final String error;

        public AuthResult(boolean success, String error) {
            this.success = success;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }
    }

    public static class ValidationErrors {
        private String nameError;
        private String emailError;
        private String phoneError;
        private String passwordError;
        private String confirmPasswordError;

        public String getNameError() {
            return nameError;
        }

        public void setNameError(String nameError) {
            this.nameError = nameError;
        }

        public String getEmailError() {
            return emailError;
        }

        public void setEmailError(String emailError) {
            this.emailError = emailError;
        }

        public String getPhoneError() {
            return phoneError;
        }

        public void setPhoneError(String phoneError) {
            this.phoneError = phoneError;
        }

        public String getPasswordError() {
            return passwordError;
        }

        public void setPasswordError(String passwordError) {
            this.passwordError = passwordError;
        }

        public String getConfirmPasswordError() {
            return confirmPasswordError;
        }

        public void setConfirmPasswordError(String confirmPasswordError) {
            this.confirmPasswordError = confirmPasswordError;
        }
    }
}