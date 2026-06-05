package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MyMathTestGPT {

    private final MyMath math = new MyMath();

    @Test
    void checkSign_positiveNumber_returnsPositive() {
        assertEquals("positive", math.checkSign(7));
    }

    @Test
    void checkSign_negativeNumber_returnsNegative() {
        assertEquals("negative", math.checkSign(-12));
    }

    @Test
    void checkSign_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void reverseNumber_flipsSign() {
        assertEquals(-42, math.reverseNumber(42));
    }
}
