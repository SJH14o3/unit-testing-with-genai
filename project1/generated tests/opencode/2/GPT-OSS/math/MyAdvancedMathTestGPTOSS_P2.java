package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MyAdvancedMathTestGPTOSS_P2 {

    private final MyAdvancedMath math = new MyAdvancedMath();

    @Test
    void add_positiveNumbers() {
        assertEquals(5, math.add(2,3));
    }

    @Test
    void add_negativeInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> math.add(-1,5));
    }

    @Test
    void add_overflowThrows() {
        int big = Integer.MAX_VALUE - 1;
        assertThrows(IllegalArgumentException.class, () -> math.add(big,2));
    }

    @Test
    void powerOfTwo_zero() {
        assertEquals(1, math.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_maxBoundary() {
        assertEquals(1073741824, math.powerOfTwo(30)); // 2^30
    }

    @Test
    void powerOfTwo_negativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_overflowThrows() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_empty() {
        int[] input = new int[]{};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    void reverseArrayNumbers_normal() {
        int[] input = new int[]{-1,0,5};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(new int[]{1,0,-5}, result);
    }
}
