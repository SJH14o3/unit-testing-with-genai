// MyAdvancedMathTestGPT_OSS_P2.java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MyAdvancedMathTestGPT_OSS_P2 {

    private final MyAdvancedMath adv = new MyAdvancedMath();

    @Test
    void add_validInputs() {
        assertEquals(5, adv.add(2, 3));
    }

    @Test
    void add_negativeInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> adv.add(-1, 5));
    }

    @Test
    void add_overflowThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void powerOfTwo_zero() {
        assertEquals(1, adv.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_one() {
        assertEquals(2, adv.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_maxBoundary() {
        assertEquals(1073741824, adv.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_negativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_aboveMaxThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        assertArrayEquals(new int[0],
                adv.reverseArrayNumbers(new int[0], new MyMath()));
    }

    @Test
    void reverseArrayNumbers_mixedValues() {
        int[] arr = {1, -2, 0};
        assertArrayEquals(new int[]{-1, 2, 0},
                adv.reverseArrayNumbers(arr, new MyMath()));
    }
}