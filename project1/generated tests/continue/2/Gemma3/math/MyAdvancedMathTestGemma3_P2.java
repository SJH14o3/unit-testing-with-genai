import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttribute;

import dmst.seip.angor.junitdemo.math.MyAdvancedMath;
import dmst.seip.angor.junitdemo.math.MyMath;
import dmst.seip.angor.junitdemo.io.FileIO;

public class MyAdvancedMathTestGemma3_P2 {

    private MyAdvancedMath advancedMath = new MyAdvancedMath();

    @Test
    void add_positiveNumbers() {
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumber_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 2));
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_overflow_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    void powerOfTwo_zeroPower() {
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_onePower() {
        assertEquals(2, advancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_negativePower_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void powerOfTwo_maxPower() {
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_tooLargePower_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        MyMath mm = new MyMath();
        int[] array = {};
        int[] reversed = advancedMath.reverseArrayNumbers(array, mm);
        assertArrayEquals(new int[]{}, reversed);
    }

    @Test
    void reverseArrayNumbers_singleElementArray() {
        MyMath mm = new MyMath();
        int[] array = {5};
        int[] reversed = advancedMath.reverseArrayNumbers(array, mm);
        assertArrayEquals(new int[]{-5}, reversed);
    }

    @Test
    void reverseArrayNumbers_multipleElementsArray() {
        MyMath mm = new MyMath();
        int[] array = {1, 2, 3};
        int[] reversed = advancedMath.reverseArrayNumbers(array, mm);
        assertArrayEquals(new int[]{-1, -2, -3}, reversed);
    }
}
