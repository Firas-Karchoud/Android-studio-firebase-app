package com.example.classroomapp.teacher;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.classroomapp.R;
import com.example.classroomapp.models.Classroom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClassroomDialogFragment extends DialogFragment {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ClassroomDialogListener listener;
    private Classroom existingClassroom;

    // Interface for callback to activity
    public interface ClassroomDialogListener {
        default void onClassroomCreated(Classroom classroom) {
            // Default empty implementation
        }
        default void onClassroomUpdated(Classroom classroom) {
            // Default empty implementation
        }
    }

    public static ClassroomDialogFragment newInstance() {
        return new ClassroomDialogFragment();
    }

    public static ClassroomDialogFragment newInstance(Classroom classroom) {
        ClassroomDialogFragment fragment = new ClassroomDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("classroom", classroom);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make the listener optional
        if (context instanceof ClassroomDialogListener) {
            listener = (ClassroomDialogListener) context;
        } else {
            // Create a default listener if the activity doesn't implement it
            listener = new ClassroomDialogListener() {};
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Check if we're editing an existing classroom
        if (getArguments() != null) {
            existingClassroom = getArguments().getParcelable("classroom");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_classroom, null);

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        // If editing, populate fields with existing data
        if (existingClassroom != null) {
            titleEditText.setText(existingClassroom.getTitle());
            descriptionEditText.setText(existingClassroom.getDescription());
            builder.setTitle(R.string.edit_classroom);
        } else {
            builder.setTitle(R.string.create_classroom);
        }

        builder.setView(view)
                .setPositiveButton(existingClassroom != null ? R.string.update : R.string.create,
                        (dialog, id) -> saveClassroom())
                .setNegativeButton(R.string.cancel, (dialog, id) -> dismiss());

        return builder.create();
    }

    private void saveClassroom() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleEditText.setError(getString(R.string.error_field_required));
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(requireContext(), R.string.error_auth_required, Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        String teacherId = mAuth.getCurrentUser().getUid();
        String teacherEmail = mAuth.getCurrentUser().getEmail();

        if (existingClassroom != null) {
            // Update existing classroom
            existingClassroom.setTitle(title);
            existingClassroom.setDescription(description);

            db.collection("classrooms")
                    .document(existingClassroom.getId())
                    .set(existingClassroom)
                    .addOnSuccessListener(aVoid -> {
                        listener.onClassroomUpdated(existingClassroom);
                        showSuccessMessage(R.string.classroom_updated);
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        showErrorMessage(R.string.error_updating_classroom);
                        dismiss();
                    });
        } else {
            // Create new classroom
            Classroom classroom = new Classroom();
            classroom.setTitle(title);
            classroom.setDescription(description);
            classroom.setTeacherId(teacherId);
            classroom.setTeacherName(teacherEmail);
            classroom.setCreatedAt(System.currentTimeMillis());

            db.collection("classrooms")
                    .add(classroom)
                    .addOnSuccessListener(documentReference -> {
                        classroom.setId(documentReference.getId());
                        listener.onClassroomCreated(classroom);
                        showSuccessMessage(R.string.classroom_created);
                        dismiss();

                    })
                    .addOnFailureListener(e -> {
                        showErrorMessage(R.string.error_creating_classroom);
                        dismiss();
                    });
        }

    }

    private void showSuccessMessage(int messageResId) {
        if (getContext() != null) {
            Toast.makeText(getContext(), messageResId, Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorMessage(int messageResId) {
        if (getContext() != null) {
            Toast.makeText(getContext(), messageResId, Toast.LENGTH_SHORT).show();
        }
    }
}