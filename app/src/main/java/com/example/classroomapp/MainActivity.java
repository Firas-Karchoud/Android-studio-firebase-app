package com.example.classroomapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classroomapp.auth.LoginActivity;
import com.example.classroomapp.student.StudentActivity;
import com.example.classroomapp.teacher.TeacherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in, redirect to login
            redirectToLogin();
            return;
        }

        // Check user role in Firestore
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        redirectBasedOnRole(role);
                    } else {
                        // User document doesn't exist, sign out and redirect to login
                        mAuth.signOut();
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    // Handle error - redirect to login
                    mAuth.signOut();
                    redirectToLogin();
                });
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;
        if ("teacher".equals(role)) {
            intent = new Intent(this, TeacherActivity.class);
        } else if ("student".equals(role)) {
            intent = new Intent(this, StudentActivity.class);
        } else {
            // Invalid role, sign out and redirect to login
            mAuth.signOut();
            redirectToLogin();
            return;
        }

        // Start appropriate activity and clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}