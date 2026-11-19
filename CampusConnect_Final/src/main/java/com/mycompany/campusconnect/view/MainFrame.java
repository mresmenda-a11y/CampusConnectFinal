package com.mycompany.campusconnect.view;

import com.mycompany.campusconnect.dao.RequestDAO;
import com.mycompany.campusconnect.exception.DuplicateTicketException;
import com.mycompany.campusconnect.exception.InvalidRequestException;
import com.mycompany.campusconnect.model.AdvisingRequest;
import com.mycompany.campusconnect.model.DocumentRequest;
import com.mycompany.campusconnect.model.EnrollmentRequest;
import com.mycompany.campusconnect.model.PaymentConcern;
import com.mycompany.campusconnect.model.RequestStatus;
import com.mycompany.campusconnect.model.ServiceCategory;
import com.mycompany.campusconnect.model.ServiceRequest;
import com.mycompany.campusconnect.model.Student;
import com.mycompany.campusconnect.service.RequestService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    private final RequestService requestService = new RequestService();
    private final RequestDAO requestDAO = new RequestDAO();

    private final JTextField txtStudentId = new JTextField(12);
    private final JTextField txtStudentName = new JTextField(16);
    private final JComboBox<ServiceCategory> cmbCategory = new JComboBox<>(ServiceCategory.values());
    private final JComboBox<Integer> cmbPriority = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

    private final CardLayout detailCards = new CardLayout();
    private final JPanel pnlDetailCards = new JPanel(detailCards);
    private final JTextField txtUnits = new JTextField(10);
    private final JTextField txtAmount = new JTextField(10);
    private final JTextField txtDocumentType = new JTextField(16);
    private final JTextField txtTopic = new JTextField(16);

    private final JTextField txtSearchTicket = new JTextField(12);
    private final JComboBox<RequestStatus> cmbStatusUpdate = new JComboBox<>(RequestStatus.values());

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Ticket ID", "Student ID", "Student Name", "Category", "Priority", "Est. Days", "Status", "Details"},
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblRequests = new JTable(tableModel);

    public MainFrame() {
        setTitle("CampusConnect - Student Service Request System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);

        initUiComponents();
        initDatabaseAndLoad();
    }

    private void initUiComponents() {
        setLayout(new BorderLayout(8, 8));

        JPanel pnlTop = new JPanel(new GridLayout(0, 1, 6, 6));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        JPanel pnlStudent = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStudent.setBorder(BorderFactory.createTitledBorder("New Service Request"));
        pnlStudent.add(new JLabel("Student ID:"));
        pnlStudent.add(txtStudentId);
        pnlStudent.add(new JLabel("Student Name:"));
        pnlStudent.add(txtStudentName);
        pnlStudent.add(new JLabel("Category:"));
        pnlStudent.add(cmbCategory);
        pnlStudent.add(new JLabel("Priority (1-5):"));
        pnlStudent.add(cmbPriority);

        JPanel pnlEnrollment = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlEnrollment.add(new JLabel("Units to Enroll:"));
        pnlEnrollment.add(txtUnits);

        JPanel pnlPayment = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlPayment.add(new JLabel("Concern Amount:"));
        pnlPayment.add(txtAmount);

        JPanel pnlDocument = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlDocument.add(new JLabel("Document Type:"));
        pnlDocument.add(txtDocumentType);

        JPanel pnlAdvising = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAdvising.add(new JLabel("Advising Topic:"));
        pnlAdvising.add(txtTopic);

        pnlDetailCards.add(pnlEnrollment, ServiceCategory.ENROLLMENT.name());
        pnlDetailCards.add(pnlPayment, ServiceCategory.PAYMENT.name());
        pnlDetailCards.add(pnlDocument, ServiceCategory.DOCUMENT.name());
        pnlDetailCards.add(pnlAdvising, ServiceCategory.ADVISING.name());

        JButton btnSubmit = new JButton("Submit Request");
        btnSubmit.addActionListener(e -> handleSubmitRequest());

        JPanel pnlDetailWrapper = new JPanel(new BorderLayout());
        pnlDetailWrapper.add(pnlDetailCards, BorderLayout.CENTER);
        pnlDetailWrapper.add(btnSubmit, BorderLayout.EAST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlActions.setBorder(BorderFactory.createTitledBorder("Manage & Filter"));
        pnlActions.add(new JLabel("Ticket ID:"));
        pnlActions.add(txtSearchTicket);

        JButton btnSearch = new JButton("Find Ticket");
        btnSearch.addActionListener(e -> handleFindTicket());
        pnlActions.add(btnSearch);

        pnlActions.add(new JLabel("New Status:"));
        pnlActions.add(cmbStatusUpdate);

        JButton btnUpdateStatus = new JButton("Update Status");
        btnUpdateStatus.addActionListener(e -> handleUpdateStatus());
        pnlActions.add(btnUpdateStatus);

        JButton btnSortPriority = new JButton("Sort by Priority");
        btnSortPriority.addActionListener(e -> refreshTable(requestService.getRequestsSortedByPriority()));
        pnlActions.add(btnSortPriority);

        JButton btnShowAll = new JButton("Show All");
        btnShowAll.addActionListener(e -> refreshTable(requestService.getAllRequests()));
        pnlActions.add(btnShowAll);

        pnlTop.add(pnlStudent);
        pnlTop.add(pnlDetailWrapper);
        pnlTop.add(pnlActions);

        add(pnlTop, BorderLayout.NORTH);
        add(new JScrollPane(tblRequests), BorderLayout.CENTER);

        cmbCategory.addActionListener(e -> {
            ServiceCategory selected = (ServiceCategory) cmbCategory.getSelectedItem();
            if (selected != null) {
                detailCards.show(pnlDetailCards, selected.name());
            }
        });
        detailCards.show(pnlDetailCards, ServiceCategory.ENROLLMENT.name());
    }

    private void initDatabaseAndLoad() {
        try {
            requestDAO.initializeTable();
            List<ServiceRequest> savedRequests = requestDAO.findAll();
            for (ServiceRequest req : savedRequests) {
                try {
                    requestService.addRequest(req);
                } catch (InvalidRequestException | DuplicateTicketException ignored) {
                }
            }
            refreshTable(requestService.getAllRequests());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database warning (running in-memory): " + ex.getMessage(),
                    "Database Notice",
                    JOptionPane.WARNING_MESSAGE
            );
            refreshTable(requestService.getAllRequests());
        }
    }

    private void handleSubmitRequest() {
        String studentId = txtStudentId.getText().trim();
        String studentName = txtStudentName.getText().trim();
        ServiceCategory category = (ServiceCategory) cmbCategory.getSelectedItem();
        int priority = (Integer) cmbPriority.getSelectedItem();

        try {
            if (studentId.isEmpty() || studentName.isEmpty()) {
                throw new InvalidRequestException("Student ID and Student Name are required.");
            }
            Student student = new Student(studentId, studentName);
            ServiceRequest request = switch (category) {
                case ENROLLMENT -> new EnrollmentRequest(student, priority, parsePositiveInt(txtUnits.getText(), "Units to enroll"));
                case PAYMENT -> new PaymentConcern(student, priority, parsePositiveDouble(txtAmount.getText(), "Amount"));
                case DOCUMENT -> new DocumentRequest(student, priority, txtDocumentType.getText());
                case ADVISING -> new AdvisingRequest(student, priority, txtTopic.getText());
            };

            requestService.addRequest(request);
            try {
                requestDAO.insertRequest(request);
            } catch (SQLException dbEx) {
                JOptionPane.showMessageDialog(
                        this,
                        "Added to session, but DB save failed: " + dbEx.getMessage(),
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            refreshTable(requestService.getAllRequests());
            clearInputs();
            JOptionPane.showMessageDialog(this, "Submitted ticket: " + request.getTicketId());
        } catch (InvalidRequestException | DuplicateTicketException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFindTicket() {
        String ticketId = txtSearchTicket.getText().trim();
        if (ticketId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Ticket ID to search.");
            return;
        }
        ServiceRequest found = requestService.findByTicketId(ticketId);
        if (found == null) {
            JOptionPane.showMessageDialog(this, "No request found for ticket: " + ticketId);
            return;
        }
        refreshTable(List.of(found));
    }

    private void handleUpdateStatus() {
        String ticketId = txtSearchTicket.getText().trim();
        RequestStatus newStatus = (RequestStatus) cmbStatusUpdate.getSelectedItem();
        try {
            boolean updated = requestService.updateStatus(ticketId, newStatus);
            if (!updated) {
                JOptionPane.showMessageDialog(this, "Ticket not found: " + ticketId);
                return;
            }
            try {
                requestDAO.updateStatus(ticketId, newStatus);
            } catch (SQLException dbEx) {
                JOptionPane.showMessageDialog(
                        this,
                        "Updated in session, but DB update failed: " + dbEx.getMessage(),
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            refreshTable(requestService.getAllRequests());
            JOptionPane.showMessageDialog(this, "Status updated for " + ticketId);
        } catch (InvalidRequestException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable(List<ServiceRequest> requests) {
        tableModel.setRowCount(0);
        for (ServiceRequest req : requests) {
            tableModel.addRow(new Object[]{
                req.getTicketId(),
                req.getStudent().getStudentId(),
                req.getStudent().getName(),
                req.getCategory(),
                req.getPriorityLevel(),
                req.estimateDays(),
                req.getStatus(),
                formatDetails(req)
            });
        }
    }

    private String formatDetails(ServiceRequest req) {
        if (req instanceof EnrollmentRequest er) {
            return "Units: " + er.getUnitsToEnroll();
        } else if (req instanceof PaymentConcern pc) {
            return String.format("Amount: %.2f", pc.getAmount());
        } else if (req instanceof DocumentRequest dr) {
            return "Doc: " + dr.getDocumentType();
        } else if (req instanceof AdvisingRequest ar) {
            return "Topic: " + ar.getTopic();
        }
        return "";
    }

    private int parsePositiveInt(String raw, String fieldName) throws InvalidRequestException {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidRequestException(fieldName + " must be a valid whole number.");
        }
    }

    private double parsePositiveDouble(String raw, String fieldName) throws InvalidRequestException {
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidRequestException(fieldName + " must be a valid number.");
        }
    }

    private void clearInputs() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtUnits.setText("");
        txtAmount.setText("");
        txtDocumentType.setText("");
        txtTopic.setText("");
    }
}