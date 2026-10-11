package queuesmarttest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import queuesmart.Models.*;
import queuesmart.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelsAndSessionTest {

    @AfterEach
    void cleanSession() {
        Session.clear();
    }

    // ---------- Session ----------

    @Test
    @DisplayName("Session: starts empty")
    void sessionStartsEmpty() {
        assertNull(Session.getCurrentUser());
    }

    @Test
    @DisplayName("Session: set, get and clear")
    void sessionSetGetClear() {
        User u = new User(1, "tshepo", "hash", "STUDENT", "Tshepo M", "t@uni.ac.za");
        Session.setCurrentUser(u);
        assertSame(u, Session.getCurrentUser());

        Session.clear();
        assertNull(Session.getCurrentUser());
    }

    @Test
    @DisplayName("Session: a new login replaces the previous user")
    void sessionReplacesUser() {
        Session.setCurrentUser(new User(1, "a", "h", "STUDENT", "A", "a@x.com"));
        User second = new User(2, "b", "h", "STAFF", "B", "b@x.com");
        Session.setCurrentUser(second);
        assertEquals(2, Session.getCurrentUser().getUserId());
    }

    // ---------- Models ----------

    @Test
    @DisplayName("User: constructor and setters")
    void userModel() {
        User u = new User(7, "tshepo", "hash", "ADMIN", "Tshepo M", "t@uni.ac.za");
        assertEquals(7, u.getUserId());
        assertEquals("tshepo", u.getUsername());
        assertEquals("hash", u.getPasswordHash());
        assertEquals("ADMIN", u.getRole());
        assertEquals("Tshepo M", u.getFullName());
        assertEquals("t@uni.ac.za", u.getEmail());

        u.setRole("STAFF");
        assertEquals("STAFF", u.getRole());
    }

    @Test
    @DisplayName("Appointment: constructor stores all fields")
    void appointmentModel() {
        LocalDate d = LocalDate.of(2026, 10, 10);
        LocalTime t = LocalTime.of(9, 30);
        Appointment a = new Appointment(1, 2, 3, d, t, "BOOKED");
        assertEquals(1, a.getAppointmentId());
        assertEquals(2, a.getStudentId());
        assertEquals(3, a.getServiceId());
        assertEquals(d, a.getAppointmentDate());
        assertEquals(t, a.getAppointmentTime());
        assertEquals("BOOKED", a.getStatus());
    }

    @Test
    @DisplayName("Service: toString returns the service name")
    void serviceModel() {
        Service s = new Service(1, "Laptop Support", "Fixes", true);
        assertEquals("Laptop Support", s.toString());
        assertTrue(s.isActive());
        s.setActive(false);
        assertFalse(s.isActive());
    }

    @Test
    @DisplayName("Student: constructor and setters")
    void studentModel() {
        Student s = new Student(1, 2, "2024001");
        assertEquals(1, s.getStudentId());
        assertEquals(2, s.getUserId());
        assertEquals("2024001", s.getStudentNumber());
    }

    @Test
    @DisplayName("AuditLog: userId may be null (system events)")
    void auditLogNullUser() {
        AuditLog log = new AuditLog(null, "QUEUE_JOIN", "Student 1 joined");
        assertNull(log.getUserId());
        assertEquals("QUEUE_JOIN", log.getAction());
        assertEquals("Student 1 joined", log.getDetails());

        LocalDateTime now = LocalDateTime.now();
        log.setCreatedAt(now);
        assertEquals(now, log.getCreatedAt());
    }

    @Test
    @DisplayName("Laptop: allocatedToStudentId is null until allocated")
    void laptopModel() {
        Laptop l = new Laptop();
        assertNull(l.getAllocatedToStudentId());

        l.setAssetTag("LT-001");
        l.setStatus("ALLOCATED");
        l.setAllocatedToStudentId(5);
        assertEquals("LT-001", l.getAssetTag());
        assertEquals("ALLOCATED", l.getStatus());
        assertEquals(5, l.getAllocatedToStudentId());
    }

    @Test
    @DisplayName("QueueEntry: setters and getters")
    void queueEntryModel() {
        QueueEntry e = new QueueEntry();
        LocalDateTime now = LocalDateTime.now();

        e.setQueueId(10);
        e.setStudentId(2);
        e.setServiceId(3);
        e.setQueueNumber(4);
        e.setQueueDate(LocalDate.now());
        e.setStatus("WAITING");
        e.setJoinedAt(now);
        e.setStudentName("Tshepo");
        e.setServiceName("Laptop Support");

        assertEquals(10, e.getQueueId());
        assertEquals(2, e.getStudentId());
        assertEquals(3, e.getServiceId());
        assertEquals(4, e.getQueueNumber());
        assertEquals("WAITING", e.getStatus());
        assertEquals(now, e.getJoinedAt());
        assertNull(e.getCalledAt());
        assertNull(e.getCompletedAt());
        assertEquals("Tshepo", e.getStudentName());
        assertEquals("Laptop Support", e.getServiceName());
    }

    @Test
    @DisplayName("Feedback: setters and getters")
    void feedbackModel() {
        Feedback f = new Feedback();
        f.setStudentId(1);
        f.setRating(4);
        f.setComment("Quick service");
        assertEquals(1, f.getStudentId());
        assertEquals(4, f.getRating());
        assertEquals("Quick service", f.getComment());
    }
}