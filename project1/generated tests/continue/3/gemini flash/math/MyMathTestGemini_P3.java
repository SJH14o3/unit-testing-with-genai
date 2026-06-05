package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MyMathTestGemini_P3 {

    private final MyMath mm = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, positive",
        "2, positive",
        "100, positive",
        "-1, negative",
        "-2, negative",
        "-100, negative"
    })
    void testCheckSign_NormalValues(int number, String expected) {
        Assertions.assertEquals(expected, mm.checkSign(number));
    }

    @Test
    void testCheckSign_Zero_Boundary() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    void testCheckSign_AboveZero_Boundary() {
        Assertions.assertEquals("positive", mm.checkSign(1));
    }

    @Test
    void testCheckSign_BelowZero_Boundary() {
        Assertions.assertEquals("negative", mm.checkSign(-1));
    }

    @ParameterizedTest
    @CsvSource({
        "1, -1",
        "-1, 1",
        "0, 0",
        "2147483647, -2147483647",
        "-2147483647, 2147483647"
    })
    void testReverseNumber_NormalAndEdges(int number, int expected) {
        Assertions.assertEquals(expected, mm.reverseNumber(number));
    }

    @Test
    void testReverseNumber_MinInt_Edge() {
        // Integer.MIN_VALUE is -2147483648. -(-2147483648) is still -2147483648 due to overflow.
        Assertions.assertEquals(Integer.MIN_VALUE, mm.reverseNumber(Integer.MIN_VALUE));
    }
}
