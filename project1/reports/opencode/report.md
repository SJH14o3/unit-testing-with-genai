# Experiment with OpenCode in Unit Test Generation
## OpenCode Introduction
OpenCode is an open-source agent that allows developers to write and execute code directly
within a terminal, IDE, or desktop application. It has been developed by Anomaly Innovations.
Similar to Continue.dev, it provides access to multiple model providers.
Here is a list of its popular providers such as:
- Anthropic
- GitHub Copilot
- OpenAI
- Google
- OpenRouter
- OpenCode Zen and Go

To use a provider, the user must obtain an API key directly from the provider's website.
This process typically requires purchasing credits for each individual service,
which can be disadvantageous. However, users have the flexibility to utilize aggregator providers, such as OpenRouter.
It also can run a local model using Ollama. However, the model must support *tools* in
The platform also supports running local models via Ollama. Crucially, the model must expose specific
tools in its capabilities. This requirement prevented the use of Gemma3 with this tool.
Users can use this command to run OpenCode in terminal with local models:
```bash
ollama launch opencode --config  
```
The command will prompt the user to select a model.  
Testing the agent in the terminal revealed that the environment is highly functional.
The terminal application features a rudimentary GUI, using a series of buttons to open a command menu.
This menu allows actions such as sharing a session, switching sessions, or selecting a model.
It also displays a history of recently used commands, and a suggested list
is provided for model selection. The main usability complaint is the inability
to collapse providers when a provider contains hundreds of models, requiring extensive scrolling.

### How to install OpenCode CLI
Installation can be accomplished in several ways. I used Node.js with the following command:
```bash
npm i -g opencode-ai
```
After it's installed. you can verify the installation by running the command below:
```bash
opencode --version
```

### How to use OpenCode
To begin using OpenCode, open a terminal in your project directory and run the following command
```bash
opencode
```
This will launch OpenCode in the terminal.

#### mode
There are two modes in OpenCode.
You can switch between them using *tab* shortcut:
- Build (Primary)
- Plan


##### Build mode
This mode is the primary, unrestricted AI agent that actively executes your coding tasks.
It has full permission to read, write, modify files, and run system commands,
making it the mode used to physically implement features and make changes.

##### Plan mode
This mode is a restricted, read-only environment designed for AI analysis,
scoping, and architectural planning without risking unintended modifications
to your codebase. It restricts destructive tools while loading context
so you can outline features safely.

#### Selecting models
Users must first acquire an API key from the designated provider, initiating the process
by selecting the provider and then inserting the API credentials.
OpenCode will then verify the successful connection with the provider.
You can also configure local models in the path below:  
"(user)\.config\opencode\opencode.jsonc"  
However, in order to apply the changes to the json file, you will need to restart OpenCode.

#### Linking something
Users can attach files or images to the AI chatbot by typing @ followed by the file's relative path.

After You have written your prompt, you can click on the enter
button and the model will begin working.
Two notable features of OpenCode are its ability to display the
time it took to generate output and the amount of context window
utilized.

## Generating Unit tests with OpenCode
The goal this time is to use two local models: GPT-OSS:20b and Gemma4:e4b with the same prompt
as Continue.dev to see how much will change if we use different tools. Although even with the
same tools, probably we will get different results since they are Artificial Intelligence. What makes
AI different from an algorithm is that the AI output might not be the same always with the same input.
So here is the first prompt I used in Continue.dev:

## Round 1
Let us start with a simple prompt. In order to prevent mock, I will explicitly include it in the prompt.
> For these units, generate unit test using JUnit5. do not use mock for dependencies. the generated
> test classes must end with "Test(ModelName)_P1"

for the suffix, if the model name is gemma4, then a test class name will have "TestGemma4_P1" suffix.

### GPT-OSS:20b
It completed generating all test files after 19 minutes and 41 seconds!
With Continue.dev, this took 10 minutes and 23 seconds!

#### MyMath Unit
```java
class MyMathTestGPTOSS_P1 {

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
  void testCheckSignZero() {
    MyMath mm = new MyMath();
    assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void testReverseNumber() {
    MyMath mm = new MyMath();
    assertEquals(-5, mm.reverseNumber(5));
    assertEquals(0, mm.reverseNumber(0));
    assertEquals(3, mm.reverseNumber(-3));
  }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
This matches the output generated by Continue.dev.

##### *reverseNumber(int)* method
a single method was generated with three assertions:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
The primary difference from Continue.dev is that this approach consolidates multiple assertions into a single method.

#### MyAdvancedMath Unit
```java
class MyAdvancedMathTestGPTOSS_P1 {

  @Test
  void testAddValid() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertEquals(12, ma.add(5, 7));
  }

  @Test
  void testAddNegative() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertThrows(IllegalArgumentException.class, () -> ma.add(-1, 5));
    assertThrows(IllegalArgumentException.class, () -> ma.add(5, -3));
  }

  @Test
  void testAddOverflow() {
    MyAdvancedMath ma = new MyAdvancedMath();
    int x = Integer.MAX_VALUE - 10;
    int y = 20; // 20 > 10 so will overflow
    assertThrows(IllegalArgumentException.class, () -> ma.add(x, y));
  }

  @Test
  void testPowerOfTwoZero() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertEquals(1, ma.powerOfTwo(0));
  }

  @Test
  void testPowerOfTwoPositive() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertEquals(32, ma.powerOfTwo(5));
  }

  @Test
  void testPowerOfTwoOutOfBounds() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertThrows(IllegalArgumentException.class, () -> ma.powerOfTwo(-1));
    assertThrows(IllegalArgumentException.class, () -> ma.powerOfTwo(31));
  }

  @Test
  void testReverseArrayNumbers() {
    MyAdvancedMath ma = new MyAdvancedMath();
    MyMath mm = new MyMath();
    int[] input = new int[]{1, -2, 0, 3};
    int[] expected = new int[]{-1, 2, 0, -3};
    int[] actual = ma.reverseArrayNumbers(input.clone(), mm);
    assertArrayEquals(expected, actual);
  }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- positive x and negative y and vise versa
- overflow test

Tests do have MC/DC.  
This is the same with Continue.dev tests.

##### *powerOfTwo(int)* method
Three methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- a normal test
- just above the upper boundary

We have MC/DC.  
Continue.dev generated two normal tests, while here only a single one was generated.
This is better since having two normal tests would mean redundancy.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated for a normal test.  
This is also what Continue.dev had generated before.

#### FileIO Unit
```java
class FileIOTestGPTOSS_P1 {

  private File tempFile;

  @AfterEach
  void cleanUp() {
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  private void writeLines(File file, String... lines) throws IOException {
    try (FileWriter fw = new FileWriter(file)) {
      for (String line : lines) {
        fw.write(line + System.lineSeparator());
      }
    }
  }

  @Test
  void testReadFile() throws Exception {
    tempFile = File.createTempFile("fileio_test", ".txt");
    writeLines(tempFile, "first", "second", "third");

    FileIO fio = new FileIO();
    String[] result = fio.readFile(tempFile.getAbsolutePath());

    assertEquals(3, result.length);
    assertArrayEquals(new String[]{"first", "second", "third"}, result);
  }
}
```
A single test method was generated that includes a normal test.

We don't run into exception, so we don't have statement coverage.
Continue.dev covered non-existing file edge case.

#### NameSearch Unit
```java
class NameSearchTestGPTOSS_P1 {

  private File tempFile;

  @AfterEach
  void cleanUp() {
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  private void writeLines(File file, String... lines) throws Exception {
    try (FileWriter fw = new FileWriter(file)) {
      for (String line : lines) {
        fw.write(line + System.lineSeparator());
      }
    }
  }

  @Test
  void testFindNamesInFileThatStartWith() throws Exception {
    tempFile = File.createTempFile("names", ".txt");
    writeLines(tempFile, "Alice", "Alfred", "Bob", "Charlie", "Alfreda");

    FileIO fileIO = new FileIO();
    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Al");

    assertArrayEquals(new String[]{"Alice", "Alfred", "Alfreda"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWithNoMatch() throws Exception {
    tempFile = File.createTempFile("names", ".txt");
    writeLines(tempFile, "Alice", "Bob", "Charlie");

    FileIO fileIO = new FileIO();
    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");

    assertArrayEquals(new String[0], result);
  }
}
```
Two test methods are generated which include:
- a normal test
- a test without result.

The test achieves MC/DC.  
This is equivalence to the code generated by Continue.dev.

#### Comparison
There are some slight differences in comparison to Continue.dev:
- Method names do comply with what they test, and they don't start with test.
Last time with Continue.dev, they did not.
- More methods have multiple assertions.
- It did not use private variables for units while Continue.dev did.
- Continue Covered non-existing file edge case for FileIO.
- OpenCode generated one less redundant test.
- OpenCode took 9 minutes and 17 seconds more to generate!
Almost twice as Continue!

Overall, there are no major differences.

### Gemma4:e4b
The agent generated all the unit tests in one go. Last time, I ran into some issues.
It took 5 minutes and 24 seconds to generate tests.

#### MyMath Unit
```java
public class MyMathTestGemma4_P1 {

  private final MyMath myMath = new MyMath();

  @Test
  void testCheckSignPositive() {
    assertEquals("positive", myMath.checkSign(10));
  }

  @Test
  void testCheckSignNegative() {
    assertEquals("negative", myMath.checkSign(-5));
  }

  @Test
  void testCheckSignZero() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myMath.checkSign(0);
    });
    assertEquals("Number had no sign", thrown.getMessage());
  }

  @Test
  void testReverseNumberPositive() {
    assertEquals(-1234, myMath.reverseNumber(1234));
  }

  @Test
  void testReverseNumberNegative() {
    assertEquals(500, myMath.reverseNumber(-500));
  }

  @Test
  void testReverseNumberZero() {
    assertEquals(0, myMath.reverseNumber(0));
  }
}
```
Private variables are used. Although they are initialized when they are defined.
Previously, a method @BeforeEach annotation was used to initialize them.

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.
It also has asserted the exception message which is unnecessary.  
This is equivalence to the generated tests with Continue.dev.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
It again used large numbers like 1234 and -500.  
This is very similar to previous time.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestGemma4_P1 {

  private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();

  @Test
  void testAddSuccessfulAddition() {
    assertEquals(15, myAdvancedMath.add(10, 5));
  }

  @Test
  void testAddWithNegativeInputX() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.add(-1, 5);
    });
    assertEquals("Input numbers should be positive.", thrown.getMessage());
  }

  @Test
  void testAddWithNegativeInputY() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.add(10, -5);
    });
    assertEquals("Input numbers should be positive.", thrown.getMessage());
  }

  @Test
  void testAddOverflow() {
    // Max Int value - 10 will still fit. Max Int + 1 will overflow.
    int x = Integer.MAX_VALUE - 1;
    int y = 2;
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.add(x, y);
    });
    assertEquals("Cannot fit the result in an Integer.", thrown.getMessage());
  }

  @Test
  void testPowerOfTwoZero() {
    assertEquals(1, myAdvancedMath.powerOfTwo(0));
  }

  @Test
  void testPowerOfTwoSmall() {
    assertEquals(2, myAdvancedMath.powerOfTwo(1));
    assertEquals(16, myAdvancedMath.powerOfTwo(4));
    assertEquals(1024, myAdvancedMath.powerOfTwo(10));
  }

  @Test
  void testPowerOfTwoMax() {
    // 2^30
    assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
  }

  @Test
  void testPowerOfTwoNegativePower() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.powerOfTwo(-1);
    });
    assertEquals("power should be 0<=power<=30", thrown.getMessage());
  }

  @Test
  void testPowerOfTwoTooLargePower() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.powerOfTwo(31);
    });
    assertEquals("power should be 0<=power<=30", thrown.getMessage());
  }

  @Test
  void testReverseArrayNumbers() {
    // The MyMath instance used here does not use mocks as per requirement
    MyMath myMath = new MyMath();
    int[] input = {1, 2, 3};
    int[] expected = {-1, -2, -3};

    int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);

    for (int i = 0; i < 3; i++) {
      assertEquals(expected[i], result[i], "Element at index " + i + " mismatch");
    }
  }
}
```
##### *add(int, int)* method
Four methods are generated that include:
- positive x and y
- positive x and negative y and vise versa
- overflow test

Tests do have MC/DC.  
The tests are the same as the Continue.dev tests, but a better test for overflow is
generated and there isn't a method with multiple assertions.

##### *powerOfTwo(int)* method
Five methods are generated. They include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- two normal tests
- the upper boundary
- just above the upper boundary

We have MC/DC.  
There are two improvements actually!
*just above the lower boundary* and *the upper boundary* were not tested last time.
Both times, models generated two normal tests.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated for a normal test, like what Continue.dev did.
MC/DC is achieved.

#### FileIO Unit
Interestingly, @BeforeEach is used in the two remaining units.
This is most likely because of the temporary file initialization.
```java
public class FileIOTestGemma4_P1 {

  private FileIO fileIO;
  private File tempFile;

  @BeforeEach
  void setUp() throws IOException {
    fileIO = new FileIO();
    // Create a temporary file for testing
    tempFile = File.createTempFile("testfile", ".txt");
  }

  @Test
  void testReadFileSuccess() throws IOException {
    String content = "Line 1\nLine 2\nLine 3";
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(content);
    }

    // Testing the behavior of the public method
    String[] lines = fileIO.readFile(tempFile.getAbsolutePath());

    assertNotNull(lines);
    assertEquals(3, lines.length);
    assertEquals("Line 1", lines[0]);
    assertEquals("Line 2", lines[1]);
    assertEquals("Line 3", lines[2]);
  }

  @Test
  void testReadFileEmptyFile() throws IOException {
    // Create an empty file
    tempFile.createNewFile();

    // The method uses BufferedReader and readLine(), which correctly handles an empty file resulting in an empty array
    String[] lines = fileIO.readFile(tempFile.getAbsolutePath());

    assertNotNull(lines);
    assertEquals(0, lines.length);
  }

  @Test
  void testReadFileNonExistentFile() {
    // Use a path that definitely does not exist
    String nonExistentPath = "non_existent_file_" + System.currentTimeMillis() + ".txt";

    // The original implementation catches IOException and prints stack trace, 
    // but still returns an empty list via stream().toArray().
    String[] lines = fileIO.readFile(nonExistentPath);

    assertNotNull(lines);
    assertTrue(lines.length == 0);
  }
}
```
Three test methods are generated that include:
- a normal test
- an empty file test
- a non-existing file

MC/DC is achieved.
Test wise, there are no differences compared to Continue.dev, however this time,
tests don't write in binary to temporary files and also there is no @AfterEach method to
clean up the temporary file.

#### NameSearch Unit
```java
public class NameSearchTestGemma4_P1 {

  private NameSearch nameSearch;
  private File tempFile;

  @BeforeEach
  void setUp() throws IOException {
    nameSearch = new NameSearch();
    // Create a temporary file for testing
    tempFile = File.createTempFile("" +
            "testnames", ".txt");
  }

  @Test
  void testFindNamesInFileWhenNamesStartWithPrefix() throws IOException {
    String content = "apple\napricot\nbanana\napply\n";
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(content);
    }

    // Dependency injection requires FileIO. We mock file creation for testing, 
    // but since dependencies cannot be mocked, we use a TestFileIO implementation 
    // or assume a controlled environment. For this controlled test, we use a helper approach
    // to handle the file dependency if FileIO needs *actual* file operations.
    // Since we cannot strictly mock FileIO in a dependency-free way within this scope,
    // we will pass a file object that the concrete FileIO handles.

    // For clean testing, we must ensure the FileIO used here is robust.
    // The internal implementation of FileIO, when reading from this tempFile, should work.

    // Running the test with the dependency injection
    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "ap");

    assertNotNull(result);
    assertEquals(3, result.length);
    assertArrayEquals(new String[]{"apple", "apricot", "apply"}, result);
  }

  @Test
  void testFindNamesInFileWithNoMatches() throws IOException {
    String content = "banana\norange\norange";
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(content);
    }

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "z");

    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testFindNamesInFileWithEmptyContent() throws IOException {
    // Create an empty file
    tempFile.createNewFile();

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "a");

    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testFindNamesInFileWithNonExistFile() {
    // Test failure case for FileIO dependency
    String nonExistentPath = "non_existent_file_" + System.currentTimeMillis() + ".txt";

    // The FileIO implementation catches IOException and returns an empty array.
    String[] result = nameSearch.findNamesInFileThatStartWith(nonExistentPath, new dmst.seip.angor.junitdemo.io.FileIO(), "test");

    assertNotNull(result);
    assertEquals(0, result.length);
  }
}
```
Four test methods are generated which include:
- a normal test
- a test without result
- an empty file test
- a non-existing file test

The test achieves MC/DC.  
Compared to the code Continue.dev generated, a non-existing file test
is new, but it's also unnecessary since it's FileIO that must handle 
non-existing file.

#### Comparison
There were minor differences:
- More boundary value testing for *powerOfTwo* method
- An unnecessary test for *NameSearch* unit.
- Some other differences that I mentioned for each method.
- OpenCode took 1 minute and 34 seconds more to generate than Continue.dev.

Overall, no major differences.


## Round 2
Now We run it back with a more complex prompt that I gave to Continue.dev. Also, I didn't measure
how long it took Continue.dev to generate code from now on, but conspicuously, OpenCode takes much longer,
specially with GPT-OSS.
> For these units, generate unit test using JUnit5. do not use mock for dependencies.
> focus on boundary values and replace them with normal tests (e.g. if 0 is a boundary, test
> -1, 0 and 1). Try to use only one assertion per method. Use JUnit's @TempDir for temporary
> storage. make sure to test many edge cases like empty arrays or strings. the generated
> test classes must end with "Test(ModelName)_P2".

### GPT-OSS:20b
This time, it took 21 minutes and 45 seconds to generate output.
#### MyMath Unit
```java
class MyMathTestGPTOSS_P2 {

  private final MyMath mm = new MyMath();

  @Test
  void checkSign_positive() {
    assertEquals("positive", mm.checkSign(1));
  }

  @Test
  void checkSign_negative() {
    assertEquals("negative", mm.checkSign(-1));
  }

  @Test
  void checkSign_zeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void reverseNumber_positive() {
    assertEquals(-5, mm.reverseNumber(5));
  }

  @Test
  void reverseNumber_negative() {
    assertEquals(5, mm.reverseNumber(-5));
  }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.  
This matches the output generated by Continue.dev.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive
- a normal negative
- zero (at the boundary)

The tests have MC/DC.  
Continue.dev did better since it tested boundary values instead of normal values.

#### MyAdvancedMath Unit
```java
class MyAdvancedMathTestGPTOSS_P2 {

  private final MyAdvancedMath math = new MyAdvancedMath();

  @Test
  void add_positiveNumbers() {
    assertEquals(5, math.add(2,3));
  }

  @Test
  void add_negativeInputThrows() {
    assertThrows(IllegalArgumentException.class, () -> math.add(-1,5));
  }

  @Test
  void add_overflowThrows() {
    int big = Integer.MAX_VALUE - 1;
    assertThrows(IllegalArgumentException.class, () -> math.add(big,2));
  }

  @Test
  void powerOfTwo_zero() {
    assertEquals(1, math.powerOfTwo(0));
  }

  @Test
  void powerOfTwo_maxBoundary() {
    assertEquals(1073741824, math.powerOfTwo(30)); // 2^30
  }

  @Test
  void powerOfTwo_negativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
  }

  @Test
  void powerOfTwo_overflowThrows() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
  }

  @Test
  void reverseArrayNumbers_empty() {
    int[] input = new int[]{};
    int[] result = math.reverseArrayNumbers(input, new MyMath());
    assertArrayEquals(new int[]{}, result);
  }

  @Test
  void reverseArrayNumbers_normal() {
    int[] input = new int[]{-1,0,5};
    int[] result = math.reverseArrayNumbers(input, new MyMath());
    assertArrayEquals(new int[]{1,0,-5}, result);
  }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- y is -1 and x is positive
- overflow test

We have decision coverage since x is never negative.
These are also the same tests Continue.dev generated.

##### *powerOfTwo(int)* method
Four methods are generated. The tests include:
- just below the lower boundary
- the lower boundary
- the upper boundary
- just above the upper boundary

MC/DC attained.  
Continue.dev additionally covered just above the lower boundary.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated which include:
- empty array test
- a normal test

MC/DC is achieved.  
These are equivalence to Continue.dev tests.

#### FileIO Unit
```java
class FileIOTestGPTOSS_P2 {

  private final FileIO fileIO = new FileIO();

  @Test
  void readFile_emptyFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);
    assertArrayEquals(new String[]{}, fileIO.readFile(file.toString()));
  }

  @Test
  void readFile_multipleLines(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("lines.txt");
    Files.write(file, java.util.List.of("one", "two", "three"));
    assertArrayEquals(new String[]{"one","two","three"}, fileIO.readFile(file.toString()));
  }
}
```
Two methods were generated that include:
- an empty file
- a file with multiple lines

Non-existing test case is not covered, resulting in no statement coverage.  
Continue.dev also covered single line file test case.

#### NameSearch Unit
```java
class NameSearchTestGPTOSS_P2 {

  private final NameSearch ns = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  void findNamesInFileThatStartWith_match() throws IOException {
    @TempDir Path tempDir = java.nio.file.Files.createTempDirectory("names");
    Path file = tempDir.resolve("names.txt");
    Files.write(file, java.util.List.of("alice","bob","alex","carol"));
    String[] result = ns.findNamesInFileThatStartWith(file.toString(), fileIO, "al");
    assertArrayEquals(new String[]{"alice","alex"}, result);
  }

  @Test
  void findNamesInFileThatStartWith_noMatch() throws IOException {
    @TempDir Path tempDir = java.nio.file.Files.createTempDirectory("names");
    Path file = tempDir.resolve("names.txt");
    Files.write(file, java.util.List.of("bob","carol"));
    String[] result = ns.findNamesInFileThatStartWith(file.toString(), fileIO, "al");
    assertArrayEquals(new String[]{}, result);
  }
}
```
Two methods with multiple are generated which include:
- a test without result.
- a test with multiple results

The test achieves MC/DC.  
Continue.dev included a test with a single result and an empty file test.

#### Comparison
The differences are more now. Continue.dev did much better:
- OpenCode used JUnit's temporary storage
- Continue.dev performed better at covering edge cases and boundary values
- Continue.dev covered 4 more cases.


### Gemma4:e4b
Thankfully, I did not run into any issue unlike previous time.
It took 8 minutes and 40 seconds to generate the codes.

#### MyMath Unit
```java
public class MyMathTestGemma4_P2 {

  MyMath myMath;

  @BeforeEach
  void setUp() {
    myMath = new MyMath();
  }

  @Test
  void testCheckSignPositive() {
    String result = myMath.checkSign(1);
    assertEquals("positive", result);
  }

  @Test
  void testCheckSignNegative() {
    String result = myMath.checkSign(-1);
    assertEquals("negative", result);
  }

  @Test
  void testCheckSignBoundary_Positive() {
    String result = myMath.checkSign(Integer.MAX_VALUE);
    assertEquals("positive", result);
  }

  @Test
  void testCheckSignBoundary_Negative() {
    String result = myMath.checkSign(Integer.MIN_VALUE);
    assertEquals("negative", result);
  }

  @Test
  void testCheckSignBoundary_Zero() {
    assertThrows(IllegalArgumentException.class, () -> {
      myMath.checkSign(0);
    }, "Expected IllegalArgumentException for number zero");
  }

  @Test
  void testReverseNumberPositive() {
    int result = myMath.reverseNumber(5);
    assertEquals(-5, result);
  }

  @Test
  void testReverseNumberNegative() {
    int result = myMath.reverseNumber(-5);
    assertEquals(5, result);
  }

  @Test
  void testReverseNumberZero() {
    int result = myMath.reverseNumber(0);
    assertEquals(0, result);
  }
}
```

##### *checkSign(int)* method
Five methods are generated that include:
- just below the boundary
- zero (boundary)
- just above the boundary
- Integer.MAX_VALUE
- Integer.MIN_VALUE

The tests have MC/DC.  
This matches the output generated by Continue.dev.

##### *reverseNumber(int)* method
Three methods are generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
It performed worst. It did not cover the Integer.MIN_VALUE edge case unlike
Continue.dev. Also Continue had a just above the boundary test.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestGemma4_P2 {

    private MyMath myMath;
    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
        myAdvancedMath = new MyAdvancedMath();
    }

    // --- Tests for add(int x, int y) ---

    @Test
    void add_TwoPositiveNumbers() {
        assertEquals(10, myAdvancedMath.add(3, 7));
    }

    @Test
    void add_BoundaryOneAndOne() {
        assertEquals(2, myAdvancedMath.add(1, 1));
    }

    @Test
    void add_BoundaryTwoAndZero() {
        // Although the method checks for positivity, we test the boundary relative to zero inputs if allowed,
        // but since the constraint is positive, we test small positive numbers near zero.
        assertEquals(2, myAdvancedMath.add(1, 1)); // Re-using 1,1 as small positive test
    }

    @Test
    void add_OneNumberNearMax() {
        int x = Integer.MAX_VALUE - 1;
        assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(x, 1));
    }

    @Test
    void add_NegativeInputX() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_NegativeInputY() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_OverflowCondition() {
        // Test case that causes overflow: x + y > Integer.MAX_VALUE
        int x = Integer.MAX_VALUE / 2 + 1;
        int y = Integer.MAX_VALUE / 2 + 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y);
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @Test
    void powerOfTwo_ZeroPower() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_PowerOfOne() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_PowerOfTwo() {
        assertEquals(4, myAdvancedMath.powerOfTwo(2));
    }

    @Test
    void powerOfTwo_PowerOfThree() {
        assertEquals(8, myAdvancedMath.powerOfTwo(3));
    }

    @Test
    void powerOfTwo_BoundaryMaxPower() {
        // Testing the highest allowed power (30)
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_NegativePower() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void powerOfTwo_AboveMaxPower() {
        // Testing power 31
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    void reverseArrayNumbers_NormalCase() {
        int[] input = {10, 20, 30};
        int[] expected = {-10, -20, -30};

        myAdvancedMath.reverseArrayNumbers(input, myMath);

        assertArrayEquals(expected, input);
    }

    @Test
    void reverseArrayNumbers_WithNegativeNumbers() {
        int[] input = {-5, 10, -15};
        int[] expected = {5, -10, 15};

        myAdvancedMath.reverseArrayNumbers(input, myMath);

        assertArrayEquals(expected, input);
    }

    @Test
    void reverseArrayNumbers_EmptyArray() {
        int[] input = {};
        // Should return the array unchanged
        myAdvancedMath.reverseArrayNumbers(input, myMath);

        assertArrayEquals(new int[]{}, input);
    }
}
```
##### *add(int, int)* method
Four methods were generated that include:
- two tests of positive x and y
- x is negative and y is positive
- overflow test

y is never negative, therefore there is condition coverage.
An additional and redundant normal test was generated, but Continue.dev also
did generate a redundant test which doesn't exist here.
Continue.dev tested negativity of both x and y while OpenCode only tested negativity
of x. Continue also had a near overflow test. The tested numbers are quite different
between them.  
Continue.dev demonstrated significantly better performance in coverage and depth here.

##### *powerOfTwo(int)* method
Five methods are generated. The tests include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- the upper boundary
- just above the upper boundary

We have MC/DC.  
Continue generated two normal tests while OpenCode generated none.
Since we are checking boundary values, we don't necessarily need to test
normal values.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated. they include:
- a normal test
- a single element array test
- an empty array

MC/DC is achieved.
OpenCode did better since it has a single element array test, and it doesn't have a redundant test.

#### FileIO Unit
```java
public class FileIOTestGemma4_P2 {

  FileIO fileIO;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    fileIO = new FileIO();
  }

  @Test
  void testReadFileNormalContent() throws IOException {
    // A. Setup a temporary file with multiple lines
    Path filePath = tempDir.resolve("test_multi.txt");
    String content = "Line One\nLine Two\nLast Line";
    Files.writeString(filePath, content);

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert
    assertEquals(3, result.length);
    assertArrayEquals(new String[]{"Line One", "Line Two", "Last Line"}, result);
  }

  @Test
  void testReadFileEmptyFile() throws IOException {
    // A. Setup a temporary empty file
    Path filePath = tempDir.resolve("empty.txt");
    Files.createFile(filePath);

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert
    assertEquals(0, result.length);
  }

  @Test
  void testReadFileSingleLine() throws IOException {
    // A. Setup a temporary file with one line
    Path filePath = tempDir.resolve("single.txt");
    Files.writeString(filePath, "Only One Line");

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert
    assertEquals(1, result.length);
    assertArrayEquals(new String[]{"Only One Line"}, result);
  }

  @Test
  void testReadFileNonExistingFile() {
    // The file does not exist, expecting the content reading to fail gracefully (via exception catching or specific return value, if possible).
    // Since the original method relies on e.printStackTrace() on IOException, we test that it doesn't crash and returns empty array.
    String nonExistentPath = tempDir.resolve("does_not_exist.txt").toString();

    String[] result = fileIO.readFile(nonExistentPath);

    // Asserting that it returns an empty array in case of failure/exception
    assertEquals(0, result.length);
  }

  @Test
  void testReadFileWhitespaceOnlyFile() throws IOException {
    // A. Setup a temporary file with only whitespace
    Path filePath = tempDir.resolve("whitespace.txt");
    Files.writeString(filePath, "\n  \t\n");

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert (readLine() handles empty lines, so we expect several lines containing whitespace)
    // Note: Because readLine() treats the first line as the content and subsequent empty lines will be null, 
    // and the buffer reader reads until null, we check if the structure is preserved.
    assertTrue(result.length > 0);
  }
}
```
Five methods are generated that include:
- a normal test
- a single line file test
- an empty file test
- a non-existing file
- only whitespace file test

MC/DC is achieved.  
OpenCode generated *a single line file test* and *only whitespace file test* additionally.

#### NameSearch Unit
```java
public class NameSearchTestGemma4_P2 {

  NameSearch nameSearch;
  FileIO fileIO;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    nameSearch = new NameSearch();
    fileIO = new FileIO(); // Initialize FileIO for use in the test
  }

  @Test
  void testFindNamesStartWithNormal() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("names.txt");
    String content = "Apple\nBanana\nApricot\nGrape";
    Files.writeString(filePath, content);

    // Execute
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Ap");

    // Assert
    assertEquals(2, results.length);
    assertArrayEquals(new String[]{"Apple", "Apricot"}, results);
  }

  @Test
  void testFindNamesStartWithAllMatches() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("all_names.txt");
    String content = "Cat\nCar\nCoat";
    Files.writeString(filePath, content);

    // Execute
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "C");

    // Assert
    assertEquals(3, results.length);
    assertArrayEquals(new String[]{"Cat", "Car", "Coat"}, results);
  }

  @Test
  void testFindNamesStartWithPrefixNonExistent() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("names.txt");
    String content = "Apple\nBanana\nApricot\nGrape";
    Files.writeString(filePath, content);

    // Execute
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "XYZ");

    // Assert
    assertEquals(0, results.length);
    assertArrayEquals(new String[0], results);
  }

  @Test
  void testFindNamesWithEmptyPrefix() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("names.txt");
    String content = "Apple\nBanana\nApricot";
    Files.writeString(filePath, content);

    // Execute: Empty prefix should match all lines
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");

    // Assert
    assertEquals(3, results.length);
    assertArrayEquals(new String[]{"Apple", "Banana", "Apricot"}, results);
  }

  @Test
  void testFindNamesInEmptyFile() {
    // Setup: Create an empty file
    Path emptyFilePath = tempDir.resolve("empty.txt");
    try {
      Files.createFile(emptyFilePath);
    } catch (IOException e) {
      // Ignore cleanup errors for testing structure
    }

    // Execute
    // We need to handle the fact that fileIO.readFile might fail/read nothing on a non-existent/empty file
    // Based on FileIO, it reads contents, so an empty file *is* read as an array of size 0.
    String[] results = nameSearch.findNamesInFileThatStartWith(emptyFilePath.toString(), fileIO, "A");

    // Assert
    assertEquals(0, results.length);
    assertArrayEquals(new String[0], results);
  }
}
```
Five methods are generated which include:
- a normal test
- an all match test
- a test without result
- an empty file test
- a test with empty prefix

The test achieves MC/DC.  
OpenCode generated a redundant all match test compared to Continue.dev. This has
been tested implicitly in empty prefix test! and also it's not necessary.


#### Comparison
The differences are getting more. With a more complex prompt, more variables will exist
that would make the AI output vary more:
- Continue.dev demonstrated superior coverage of boundary values.
- OpenCode did better at covering edge cases.

## Round 3
This is the final round. Let's run it back again for the last time.
> For these units, generate unit test using JUnit5. do not use mock for dependencies.
> make sure to have a robust boundary values testing (test the boundary itself, just above
> it and just below it). Try to use only one assertion per method. Use JUnit's @TempDir
> for temporary storage. make sure to test many edge cases like empty arrays or strings.
> achieve MC/DC coverage. use parametrized tests for normal values. avoid generating redundant
> tests.  the generated test classes must end with "Test(modelName)_P3".


### GPT-OSS:20b
It took 23 minutes and 53 seconds to generate output.

#### MyMath Unit
```java
class MyMathTestGPTOSS_P3 {

  private final MyMath myMath = new MyMath();

  @ParameterizedTest
  @ValueSource(ints = {1, 42, Integer.MAX_VALUE})
  void checkSign_withPositiveNumbers_returnsPositive(int number) {
    assertEquals("positive", myMath.checkSign(number));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -42, Integer.MIN_VALUE})
  void checkSign_withNegativeNumbers_returnsNegative(int number) {
    assertEquals("negative", myMath.checkSign(number));
  }

  @Test
  void checkSign_withZero_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, -1, 0, Integer.MAX_VALUE, Integer.MIN_VALUE})
  void reverseNumber_returnsOppositeSign(int number) {
    assertEquals(-number, myMath.reverseNumber(number));
  }
}
```
##### *checkSign(int)* method
Three methods are generated. Two of which are parametrized. They include:
- a normal positive
- a normal negative
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.  
These generated tests are much better compared to Continue.dev.
Parametrized tests are used and boundary values are checked.

##### *reverseNumber(int)* method
a single method is generated. It is parametrized and it includes:
- just above the boundary
- just below the boundary
- zero (at the boundary)
- Integer.MIN edge case
- Integer.MAX

The tests have MC/DC.  
the Integer.MIN_VALUE edge case is present unlike Continue.dev.  
Parametrized test is also used. Integer.MAX is a redundant test.

#### MyAdvancedMath Unit
```java
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
```
##### *add(int, int)* method
Three methods were generated that include:
- a parametrized test for both non-negatives (includes both zeros)
- x is negative and y is positive
- overflow test

Like Continue.dev, there is no MC/DC since y is never negative.  
Both zeros is a new test, also the both (Integer.MAX_VALUE / 2).

##### *powerOfTwo(int)* method
Four methods are generated that include:
- a valid parametrized test
  - the lower boundary
  - just above the boundary
  - a normal value
  - just below the upper boundary
- just below the lower boundary
- the upper boundary
- the lower boundary (duplicate)

We have MC/DC.  
OpenCode additionally covered the upper boundary in its parametrized test.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated which include:
- empty array test
- a normal test

MC/DC is achieved.  
OpenCode used parametrized test with a single array for this method, completely absurd.
Continue.dev at least had covered an empty array test case.

#### FileIO Unit
```java
class FileIOTestGPTOSS_P3 {

  private final FileIO io = new FileIO();

  @TempDir
  File tempDir;

  @Test
  void readFile_withEmptyFile_returnsEmptyArray() throws IOException {
    File file = new File(tempDir, "empty.txt");
    file.createNewFile();
    assertNotNull(io.readFile(file.getAbsolutePath()));
    assertEquals(0, io.readFile(file.getAbsolutePath()).length);
  }

  @Test
  void readFile_withMultipleLines_returnsArrayWithLines() throws IOException {
    File file = new File(tempDir, "lines.txt");
    try (var writer = new FileWriter(file)) {
      writer.write("first\nsecond\nthird");
    }
    String[] result = io.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{"first", "second", "third"}, result);
  }

  @Test
  void readFile_withNonExistentFile_returnsEmptyArray() {
    // The method does not throw on non-existent but returns empty array because exception is caught
    String[] result = io.readFile("nonexistent.file");
    assertNotNull(result);
  }
}
```
Three methods were generated that include:
- an empty file
- a non-existing file
- a file with multiple lines

MC/DC attained.
Continue had covered a single line file test while OpenCode covered non-existing file test.

#### NameSearch Unit
```java
class NameSearchTestGPTOSS_P3 {

  private final NameSearch ns = new NameSearch();
  private final FileIO io = new FileIO();

  @TempDir
  File tempDir;

  @Test
  void findNamesInFileThatStartWith_noMatches_returnsEmptyArray() throws IOException {
    File file = new File(tempDir, "names.txt");
    try (var w = new FileWriter(file)) {
      w.write("alice\nbob\ncarl");
    }
    String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "z");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void findNamesInFileThatStartWith_multipleMatches_returnsArray() throws IOException {
    File file = new File(tempDir, "names.txt");
    try (var w = new FileWriter(file)) {
      w.write("charlie\ncharles\nalice\ncharles");
    }
    String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "char");
    assertArrayEquals(new String[]{"charlie", "charles", "charles"}, result);
  }

  @Test
  void findNamesInFileThatStartWith_emptyFile_returnsEmptyArray() throws IOException {
    File file = new File(tempDir, "empty.txt");
    file.createNewFile();
    String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "a");
    assertEquals(0, result.length);
  }
}
```
Four methods with multiple are generated which include:
- a test without result.
- a test with an empty file
- a test with multiple result

The test achieves MC/DC.  
Continue.dev had a single result test while OpenCode generated an
empty file test.


#### Comparison
The differences are more obvious now:
- OpenCode demonstrated superior use of parameterized tests, leading to better boundary value coverage.
- Continue.dev had one more test for edge cases than OpenCode.

Overall, OpenCode did better here. more than slightly.


### Gemma4:e4b
Unfortunately, I ran into issues with this model. the models stopped generating twice, and I had to prompt it
to continue. All three prompts took 10 minutes and 45 seconds in total.

#### MyMath Unit
There is a syntax error. there is no parenthesis around the first CSV source.
```java
public class MyMathTestGemma4_P3 {

  private final MyMath myMath = new MyMath();

  // --- Tests for checkSign(int number) ---

  @ParameterizedTest(name = "checkSign({0}) should return {1}")
  @CsvSource({
          "1, 'positive'",
          ("Integer.MAX_VALUE", "positive"), // Boundary check
          ("Integer.MAX_VALUE - 1", "positive"),
          ("-1", "negative"),
          ("Integer.MIN_VALUE", "negative"), // Boundary check
          ("Integer.MIN_VALUE + 1", "negative")
  })
  void checkSign_PositiveOrNegative(int number, String expected) {
    assertEquals(expected, myMath.checkSign(number));
  }
  
  @ParameterizedTest(name = "checkSign({0}) should throw IllegalArgumentException")
  @CsvSource({"0"})
  void checkSign_ThrowsExceptionForZero(int zero) {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      myMath.checkSign(zero);
    });
    assertEquals("Number had no sign", exception.getMessage());
  }
  
  // --- Tests for reverseNumber(int number) ---
  
  @ParameterizedTest(name = "reverseNumber({0}) should return {1}")
  @CsvSource({
          ("123", -123),
          ("-456", 456),
          ("0", 0)
          })
  void reverseNumber_BasicScenarios(int input, int expected) {
    assertEquals(expected, myMath.reverseNumber(input));
  }
  
  @Test
  void reverseNumber_IntegerRangeBoundaries() {
    // Testing max and min values for safety, although standard int arithmetic handles it.
    // The implementation simply negates the number.
    assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
  }
}
```

##### *checkSign(int)* method
Two methods are generated that include:
- a mixed parametrized test
  - just above the boundary
  - Integer.MAX_VALUE
  - Integer.MAX_VALUE - 1
  - just below the boundary
  - Integer.MIN_VALUE
  - Integer.MIN_VALUE -1
- a parametrized test that includes just a zero?!

- a positive values parametrized test
    - a normal positive
    - just above the boundary
- a negative values parametrized test
    - a normal negative
    - just below the boundary
- a test for 0


The tests have MC/DC.  
Continue generated ideal tests, but OpenCode made two
mistakes. firstly, it mixed the parametrized test by also passing 
the result. secondly, it made a parametrized test with a single value!
Also, all the extreme value tests are redundant.

##### *reverseNumber(int)* method
Two methods are generated that include:
- a mixed parametrized test
  - a normal positive
  - a normal negative
  - zero
- an extreme values parametrized test
  - Integer.MIN_VALUE
  - Integer.MAX_VALUE

Model made a foolish mistake. In the extreme values, it did not
realize that MAX and MIN values are not the reverse of each other!
Continue did not use parametrized test, but it did generate the test for MIN value correctly.


#### MyAdvancedMath Unit
```java
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
```
##### *add(int, int)* method
Three methods are generated that include:
- a parametrized  both normal positives test
- a negative x and positive y test
- an overflow test

The third values in parametrized test will cause overflow exception!  
MC/DC achieved.  
Continue.dev did better since it has MC/DC. This model really likes to assert the exception messages
rather than its type.

##### *powerOfTwo(int)* method
Three methods are generated that include:
- a normal parametrized test:
  - the lower boundary
  - just above the boundary
  - a normal value
  - the upper boundary
- just below the lower boundary
- just above the upper boundary

Continue.dev and OpenCode generated tests for this method are equivalent.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated. they include:
- all positive test
- a mixed test
- an empty array

MC/DC is achieved.  
Continue.dev had an all zeros test and OpenCode has an all positive test. both could be redundant.

#### FileIO Unit
```java
public class FileIO_TestGemma4_P3 {

  private FileIO fileIO;

  @BeforeEach
  void setUp() {
    fileIO = new FileIO();
  }

  /**
   * Reads a file with standard content and verifies content retrieval.
   */
  @Test
  void readFile_StandardContent(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("test_file.txt");
    String content = "First line\nSecond line\nThird line";

    // Write content to the temporary file
    Files.write(filePath, content.getBytes());

    String[] expectedArray = {"First line", "Second line", "Third line"};
    String[] actualArray = fileIO.readFile(filePath.toString());

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Reads an empty file, verifying an empty array is returned.
   */
  @Test
  void readFile_EmptyFile(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("empty_file.txt");
    // Create an empty file
    Files.write(filePath, new byte[0]);

    String[] expectedArray = new String[0];
    String[] actualArray = fileIO.readFile(filePath.toString());

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Reads a file containing boundary/edge case lines (e.g., empty line).
   */
  @Test
  void readFile_EdgeCaseLines(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("edge_case_file.txt");
    // Line 1: Good content, Line 2: Empty line, Line 3: Only spaces
    String content = "Name A\n\n   \nName B";

    // Write content to the temporary file
    Files.write(filePath, content.getBytes());

    // BufferedReader reads the empty line as a single empty string element
    String[] expectedArray = {"Name A", "", "   ", "Name B"};
    String[] actualArray = fileIO.readFile(filePath.toString());

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Tests reading a file that does not exist, verifying exception handling does not crash the test.
   */
  @Test
  void readFile_FileNotFound(@TempDir Path tempDir) {
    // Use a Path that definitely does not exist in the temp directory
    Path nonExistentFile = tempDir.resolve("non_existent.txt");

    // The method catches IOException and prints the stack trace but returns an array.
    // We primarily test that it does not throw a runtime exception.
    assertDoesNotThrow(() -> {
      String[] actualArray = fileIO.readFile(nonExistentFile.toString());
      // When failure occurs, it returns an empty array of strings
      assertTrue(actualArray.length == 0);
    });
  }
}
```
Four methods are generated that include:
- a normal test
- an empty file test
- a non-existing file
- a file with empty line in between its content test

MC/DC is achieved.  
Continue had a single line and only empty line test, while OpenCode has a file with empty line in between
its content test. So Continue covered one edge case more than OpenCode.

#### NameSearch Unit
```java
public class NameSearchTestGemma4_P3 {

  private NameSearch nameSearch;

  // Note: Since NameSearch depends on FileIO, and FileIO relies on the actual file system,
  // we will mock the FileIO behavior using @TempDir to simulate successful file reads.

  @BeforeEach
  void setUp() {
    nameSearch = new NameSearch();
  }

  /**
   * Test case for finding names starting with a specific prefix in a well-formed file.
   */
  @Test
  void findNamesInFileThatStartWith_FoundNames(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("names_file.txt");
    String content = "Alice Smith\nBob Johnson\nAlex Brown\nAndrew Taylor";
    Files.write(filePath, content.getBytes());

    // We use the FileIO class we tested previously, assuming a successful read operation.
    FileIO fileIO = new FileIO();

    String prefix = "A";
    String[] expectedArray = {"Alice Smith", "Alex Brown", "Andrew Taylor"};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Test case for finding no names matching the prefix.
   */
  @Test
  void findNamesInFileThatStartWith_NoMatches(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("names_file_2.txt");
    String content = "Alpha\nBeta\nGamma";
    Files.write(filePath, content.getBytes());

    FileIO fileIO = new FileIO();

    String prefix = "X";
    String[] expectedArray = {};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Test case for an empty file, simulating zero names.
   */
  @Test
  void findNamesInFileThatStartWith_EmptyFile(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("empty_names_file.txt");
    Files.write(filePath, new byte[0]);

    FileIO fileIO = new FileIO();

    String prefix = "N/A";
    String[] expectedArray = {};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Test case to check when the name list only contains names that match the prefix.
   */
  @Test
  void findNamesInFileThatStartWith_AllMatch(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("all_names_file.txt");
    String content = "apple\napricot\napple pie";
    Files.write(filePath, content.getBytes());

    FileIO fileIO = new FileIO();

    String prefix = "app";
    String[] expectedArray = {"apple", "apricot", "apple pie"};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }
}
```
Last time, the model did very poorly with this method.  
Four methods are generated that include:
- a normal test
- a test without result
- an empty file test
- all matching test

The test achieves MC/DC.  
Compared to Continue, the all matching test is added, but it's not really important, I believe.


#### Comparison
Continue.dev did have MC/DC over all methods, but OpenCode doesn't.  
Overall, Continue.dev did slightly better.


## Final Conclusion
I have no idea why it took OpenCode much longer to generate the code than Continue.dev.
This was quite a difference.  
In the first prompt, there were minor differences, but with the more
complex prompt, these differences grew. In some methods, there were major differences,
but I won't use major difference for overall comparison. Perhaps this won't be the case
for the next projects which would be more complicated than this. The Excel files
comparing Continue.dev and OpenCode generated tests are provided.