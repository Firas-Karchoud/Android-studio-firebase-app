package com.example.classroomapp.student;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.classroomapp.models.Classroom;
import com.example.classroomapp.models.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class StudentClassroomActivity extends AppCompatActivity {
    private static final String EXTRA_CLASSROOM = "extra_classroom";

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView teacherNameTextView;
    private RecyclerView postsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;

    private FirebaseFirestore db;
    private Classroom classroom;
    private StudentPostAdapter postAdapter;
    private List<Post> posts;

    public static Intent createIntent(Context context, Classroom classroom) {
        Intent intent = new Intent(context, StudentClassroomActivity.class);
        intent.putExtra(EXTRA_CLASSROOM, classroom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classroom);

        // Get classroom from intent
        classroom = getIntent().getParcelableExtra(EXTRA_CLASSROOM);
        if (classroom == null) {
            Toast.makeText(this, R.string.error_invalid_classroom, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup views and load data
        setupViews();
        setupActionBar();
        setupRecyclerView();
        loadPosts();
    }

    private void setupViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        teacherNameTextView = findViewById(R.id.teacherNameTextView);
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        // Set classroom details
        titleTextView.setText(classroom.getTitle());
        descriptionTextView.setText(classroom.getDescription());
        teacherNameTextView.setText(getString(R.string.teacher_name, classroom.getTeacherName()));
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(classroom.getTitle());
        }
    }

    private void setupRecyclerView() {
        posts = new ArrayList<>();
        postAdapter = new StudentPostAdapter(posts);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);
    }

    private void loadPosts() {
        showProgress(true);

        db.collection("classrooms")
                .document(classroom.getId())
                .collection("posts")
                .whereEqualTo("published", true)  // Only show published posts
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    showProgress(false);

                    if (error != null) {
                        Toast.makeText(this, R.string.error_loading_posts, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        posts.clear();
                        value.forEach(doc -> {
                            Post post = doc.toObject(Post.class);
                            post.setId(doc.getId());
                            posts.add(post);
                        });
                        postAdapter.notifyDataSetChanged();
                        updateEmptyView();
                    }
                });
    }

    private void updateEmptyView() {
        if (posts.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            postsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            postsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}