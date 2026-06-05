package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MyAdvancedMathTestGPT {

    private final MyAdvancedMath advancedMath = new MyAdvancedMath();

    @Test
    void add_positiveNumbers_returnsSum() {
        assertEquals(9, advancedMath.add(4, 5));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    }

    @Test
    void add_overflow_throwsIllegalArgumentException() {
        int nearLimit = Integer.MAX_VALUE;
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(nearLimit, 1));
    }

    @Test
    void powerOfTwo_validInputs_returnExpectedResults() {
        assertEquals(1, advancedMath.powerOfTwo(0));
        assertEquals(1024, advancedMath.powerOfTwo(10));
    }

    @Test
    void powerOfTwo_tooLarge_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void powerOfTwo_negative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-3));
    }

    @Test
    void reverseArrayNumbers_flipsEachValue() {
        int[] values = {1, -2, 3};
        int[] copy = values.clone();

        int[] result = advancedMath.reverseArrayNumbers(copy, new MyMath());

        assertArrayEquals(new int[]{-1, 2, -3}, result);
        assertArrayEquals(result, copy);
    }
}
