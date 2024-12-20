package com.example.classroomapp.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity implements ClassroomDialogFragment.ClassroomDialogListener {
    private RecyclerView recyclerView;
    private TeacherAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FloatingActionButton fabAddClassroom;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Classroom> classrooms;
    private ListenerRegistration classroomListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        setupViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load teacher's classrooms
        loadClassrooms();

    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        fabAddClassroom = findViewById(R.id.fabAddClassroom);

        fabAddClassroom.setOnClickListener(v -> showCreateClassroomDialog());
    }

    private void setupRecyclerView() {
        classrooms = new ArrayList<>();
        adapter = new TeacherAdapter(classrooms, this::onClassroomClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }




    private void loadClassrooms() {
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        showProgress(true);
        String teacherId = mAuth.getCurrentUser().getUid();

        // Remove any existing listener
        if (classroomListener != null) {
            classroomListener.remove();
        }

        // Set up real-time listener
        classroomListener = db.collection("classrooms")
                .whereEqualTo("teacherId", teacherId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    showProgress(false);


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

    private void showCreateClassroomDialog() {
        ClassroomDialogFragment dialog = ClassroomDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "CreateClassroom");
    }

    @Override
    public void onClassroomCreated(Classroom classroom) {
        // The classroom will be automatically added to the list
        // through the Firestore snapshot listener
    }

    @Override
    public void onClassroomUpdated(Classroom classroom) {
        // The classroom will be automatically updated in the list
        // through the Firestore snapshot listener
    }

    private void onClassroomClick(Classroom classroom) {
        startActivity(ClassroomActivity.createIntent(this, classroom));
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
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classroomListener != null) {
            classroomListener.remove();
        }
    }

    @Override
        protected void onStart() {
        super.onStart();

        Log.d("firas","Start");    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("firas","Stop");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("firas","Resume");
    }

    @Override
    protected void onPause() {
        Log.d("firas","Pause");
        super.onPause();
    }

0


}