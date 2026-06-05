package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MyAdvancedMathTestQwen3_P3 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "10, 10",
        "100, 100",
        "1000, 1000"
    })
    void add_NormalValues(int x, int y) {
        assertEquals(x + y, myAdvancedMath.add(x, y));
    }

    @Test
    void add_NegativeNumbersThrowException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(5, -1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, -1));
    }

    @Test
    void add_OverflowThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(1000000, Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 15, 20, 25, 30})
    void powerOfTwo_NormalValues(int power) {
        assertEquals(1 << power, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_ZeroReturnsOne() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_NegativePowerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-10));
    }

    @Test
    void powerOfTwo_PowerAbove30ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(50));
    }

    @Test
    void reverseArrayNumbers_NormalArray() {
        int[] array = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_EmptyArray() {
        int[] array = {};
        assertArrayEquals(new int[0], myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_SingleElementArray() {
        int[] array = {5};
        int[] expected = {-5};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }
}