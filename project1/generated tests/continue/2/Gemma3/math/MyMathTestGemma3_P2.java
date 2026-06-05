import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttribute;

import dmst.seip.angor.junitdemo.math.MyMath;
import dmst.seip.angor.junitdemo.io.FileIO;

public class MyMathTestGemma3_P2 {

    private MyMath myMath = new MyMath();

    @Test
    void checkSign_positiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_negativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_zeroNumber_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void reverseNumber_positiveNumber() {
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    void reverseNumber_negativeNumber() {
        assertEquals(1, myMath.reverseNumber(-1));
    }

    @Test
    void reverseNumber_zeroNumber() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
