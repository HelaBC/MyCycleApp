package tn.rnu.isi.mycycle.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.models.User;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;
    private Button btnCreateAccount;
    private TextView tvForgotPassword;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize database manager
        dbManager = DatabaseManager.getInstance(this);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initialize all views from layout
     */
    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    /**
     * Set up click listeners for buttons
     */
    private void setupClickListeners() {
        // Sign In button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Create Account button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUp();
            }
        });

        // Forgot Password
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
    }

    /**
     * Attempt to login with email and password
     */
    private void attemptLogin() {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Disable button to prevent multiple clicks
        btnSignIn.setEnabled(false);
        btnSignIn.setText("Signing in...");

        // Perform login in background (simple synchronous call for now)
        // In production, you might want to use AsyncTask or coroutines
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Attempt login
                long userId = dbManager.loginUser(email, password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSignIn.setEnabled(true);
                        btnSignIn.setText("Sign In");

                        if (userId > 0) {
                            // Login successful
                            handleLoginSuccess(userId);
                        } else {
                            // Login failed
                            handleLoginFailure();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Validate email and password inputs
     */
    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

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
     * Handle successful login
     */
    private void handleLoginSuccess(long userId) {
        // Get user information
        User user = dbManager.getUser(userId);
        
        if (user != null) {
            // Save user session
            MainActivity.saveUserSession(this, userId, user.getName());
            
            // Show success message
            Toast.makeText(this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
            
            // Navigate to HomeActivity
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            // User not found (shouldn't happen, but handle it)
            Toast.makeText(this, "Error retrieving user information", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle login failure
     */
    private void handleLoginFailure() {
        Toast.makeText(this, "Invalid email or password. Please try again.", Toast.LENGTH_LONG).show();
        // Clear password field
        etPassword.setText("");
        etPassword.requestFocus();
    }

    /**
     * Navigate to Sign Up screen
     */
    private void navigateToSignUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Handle forgot password click
     * TODO: Implement forgot password functionality
     */
    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address first", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        // Check if email exists
        if (dbManager.emailExists(email)) {
            // TODO: Implement password reset functionality
            // For now, just show a message
            Toast.makeText(this, "Password reset feature coming soon!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Email not found. Please check your email address.", Toast.LENGTH_LONG).show();
        }
    }


}
