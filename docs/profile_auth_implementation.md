# Profile Authentication Implementation Plan

## Overview
This document outlines the plan for enhancing the profile functionality to use authentication and profile data from the database in the Java Shop Android application.

## Current Implementation Analysis
- AccountFragment extends BaseProtectedFragment for authentication handling
- User data stored in Room database with User model
- SessionManager handles auth state and user session
- AccountViewModel manages user data, orders, and addresses
- UI adapts based on authentication state

## Implementation Plan

### 1. Authentication Integration

#### SessionManager Enhancements
- Add session timeout handling
- Improve session validation logic
- Add proper cleanup on logout

```java
// Session timeout handling
public boolean isSessionValid() {
    long lastActivityTime = getLastActivityTime();
    return isLoggedIn() && 
           (System.currentTimeMillis() - lastActivityTime) < SESSION_TIMEOUT;
}

// Activity tracking
public void updateLastActivity() {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
    editor.apply();
}
```

### 2. Profile Data Management

#### AccountViewModel Enhancements
```java
public class AccountViewModel extends AndroidViewModel {
    // New fields
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>(ViewState.IDLE);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // View state enum
    public enum ViewState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    // New methods
    public void updateProfile(String name, String email, String phone) {
        viewState.setValue(ViewState.LOADING);
        if (!validateProfileData(name, email, phone)) {
            viewState.setValue(ViewState.ERROR);
            errorMessage.setValue("Invalid profile data");
            return;
        }

        // Update user data
        User currentUser = getCurrentUser().getValue();
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            userRepository.updateUser(currentUser,
                success -> {
                    viewState.setValue(ViewState.SUCCESS);
                    sessionManager.updateUserEmail(email);
                },
                error -> {
                    viewState.setValue(ViewState.ERROR);
                    errorMessage.setValue(error);
                }
            );
        }
    }

    public void updateProfilePicture(Uri imageUri) {
        viewState.setValue(ViewState.LOADING);
        userRepository.updateProfilePicture(sessionManager.getUserId(), imageUri.toString(),
            success -> viewState.setValue(ViewState.SUCCESS),
            error -> {
                viewState.setValue(ViewState.ERROR);
                errorMessage.setValue(error);
            }
        );
    }

    private boolean validateProfileData(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) return false;
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false;
        if (phone == null || phone.trim().isEmpty()) return false;
        return true;
    }
}
```

### 3. UserRepository Updates

```java
public class UserRepository {
    // New methods
    public void updateUser(User user, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        executorService.execute(() -> {
            try {
                userDao.update(user);
                handler.post(() -> onSuccess.onSuccess(null));
            } catch (Exception e) {
                handler.post(() -> onFailure.onFailure(e));
            }
        });
    }

    public void updateProfilePicture(int userId, String pictureUri,
                                   OnSuccessListener<Void> onSuccess,
                                   OnFailureListener onFailure) {
        executorService.execute(() -> {
            try {
                userDao.updateProfilePicture(userId, pictureUri);
                handler.post(() -> onSuccess.onSuccess(null));
            } catch (Exception e) {
                handler.post(() -> onFailure.onFailure(e));
            }
        });
    }
}
```

### 4. UI Implementation

#### AccountFragment Updates
- Add loading indicators
- Improve error handling
- Add input validation feedback
- Implement smooth transitions

```java
public class AccountFragment extends BaseProtectedFragment {
    // Add progress indicator
    private ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressBar);

        // Observe view state
        accountViewModel.getViewState().observe(getViewLifecycleOwner(), this::handleViewState);
        accountViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    private void handleViewState(AccountViewModel.ViewState state) {
        switch (state) {
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                progressBar.setVisibility(View.GONE);
                // Show success message
                break;
            case ERROR:
                progressBar.setVisibility(View.GONE);
                break;
            case IDLE:
                progressBar.setVisibility(View.GONE);
                break;
        }
    }
}
```

### 5. Security Considerations

1. Session Management
- Implement session timeout
- Validate session on each profile operation
- Proper logout cleanup

2. Data Security
- Input sanitization
- Secure password handling
- Profile picture validation

3. Error Handling
- User-friendly error messages
- Proper error logging
- Graceful degradation

## Implementation Process

1. Update ViewModels and Repositories
2. Implement new UI components
3. Add security features
4. Test authentication flow
5. Test profile operations
6. Add error handling
7. Perform security testing

## Testing Plan

1. Authentication Tests
- Session creation
- Session validation
- Session timeout
- Logout

2. Profile Operations Tests
- Load profile data
- Update profile
- Update profile picture
- Validation rules

3. Error Handling Tests
- Invalid input
- Network failures
- Database errors

4. Security Tests
- Session timeout
- Input validation
- Authorization checks

## Migration Notes

1. Database Changes
- No schema changes required
- Existing data remains compatible

2. UI Changes
- Add progress indicators
- Update error message displays
- Add validation feedback

3. Navigation Changes
- No changes required to existing navigation