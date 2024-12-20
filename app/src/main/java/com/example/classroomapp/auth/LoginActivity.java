package com.example.classroomapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classroomapp.R;
import com.example.classroomapp.student.StudentActivity;
import com.example.classroomapp.teacher.TeacherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signupLink;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        loginButton.setOnClickListener(v -> attemptLogin());
        signupLink.setOnClickListener(v -> navigateToSignup());
    }

    private void attemptLogin() {
        // Reset errors
        emailEditText.setError(null);
        passwordEditText.setError(null);

        // Get values
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            passwordEditText.requestFocus();
            return;
        }

        // Show progress
        showProgress(true);

        // Attempt login with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get user role from Firestore
                        String userId = task.getResult().getUser().getUid();
                        checkUserRoleAndRedirect(userId);
                    } else {
                        // Hide progress
                        showProgress(false);
                        // Show error message
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.error_login_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRoleAndRedirect(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showProgress(false);
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        redirectBasedOnRole(role);
                    } else {
                        // Handle error - user document doesn't exist
                        mAuth.signOut();
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.error_user_not_found),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    mAuth.signOut();
                    Toast.makeText(LoginActivity.this,
                            getString(R.string.error_login_failed),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;
        if ("teacher".equals(role)) {
            intent = new Intent(this, TeacherActivity.class);
        } else if ("student".equals(role)) {
            intent = new Intent(this, StudentActivity.class);
        } else {
            // Invalid role, sign out
            mAuth.signOut();
            Toast.makeText(this,
                    getString(R.string.error_invalid_role),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Start appropriate activity and clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);
        signupLink.setEnabled(!show);
    }
}