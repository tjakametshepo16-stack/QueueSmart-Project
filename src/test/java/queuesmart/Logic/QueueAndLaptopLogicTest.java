package queuesmart.Logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import queuesmart.Models.QueueEntry;

import static org.junit.jupiter.api.Assertions.*;

class QueueAndLaptopLogicTest {

    private final QueueLogic queue = new QueueLogic();
    private final LaptopLogic laptops = new LaptopLogic();

    // ---------- QueueLogic ----------

    @Test
    @DisplayName("joinQueue: invalid student or service returns 0")
    void joinQueueInvalidIds() {
        assertEquals(0, queue.joinQueue(0, 1));
        assertEquals(0, queue.joinQueue(1, 0));
        assertEquals(0, queue.joinQueue(-1, -1));
    }

    @Test
    @DisplayName("getPositionInLine: null entry returns 0")
    void positionNullEntry() {
        assertEquals(0, queue.getPositionInLine(null));
    }

    @Test
    @DisplayName("getPositionInLine: non-WAITING entries return 0")
    void positionNotWaiting() {
        QueueEntry e = new QueueEntry();

        e.setStatus("IN_SERVICE");
        assertEquals(0, queue.getPositionInLine(e));

        e.setStatus("COMPLETED");
        assertEquals(0, queue.getPositionInLine(e));

        e.setStatus("NO_SHOW");
        assertEquals(0, queue.getPositionInLine(e));

        e.setStatus(null);
        assertEquals(0, queue.getPositionInLine(e));
    }

    @Test
    @DisplayName("callNext: null entry returns false")
    void callNextNull() {
        assertFalse(queue.callNext(null, 1));
    }

    @Test
    @DisplayName("completeService: null entry returns false")
    void completeNull() {
        assertFalse(queue.completeService(null, 1));
    }

    // ---------- LaptopLogic ----------

    @Test
    @DisplayName("allocateLaptop: invalid laptop or student id returns false")
    void allocateInvalidIds() {
        assertFalse(laptops.allocateLaptop(0, 1, 1));
        assertFalse(laptops.allocateLaptop(1, 0, 1));
        assertFalse(laptops.allocateLaptop(-5, -5, 1));
    }
}