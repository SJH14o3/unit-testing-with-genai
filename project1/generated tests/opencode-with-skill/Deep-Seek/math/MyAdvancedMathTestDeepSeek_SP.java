package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MyAdvancedMath Tests (DeepSeek_SP)")
class MyAdvancedMathTestDeepSeek_SP {

    private final MyAdvancedMath math = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @ParameterizedTest(name = "add({0}, {1}) should return {2}")
    @CsvSource({
            "1, 2, 3",
            "0, 5, 5",
            "10, 20, 30",
            "0, 0, 0",
            "100, 200, 300"
    })
    @DisplayName("add returns correct sum for positive inputs")
    void add_shouldReturnCorrectSum_whenBothInputsArePositive(int x, int y, int expected) {
        assertEquals(expected, math.add(x, y));
    }

    @Test
    @DisplayName("add throws exception when first argument is negative")
    void add_shouldThrowException_whenFirstArgumentIsNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> math.add(-1, 5));
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    @DisplayName("add throws exception when second argument is negative")
    void add_shouldThrowException_whenSecondArgumentIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> math.add(5, -1));
    }

    @Test
    @DisplayName("add throws exception when both arguments are negative")
    void add_shouldThrowException_whenBothArgumentsAreNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> math.add(-1, -1));
    }

    @Test
    @DisplayName("add throws exception on integer overflow")
    void add_shouldThrowException_whenResultOverflows() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> math.add(Integer.MAX_VALUE, 1));
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    @DisplayName("add throws exception when result would overflow near max boundary")
    void add_shouldThrowException_whenResultExceedsMaxInt() {
        assertThrows(IllegalArgumentException.class,
                () -> math.add(Integer.MAX_VALUE - 5, 10));
    }

    @ParameterizedTest(name = "powerOfTwo({0}) should return {1}")
    @CsvSource({
            "0, 1",
            "1, 2",
            "2, 4",
            "3, 8",
            "5, 32",
            "10, 1024",
            "20, 1048576",
            "30, 1073741824"
    })
    @DisplayName("powerOfTwo returns correct result for valid powers")
    void powerOfTwo_shouldReturnCorrectResult_whenPowerIsValid(int power, int expected) {
        assertEquals(expected, math.powerOfTwo(power));
    }

    @ParameterizedTest(name = "powerOfTwo({0}) should throw exception")
    @ValueSource(ints = {-1, -5, -100})
    @DisplayName("powerOfTwo throws exception when power is negative")
    void powerOfTwo_shouldThrowException_whenPowerIsNegative(int power) {
        assertThrows(IllegalArgumentException.class,
                () -> math.powerOfTwo(power));
    }

    @Test
    @DisplayName("powerOfTwo throws exception when power is 31")
    void powerOfTwo_shouldThrowException_whenPowerIs31() {
        assertThrows(IllegalArgumentException.class,
                () -> math.powerOfTwo(31));
    }

    @Test
    @DisplayName("powerOfTwo throws exception when power is 100")
    void powerOfTwo_shouldThrowException_whenPowerIsFarOutOfRange() {
        assertThrows(IllegalArgumentException.class,
                () -> math.powerOfTwo(100));
    }

    @Test
    @DisplayName("reverseArrayNumbers reverses signs when using real MyMath")
    void reverseArrayNumbers_shouldReverseSignsOfAllElements() {
        int[] input = {10, -20, 30, -40, 0};
        int[] expected = {-10, 20, -30, 40, 0};
        assertArrayEquals(expected, math.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("reverseArrayNumbers returns empty array when input is empty")
    void reverseArrayNumbers_shouldReturnEmptyArray_whenInputIsEmpty() {
        int[] input = {};
        assertArrayEquals(new int[]{}, math.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("reverseArrayNumbers handles single element array")
    void reverseArrayNumbers_shouldHandleSingleElement() {
        int[] input = {7};
        assertArrayEquals(new int[]{-7}, math.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("reverseArrayNumbers modifies the original array")
    void reverseArrayNumbers_shouldModifyOriginalArray() {
        int[] input = {1, 2, 3};
        math.reverseArrayNumbers(input, myMath);
        assertArrayEquals(new int[]{-1, -2, -3}, input);
    }

    @Test
    @DisplayName("reverseArrayNumbers handles array with all zeros")
    void reverseArrayNumbers_shouldHandleAllZeros() {
        int[] input = {0, 0, 0};
        assertArrayEquals(new int[]{0, 0, 0}, math.reverseArrayNumbers(input, myMath));
    }
}
