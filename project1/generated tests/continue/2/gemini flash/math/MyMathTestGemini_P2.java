package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyMathTestGemini_P2 {

    private final MyMath mm = new MyMath();

    @Test
    public void testCheckSignPositive() {
        Assertions.assertEquals("positive", mm.checkSign(1));
    }

    @Test
    public void testCheckSignPositiveMoreThanOne() {
        Assertions.assertEquals("positive", mm.checkSign(2));
    }

    @Test
    public void testCheckSignNegative() {
        Assertions.assertEquals("negative", mm.checkSign(-1));
    }

    @Test
    public void testCheckSignNegativeLessThanMinusOne() {
        Assertions.assertEquals("negative", mm.checkSign(-2));
    }

    @Test
    public void testCheckSignZeroThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    public void testReverseNumberPositive() {
        Assertions.assertEquals(-5, mm.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        Assertions.assertEquals(5, mm.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        Assertions.assertEquals(0, mm.reverseNumber(0));
    }
}
