package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyAdvancedMathTestMistral_P2 {

    @Test
    public void testAddPositiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    public void testAddNegativeNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    }

    @Test
    public void testAddOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositive() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(8, advancedMath.powerOfTwo(3));
    }

    @Test
    public void testPowerOfTwoNegative() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    public void testPowerOfTwoOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {};
        int[] expected = {};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(array, myMath));
    }
}
