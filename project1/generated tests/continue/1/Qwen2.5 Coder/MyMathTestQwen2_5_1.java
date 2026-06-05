import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dmst.seip.angor.junitdemo.math.MyMath;
import dmst.seip.angor.junitdemo.math.MyAdvancedMath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyMathTestQwen2_5_1 {
    private MyMath myMath;
    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    public void setUp() {
        myMath = new MyMath();
        myAdvancedMath = new MyAdvancedMath();
    }

    @Test
    public void testCheckSignPositiveNumber() {
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    public void testCheckSignNegativeNumber() {
        assertEquals("negative", myMath.checkSign(-20));
    }

    @Test
    public void testCheckSignZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    public void testReverseNumber() {
        assertEquals(-15, myMath.reverseNumber(15));
    }

    @Test
    public void testAddPositiveNumbers() {
        int result = myAdvancedMath.add(10, 20);
        assertEquals(30, result);
    }

    @Test
    public void testAddNegativeNumbersThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-5, -10));
    }

    @Test
    public void testAddIntegerOverflowThrowsException() {
        int maxInt = Integer.MAX_VALUE;
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(maxInt, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositiveNumber() {
        assertEquals(16, myAdvancedMath.powerOfTwo(4));
    }

    @Test
    public void testPowerOfTwoNegativeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-2));
    }

    @Test
    public void testPowerOfTwoTooLargeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }
}
