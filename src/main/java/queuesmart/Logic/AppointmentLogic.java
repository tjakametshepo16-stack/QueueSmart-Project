package queuesmart.Logic;

import queuesmart.Database.AppointmentDAO;
import queuesmart.Models.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentLogic {

    private final AppointmentDAO appointmentDAO;

    public AppointmentLogic() {
        appointmentDAO = new AppointmentDAO();
    }

    public String bookAppointment(int studentId, int serviceId, LocalDate date, LocalTime time){
        
        if (studentId <= 0) return "Student account could not be found.";
        if (serviceId <= 0) return "Please select a service.";
        
        if (date == null || date.isBefore(LocalDate.now())) {
            return "Please choose today or a future date.";
        }
        
        if (time == null) return "Please enter an appointment time.";

        Appointment appointment = new Appointment( 0, studentId, serviceId, date, time, "BOOKED" );

        if (!appointmentDAO.addAppointment(appointment)) {
            return "Could not book the appointment. The selected slot may already be in use.";
        }

        return null;
    }

    public List<Appointment> getStudentAppointments(int studentId) {
        if (studentId <= 0) return List.of();
        return appointmentDAO.getStudentAppointments(studentId);
    }

    public boolean cancelAppointment(int appointmentId, int studentId) {
        if (appointmentId <= 0 || studentId <= 0) return false;
        return appointmentDAO.cancelAppointment(appointmentId, studentId);
    }
}