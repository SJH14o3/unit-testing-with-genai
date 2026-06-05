package dmst.seip.angor.junitdemo.math;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

public class MyAdvancedMathGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddNormal() {
        MyAdvancedMath math = new MyAdvancedMath();
        Assert.assertEquals(5, math.add(2, 3));
    }

    @Test
    public void testAddNegativeX() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Input numbers should be positive.");
        math.add(-1, 5);
    }

    @Test
    public void testAddNegativeY() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Input numbers should be positive.");
        math.add(5, -1);
    }

    @Test
    public void testAddOverflow() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot fit the result in an Integer.");
        math.add(Integer.MAX_VALUE, 1);
    }

    @Test
    public void testPowerOfTwoNegative() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("power should be 0<=power<=30");
        math.powerOfTwo(-1);
    }

    @Test
    public void testPowerOfTwoGreaterThan30() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("power should be 0<=power<=30");
        math.powerOfTwo(31);
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath math = new MyAdvancedMath();
        MyMath myMathMock = mock(MyMath.class);
        when(myMathMock.reverseNumber(1)).thenReturn(-1);
        when(myMathMock.reverseNumber(-2)).thenReturn(2);
        when(myMathMock.reverseNumber(3)).thenReturn(-3);

        int[] input = {1, -2, 3};
        int[] expected = {-1, 2, -3};
        int[] result = math.reverseArrayNumbers(input, myMathMock);

        Assert.assertArrayEquals(expected, result);
        verify(myMathMock, times(1)).reverseNumber(1);
        verify(myMathMock, times(1)).reverseNumber(-2);
        verify(myMathMock, times(1)).reverseNumber(3);
    }
}
