package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyMathTestQwen2_5_P2 {

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    public void testCheckSignZeroThrowsException() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0), "Expected IllegalArgumentException for zero");
    }

    @Test
    public void testReverseNumberPositive() {
        MyMath myMath = new MyMath();
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    public void testReverseNumberNegative() {
        MyMath myMath = new MyMath();
        assertEquals(1, myMath.reverseNumber(-1));
    }
}