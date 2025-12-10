package tn.rnu.isi.mycycle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.DateUtils;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.models.CycleEntry;

public class HomeActivity extends AppCompatActivity {

    private TextView greetingText;
    private TextView tvNextPeriodDays;
    private TextView tvPhaseText;
    private View progressPhaseView;
    private Button btnAddSymptom;
    private Button btnCalendar;

    private LinearLayout navHome;
    private LinearLayout navCalendar;
    private LinearLayout navStats;
    private LinearLayout navSettings;

    private DatabaseManager dbManager;
    private long userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = DatabaseManager.getInstance(this);
        userId = MainActivity.getCurrentUserId(this);

        if (userId == -1) {
            // No user logged in – go back to SignInActivity
            navigateToSignIn();
            return;
        }

        initializeViews();
        setupClickListeners();
        loadDashboardData();
    }

    private void initializeViews() {
        greetingText = findViewById(R.id.greeting_text);
        tvNextPeriodDays = findViewById(R.id.tv_next_period_days);
        tvPhaseText = findViewById(R.id.tv_phase_text);
        progressPhaseView = findViewById(R.id.progress_phase);
        btnAddSymptom = findViewById(R.id.btn_add_symptom);
        btnCalendar = findViewById(R.id.btn_calendar);

        navHome = findViewById(R.id.nav_home);
        navCalendar = findViewById(R.id.nav_calendar);
        navStats = findViewById(R.id.nav_stats);
        navSettings = findViewById(R.id.nav_settings);

        // Set greeting
        String name = MainActivity.getCurrentUserName(this);
        if (name == null || name.isEmpty()) {
            greetingText.setText(getString(R.string.home_greeting_generic));
        } else {
            greetingText.setText(getString(R.string.home_greeting_with_name, name));
        }
    }

    private void setupClickListeners() {
        // Profile image could later open profile/settings
        ImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> navigateToSettings());

        btnAddSymptom.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SymptomsActivity.class);
            startActivity(intent);
        });

        btnCalendar.setOnClickListener(v -> navigateToCalendar());

        // Bottom navigation
        navHome.setOnClickListener(v -> {
            // Already on home – optionally scroll to top or refresh
            loadDashboardData();
        });

        navCalendar.setOnClickListener(v -> navigateToCalendar());

        navStats.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        navSettings.setOnClickListener(v -> navigateToSettings());
    }

    private void loadDashboardData() {
        // Next period
        int daysUntilNextPeriod = dbManager.getDaysUntilNextPeriod(userId);
        if (daysUntilNextPeriod >= 0) {
            tvNextPeriodDays.setText(String.valueOf(daysUntilNextPeriod));
        } else {
            tvNextPeriodDays.setText("--");
        }

        // Current phase
        String phase = dbManager.getCurrentPhase(userId);
        if (phase == null || phase.isEmpty()) {
            phase = "Unknown";
        }
        tvPhaseText.setText(phase + " Phase");

        // Simple phase progress indication based on day in cycle if possible
        updatePhaseProgress();

        // Optionally load today's status/mood from latest cycle entry
        updateTodayStatusAndMood();
    }

    private void updatePhaseProgress() {
        // For now, use a simple fixed width depending on phase
        String phase = dbManager.getCurrentPhase(userId);
        int widthDp;
        if ("Menstrual".equalsIgnoreCase(phase)) {
            widthDp = 16;
        } else if ("Follicular".equalsIgnoreCase(phase)) {
            widthDp = 32;
        } else if ("Ovulation".equalsIgnoreCase(phase)) {
            widthDp = 48;
        } else if ("Luteal".equalsIgnoreCase(phase)) {
            widthDp = 56;
        } else {
            widthDp = 24;
        }

        int widthPx = (int) (widthDp * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout.LayoutParams params =
                (android.widget.FrameLayout.LayoutParams) progressPhaseView.getLayoutParams();
        params.width = widthPx;
        progressPhaseView.setLayoutParams(params);
    }

    private void updateTodayStatusAndMood() {
        TextView statusText = findViewById(R.id.tv_status_text);
        TextView moodText = findViewById(R.id.tv_mood_text);

        if (statusText == null || moodText == null) {
            return;
        }

        String today = DateUtils.getCurrentDate();
        CycleEntry entry = dbManager.getCycleEntry(userId, today);
        if (entry != null) {
            // Simple mapping: if we have notes or symptoms, we could infer status;
            // for now just show mood and note presence.
            String mood = entry.getMood();
            if (mood == null || mood.isEmpty()) {
                mood = "Neutral";
            }
            moodText.setText(mood);

            if (entry.getNotes() != null && !entry.getNotes().isEmpty()) {
                statusText.setText(getString(R.string.home_status_with_notes));
            } else {
                statusText.setText(getString(R.string.home_status_default));
            }
        } else {
            moodText.setText(getString(R.string.home_mood_default));
            statusText.setText(getString(R.string.home_status_default));
        }
    }

    private void navigateToCalendar() {
        Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void navigateToSignIn() {
        Toast.makeText(this, getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}


