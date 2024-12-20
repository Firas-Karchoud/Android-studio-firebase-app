package com.example.classroomapp.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroomapp.R;
import com.example.classroomapp.models.Classroom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ClassroomViewHolder> {
    private final List<Classroom> classrooms;
    private final OnClassroomClickListener listener;
    private final SimpleDateFormat dateFormat;

    public interface OnClassroomClickListener {
        void onClassroomClick(Classroom classroom);
    }

    public TeacherAdapter(List<Classroom> classrooms, OnClassroomClickListener listener) {
        this.classrooms = classrooms;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classroom, parent, false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        Classroom classroom = classrooms.get(position);
        holder.bind(classroom);
    }

    @Override
    public int getItemCount() {
        return classrooms.size();
    }

    class ClassroomViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView dateTextView;
        private final TextView studentCountTextView;

        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            studentCountTextView = itemView.findViewById(R.id.studentCountTextView);
        }

        public void bind(Classroom classroom) {
            titleTextView.setText(classroom.getTitle());

            // Set description, handle null or empty
            String description = classroom.getDescription();
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(description);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            // Format and set date
            if (classroom.getCreatedAt() > 0) {
                dateTextView.setVisibility(View.VISIBLE);
                String formattedDate = dateFormat.format(new Date(classroom.getCreatedAt()));
                dateTextView.setText(itemView.getContext().getString(R.string.created_on, formattedDate));
            } else {
                dateTextView.setVisibility(View.GONE);
            }

            // Set student count
            int studentCount = classroom.getStudentCount();
            studentCountTextView.setText(itemView.getContext()
                    .getResources()
                    .getQuantityString(R.plurals.student_count, studentCount, studentCount));

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClassroomClick(classroom);
                }
            });
        }
    }
}