package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyAdvancedMathTestGemma4_P1 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();

    @Test
    void testAddSuccessfulAddition() {
        assertEquals(15, myAdvancedMath.add(10, 5));
    }

    @Test
    void testAddWithNegativeInputX() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }

    @Test
    void testAddWithNegativeInputY() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(10, -5);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }

    @Test
    void testAddOverflow() {
        // Max Int value - 10 will still fit. Max Int + 1 will overflow.
        int x = Integer.MAX_VALUE - 1;
        int y = 2;
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y);
        });
        assertEquals("Cannot fit the result in an Integer.", thrown.getMessage());
    }

    @Test
    void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void testPowerOfTwoSmall() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
        assertEquals(16, myAdvancedMath.powerOfTwo(4));
        assertEquals(1024, myAdvancedMath.powerOfTwo(10));
    }

    @Test
    void testPowerOfTwoMax() {
        // 2^30
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }

    @Test
    void testPowerOfTwoNegativePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    @Test
    void testPowerOfTwoTooLargePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    @Test
    void testReverseArrayNumbers() {
        // The MyMath instance used here does not use mocks as per requirement
        MyMath myMath = new MyMath();
        int[] input = {1, 2, 3};
        int[] expected = {-1, -2, -3};

        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);

        for (int i = 0; i < 3; i++) {
            assertEquals(expected[i], result[i], "Element at index " + i + " mismatch");
        }
    }
}