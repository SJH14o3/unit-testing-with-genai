package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestMistral_P2 {

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
    public void testCheckSignZero() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    public void testReverseNumberPositive() {
        MyMath myMath = new MyMath();
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        MyMath myMath = new MyMath();
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        MyMath myMath = new MyMath();
        assertEquals(0, myMath.reverseNumber(0));
    }
}
