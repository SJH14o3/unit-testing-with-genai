package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestGemma3_1 {

    @Test
    void checkSign_positiveNumber() {
        MyMath math = new MyMath();
        assertEquals("positive", math.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber() {
        MyMath math = new MyMath();
        assertEquals("negative", math.checkSign(-3));
    }

    @Test
    void checkSign_zeroNumber() {
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void reverseNumber_positiveNumber() {
        MyMath math = new MyMath();
        assertEquals(-7, math.reverseNumber(7));
    }

    @Test
    void reverseNumber_negativeNumber() {
        MyMath math = new MyMath();
        assertEquals(5, math.reverseNumber(-5));
    }

    @Test
    void reverseNumber_zero() {
        MyMath math = new MyMath();
        assertEquals(0, math.reverseNumber(0));
    }
}
