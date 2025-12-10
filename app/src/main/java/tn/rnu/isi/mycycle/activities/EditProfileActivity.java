package tn.rnu.isi.mycycle.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.rnu.isi.mycycle.R;
import tn.rnu.isi.mycycle.database.DatabaseManager;
import tn.rnu.isi.mycycle.models.User;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private Button btnSave;
    private ImageView btnBack;

    private DatabaseManager dbManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbManager = DatabaseManager.getInstance(this);
        userId = MainActivity.getCurrentUserId(this);

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadUserData() {
        if (userId != -1) {
            User user = dbManager.getUser(userId);
            if (user != null) {
                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        // Check if email is already taken by another user
        if (dbManager.emailExists(email)) {
            User currentUser = dbManager.getUser(userId);
            if (currentUser != null && !currentUser.getEmail().equals(email)) {
                etEmail.setError("Email already exists");
                return;
            }
        }

        // Update user profile in database
        boolean success = dbManager.updateUserProfile(userId, name, email);

        if (success) {
            // Update session data
            MainActivity.saveUserSession(this, userId, name);
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
