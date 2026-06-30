package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MyAdvancedMathTestDeepSeek_NS {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @ParameterizedTest
    @CsvSource({"1, 2, 3", "0, 5, 5", "100, 200, 300", "2147483646, 1, 2147483647"})
    void testAddValidInputs(int x, int y, int expected) {
        assertEquals(expected, mam.add(x, y));
    }

    @Test
    void testAddNegativeXThrows() {
        assertThrows(IllegalArgumentException.class, () -> mam.add(-1, 5));
    }

    @Test
    void testAddNegativeYThrows() {
        assertThrows(IllegalArgumentException.class, () -> mam.add(5, -1));
    }

    @Test
    void testAddBothNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> mam.add(-1, -1));
    }

    @Test
    void testAddOverflowThrows() {
        assertThrows(IllegalArgumentException.class, () -> mam.add(Integer.MAX_VALUE, 1));
    }

    @ParameterizedTest
    @CsvSource({"0, 1", "1, 2", "2, 4", "3, 8", "4, 16", "5, 32", "10, 1024", "30, 1073741824"})
    void testPowerOfTwoValidInputs(int power, int expected) {
        assertEquals(expected, mam.powerOfTwo(power));
    }

    @Test
    void testPowerOfTwoNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(-1));
    }

    @Test
    void testPowerOfTwoAbove30Throws() {
        assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(31));
    }

    @Test
    void testReverseArrayNumbers() {
        int[] input = {1, -2, 3, -4, 0};
        int[] expected = {-1, 2, -3, 4, 0};
        assertArrayEquals(expected, mam.reverseArrayNumbers(input, new MyMath()));
    }

    @Test
    void testReverseArrayNumbersEmpty() {
        int[] input = {};
        assertArrayEquals(new int[]{}, mam.reverseArrayNumbers(input, new MyMath()));
    }
}
