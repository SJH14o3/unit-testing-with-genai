package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestGemma4_P1 {

    private final MyMath myMath = new MyMath();

    @Test
    void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    void testCheckSignZero() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", thrown.getMessage());
    }

    @Test
    void testReverseNumberPositive() {
        assertEquals(-1234, myMath.reverseNumber(1234));
    }

    @Test
    void testReverseNumberNegative() {
        assertEquals(500, myMath.reverseNumber(-500));
    }

    @Test
    void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}