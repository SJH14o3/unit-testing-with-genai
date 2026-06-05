// MyMathTestGPT_OSS_P2.java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MyMathTestGPT_OSS_P2 {

    private final MyMath math = new MyMath();

    @Test
    void checkSign_negativeNumber() {
        assertEquals("negative", math.checkSign(-1));
    }

    @Test
    void checkSign_zeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void checkSign_positiveNumber() {
        assertEquals("positive", math.checkSign(1));
    }

    @Test
    void reverseNumber_negative() {
        assertEquals(1, math.reverseNumber(-1));
    }

    @Test
    void reverseNumber_zero() {
        assertEquals(0, math.reverseNumber(0));
    }

    @Test
    void reverseNumber_positive() {
        assertEquals(-1, math.reverseNumber(1));
    }
}