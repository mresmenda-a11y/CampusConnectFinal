Project Structure:
com.mycompany.campusconnect.model: Domain classes, enums, and interfaces (Student, ServiceRequest, subtypes, RequestStatus, ServiceCategory, Prioritizable)

com.mycompany.campusconnect.dao: JDBC persistence and connection handling (DatabaseConnection, RequestDAO)

com.mycompany.campusconnect.service: Business rules, validation, and queue/workflow logic (RequestService)

com.mycompany.campusconnect.exception: Custom domain exceptions (DuplicateTicketException, InvalidRequestException)

com.mycompany.campusconnect.view: Swing UI (MainFrame)

com.mycompany.campusconnect_final: Application entry point (CampusConnect_Final

Required to run project: Java version (JDK), Apache NetBeans (with Maven if using pom.xml), MySQL 8.0, and the MySQL JDBC driver/dependency.

database setup: 
Start the MySQL service (MySQL80).

Run schema.sql to create the required database and tables.

(Optional) Run seed_data.sql to populate sample records.

Configuration:
Check DatabaseConnection.java (and pom.xml if applicable) for your JDBC URL, database name, username, and password.

Update local credentials before running so they match your MySQL installation.
