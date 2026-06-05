package dmst.seip.angor.junitdemo.math;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MyMathGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        Assert.assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        Assert.assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    public void testCheckSignZero() {
        MyMath myMath = new MyMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Number had no sign");
        myMath.checkSign(0);
    }

    @Test
    public void testReverseNumber() {
        MyMath myMath = new MyMath();
        Assert.assertEquals(-5, myMath.reverseNumber(5));
        Assert.assertEquals(5, myMath.reverseNumber(-5));
        Assert.assertEquals(0, myMath.reverseNumber(0));
    }
}
