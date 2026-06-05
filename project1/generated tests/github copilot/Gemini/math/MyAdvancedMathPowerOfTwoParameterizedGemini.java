package dmst.seip.angor.junitdemo.math;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MyAdvancedMathPowerOfTwoParameterizedGemini {

    private int power;
    private int expectedResult;

    public MyAdvancedMathPowerOfTwoParameterizedGemini(int power, int expectedResult) {
        this.power = power;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 1},
                {1, 2},
                {2, 4},
                {3, 8},
                {4, 16},
                {10, 1024},
                {30, 1073741824}
        });
    }

    @Test
    public void testPowerOfTwo() {
        MyAdvancedMath math = new MyAdvancedMath();
        Assert.assertEquals(expectedResult, math.powerOfTwo(power));
    }
}
