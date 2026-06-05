package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyAdvancedMath
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class MyAdvancedMathTestQwen3_1 {

    private MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private MyMath myMath = new MyMath();

    @Test
    public void testAddPositiveNumbers() {
        assertEquals(8, myAdvancedMath.add(3, 5));
    }

    @Test
    public void testAddNegativeNumbers() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.add(-3, 5));
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    public void testAddOverflow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    public void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoValid() {
        assertEquals(8, myAdvancedMath.powerOfTwo(3));
    }

    @Test
    public void testPowerOfTwoNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.powerOfTwo(-1));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    public void testPowerOfTwoTooLarge() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.powerOfTwo(31));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    public void testReverseArrayNumbers() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }
}