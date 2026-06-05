package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestMistral_P3 {

    @Test
    void add_WhenXIsNegative_ThrowsIllegalArgumentException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 1));
    }

    @Test
    void add_WhenYIsNegative_ThrowsIllegalArgumentException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(1, -1));
    }

    @Test
    void add_WhenBothAreNegative_ThrowsIllegalArgumentException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, -1));
    }

    @Test
    void add_WhenOverflow_ThrowsIllegalArgumentException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
    }

    @ParameterizedTest
    @CsvSource({"0, 0, 0", "1, 1, 2", "10, 20, 30", "100, 200, 300"})
    void add_WhenValidInputs_ReturnsSum(int x, int y, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    void powerOfTwo_WhenPowerIsNegative_ThrowsIllegalArgumentException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_WhenPowerIsGreaterThan30_ThrowsIllegalArgumentException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }

    @ParameterizedTest
    @CsvSource({"0, 1", "1, 2", "2, 4", "3, 8", "10, 1024", "30, 1073741824"})
    void powerOfTwo_WhenValidPower_ReturnsCorrectResult(int power, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    void reverseArrayNumbers_WhenEmptyArray_ReturnsEmptyArray() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {};
        assertArrayEquals(new int[]{}, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_WhenSingleElement_ReturnsReversedArray() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {5};
        assertArrayEquals(new int[]{-5}, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_WhenMultipleElements_ReturnsReversedArray() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {1, -2, 3, -4};
        assertArrayEquals(new int[]{-1, 2, -3, 4}, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }
}
