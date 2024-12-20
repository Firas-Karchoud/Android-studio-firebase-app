package com.example.classroomapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Post implements Parcelable {
    private String id;
    private String classroomId;
    private String title;
    private String content;
    private String teacherId;
    private String teacherName;
    private long createdAt;
    private long updatedAt;
    private boolean isPublished;

    // Default constructor required for Firestore
    public Post() {}

    public Post(String classroomId, String title, String content, String teacherId, String teacherName) {
        this.classroomId = classroomId;
        this.title = title;
        this.content = content;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.isPublished = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    // Helper methods
    public boolean isEdited() {
        return updatedAt > createdAt;
    }

    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // Parcelable implementation
    protected Post(Parcel in) {
        id = in.readString();
        classroomId = in.readString();
        title = in.readString();
        content = in.readString();
        teacherId = in.readString();
        teacherName = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        isPublished = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(classroomId);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(teacherId);
        dest.writeString(teacherName);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (isPublished ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // Builder pattern for easier object creation
    public static class Builder {
        private final Post post;

        public Builder(String classroomId, String title, String teacherId, String teacherName) {
            post = new Post();
            post.classroomId = classroomId;
            post.title = title;
            post.teacherId = teacherId;
            post.teacherName = teacherName;
            post.createdAt = System.currentTimeMillis();
            post.updatedAt = post.createdAt;
            post.isPublished = true;
        }

        public Builder content(String content) {
            post.content = content;
            return this;
        }

        public Builder createdAt(long timestamp) {
            post.createdAt = timestamp;
            post.updatedAt = timestamp;
            return this;
        }

        public Builder updatedAt(long timestamp) {
            post.updatedAt = timestamp;
            return this;
        }

        public Builder isPublished(boolean published) {
            post.isPublished = published;
            return this;
        }

        public Post build() {
            return post;
        }
    }
}