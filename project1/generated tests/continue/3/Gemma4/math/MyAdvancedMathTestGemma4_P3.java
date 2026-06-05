// Content for MyAdvancedMathTestGemma4_P3.java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class MyAdvancedMathTestGemma4_P3 {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    // --- Tests for add(int x, int y) ---

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "100, 200",
        "Integer.MAX_VALUE - 1, 1" // Just below overflow
    })
    @DisplayName("Should correctly add two positive integers")
    void add_Positive(int x, int y) {
        assertEquals(x + y, myAdvancedMath.add(x, y));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when first number is negative (Boundary Test)")
    void add_FirstNegative() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when second number is negative (Boundary Test)")
    void add_SecondNegative() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException upon overflow (Boundary Test)")
    void add_Overflow() {
        // Test case where x + y > Integer.MAX_VALUE
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(Integer.MAX_VALUE, 1);
        });
        assertEquals("Cannot fit the result in an Integer.", thrown.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @Test
    @DisplayName("Should return 1 when power is 0 (Boundary Test)")
    void powerOfTwo_Zero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "10, 1024",
        "30, 1073741824" // Boundary high
    })
    @DisplayName("Should calculate power of two correctly for valid inputs")
    void powerOfTwo_Valid(int power, int expected) {
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when power is -1 (Boundary Test)")
    void powerOfTwo_NegativePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when power is 31 (Boundary Test)")
    void powerOfTwo_TooLargePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    @DisplayName("Should reverse signs of all elements in an array")
    void reverseArrayNumbers_MixedSigns() {
        int[] input = {1, -2, 3, -4};
        // Expected: [-1, 2, -3, 4]
        int[] expected = {-1, 2, -3, 4};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle an array containing only zero")
    void reverseArrayNumbers_OnlyZeros() {
        int[] input = {0, 0, 0};
        int[] expected = {0, 0, 0};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return the array unchanged when it is empty")
    void reverseArrayNumbers_EmptyArray() {
        int[] input = {};
        int[] expected = {};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }
}