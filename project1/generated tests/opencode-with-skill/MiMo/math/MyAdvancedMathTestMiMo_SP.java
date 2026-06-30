package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MyAdvancedMath Tests")
class MyAdvancedMathTestMiMo_SP {

    private final MyAdvancedMath math = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    // --- add tests ---

    @Test
    @DisplayName("add returns sum of two positive integers")
    void add_shouldReturnSum_whenBothPositive() {
        assertEquals(5, math.add(2, 3));
    }

    @Test
    @DisplayName("add returns zero when both inputs are zero")
    void add_shouldReturnZero_whenBothZero() {
        assertEquals(0, math.add(0, 0));
    }

    @Test
    @DisplayName("add throws exception when first number is negative")
    void add_shouldThrowException_whenFirstIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
    }

    @Test
    @DisplayName("add throws exception when second number is negative")
    void add_shouldThrowException_whenSecondIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> math.add(5, -1));
    }

    @Test
    @DisplayName("add throws exception on integer overflow")
    void add_shouldThrowException_whenOverflow() {
        assertThrows(IllegalArgumentException.class, () -> math.add(Integer.MAX_VALUE, 1));
    }

    // --- powerOfTwo tests ---

    @ParameterizedTest(name = "powerOfTwo({0}) should return correct result")
    @CsvSource({
        "0, 1",
        "1, 2",
        "2, 4",
        "10, 1024",
        "30, 1073741824"
    })
    @DisplayName("powerOfTwo returns correct result for valid powers")
    void powerOfTwo_shouldReturnCorrectResult_whenPowerIsWithinRange(int power, int expected) {
        assertEquals(expected, math.powerOfTwo(power));
    }

    @Test
    @DisplayName("powerOfTwo throws exception when power is negative")
    void powerOfTwo_shouldThrowException_whenPowerIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
    }

    @Test
    @DisplayName("powerOfTwo throws exception when power is greater than 30")
    void powerOfTwo_shouldThrowException_whenPowerExceeds30() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
    }

    // --- reverseArrayNumbers tests ---

    @Test
    @DisplayName("reverseArrayNumbers reverses sign of each element")
    void reverseArrayNumbers_shouldReverseSignsOfAllElements() {
        int[] input = {1, -2, 3, 0};
        int[] result = math.reverseArrayNumbers(input, myMath);
        assertArrayEquals(new int[]{-1, 2, -3, 0}, result);
    }

    @Test
    @DisplayName("reverseArrayNumbers handles empty array")
    void reverseArrayNumbers_shouldHandleEmptyArray() {
        int[] input = {};
        int[] result = math.reverseArrayNumbers(input, myMath);
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    @DisplayName("reverseArrayNumbers handles single element array")
    void reverseArrayNumbers_shouldHandleSingleElementArray() {
        int[] input = {42};
        int[] result = math.reverseArrayNumbers(input, myMath);
        assertArrayEquals(new int[]{-42}, result);
    }
}
