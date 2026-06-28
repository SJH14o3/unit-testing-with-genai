package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MyMathTestGPTOSS_P2 {

    private final MyMath mm = new MyMath();

    @Test
    void checkSign_positive() {
        assertEquals("positive", mm.checkSign(1));
    }

    @Test
    void checkSign_negative() {
        assertEquals("negative", mm.checkSign(-1));
    }

    @Test
    void checkSign_zeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    void reverseNumber_positive() {
        assertEquals(-5, mm.reverseNumber(5));
    }

    @Test
    void reverseNumber_negative() {
        assertEquals(5, mm.reverseNumber(-5));
    }
}
