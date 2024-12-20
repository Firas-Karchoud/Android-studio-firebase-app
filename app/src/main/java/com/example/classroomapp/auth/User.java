package com.example.classroomapp.auth;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class User implements Parcelable {
    private String userId;
    private String email;
    private String role;
    private long createdAt;
    private String displayName;
    private String profileImageUrl;

    // Default constructor required for Firestore
    public User() {}

    public User(String userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor with all fields
    public User(String userId, String email, String role, long createdAt, String displayName, String profileImageUrl) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
    }

    // Parcelable constructor
    protected User(Parcel in) {
        userId = in.readString();
        email = in.readString();
        role = in.readString();
        createdAt = in.readLong();
        displayName = in.readString();
        profileImageUrl = in.readString();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Helper methods
    public boolean isTeacher() {
        return "teacher".equals(role);
    }

    public boolean isStudent() {
        return "student".equals(role);
    }

    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    // Parcelable implementation
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(role);
        dest.writeLong(createdAt);
        dest.writeString(displayName);
        dest.writeString(profileImageUrl);
    }

    // Builder pattern for easier object creation
    public static class Builder {
        private String userId;
        private String email;
        private String role;
        private long createdAt = System.currentTimeMillis();
        private String displayName;
        private String profileImageUrl;

        public Builder(String userId, String email, String role) {
            this.userId = userId;
            this.email = email;
            this.role = role;
        }

        public Builder setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public User build() {
            return new User(userId, email, role, createdAt, displayName, profileImageUrl);
        }
    }
}