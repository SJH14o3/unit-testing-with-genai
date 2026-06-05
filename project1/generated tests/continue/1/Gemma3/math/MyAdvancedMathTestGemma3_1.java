package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestGemma3_1 {

    @Test
    void add_positiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumberInput() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-2, 3));
    }

    @Test
    void add_overflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void powerOfTwo_validPower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(16, advancedMath.powerOfTwo(4));
    }

    @Test
    void powerOfTwo_zeroPower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_negativePower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_powerTooLarge() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_validArray() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        int[] array = {1, 2, 3};
        int[] reversedArray = advancedMath.reverseArrayNumbers(array, math);
        assertArrayEquals(new int[]{-1, -2, -3}, reversedArray);
    }
}
