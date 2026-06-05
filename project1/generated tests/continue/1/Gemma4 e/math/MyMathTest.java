package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MyMath class.
 * Must end with TestGemma4_1
 */
public class MyMathTest {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    @Test
    void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(10), "Should return 'positive' for positive numbers.");
    }

    @Test
    void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-5), "Should return 'negative' for negative numbers.");
    }

    @Test
    void testCheckSignZero() {
        // Testing for IllegalArgumentException when input is 0
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void testReverseNumberPositive() {
        assertEquals(-123, myMath.reverseNumber(123), "Should correctly reverse a positive number.");
    }

    @Test
    void testReverseNumberNegative() {
        assertEquals(456, myMath.reverseNumber(-456), "Should correctly reverse a negative number.");
    }

    @Test
    void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0), "Should return 0 for zero.");
    }
}