package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MyAdvancedMath.
 * @author Gemma 4
 */
public class MyAdvancedMathTestGemma4_P2 {

    private MyMath myMath;
    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
        myAdvancedMath = new MyAdvancedMath();
    }

    // --- Tests for add(int x, int y) ---

    @Test
    void add_TwoPositiveNumbers() {
        assertEquals(10, myAdvancedMath.add(3, 7));
    }

    @Test
    void add_BoundaryOneAndOne() {
        assertEquals(2, myAdvancedMath.add(1, 1));
    }

    @Test
    void add_BoundaryTwoAndZero() {
        // Although the method checks for positivity, we test the boundary relative to zero inputs if allowed,
        // but since the constraint is positive, we test small positive numbers near zero.
        assertEquals(2, myAdvancedMath.add(1, 1)); // Re-using 1,1 as small positive test
    }

    @Test
    void add_OneNumberNearMax() {
        int x = Integer.MAX_VALUE - 1;
        assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(x, 1));
    }

    @Test
    void add_NegativeInputX() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_NegativeInputY() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_OverflowCondition() {
        // Test case that causes overflow: x + y > Integer.MAX_VALUE
        int x = Integer.MAX_VALUE / 2 + 1;
        int y = Integer.MAX_VALUE / 2 + 1;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y);
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @Test
    void powerOfTwo_ZeroPower() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_PowerOfOne() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_PowerOfTwo() {
        assertEquals(4, myAdvancedMath.powerOfTwo(2));
    }
    
    @Test
    void powerOfTwo_PowerOfThree() {
        assertEquals(8, myAdvancedMath.powerOfTwo(3));
    }

    @Test
    void powerOfTwo_BoundaryMaxPower() {
        // Testing the highest allowed power (30)
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }
    
    @Test
    void powerOfTwo_NegativePower() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }
    
    @Test
    void powerOfTwo_AboveMaxPower() {
        // Testing power 31
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    void reverseArrayNumbers_NormalCase() {
        int[] input = {10, 20, 30};
        int[] expected = {-10, -20, -30};
        
        myAdvancedMath.reverseArrayNumbers(input, myMath);
        
        assertArrayEquals(expected, input);
    }

    @Test
    void reverseArrayNumbers_WithNegativeNumbers() {
        int[] input = {-5, 10, -15};
        int[] expected = {5, -10, 15};

        myAdvancedMath.reverseArrayNumbers(input, myMath);
        
        assertArrayEquals(expected, input);
    }

    @Test
    void reverseArrayNumbers_EmptyArray() {
        int[] input = {};
        // Should return the array unchanged
        myAdvancedMath.reverseArrayNumbers(input, myMath);
        
        assertArrayEquals(new int[]{}, input);
    }
}