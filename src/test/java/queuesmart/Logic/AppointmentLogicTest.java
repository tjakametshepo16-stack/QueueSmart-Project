package queuesmart.Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import queuesmart.Models.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentLogicTest {

    private AppointmentLogic logic;
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);
    private final LocalTime ten = LocalTime.of(10, 0);

    @BeforeEach
    void setUp() {
        logic = new AppointmentLogic();
    }

    @Test
    @DisplayName("book: invalid student id")
    void invalidStudent() {
        assertEquals("Student account could not be found.", logic.bookAppointment(0, 1, tomorrow, ten));
        assertEquals("Student account could not be found.", logic.bookAppointment(-3, 1, tomorrow, ten));
    }

    @Test
    @DisplayName("book: invalid service id")
    void invalidService() {
        assertEquals("Please select a service.", logic.bookAppointment(1, 0, tomorrow, ten));
        assertEquals("Please select a service.", logic.bookAppointment(1, -1, tomorrow, ten));
    }

    @Test
    @DisplayName("book: null date is rejected")
    void nullDate() {
        assertEquals("Please choose today or a future date.", logic.bookAppointment(1, 1, null, ten));
    }

    @Test
    @DisplayName("book: past date is rejected")
    void pastDate() {
        assertEquals("Please choose today or a future date.",
                logic.bookAppointment(1, 1, LocalDate.now().minusDays(1), ten));
    }

    @Test
    @DisplayName("book: null time is rejected")
    void nullTime() {
        assertEquals("Please enter an appointment time.", logic.bookAppointment(1, 1, tomorrow, null));
    }

    @Test
    @DisplayName("book: student check runs before service check")
    void validationOrder() {
        assertEquals("Student account could not be found.", logic.bookAppointment(0, 0, null, null));
    }

    @Test
    @DisplayName("getStudentAppointments: invalid id returns empty list, not null")
    void getAppointmentsInvalidId() {
        List<Appointment> result = logic.getStudentAppointments(0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(logic.getStudentAppointments(-10).isEmpty());
    }

    @Test
    @DisplayName("cancel: invalid ids return false")
    void cancelInvalidIds() {
        assertFalse(logic.cancelAppointment(0, 1));
        assertFalse(logic.cancelAppointment(1, 0));
        assertFalse(logic.cancelAppointment(-1, -1));
    }
}