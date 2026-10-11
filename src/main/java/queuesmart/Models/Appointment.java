package queuesmart.Models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private int appointmentId;
    private int studentId;
    private int serviceId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    public Appointment() {
    }

    public Appointment(int appointmentId, int studentId, int serviceId, LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public int getAppointmentId() {
        return appointmentId; 
    }
    
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId; 
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

    public LocalDate getAppointmentDate() { 
        return appointmentDate; 
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) { 
        this.appointmentDate = appointmentDate; 
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime; 
    }
    
    public void setAppointmentTime(LocalTime appointmentTime) { 
        this.appointmentTime = appointmentTime; 
    }

    public String getStatus() {
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
}