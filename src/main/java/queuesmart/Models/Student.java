package queuesmart.Models;

public class Student {

    private int studentId;
    private int userId;
    private String studentNumber;

    public Student() {
    }

    public Student(int studentId, int userId, String studentNumber) {
        this.studentId = studentId;
        this.userId = userId;
        this.studentNumber = studentNumber;
    }

    public int getStudentId() { 
        return studentId;
    }
    
    public void setStudentId(int studentId) { 
        this.studentId = studentId; 
    }

    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int userId) {
        this.userId = userId; 
    }

    public String getStudentNumber() { 
        return studentNumber; 
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber; 
    }
}