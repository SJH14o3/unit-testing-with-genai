package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class MyAdvancedMathTestQwen3_P2 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("Test add with normal positive numbers")
    public void testAddWithNormalNumbers() {
        assertEquals(10, myAdvancedMath.add(3, 7));
    }

    @Test
    @DisplayName("Test add with negative first number - should throw IllegalArgumentException")
    public void testAddWithNegativeFirstNumber() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
    }

    @Test
    @DisplayName("Test add with negative second number - should throw IllegalArgumentException")
    public void testAddWithNegativeSecondNumber() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(5, -1));
    }

    @Test
    @DisplayName("Test add with both negative numbers - should throw IllegalArgumentException")
    public void testAddWithBothNegativeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-2, -3));
    }

    @Test
    @DisplayName("Test add with overflow - should throw IllegalArgumentException")
    public void testAddWithOverflow() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("Test powerOfTwo with zero power")
    public void testPowerOfTwoWithZeroPower() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    @DisplayName("Test powerOfTwo with power 1")
    public void testPowerOfTwoWithPower1() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
    }

    @Test
    @DisplayName("Test powerOfTwo with maximum allowed power")
    public void testPowerOfTwoWithMaximumPower() {
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }

    @Test
    @DisplayName("Test powerOfTwo with negative power - should throw IllegalArgumentException")
    public void testPowerOfTwoWithNegativePower() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
    }

    @Test
    @DisplayName("Test powerOfTwo with power greater than 30 - should throw IllegalArgumentException")
    public void testPowerOfTwoWithPowerGreaterThan30() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with normal array")
    public void testReverseArrayNumbersWithNormalArray() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with empty array")
    public void testReverseArrayNumbersWithEmptyArray() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with single element")
    public void testReverseArrayNumbersWithSingleElement() {
        int[] input = {5};
        int[] expected = {-5};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with zero element")
    public void testReverseArrayNumbersWithZeroElement() {
        int[] input = {0};
        int[] expected = {0};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }
}