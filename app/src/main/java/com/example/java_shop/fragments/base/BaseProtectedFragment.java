package com.example.java_shop.fragments.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import com.example.java_shop.utils.SessionManager;

/**
 * Base fragment class for screens that require authentication.
 * Automatically handles checking authentication and redirecting to login if needed.
 */
public abstract class BaseProtectedFragment extends BaseFragment {
    
    private Observer<Boolean> authObserver;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Create authentication observer
        authObserver = this::handleAuthenticationState;
        
        // Check authentication immediately and start observing changes
        checkAuthenticationAndObserve();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recheck authentication when fragment becomes visible
        checkAuthentication();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up observer
        if (authObserver != null) {
            SessionManager manager = getSessionManager();
            if (manager != null) {
                manager.getAuthenticationState().removeObserver(authObserver);
            }
        }
    }

    /**
     * Checks current authentication and starts observing changes
     */
    private void checkAuthenticationAndObserve() {
        // Check current auth state
        checkAuthentication();
        
        // Start observing auth changes
        SessionManager manager = getSessionManager();
        if (manager != null) {
            manager.getAuthenticationState().observe(getViewLifecycleOwner(), authObserver);
        }
    }

    /**
     * Checks if user is authenticated and redirects to login if not
     */
    private void checkAuthentication() {
        SessionManager manager = getSessionManager();
        if (manager == null || !manager.hasValidSession()) {
            redirectToLogin();
        }
    }

    /**
     * Handles changes in authentication state
     */
    private void handleAuthenticationState(boolean isAuthenticated) {
        if (!isAuthenticated) {
            redirectToLogin();
        }
        onAuthenticationChanged(isAuthenticated);
    }

    /**
     * Redirects to login screen, saving current destination for return
     */
    private void redirectToLogin() {
        if (navController != null && navController.getCurrentDestination() != null) {
            SessionManager manager = getSessionManager();
            if (manager != null) {
                manager.checkAuthenticationAndRedirect(navController);
            }
        }
    }
}