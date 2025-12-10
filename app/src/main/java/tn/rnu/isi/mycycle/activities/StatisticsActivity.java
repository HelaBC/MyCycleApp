package tn.rnu.isi.mycycle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.models.SymptomFrequency;
import tn.rnu.isi.mycycle.utils.DateUtils;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private long userId = -1;

    // Views
    private LinearLayout symptomFrequencyList;
    private TextView tvNoSymptoms;
    private TextView tvAvgCycleLength;
    private TextView tvCycleBasedOn;
    private TextView tvMoodEmoji;
    private TextView tvMoodDescription;
    private LinearLayout phaseCorrelationsList;
    private TextView tvNoCorrelations;

    // Navigation
    private LinearLayout navHome;
    private LinearLayout navCalendar;
    private LinearLayout navStats;
    private LinearLayout navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbManager = DatabaseManager.getInstance(this);
        userId = MainActivity.getCurrentUserId(this);


        initializeViews();
        setupNavigation();
        loadStatistics();
    }

    private void initializeViews() {
        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> finish());

        symptomFrequencyList = findViewById(R.id.symptom_frequency_list);
        tvNoSymptoms = findViewById(R.id.tv_no_symptoms);
        tvAvgCycleLength = findViewById(R.id.tv_avg_cycle_length);
        tvCycleBasedOn = findViewById(R.id.tv_cycle_based_on);
        tvMoodEmoji = findViewById(R.id.tv_mood_emoji);
        tvMoodDescription = findViewById(R.id.tv_mood_description);
        phaseCorrelationsList = findViewById(R.id.phase_correlations_list);
        tvNoCorrelations = findViewById(R.id.tv_no_correlations);

        navHome = findViewById(R.id.nav_home);
        navCalendar = findViewById(R.id.nav_calendar);
        navStats = findViewById(R.id.nav_stats);
        navSettings = findViewById(R.id.nav_settings);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navStats.setOnClickListener(v -> {
            // Already on stats page
        });

        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadStatistics() {
        // Calculate date range: last 3 months
        Calendar calendar = Calendar.getInstance();
        String endDate = DateUtils.getCurrentDate();
        calendar.add(Calendar.MONTH, -3);
        String startDate = DateUtils.formatDate(calendar);

        // Load symptom frequency
        loadSymptomFrequency(startDate, endDate);

        // Load average cycle length
        loadAverageCycleLength();

        // Load most common mood
        loadMostCommonMood(startDate, endDate);

        // Load symptom correlations by phase
        loadSymptomCorrelations(startDate, endDate);
    }

    private void loadSymptomFrequency(String startDate, String endDate) {
        List<SymptomFrequency> frequencies = dbManager.getSymptomFrequency(userId, startDate, endDate);

        if (frequencies == null || frequencies.isEmpty()) {
            tvNoSymptoms.setVisibility(View.VISIBLE);
            symptomFrequencyList.setVisibility(View.GONE);
            return;
        }

        tvNoSymptoms.setVisibility(View.GONE);
        symptomFrequencyList.setVisibility(View.VISIBLE);
        symptomFrequencyList.removeAllViews();

        // Find max count for percentage calculation
        int maxCount = 0;
        for (SymptomFrequency freq : frequencies) {
            if (freq.getCount() > maxCount) {
                maxCount = freq.getCount();
            }
        }

        // Create views for each symptom
        for (SymptomFrequency freq : frequencies) {
            View itemView = getLayoutInflater().inflate(R.layout.item_symptom_frequency, symptomFrequencyList, false);
            
            TextView symptomName = itemView.findViewById(R.id.tv_symptom_name);
            ProgressBar progressBar = itemView.findViewById(R.id.progress_symptom);
            TextView symptomCount = itemView.findViewById(R.id.tv_symptom_count);

            symptomName.setText(freq.getSymptomName());
            int progress = maxCount > 0 ? (int) ((freq.getCount() * 100.0) / maxCount) : 0;
            progressBar.setProgress(progress);
            symptomCount.setText(String.valueOf(freq.getCount()));

            symptomFrequencyList.addView(itemView);
        }
    }

    private void loadAverageCycleLength() {
        double avgCycle = dbManager.getAverageCycleLength(userId);
        if (avgCycle > 0) {
            tvAvgCycleLength.setText(String.format("%.0f days", avgCycle));
        } else {
            tvAvgCycleLength.setText("-- days");
        }
    }

    private void loadMostCommonMood(String startDate, String endDate) {
        String[] moodData = dbManager.getMostCommonMood(userId, startDate, endDate);
        
        if (moodData != null && moodData.length == 2) {
            String mood = moodData[0];
            int count = Integer.parseInt(moodData[1]);
            
            tvMoodEmoji.setText(mood);
            
            // Get mood description
            String moodDescription = getMoodDescription(mood);
            String description = getString(R.string.mood_times, moodDescription, count);
            tvMoodDescription.setText(description);
        } else {
            tvMoodEmoji.setText("--");
            tvMoodDescription.setText(getString(R.string.no_mood_data));
        }
    }

    private String getMoodDescription(String moodEmoji) {
        switch (moodEmoji) {
            case "😊":
                return "happy";
            case "😐":
                return "neutral";
            case "😔":
                return "sad";
            case "😡":
                return "angry";
            default:
                return moodEmoji;
        }
    }

    private void loadSymptomCorrelations(String startDate, String endDate) {
        Map<String, List<String>> correlations = dbManager.getSymptomCorrelationsByPhase(userId, startDate, endDate);

        if (correlations == null || correlations.isEmpty()) {
            tvNoCorrelations.setVisibility(View.VISIBLE);
            phaseCorrelationsList.setVisibility(View.GONE);
            return;
        }

        tvNoCorrelations.setVisibility(View.GONE);
        phaseCorrelationsList.setVisibility(View.VISIBLE);
        phaseCorrelationsList.removeAllViews();

        // Define phase order
        String[] phases = {"Menstrual", "Follicular", "Ovulation", "Luteal"};

        boolean isFirst = true;
        for (String phase : phases) {
            List<String> symptoms = correlations.get(phase);
            if (symptoms != null && !symptoms.isEmpty()) {
                if (!isFirst) {
                    // Add separator before each phase (except first)
                    View separator = new View(this);
                    separator.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) (1 * getResources().getDisplayMetrics().density)
                    ));
                    separator.setBackgroundColor(getResources().getColor(R.color.neutral_dark, null));
                    phaseCorrelationsList.addView(separator);
                }
                addPhaseCorrelationView(phase, symptoms);
                isFirst = false;
            }
        }
    }

    private void addPhaseCorrelationView(String phase, List<String> symptoms) {
        View phaseView = getLayoutInflater().inflate(R.layout.item_phase_correlation, phaseCorrelationsList, false);

        TextView phaseName = phaseView.findViewById(R.id.tv_phase_name);
        LinearLayout symptomsContainer = phaseView.findViewById(R.id.symptoms_container);

        phaseName.setText(phase);

        // Add symptom tags
        for (String symptom : symptoms) {
            TextView symptomTag = new TextView(this);
            symptomTag.setText(symptom);
            symptomTag.setTextSize(12);
            symptomTag.setPadding(
                    (int) (6 * getResources().getDisplayMetrics().density),
                    (int) (2 * getResources().getDisplayMetrics().density),
                    (int) (6 * getResources().getDisplayMetrics().density),
                    (int) (2 * getResources().getDisplayMetrics().density)
            );
            symptomTag.setBackgroundColor(getResources().getColor(R.color.symptom, null));
            symptomTag.setTextColor(getResources().getColor(R.color.text_primary, null));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd((int) (4 * getResources().getDisplayMetrics().density));
            symptomTag.setLayoutParams(params);

            symptomsContainer.addView(symptomTag);
        }

        phaseCorrelationsList.addView(phaseView);
    }
}
