package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(value = "dmst.seip.angor.junitdemo.math.MyAdvancedMathTestGemma3_P3")
public class MyAdvancedMathTestGemma3_P3 {

    private MyAdvancedMath advancedMath;
    private MyMath math;

    @Test
    void add_positiveNumbers() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumber_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-2, 3));
    }

    @Test
    void add_zeroNumber_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(2, -3));
    }

    @Test
    void powerOfTwo_zeroPower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_onePower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(2, advancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_smallPower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(8, advancedMath.powerOfTwo(3));
    }

    @Test
    void powerOfTwo_largePower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_negativePower_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_powerGreaterThanThirty_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_validArray() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        int[] inputArray = {1, 2, 3};
        int[] expectedArray = {-1, -2, -3};
        int[] actualArray = advancedMath.reverseArrayNumbers(inputArray, math);
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        int[] inputArray = {};
        int[] expectedArray = {};
        int[] actualArray = advancedMath.reverseArrayNumbers(inputArray, math);
        assertArrayEquals(expectedArray, actualArray);
    }
}