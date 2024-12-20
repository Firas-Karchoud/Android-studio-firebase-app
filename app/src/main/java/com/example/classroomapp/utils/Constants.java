package com.example.classroomapp.utils;

public class Constants {
    // User roles
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    // Intent extras
    public static final String EXTRA_CLASSROOM = "extra_classroom";
    public static final String EXTRA_POST = "extra_post";

    // Fragment tags
    public static final String DIALOG_CREATE_CLASSROOM = "dialog_create_classroom";
    public static final String DIALOG_EDIT_CLASSROOM = "dialog_edit_classroom";
    public static final String DIALOG_CREATE_POST = "dialog_create_post";
    public static final String DIALOG_EDIT_POST = "dialog_edit_post";

    // Default values
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    private Constants() {
        // Prevent instantiation
    }
}