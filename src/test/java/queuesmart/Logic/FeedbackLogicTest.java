package queuesmart.Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackLogicTest {

    private FeedbackLogic logic;

    @BeforeEach
    void setUp() {
        logic = new FeedbackLogic();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    @DisplayName("submitFeedback: invalid student id is rejected")
    void invalidStudent(int studentId) {
        assertEquals("You must be logged in as a student to submit feedback.",
                logic.submitFeedback(studentId, 5, "Great"));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 6, 100, Integer.MAX_VALUE})
    @DisplayName("submitFeedback: rating outside 1-5 is rejected")
    void ratingOutOfRange(int rating) {
        assertEquals("Please choose a rating between 1 and 5.",
                logic.submitFeedback(1, rating, "comment"));
    }

    @Test
    @DisplayName("submitFeedback: student check takes priority over rating check")
    void studentCheckFirst() {
        assertEquals("You must be logged in as a student to submit feedback.",
                logic.submitFeedback(0, 99, null));
    }
}