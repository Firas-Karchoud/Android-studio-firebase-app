package com.example.classroomapp.teacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroomapp.R;
import com.example.classroomapp.models.Classroom;
import com.example.classroomapp.models.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ClassroomActivity extends AppCompatActivity implements ClassroomDialogFragment.ClassroomDialogListener {
    private static final String EXTRA_CLASSROOM = "extra_classroom";

    private TextView titleTextView;
    private TextView descriptionTextView;
    private RecyclerView postsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FloatingActionButton fabAddPost;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Classroom classroom;
    private PostAdapter postAdapter;
    private List<Post> posts;
    private ListenerRegistration postsListener;

    public static Intent createIntent(Context context, Classroom classroom) {
        Intent intent = new Intent(context, ClassroomActivity.class);
        intent.putExtra(EXTRA_CLASSROOM, classroom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        // Get classroom from intent
        classroom = getIntent().getParcelableExtra(EXTRA_CLASSROOM);
        if (classroom == null) {
            Toast.makeText(this, R.string.error_invalid_classroom, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Setup views and load data
        setupViews();
        setupActionBar();
        setupRecyclerView();
        loadPosts();
    }

    private void setupViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        fabAddPost = findViewById(R.id.fabAddPost);

        updateClassroomViews();

        // Setup FAB
        fabAddPost.setOnClickListener(v -> showAddPostDialog());
    }

    private void updateClassroomViews() {
        titleTextView.setText(classroom.getTitle());
        String description = classroom.getDescription();
        if (description != null && !description.isEmpty()) {
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(description);
        } else {
            descriptionTextView.setVisibility(View.GONE);
        }
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(classroom.getTitle());
        }
    }

    private void setupRecyclerView() {
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts, this::onPostClick);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);
    }

    private void loadPosts() {
        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        showProgress(true);

        // Remove any existing listener
        if (postsListener != null) {
            postsListener.remove();
        }

        // Set up real-time listener for posts
        postsListener = db.collection("classrooms")
                .document(classroom.getId())
                .collection("posts")
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

    private void showAddPostDialog() {
        PostDialogFragment dialog = PostDialogFragment.newInstance(classroom.getId());
        dialog.show(getSupportFragmentManager(), "AddPost");
    }

    private void onPostClick(Post post) {
        PostDialogFragment dialog = PostDialogFragment.newInstance(classroom.getId(), post);
        dialog.show(getSupportFragmentManager(), "EditPost");
    }

    private void deleteClassroom() {
        if (!classroom.getTeacherId().equals(mAuth.getCurrentUser().getUid())) {
            Toast.makeText(this, R.string.error_not_authorized, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_classroom_title)
                .setMessage(R.string.delete_classroom_message)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    showProgress(true);
                    db.collection("classrooms")
                            .document(classroom.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, R.string.classroom_deleted, Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, R.string.error_deleting_classroom, Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void editClassroom() {
        if (!classroom.getTeacherId().equals(mAuth.getCurrentUser().getUid())) {
            Toast.makeText(this, R.string.error_not_authorized, Toast.LENGTH_SHORT).show();
            return;
        }

        ClassroomDialogFragment dialog = ClassroomDialogFragment.newInstance(classroom);
        dialog.show(getSupportFragmentManager(), "EditClassroom");
    }

    @Override
    public void onClassroomUpdated(Classroom updatedClassroom) {
        // Update the local classroom object
        classroom = updatedClassroom;

        // Update UI
        updateClassroomViews();
        setupActionBar();

        // Show confirmation
        Toast.makeText(this, R.string.classroom_updated, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClassroomCreated(Classroom classroom) {
        // This method won't be used in this activity
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classroom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_edit) {
            editClassroom();
            return true;
        } else if (itemId == R.id.action_delete) {
            deleteClassroom();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postsListener != null) {
            postsListener.remove();
        }
    }
}