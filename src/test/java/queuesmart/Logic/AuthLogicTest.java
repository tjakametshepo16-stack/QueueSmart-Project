package queuesmart.Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AuthLogicTest {

    private AuthLogic auth;

    @BeforeEach
    void setUp() {
        auth = new AuthLogic();
    }

    // ---------- login ----------

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "    "})
    @DisplayName("login: null/empty/blank username returns null")
    void loginRejectsBadUsername(String username) {
        assertNull(auth.login(username, "Password123"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "    "})
    @DisplayName("login: null/empty/blank password returns null")
    void loginRejectsBadPassword(String password) {
        assertNull(auth.login("tshepo", password));
    }

    // ---------- registerStudent ----------

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("registerStudent: username required")
    void studentUsernameRequired(String username) {
        assertEquals("Username is required.",
                auth.registerStudent(username, "Password123", "Tshepo M", "t@uni.ac.za", "2024001"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"1", "abc", "1234567"})
    @DisplayName("registerStudent: password under 8 characters is rejected")
    void studentPasswordTooShort(String password) {
        assertEquals("Password must be at least 8 characters.",
                auth.registerStudent("tshepo", password, "Tshepo M", "t@uni.ac.za", "2024001"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("registerStudent: full name required")
    void studentFullNameRequired(String name) {
        assertEquals("Full name is required.",
                auth.registerStudent("tshepo", "Password123", name, "t@uni.ac.za", "2024001"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"no-at-sign", "plainaddress"})
    @DisplayName("registerStudent: email must contain @")
    void studentEmailInvalid(String email) {
        assertEquals("Please enter a valid email address.",
                auth.registerStudent("tshepo", "Password123", "Tshepo M", email, "2024001"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("registerStudent: student number required")
    void studentNumberRequired(String number) {
        assertEquals("Student number is required.",
                auth.registerStudent("tshepo", "Password123", "Tshepo M", "t@uni.ac.za", number));
    }

    @Test
    @DisplayName("registerStudent: checks run in order (username first)")
    void studentValidationOrder() {
        assertEquals("Username is required.", auth.registerStudent(null, null, null, null, null));
    }

    // ---------- registerStaff ----------

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" "})
    @DisplayName("registerStaff: username required")
    void staffUsernameRequired(String username) {
        assertEquals("Username is required.",
                auth.registerStaff(1, username, "Password123", "Staff One", "s@uni.ac.za", "ST001"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "short", "1234567"})
    @DisplayName("registerStaff: password under 8 characters is rejected")
    void staffPasswordTooShort(String password) {
        assertEquals("Password must be at least 8 characters.",
                auth.registerStaff(1, "staff1", password, "Staff One", "s@uni.ac.za", "ST001"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    @DisplayName("registerStaff: full name required")
    void staffFullNameRequired(String name) {
        assertEquals("Full name is required.",
                auth.registerStaff(1, "staff1", "Password123", name, "s@uni.ac.za", "ST001"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "staff.at.uni"})
    @DisplayName("registerStaff: email must contain @")
    void staffEmailInvalid(String email) {
        assertEquals("Please enter a valid email address.",
                auth.registerStaff(1, "staff1", "Password123", "Staff One", email, "ST001"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("registerStaff: staff number required")
    void staffNumberRequired(String number) {
        assertEquals("Staff number is required.",
                auth.registerStaff(1, "staff1", "Password123", "Staff One", "s@uni.ac.za", number));
    }
}