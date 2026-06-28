package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MyMathTestGPTOSS_P1 {

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
    void testCheckSignZero() {
        MyMath mm = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    void testReverseNumber() {
        MyMath mm = new MyMath();
        assertEquals(-5, mm.reverseNumber(5));
        assertEquals(0, mm.reverseNumber(0));
        assertEquals(3, mm.reverseNumber(-3));
    }
}
