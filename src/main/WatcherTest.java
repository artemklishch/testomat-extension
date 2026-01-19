import annotations.TestId;
import annotations.Title;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ReportingExtension.class)
public class WatcherTest {

    @Test
    @TestId("T-101")
    @Title("User can login")
    void passedTest() {
        assertTrue(true);
    }

    @Test
    @TestId("T-102")
    @Title("User cannot login with wrong password")
    void failedTest() {
        assertEquals(1, 2);
    }

    @Disabled("Feature not ready")
    @Test
    @TestId("T-103")
    @Title("User logout")
    void skippedTest() {
        assertTrue(true);
    }
}
