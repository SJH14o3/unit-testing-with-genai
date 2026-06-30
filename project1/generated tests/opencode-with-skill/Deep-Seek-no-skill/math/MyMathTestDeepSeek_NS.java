package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MyMathTestDeepSeek_NS {

    @Test
    void testCheckSignPositive() {
        MyMath mm = new MyMath();
        assertEquals("positive", mm.checkSign(5));
    }

    @Test
    void testCheckSignNegative() {
        MyMath mm = new MyMath();
        assertEquals("negative", mm.checkSign(-3));
    }

    @Test
    void testCheckSignZeroThrowsException() {
        MyMath mm = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    void testReverseNumberPositive() {
        MyMath mm = new MyMath();
        assertEquals(-7, mm.reverseNumber(7));
    }

    @Test
    void testReverseNumberNegative() {
        MyMath mm = new MyMath();
        assertEquals(4, mm.reverseNumber(-4));
    }

    @Test
    void testReverseNumberZero() {
        MyMath mm = new MyMath();
        assertEquals(0, mm.reverseNumber(0));
    }
}
