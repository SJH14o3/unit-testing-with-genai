package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.stream.IntStream;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(value = "dmst.seip.angor.junitdemo.math.MyMathTestGemma3_P3")
public class MyMathTestGemma3_P3 {

    private MyMath myMath;

    @Test
    void checkSign_positiveNumber() {
        myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber() {
        myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    void checkSign_zeroNumber_throwsException() {
        myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    void reverseNumber_positiveNumber() {
        myMath = new MyMath();
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    void reverseNumber_negativeNumber() {
        myMath = new MyMath();
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    void reverseNumber_zeroNumber() {
        myMath = new MyMath();
        assertEquals(0, myMath.reverseNumber(0));
    }

    @Test
    public void checkSign_boundaryPositive() {
        myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void checkSign_boundaryNegative() {
        myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

}