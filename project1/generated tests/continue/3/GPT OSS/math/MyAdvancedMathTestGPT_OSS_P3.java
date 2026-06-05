// src/test/java/dmst/seip/angor/junitdemo/math/MyAdvancedMathTestGPT_OSS_P3.java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestGPT_OSS_P3 {

    private final MyAdvancedMath math = new MyAdvancedMath();

    // ------------------------------------------------------------------------
    // add – valid, negative, and overflow cases
    // ------------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(ints = {1, 42, 1000})
    void add_validNumbers_returnsSum(int x) {
        int y = 10;
        assertEquals(x + y, math.add(x, y));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
    }

    @Test
    void add_overflowInput_throwsIllegalArgumentException() {
        int x = Integer.MAX_VALUE - 1;
        int y = 3; // overflow
        assertThrows(IllegalArgumentException.class, () -> math.add(x, y));
    }

    // ------------------------------------------------------------------------
    // powerOfTwo – boundary and normal values
    // ------------------------------------------------------------------------

    @Test
    void powerOfTwo_zero_returnsOne() {
        assertEquals(1, math.powerOfTwo(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 10})
    void powerOfTwo_normalValues_returnsCorrectPower(int power) {
        int expected = 1 << power; // 2^power
        assertEquals(expected, math.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_negativePower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_exceedingPower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
    }

    // ------------------------------------------------------------------------
    // reverseArrayNumbers – empty, normal, mixed values
    // ------------------------------------------------------------------------

    @Test
    void reverseArrayNumbers_emptyArray_returnsEmptyArray() {
        int[] input = {};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    void reverseArrayNumbers_normalArray_reversesSigns() {
        int[] input = {1, -2, 0, 5};
        int[] expected = {-1, 2, 0, -5};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(expected, result);
    }
}