package com.mycompany.campusconnect.model;

public class Student {
    private String studentId;
    private String name;

    public Student(String studentId, String name) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be blank.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        this.studentId = studentId.trim();
        this.name = name.trim();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }
}