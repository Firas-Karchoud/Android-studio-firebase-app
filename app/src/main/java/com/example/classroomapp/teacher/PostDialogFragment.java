package com.example.classroomapp.teacher;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.classroomapp.R;
import com.example.classroomapp.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostDialogFragment extends DialogFragment {
    private EditText titleEditText;
    private EditText contentEditText;
    private ProgressBar progressBar;
    private Button positiveButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String classroomId;
    private Post existingPost;

    public static PostDialogFragment newInstance(String classroomId) {
        PostDialogFragment fragment = new PostDialogFragment();
        Bundle args = new Bundle();
        args.putString("classroomId", classroomId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostDialogFragment newInstance(String classroomId, Post post) {
        PostDialogFragment fragment = new PostDialogFragment();
        Bundle args = new Bundle();
        args.putString("classroomId", classroomId);
        args.putParcelable("post", post);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get arguments
        if (getArguments() != null) {
            classroomId = getArguments().getString("classroomId");
            existingPost = getArguments().getParcelable("post");
        }

        // Inflate layout
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_post, null);

        // Initialize views
        titleEditText = view.findViewById(R.id.titleEditText);
        contentEditText = view.findViewById(R.id.contentEditText);
        progressBar = view.findViewById(R.id.progressBar);

        // Set existing data if editing
        if (existingPost != null) {
            titleEditText.setText(existingPost.getTitle());
            contentEditText.setText(existingPost.getContent());
        }

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(existingPost != null ? R.string.edit_post : R.string.create_post)
                .setView(view)
                .setPositiveButton(existingPost != null ? R.string.action_save : R.string.action_post, null)
                .setNegativeButton(R.string.action_cancel, (dialog, which) -> dismiss());

        AlertDialog dialog = builder.create();

        // Override positive button to prevent automatic dismiss on error
        dialog.setOnShowListener(dialogInterface -> {
            positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if (validateInput()) {
                    if (existingPost != null) {
                        updatePost();
                    } else {
                        createPost();
                    }
                }
            });
        });

        return dialog;
    }

    private boolean validateInput() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleEditText.setError(getString(R.string.error_field_required));
            titleEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(content)) {
            contentEditText.setError(getString(R.string.error_field_required));
            contentEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void createPost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("teacherId", auth.getCurrentUser().getUid());
        post.put("teacherName", auth.getCurrentUser().getEmail());
        post.put("createdAt", System.currentTimeMillis());
        post.put("published", true);

        showProgress(true);
        db.collection("classrooms")
                .document(classroomId)
                .collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    showProgress(false);
                    Toast.makeText(getContext(), R.string.post_created, Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(getContext(), R.string.error_creating_post, Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("content", content);
        updates.put("updatedAt", System.currentTimeMillis());

        showProgress(true);
        db.collection("classrooms")
                .document(classroomId)
                .collection("posts")
                .document(existingPost.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(getContext(), R.string.post_updated, Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(getContext(), R.string.error_updating_post, Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        positiveButton.setEnabled(!show);
    }
}