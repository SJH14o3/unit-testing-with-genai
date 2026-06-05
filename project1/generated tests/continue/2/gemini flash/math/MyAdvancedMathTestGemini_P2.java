package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyAdvancedMathTestGemini_P2 {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @Test
    public void testAddBothZero() {
        Assertions.assertEquals(0, mam.add(0, 0));
    }

    @Test
    public void testAddOneZeroFirst() {
        Assertions.assertEquals(5, mam.add(0, 5));
    }

    @Test
    public void testAddOneZeroSecond() {
        Assertions.assertEquals(5, mam.add(5, 0));
    }

    @Test
    public void testAddNormalPositive() {
        Assertions.assertEquals(10, mam.add(4, 6));
    }

    @Test
    public void testAddNegativeXThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(-1, 5));
    }

    @Test
    public void testAddNegativeYThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(5, -1));
    }

    @Test
    public void testAddOverflowBoundary() {
        Assertions.assertEquals(Integer.MAX_VALUE, mam.add(Integer.MAX_VALUE, 0));
    }

    @Test
    public void testAddOverflowThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        Assertions.assertEquals(1, mam.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoOne() {
        Assertions.assertEquals(2, mam.powerOfTwo(1));
    }

    @Test
    public void testPowerOfTwoThirty() {
        Assertions.assertEquals(1073741824, mam.powerOfTwo(30));
    }

    @Test
    public void testPowerOfTwoNegativeThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(-1));
    }

    @Test
    public void testPowerOfTwoThirtyOneThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(31));
    }

    @Test
    public void testReverseArrayNumbersNormal() {
        MyMath mm = new MyMath();
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        MyMath mm = new MyMath();
        int[] input = {};
        int[] expected = {};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }
}
