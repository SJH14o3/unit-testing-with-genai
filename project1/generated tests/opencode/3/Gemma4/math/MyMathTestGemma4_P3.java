package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.Stream;

public class MyMathTestGemma4_P3 {

    private final MyMath myMath = new MyMath();

    // --- Tests for checkSign(int number) ---
    
    @ParameterizedTest(name = "checkSign({0}) should return {1}")
    @CsvSource({
            "1, 'positive'",
            ("Integer.MAX_VALUE", "positive"), // Boundary check
            ("Integer.MAX_VALUE - 1", "positive"),
            ("-1", "negative"),
            ("Integer.MIN_VALUE", "negative"), // Boundary check
            ("Integer.MIN_VALUE + 1", "negative")
    })
    void checkSign_PositiveOrNegative(int number, String expected) {
        assertEquals(expected, myMath.checkSign(number));
    }

    @ParameterizedTest(name = "checkSign({0}) should throw IllegalArgumentException")
    @CsvSource({"0"})
    void checkSign_ThrowsExceptionForZero(int zero) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(zero);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    // --- Tests for reverseNumber(int number) ---

    @ParameterizedTest(name = "reverseNumber({0}) should return {1}")
    @CsvSource({
            ("123", -123),
            ("-456", 456),
            ("0", 0)
    })
    void reverseNumber_BasicScenarios(int input, int expected) {
        assertEquals(expected, myMath.reverseNumber(input));
    }

    @Test
    void reverseNumber_IntegerRangeBoundaries() {
        // Testing max and min values for safety, although standard int arithmetic handles it.
        // The implementation simply negates the number.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}