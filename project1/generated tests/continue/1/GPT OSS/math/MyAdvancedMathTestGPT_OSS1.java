package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MyAdvancedMath}.
 */
class MyAdvancedMathTestGPT_OSS1 {

    private final MyAdvancedMath advancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @Test
    void add_validInputs_returnsSum() {
        assertEquals(15, advancedMath.add(7, 8));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(3, -2));
    }

    @Test
    void add_overflow_throwsIllegalArgumentException() {
        int large = Integer.MAX_VALUE - 10;
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(large, 20));
    }

    @Test
    void powerOfTwo_zero_returnsOne() {
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_withinBounds_returnsCorrectResult() {
        assertEquals(32, advancedMath.powerOfTwo(5));
        assertEquals(1 << 10, advancedMath.powerOfTwo(10));
    }

    @Test
    void powerOfTwo_negativeOrTooLarge_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_correctlyReversesSigns() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        int[] result = advancedMath.reverseArrayNumbers(input.clone(), myMath);
        assertArrayEquals(expected, result);
    }
}