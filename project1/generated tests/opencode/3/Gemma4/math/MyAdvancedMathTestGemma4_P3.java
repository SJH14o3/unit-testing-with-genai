package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

// Testing MyAdvancedMath
public class MyAdvancedMathTestGemma4_P3 {
    private MyMath myMathInstance;

    @BeforeEach
    void setUp() {
        myMathInstance = new MyMath();
    }

    // --- Tests for add(int x, int y) ---

    @ParameterizedTest(name = "add({0}, {1}) should return {2}")
    @CsvSource({
            ("1", "2", "3"), // Small positive numbers
            ("10000", "20000", "30000"),
            ("Integer.MAX_VALUE - 10", "20", "Integer.MAX_VALUE - 8"),
            ("1", "Integer.MIN_VALUE + 1", "Integer.MIN_VALUE + 2") // Test wrap around boundaries
    })
    void add_PositiveInputs(int x, int y, int expected) {
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    void add_NegativeInputShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_OverflowShouldThrowException() {
        // Test case: x > MAX_VALUE - y
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Use MAX_VALUE / 2 + 1 and MAX_VALUE / 2 - 1 to exceed MAX_VALUE
            myAdvancedMath.add(Integer.MAX_VALUE - 1, 2);
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @ParameterizedTest(name = "powerOfTwo({0}) should return {1}")
    @CsvSource({
            ("0", "1"),      // Boundary case: 2^0
            ("1", "2"),      // Smallest power
            ("10", "1024"),  // Normal value
            ("30", 1073741824) // Boundary case: 2^30
    })
    void powerOfTwo_ValidPowers(int power, int expected) {
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_NegativePowerShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void powerOfTwo_PowerGreaterThan30ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    void reverseArrayNumbers_NormalCase() {
        int[] array = {1, 2, 3};
        int[] expected = {-1, -2, -3};
        // Use MyMath instance that was set up in @BeforeEach
        MyMath advancedMath = new MyAdvancedMath(); 
        
        int[] actual = advancedMath.reverseArrayNumbers(array, myMathInstance);
        assertArrayEquals(expected, actual);
    }

    @Test
    void reverseArrayNumbers_MixedSignCase() {
        int[] array = {10, -20, 30};
        int[] expected = {-10, 20, -30};
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        
        int[] actual = advancedMath.reverseArrayNumbers(array, myMathInstance);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    void reverseArrayNumbers_EmptyArrayCase() {
        int[] array = {};
        int[] expected = {};
        MyAdvancedMath advancedMath = new MyAdvancedMath();

        int[] actual = advancedMath.reverseArrayNumbers(array, myMathInstance);
        assertArrayEquals(expected, actual);
    }
}