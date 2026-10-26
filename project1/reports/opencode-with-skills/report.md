# Experiment with OpenCode in Unit Test Generation with Utilizing Skills
## Introduction to Skills
Agent Skills are a lightweight, open format for extending AI agent capabilities
with specialized knowledge and workflows. At its core,
a skill is a folder containing a SKILL.md file.
This file includes metadata ( name and description , at minimum) and
instructions that tell an agent how to perform a specific task.  


### How to Install Skills
OpenCode and many other agents support skills. Firstly, you need to find
the skills you are planning to use. There are many repositories on GitHub
that have various skills. However, I used this website to find skills easier:  
https://agentskillsforall.com/  
It's also free.  
After you have found a skill that you want to utilize on this website, you install it.
This website provides a command that you can run on a CLI to install the tool.
After running the command, you will be prompted to customize the installation. 
It will ask what code assistance tools you want this skill to be compatible with.
By default, OpenCode was selected. After that, it prompts you whether to install
the skill on a specific project or install it globally. If you want to install it for a
specific project, you must run the command on the project folder.  
You also can install the skills manually by just downloading the skill folder from 
the repository and copying it to the correct skills' folder. You do have to configure
a json file which won't be a hassle. I was unable to install a skill through command,
so I installed it manually.


### How to utilize a skill with OpenCode
If you have installed some skills, OpenCode will automatically determine if a useful skill
exists for your prompt! For instance, if I prompt an AI to make a unit test,
it will look through the available skills and check whether a skill is suitable for unit testing.
However, you can explicitly use a skill by prompting the agent to use a specific skill. 

## Generating Unit tests with OpenCode and skills
I have found two skills related to unit testing with JUnit. here are their links:  
https://agentskillsforall.com/skills/java-junit (junit-5-skill)  
https://agentskillsforall.com/skills/junit-skill (java-junit)  

This is my plan for testing the skills:  
I will try the skills in three rounds with a very basic prompt:
> For these units, generate unit tests. Do not mock the dependencies.

Since each skill has provided a robust framework for unit testing, I will give the simplest prompt.
However, since both skills have a guide for mocking, I have added not mocking the dependencies.  
In the first round, I will use GPT-OSS local model with the first skill which looks more sophisticated.
The Goal is to compare it to the time I generated unit tests without utilizing a skill.  
In the second round, I will use "MiMo V2.5 (medium variant)" with the second skill.  
In the last round, I will use "DeepSeek V4 Flash (default variant)" with both skills and compare it to
the previous round to see what would happen if multiple similar skills are used.

## Round 1: GPT-OSS with "junit-5-skill"
Remember, the goal is to compare the generated tests to the ones that didn't use a skill:
> For these units, generate unit tests. the generated test classes must end with "TestGPTOSS_SP".
> use "junit-5-skill" skill. Do not mock the dependencies.

It completed generating all test files after 27 minutes and 35 seconds!
Without the skill, this took 19 minutes and 41 seconds. It took longer since it had to
read all the skill context.

#### MyMath Unit
```java
class MyMathTestGPTOSS_SP {

  private MyMath myMath;

  @BeforeEach
  void setUp() {
    myMath = new MyMath();
  }

  @Test
  @DisplayName("checkSign returns positive for positive numbers")
  void checkSignPositive() {
    assertEquals("positive", myMath.checkSign(5));
  }

  @Test
  @DisplayName("checkSign returns negative for negative numbers")
  void checkSignNegative() {
    assertEquals("negative", myMath.checkSign(-3));
  }

  @Test
  @DisplayName("checkSign throws IllegalArgumentException for zero")
  void checkSignZeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @Test
  @DisplayName("reverseNumber returns negative of input")
  void reverseNumberPositive() {
    assertEquals(-10, myMath.reverseNumber(10));
  }

  @Test
  @DisplayName("reverseNumber returns positive of input")
  void reverseNumberNegative() {
    assertEquals(15, myMath.reverseNumber(-15));
  }
}
```
##### *checkSign(int)* method
Three methods are generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
This matches the output generated without skill.

##### *reverseNumber(int)* method
Three single methods are generated that contain:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
This is equivalence to the without skill generation. 

#### MyAdvancedMath Unit
```java
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
```
##### *add(int, int)* method
Four methods are generated that include:
- positive x and y
- a valid parametrized test
  - two both positive x and y
  - both zeros
- positive x and negative y and vise versa
- overflow test

Tests do have MC/DC.  
This is a noticeable improvement. In addition to what
without skill agent generated, we have a parametrized test and both zeros test.
However, it did use two assertions in a single method. Previously, this was also the case.

##### *powerOfTwo(int)* method
Five methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- a parametrized test
  - just above the lower boundary
  - two normal tests
  - the upper boundary
- just above the upper boundary

We have MC/DC.  
There are also improvements here. A parametrized test was used and just above the
lower boundary and the upper boundary are covered.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated for a normal test.  
This is equivalence to the no skill generated tests.

#### FileIO Unit
```java
class FileIOTestGPTOSS_SP {

  private FileIO fileIO;

  @BeforeEach
  void setUp() {
    fileIO = new FileIO();
  }

  @Test
  @DisplayName("readFile returns lines as array for a small file")
  void readSmallFile() throws IOException {
    Path temp = Files.createTempFile("fileio-test", ".txt");
    Files.writeString(temp, "line1\nline2\nline3");
    String[] result = fileIO.readFile(temp.toString());
    assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
    Files.deleteIfExists(temp);
  }

  @Test
  @DisplayName("readFile returns empty array for empty file")
  void readEmptyFile() throws IOException {
    Path temp = Files.createTempFile("fileio-empty", ".txt");
    String[] result = fileIO.readFile(temp.toString());
    assertArrayEquals(new String[]{}, result);
    Files.deleteIfExists(temp);
  }
}
```
Two methods are generated that include:
- a normal test
- empty file test

We don't run into exception, so we don't have statement coverage.
Last time, only a normal test was generated.

#### NameSearch Unit
```java
class NameSearchTestGPTOSS_SP {

  private NameSearch nameSearch;
  private FileIO fileIO;

  @BeforeEach
  void setUp() {
    nameSearch = new NameSearch();
    fileIO = new FileIO();
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns matching names")
  void findMatchingNames() throws IOException {
    Path temp = Files.createTempFile("names", ".txt");
    Files.writeString(temp, "Alice\nAlfred\nbob\nALBERT");
    String[] result = nameSearch.findNamesInFileThatStartWith(temp.toString(), fileIO, "Al");
    assertArrayEquals(new String[]{"Alice", "Alfred", "ALBERT"}, result);
    Files.deleteIfExists(temp);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array when no match")
  void noMatches() throws IOException {
    Path temp = Files.createTempFile("emptysearch", ".txt");
    Files.writeString(temp, "John\nJane");
    String[] result = nameSearch.findNamesInFileThatStartWith(temp.toString(), fileIO, "Xy");
    assertArrayEquals(new String[]{}, result);
    Files.deleteIfExists(temp);
  }
}
```
Two test methods are generated which include:
- a normal test
- a test without result.

The test achieves MC/DC.  
This is exactly what agent generated without any skills.

#### Comparison
Looks like the more complicated a method is, more improvement will be.
The *add* and *powerOfTwo* methods, which are the most complex methods
of the project, had a noticeable improvement. Also, one more edge case
was covered in *readFile* method. Other improvements are using private
variables and setting them up before each test and displaying a name
for each method. The naming of the methods is terrible too. Not only
they don't start with test, but also they don't exactly implicate what
they do. For example, a method called "powerZero" tests *powerOfTwo* with 0.
It should have been "testPowerOfZeroIsOne".  
The fact that it took the model 50% more time to generate
the tests must also be considered.

## Round 2 (MiMo V2.5 with "java-junit")
Now I will try a cloud model. Also, the other skill will be used. The prompt is only changed
to oblige the model to name the classes after its name.
> For these units, generate unit tests. the generated test classes must end with "TestMiMo_SP".
> use "java-junit" skill. Do not mock the dependencies.

It took a minute and 37 seconds to generate the unit tests.
#### MyMath Unit
```java
@DisplayName("MyMath Tests")
class MyMathTestMiMo_SP {

  private final MyMath myMath = new MyMath();

  @Test
  @DisplayName("checkSign returns 'positive' for positive numbers")
  void checkSign_shouldReturnPositive_whenNumberIsPositive() {
    assertEquals("positive", myMath.checkSign(5));
  }

  @ParameterizedTest(name = "checkSign({0}) should return 'negative'")
  @ValueSource(ints = {-1, -100, -999})
  @DisplayName("checkSign returns 'negative' for negative numbers")
  void checkSign_shouldReturnNegative_whenNumberIsNegative(int number) {
    assertEquals("negative", myMath.checkSign(number));
  }

  @Test
  @DisplayName("checkSign throws exception when input is zero")
  void checkSign_shouldThrowException_whenNumberIsZero() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @Test
  @DisplayName("reverseNumber negates positive number")
  void reverseNumber_shouldNegatePositiveNumber() {
    assertEquals(-5, myMath.reverseNumber(5));
  }

  @Test
  @DisplayName("reverseNumber negates negative number")
  void reverseNumber_shouldNegateNegativeNumber() {
    assertEquals(5, myMath.reverseNumber(-5));
  }

  @Test
  @DisplayName("reverseNumber returns zero when input is zero")
  void reverseNumber_shouldReturnZero_whenInputIsZero() {
    assertEquals(0, myMath.reverseNumber(0));
  }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive test
- a negative parametrized test
  - just above the boundary
  - two normal negative values test
- zero (boundary)

The tests have MC/DC.  
It's interesting that it only used parametrized for negatives. It should
have done the same for positives as well. It would have been ideal.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive
- a normal negative
- zero (at the boundary)

The tests have MC/DC.  
This is a very basic test generation, It feels like the skill done nothing.

#### MyAdvancedMath Unit
```java
@DisplayName("MyAdvancedMath Tests")
class MyAdvancedMathTestMiMo_SP {

  private final MyAdvancedMath math = new MyAdvancedMath();
  private final MyMath myMath = new MyMath();

  // --- add tests ---

  @Test
  @DisplayName("add returns sum of two positive integers")
  void add_shouldReturnSum_whenBothPositive() {
    assertEquals(5, math.add(2, 3));
  }

  @Test
  @DisplayName("add returns zero when both inputs are zero")
  void add_shouldReturnZero_whenBothZero() {
    assertEquals(0, math.add(0, 0));
  }

  @Test
  @DisplayName("add throws exception when first number is negative")
  void add_shouldThrowException_whenFirstIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
  }

  @Test
  @DisplayName("add throws exception when second number is negative")
  void add_shouldThrowException_whenSecondIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> math.add(5, -1));
  }

  @Test
  @DisplayName("add throws exception on integer overflow")
  void add_shouldThrowException_whenOverflow() {
    assertThrows(IllegalArgumentException.class, () -> math.add(Integer.MAX_VALUE, 1));
  }

  // --- powerOfTwo tests ---

  @ParameterizedTest(name = "powerOfTwo({0}) should return correct result")
  @CsvSource({
          "0, 1",
          "1, 2",
          "2, 4",
          "10, 1024",
          "30, 1073741824"
  })
  @DisplayName("powerOfTwo returns correct result for valid powers")
  void powerOfTwo_shouldReturnCorrectResult_whenPowerIsWithinRange(int power, int expected) {
    assertEquals(expected, math.powerOfTwo(power));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is negative")
  void powerOfTwo_shouldThrowException_whenPowerIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is greater than 30")
  void powerOfTwo_shouldThrowException_whenPowerExceeds30() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
  }

  // --- reverseArrayNumbers tests ---

  @Test
  @DisplayName("reverseArrayNumbers reverses sign of each element")
  void reverseArrayNumbers_shouldReverseSignsOfAllElements() {
    int[] input = {1, -2, 3, 0};
    int[] result = math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{-1, 2, -3, 0}, result);
  }

  @Test
  @DisplayName("reverseArrayNumbers handles empty array")
  void reverseArrayNumbers_shouldHandleEmptyArray() {
    int[] input = {};
    int[] result = math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{}, result);
  }

  @Test
  @DisplayName("reverseArrayNumbers handles single element array")
  void reverseArrayNumbers_shouldHandleSingleElementArray() {
    int[] input = {42};
    int[] result = math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{-42}, result);
  }
}

```
##### *add(int, int)* method
Five methods were generated that include:
- positive x and y
- both zeros
- negative x and positive y and vice versa
- overflow test

MC/DC attained.  
These are good tests. It could have been improved further more
with parametrized tests and boundary values.

##### *powerOfTwo(int)* method
Three methods are generated. The tests include:
- A parametrized valid values test
  - the lower boundary
  - just above the lower boundary
  - two normal tests
  - the upper boundary
- just below the lower boundary
- just above the upper boundary

MC/DC achieved.  
Agent did well in this test too. Replacing one of the normal tests with 29
would have made this ideal.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated which include:
- empty array test
- a normal test
- a single element array test

MC/DC is achieved.  
All edge cases covered.

#### FileIO Unit
```java
@DisplayName("FileIO Tests")
class FileIOTestMiMo_SP {

  private final FileIO fileIO = new FileIO();

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("readFile returns lines from a valid file")
  void readFile_shouldReturnLines_whenFileExists() throws IOException {
    File file = tempDir.resolve("names.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("Alice\nBob\nCharlie\n");
    }

    String[] result = fileIO.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{"Alice", "Bob", "Charlie"}, result);
  }

  @Test
  @DisplayName("readFile returns empty array for empty file")
  void readFile_shouldReturnEmptyArray_whenFileIsEmpty() throws IOException {
    File file = tempDir.resolve("empty.txt").toFile();
    file.createNewFile();

    String[] result = fileIO.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("readFile handles single line file")
  void readFile_shouldReturnSingleLine_whenFileHasOneLine() throws IOException {
    File file = tempDir.resolve("single.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("OnlyLine\n");
    }

    String[] result = fileIO.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{"OnlyLine"}, result);
  }

  @Test
  @DisplayName("readFile returns empty array for non-existent file")
  void readFile_shouldReturnEmptyArray_whenFileDoesNotExist() {
    String[] result = fileIO.readFile("non_existent_file.txt");
    assertArrayEquals(new String[]{}, result);
  }
}
```
Fours methods were generated that include:
- an empty file
- a file with multiple lines
- a file with a single line
- a non-existing file

MC/DC achieved.
This is well done. More edge cases could be covered, but they are not really necessary.

#### NameSearch Unit
```java
@DisplayName("NameSearch Tests")
class NameSearchTestMiMo_SP {

  private NameSearch nameSearch;
  private FileIO fileIO;
  private File namesFile;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    nameSearch = new NameSearch();
    fileIO = new FileIO();
    namesFile = tempDir.resolve("names.txt").toFile();
    try (FileWriter writer = new FileWriter(namesFile)) {
      writer.write("Alice\nBob\nCharlie\nAmanda\nDavid\nAnna\n");
    }
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns matching names")
  void findNamesInFileThatStartWith_shouldReturnMatchingNames() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "A");
    assertArrayEquals(new String[]{"Alice", "Amanda", "Anna"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array when no matches")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenNoMatches() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "Z");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns single match")
  void findNamesInFileThatStartWith_shouldReturnSingleMatch() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "D");
    assertArrayEquals(new String[]{"David"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns all names when prefix is empty")
  void findNamesInFileThatStartWith_shouldReturnAll_whenPrefixIsEmpty() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "");
    assertArrayEquals(new String[]{"Alice", "Bob", "Charlie", "Amanda", "David", "Anna"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array for empty file")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileIsEmpty() throws IOException {
    File emptyFile = tempDir.resolve("empty.txt").toFile();
    emptyFile.createNewFile();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            emptyFile.getAbsolutePath(), fileIO, "A");
    assertArrayEquals(new String[]{}, result);
  }
}
```
Five methods are generated which include:
- multiple result test
- single result test
- empty file test
- empty prefix test
- no match test

The test achieves MC/DC.  
This is a perfect unit test generation.

#### Conclusion
I can tell the skills are improving the generated tests structure.  
The only edge case that should have been covered is Integer.MIN_VALUE in 
*reverseNumber* method. The naming of the methods is done very well, if
not starting with "test" is ignored though.
There is more potential for utilizing parametrized tests, more boundary value checking and
using JUnit's temporary storage for sure.

## Round 3 (DeepSeek V4 Flash default variant)
The main goal is to see what happens if you use multiple skills. There might be some
inconsistencies. Prompt is basically the same:
> For these units, generate unit tests. the generated test classes must end with "TestDeepSeek_SP".
> use "java-junit" and "junit-5-skill" skills. Do not mock the dependencies.

It took 2 minutes and 36 seconds to generate the tests.

#### MyMath Unit
```java
@DisplayName("MyMath Tests (DeepSeek_SP)")
class MyMathTestDeepSeek_SP {

  private final MyMath myMath = new MyMath();

  @Test
  @DisplayName("checkSign returns 'positive' for positive numbers")
  void checkSign_shouldReturnPositive_whenNumberIsPositive() {
    assertEquals("positive", myMath.checkSign(1));
  }

  @ParameterizedTest(name = "checkSign({0}) should return 'positive'")
  @ValueSource(ints = {2, 100, Integer.MAX_VALUE})
  @DisplayName("checkSign returns 'positive' for various positive numbers")
  void checkSign_shouldReturnPositive_forVariousPositiveNumbers(int number) {
    assertEquals("positive", myMath.checkSign(number));
  }

  @ParameterizedTest(name = "checkSign({0}) should return 'negative'")
  @ValueSource(ints = {-1, -50, Integer.MIN_VALUE})
  @DisplayName("checkSign returns 'negative' for various negative numbers")
  void checkSign_shouldReturnNegative_forVariousNegativeNumbers(int number) {
    assertEquals("negative", myMath.checkSign(number));
  }

  @Test
  @DisplayName("checkSign throws IllegalArgumentException when input is zero")
  void checkSign_shouldThrowException_whenNumberIsZero() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> myMath.checkSign(0));
    assertEquals("Number had no sign", exception.getMessage());
  }

  @ParameterizedTest(name = "reverseNumber({0}) should return {1}")
  @org.junit.jupiter.params.provider.CsvSource({
          "5, -5",
          "-3, 3",
          "0, 0",
          "1, -1",
          "-1, 1"
  })
  @DisplayName("reverseNumber reverses sign correctly")
  void reverseNumber_shouldReverseSign_correctly(int input, int expected) {
    assertEquals(expected, myMath.reverseNumber(input));
  }

  @Test
  @DisplayName("reverseNumber handles Integer.MAX_VALUE")
  void reverseNumber_shouldHandleMaxValue() {
    assertEquals(Integer.MIN_VALUE + 1, myMath.reverseNumber(Integer.MAX_VALUE));
  }

  @Test
  @DisplayName("reverseNumber handles Integer.MIN_VALUE")
  void reverseNumber_shouldHandleMinValue() {
    assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
  }
}

```
##### *checkSign(int)* method
Four methods are generated. They include:
- just above the boundary
- a parametrized test with two normal positive values
- a negative numbers parametrized test
  - just below the boundary
  - a normal negative number
- zero (boundary)

The tests have MC/DC.  
The only issue is that 1 was tested in its own method but
-1 was tested inside a parametrized test. This was a weird inconsistency.

##### *reverseNumber(int)* method
Three methods are generated. they include:
- a mixed parametrized test
  - a normal negative
  - a normal positive
  - the boundary
  - just below the boundary
  - just above the boundary
- Integer.MAX
- Integer.MIN edge case

The tests have MC/DC.  
This is a perfect test for this method.

#### MyAdvancedMath Unit
```java
@DisplayName("MyAdvancedMath Tests (DeepSeek_SP)")
class MyAdvancedMathTestDeepSeek_SP {

  private final MyAdvancedMath math = new MyAdvancedMath();
  private final MyMath myMath = new MyMath();

  @ParameterizedTest(name = "add({0}, {1}) should return {2}")
  @CsvSource({
          "1, 2, 3",
          "0, 5, 5",
          "10, 20, 30",
          "0, 0, 0",
          "100, 200, 300"
  })
  @DisplayName("add returns correct sum for positive inputs")
  void add_shouldReturnCorrectSum_whenBothInputsArePositive(int x, int y, int expected) {
    assertEquals(expected, math.add(x, y));
  }

  @Test
  @DisplayName("add throws exception when first argument is negative")
  void add_shouldThrowException_whenFirstArgumentIsNegative() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> math.add(-1, 5));
    assertEquals("Input numbers should be positive.", exception.getMessage());
  }

  @Test
  @DisplayName("add throws exception when second argument is negative")
  void add_shouldThrowException_whenSecondArgumentIsNegative() {
    assertThrows(IllegalArgumentException.class,
            () -> math.add(5, -1));
  }

  @Test
  @DisplayName("add throws exception when both arguments are negative")
  void add_shouldThrowException_whenBothArgumentsAreNegative() {
    assertThrows(IllegalArgumentException.class,
            () -> math.add(-1, -1));
  }

  @Test
  @DisplayName("add throws exception on integer overflow")
  void add_shouldThrowException_whenResultOverflows() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> math.add(Integer.MAX_VALUE, 1));
    assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
  }

  @Test
  @DisplayName("add throws exception when result would overflow near max boundary")
  void add_shouldThrowException_whenResultExceedsMaxInt() {
    assertThrows(IllegalArgumentException.class,
            () -> math.add(Integer.MAX_VALUE - 5, 10));
  }

  @ParameterizedTest(name = "powerOfTwo({0}) should return {1}")
  @CsvSource({
          "0, 1",
          "1, 2",
          "2, 4",
          "3, 8",
          "5, 32",
          "10, 1024",
          "20, 1048576",
          "30, 1073741824"
  })
  @DisplayName("powerOfTwo returns correct result for valid powers")
  void powerOfTwo_shouldReturnCorrectResult_whenPowerIsValid(int power, int expected) {
    assertEquals(expected, math.powerOfTwo(power));
  }

  @ParameterizedTest(name = "powerOfTwo({0}) should throw exception")
  @ValueSource(ints = {-1, -5, -100})
  @DisplayName("powerOfTwo throws exception when power is negative")
  void powerOfTwo_shouldThrowException_whenPowerIsNegative(int power) {
    assertThrows(IllegalArgumentException.class,
            () -> math.powerOfTwo(power));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is 31")
  void powerOfTwo_shouldThrowException_whenPowerIs31() {
    assertThrows(IllegalArgumentException.class,
            () -> math.powerOfTwo(31));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is 100")
  void powerOfTwo_shouldThrowException_whenPowerIsFarOutOfRange() {
    assertThrows(IllegalArgumentException.class,
            () -> math.powerOfTwo(100));
  }

  @Test
  @DisplayName("reverseArrayNumbers reverses signs when using real MyMath")
  void reverseArrayNumbers_shouldReverseSignsOfAllElements() {
    int[] input = {10, -20, 30, -40, 0};
    int[] expected = {-10, 20, -30, 40, 0};
    assertArrayEquals(expected, math.reverseArrayNumbers(input, myMath));
  }

  @Test
  @DisplayName("reverseArrayNumbers returns empty array when input is empty")
  void reverseArrayNumbers_shouldReturnEmptyArray_whenInputIsEmpty() {
    int[] input = {};
    assertArrayEquals(new int[]{}, math.reverseArrayNumbers(input, myMath));
  }

  @Test
  @DisplayName("reverseArrayNumbers handles single element array")
  void reverseArrayNumbers_shouldHandleSingleElement() {
    int[] input = {7};
    assertArrayEquals(new int[]{-7}, math.reverseArrayNumbers(input, myMath));
  }

  @Test
  @DisplayName("reverseArrayNumbers modifies the original array")
  void reverseArrayNumbers_shouldModifyOriginalArray() {
    int[] input = {1, 2, 3};
    math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{-1, -2, -3}, input);
  }

  @Test
  @DisplayName("reverseArrayNumbers handles array with all zeros")
  void reverseArrayNumbers_shouldHandleAllZeros() {
    int[] input = {0, 0, 0};
    assertArrayEquals(new int[]{0, 0, 0}, math.reverseArrayNumbers(input, myMath));
  }
}

```
##### *add(int, int)* method
Six methods are generated that include:
- a valid parametrized test
  - three both positive tests
  - x is zero and y is positive
  - both are zeros
- x is negative and y is positive and vice versa
- both negatives
- two overflow tests

MC/DC achieved.  
This is also a good test. boundary values are tested well. The second overflow test
was unnecessary.

##### *powerOfTwo(int)* method
Six methods are generated that include:
- a valid parametrized test
  - the lower boundary
  - just above the lower boundary
  - five normal values
  - the upper boundary
- an invalid parametrized test
  - just below the lower boundary
  - two normal negative values
- just above the upper boundary
- exceeding the upper boundary with a high distance

We have MC/DC.  
Overall, good tests are generated, however, the last test was unnecessary.

##### *reverseArrayNumbers(int[], MyMath)* method
Five methods are generated which include:
- a normal test
- empty array test
- single element array
- checking the modification of the original array
- all zeros array

MC/DC is achieved.  
The last two tests seem unnecessary. Checking the modification of array is done
since it's the array parameter in the method is a reference to the original array, so it's
a java feature. Zero has been checked inside *MyMath* unit, so it is unnecessary to test it here.

#### FileIO Unit
```java
@DisplayName("FileIO Tests (DeepSeek_SP)")
class FileIOTestDeepSeek_SP {

  private final FileIO fileIO = new FileIO();

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("readFile returns all lines from valid file")
  void readFile_shouldReturnLines_whenFileExists() throws IOException {
    File file = tempDir.resolve("test.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("line1\nline2\nline3\n");
    }
    assertArrayEquals(new String[]{"line1", "line2", "line3"},
            fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile returns empty array for empty file")
  void readFile_shouldReturnEmptyArray_whenFileIsEmpty() throws IOException {
    File file = tempDir.resolve("empty.txt").toFile();
    file.createNewFile();
    assertArrayEquals(new String[]{}, fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile handles single line file")
  void readFile_shouldReturnSingleLine_whenFileHasOneLine() throws IOException {
    File file = tempDir.resolve("single.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("soleEntry\n");
    }
    assertArrayEquals(new String[]{"soleEntry"},
            fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile handles file with trailing newline")
  void readFile_shouldHandleTrailingNewline() throws IOException {
    File file = tempDir.resolve("trailing.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("first\nsecond\n");
    }
    assertArrayEquals(new String[]{"first", "second"},
            fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile returns empty array for non-existent file")
  void readFile_shouldReturnEmptyArray_whenFileDoesNotExist() {
    assertArrayEquals(new String[]{},
            fileIO.readFile(tempDir.resolve("nonexistent.txt").toString()));
  }

  @Test
  @DisplayName("readFile handles file with special characters")
  void readFile_shouldHandleSpecialCharacters() throws IOException {
    File file = tempDir.resolve("special.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("héllo\nwörld\n");
    }
    assertArrayEquals(new String[]{"héllo", "wörld"},
            fileIO.readFile(file.getAbsolutePath()));
  }
}
```
Six methods are generated that include:
- a normal test
- an empty file test
- a single line file test
- trailing new lines
- non-existing file
- file containing special characters

MC/DC achieved.
Edge cases are covered well, but some of them are just necessary. Java's FileReader itself 
will consider empty line at the end as null. Java will deal with special character itself, not
our unit.

#### NameSearch Unit
```java
@DisplayName("NameSearch Tests (DeepSeek_SP)")
class NameSearchTestDeepSeek_SP {

  private NameSearch nameSearch;
  private FileIO fileIO;
  private File namesFile;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    nameSearch = new NameSearch();
    fileIO = new FileIO();
    namesFile = tempDir.resolve("names.txt").toFile();
    try (FileWriter writer = new FileWriter(namesFile)) {
      writer.write("Alice\nBob\nCharlie\nAmanda\nDavid\nAnna\nAlex\n");
    }
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns names matching prefix")
  void findNamesInFileThatStartWith_shouldReturnMatchingNames() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "A");
    assertAll("should find all names starting with A",
            () -> assertArrayEquals(new String[]{"Alice", "Amanda", "Anna", "Alex"}, result));
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array when no match")
  void findNamesInFileThatStartWith_shouldReturnEmptyArray_whenNoMatch() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "Z");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns single match")
  void findNamesInFileThatStartWith_shouldReturnSingleMatch() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "B");
    assertArrayEquals(new String[]{"Bob"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns all names with empty prefix")
  void findNamesInFileThatStartWith_shouldReturnAllNames_whenPrefixIsEmpty() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "");
    assertArrayEquals(
            new String[]{"Alice", "Bob", "Charlie", "Amanda", "David", "Anna", "Alex"},
            result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty for empty file")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileIsEmpty() throws IOException {
    File emptyFile = tempDir.resolve("empty.txt").toFile();
    emptyFile.createNewFile();
    String[] result = nameSearch.findNamesInFileThatStartWith(
            emptyFile.getAbsolutePath(), fileIO, "A");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith handles prefix with special characters")
  void findNamesInFileThatStartWith_shouldHandleSpecialPrefix() throws IOException {
    File specialFile = tempDir.resolve("special.txt").toFile();
    try (FileWriter writer = new FileWriter(specialFile)) {
      writer.write("@lpha\n#hash\n@lso\n");
    }
    String[] result = nameSearch.findNamesInFileThatStartWith(
            specialFile.getAbsolutePath(), fileIO, "@");
    assertArrayEquals(new String[]{"@lpha", "@lso"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith is case-sensitive")
  void findNamesInFileThatStartWith_shouldBeCaseSensitive() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "a");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty for non-existent file")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileDoesNotExist() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempDir.resolve("nope.txt").toString(), fileIO, "A");
    assertArrayEquals(new String[]{}, result);
  }
}
```
Eight methods are generated which include:
- a test with multiple result
- a no matching test
- a single result test
- empty prefix test
- empty file test
- special prefix test
- case sensitivity test
- non-existing file test

The test achieves MC/DC.  
Like *FileIO* unit test, many of the tests above are unnecessary which are special prefix test,
case sensitivity test and non-existing file test. All necessary edge cases are covered.


#### Conclusion
Agent did superb. I wonder if it's because of the model itself. So I will generate tests with
this model again but without any skill in a bonus round. The Naming of methods is good, however, 
they don't start with test. Boundary values are checked very well. MC/DC is achieved without me
mentioning it. Edge cases are covered well, but testing some of them were unnecessary.  
Mixing skills did not cause any inconsistency, and this round had the best performance so far.
So, on paper, mixing skills will improve the generated unit tests, but there should be more
tests to really evaluate this.

## Bonus round: DeepSeek V4 Flash default variant without skill
Since this model is superb, I must compare its performance when it generates unit tests
without any skill.
> For these units, generate unit tests. Do not mock the dependencies and do not use any skill.
> The generated test classes must end with "TestDeepSeek_NS".

It took 33.9 seconds to generate the tests, 2 minutes less than the previous round.

#### MyMath Unit
```java
public class MyMathTestDeepSeek_NS {

  @Test
  void testCheckSignPositive() {
    MyMath mm = new MyMath();
    assertEquals("positive", mm.checkSign(5));
  }

  @Test
  void testCheckSignNegative() {
    MyMath mm = new MyMath();
    assertEquals("negative", mm.checkSign(-3));
  }

  @Test
  void testCheckSignZeroThrowsException() {
    MyMath mm = new MyMath();
    assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void testReverseNumberPositive() {
    MyMath mm = new MyMath();
    assertEquals(-7, mm.reverseNumber(7));
  }

  @Test
  void testReverseNumberNegative() {
    MyMath mm = new MyMath();
    assertEquals(4, mm.reverseNumber(-4));
  }

  @Test
  void testReverseNumberZero() {
    MyMath mm = new MyMath();
    assertEquals(0, mm.reverseNumber(0));
  }
}

```
##### *checkSign(int)* method
Three methods are generated. They include:
- a normal positive test
- a normal negative test
- zero (boundary)

The tests have MC/DC.  
This is a very basic test set. Last round, Parametrized tests and boundary values
were utilized.

##### *reverseNumber(int)* method
Three methods are generated. they include:
- a normal positive test
- a normal negative test
- zero (boundary)

The tests have MC/DC.  
This is also very basic. Last round did ideal here. The gap is massive.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestDeepSeek_NS {

  private final MyAdvancedMath mam = new MyAdvancedMath();

  @ParameterizedTest
  @CsvSource({"1, 2, 3", "0, 5, 5", "100, 200, 300", "2147483646, 1, 2147483647"})
  void testAddValidInputs(int x, int y, int expected) {
    assertEquals(expected, mam.add(x, y));
  }

  @Test
  void testAddNegativeXThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(-1, 5));
  }

  @Test
  void testAddNegativeYThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(5, -1));
  }

  @Test
  void testAddBothNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(-1, -1));
  }

  @Test
  void testAddOverflowThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(Integer.MAX_VALUE, 1));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 2", "2, 4", "3, 8", "4, 16", "5, 32", "10, 1024", "30, 1073741824"})
  void testPowerOfTwoValidInputs(int power, int expected) {
    assertEquals(expected, mam.powerOfTwo(power));
  }

  @Test
  void testPowerOfTwoNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(-1));
  }

  @Test
  void testPowerOfTwoAbove30Throws() {
    assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(31));
  }

  @Test
  void testReverseArrayNumbers() {
    int[] input = {1, -2, 3, -4, 0};
    int[] expected = {-1, 2, -3, 4, 0};
    assertArrayEquals(expected, mam.reverseArrayNumbers(input, new MyMath()));
  }

  @Test
  void testReverseArrayNumbersEmpty() {
    int[] input = {};
    assertArrayEquals(new int[]{}, mam.reverseArrayNumbers(input, new MyMath()));
  }
}

```
##### *add(int, int)* method
Five methods are generated that include:
- a valid parametrized test
  - two both positive tests
  - x is zero and y is positive
  - x and y sum is just below overflow
- x is negative and y is positive and vice versa
- both negatives
- an overflow test

MC/DC achieved.  
Somehow, in this complex unit, agent is doing much better! This is
as good as the previous round. The previous round had covered a both zero
test while this round covered a near overflow test. It also used a parametrized
test.

##### *powerOfTwo(int)* method
Three methods are generated that include:
- a valid parametrized test
  - the lower boundary
  - just above the lower boundary
  - five normal values
  - the upper boundary
- just below the lower boundary
- just above the upper boundary

We have MC/DC.  
This is a great test set. The Last round was the same but with some unnecessary tests.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated which include:
- a normal test
- empty array test

MC/DC attained.  
It is missing a single element array. The Last round had covered, and it also
included some unnecessary edge cases.

#### FileIO Unit
```java
public class FileIOTestDeepSeek_NS {

  @TempDir
  Path tempDir;

  @Test
  void testReadFileWithMultipleLines() throws Exception {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, "Alice\nBob\nCharlie".getBytes());

    FileIO fileIo = new FileIO();
    String[] result = fileIo.readFile(file.toString());
    assertArrayEquals(new String[]{"Alice", "Bob", "Charlie"}, result);
  }

  @Test
  void testReadFileEmptyFile() throws Exception {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);

    FileIO fileIo = new FileIO();
    String[] result = fileIo.readFile(file.toString());
    assertEquals(0, result.length);
  }

  @Test
  void testReadFileNonExistent() {
    FileIO fileIo = new FileIO();
    String[] result = fileIo.readFile("nonexistent.txt");
    assertEquals(0, result.length);
  }
}
```
Three methods are generated that include:
- a normal test
- an empty file test
- non-existing file

MC/DC achieved.
Edge cases are covered well. The agent had generated three more tests when it utilized
skills. Although some of them were unnecessary.

#### NameSearch Unit
```java
public class NameSearchTestDeepSeek_NS {

  @TempDir
  Path tempDir;

  @Test
  void testFindNamesInFileThatStartWithMatchingPrefix() throws Exception {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, "Alice\nBob\nCharlie\nAlex".getBytes());

    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(
            file.toString(), new FileIO(), "Al");
    assertArrayEquals(new String[]{"Alice", "Alex"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWithNoMatch() throws Exception {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, "Bob\nCharlie".getBytes());

    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(
            file.toString(), new FileIO(), "X");
    assertEquals(0, result.length);
  }

  @Test
  void testFindNamesInFileThatStartWithEmptyFile() throws Exception {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);

    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(
            file.toString(), new FileIO(), "A");
    assertEquals(0, result.length);
  }
}

```
Three methods are generated which include:
- a test with multiple result
- no match test
- empty file test

The test achieves MC/DC.  
The gap between rounds has been increased. This round should have covered a single result and
empty prefix tests. The Last round had eight tests, but some of them were unnecessary.


#### Comparison
After I checked *MyMath*, I was so sure that it was the skills
that elevated the performance of this agent, but after that unit, the gap between two rounds
was reduced by a lot!  
Definitely, the previous round structure is much better than this since the skills provide a framework
for the model to work with. Also, more edge cases were covered in the previous round, but some of them
were unnecessary.  
By default, this model is great, but users can also improve it further with skills.
The skills caused the agent to cover more boundary values, edge cases and much better structure.
Alas, more redundant tests were generated.


## Final Conclusion
It is not surprising that the skills improved the agents' performance, especially by providing 
a framework for them to work with which resulted in a major boost to the structure of the tests.
The agents used parametrized tests without being prompted. There is little improvement to boundary
values which are probably credited to parametrized tests. There was also an improvement to edge case
testing too.  
It does take longer to generate tests since agent need to read the skill as well and more redundant
tests will be generated.  
Using skills should be done if you agree to more token consumption and time for code generation.
As we saw, DeepSeek took 5 times longer to generate tests with two skills.