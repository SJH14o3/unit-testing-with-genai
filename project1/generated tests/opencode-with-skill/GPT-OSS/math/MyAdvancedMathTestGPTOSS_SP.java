package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestGPTOSS_SP {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    @Test
    @DisplayName("add positive numbers returns sum")
    void addPositive() {
        assertEquals(15, myAdvancedMath.add(7, 8));
    }

    @ParameterizedTest(name = "add({0}, {1}) -> {2}")
    @CsvSource("5,3,8", "10,20,30", "0,0,0")
    void addWithCsv(int x, int y, int expected) {
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    @DisplayName("add throws for negative input")
    void addNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(4, -2));
    }

    @Test
    @DisplayName("add throws for integer overflow")
    void addOverflowThrows() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("powerOfTwo 0 returns 1")
    void powerZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    @DisplayName("powerOfTwo 5 returns 32")
    void powerFive() {
        assertEquals(32, myAdvancedMath.powerOfTwo(5));
    }

    @ParameterizedTest(name = "powerOfTwo({0}) -> {1}")
    @CsvSource("1,2", "2,4", "3,8", "30,1073741824")
    void powerCsv(int power, int expected) {
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    @DisplayName("powerOfTwo throws for negative power")
    void powerNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
    }

    @Test
    @DisplayName("powerOfTwo throws for power > 30")
    void powerTooLargeThrows() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }

    @Test
    @DisplayName("reverseArrayNumbers reverses signs of each element")
    void reverseArrayNumbers() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, new MyMath()));
    }
}
