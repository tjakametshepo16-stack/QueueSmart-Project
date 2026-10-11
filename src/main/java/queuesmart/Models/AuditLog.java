package queuesmart.Models;

import java.time.LocalDateTime;

public class AuditLog {

    private int auditId;
    private Integer userId;
    private String action;
    private String details;
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(Integer userId, String action, String details) {
        this.userId = userId;
        this.action = action;
        this.details = details;
    }

    public int getAuditId() { return auditId; }
    
    public void setAuditId(int auditId) { 
        this.auditId = auditId; 
    }

    public Integer getUserId() { return userId; }
    
    public void setUserId(Integer userId) {
        this.userId = userId; 
    }

    public String getAction() { return action; }
    
    public void setAction(String action) { 
        this.action = action; 
    }

    public String getDetails() { return details; }
    
    public void setDetails(String details) { 
        this.details = details; 
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}