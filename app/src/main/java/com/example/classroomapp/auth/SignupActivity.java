package com.example.classroomapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classroomapp.R;
import com.example.classroomapp.student.StudentActivity;
import com.example.classroomapp.teacher.TeacherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private RadioGroup roleRadioGroup;
    private Button signupButton;
    private TextView loginLink;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        signupButton.setOnClickListener(v -> attemptSignup());
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void attemptSignup() {
        // Reset errors
        emailEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        // Get values
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Get selected role
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, getString(R.string.error_select_role), Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole.getTag().toString();

        // Validate input
        if (!validateInput(email, password, confirmPassword)) {
            return;
        }

        // Show progress
        showProgress(true);

        // Create account with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Save additional user data to Firestore
                        String userId = task.getResult().getUser().getUid();
                        saveUserData(userId, email, role);
                    } else {
                        // Hide progress
                        showProgress(false);
                        // Show error message
                        Toast.makeText(SignupActivity.this,
                                getString(R.string.error_signup_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            emailEditText.requestFocus();
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            emailEditText.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            passwordEditText.requestFocus();
            valid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            passwordEditText.requestFocus();
            valid = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_passwords_dont_match));
            confirmPasswordEditText.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void saveUserData(String userId, String email, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("role", role);
        user.put("createdAt", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(SignupActivity.this,
                            getString(R.string.signup_successful),
                            Toast.LENGTH_SHORT).show();
                    redirectBasedOnRole(role);
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    mAuth.getCurrentUser().delete(); // Delete the auth user if Firestore save fails
                    Toast.makeText(SignupActivity.this,
                            getString(R.string.error_signup_failed),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;
        if ("teacher".equals(role)) {
            intent = new Intent(this, TeacherActivity.class);
        } else {
            intent = new Intent(this, StudentActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        finish(); // Return to login activity
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!show);
        loginLink.setEnabled(!show);
    }
}