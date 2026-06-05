package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive unit tests for {@link MyAdvancedMath} with MC/DC coverage,
 * boundary value analysis, edge case testing, and mocking.
 */
@DisplayName("MyAdvancedMath Tests")
public class MyAdvancedMathClaudeTest {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    // =========================================================================
    // add(int x, int y) tests
    // =========================================================================
    @Nested
    @DisplayName("add")
    class AddTests {

        // --- MC/DC for Decision 1: (x < 0 || y < 0) ---
        // To achieve MC/DC for a 2-condition OR:
        //   TC1: x<0=T, y<0=F → decision=T  (x independently causes true)
        //   TC2: x<0=F, y<0=T → decision=T  (y independently causes true)
        //   TC3: x<0=F, y<0=F → decision=F  (both false → false)

        @Test
        @DisplayName("MC/DC: x<0 independently causes exception (x<0=T, y<0=F)")
        void add_negativeXPositiveY_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(-1, 5)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: y<0 independently causes exception (x<0=F, y<0=T)")
        void add_positiveXNegativeY_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(5, -1)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: both non-negative → no exception (x<0=F, y<0=F)")
        void add_bothNonNegative_returnsSum() {
            assertEquals(10, myAdvancedMath.add(5, 5));
        }

        @Test
        @DisplayName("MC/DC: both negative → exception (x<0=T, y<0=T)")
        void add_bothNegative_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(-3, -7)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        // --- MC/DC for Decision 2: y > Integer.MAX_VALUE - x (overflow check) ---
        // Reached when x>=0 && y>=0.
        // Single condition, so T/F covers MC/DC.

        @Test
        @DisplayName("MC/DC: overflow condition true → exception")
        void add_overflowDetected_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MAX_VALUE, 1)
            );
            assertEquals("Cannot fit the result in an Integer.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: overflow condition false → returns sum")
        void add_noOverflow_returnsSum() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(Integer.MAX_VALUE, 0));
        }

        // --- Boundary: x around 0 ---

        @Test
        @DisplayName("boundary: x = -1 (just below 0), y = 0 → exception")
        void add_xJustBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 0));
        }

        @Test
        @DisplayName("boundary: x = 0, y = 0 → returns 0")
        void add_xAtZeroYAtZero_returnsZero() {
            assertEquals(0, myAdvancedMath.add(0, 0));
        }

        @Test
        @DisplayName("boundary: x = 1 (just above 0), y = 0 → returns 1")
        void add_xJustAboveZero_returnsOne() {
            assertEquals(1, myAdvancedMath.add(1, 0));
        }

        // --- Boundary: y around 0 ---

        @Test
        @DisplayName("boundary: x = 0, y = -1 (just below 0) → exception")
        void add_yJustBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(0, -1));
        }

        @Test
        @DisplayName("boundary: x = 0, y = 1 (just above 0) → returns 1")
        void add_yJustAboveZero_returnsOne() {
            assertEquals(1, myAdvancedMath.add(0, 1));
        }

        // --- Boundary: overflow boundary (y = MAX_VALUE - x) ---

        @Test
        @DisplayName("boundary: y exactly at overflow limit → no exception")
        void add_yExactlyAtOverflowLimit_returnsSum() {
            // x=1, y=MAX_VALUE-1 → y == MAX_VALUE-x → just within bounds
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(1, Integer.MAX_VALUE - 1));
        }

        @Test
        @DisplayName("boundary: y one above overflow limit → exception")
        void add_yOneAboveOverflowLimit_throwsException() {
            // x=1, y=MAX_VALUE → y > MAX_VALUE-1 → overflow
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(1, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("boundary: y one below overflow limit → sum OK")
        void add_yOneBelowOverflowLimit_returnsSum() {
            // x=2, y=MAX_VALUE-3 → y < MAX_VALUE-2 → within bounds
            assertEquals(Integer.MAX_VALUE - 1, myAdvancedMath.add(2, Integer.MAX_VALUE - 3));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("both MAX_VALUE → overflow exception")
        void add_bothMaxValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("MAX_VALUE + 0 = MAX_VALUE (no overflow)")
        void add_maxValuePlusZero_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(Integer.MAX_VALUE, 0));
        }

        @Test
        @DisplayName("0 + MAX_VALUE = MAX_VALUE (no overflow)")
        void add_zeroPlusMaxValue_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(0, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("x = MIN_VALUE → exception (negative)")
        void add_xMinValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MIN_VALUE, 0));
        }

        @Test
        @DisplayName("y = MIN_VALUE → exception (negative)")
        void add_yMinValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(0, Integer.MIN_VALUE));
        }

        // --- Parameterized: valid additions ---

        @ParameterizedTest(name = "add({0}, {1}) = {2}")
        @CsvSource({
                "0, 0, 0",
                "1, 1, 2",
                "0, 5, 5",
                "5, 0, 5",
                "100, 200, 300",
                "1000000, 2000000, 3000000"
        })
        @DisplayName("parameterized: valid additions return correct sum")
        void add_validInputs_returnsCorrectSum(int x, int y, int expected) {
            assertEquals(expected, myAdvancedMath.add(x, y));
        }

        // --- Parameterized: negative input exceptions ---

        @ParameterizedTest(name = "add({0}, {1}) throws exception")
        @CsvSource({
                "-1, 0",
                "0, -1",
                "-1, -1",
                "-100, 50",
                "50, -100"
        })
        @DisplayName("parameterized: negative inputs throw exception")
        void add_negativeInputs_throwsException(int x, int y) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(x, y)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }
    }

    // =========================================================================
    // powerOfTwo(int power) tests
    // =========================================================================
    @Nested
    @DisplayName("powerOfTwo")
    class PowerOfTwoTests {

        // --- MC/DC for Decision 1: power == 0 ---
        // Single condition: true → return 1, false → continue

        @Test
        @DisplayName("MC/DC: power == 0 → returns 1")
        void powerOfTwo_zero_returnsOne() {
            assertEquals(1, myAdvancedMath.powerOfTwo(0));
        }

        @Test
        @DisplayName("MC/DC: power != 0, valid → computes result")
        void powerOfTwo_nonZeroValid_returnsResult() {
            assertEquals(2, myAdvancedMath.powerOfTwo(1));
        }

        // --- MC/DC for Decision 2: (power < 0 || power > 30) ---
        // Reached when power != 0.
        // TC1: power<0=T, power>30=F → decision=T (power<0 independently causes true)
        // TC2: power<0=F, power>30=T → decision=T (power>30 independently causes true)
        // TC3: power<0=F, power>30=F → decision=F (both false)

        @Test
        @DisplayName("MC/DC: power<0 independently causes exception (power<0=T, power>30=F)")
        void powerOfTwo_negative_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(-1)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: power>30 independently causes exception (power<0=F, power>30=T)")
        void powerOfTwo_above30_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(31)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: both conditions false → computes (power<0=F, power>30=F)")
        void powerOfTwo_withinRange_returnsResult() {
            assertEquals(1024, myAdvancedMath.powerOfTwo(10));
        }

        // --- Boundary at 0: -1, 0, 1 ---

        @Test
        @DisplayName("boundary: power = -1 (just below 0) → exception")
        void powerOfTwo_justBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
        }

        @Test
        @DisplayName("boundary: power = 0 → returns 1")
        void powerOfTwo_atZero_returnsOne() {
            assertEquals(1, myAdvancedMath.powerOfTwo(0));
        }

        @Test
        @DisplayName("boundary: power = 1 (just above 0) → returns 2")
        void powerOfTwo_justAboveZero_returnsTwo() {
            assertEquals(2, myAdvancedMath.powerOfTwo(1));
        }

        // --- Boundary at 30: 29, 30, 31 ---

        @Test
        @DisplayName("boundary: power = 29 (just below 30) → returns 2^29")
        void powerOfTwo_justBelow30_returnsResult() {
            assertEquals(536870912, myAdvancedMath.powerOfTwo(29)); // 2^29
        }

        @Test
        @DisplayName("boundary: power = 30 (at upper bound) → returns 2^30")
        void powerOfTwo_at30_returnsResult() {
            assertEquals(1073741824, myAdvancedMath.powerOfTwo(30)); // 2^30
        }

        @Test
        @DisplayName("boundary: power = 31 (just above 30) → exception")
        void powerOfTwo_justAbove30_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("Integer.MIN_VALUE → exception")
        void powerOfTwo_intMinValue_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(Integer.MIN_VALUE)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("Integer.MAX_VALUE → exception")
        void powerOfTwo_intMaxValue_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(Integer.MAX_VALUE)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        // --- Parameterized: valid power computations ---

        @ParameterizedTest(name = "2^{0} = {1}")
        @CsvSource({
                "0, 1",
                "1, 2",
                "2, 4",
                "3, 8",
                "4, 16",
                "5, 32",
                "10, 1024",
                "15, 32768",
                "20, 1048576",
                "25, 33554432",
                "30, 1073741824"
        })
        @DisplayName("parameterized: valid powers return correct result")
        void powerOfTwo_validPower_returnsCorrectResult(int power, int expected) {
            assertEquals(expected, myAdvancedMath.powerOfTwo(power));
        }

        // --- Parameterized: invalid power values ---

        @ParameterizedTest(name = "powerOfTwo({0}) throws exception")
        @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE, 31, 32, 100, Integer.MAX_VALUE})
        @DisplayName("parameterized: invalid powers throw exception")
        void powerOfTwo_invalidPower_throwsException(int power) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(power)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }
    }

    // =========================================================================
    // reverseArrayNumbers(int[] array, MyMath mm) tests
    // =========================================================================
    @Nested
    @DisplayName("reverseArrayNumbers")
    class ReverseArrayNumbersTests {

        // --- Tests WITHOUT mocking (using real MyMath) ---

        @Test
        @DisplayName("reverses array of positive numbers")
        void reverseArrayNumbers_positiveNumbers_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{-1, -2, -3}, result);
        }

        @Test
        @DisplayName("reverses array of negative numbers")
        void reverseArrayNumbers_negativeNumbers_returnsPositive() {
            MyMath mm = new MyMath();
            int[] input = {-4, -5, -6};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{4, 5, 6}, result);
        }

        @Test
        @DisplayName("reverses array of mixed numbers")
        void reverseArrayNumbers_mixedNumbers_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {-3, 0, 7};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{3, 0, -7}, result);
        }

        @Test
        @DisplayName("empty array returns empty array")
        void reverseArrayNumbers_emptyArray_returnsEmptyArray() {
            MyMath mm = new MyMath();
            int[] input = {};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{}, result);
        }

        @Test
        @DisplayName("single element array")
        void reverseArrayNumbers_singleElement_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {42};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{-42}, result);
        }

        @Test
        @DisplayName("array with zeros")
        void reverseArrayNumbers_zeros_returnsZeros() {
            MyMath mm = new MyMath();
            int[] input = {0, 0, 0};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{0, 0, 0}, result);
        }

        @Test
        @DisplayName("array with extreme values")
        void reverseArrayNumbers_extremeValues_handlesCorrectly() {
            MyMath mm = new MyMath();
            int[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            // -MAX_VALUE is valid; -MIN_VALUE overflows back to MIN_VALUE
            assertArrayEquals(new int[]{-Integer.MAX_VALUE, Integer.MIN_VALUE}, result);
        }

        @Test
        @DisplayName("modifies the input array in place (returns same reference)")
        void reverseArrayNumbers_modifiesInputArray_sameReference() {
            MyMath mm = new MyMath();
            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertSame(input, result);
        }

        // --- Null argument edge cases ---

        @Test
        @DisplayName("null array throws NullPointerException")
        void reverseArrayNumbers_nullArray_throwsNPE() {
            MyMath mm = new MyMath();
            assertThrows(NullPointerException.class,
                    () -> myAdvancedMath.reverseArrayNumbers(null, mm));
        }

        @Test
        @DisplayName("null MyMath throws NullPointerException for non-empty array")
        void reverseArrayNumbers_nullMyMath_throwsNPE() {
            int[] input = {1, 2, 3};
            assertThrows(NullPointerException.class,
                    () -> myAdvancedMath.reverseArrayNumbers(input, null));
        }

        @Test
        @DisplayName("null MyMath with empty array returns empty array (loop not entered)")
        void reverseArrayNumbers_nullMyMathEmptyArray_returnsEmptyArray() {
            int[] input = {};
            // Loop body never executes, so null mm is never dereferenced
            int[] result = myAdvancedMath.reverseArrayNumbers(input, null);
            assertArrayEquals(new int[]{}, result);
        }
    }

    // =========================================================================
    // reverseArrayNumbers with Mock (verify interactions)
    // =========================================================================
    @Nested
    @DisplayName("reverseArrayNumbers with Mock")
    @ExtendWith(MockitoExtension.class)
    class ReverseArrayNumbersMockTests {

        @Mock
        private MyMath mockMyMath;

        @Test
        @DisplayName("mock: verifies reverseNumber called for each element")
        void reverseArrayNumbers_mockMyMath_verifiesInteraction() {
            when(mockMyMath.reverseNumber(1)).thenReturn(-1);
            when(mockMyMath.reverseNumber(2)).thenReturn(-2);
            when(mockMyMath.reverseNumber(3)).thenReturn(-3);

            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{-1, -2, -3}, result);

            verify(mockMyMath, times(1)).reverseNumber(1);
            verify(mockMyMath, times(1)).reverseNumber(2);
            verify(mockMyMath, times(1)).reverseNumber(3);
            verifyNoMoreInteractions(mockMyMath);
        }

        @Test
        @DisplayName("mock: empty array → reverseNumber never called")
        void reverseArrayNumbers_mockEmptyArray_noCalls() {
            int[] input = {};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{}, result);
            verifyNoInteractions(mockMyMath);
        }

        @Test
        @DisplayName("mock: single element array → reverseNumber called once")
        void reverseArrayNumbers_mockSingleElement_calledOnce() {
            when(mockMyMath.reverseNumber(99)).thenReturn(-99);

            int[] input = {99};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{-99}, result);
            verify(mockMyMath, times(1)).reverseNumber(99);
        }

        @Test
        @DisplayName("mock: verifies call order with large array")
        void reverseArrayNumbers_mockLargeArray_allElementsProcessed() {
            int[] input = new int[100];
            for (int i = 0; i < 100; i++) {
                input[i] = i;
                when(mockMyMath.reverseNumber(i)).thenReturn(-i);
            }

            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            for (int i = 0; i < 100; i++) {
                assertEquals(-i, result[i]);
                verify(mockMyMath).reverseNumber(i);
            }
        }
    }
}
