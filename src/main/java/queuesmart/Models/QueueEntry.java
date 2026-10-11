package queuesmart.Models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QueueEntry {

    private int queueId;
    private int studentId;
    private int serviceId;
    private int queueNumber;
    private LocalDate queueDate;
    private String status; // WAITING, IN_SERVICE, COMPLETED, NO_SHOW
    private LocalDateTime joinedAt;
    private LocalDateTime calledAt;
    private LocalDateTime completedAt;

    private String studentName;
    private String serviceName;

    public QueueEntry() {
    }

    public int getQueueId() {
        return queueId; 
    }
    
    public void setQueueId(int queueId) {
        this.queueId = queueId; 
    }

    public int getStudentId() {
        return studentId; 
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId; 
    }

    public int getServiceId() { 
        return serviceId; 
    }
    
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId; 
    }

    public int getQueueNumber() { 
        return queueNumber; 
    }
    
    public void setQueueNumber(int queueNumber) { 
        this.queueNumber = queueNumber; 
    }

    public LocalDate getQueueDate() { 
        return queueDate; 
    }
    
    public void setQueueDate(LocalDate queueDate) { 
        this.queueDate = queueDate; 
    }

    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }

    public LocalDateTime getJoinedAt() { 
        return joinedAt; 
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) { 
        this.joinedAt = joinedAt; 
    }

    public LocalDateTime getCalledAt() { 
        return calledAt; 
    }
    
    public void setCalledAt(LocalDateTime calledAt) { 
        this.calledAt = calledAt; 
    }

    public LocalDateTime getCompletedAt() { 
        return completedAt; 
    }
    
    public void setCompletedAt(LocalDateTime completedAt) { 
        this.completedAt = completedAt; 
    }

    public String getStudentName() { 
        return studentName; 
    }
    
    public void setStudentName(String studentName) { 
        this.studentName = studentName; 
    }

    public String getServiceName() { 
        return serviceName; 
    }
    
    public void setServiceName(String serviceName) { 
        this.serviceName = serviceName; 
    }
}
