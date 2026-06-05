package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Comprehensive unit tests for {@link MyMath} with MC/DC coverage,
 * boundary value analysis, and edge case testing.
 */
@DisplayName("MyMath Tests")
public class MyMathClaudeTest {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    // =========================================================================
    // checkSign(int number) tests
    // =========================================================================
    @Nested
    @DisplayName("checkSign")
    class CheckSignTests {

        // --- MC/DC: Decision 1: number > 0 ---
        // Condition: number > 0 is a single condition.
        // true  → "positive"
        // false → falls through to next decision

        // --- MC/DC: Decision 2: number < 0 (reached when number <= 0) ---
        // Condition: number < 0 is a single condition.
        // true  → "negative"
        // false → exception (number == 0)

        @Test
        @DisplayName("returns 'positive' for positive number (MC/DC: number>0 = true)")
        void checkSign_positiveNumber_returnsPositive() {
            assertEquals("positive", myMath.checkSign(5));
        }

        @Test
        @DisplayName("returns 'negative' for negative number (MC/DC: number>0 = false, number<0 = true)")
        void checkSign_negativeNumber_returnsNegative() {
            assertEquals("negative", myMath.checkSign(-5));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for zero (MC/DC: number>0 = false, number<0 = false)")
        void checkSign_zero_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myMath.checkSign(0)
            );
            assertEquals("Number had no sign", ex.getMessage());
        }

        // --- Boundary value analysis around 0 ---

        @Test
        @DisplayName("boundary: number = 1 (just above 0)")
        void checkSign_justAboveZero_returnsPositive() {
            assertEquals("positive", myMath.checkSign(1));
        }

        @Test
        @DisplayName("boundary: number = 0 (at boundary)")
        void checkSign_atZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
        }

        @Test
        @DisplayName("boundary: number = -1 (just below 0)")
        void checkSign_justBelowZero_returnsNegative() {
            assertEquals("negative", myMath.checkSign(-1));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("Integer.MAX_VALUE returns 'positive'")
        void checkSign_intMaxValue_returnsPositive() {
            assertEquals("positive", myMath.checkSign(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer.MIN_VALUE returns 'negative'")
        void checkSign_intMinValue_returnsNegative() {
            assertEquals("negative", myMath.checkSign(Integer.MIN_VALUE));
        }

        // --- Parameterized positive values ---
        @ParameterizedTest(name = "checkSign({0}) = positive")
        @ValueSource(ints = {1, 2, 10, 100, 1000, Integer.MAX_VALUE})
        @DisplayName("parameterized: various positive numbers")
        void checkSign_variousPositiveNumbers_returnsPositive(int number) {
            assertEquals("positive", myMath.checkSign(number));
        }

        // --- Parameterized negative values ---
        @ParameterizedTest(name = "checkSign({0}) = negative")
        @ValueSource(ints = {-1, -2, -10, -100, -1000, Integer.MIN_VALUE})
        @DisplayName("parameterized: various negative numbers")
        void checkSign_variousNegativeNumbers_returnsNegative(int number) {
            assertEquals("negative", myMath.checkSign(number));
        }
    }

    // =========================================================================
    // reverseNumber(int number) tests
    // =========================================================================
    @Nested
    @DisplayName("reverseNumber")
    class ReverseNumberTests {

        @Test
        @DisplayName("reverses a positive number to negative")
        void reverseNumber_positiveNumber_returnsNegative() {
            assertEquals(-5, myMath.reverseNumber(5));
        }

        @Test
        @DisplayName("reverses a negative number to positive")
        void reverseNumber_negativeNumber_returnsPositive() {
            assertEquals(5, myMath.reverseNumber(-5));
        }

        @Test
        @DisplayName("reverses zero to zero")
        void reverseNumber_zero_returnsZero() {
            assertEquals(0, myMath.reverseNumber(0));
        }

        // --- Boundary values ---

        @Test
        @DisplayName("boundary: number = 1")
        void reverseNumber_one_returnsMinusOne() {
            assertEquals(-1, myMath.reverseNumber(1));
        }

        @Test
        @DisplayName("boundary: number = -1")
        void reverseNumber_minusOne_returnsOne() {
            assertEquals(1, myMath.reverseNumber(-1));
        }

        // --- Extreme / overflow edge cases ---

        @Test
        @DisplayName("Integer.MAX_VALUE reversal")
        void reverseNumber_intMaxValue_returnsNegative() {
            assertEquals(-Integer.MAX_VALUE, myMath.reverseNumber(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer.MIN_VALUE reversal causes overflow (stays MIN_VALUE)")
        void reverseNumber_intMinValue_overflows() {
            // -Integer.MIN_VALUE overflows in two's complement,
            // resulting in Integer.MIN_VALUE itself.
            // This is a known integer overflow edge case.
            assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
        }

        @Test
        @DisplayName("-(MAX_VALUE) reversal returns MAX_VALUE")
        void reverseNumber_negativeMaxValue_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myMath.reverseNumber(-Integer.MAX_VALUE));
        }

        // --- Parameterized: verify reverse is an involution (f(f(x)) = x) ---
        @ParameterizedTest(name = "reverseNumber(reverseNumber({0})) = {0}")
        @ValueSource(ints = {0, 1, -1, 42, -42, 1000, -1000, Integer.MAX_VALUE})
        @DisplayName("parameterized: double reversal is identity")
        void reverseNumber_doubleReversal_returnsOriginal(int number) {
            assertEquals(number, myMath.reverseNumber(myMath.reverseNumber(number)));
        }
    }
}
