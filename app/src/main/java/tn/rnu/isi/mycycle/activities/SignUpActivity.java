package tn.rnu.isi.mycycle.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    // Step 1 views
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnContinue;
    private ImageView btnBack;
    private TextView tvLoginRedirect;
    
    // Step 2 views
    private EditText etLastPeriodDate;
    private EditText etPeriodDuration;
    private EditText etCycleDuration;
    private Button btnCramps;
    private Button btnHeadache;
    private Button btnMoodSwings;
    private Button btnBloating;
    private Button btnContinueStep2;
    private TextView tvSkip;
    private ImageView btnBackStep2;
    
    private DatabaseManager dbManager;
    private static final int STEP_1 = 1;
    private static final int STEP_2 = 2;
    private int currentStep = STEP_1;

    // User data to pass between steps
    private String userName;
    private String userEmail;
    private String userPassword;
    private long userId = -1;
    
    // Step 2 data
    private String lastPeriodStart = "";
    private int periodDuration = 5;
    private int cycleLength = 28;
    private List<String> selectedSymptoms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize database manager
        dbManager = DatabaseManager.getInstance(this);
        
        // Check if we're coming from step 2 (back button) or from Intent
        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("currentStep", STEP_1);
            userId = savedInstanceState.getLong("userId", -1);
            userName = savedInstanceState.getString("userName", "");
            userEmail = savedInstanceState.getString("userEmail", "");
            userPassword = savedInstanceState.getString("userPassword", "");
            lastPeriodStart = savedInstanceState.getString("lastPeriodStart", "");
            periodDuration = savedInstanceState.getInt("periodDuration", 5);
            cycleLength = savedInstanceState.getInt("cycleLength", 28);
        } else {
            // Check if coming from Intent (e.g., from step 1)
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("step")) {
                currentStep = intent.getIntExtra("step", STEP_1);
                userId = intent.getLongExtra("userId", -1);
                userName = intent.getStringExtra("userName");
                userEmail = intent.getStringExtra("userEmail");
            }
        }
        
        // Load appropriate layout based on step
        loadStepLayout();
    }

    /**
     * Load the appropriate layout based on current step
     */
    private void loadStepLayout() {
        if (currentStep == STEP_1) {
            setContentView(R.layout.activity_sign_up_step_1);
            initializeStep1Views();
            setupStep1Listeners();
        } else if (currentStep == STEP_2) {
            setContentView(R.layout.activity_sign_up_step_2);
            initializeStep2Views();
            setupStep2Listeners();
        }
    }

    /**
     * Initialize views for Step 1
     */
    private void initializeStep1Views() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnContinue = findViewById(R.id.btn_continue);
        btnBack = findViewById(R.id.btn_back);
        tvLoginRedirect = findViewById(R.id.tv_login_redirect);
    }

    /**
     * Set up click listeners for Step 1
     */
    private void setupStep1Listeners() {
        // Continue button - proceed to step 2
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStep1Continue();
            }
        });

        // Back button - go back to SignInActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });

        // Login redirect - go to SignInActivity
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });
    }

    /**
     * Initialize views for Step 2
     */
    private void initializeStep2Views() {
        etLastPeriodDate = findViewById(R.id.et_last_period_date);
        etPeriodDuration = findViewById(R.id.et_period_duration);
        etCycleDuration = findViewById(R.id.et_cycle_duration);
        btnCramps = findViewById(R.id.btn_cramps);
        btnHeadache = findViewById(R.id.btn_headache);
        btnMoodSwings = findViewById(R.id.btn_mood_swings);
        btnBloating = findViewById(R.id.btn_bloating);
        btnContinueStep2 = findViewById(R.id.btn_continue);
        tvSkip = findViewById(R.id.tv_skip);
        btnBackStep2 = findViewById(R.id.btn_back);
        
        // Set default values
        etPeriodDuration.setText(String.valueOf(periodDuration));
        etCycleDuration.setText(String.valueOf(cycleLength));
    }

    /**
     * Set up click listeners for Step 2
     */
    private void setupStep2Listeners() {
        // Date picker for last period date
        etLastPeriodDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Symptom buttons - toggle selection
        btnCramps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSymptom("Cramps", btnCramps);
            }
        });

        btnHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSymptom("Headache", btnHeadache);
            }
        });

        btnMoodSwings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSymptom("Mood Swings", btnMoodSwings);
            }
        });

        btnBloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSymptom("Bloating", btnBloating);
            }
        });

        // Continue button
        btnContinueStep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStep2Continue();
            }
        });

        // Skip button
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSkip();
            }
        });

        // Back button - go back to step 1
        btnBackStep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToStep1();
            }
        });
    }

    /**
     * Show date picker dialog for last period date
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Month is 0-indexed in Calendar, but we need 1-indexed for our format
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(selectedYear, selectedMonth, selectedDay);
                
                // Format date as yyyy-MM-dd
                String formattedDate = DateUtils.formatDate(selectedDate);
                lastPeriodStart = formattedDate;
                etLastPeriodDate.setText(DateUtils.getDisplayDate(formattedDate));
            },
            year, month, day
        );
        
        // Set max date to today (can't select future dates)
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        
        // Set min date to 1 year ago (reasonable range)
        calendar.add(Calendar.YEAR, -1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        
        datePickerDialog.show();
    }

    /**
     * Toggle symptom selection
     */
    private void toggleSymptom(String symptomName, Button button) {
        if (selectedSymptoms.contains(symptomName)) {
            // Deselect
            selectedSymptoms.remove(symptomName);
            button.setBackgroundTintList(getResources().getColorStateList(R.color.primary_light));
            button.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            // Select
            selectedSymptoms.add(symptomName);
            button.setBackgroundTintList(getResources().getColorStateList(R.color.symptom));
            button.setTextColor(getResources().getColor(R.color.primary));
        }
    }

    /**
     * Handle Step 1 continue button click
     */
    private void handleStep1Continue() {
        // Get input values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateStep1Inputs(name, email, password)) {
            return;
        }

        // Disable button to prevent multiple clicks
        btnContinue.setEnabled(false);
        btnContinue.setText("Creating account...");

        // Register user in background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Attempt to register user
                long newUserId = dbManager.registerUser(name, email, password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnContinue.setEnabled(true);
                        btnContinue.setText("Continue");

                        if (newUserId > 0) {
                            // Registration successful
                            handleStep1Success(newUserId, name, email, password);
                        } else {
                            // Registration failed (email already exists)
                            handleStep1Failure();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Validate Step 1 inputs
     */
    private boolean validateStep1Inputs(String name, String email, String password) {
        boolean isValid = true;

        // Validate name
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            isValid = false;
        } else if (name.length() < 2) {
            etName.setError("Name must be at least 2 characters");
            etName.requestFocus();
            isValid = false;
        } else {
            etName.setError(null);
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            isValid = false;
        } else {
            etPassword.setError(null);
        }

        return isValid;
    }

    /**
     * Handle successful Step 1 registration
     */
    private void handleStep1Success(long newUserId, String name, String email, String password) {
        // Store user data for step 2
        userId = newUserId;
        userName = name;
        userEmail = email;
        userPassword = password;

        // Navigate to Step 2 (Cycle Setup)
        navigateToStep2();
    }

    /**
     * Handle Step 1 registration failure
     */
    private void handleStep1Failure() {
        Toast.makeText(this, "Email already registered. Please use a different email or sign in.", Toast.LENGTH_LONG).show();
        etEmail.requestFocus();
        etEmail.selectAll();
    }

    /**
     * Navigate to Step 2 (Cycle Setup)
     */
    private void navigateToStep2() {
        currentStep = STEP_2;
        loadStepLayout();
    }

    /**
     * Handle Step 2 continue button click
     */
    private void handleStep2Continue() {
        // Get input values
        String periodDurationStr = etPeriodDuration.getText().toString().trim();
        String cycleDurationStr = etCycleDuration.getText().toString().trim();

        // Validate inputs
        if (!validateStep2Inputs(periodDurationStr, cycleDurationStr)) {
            return;
        }

        // Parse values
        periodDuration = Integer.parseInt(periodDurationStr);
        cycleLength = Integer.parseInt(cycleDurationStr);

        // Disable button
        btnContinueStep2.setEnabled(false);
        btnContinueStep2.setText("Saving...");

        // Save preferences in background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Convert symptoms list to comma-separated string
                String trackedSymptoms = TextUtils.join(",", selectedSymptoms);
                
                // If no last period date selected, use a default (30 days ago)
                if (TextUtils.isEmpty(lastPeriodStart)) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, -30);
                    lastPeriodStart = DateUtils.formatDate(cal);
                }

                // Save user preferences
                boolean success = dbManager.saveUserPreferences(
                    userId,
                    cycleLength,
                    periodDuration,
                    lastPeriodStart,
                    trackedSymptoms
                );

                // Also add the period to periods table
                if (success && !TextUtils.isEmpty(lastPeriodStart)) {
                    String endDate = DateUtils.addDays(lastPeriodStart, periodDuration);
                    dbManager.addPeriod(userId, lastPeriodStart, endDate, periodDuration, "Medium");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnContinueStep2.setEnabled(true);
                        btnContinueStep2.setText("Continue");

                        if (success) {
                            handleStep2Success();
                        } else {
                            handleStep2Failure();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Validate Step 2 inputs
     */
    private boolean validateStep2Inputs(String periodDurationStr, String cycleDurationStr) {
        boolean isValid = true;

        // Validate period duration
        if (TextUtils.isEmpty(periodDurationStr)) {
            etPeriodDuration.setError("Period duration is required");
            etPeriodDuration.requestFocus();
            isValid = false;
        } else {
            try {
                int duration = Integer.parseInt(periodDurationStr);
                if (duration < 1 || duration > 15) {
                    etPeriodDuration.setError("Period duration should be between 1 and 15 days");
                    etPeriodDuration.requestFocus();
                    isValid = false;
                } else {
                    etPeriodDuration.setError(null);
                }
            } catch (NumberFormatException e) {
                etPeriodDuration.setError("Please enter a valid number");
                etPeriodDuration.requestFocus();
                isValid = false;
            }
        }

        // Validate cycle duration
        if (TextUtils.isEmpty(cycleDurationStr)) {
            etCycleDuration.setError("Cycle duration is required");
            etCycleDuration.requestFocus();
            isValid = false;
        } else {
            try {
                int duration = Integer.parseInt(cycleDurationStr);
                if (duration < 21 || duration > 45) {
                    etCycleDuration.setError("Cycle duration should be between 21 and 45 days");
                    etCycleDuration.requestFocus();
                    isValid = false;
                } else {
                    etCycleDuration.setError(null);
                }
            } catch (NumberFormatException e) {
                etCycleDuration.setError("Please enter a valid number");
                etCycleDuration.requestFocus();
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Handle successful Step 2 completion
     */
    private void handleStep2Success() {
        // Save user session
        MainActivity.saveUserSession(this, userId, userName);
        
        Toast.makeText(this, "Setup complete! Welcome to MyCycle!", Toast.LENGTH_SHORT).show();
        
        // Navigate to HomeActivity
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Handle Step 2 failure
     */
    private void handleStep2Failure() {
        Toast.makeText(this, "Error saving preferences. Please try again.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handle skip button - save with default values
     */
    private void handleSkip() {
        // Use default values
        lastPeriodStart = DateUtils.getCurrentDate();
        periodDuration = 5;
        cycleLength = 28;
        selectedSymptoms.clear();

        // Save with defaults
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = dbManager.saveUserPreferences(
                    userId,
                    cycleLength,
                    periodDuration,
                    lastPeriodStart,
                    ""
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            // Save user session
                            MainActivity.saveUserSession(SignUpActivity.this, userId, userName);
                            
                            Toast.makeText(SignUpActivity.this, "Setup skipped. You can configure this later in settings.", Toast.LENGTH_LONG).show();
                            
                            // Navigate to HomeActivity
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Go back to Step 1
     */
    private void goBackToStep1() {
        currentStep = STEP_1;
        loadStepLayout();
    }

    /**
     * Navigate back to Sign In screen
     */
    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentStep", currentStep);
        outState.putLong("userId", userId);
        outState.putString("userName", userName);
        outState.putString("userEmail", userEmail);
        outState.putString("userPassword", userPassword);
        outState.putString("lastPeriodStart", lastPeriodStart);
        outState.putInt("periodDuration", periodDuration);
        outState.putInt("cycleLength", cycleLength);
    }

    @Override
    public void onBackPressed() {
        if (currentStep == STEP_1) {
            // Go back to SignInActivity
            navigateToSignIn();
        } else {
            // Go back to previous step
            goBackToStep1();
        }
    }
}
