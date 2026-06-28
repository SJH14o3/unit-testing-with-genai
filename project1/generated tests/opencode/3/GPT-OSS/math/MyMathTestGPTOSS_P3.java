package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestGPTOSS_P3 {

    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @ValueSource(ints = {1, 42, Integer.MAX_VALUE})
    void checkSign_withPositiveNumbers_returnsPositive(int number) {
        assertEquals("positive", myMath.checkSign(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -42, Integer.MIN_VALUE})
    void checkSign_withNegativeNumbers_returnsNegative(int number) {
        assertEquals("negative", myMath.checkSign(number));
    }

    @Test
    void checkSign_withZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, -1, 0, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void reverseNumber_returnsOppositeSign(int number) {
        assertEquals(-number, myMath.reverseNumber(number));
    }
}
