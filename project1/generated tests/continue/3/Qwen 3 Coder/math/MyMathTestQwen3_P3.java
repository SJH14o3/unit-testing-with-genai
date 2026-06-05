package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestQwen3_P3 {

    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, positive",
        "100, positive",
        "1000, positive",
        "-1, negative",
        "-100, negative",
        "-1000, negative"
    })
    void checkSign_NormalValues(int number, String expected) {
        assertEquals(expected, myMath.checkSign(number));
    }

    @Test
    void checkSign_ZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000, -1, -100, -1000})
    void reverseNumber_NormalValues(int number) {
        assertEquals(-number, myMath.reverseNumber(number));
    }
}