package com.mycompany.campusconnect.service;

import com.mycompany.campusconnect.exception.DuplicateTicketException;
import com.mycompany.campusconnect.exception.InvalidRequestException;
import com.mycompany.campusconnect.model.RequestStatus;
import com.mycompany.campusconnect.model.ServiceRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final Map<String, ServiceRequest> requestsByTicket = new HashMap<>();

    public void addRequest(ServiceRequest request)
            throws InvalidRequestException, DuplicateTicketException {
        if (request == null) {
            throw new InvalidRequestException("Request cannot be null.");
        }
        if (request.getTicketId() == null || request.getTicketId().trim().isEmpty()) {
            throw new InvalidRequestException("Ticket ID cannot be blank.");
        }
        if (requestsByTicket.containsKey(request.getTicketId())) {
            throw new DuplicateTicketException(
                    "Ticket ID already exists: " + request.getTicketId());
        }
        requestsByTicket.put(request.getTicketId(), request);
    }

    public List<ServiceRequest> getAllRequests() {
        return new ArrayList<>(requestsByTicket.values());
    }

    public List<ServiceRequest> getRequestsSortedByPriority() {
        List<ServiceRequest> list = new ArrayList<>(requestsByTicket.values());
        list.sort(Comparator.comparingInt(ServiceRequest::getPriorityLevel));
        return list;
    }

    public ServiceRequest findByTicketId(String ticketId) {
        if (ticketId == null) {
            return null;
        }
        return requestsByTicket.get(ticketId.trim());
    }

    public boolean updateStatus(String ticketId, RequestStatus newStatus)
            throws InvalidRequestException {
        if (newStatus == null) {
            throw new InvalidRequestException("Status cannot be null.");
        }
        ServiceRequest target = findByTicketId(ticketId);
        if (target == null) {
            return false;
        }
        target.setStatus(newStatus);
        return true;
    }
}
