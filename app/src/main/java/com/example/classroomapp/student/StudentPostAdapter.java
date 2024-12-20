package com.example.classroomapp.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroomapp.R;
import com.example.classroomapp.models.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentPostAdapter extends RecyclerView.Adapter<StudentPostAdapter.PostViewHolder> {
    private final List<Post> posts;
    private final SimpleDateFormat dateFormat;

    public StudentPostAdapter(List<Post> posts) {
        this.posts = posts;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_student, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (position >= 0 && position < posts.size()) {
            Post post = posts.get(position);
            holder.bind(post);
        }
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView contentTextView;
        private final TextView teacherNameTextView;
        private final TextView dateTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize all views
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            teacherNameTextView = itemView.findViewById(R.id.teacherNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(Post post) {
            if (post != null) {
                // Safely set text with null checks
                titleTextView.setText(post.getTitle() != null ? post.getTitle() : "");
                contentTextView.setText(post.getContent() != null ? post.getContent() : "");

                String teacherName = post.getTeacherName() != null ? post.getTeacherName() : "";
                teacherNameTextView.setText(itemView.getContext()
                        .getString(R.string.posted_by, teacherName));

                // Format date
                String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(new Date(post.getCreatedAt()));

                if (post.isEdited()) {
                    dateTextView.setText(itemView.getContext()
                            .getString(R.string.edited_on, formattedDate));
                } else {
                    dateTextView.setText(itemView.getContext()
                            .getString(R.string.posted_on, formattedDate));
                }
            }
        }
    }
}