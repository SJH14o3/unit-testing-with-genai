package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MyMathTestGPTOSS_SP {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    @Test
    @DisplayName("checkSign returns positive for positive numbers")
    void checkSignPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    @DisplayName("checkSign returns negative for negative numbers")
    void checkSignNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    @DisplayName("checkSign throws IllegalArgumentException for zero")
    void checkSignZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    @DisplayName("reverseNumber returns negative of input")
    void reverseNumberPositive() {
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    @DisplayName("reverseNumber returns positive of input")
    void reverseNumberNegative() {
        assertEquals(15, myMath.reverseNumber(-15));
    }
}
