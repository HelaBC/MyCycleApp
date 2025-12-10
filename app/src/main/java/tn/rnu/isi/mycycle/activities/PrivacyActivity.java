package tn.rnu.isi.mycycle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.models.CycleEntry;
import tn.rnu.isi.mycycle.models.Period;
import tn.rnu.isi.mycycle.models.User;
import tn.rnu.isi.mycycle.models.UserPreferences;

public class PrivacyActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        dbManager = DatabaseManager.getInstance(this);
        userId = MainActivity.getCurrentUserId(this);

        ImageView btnBack = findViewById(R.id.btn_back);
        Button btnExport = findViewById(R.id.btn_export_data);
        Button btnDelete = findViewById(R.id.btn_delete_data);

        btnBack.setOnClickListener(v -> finish());
        btnExport.setOnClickListener(v -> exportData());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void exportData() {
        try {
            // Get user data
            User user = dbManager.getUser(userId);
            UserPreferences prefs = dbManager.getUserPreferences(userId);
            List<Period> periods = dbManager.getPeriods(userId);

            // Get cycle entries for last 3 months
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String endDate = sdf.format(new Date());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -3);
            String startDate = sdf.format(cal.getTime());
            List<CycleEntry> entries = dbManager.getCycleEntries(userId, startDate, endDate);

            // Build JSON
            JSONObject exportData = new JSONObject();

            // User info
            if (user != null) {
                JSONObject userJson = new JSONObject();
                userJson.put("name", user.getName());
                userJson.put("email", user.getEmail());
                userJson.put("created_at", user.getCreatedAt());
                exportData.put("user", userJson);
            }

            // Preferences
            if (prefs != null) {
                JSONObject prefsJson = new JSONObject();
                prefsJson.put("cycle_length", prefs.getCycleLength());
                prefsJson.put("period_length", prefs.getPeriodLength());
                prefsJson.put("last_period_start", prefs.getLastPeriodStart());
                exportData.put("preferences", prefsJson);
            }

            // Periods
            JSONArray periodsArray = new JSONArray();
            for (Period period : periods) {
                JSONObject periodJson = new JSONObject();
                periodJson.put("start_date", period.getStartDate());
                periodJson.put("end_date", period.getEndDate());
                periodJson.put("duration", period.getDuration());
                periodJson.put("flow", period.getFlow());
                periodsArray.put(periodJson);
            }
            exportData.put("periods", periodsArray);

            // Cycle entries
            JSONArray entriesArray = new JSONArray();
            for (CycleEntry entry : entries) {
                JSONObject entryJson = new JSONObject();
                entryJson.put("date", entry.getDate());
                entryJson.put("phase", entry.getPhase());
                entryJson.put("mood", entry.getMood());
                entryJson.put("notes", entry.getNotes());
                entriesArray.put(entryJson);
            }
            exportData.put("cycle_entries", entriesArray);

            // Write to file
            File exportDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "MyCycle");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File exportFile = new File(exportDir, "mycycle_export_" + timestamp + ".json");

            FileWriter writer = new FileWriter(exportFile);
            writer.write(exportData.toString(2)); // Pretty print with indent
            writer.close();

            Toast.makeText(this, "Data exported to: " + exportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete All Data")
                .setMessage("Are you sure you want to delete all your data? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAllData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAllData() {
        // Clear user session
        MainActivity.clearUserSession(this);

        // Navigate to sign-in
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(this, "All data has been deleted. Please create a new account.", Toast.LENGTH_LONG).show();
        finish();
    }
}
