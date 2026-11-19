package com.mycompany.campusconnect.model;

public class EnrollmentRequest extends ServiceRequest {
    private int unitsToEnroll;

    public EnrollmentRequest(Student student, int priorityLevel, int unitsToEnroll) {
        super(student, priorityLevel);
        if (unitsToEnroll <= 0) {
            throw new IllegalArgumentException("Units to enroll must be positive.");
        }
        this.unitsToEnroll = unitsToEnroll;
    }

    public int getUnitsToEnroll() {
        return unitsToEnroll;
    }

    @Override
    public ServiceCategory getCategory() {
        return ServiceCategory.ENROLLMENT;
    }

    @Override
    public int estimateDays() {
        return getPriorityLevel() == 5 ? 2 : 3;
    }
}