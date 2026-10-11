package queuesmart.Logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Same password always produces the same hash")
    void hashIsDeterministic() {
        assertEquals(PasswordUtil.hash("Secret123"), PasswordUtil.hash("Secret123"));
    }

    @Test
    @DisplayName("Different passwords produce different hashes")
    void differentPasswordsDifferentHashes() {
        assertNotEquals(PasswordUtil.hash("Secret123"), PasswordUtil.hash("Secret124"));
    }

    @Test
    @DisplayName("Hash is never the plain-text password")
    void hashIsNotPlainText() {
        assertNotEquals("Secret123", PasswordUtil.hash("Secret123"));
    }

    @Test
    @DisplayName("SHA-256 as Base64 is always 44 characters")
    void hashHasExpectedLength() {
        assertEquals(44, PasswordUtil.hash("a").length());
        assertEquals(44, PasswordUtil.hash("a much longer password with spaces 123!@#").length());
    }

    @Test
    @DisplayName("Empty and unicode passwords can be hashed")
    void edgeCaseInputsHash() {
        assertDoesNotThrow(() -> PasswordUtil.hash(""));
        assertDoesNotThrow(() -> PasswordUtil.hash("pässwörd-ñ-日本語"));
    }

    @Test
    @DisplayName("matches() accepts the correct password")
    void matchesCorrectPassword() {
        String stored = PasswordUtil.hash("Secret123");
        assertTrue(PasswordUtil.matches("Secret123", stored));
    }

    @Test
    @DisplayName("matches() rejects wrong and differently-cased passwords")
    void matchesRejectsWrongPassword() {
        String stored = PasswordUtil.hash("Secret123");
        assertFalse(PasswordUtil.matches("secret123", stored));
        assertFalse(PasswordUtil.matches("Wrong", stored));
        assertFalse(PasswordUtil.matches("", stored));
    }

    @Test
    @DisplayName("matches() returns false when the stored hash is null")
    void matchesNullStoredHash() {
        assertFalse(PasswordUtil.matches("Secret123", null));
    }

    @Test
    @DisplayName("Null password is rejected with NullPointerException")
    void nullPasswordThrows() {
        assertThrows(NullPointerException.class, () -> PasswordUtil.hash(null));
        assertThrows(NullPointerException.class, () -> PasswordUtil.matches(null, "abc"));
    }
}