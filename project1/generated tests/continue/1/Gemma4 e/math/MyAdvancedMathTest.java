package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Unit tests for the MyAdvancedMath class.
 * Must end with TestGemma4_1
 */
public class MyAdvancedMathTest {

    private MyAdvancedMath myAdvancedMath;
    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
        myMath = new MyMath();
    }

    @Test
    void testAddPositiveNumbers() {
        // Test case 1: Simple addition
        assertEquals(5, myAdvancedMath.add(2, 3), "Should correctly add two small positive integers.");
        // Test case 2: Larger numbers
        assertEquals(100, myAdvancedMath.add(50, 50), "Should handle larger positive integers.");
    }

    @Test
    void testAddWithNegativeNumber() {
        // Testing for IllegalArgumentException when one or more inputs are negative
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void testAddOverflow() {
        // Testing for Integer overflow
        // MAX_VALUE is 2,147,483,647. (MAX_VALUE - 1) + 2 > MAX_VALUE
        int x = Integer.MAX_VALUE - 5;
        int y = 10;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y + 1); // This will overflow
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0), "Power of two for 0 should be 1.");
    }

    @Test
    void testPowerOfTwoPositive() {
        assertEquals(16, myAdvancedMath.powerOfTwo(4), "2^4 should be 16.");
        assertEquals(1024, myAdvancedMath.powerOfTwo(10), "2^10 should be 1024.");
    }

    @Test
    void testPowerOfTwoNegative() {
        // Testing for IllegalArgumentException when power is negative
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void testPowerOfTwoTooLarge() {
        // Testing for IllegalArgumentException when power > 30
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void testReverseArrayNumbers() {
        // Input array: {1, 2, 3}
        int[] originalArray = {1, 2, 3};
        // Expected: {-1, -2, -3}
        int[] expected = {-1, -2, -3};
        
        // We must copy the array to ensure the original state isn't unexpectedly changed by the method signature
        int[] arrayCopy = Arrays.copyOf(originalArray, originalArray.length);

        int[] actual = myAdvancedMath.reverseArrayNumbers(arrayCopy, myMath);
        
        assertArrayEquals(expected, actual, "Should reverse the sign of every element in the array.");
    }
}