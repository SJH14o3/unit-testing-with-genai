package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class MyAdvancedMathTestQwen2_5_P3 {

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "0, 0, 0"
    })
    public void testAdd_positiveNumbers(int x, int y, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    public void testAdd_negativeNumber() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 2);
        });
        assertTrue(exception.getMessage().contains("Input numbers should be positive."));
    }

    @Test
    public void testAdd_integerOverflow() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(Integer.MAX_VALUE - 1, 2);
        });
        assertTrue(exception.getMessage().contains("Cannot fit the result in an Integer."));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 1",
        "5, 0"
    })
    public void testPowerOfTwo_validPower(int power, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    public void testPowerOfTwo_negativePower() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertTrue(exception.getMessage().contains("power should be 0<=power<=30"));
    }

    @Test
    public void testPowerOfTwo_powerAboveLimit() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertTrue(exception.getMessage().contains("power should be 0<=power<=30"));
    }

    @Test
    public void testReverseArrayNumbers_emptyArray() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    public void testReverseArrayNumbers_arrayWithSingleElement() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{-1}, result);
    }

    @Test
    public void testReverseArrayNumbers_arrayWithMultipleElements() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1, 2, 3};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{-1, -2, -3}, result);
    }
}