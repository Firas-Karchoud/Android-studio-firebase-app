package com.example.classroomapp.student;

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

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ClassroomViewHolder> {
    private final List<Classroom> classrooms;
    private final OnClassroomClickListener listener;
    private final SimpleDateFormat dateFormat;

    public interface OnClassroomClickListener {
        void onClassroomClick(Classroom classroom);
    }

    public StudentAdapter(List<Classroom> classrooms, OnClassroomClickListener listener) {
        this.classrooms = classrooms;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classroom_student, parent, false);
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
        private final TextView teacherNameTextView;
        private final TextView dateTextView;

        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            teacherNameTextView = itemView.findViewById(R.id.teacherNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(Classroom classroom) {
            titleTextView.setText(classroom.getTitle());

            String description = classroom.getDescription();
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(description);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            teacherNameTextView.setText(itemView.getContext()
                    .getString(R.string.teacher_name, classroom.getTeacherName()));

            if (classroom.getCreatedAt() > 0) {
                dateTextView.setVisibility(View.VISIBLE);
                String formattedDate = dateFormat.format(new Date(classroom.getCreatedAt()));
                dateTextView.setText(itemView.getContext()
                        .getString(R.string.created_on, formattedDate));
            } else {
                dateTextView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClassroomClick(classroom);
                }
            });
        }
    }
}