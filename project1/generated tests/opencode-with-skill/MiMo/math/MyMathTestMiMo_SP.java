package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MyMath Tests")
class MyMathTestMiMo_SP {

    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("checkSign returns 'positive' for positive numbers")
    void checkSign_shouldReturnPositive_whenNumberIsPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @ParameterizedTest(name = "checkSign({0}) should return 'negative'")
    @ValueSource(ints = {-1, -100, -999})
    @DisplayName("checkSign returns 'negative' for negative numbers")
    void checkSign_shouldReturnNegative_whenNumberIsNegative(int number) {
        assertEquals("negative", myMath.checkSign(number));
    }

    @Test
    @DisplayName("checkSign throws exception when input is zero")
    void checkSign_shouldThrowException_whenNumberIsZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    @DisplayName("reverseNumber negates positive number")
    void reverseNumber_shouldNegatePositiveNumber() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    @DisplayName("reverseNumber negates negative number")
    void reverseNumber_shouldNegateNegativeNumber() {
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    @DisplayName("reverseNumber returns zero when input is zero")
    void reverseNumber_shouldReturnZero_whenInputIsZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
