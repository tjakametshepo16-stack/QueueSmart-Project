package queuesmart.Models;

public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String role;      // STUDENT, STAFF, ADMIN
    private String fullName;
    private String email;

    public User() {
    }

    public User(int userId, String username, String passwordHash, String role, String fullName, String email) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }

    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int userId) { 
        this.userId = userId; 
    }

    public String getUsername() {
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPasswordHash() {
        return passwordHash; 
    }
    
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
    }

    public String getRole() { 
        return role;
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }

    public String getFullName() {
        return fullName; 
    }
    
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
}
