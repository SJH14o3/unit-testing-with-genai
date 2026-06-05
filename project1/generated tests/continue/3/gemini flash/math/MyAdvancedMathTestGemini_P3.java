package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MyAdvancedMathTestGemini_P3 {

    private final MyAdvancedMath am = new MyAdvancedMath();
    private final MyMath mm = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, 1, 2",
        "0, 0, 0",
        "100, 200, 300",
        "2147483647, 0, 2147483647"
    })
    void testAdd_NormalValues(int x, int y, int expected) {
        Assertions.assertEquals(expected, am.add(x, y));
    }

    @Test
    void testAdd_XNegative_MCDC() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(-1, 0));
    }

    @Test
    void testAdd_YNegative_MCDC() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(0, -1));
    }

    @Test
    void testAdd_Overflow_Boundary() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void testAdd_JustAtMax_Boundary() {
        Assertions.assertEquals(Integer.MAX_VALUE, am.add(Integer.MAX_VALUE, 0));
    }

    @Test
    void testAdd_JustBelowMax_Boundary() {
        Assertions.assertEquals(Integer.MAX_VALUE - 1, am.add(Integer.MAX_VALUE - 1, 0));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "2, 4",
        "5, 32",
        "10, 1024"
    })
    void testPowerOfTwo_NormalValues(int power, int expected) {
        Assertions.assertEquals(expected, am.powerOfTwo(power));
    }

    @Test
    void testPowerOfTwo_Zero_Boundary() {
        Assertions.assertEquals(1, am.powerOfTwo(0));
    }

    @Test
    void testPowerOfTwo_One_Boundary() {
        Assertions.assertEquals(2, am.powerOfTwo(1));
    }

    @Test
    void testPowerOfTwo_Negative_Boundary() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> am.powerOfTwo(-1));
    }

    @Test
    void testPowerOfTwo_30_Boundary() {
        Assertions.assertEquals(1073741824, am.powerOfTwo(30));
    }

    @Test
    void testPowerOfTwo_29_Boundary() {
        Assertions.assertEquals(536870912, am.powerOfTwo(29));
    }

    @Test
    void testPowerOfTwo_31_Boundary() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> am.powerOfTwo(31));
    }

    @Test
    void testReverseArrayNumbers_Normal() {
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, am.reverseArrayNumbers(input, mm));
    }

    @Test
    void testReverseArrayNumbers_Empty_Edge() {
        int[] input = {};
        int[] expected = {};
        Assertions.assertArrayEquals(expected, am.reverseArrayNumbers(input, mm));
    }
}
