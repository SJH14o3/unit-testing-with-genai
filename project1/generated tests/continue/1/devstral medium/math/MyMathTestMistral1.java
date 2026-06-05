package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestMistral1 {

    @Test
    void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    void testCheckSignZero() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    void testReverseNumber() {
        MyMath myMath = new MyMath();
        assertEquals(-10, myMath.reverseNumber(10));
        assertEquals(10, myMath.reverseNumber(-10));
        assertEquals(0, myMath.reverseNumber(0));
    }
}
