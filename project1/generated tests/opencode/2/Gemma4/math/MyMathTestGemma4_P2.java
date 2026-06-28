package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestGemma4_P2 {

    MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    @Test
    void testCheckSignPositive() {
        String result = myMath.checkSign(1);
        assertEquals("positive", result);
    }

    @Test
    void testCheckSignNegative() {
        String result = myMath.checkSign(-1);
        assertEquals("negative", result);
    }

    @Test
    void testCheckSignBoundary_Positive() {
        String result = myMath.checkSign(Integer.MAX_VALUE);
        assertEquals("positive", result);
    }

    @Test
    void testCheckSignBoundary_Negative() {
        String result = myMath.checkSign(Integer.MIN_VALUE);
        assertEquals("negative", result);
    }

    @Test
    void testCheckSignBoundary_Zero() {
        assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        }, "Expected IllegalArgumentException for number zero");
    }

    @Test
    void testReverseNumberPositive() {
        int result = myMath.reverseNumber(5);
        assertEquals(-5, result);
    }

    @Test
    void testReverseNumberNegative() {
        int result = myMath.reverseNumber(-5);
        assertEquals(5, result);
    }

    @Test
    void testReverseNumberZero() {
        int result = myMath.reverseNumber(0);
        assertEquals(0, result);
    }
}