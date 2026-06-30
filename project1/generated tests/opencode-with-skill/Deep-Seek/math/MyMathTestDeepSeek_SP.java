package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MyMath Tests (DeepSeek_SP)")
class MyMathTestDeepSeek_SP {

    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("checkSign returns 'positive' for positive numbers")
    void checkSign_shouldReturnPositive_whenNumberIsPositive() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @ParameterizedTest(name = "checkSign({0}) should return 'positive'")
    @ValueSource(ints = {2, 100, Integer.MAX_VALUE})
    @DisplayName("checkSign returns 'positive' for various positive numbers")
    void checkSign_shouldReturnPositive_forVariousPositiveNumbers(int number) {
        assertEquals("positive", myMath.checkSign(number));
    }

    @ParameterizedTest(name = "checkSign({0}) should return 'negative'")
    @ValueSource(ints = {-1, -50, Integer.MIN_VALUE})
    @DisplayName("checkSign returns 'negative' for various negative numbers")
    void checkSign_shouldReturnNegative_forVariousNegativeNumbers(int number) {
        assertEquals("negative", myMath.checkSign(number));
    }

    @Test
    @DisplayName("checkSign throws IllegalArgumentException when input is zero")
    void checkSign_shouldThrowException_whenNumberIsZero() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> myMath.checkSign(0));
        assertEquals("Number had no sign", exception.getMessage());
    }

    @ParameterizedTest(name = "reverseNumber({0}) should return {1}")
    @org.junit.jupiter.params.provider.CsvSource({
            "5, -5",
            "-3, 3",
            "0, 0",
            "1, -1",
            "-1, 1"
    })
    @DisplayName("reverseNumber reverses sign correctly")
    void reverseNumber_shouldReverseSign_correctly(int input, int expected) {
        assertEquals(expected, myMath.reverseNumber(input));
    }

    @Test
    @DisplayName("reverseNumber handles Integer.MAX_VALUE")
    void reverseNumber_shouldHandleMaxValue() {
        assertEquals(Integer.MIN_VALUE + 1, myMath.reverseNumber(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("reverseNumber handles Integer.MIN_VALUE")
    void reverseNumber_shouldHandleMinValue() {
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}
