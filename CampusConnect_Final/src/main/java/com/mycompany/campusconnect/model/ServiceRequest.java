package com.mycompany.campusconnect.model;

public abstract class ServiceRequest implements Prioritizable {
    private static int ticketCounter = 1000;
    private String ticketId;
    private Student student;
    private int priorityLevel;
    private RequestStatus status;

    public ServiceRequest(Student student, int priorityLevel) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (priorityLevel < 1 || priorityLevel > 5) {
            throw new IllegalArgumentException("Priority must be 1-5.");
        }
        this.ticketId = "REQ-" + (++ticketCounter);
        this.student = student;
        this.priorityLevel = priorityLevel;
        this.status = RequestStatus.PENDING;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public int getPriorityLevel() {
        return priorityLevel;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public abstract ServiceCategory getCategory();

    public abstract int estimateDays();
}