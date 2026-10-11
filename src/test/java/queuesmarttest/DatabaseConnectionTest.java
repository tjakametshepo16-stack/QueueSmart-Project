package queuesmarttest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import queuesmart.DatabaseConnection;
import queuesmart.Logic.AuthLogic;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    @DisplayName("testConnection never throws, even if MySQL is down")
    void testConnectionDoesNotThrow() {
        assertDoesNotThrow(DatabaseConnection::testConnection);
    }

    @Test
    @DisplayName("DAO-backed logic fails safely when the database is unavailable")
    void logicFailsSafelyWithoutDatabase() {
        // Valid input, so this reaches the DAO. With MySQL down the DAO catches
        // the SQLException and login returns null instead of crashing.
        // If MySQL is up and the user does not exist, it also returns null.
        assertDoesNotThrow(() -> new AuthLogic().login("no_such_user_xyz", "WrongPass123"));
        assertNull(new AuthLogic().login("no_such_user_xyz", "WrongPass123"));
    }
}