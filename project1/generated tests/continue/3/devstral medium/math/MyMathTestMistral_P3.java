package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MyMathTestMistral_P3 {

    @Test
    void checkSign_WhenZero_ThrowsIllegalArgumentException() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, Integer.MAX_VALUE})
    void checkSign_WhenPositive_ReturnsPositive(int number) {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE})
    void checkSign_WhenNegative_ReturnsNegative(int number) {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, -1, 100, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void reverseNumber_ReversesSign(int number) {
        MyMath myMath = new MyMath();
        assertEquals(-number, myMath.reverseNumber(number));
    }
}
