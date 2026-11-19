package com.mycompany.campusconnect.dao;

import com.mycompany.campusconnect.model.AdvisingRequest;
import com.mycompany.campusconnect.model.DocumentRequest;
import com.mycompany.campusconnect.model.EnrollmentRequest;
import com.mycompany.campusconnect.model.PaymentConcern;
import com.mycompany.campusconnect.model.RequestStatus;
import com.mycompany.campusconnect.model.ServiceCategory;
import com.mycompany.campusconnect.model.ServiceRequest;
import com.mycompany.campusconnect.model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public void initializeTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS service_requests (
                    ticket_id VARCHAR(32) PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    student_name VARCHAR(120) NOT NULL,
                    category VARCHAR(32) NOT NULL,
                    priority_level INT NOT NULL,
                    status VARCHAR(32) NOT NULL,
                    units_to_enroll INT NULL,
                    amount DOUBLE NULL,
                    document_type VARCHAR(120) NULL,
                    topic VARCHAR(120) NULL
                )
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertRequest(ServiceRequest request) throws SQLException {
        String sql = """
                INSERT INTO service_requests (
                    ticket_id, student_id, student_name, category, priority_level, status,
                    units_to_enroll, amount, document_type, topic
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getTicketId());
            ps.setString(2, request.getStudent().getStudentId());
            ps.setString(3, request.getStudent().getName());
            ps.setString(4, request.getCategory().name());
            ps.setInt(5, request.getPriorityLevel());
            ps.setString(6, request.getStatus().name());

            ps.setNull(7, Types.INTEGER);
            ps.setNull(8, Types.DOUBLE);
            ps.setNull(9, Types.VARCHAR);
            ps.setNull(10, Types.VARCHAR);

            if (request instanceof EnrollmentRequest er) {
                ps.setInt(7, er.getUnitsToEnroll());
            } else if (request instanceof PaymentConcern pc) {
                ps.setDouble(8, pc.getAmount());
            } else if (request instanceof DocumentRequest dr) {
                ps.setString(9, dr.getDocumentType());
            } else if (request instanceof AdvisingRequest ar) {
                ps.setString(10, ar.getTopic());
            }

            ps.executeUpdate();
        }
    }

    public List<ServiceRequest> findAll() throws SQLException {
        String sql = "SELECT * FROM service_requests";
        List<ServiceRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean updateStatus(String ticketId, RequestStatus status) throws SQLException {
        String sql = "UPDATE service_requests SET status = ? WHERE ticket_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, ticketId);
            return ps.executeUpdate() > 0;
        }
    }

    private ServiceRequest mapRow(ResultSet rs) throws SQLException {
        Student student = new Student(
                rs.getString("student_id"),
                rs.getString("student_name")
        );
        int priority = rs.getInt("priority_level");
        ServiceCategory category = ServiceCategory.valueOf(rs.getString("category"));
        ServiceRequest req = switch (category) {
            case ENROLLMENT -> new EnrollmentRequest(student, priority, rs.getInt("units_to_enroll"));
            case PAYMENT -> new PaymentConcern(student, priority, rs.getDouble("amount"));
            case DOCUMENT -> new DocumentRequest(student, priority, rs.getString("document_type"));
            case ADVISING -> new AdvisingRequest(student, priority, rs.getString("topic"));
        };
        req.setStatus(RequestStatus.valueOf(rs.getString("status")));
        return req;
    }
}