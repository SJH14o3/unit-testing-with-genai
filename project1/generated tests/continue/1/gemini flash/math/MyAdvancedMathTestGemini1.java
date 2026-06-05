package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyAdvancedMathTestGemini1 {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @Test
    public void testAddSuccess() {
        Assertions.assertEquals(10, mam.add(4, 6));
    }

    @Test
    public void testAddNegativeFirst() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(-1, 5);
        });
    }

    @Test
    public void testAddNegativeSecond() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(5, -1);
        });
    }

    @Test
    public void testAddOverflow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(Integer.MAX_VALUE, 1);
        });
    }

    @Test
    public void testPowerOfTwoSuccess() {
        Assertions.assertEquals(1, mam.powerOfTwo(0));
        Assertions.assertEquals(2, mam.powerOfTwo(1));
        Assertions.assertEquals(1024, mam.powerOfTwo(10));
        Assertions.assertEquals(1073741824, mam.powerOfTwo(30));
    }

    @Test
    public void testPowerOfTwoNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.powerOfTwo(-1);
        });
    }

    @Test
    public void testPowerOfTwoTooLarge() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.powerOfTwo(31);
        });
    }

    @Test
    public void testReverseArrayNumbers() {
        MyMath mm = new MyMath();
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }
}
