package tn.rnu.isi.mycycle.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import tn.rnu.isi.mycycle.R;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyCyclePrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get UI elements
        ImageView logoIcon = findViewById(R.id.florist_icon);
        TextView appName = findViewById(R.id.app_name);
        TextView descriptionText = findViewById(R.id.descriptionText);

        // Add fade-in animation
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(200);

        if (logoIcon != null) {
            logoIcon.startAnimation(fadeIn);
        }
        if (appName != null) {
            appName.startAnimation(fadeIn);
        }
        if (descriptionText != null) {
            descriptionText.startAnimation(fadeIn);
        }

        // Navigate after splash duration
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DURATION);
    }

    /**
     * Check if user is logged in and navigate accordingly
     */
    private void navigateToNextScreen() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long userId = prefs.getLong(KEY_USER_ID, -1);

        Intent intent;
        if (userId != -1) {
            // User is logged in, go to HomeActivity
            intent = new Intent(MainActivity.this, HomeActivity.class);
        } else {
            // User is not logged in, go to SignInActivity
            intent = new Intent(MainActivity.this, SignInActivity.class);
        }

        startActivity(intent);
        finish(); // Close splash screen so user can't go back to it
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Get current logged-in user ID from SharedPreferences
     * This is a helper method that can be used by other activities
     */
    public static long getCurrentUserId(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return prefs.getLong(KEY_USER_ID, -1);
    }

    /**
     * Save user session after login
     * This is a helper method that can be used by SignInActivity
     */
    public static void saveUserSession(android.content.Context context, long userId, String userName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    /**
     * Clear user session (logout)
     * This is a helper method that can be used by SettingsActivity
     */
    public static void clearUserSession(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.apply();
    }

    /**
     * Get current logged-in user name from SharedPreferences
     */
    public static String getCurrentUserName(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn(android.content.Context context) {
        return getCurrentUserId(context) != -1;
    }
}
