// StudentActivity.java
package com.example.classroomapp.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroomapp.R;
import com.example.classroomapp.auth.LoginActivity;
import com.example.classroomapp.models.Classroom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Classroom> classrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        setupViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load available classrooms
        loadClassrooms();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        classrooms = new ArrayList<>();
        adapter = new StudentAdapter(classrooms, this::onClassroomClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadClassrooms() {
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        showProgress(true);

        db.collection("classrooms")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    showProgress(false);

                    if (error != null) {
                        Toast.makeText(this, getString(R.string.error_loading_classrooms),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        classrooms.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Classroom classroom = document.toObject(Classroom.class);
                            classroom.setId(document.getId());
                            classrooms.add(classroom);
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyView();
                    }
                });
    }

    private void onClassroomClick(Classroom classroom) {
        // Navigate to classroom detail activity
        startActivity(StudentClassroomActivity.createIntent(this, classroom));
    }

    private void updateEmptyView() {
        if (classrooms.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            redirectToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}