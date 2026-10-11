package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.Appointment;
import java.sql.*;
import java.time.*;
import java.util.*;

public class AppointmentDAO {
    
    public boolean addAppointment(Appointment a){
        String s="INSERT INTO appointments(student_id,service_id,appointment_date,appointment_time,status) VALUES(?,?,?,?,'BOOKED')";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){
            p.setInt(1,a.getStudentId());p.setInt(2,a.getServiceId());
            p.setObject(3,a.getAppointmentDate());p.setObject(4,a.getAppointmentTime());
            
            return p.executeUpdate()>0;
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public List<Appointment> getStudentAppointments(int studentId){
        List<Appointment> out=new ArrayList<>();
        String s="SELECT a.*,s.service_name FROM appointments a JOIN services s ON s.service_id=a.service_id WHERE a.student_id=? ORDER BY a.appointment_date,a.appointment_time";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,studentId);
                        
        try(ResultSet r=p.executeQuery()){
            while(r.next()){
                Appointment a=new Appointment(r.getInt("appointment_id"),r.getInt("student_id"),r.getInt("service_id"),
                        r.getObject("appointment_date",LocalDate.class),r.getObject("appointment_time",LocalTime.class),r.getString("status"));out.add(a);
            }
        }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }return out;
    }
    
    public String getServiceName(int appointmentId){
        String s="SELECT s.service_name FROM appointments a JOIN services s ON s.service_id=a.service_id WHERE a.appointment_id=?";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,appointmentId);
        
        try(ResultSet r=p.executeQuery()){
            if(r.next())return r.getString(1);
        }
        }catch(SQLException e){}return "";
    }
    
    public boolean cancelAppointment(int id,int studentId){
        String s="UPDATE appointments SET status='CANCELLED' WHERE appointment_id=? AND student_id=? AND status='BOOKED'";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,id);p.setInt(2,studentId);
                return p.executeUpdate()>0;
                
        }catch(SQLException e){
            return false;
        }
    }
}