package tn.rnu.isi.mycycle.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.utils.NotificationHelper;

public class NotificationSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NOTIFICATIONS = "NotificationPrefs";
    private static final String KEY_PERIOD = "notify_period";
    private static final String KEY_FERTILE = "notify_fertile";
    private static final String KEY_DAILY = "notify_daily";

    private Switch switchPeriod;
    private Switch switchFertile;
    private Switch switchDaily;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        initializeViews();
        loadPreferences();
        setupListeners();
    }

    private void initializeViews() {
        switchPeriod = findViewById(R.id.switch_period);
        switchFertile = findViewById(R.id.switch_fertile);
        switchDaily = findViewById(R.id.switch_daily);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NOTIFICATIONS, MODE_PRIVATE);
        switchPeriod.setChecked(prefs.getBoolean(KEY_PERIOD, true));
        switchFertile.setChecked(prefs.getBoolean(KEY_FERTILE, true));
        switchDaily.setChecked(prefs.getBoolean(KEY_DAILY, false));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        switchPeriod.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(KEY_PERIOD, isChecked);
            if (isChecked) {
                // Schedule period notification (example: 5 days from now)
                NotificationHelper.schedulePeriodReminder(this, 5);
                Toast.makeText(this, "Period reminders enabled", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelPeriodReminder(this);
                Toast.makeText(this, "Period reminders disabled", Toast.LENGTH_SHORT).show();
            }
        });

        switchFertile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(KEY_FERTILE, isChecked);
            if (isChecked) {
                // Schedule fertile window notification (example: 10 days from now)
                NotificationHelper.scheduleFertileWindowReminder(this, 10);
                Toast.makeText(this, "Fertile window reminders enabled", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelFertileReminder(this);
                Toast.makeText(this, "Fertile window reminders disabled", Toast.LENGTH_SHORT).show();
            }
        });

        switchDaily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(KEY_DAILY, isChecked);
            if (isChecked) {
                // Schedule daily log reminder (24 hours from now, repeating)
                NotificationHelper.scheduleDailyLogReminder(this);
                Toast.makeText(this, "Daily log reminders enabled (starting 24h from now)", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelDailyLogReminder(this);
                Toast.makeText(this, "Daily log reminders disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NOTIFICATIONS, MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
}
