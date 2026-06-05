package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for {@link MyMath}.
 */
class MyMathTestGPT_OSS_P3 {

    private final MyMath myMath = new MyMath();

    // ------------------------------------------------------------------------
    // checkSign – boundary values (0, -1, 1)
    // ------------------------------------------------------------------------

    @Test
    void checkSign_positiveNumber_returnsPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber_returnsNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    void checkSign_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    // ------------------------------------------------------------------------
    // reverseNumber – typical values and zero
    // ------------------------------------------------------------------------

    @Test
    void reverseNumber_positiveNumber_returnsNegative() {
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    void reverseNumber_negativeNumber_returnsPositive() {
        assertEquals(7, myMath.reverseNumber(-7));
    }

    @Test
    void reverseNumber_zero_returnsZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}