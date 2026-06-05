package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestQwen2_5_P3 {

    @Test
    public void testCheckSign_positiveNumber() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    public void testCheckSign_negativeNumber() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-10));
    }

    @Test
    public void testCheckSign_zero() {
        MyMath myMath = new MyMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertTrue(exception.getMessage().contains("Number had no sign"));
    }

    @Test
    public void testReverseNumber_positiveNumber() {
        MyMath myMath = new MyMath();
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    public void testReverseNumber_negativeNumber() {
        MyMath myMath = new MyMath();
        assertEquals(10, myMath.reverseNumber(-10));
    }
}