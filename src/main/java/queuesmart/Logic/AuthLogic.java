package queuesmart.Logic;

import queuesmart.Database.AuditLogDAO;
import queuesmart.Database.StaffDAO;
import queuesmart.Database.StudentDAO;
import queuesmart.Database.UserDAO;
import queuesmart.Models.AuditLog;
import queuesmart.Models.User;

public class AuthLogic {

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final StaffDAO staffDAO;
    private final AuditLogDAO auditLogDAO;

    public AuthLogic() {
        userDAO = new UserDAO();
        studentDAO = new StudentDAO();
        staffDAO = new StaffDAO();
        auditLogDAO = new AuditLogDAO();
    }

    public User login(String username, String password) {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());

        if (user == null) {
            return null;
        }

        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            return null;
        }

        auditLogDAO.insertLog(new AuditLog(user.getUserId(), "LOGIN", "User logged in"));

        return user;
    }

    public String registerStudent(String username, String password, String fullName, String email, String studentNumber) {
        
        if (username == null || username.isBlank()) {
            return "Username is required.";
        }
        
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        
        if (fullName == null || fullName.isBlank()) {
            return "Full name is required.";
        }
        
        if (email == null || !email.contains("@")) {
            return "Please enter a valid email address.";
        }
        
        if (studentNumber == null || studentNumber.isBlank()) {
            return "Student number is required.";
        }
        
        if (userDAO.usernameExists(username.trim())) {
            return "An account with this username already exists.";
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole("STUDENT");
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());

        int userId = userDAO.insertUser(user);

        if (userId <= 0) {
            return "Could not create the account. Please try again.";
        }

        int studentId = studentDAO.insertStudent(userId, studentNumber.trim());

        if (studentId <= 0) {
            return "Account was created but the student record failed. Please contact IT.";
        }

        auditLogDAO.insertLog(new AuditLog(userId, "REGISTER", "New student account created"));

        return null;
    }

    /** Creates a new STAFF account. Only an ADMIN should be able to call this  */
    
    public String registerStaff(int adminUserId, String username, String password, String fullName, String email, String staffNumber) {

        if (username == null || username.isBlank()) {
            return "Username is required.";
        }
        
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        
        if (fullName == null || fullName.isBlank()) {
            return "Full name is required.";
        }
        
        if (email == null || !email.contains("@")) {
            return "Please enter a valid email address.";
        }
        
        if (staffNumber == null || staffNumber.isBlank()) {
            return "Staff number is required.";
        }
        
        if (userDAO.usernameExists(username.trim())) {
            return "An account with this username already exists.";
        }
        
        if (staffDAO.staffNumberExists(staffNumber.trim())) {
            return "A staff member with this staff number already exists.";
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole("STAFF");
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());

        int userId = userDAO.insertUser(user);

        if (userId <= 0) {
            return "Could not create the account. Please try again.";
        }

        int staffId = staffDAO.insertStaff(userId, staffNumber.trim());

        if (staffId <= 0) {
            return "Account was created but the staff record failed. Please contact IT.";
        }

        auditLogDAO.insertLog(new AuditLog(adminUserId, "REGISTER_STAFF",
                "Admin created new staff account: " + username.trim()));

        return null;
    }
}