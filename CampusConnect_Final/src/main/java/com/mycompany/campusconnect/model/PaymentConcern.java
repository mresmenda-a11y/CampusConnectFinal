package com.mycompany.campusconnect.model;

public class PaymentConcern extends ServiceRequest {
    private double amount;

    public PaymentConcern(Student student, int priorityLevel, double amount) {
        super(student, priorityLevel);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public ServiceCategory getCategory() {
        return ServiceCategory.PAYMENT;
    }

    @Override
    public int estimateDays() {
        return getPriorityLevel() >= 4 ? 2 : 4;
    }
}