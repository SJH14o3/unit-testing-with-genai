package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MyAdvancedMathTestGPTOSS_P1 {

    @Test
    void testAddValid() {
        MyAdvancedMath ma = new MyAdvancedMath();
        assertEquals(12, ma.add(5, 7));
    }

    @Test
    void testAddNegative() {
        MyAdvancedMath ma = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> ma.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> ma.add(5, -3));
    }

    @Test
    void testAddOverflow() {
        MyAdvancedMath ma = new MyAdvancedMath();
        int x = Integer.MAX_VALUE - 10;
        int y = 20; // 20 > 10 so will overflow
        assertThrows(IllegalArgumentException.class, () -> ma.add(x, y));
    }

    @Test
    void testPowerOfTwoZero() {
        MyAdvancedMath ma = new MyAdvancedMath();
        assertEquals(1, ma.powerOfTwo(0));
    }

    @Test
    void testPowerOfTwoPositive() {
        MyAdvancedMath ma = new MyAdvancedMath();
        assertEquals(32, ma.powerOfTwo(5));
    }

    @Test
    void testPowerOfTwoOutOfBounds() {
        MyAdvancedMath ma = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> ma.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> ma.powerOfTwo(31));
    }

    @Test
    void testReverseArrayNumbers() {
        MyAdvancedMath ma = new MyAdvancedMath();
        MyMath mm = new MyMath();
        int[] input = new int[]{1, -2, 0, 3};
        int[] expected = new int[]{-1, 2, 0, -3};
        int[] actual = ma.reverseArrayNumbers(input.clone(), mm);
        assertArrayEquals(expected, actual);
    }
}
