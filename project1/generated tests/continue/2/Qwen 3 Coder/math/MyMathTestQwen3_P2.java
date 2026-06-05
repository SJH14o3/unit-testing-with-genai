package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestQwen3_P2 {

    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("Test checkSign with positive number")
    public void testCheckSignWithPositiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    @DisplayName("Test checkSign with negative number")
    public void testCheckSignWithNegativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    @DisplayName("Test checkSign with zero - should throw IllegalArgumentException")
    public void testCheckSignWithZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    @DisplayName("Test reverseNumber with positive number")
    public void testReverseNumberWithPositiveNumber() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    @DisplayName("Test reverseNumber with negative number")
    public void testReverseNumberWithNegativeNumber() {
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    @DisplayName("Test reverseNumber with zero")
    public void testReverseNumberWithZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}