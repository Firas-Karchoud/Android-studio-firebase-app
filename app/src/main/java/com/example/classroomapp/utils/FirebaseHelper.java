package com.example.classroomapp.utils;

import androidx.annotation.NonNull;

import com.example.classroomapp.models.Classroom;
import com.example.classroomapp.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseHelper {
    private static final String USERS_COLLECTION = "users";
    private static final String CLASSROOMS_COLLECTION = "classrooms";
    private static final String POSTS_COLLECTION = "posts";

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    // Singleton instance
    private static FirebaseHelper instance;

    private FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    // Auth methods
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public Task<DocumentSnapshot> getCurrentUserData() {
        String userId = getCurrentUserId();
        if (userId == null) return null;
        return db.collection(USERS_COLLECTION).document(userId).get();
    }

    public void signOut() {
        mAuth.signOut();
    }

    // Classroom methods
    public Task<DocumentReference> createClassroom(@NonNull Classroom classroom) {
        return db.collection(CLASSROOMS_COLLECTION).add(classroom);
    }

    public Task<Void> updateClassroom(@NonNull String classroomId, @NonNull Classroom classroom) {
        return db.collection(CLASSROOMS_COLLECTION).document(classroomId).set(classroom);
    }

    public Task<Void> deleteClassroom(@NonNull String classroomId) {
        return db.collection(CLASSROOMS_COLLECTION).document(classroomId).delete();
    }

    public Query getTeacherClassrooms(@NonNull String teacherId) {
        return db.collection(CLASSROOMS_COLLECTION)
                .whereEqualTo("teacherId", teacherId)
                .orderBy("createdAt", Query.Direction.DESCENDING);
    }

    public Query getAllClassrooms() {
        return db.collection(CLASSROOMS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING);
    }

    // Post methods
    public Task<DocumentReference> createPost(@NonNull String classroomId, @NonNull Post post) {
        return db.collection(CLASSROOMS_COLLECTION)
                .document(classroomId)
                .collection(POSTS_COLLECTION)
                .add(post);
    }

    public Task<Void> updatePost(@NonNull String classroomId, @NonNull String postId, @NonNull Post post) {
        return db.collection(CLASSROOMS_COLLECTION)
                .document(classroomId)
                .collection(POSTS_COLLECTION)
                .document(postId)
                .set(post);
    }

    public Task<Void> deletePost(@NonNull String classroomId, @NonNull String postId) {
        return db.collection(CLASSROOMS_COLLECTION)
                .document(classroomId)
                .collection(POSTS_COLLECTION)
                .document(postId)
                .delete();
    }

    public Query getClassroomPosts(@NonNull String classroomId) {
        return db.collection(CLASSROOMS_COLLECTION)
                .document(classroomId)
                .collection(POSTS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING);
    }

    // User methods
    public Task<Void> createUserData(@NonNull String userId, @NonNull String email, @NonNull String role) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .set(new UserData(email, role));
    }

    public Task<DocumentSnapshot> getUserData(@NonNull String userId) {
        return db.collection(USERS_COLLECTION).document(userId).get();
    }

    // Helper class for user data
    private static class UserData {
        public String email;
        public String role;
        public long createdAt;

        public UserData(String email, String role) {
            this.email = email;
            this.role = role;
            this.createdAt = System.currentTimeMillis();
        }
    }
}