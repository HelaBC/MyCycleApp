package tn.rnu.isi.mycycle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import tn.rnu.isi.mycycle.R;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout navHome;
    private LinearLayout navCalendar;
    private LinearLayout navStats;
    private LinearLayout navSettings;

    // Settings items
    private LinearLayout btnEditProfile;
    private LinearLayout btnNotifications;
    private LinearLayout btnPrivacy;
    private LinearLayout btnAbout;
    private LinearLayout btnLogout;
    private Switch switchTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupNavigation();
        setupSettingsListeners();
    }

    private void initializeViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        navHome = findViewById(R.id.nav_home);
        navCalendar = findViewById(R.id.nav_calendar);
        navStats = findViewById(R.id.nav_stats);
        navSettings = findViewById(R.id.nav_settings);

        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnPrivacy = findViewById(R.id.btn_privacy);
        btnAbout = findViewById(R.id.btn_about);
        switchTheme = findViewById(R.id.theme_switch);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupSettingsListeners() {
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        }

        if (btnNotifications != null) {
            btnNotifications
                    .setOnClickListener(v -> startActivity(new Intent(this, NotificationSettingsActivity.class)));
        }

        if (btnPrivacy != null) {
            btnPrivacy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyActivity.class)));
        }

        if (btnAbout != null) {
            btnAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        }

        if (switchTheme != null) {
            switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Toast.makeText(this, "Theme switching coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        MainActivity.clearUserSession(this);
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navStats.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, StatisticsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navSettings.setOnClickListener(v -> {
            // Already on settings
        });
    }
}
