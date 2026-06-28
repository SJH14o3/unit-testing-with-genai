package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestGPTOSS_P3 {

    private final MyAdvancedMath math = new MyAdvancedMath();
    private final MyMath mm = new MyMath();

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE / 2})
    void add_withPositiveInputs_returnsSum(int x) {
        int y = x;
        assertEquals(x + y, math.add(x, y));
    }

    @Test
    void add_withNegativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
    }

    @Test
    void add_overflow_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.add(Integer.MAX_VALUE, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 30})
    void powerOfTwo_validValues_returnsCorrect(int power) {
        int expected = (int)Math.pow(2, power);
        assertEquals(expected, math.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_boundaryBelowZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_boundaryAbove30_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
    }

    @Test
    void powerOfTwo_exactBoundaryZero_returnsOne() {
        assertEquals(1, math.powerOfTwo(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE})
    void reverseArrayNumbers_withVariousArrays_reversesSigns(int input) {
        int[] arr = new int[]{input};
        int[] result = math.reverseArrayNumbers(arr, mm);
        assertEquals(-input, result[0]);
    }
}
