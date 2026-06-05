package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyAdvancedMathTestQwen2_5_P2 {

    @Test
    public void testAddPositiveNumbers() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(3, myAdvancedMath.add(1, 2));
    }

    @Test
    public void testAddNegativeNumberThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 2), "Expected IllegalArgumentException for negative number");
    }

    @Test
    public void testAddOverflowThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1), "Expected IllegalArgumentException for overflow");
    }

    @Test
    public void testPowerOfTwoZero() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositive() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(256, myAdvancedMath.powerOfTwo(8));
    }

    @Test
    public void testPowerOfTwoNegativeThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1), "Expected IllegalArgumentException for negative power");
    }

    @Test
    public void testPowerOfTwoTooLargeThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31), "Expected IllegalArgumentException for too large power");
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1, -2, 3, -4};
        int[] expectedOutputArray = {-1, 2, -3, 4};
        int[] resultArray = myAdvancedMath.reverseArrayNumbers(inputArray, myAdvancedMath);
        assertArrayEquals(expectedOutputArray, resultArray);
    }
}