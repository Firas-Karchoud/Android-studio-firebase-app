package com.example.classroomapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Classroom implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private String teacherName;
    private int studentCount;
    private long createdAt;
    private boolean isActive;

    // Default constructor required for Firestore
    public Classroom() {}

    public Classroom(String title, String description, String teacherId, String teacherName) {
        this.title = title;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.studentCount = 0;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equals(id, classroom.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", studentCount=" + studentCount +
                '}';
    }

    // Parcelable implementation
    protected Classroom(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        teacherId = in.readString();
        teacherName = in.readString();
        studentCount = in.readInt();
        createdAt = in.readLong();
        isActive = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(teacherId);
        dest.writeString(teacherName);
        dest.writeInt(studentCount);
        dest.writeLong(createdAt);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Classroom> CREATOR = new Creator<Classroom>() {
        @Override
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }

        @Override
        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    // Builder pattern for easier object creation
    public static class Builder {
        private final Classroom classroom;

        public Builder(String title, String teacherId, String teacherName) {
            classroom = new Classroom();
            classroom.title = title;
            classroom.teacherId = teacherId;
            classroom.teacherName = teacherName;
            classroom.createdAt = System.currentTimeMillis();
            classroom.isActive = true;
            classroom.studentCount = 0;
        }

        public Builder description(String description) {
            classroom.description = description;
            return this;
        }

        public Builder studentCount(int count) {
            classroom.studentCount = count;
            return this;
        }

        public Builder createdAt(long timestamp) {
            classroom.createdAt = timestamp;
            return this;
        }

        public Builder isActive(boolean active) {
            classroom.isActive = active;
            return this;
        }

        public Classroom build() {
            return classroom;
        }
    }
}