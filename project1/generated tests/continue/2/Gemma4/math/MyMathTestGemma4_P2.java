package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MyMath.
 * @author Gemma 4
 */
public class MyMathTestGemma4_P2 {

    private final MyMath myMath = new MyMath();

    // --- Tests for checkSign(int number) ---

    @Test
    void checkSign_PositiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_NegativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_BoundaryZeroMinusOne() {
        // Testing -1 (should be negative)
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_BoundaryZero() {
        // Testing 0 (should throw exception)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void checkSign_BoundaryOne() {
        // Testing 1 (should be positive)
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_LargePositiveNumber() {
        assertEquals("positive", myMath.checkSign(Integer.MAX_VALUE));
    }

    @Test
    void checkSign_LargeNegativeNumber() {
        assertEquals("negative", myMath.checkSign(Integer.MIN_VALUE));
    }

    // --- Tests for reverseNumber(int number) ---

    @Test
    void reverseNumber_PositiveNumber() {
        assertEquals(-123, myMath.reverseNumber(123));
    }

    @Test
    void reverseNumber_NegativeNumber() {
        assertEquals(456, myMath.reverseNumber(-456));
    }

    @Test
    void reverseNumber_Zero() {
        assertEquals(0, myMath.reverseNumber(0));
    }

    @Test
    void reverseNumber_One() {
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    void reverseNumber_BoundaryMinInt() {
        // Reversing Integer.MIN_VALUE = -2147483648 results in 2147483648 which overflows int.
        // The current implementation will wrap around, testing for predictable wrapping behavior or the expected result if it were handled.
        // Since the method returns int, we test the predictable result of the unary negation on the extreme boundary case.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}