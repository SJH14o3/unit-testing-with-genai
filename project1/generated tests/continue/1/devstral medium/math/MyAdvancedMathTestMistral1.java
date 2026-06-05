package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestMistral1 {

    @Test
    void testAddPositiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(15, advancedMath.add(10, 5));
    }

    @Test
    void testAddNegativeNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(10, -5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-10, -5));
    }

    @Test
    void testAddOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void testPowerOfTwo() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
        assertEquals(2, advancedMath.powerOfTwo(1));
        assertEquals(4, advancedMath.powerOfTwo(2));
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void testPowerOfTwoInvalid() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void testReverseArrayNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] input = {10, -5, 0};
        int[] expected = {-10, 5, 0};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(input, myMath));
    }
}
