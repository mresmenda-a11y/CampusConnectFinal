package com.mycompany.campusconnect.model;

public class AdvisingRequest extends ServiceRequest {
    private String topic;

    public AdvisingRequest(Student student, int priorityLevel, String topic) {
        super(student, priorityLevel);
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be blank.");
        }
        this.topic = topic.trim();
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public ServiceCategory getCategory() {
        return ServiceCategory.ADVISING;
    }

    @Override
    public int estimateDays() {
        return getPriorityLevel() >= 4 ? 1 : 2;
    }
}