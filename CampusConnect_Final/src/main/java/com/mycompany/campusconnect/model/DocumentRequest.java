package com.mycompany.campusconnect.model;

public class DocumentRequest extends ServiceRequest {
    private String documentType;

    public DocumentRequest(Student student, int priorityLevel, String documentType) {
        super(student, priorityLevel);
        if (documentType == null || documentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Document type cannot be blank.");
        }
        this.documentType = documentType.trim();
    }

    public String getDocumentType() {
        return documentType;
    }

    @Override
    public ServiceCategory getCategory() {
        return ServiceCategory.DOCUMENT;
    }

    @Override
    public int estimateDays() {
        return getPriorityLevel() == 5 ? 3 : 5;
    }
}