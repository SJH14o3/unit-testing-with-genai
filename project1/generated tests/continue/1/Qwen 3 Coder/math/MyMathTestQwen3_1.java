package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyMath
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class MyMathTestQwen3_1 {

    private MyMath myMath = new MyMath();

    @Test
    public void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    public void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    public void testCheckSignZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myMath.checkSign(0));
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    public void testReverseNumberPositive() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        assertEquals(3, myMath.reverseNumber(-3));
    }

    @Test
    public void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}