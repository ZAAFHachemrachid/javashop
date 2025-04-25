package com.example.java_shop.utils;

import android.content.Context;
import com.example.java_shop.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "JavaShopSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LAST_ACTIVITY = "lastActivity";
    private static final String KEY_SAVED_DESTINATION = "savedDestination";
    private static final String KEY_SAVED_ARGS = "savedArgs";
    
    // Session timeout after 30 minutes of inactivity
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes in milliseconds
    
    private final SharedPreferences prefs;
    private static SessionManager instance;
    private final MutableLiveData<Boolean> authenticationState = new MutableLiveData<>(false);

    private SessionManager(Context context) {
        // Use regular SharedPreferences with private mode
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void createSession(int userId, String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
        editor.apply();
        authenticationState.postValue(true);
    }

    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        authenticationState.postValue(false);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void updateUserEmail(String newEmail) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, newEmail);
        editor.apply();
    }

    public boolean hasValidSession() {
        if (!isLoggedIn() || getUserId() == -1 || getEmail() == null) {
            return false;
        }
        
        long lastActivity = getLastActivityTime();
        long currentTime = System.currentTimeMillis();
        
        // Check if session has timed out
        if (currentTime - lastActivity > SESSION_TIMEOUT) {
            clearSession();
            return false;
        }
        
        // Update last activity time
        updateLastActivity();
        return true;
    }
    
    /**
     * Get the last activity timestamp
     */
    private long getLastActivityTime() {
        return prefs.getLong(KEY_LAST_ACTIVITY, 0);
    }
    
    /**
     * Update the last activity timestamp
     */
    public void updateLastActivity() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Get the authentication state as LiveData for observing changes
     */
    public LiveData<Boolean> getAuthenticationState() {
        return authenticationState;
    }

    /**
     * Save the intended destination for post-login navigation
     */
    public void saveIntendedDestination(int destinationId, Bundle args) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SAVED_DESTINATION, destinationId);
        if (args != null) {
            editor.putString(KEY_SAVED_ARGS, args.toString());
        }
        editor.apply();
    }

    /**
     * Clear saved destination after successful navigation
     */
    public void clearSavedDestination() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_SAVED_DESTINATION);
        editor.remove(KEY_SAVED_ARGS);
        editor.apply();
    }

    /**
     * Get saved destination ID or -1 if none exists
     */
    public int getSavedDestinationId() {
        return prefs.getInt(KEY_SAVED_DESTINATION, -1);
    }

    /**
     * Get saved destination arguments or null if none exists
     */
    public String getSavedDestinationArgs() {
        return prefs.getString(KEY_SAVED_ARGS, null);
    }

    /**
     * Handle authentication check and navigation
     * @return true if authenticated, false if redirected to login
     */
    public boolean checkAuthenticationAndRedirect(NavController navController) {
        if (!hasValidSession()) {
            // Save current destination if not already on login screen
            if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() != R.id.loginFragment) {
                saveIntendedDestination(
                    navController.getCurrentDestination().getId(),
                    navController.getCurrentBackStackEntry() != null ?
                        navController.getCurrentBackStackEntry().getArguments() : null
                );
            }
            navController.navigate(R.id.loginFragment);
            return false;
        }
        return true;
    }
}