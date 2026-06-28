package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Collections;

public class MyAdvancedMathTestGemma4_P2 {

    MyAdvancedMath advancedMath;
    MyMath simpleMath;

    @BeforeEach
    void setUp() {
        advancedMath = new MyAdvancedMath();
        simpleMath = new MyMath();
    }

    @Test
    void testAddPositiveNumbers() {
        int result = advancedMath.add(1, 2);
        assertEquals(3, result);
    }

    @Test
    void testAddBoundary_SmallestPositive() {
        int result = advancedMath.add(1, 1);
        assertEquals(2, result);
    }

    @Test
    void testAddNegativeInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            advancedMath.add(-1, 1);
        }, "Should throw IllegalArgumentException for negative input.");
    }

    @Test
    void testAddOverflowBoundary() {
        // Test case where addition will cause overflow
        assertThrows(IllegalArgumentException.class, () -> {
            advancedMath.add(Integer.MAX_VALUE, 1);
        }, "Should throw IllegalArgumentException on overflow.");
    }

    @Test
    void testPowerOfTwoZeroPower() {
        int result = advancedMath.powerOfTwo(0);
        assertEquals(1, result);
    }

    @Test
    void testPowerOfTwoOnePower() {
        int result = advancedMath.powerOfTwo(1);
        assertEquals(2, result);
    }

    @Test
    void testPowerOfTwoMaxPower() {
        int result = advancedMath.powerOfTwo(30);
        assertEquals(1073741824, result); // 2^30
    }

    @Test
    void testPowerOfTwoBoundary_TooLarge() {
        assertThrows(IllegalArgumentException.class, () -> {
            advancedMath.powerOfTwo(31);
        }, "Should throw IllegalArgumentException for power > 30.");
    }

    @Test
    void testPowerOfTwoBoundary_Negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            advancedMath.powerOfTwo(-1);
        }, "Should throw IllegalArgumentException for negative power.");
    }
    
    @Test
    void testReverseArrayNumbersEmptyArray() {
        int[] array = new int[0];
        int[] result = advancedMath.reverseArrayNumbers(array, simpleMath);
        
        // Asserting on the passed array content
        assertTrue(result.length == 0);
    }

    @Test
    void testReverseArrayNumbersMixedSigns() {
        int[] array = {1, -2, 3}; // Original: {1, -2, 3}. Expected: {-1, 2, -3}
        int[] expected = {-1, 2, -3};
        
        // NOTE: reverseArrayNumbers modifies array IN PLACE
        advancedMath.reverseArrayNumbers(array, simpleMath); 
        
        assertArrayEquals(expected, array, "Array elements should have reversed signs.");
    }
    
    @Test
    void testReverseArrayNumbersSingleElement() {
        int[] array = {-5}; // Original: {-5}. Expected: {5}
        advancedMath.reverseArrayNumbers(array, simpleMath);
        assertEquals(5, array[0]);
    }
}