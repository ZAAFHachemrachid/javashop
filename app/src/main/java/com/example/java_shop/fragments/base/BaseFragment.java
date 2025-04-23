package com.example.java_shop.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.java_shop.utils.SessionManager;

public abstract class BaseFragment extends Fragment {
    protected SessionManager sessionManager;
    protected NavController navController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sessionManager = SessionManager.getInstance(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    /**
     * Called when authentication state changes
     * Override in child fragments if needed
     */
    protected void onAuthenticationChanged(boolean isAuthenticated) {
        // Default implementation does nothing
    }
}