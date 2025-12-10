package tn.rnu.isi.mycycle.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tn.rnu.isi.mycycle.DateUtils;
import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.models.CycleEntry;
import tn.rnu.isi.mycycle.models.Symptom;

public class SymptomsActivity extends AppCompatActivity {

    private ImageView closeIcon;
    private TextView tvSelectedDate;

    private SeekBar seekCramps;
    private SeekBar seekFatigue;
    private SeekBar seekMoodSwings;
    private SeekBar seekBloating;

    private TextView moodHappy;
    private TextView moodNeutral;
    private TextView moodSad;
    private TextView moodAngry;

    private EditText etNotes;
    private Button btnSave;

    private DatabaseManager dbManager;
    private long userId = -1;

    private String selectedDate;   // yyyy-MM-dd
    private String selectedMood;   // 😊, 😐, 😔, 😡

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        dbManager = DatabaseManager.getInstance(this);
        userId = MainActivity.getCurrentUserId(this);
        if (userId == -1) {
            navigateToSignIn();
            return;
        }

        initializeViews();
        setupListeners();

        // Default date is today or passed from Intent
        String intentDate = getIntent().getStringExtra("date");
        if (!TextUtils.isEmpty(intentDate)) {
            selectedDate = intentDate;
        } else {
            selectedDate = DateUtils.getCurrentDate();
        }
        tvSelectedDate.setText(DateUtils.getDisplayDate(selectedDate));

        // Default mood
        selectMood(moodNeutral, "😐");

        // Load existing entry for today (if any)
        loadEntryForDate(selectedDate);
    }

    private void initializeViews() {
        closeIcon = findViewById(R.id.close_icon);
        tvSelectedDate = findViewById(R.id.tv_selected_date);

        seekCramps = findViewById(R.id.seek_cramps);
        seekFatigue = findViewById(R.id.seek_fatigue);
        seekMoodSwings = findViewById(R.id.seek_mood_swings);
        seekBloating = findViewById(R.id.seek_bloating);

        moodHappy = findViewById(R.id.mood_happy);
        moodNeutral = findViewById(R.id.mood_neutral);
        moodSad = findViewById(R.id.mood_sad);
        moodAngry = findViewById(R.id.mood_angry);

        etNotes = findViewById(R.id.notes);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupListeners() {
        closeIcon.setOnClickListener(v -> finish());

        tvSelectedDate.setOnClickListener(v -> showDatePicker());

        moodHappy.setOnClickListener(v -> selectMood(moodHappy, "😊"));
        moodNeutral.setOnClickListener(v -> selectMood(moodNeutral, "😐"));
        moodSad.setOnClickListener(v -> selectMood(moodSad, "😔"));
        moodAngry.setOnClickListener(v -> selectMood(moodAngry, "😡"));

        btnSave.setOnClickListener(v -> saveSymptoms());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(y, m, d);
                    String formatted = DateUtils.formatDate(selectedCal);
                    selectedDate = formatted;
                    tvSelectedDate.setText(DateUtils.getDisplayDate(formatted));
                    loadEntryForDate(selectedDate);
                },
                year, month, day
        );

        // Limit to past dates
        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, -1);
        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        dialog.show();
    }

    private void selectMood(TextView selectedView, String moodValue) {
        // Reset all backgrounds
        resetMoodBackgrounds();

        selectedMood = moodValue;
        selectedView.setBackgroundResource(R.drawable.card_bg);
    }

    private void resetMoodBackgrounds() {
        moodHappy.setBackgroundResource(R.drawable.card_bg);
        moodNeutral.setBackgroundColor(getResources().getColor(R.color.neutral_light_subtle));
        moodSad.setBackgroundColor(getResources().getColor(R.color.neutral_light_subtle));
        moodAngry.setBackgroundColor(getResources().getColor(R.color.neutral_light_subtle));
    }

    private void loadEntryForDate(String date) {
        CycleEntry entry = dbManager.getCycleEntry(userId, date);
        if (entry != null) {
            // Load mood
            String mood = entry.getMood();
            if ("😊".equals(mood)) {
                selectMood(moodHappy, "😊");
            } else if ("😔".equals(mood)) {
                selectMood(moodSad, "😔");
            } else if ("😡".equals(mood)) {
                selectMood(moodAngry, "😡");
            } else {
                selectMood(moodNeutral, "😐");
            }

            // Load notes
            if (!TextUtils.isEmpty(entry.getNotes())) {
                etNotes.setText(entry.getNotes());
            } else {
                etNotes.setText("");
            }

            // Load symptoms if available
            if (entry.getSymptoms() != null) {
                for (Symptom s : entry.getSymptoms()) {
                    String name = s.getName();
                    int intensity = s.getIntensity();
                    if ("Cramps".equalsIgnoreCase(name)) {
                        seekCramps.setProgress(intensity);
                    } else if ("Fatigue".equalsIgnoreCase(name)) {
                        seekFatigue.setProgress(intensity);
                    } else if ("Mood Swings".equalsIgnoreCase(name)) {
                        seekMoodSwings.setProgress(intensity);
                    } else if ("Bloating".equalsIgnoreCase(name)) {
                        seekBloating.setProgress(intensity);
                    }
                }
            }
        } else {
            // No entry: reset to defaults
            seekCramps.setProgress(0);
            seekFatigue.setProgress(0);
            seekMoodSwings.setProgress(0);
            seekBloating.setProgress(0);
            etNotes.setText("");
            selectMood(moodNeutral, "😐");
        }
    }

    private void saveSymptoms() {
        if (TextUtils.isEmpty(selectedDate)) {
            selectedDate = DateUtils.getCurrentDate();
        }

        if (TextUtils.isEmpty(selectedMood)) {
            selectedMood = "😐";
        }

        String notes = etNotes.getText().toString().trim();

        // Build symptom list
        List<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom(0, 0, "Cramps", seekCramps.getProgress()));
        symptoms.add(new Symptom(0, 0, "Fatigue", seekFatigue.getProgress()));
        symptoms.add(new Symptom(0, 0, "Mood Swings", seekMoodSwings.getProgress()));
        symptoms.add(new Symptom(0, 0, "Bloating", seekBloating.getProgress()));

        // Phase can be derived from DB
        String phase = dbManager.getCurrentPhase(userId);

        // Save in background
        btnSave.setEnabled(false);
        btnSave.setText(R.string.saving);

        new Thread(() -> {
            long entryId = dbManager.saveCycleEntry(userId, selectedDate, phase, selectedMood, notes, symptoms);

            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText(R.string.save);

                if (entryId > 0) {
                    Toast.makeText(SymptomsActivity.this, R.string.symptoms_saved, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SymptomsActivity.this, R.string.symptoms_save_error, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    private void navigateToSignIn() {
        Toast.makeText(this, getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SymptomsActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
