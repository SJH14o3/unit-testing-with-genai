// Content for MyMathTestGemma4_P3.java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MyMathTestGemma4_P3 {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    // --- Tests for checkSign(int number) ---

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    @DisplayName("Should return 'positive' for positive numbers")
    void checkSign_Positive(int number) {
        assertEquals("positive", myMath.checkSign(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should return 'negative' for negative numbers")
    void checkSign_Negative(int number) {
        assertEquals("negative", myMath.checkSign(number));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when input is zero (Boundary Test)")
    void checkSign_Zero() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", thrown.getMessage());
    }

    // --- Tests for reverseNumber(int number) ---

    @Test
    @DisplayName("Should correctly reverse a positive number")
    void reverseNumber_Positive() {
        assertEquals(-123, myMath.reverseNumber(123));
    }

    @Test
    @DisplayName("Should correctly reverse a negative number")
    void reverseNumber_Negative() {
        assertEquals(99, myMath.reverseNumber(-99));
    }

    @Test
    @DisplayName("Should return zero when input is zero")
    void reverseNumber_Zero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
    
    @Test
    @DisplayName("Should handle Integer.MIN_VALUE correctly (Boundary Test)")
    void reverseNumber_MinValue() {
        // -Integer.MIN_VALUE overflows positive range, but the method returns -number, 
        // so we test the direct result of negation which is the expected behavior based on the code.
        // For Integer.MIN_VALUE (-2147483648), -number is still -2147483648 due to 2's complement behavior in Java int.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}