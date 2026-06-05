# Experiment with GitHub Copilot in Unit Test Generation
## GitHub Copilot Introduction
GitHub Copilot is a code generator, completion and programming AI-assistance
developed and maintained by GitHub (Owned by Microsoft). It can be installed
on many integrated development environments (IDE) such as Visual Studio Code (VS Code),
Visual Studio, Eclipse, JetBrains products, etc. It was released in October 2021.
It is not a model, instead it allows the user to choose a model to generate code
with. as of now, users can choose many models from different companies.
This list shows some available  models from different companies as
of the time this markdown is being written:
- Anthropic:
  - Claude Haiku 4.5
  - Claude Sonnet 4.6
  - Claude Opus 4.6
- Google:
  - Gemini 3 Flash
  - Gemini 3.1 Pro
- OpenAI:
  - GPT-5.1-Codex-Mini
  - GPT-5.3-Codex
- xAI:
  - Grok Code Fast 1

GitHub Copilot has three available plans for everyone. The free plan limits the
user to basic models and very limited code completion. There is also a special
student plan that lets students use GitHub Copilot for free with a generous
limit. It's 300 tokens that reset at the beginning of the month. every chat will
use 1 token multiplied by the selected model token consumption modifier. for example,
GPT-5.1-Codex-Mini modifier is 0.33x while Claude Opus 4.6 modifier is 3x.

### How to install on Visual Studio Code
1. Install Visual Studio Code 2022 17.6 or later
2. Create a GitHub account if you have not already.
3. Install **GitHub Copilot Chat** Extension. Note that **GitHub Copilot** Extension
    is mostly focused on code completion, while the other provides a conventional
    chatbot interface.

Now You should be ready to go.

### How to use GitHub Copilot
A chat icon must have appeared on top bar of Visual Studio Code. clicking on it will open
a chat tab. here you can type your prompt, access your previous chat, choose model,
choose agent type and choose the agent session.

In the prompt field, you can attach some of the files available in the currently open
folder, they act as adding context. although it can read other files for context if needed.

You can choose your model here. It's worth to note that models have different context windows.

There are three agents you can choose:
1. Agent: It will edit files in your workspace in agent mode.
2. Ask: It will ask or edit in context.
3. Plan: It will research and outline multistep plans.

There are four agent sessions you can choose:
1. Local: Run tasks within VS Code chat. The agent iterates via chat and works interactively to
   implement changes in your main workspace.
2. Background: Delegate tasks to a background agent running locally on your machine. The agent
   iterates via chat and works asynchronously in a Git worktree to implement changes
   isolated from your main workspace using the GitHub Copilot CLI.
3. Cloud: Delegate tasks to the GitHub Copilot coding agent. The agent iterates via chat and
   works asynchronously in the cloud to implement changes and pull requests as needed.
4. Claude: Delegate tasks to the Claude Agent SDK using the Claude models included in your
   GitHub Copilot subscription. The agent iterates via chat and works interactively to
   implement changes in your main workspace.

After you have written down your prompt, and you have set up the options, just click the
send button. In the chat, the agent will show its current status like whether it is 
understanding the provided context, is generating code, etc.


## Generating Unit Tests with GitHub Copilot
Now we can generate unit tests. Since it's not a model, and you choose the model and
models differ by how large they are, what company made them, and how many tokens
they consume, I will try three different models with three different
token modifiers. This includes:
- OpenAI GPT-5.1-Codex-Mini (0.33x)
- Google Gemini 3.1 Pro (1x)
- Anthropic Claude Opus 4.6 (3x)

It's worth noting that none of the models above have an official parameter
count.

I will also give different prompts for each model to also see how much prompt engineering
can affect the generated tests.

### Generating Unit tests with OpenAI GPT-5.1-Codex-Mini
Let's begin with the weakest model, OpenAI GPT-5.1-Codex-Mini. I added all 4 files as
context and gave this very basic prompt:
> Generate unit tests for provided files.

It is a very simple prompt as I said. I just provided the main Idea. I didn't even say
where to store the tests, what tools to use and how complex the tests should be.
before sending the prompt, I moved the previous tests that existed in the repository
so the model won't get a reference from them or override them.
The model started by reading all four files, then it created io, math and search folder
exactly where the project itself tests were.

I added GPT to the end of files and classes name myself.
let's take a look at the generated files. let's start with the simplest one, "MyMathTestGPT":
note that I have removed package and imports from all files throughout the report.
#### MyMathTest Unit
```java
class MyMathTestGPT {

    private final MyMath math = new MyMath();

    @Test
    void checkSign_positiveNumber_returnsPositive() {
        assertEquals("positive", math.checkSign(7));
    }

    @Test
    void checkSign_negativeNumber_returnsNegative() {
        assertEquals("negative", math.checkSign(-12));
    }

    @Test
    void checkSign_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void reverseNumber_flipsSign() {
        assertEquals(-42, math.reverseNumber(42));
    }
}
```
the names of tests reveal what scenario and output we are dealing with. one problem is that
it made a mistake by not adding test to the beginning of methods names; as far as I know, that is a convention.
also for Java method naming conventions, camelCase is often used. the state of naming is the
same across all generated unit tests.

##### *checkSign(int)* method
it has covered all three possible outcomes. 
positive number returns "positive", negative number returns "negative" and zero throws an exception.
it has statement, decision, condition, condition/decision and MC/DC coverage.
it is a very simple method and all decision points only have one condition, so
it was going to be easy. I personally would have added more tests for boundary values
checking with 1, -1, Integer.MAX_VALUE and Integer.MIN_VALUE. all in all,
it's a simple method and the tests are good. compared to already existing tests,
we are even. the already existing tests also cover all three scenarios in three tests.

##### *reverseNumber(int)* method
this method, it just one simple line of code with no decision point.
one single test covered all the said coverages. I personally would have added a check
for a minus number and zero too to cover more scenarios. boundary values checking
would have been good to.  
adding more tests does increase the robustness, but more tests will result in
harder management, maintenance and more time testing. compared to already existing tests,
it did worse. while GPT only covered a positive number, the existing tests covered 
positive, negative and zero in three tests.
#### MyAdvancedMathTest Unit
This is a more complex unit containing 3 methods with loop, multi-condition decision.
Here is the generated file:
```java
class MyAdvancedMathTestGPT {

    private final MyAdvancedMath advancedMath = new MyAdvancedMath();

    @Test
    void add_positiveNumbers_returnsSum() {
        assertEquals(9, advancedMath.add(4, 5));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    }

    @Test
    void add_overflow_throwsIllegalArgumentException() {
        int nearLimit = Integer.MAX_VALUE;
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(nearLimit, 1));
    }

    @Test
    void powerOfTwo_validInputs_returnExpectedResults() {
        assertEquals(1, advancedMath.powerOfTwo(0));
        assertEquals(1024, advancedMath.powerOfTwo(10));
    }

    @Test
    void powerOfTwo_tooLarge_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void powerOfTwo_negative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-3));
    }

    @Test
    void reverseArrayNumbers_flipsEachValue() {
        int[] values = {1, -2, 3};
        int[] copy = values.clone();

        int[] result = advancedMath.reverseArrayNumbers(copy, new MyMath());

        assertArrayEquals(new int[]{-1, 2, -3}, result);
        assertArrayEquals(result, copy);
    }
}
```
##### *add(int, int)* method
we have 3 tests for this method.
a normal test, an overflow test and a negative input test.
all lines of codes are executed (statement coverage).
tests also have condition coverage, but they do not have decision coverage since
in the tests we never assign a negative number to *y*. If this project was a more critical
one in which a fault can result in severe or catastrophic damage, these tests would have
not been enough. for a simple add method, these tests are OK.
I believe a 0 + 0 and an Integer.MAX_VALUE + 0 should have been generated.
compared to already
existing tests, those have one more test covering a 0 + 0 and a more sophisticated checks
for exceptions using **@Rule**. with that, you can not only check whether the exception
class matches, also you can check whether the exception message matches the message
you expect. for the next models, I prompt the agents to use **@Rule** for more
sophisticated matching, including exception messages as well. Although I figured that
it is not really important to check the exception message in Unit testing.

##### *powerOfTwo(int)* method
4 tests were generated in three methods.  
the method containing two tests handles the power 0 edge case and a normal test.  
the two other tests will result in IllegalArgumentException; one is a negative power
 and the other one is a result that cannot fit in an Integer.  
the tests have MC/DC coverage.  
It is missing boundary checking like 1, 29 and 30.  
compared to the existing tests, those only used a
parametrized test, although they only covered three normal tests and did not handle
the possible exceptions. I will prompt the next models to use parametrized tests for
this method.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one test with three elements generated which covered positive and negative values.  
It's missing a zero test.  
We assert the equality of result twice, once with the expected result, and second
with checking the passed array and returned array (since the function affects the reference array).
An empty array test must have been generated. this test has all
the mentioned coverage, because the function only has a simple loop.  
in the existing tests, there were two methods. One with normal parameters and another one with mocking MyMath.
We don't have anything to do with mocking which is integrated testing territories.  
in these experiments, although I prompt the next two models to use mocking. I should have asked
earlier whether I should include mocking or not. I won't review the tests that used mocking.

#### FileIOTest Unit
This unit works with files. let's take a look at the generated test file:
```java
class FileIOTestGPT {

    @Test
    void readFile_returnsLinesInSameOrder(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        List<String> lines = List.of("Alice", "Bob", "Charlie");
        Files.write(file, lines, StandardCharsets.UTF_8);

        FileIO fileIO = new FileIO();
        String[] result = fileIO.readFile(file.toString());

        assertArrayEquals(lines.toArray(new String[0]), result);
    }

    @Test
    void readFile_emptyFile_returnsEmptyArray(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        FileIO fileIO = new FileIO();
        String[] result = fileIO.readFile(file.toString());

        assertEquals(0, result.length);
    }
}
```
for the single method of this class, two tests were generated. It uses a temporary directory to store
the file in it. The first test is a simple scenario. the second test is an empty file.
we do not run into exception so we don't have statement coverage so we need a test for
a non-existing file path. it would have been better 
to have a test for white spaces. comparing to already existing test,
we did better, which they didn't use a temporary
directory, instead they used the stored file in *test/resources*. It only has one test while GPT
second test covers an empty file scenario.

#### NameSearchTest Unit
The final unit. let's see what GPT did:
```java
class NameSearchTestGPT {

    private final NameSearch search = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_returnsMatchingNames(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        List<String> names = List.of("Anna", "Bob", "Ansel", "Charlie");
        Files.write(file, names, StandardCharsets.UTF_8);

        String[] matches = search.findNamesInFileThatStartWith(file.toString(), fileIO, "An");

        assertArrayEquals(new String[]{"Anna", "Ansel"}, matches);
    }

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmpty(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, List.of("Bob", "Charlie"), StandardCharsets.UTF_8);

        String[] matches = search.findNamesInFileThatStartWith(file.toString(), fileIO, "Z");

        assertEquals(0, matches.length);
    }
}
```
Again, two tests for one method. one test is a normal scenario and the other test is 
an empty result. we do use *fileIO* here to read the file so we have a dependency, but
we don't focus on integrating it into this unit.  
I would say this was the worst tests generated for a unit. there are some edge cases here that GPT missed:
- empty file
- empty prefix
- null arguments

Compared to the already existing tests in the repository, GPT did better. it generated
one more test with an edge case, and it used a temporary directory.

#### Conclusion
With a weak model and a simple prompt. I would say the generated tests are OK. I can
assume even with this weak model, a good prompt would have made a big difference.
with the next model, which is better on the paper and with a more sophisticated prompt,
I am expecting to get a noticeable improvement for the generated tests.


### Generating Unit tests with Google Gemini 3.1 Pro
Let's move on to a better model. this model token consumption modifier
is 1x, 3 times more than the previous model. I am going to give
this model a more sophisticated prompt mostly adding the previous model
shortcomings, including boundary values checking. here is the prompt:

> create unit tests for the added files in the context.
> use @Rule for more sophisticated checking, including matching exception messages.
> for MyAdvancedMathTest.powerOfTwo, include a parametrized test.
> for reverseArrayNumbers, include mocking MyMath.
> for findNamesInFileThatStartWith, include mocking FileIO.
> include boundary values checks.
> use test suite if needed.
> add "Gemini" as suffix to all generated test classes.

As we can see, the prompt has much more details and instructions.
I added all the units as context. Here we are using @Rule for a better
exception testing which I now find it unnecessary.
we are also using a parametrized test. for two methods
that have a dependency, generated tests with mocking will be ignored.
The previous model did little for
boundary value checking, so I included that in the prompt as well.
I prompted the agent to use a test suite if needed. To make identifying
all generated tests, I also prompted the agent to add a *Gemini* suffix
so we can know these tests were generated by Gemini 3.1 Pro.  

Before sending the prompt, I moved all available tests to a temporary
location. I sent the message and the agent started working. It started
by reading the maven files, then it started generating the tests in the
standard directory. It took less than 2 minutes to generate all the tests.
the previous model did take longer. but I can blame it on my Internet connection
as the first prompt ran into connection error. It generated the tests using
**JUnit 4** and **Mockito** for mocking. let's take a look at the generated
unit tests. I will review them in the order of previous time. One problem
that I noticed is that the model did not use "Test" in the names, most
likely caused by me prompting it to add "Gemini" suffix. also method
names start with test now, GPT did not obey this convention.

#### MyMath Unit
```java
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
```
##### *checkSign(int)* method.  three tests were generated.
with a positive number, a negative number and zero. unfortunately,
it did not generate
boundary values checking like 1 and -1 even when I prompted it to do.
If we do a mutation test and the 
*( number > 0 )* mutates to *( number > 1 )*, tests would pass this.
It does have MC/DC coverage.

##### *reverseNumber(int)* method
One method with three assertions was generated. like previous method, there is no boundary checking,
especially Integer.MIN_VALUE that will cause an overflow exception.

Overall, generated tests lack on boundary values checking even though I included that in the prompt.

I figured out that @Rule is deprecated in JUnit 5.
here is used to check whether exception messages matches what is excepted,
in the next model, I will not use @Rule. (this was me not knowing
exception message checking is not important)

#### MyAdvancedMath Unit
Since I prompted the agent to use parametrized test for a function in this
class, we have two files:
```java
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
```

```java
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
```
More tests are generated. most notably, a parametrized test and a mocking test.

##### *add(int, int)* method
there are 4 tests. a normal test, two tests with a negative argument and
a test for overflow. a *0 + 0* test is missing in my opinion. We could possibly
have more tests covering boundary values, like *Integer.MAX_VALUE + 0*.

##### *powerOfTwo(int)* method
the agent generated 4 test methods, covering -1 and
31 as boundary values. It is better to have 0, 1 and 30 that are available
in the parametrized tests outside it as a separate function each for testing
the boundaries explicitly, and for parametrized test, just some normal
values are given.

##### reverseArrayNumbers(int[] array, MyMath mm)
mocking is used. we only have one test, causing
ignorance for many edge cases like empty array, null array, etc. even
the test does not include a zero for test. overall, generated
test for this method was terrible. Even with a sophisticated prompt and
being a stronger model, it did the same as GPT!

#### FileIO
```java
public class FileIOGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testReadFileValid() {
        FileIO fileIO = new FileIO();
        // Using the existing names_valid.txt in src/test/resources
        String filepath = "src/test/resources/names_valid.txt";
        String[] result = fileIO.readFile(filepath);
        Assert.assertNotNull(result);
    }

    @Test
    public void testReadFileInvalid() {
        FileIO fileIO = new FileIO();
        String filepath = "src/test/resources/does_not_exist.txt";
        String[] result = fileIO.readFile(filepath);
        Assert.assertEquals(0, result.length);
    }
}
```
For this class with the single method, two tests were generated.
It used the already existing *names_valid.txt* for one test and
for other the test, it used a file that doesn't exist. we do not
run into any exception, but it uses ExpectedException?
The first test only checks if the result is not null while
it should check if result array is equal to what we expect.

The second test passes a file path that doesn't exist. the method
will run into exception and will return an empty array. checking
the size of array with 0 is probably more optimized than asserting
equality of two empty arrays.

Many edge cases were not covered. The agent did poor for this unit.
This was also the case with GPT.

#### NameSearch
```java
public class NameSearchGemini {

    @Test
    public void testFindNamesInFileThatStartWith() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIOMock = mock(FileIO.class);
        
        String filepath = "dummy_path.txt";
        String[] mockNames = {"Alice", "Bob", "Charlie", "Alex", "David"};
        when(fileIOMock.readFile(filepath)).thenReturn(mockNames);

        String[] expected = {"Alice", "Alex"};
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIOMock, "Al");

        Assert.assertArrayEquals(expected, result);
        verify(fileIOMock, times(1)).readFile(filepath);
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatch() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIOMock = mock(FileIO.class);
        
        String filepath = "dummy_path.txt";
        String[] mockNames = {"Alice", "Bob", "Charlie", "Alex", "David"};
        when(fileIOMock.readFile(filepath)).thenReturn(mockNames);

        String[] expected = {};
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIOMock, "Z");

        Assert.assertArrayEquals(expected, result);
        verify(fileIOMock, times(1)).readFile(filepath);
    }
}
```
Two methods were generated. overall, the quality of tests is weak.
it only covered an edge case for empty result while empty file
and empty suffix should have been checked. Arguments could
also be null.

#### Conclusion
Even with a more complex prompt, these generated tests did not meet
my expectations. More
testing should be done to understand whether the problem is the model itself.
I will prompt the last model for perfection and I will keep my expectation much higher.


### Generating Unit tests with Anthropic Claude Opus 4.6
> create unit tests for the added files in the context. use JUnit 5.
Make sure tests have MC/DC coverage together for each method.
do a robust boundary values testing in every decision. make sure to cover a value just above, same and just below the boundary.
include tests with lowest and highest integer or other types values, especially if it can affect the functionality negatively like overflow.
If a test results in exception, assert the exception message as well.
Include parametrized test if needed.
Include mocking if a unit has dependencies (don't use mocking on all test methods).
cover all possible edge cases like empty array, null argument, all possible IO states, etc.
use test suite if needed.
generated classes name must end with "ClaudeTest".
Do your best, figure out things that I missed in the prompt
yourself. the generated tests must be 10 out of 10, perfection!

It took about 10 minutes for the agent to generate all the tests.
this model always takes a long time to complete its output.
It generated 1429 lines (including comments, codes and empty spaces)!
the model started with reading the units, then it created a todo
list for itself, then it generated all test files.
It also generated a test suite.

I have very high expectation; not only we are using the best currently
available model, but also I asked for perfection. Although 1429 lines is crazy high.
This could be overkill.

#### MyMath
```java
/**
 * Comprehensive unit tests for {@link MyMath} with MC/DC coverage,
 * boundary value analysis, and edge case testing.
 */
@DisplayName("MyMath Tests")
public class MyMathClaudeTest {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    // =========================================================================
    // checkSign(int number) tests
    // =========================================================================
    @Nested
    @DisplayName("checkSign")
    class CheckSignTests {

        // --- MC/DC: Decision 1: number > 0 ---
        // Condition: number > 0 is a single condition.
        // true  → "positive"
        // false → falls through to next decision

        // --- MC/DC: Decision 2: number < 0 (reached when number <= 0) ---
        // Condition: number < 0 is a single condition.
        // true  → "negative"
        // false → exception (number == 0)

        @Test
        @DisplayName("returns 'positive' for positive number (MC/DC: number>0 = true)")
        void checkSign_positiveNumber_returnsPositive() {
            assertEquals("positive", myMath.checkSign(5));
        }

        @Test
        @DisplayName("returns 'negative' for negative number (MC/DC: number>0 = false, number<0 = true)")
        void checkSign_negativeNumber_returnsNegative() {
            assertEquals("negative", myMath.checkSign(-5));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for zero (MC/DC: number>0 = false, number<0 = false)")
        void checkSign_zero_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myMath.checkSign(0)
            );
            assertEquals("Number had no sign", ex.getMessage());
        }

        // --- Boundary value analysis around 0 ---

        @Test
        @DisplayName("boundary: number = 1 (just above 0)")
        void checkSign_justAboveZero_returnsPositive() {
            assertEquals("positive", myMath.checkSign(1));
        }

        @Test
        @DisplayName("boundary: number = 0 (at boundary)")
        void checkSign_atZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
        }

        @Test
        @DisplayName("boundary: number = -1 (just below 0)")
        void checkSign_justBelowZero_returnsNegative() {
            assertEquals("negative", myMath.checkSign(-1));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("Integer.MAX_VALUE returns 'positive'")
        void checkSign_intMaxValue_returnsPositive() {
            assertEquals("positive", myMath.checkSign(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer.MIN_VALUE returns 'negative'")
        void checkSign_intMinValue_returnsNegative() {
            assertEquals("negative", myMath.checkSign(Integer.MIN_VALUE));
        }

        // --- Parameterized positive values ---
        @ParameterizedTest(name = "checkSign({0}) = positive")
        @ValueSource(ints = {1, 2, 10, 100, 1000, Integer.MAX_VALUE})
        @DisplayName("parameterized: various positive numbers")
        void checkSign_variousPositiveNumbers_returnsPositive(int number) {
            assertEquals("positive", myMath.checkSign(number));
        }

        // --- Parameterized negative values ---
        @ParameterizedTest(name = "checkSign({0}) = negative")
        @ValueSource(ints = {-1, -2, -10, -100, -1000, Integer.MIN_VALUE})
        @DisplayName("parameterized: various negative numbers")
        void checkSign_variousNegativeNumbers_returnsNegative(int number) {
            assertEquals("negative", myMath.checkSign(number));
        }
    }

    // =========================================================================
    // reverseNumber(int number) tests
    // =========================================================================
    @Nested
    @DisplayName("reverseNumber")
    class ReverseNumberTests {

        @Test
        @DisplayName("reverses a positive number to negative")
        void reverseNumber_positiveNumber_returnsNegative() {
            assertEquals(-5, myMath.reverseNumber(5));
        }

        @Test
        @DisplayName("reverses a negative number to positive")
        void reverseNumber_negativeNumber_returnsPositive() {
            assertEquals(5, myMath.reverseNumber(-5));
        }

        @Test
        @DisplayName("reverses zero to zero")
        void reverseNumber_zero_returnsZero() {
            assertEquals(0, myMath.reverseNumber(0));
        }

        // --- Boundary values ---

        @Test
        @DisplayName("boundary: number = 1")
        void reverseNumber_one_returnsMinusOne() {
            assertEquals(-1, myMath.reverseNumber(1));
        }

        @Test
        @DisplayName("boundary: number = -1")
        void reverseNumber_minusOne_returnsOne() {
            assertEquals(1, myMath.reverseNumber(-1));
        }

        // --- Extreme / overflow edge cases ---

        @Test
        @DisplayName("Integer.MAX_VALUE reversal")
        void reverseNumber_intMaxValue_returnsNegative() {
            assertEquals(-Integer.MAX_VALUE, myMath.reverseNumber(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer.MIN_VALUE reversal causes overflow (stays MIN_VALUE)")
        void reverseNumber_intMinValue_overflows() {
            // -Integer.MIN_VALUE overflows in two's complement,
            // resulting in Integer.MIN_VALUE itself.
            // This is a known integer overflow edge case.
            assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
        }

        @Test
        @DisplayName("-(MAX_VALUE) reversal returns MAX_VALUE")
        void reverseNumber_negativeMaxValue_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myMath.reverseNumber(-Integer.MAX_VALUE));
        }

        // --- Parameterized: verify reverse is an involution (f(f(x)) = x) ---
        @ParameterizedTest(name = "reverseNumber(reverseNumber({0})) = {0}")
        @ValueSource(ints = {0, 1, -1, 42, -42, 1000, -1000, Integer.MAX_VALUE})
        @DisplayName("parameterized: double reversal is identity")
        void reverseNumber_doubleReversal_returnsOriginal(int number) {
            assertEquals(number, myMath.reverseNumber(myMath.reverseNumber(number)));
        }
    }
}
```
Already compared to previous models, many more test methods are generated.
It is using nested class to check each function in a corresponding class
interestingly. I have not seen this pattern before. Also, a *@beforeEach*
is used to instantiate MyMath. this does reduce code size. it has been
used for other generated unit tests as well.

##### *checkSign(int)* method
10 tests were generated, including 2 parametrized tests. the tests include:
- a normal positive number (5)
- a normal negative number (-5)
- zero which causes exception, the exception message is asserted as well
- just above the 0 boundary (1)
- at the boundary (0) we have a duplicate it looks like
- just below the 0 boundary (-1)
- maximum 32-bit integer (2147483647)
- minimum 32-bit integer (-2147483648)
- 5 positive numbers parametrized test, (1) is duplicate with just above boundary
- 5 negative numbers parametrized test, (-1) is duplicate with just below boundary

Other than having 3 duplicates, I say we achieved perfection here.
As in the comments, it has mentioned, we have achieved MC/DC coverage.
perfection definitely came at the cost of redundancy and overkilling unit test.

##### *reverseNumber(int)* method
The model generated 9 tests, including a
parametrized test. the tests include:
- a normal positive number (5)
- a normal negative number (-5)
- zero in which output is equal to input
- just above the boundary (1)
- just below the boundary (-1)
- maximum 32-bit integer (2147483647)
- minimum 32-bit integer (-2147483648) which results in overflow
- reverse of maximum 32-bit integer (-2147483647)
- a parametrized test, asserting reversing the reverse of number is equal to itself.

Again, there are some duplicated tests like 1, 0 and -1. It is better to
have those boundary values in their separated tests not in a parametrized
test. MC/DC is achieved. boundaries are checked very strongly too.


#### MyAdvancedMath
Talking about overkill, remember when previous models generated like 6 methods total,
we have over 50 here!!!
```java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive unit tests for {@link MyAdvancedMath} with MC/DC coverage,
 * boundary value analysis, edge case testing, and mocking.
 */
@DisplayName("MyAdvancedMath Tests")
public class MyAdvancedMathClaudeTest {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    // =========================================================================
    // add(int x, int y) tests
    // =========================================================================
    @Nested
    @DisplayName("add")
    class AddTests {

        // --- MC/DC for Decision 1: (x < 0 || y < 0) ---
        // To achieve MC/DC for a 2-condition OR:
        //   TC1: x<0=T, y<0=F → decision=T  (x independently causes true)
        //   TC2: x<0=F, y<0=T → decision=T  (y independently causes true)
        //   TC3: x<0=F, y<0=F → decision=F  (both false → false)

        @Test
        @DisplayName("MC/DC: x<0 independently causes exception (x<0=T, y<0=F)")
        void add_negativeXPositiveY_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(-1, 5)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: y<0 independently causes exception (x<0=F, y<0=T)")
        void add_positiveXNegativeY_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(5, -1)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: both non-negative → no exception (x<0=F, y<0=F)")
        void add_bothNonNegative_returnsSum() {
            assertEquals(10, myAdvancedMath.add(5, 5));
        }

        @Test
        @DisplayName("MC/DC: both negative → exception (x<0=T, y<0=T)")
        void add_bothNegative_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(-3, -7)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        // --- MC/DC for Decision 2: y > Integer.MAX_VALUE - x (overflow check) ---
        // Reached when x>=0 && y>=0.
        // Single condition, so T/F covers MC/DC.

        @Test
        @DisplayName("MC/DC: overflow condition true → exception")
        void add_overflowDetected_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MAX_VALUE, 1)
            );
            assertEquals("Cannot fit the result in an Integer.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: overflow condition false → returns sum")
        void add_noOverflow_returnsSum() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(Integer.MAX_VALUE, 0));
        }

        // --- Boundary: x around 0 ---

        @Test
        @DisplayName("boundary: x = -1 (just below 0), y = 0 → exception")
        void add_xJustBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 0));
        }

        @Test
        @DisplayName("boundary: x = 0, y = 0 → returns 0")
        void add_xAtZeroYAtZero_returnsZero() {
            assertEquals(0, myAdvancedMath.add(0, 0));
        }

        @Test
        @DisplayName("boundary: x = 1 (just above 0), y = 0 → returns 1")
        void add_xJustAboveZero_returnsOne() {
            assertEquals(1, myAdvancedMath.add(1, 0));
        }

        // --- Boundary: y around 0 ---

        @Test
        @DisplayName("boundary: x = 0, y = -1 (just below 0) → exception")
        void add_yJustBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(0, -1));
        }

        @Test
        @DisplayName("boundary: x = 0, y = 1 (just above 0) → returns 1")
        void add_yJustAboveZero_returnsOne() {
            assertEquals(1, myAdvancedMath.add(0, 1));
        }

        // --- Boundary: overflow boundary (y = MAX_VALUE - x) ---

        @Test
        @DisplayName("boundary: y exactly at overflow limit → no exception")
        void add_yExactlyAtOverflowLimit_returnsSum() {
            // x=1, y=MAX_VALUE-1 → y == MAX_VALUE-x → just within bounds
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(1, Integer.MAX_VALUE - 1));
        }

        @Test
        @DisplayName("boundary: y one above overflow limit → exception")
        void add_yOneAboveOverflowLimit_throwsException() {
            // x=1, y=MAX_VALUE → y > MAX_VALUE-1 → overflow
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(1, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("boundary: y one below overflow limit → sum OK")
        void add_yOneBelowOverflowLimit_returnsSum() {
            // x=2, y=MAX_VALUE-3 → y < MAX_VALUE-2 → within bounds
            assertEquals(Integer.MAX_VALUE - 1, myAdvancedMath.add(2, Integer.MAX_VALUE - 3));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("both MAX_VALUE → overflow exception")
        void add_bothMaxValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("MAX_VALUE + 0 = MAX_VALUE (no overflow)")
        void add_maxValuePlusZero_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(Integer.MAX_VALUE, 0));
        }

        @Test
        @DisplayName("0 + MAX_VALUE = MAX_VALUE (no overflow)")
        void add_zeroPlusMaxValue_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(0, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("x = MIN_VALUE → exception (negative)")
        void add_xMinValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MIN_VALUE, 0));
        }

        @Test
        @DisplayName("y = MIN_VALUE → exception (negative)")
        void add_yMinValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(0, Integer.MIN_VALUE));
        }

        // --- Parameterized: valid additions ---

        @ParameterizedTest(name = "add({0}, {1}) = {2}")
        @CsvSource({
                "0, 0, 0",
                "1, 1, 2",
                "0, 5, 5",
                "5, 0, 5",
                "100, 200, 300",
                "1000000, 2000000, 3000000"
        })
        @DisplayName("parameterized: valid additions return correct sum")
        void add_validInputs_returnsCorrectSum(int x, int y, int expected) {
            assertEquals(expected, myAdvancedMath.add(x, y));
        }

        // --- Parameterized: negative input exceptions ---

        @ParameterizedTest(name = "add({0}, {1}) throws exception")
        @CsvSource({
                "-1, 0",
                "0, -1",
                "-1, -1",
                "-100, 50",
                "50, -100"
        })
        @DisplayName("parameterized: negative inputs throw exception")
        void add_negativeInputs_throwsException(int x, int y) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(x, y)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }
    }

    // =========================================================================
    // powerOfTwo(int power) tests
    // =========================================================================
    @Nested
    @DisplayName("powerOfTwo")
    class PowerOfTwoTests {

        // --- MC/DC for Decision 1: power == 0 ---
        // Single condition: true → return 1, false → continue

        @Test
        @DisplayName("MC/DC: power == 0 → returns 1")
        void powerOfTwo_zero_returnsOne() {
            assertEquals(1, myAdvancedMath.powerOfTwo(0));
        }

        @Test
        @DisplayName("MC/DC: power != 0, valid → computes result")
        void powerOfTwo_nonZeroValid_returnsResult() {
            assertEquals(2, myAdvancedMath.powerOfTwo(1));
        }

        // --- MC/DC for Decision 2: (power < 0 || power > 30) ---
        // Reached when power != 0.
        // TC1: power<0=T, power>30=F → decision=T (power<0 independently causes true)
        // TC2: power<0=F, power>30=T → decision=T (power>30 independently causes true)
        // TC3: power<0=F, power>30=F → decision=F (both false)

        @Test
        @DisplayName("MC/DC: power<0 independently causes exception (power<0=T, power>30=F)")
        void powerOfTwo_negative_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(-1)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: power>30 independently causes exception (power<0=F, power>30=T)")
        void powerOfTwo_above30_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(31)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: both conditions false → computes (power<0=F, power>30=F)")
        void powerOfTwo_withinRange_returnsResult() {
            assertEquals(1024, myAdvancedMath.powerOfTwo(10));
        }

        // --- Boundary at 0: -1, 0, 1 ---

        @Test
        @DisplayName("boundary: power = -1 (just below 0) → exception")
        void powerOfTwo_justBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
        }

        @Test
        @DisplayName("boundary: power = 0 → returns 1")
        void powerOfTwo_atZero_returnsOne() {
            assertEquals(1, myAdvancedMath.powerOfTwo(0));
        }

        @Test
        @DisplayName("boundary: power = 1 (just above 0) → returns 2")
        void powerOfTwo_justAboveZero_returnsTwo() {
            assertEquals(2, myAdvancedMath.powerOfTwo(1));
        }

        // --- Boundary at 30: 29, 30, 31 ---

        @Test
        @DisplayName("boundary: power = 29 (just below 30) → returns 2^29")
        void powerOfTwo_justBelow30_returnsResult() {
            assertEquals(536870912, myAdvancedMath.powerOfTwo(29)); // 2^29
        }

        @Test
        @DisplayName("boundary: power = 30 (at upper bound) → returns 2^30")
        void powerOfTwo_at30_returnsResult() {
            assertEquals(1073741824, myAdvancedMath.powerOfTwo(30)); // 2^30
        }

        @Test
        @DisplayName("boundary: power = 31 (just above 30) → exception")
        void powerOfTwo_justAbove30_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("Integer.MIN_VALUE → exception")
        void powerOfTwo_intMinValue_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(Integer.MIN_VALUE)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("Integer.MAX_VALUE → exception")
        void powerOfTwo_intMaxValue_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(Integer.MAX_VALUE)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        // --- Parameterized: valid power computations ---

        @ParameterizedTest(name = "2^{0} = {1}")
        @CsvSource({
                "0, 1",
                "1, 2",
                "2, 4",
                "3, 8",
                "4, 16",
                "5, 32",
                "10, 1024",
                "15, 32768",
                "20, 1048576",
                "25, 33554432",
                "30, 1073741824"
        })
        @DisplayName("parameterized: valid powers return correct result")
        void powerOfTwo_validPower_returnsCorrectResult(int power, int expected) {
            assertEquals(expected, myAdvancedMath.powerOfTwo(power));
        }

        // --- Parameterized: invalid power values ---

        @ParameterizedTest(name = "powerOfTwo({0}) throws exception")
        @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE, 31, 32, 100, Integer.MAX_VALUE})
        @DisplayName("parameterized: invalid powers throw exception")
        void powerOfTwo_invalidPower_throwsException(int power) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(power)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }
    }

    // =========================================================================
    // reverseArrayNumbers(int[] array, MyMath mm) tests
    // =========================================================================
    @Nested
    @DisplayName("reverseArrayNumbers")
    class ReverseArrayNumbersTests {

        // --- Tests WITHOUT mocking (using real MyMath) ---

        @Test
        @DisplayName("reverses array of positive numbers")
        void reverseArrayNumbers_positiveNumbers_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{-1, -2, -3}, result);
        }

        @Test
        @DisplayName("reverses array of negative numbers")
        void reverseArrayNumbers_negativeNumbers_returnsPositive() {
            MyMath mm = new MyMath();
            int[] input = {-4, -5, -6};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{4, 5, 6}, result);
        }

        @Test
        @DisplayName("reverses array of mixed numbers")
        void reverseArrayNumbers_mixedNumbers_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {-3, 0, 7};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{3, 0, -7}, result);
        }

        @Test
        @DisplayName("empty array returns empty array")
        void reverseArrayNumbers_emptyArray_returnsEmptyArray() {
            MyMath mm = new MyMath();
            int[] input = {};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{}, result);
        }

        @Test
        @DisplayName("single element array")
        void reverseArrayNumbers_singleElement_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {42};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{-42}, result);
        }

        @Test
        @DisplayName("array with zeros")
        void reverseArrayNumbers_zeros_returnsZeros() {
            MyMath mm = new MyMath();
            int[] input = {0, 0, 0};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{0, 0, 0}, result);
        }

        @Test
        @DisplayName("array with extreme values")
        void reverseArrayNumbers_extremeValues_handlesCorrectly() {
            MyMath mm = new MyMath();
            int[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            // -MAX_VALUE is valid; -MIN_VALUE overflows back to MIN_VALUE
            assertArrayEquals(new int[]{-Integer.MAX_VALUE, Integer.MIN_VALUE}, result);
        }

        @Test
        @DisplayName("modifies the input array in place (returns same reference)")
        void reverseArrayNumbers_modifiesInputArray_sameReference() {
            MyMath mm = new MyMath();
            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertSame(input, result);
        }

        // --- Null argument edge cases ---

        @Test
        @DisplayName("null array throws NullPointerException")
        void reverseArrayNumbers_nullArray_throwsNPE() {
            MyMath mm = new MyMath();
            assertThrows(NullPointerException.class,
                    () -> myAdvancedMath.reverseArrayNumbers(null, mm));
        }

        @Test
        @DisplayName("null MyMath throws NullPointerException for non-empty array")
        void reverseArrayNumbers_nullMyMath_throwsNPE() {
            int[] input = {1, 2, 3};
            assertThrows(NullPointerException.class,
                    () -> myAdvancedMath.reverseArrayNumbers(input, null));
        }

        @Test
        @DisplayName("null MyMath with empty array returns empty array (loop not entered)")
        void reverseArrayNumbers_nullMyMathEmptyArray_returnsEmptyArray() {
            int[] input = {};
            // Loop body never executes, so null mm is never dereferenced
            int[] result = myAdvancedMath.reverseArrayNumbers(input, null);
            assertArrayEquals(new int[]{}, result);
        }
    }

    // =========================================================================
    // reverseArrayNumbers with Mock (verify interactions)
    // =========================================================================
    @Nested
    @DisplayName("reverseArrayNumbers with Mock")
    @ExtendWith(MockitoExtension.class)
    class ReverseArrayNumbersMockTests {

        @Mock
        private MyMath mockMyMath;

        @Test
        @DisplayName("mock: verifies reverseNumber called for each element")
        void reverseArrayNumbers_mockMyMath_verifiesInteraction() {
            when(mockMyMath.reverseNumber(1)).thenReturn(-1);
            when(mockMyMath.reverseNumber(2)).thenReturn(-2);
            when(mockMyMath.reverseNumber(3)).thenReturn(-3);

            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{-1, -2, -3}, result);

            verify(mockMyMath, times(1)).reverseNumber(1);
            verify(mockMyMath, times(1)).reverseNumber(2);
            verify(mockMyMath, times(1)).reverseNumber(3);
            verifyNoMoreInteractions(mockMyMath);
        }

        @Test
        @DisplayName("mock: empty array → reverseNumber never called")
        void reverseArrayNumbers_mockEmptyArray_noCalls() {
            int[] input = {};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{}, result);
            verifyNoInteractions(mockMyMath);
        }

        @Test
        @DisplayName("mock: single element array → reverseNumber called once")
        void reverseArrayNumbers_mockSingleElement_calledOnce() {
            when(mockMyMath.reverseNumber(99)).thenReturn(-99);

            int[] input = {99};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{-99}, result);
            verify(mockMyMath, times(1)).reverseNumber(99);
        }

        @Test
        @DisplayName("mock: verifies call order with large array")
        void reverseArrayNumbers_mockLargeArray_allElementsProcessed() {
            int[] input = new int[100];
            for (int i = 0; i < 100; i++) {
                input[i] = i;
                when(mockMyMath.reverseNumber(i)).thenReturn(-i);
            }

            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            for (int i = 0; i < 100; i++) {
                assertEquals(-i, result[i]);
                verify(mockMyMath).reverseNumber(i);
            }
        }
    }
}
```
##### AdvancedMath.add(int, int)
Generated tests include:
- positive x, negative y and vice versa
- both negative
- overflow
- boundary checking at 0 for x and y separately
- both zeros
- boundary checking at minimum and maximum integer values
- normal parametrized tests
- negative number parametrized tests

overall, 21 tests were generated. There are duplicate again. the same boundary numbers in
parametrized tests. we can conclude that in parametrized tests, the agent mistakenly places
some inputs that it had tested before, notably values at the boundaries.

##### AdvancedMath.powerOfTwo(int)
15 methods were generated. the tests include:
- boundary checking at the lower boundary (-1, 0 , 1)
- boundary checking at the upper boundary (29, 30, 31)
- extreme values (min and max integer)
- normal parametrized test
- invalid parametrized test

Not only there are redundant values at parametrized test but also in the
normal test themselves! as much as how well
the boundaries are checked, these recent tests were ignored. I believe checking
maximum values is unnecessary for this method since they don't really differ from 
a normal value with the same sign.

##### AdvancedMath.ReverseArrayNumber(int[], MyMath)
15 methods were generated. some include mocking which I won't assess. test cases include:
- a normal all positive integers array
- a normal all negative integers array
- a mixed array (contains a positive, zero and a negative) number
- empty array
- single element array
- array filled with zeros
- array with extreme values (also causing overflow)
- asserting equality of returned array and referenced array
- null array
- null MyMath
- null MyMath with empty array (won't run to exception)

I believe all the edge cases were covered here.

#### FileIO Unit
For this unit with a single method, 17 tests are generated.
```java
package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Comprehensive unit tests for {@link FileIO} with boundary value analysis,
 * IO edge cases, and temporary file testing.
 */
@DisplayName("FileIO Tests")
public class FileIOClaudeTest {

    private FileIO fileIO;

    @BeforeEach
    void setUp() {
        fileIO = new FileIO();
    }

    // =========================================================================
    // readFile(String filepath) - Normal cases
    // =========================================================================
    @Nested
    @DisplayName("readFile - normal cases")
    class ReadFileNormalTests {

        @Test
        @DisplayName("reads valid file from test resources (names_valid.txt)")
        void readFile_validResourceFile_returnsAllLines() {
            // Use the test resource file that exists in the project
            String filepath = "src/test/resources/names_valid.txt";
            String[] result = fileIO.readFile(filepath);
            assertNotNull(result);
            assertEquals(9, result.length);
            assertEquals("Mitch", result[0]);
            assertEquals("Logan", result[8]);
        }

        @Test
        @DisplayName("reads file with multiple lines and verifies content order")
        void readFile_multipleLines_preservesOrder(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("multiline.txt");
            Files.write(file, java.util.List.of("alpha", "beta", "gamma", "delta"));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(4, result.length);
            assertEquals("alpha", result[0]);
            assertEquals("beta", result[1]);
            assertEquals("gamma", result[2]);
            assertEquals("delta", result[3]);
        }

        @Test
        @DisplayName("reads file with single line")
        void readFile_singleLine_returnsSingleElementArray(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("single.txt");
            Files.writeString(file, "only one line");

            String[] result = fileIO.readFile(file.toString());

            assertEquals(1, result.length);
            assertEquals("only one line", result[0]);
        }
    }

    // =========================================================================
    // readFile - Edge cases
    // =========================================================================
    @Nested
    @DisplayName("readFile - edge cases")
    class ReadFileEdgeCaseTests {

        @Test
        @DisplayName("empty file returns empty array")
        void readFile_emptyFile_returnsEmptyArray(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("empty.txt");
            Files.writeString(file, "");

            String[] result = fileIO.readFile(file.toString());

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("file with only whitespace lines returns those lines")
        void readFile_whitespaceLines_returnsWhitespace(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("whitespace.txt");
            Files.write(file, java.util.List.of("  ", "\t", "   "));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(3, result.length);
            assertEquals("  ", result[0]);
            assertEquals("\t", result[1]);
            assertEquals("   ", result[2]);
        }

        @Test
        @DisplayName("file with empty lines between content")
        void readFile_emptyLinesBetweenContent_preservesEmptyLines(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("gaps.txt");
            Files.write(file, java.util.List.of("line1", "", "line3", "", "line5"));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(5, result.length);
            assertEquals("line1", result[0]);
            assertEquals("", result[1]);
            assertEquals("line3", result[2]);
            assertEquals("", result[3]);
            assertEquals("line5", result[4]);
        }

        @Test
        @DisplayName("file with special characters")
        void readFile_specialCharacters_readsCorrectly(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("special.txt");
            Files.write(file, java.util.List.of(
                    "hello world!",
                    "café",
                    "line with\ttab",
                    "!@#$%^&*()"
            ));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(4, result.length);
            assertEquals("hello world!", result[0]);
            assertEquals("café", result[1]);
            assertEquals("line with\ttab", result[2]);
            assertEquals("!@#$%^&*()", result[3]);
        }

        @Test
        @DisplayName("very long lines are read correctly")
        void readFile_veryLongLines_readsCorrectly(@TempDir Path tempDir) throws IOException {
            String longLine = "A".repeat(10000);
            Path file = tempDir.resolve("longline.txt");
            Files.writeString(file, longLine);

            String[] result = fileIO.readFile(file.toString());

            assertEquals(1, result.length);
            assertEquals(longLine, result[0]);
        }

        @Test
        @DisplayName("file with many lines")
        void readFile_manyLines_readsAll(@TempDir Path tempDir) throws IOException {
            java.util.List<String> lines = new java.util.ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                lines.add("Line " + i);
            }
            Path file = tempDir.resolve("many.txt");
            Files.write(file, lines);

            String[] result = fileIO.readFile(file.toString());

            assertEquals(1000, result.length);
            assertEquals("Line 0", result[0]);
            assertEquals("Line 999", result[999]);
        }
    }

    // =========================================================================
    // readFile - IO error / invalid input cases
    // =========================================================================
    @Nested
    @DisplayName("readFile - IO and error handling")
    class ReadFileErrorTests {

        @Test
        @DisplayName("non-existent file returns empty array (IOException caught)")
        void readFile_nonExistentFile_returnsEmptyArray() {
            String[] result = fileIO.readFile("this/file/does/not/exist.txt");

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("non-existent file prints stack trace to stderr")
        void readFile_nonExistentFile_printsStackTrace() {
            PrintStream originalErr = System.err;
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errContent));
            try {
                fileIO.readFile("nonexistent_file_12345.txt");
                String errorOutput = errContent.toString();
                assertTrue(errorOutput.contains("java.io.FileNotFoundException"));
            } finally {
                System.setErr(originalErr);
            }
        }

        @Test
        @DisplayName("null filepath throws NullPointerException")
        void readFile_nullFilepath_throwsNPE() {
            assertThrows(NullPointerException.class, () -> fileIO.readFile(null));
        }

        @Test
        @DisplayName("filepath is a directory → returns empty array (IOException caught)")
        void readFile_directory_returnsEmptyArray(@TempDir Path tempDir) {
            // Attempting to read a directory as a file triggers an IOException
            String[] result = fileIO.readFile(tempDir.toString());

            assertNotNull(result);
            // Behavior is platform-dependent; on some platforms a directory may
            // not be readable as a file, resulting in empty or IOException
            // On most platforms, FileReader on a directory throws IOException
        }

        @Test
        @DisplayName("empty string filepath returns empty array (IOException)")
        void readFile_emptyStringFilepath_returnsEmptyArray() {
            // new File("") may or may not throw, depending on platform
            // but attempting to read it should fail gracefully
            String[] result = fileIO.readFile("");
            assertNotNull(result);
        }

        @Test
        @DisplayName("filepath with only spaces")
        void readFile_spacesOnlyFilepath_returnsEmptyArray() {
            String[] result = fileIO.readFile("   ");
            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // =========================================================================
    // readFile - Return type validation
    // =========================================================================
    @Nested
    @DisplayName("readFile - return type")
    class ReadFileReturnTypeTests {

        @Test
        @DisplayName("returns String array, not null, for valid file")
        void readFile_validFile_neverReturnsNull(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test.txt");
            Files.writeString(file, "content");

            String[] result = fileIO.readFile(file.toString());

            assertNotNull(result);
            assertInstanceOf(String[].class, result);
        }

        @Test
        @DisplayName("return array is independent of internal list")
        void readFile_returnedArray_isNewArray(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test.txt");
            Files.write(file, java.util.List.of("a", "b"));

            String[] result1 = fileIO.readFile(file.toString());
            String[] result2 = fileIO.readFile(file.toString());

            assertNotSame(result1, result2);
            assertArrayEquals(result1, result2);
        }
    }
}
```
Some methods use temporary directory while others don't. this
did not happen with previous models.
Generated Tests include:
- normal test
- single line file
- empty file
- file with only white spaces
- file with empty lines between content
- file with special characters
- file with a single very long line
- file with a thousand lines
- non-existent file
- null filepath
- filepath is a directory
- empty string filepath
- filepath with only spaces
- return type assertion
- return array is independent of internal list

Many edge cases that I couldn't think about like a single very long line
are covered in these tests, But was some of them necessary when BufferReader
is already a trusted Java's Library class? Checking some implementation details like
checking stack trace was not necessary. 

#### NameSearch Unit
For the final unit review of this report, the agent generated 24 methods.
```java
package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dmst.seip.angor.junitdemo.io.FileIO;

/**
 * Comprehensive unit tests for {@link NameSearch} with MC/DC coverage,
 * boundary value analysis, mocking, and edge case testing.
 */
@DisplayName("NameSearch Tests")
public class NameSearchClaudeTest {

    private NameSearch nameSearch;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
    }

    // =========================================================================
    // findNamesInFileThatStartWith - Integration tests (real FileIO)
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - Integration with real FileIO")
    class IntegrationTests {

        @Test
        @DisplayName("finds names starting with 'M' in test resource file")
        void findNames_prefixM_returnsMatches() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "M");

            assertEquals(2, result.length);
            assertEquals("Mitch", result[0]);
            assertEquals("Matt", result[1]);
        }

        @Test
        @DisplayName("finds names starting with 'C' in test resource file")
        void findNames_prefixC_returnsMatches() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "C");

            assertEquals(2, result.length);
            assertEquals("C.J", result[0]);
            assertEquals("Caroline", result[1]);
        }

        @Test
        @DisplayName("prefix with no match returns empty array")
        void findNames_noMatch_returnsEmpty() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "Z");

            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // =========================================================================
    // findNamesInFileThatStartWith - Mocked FileIO tests
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - Mocked FileIO")
    @ExtendWith(MockitoExtension.class)
    class MockedTests {

        @Mock
        private FileIO mockFileIO;

        // --- MC/DC for Decision: name.startsWith(prefix) ---
        // Single condition: true → add to list, false → skip
        // Two tests: one matching, one not matching → full MC/DC

        @Test
        @DisplayName("MC/DC: name.startsWith(prefix) = true → included in results")
        void findNames_matchingPrefix_included() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("MC/DC: name.startsWith(prefix) = false → excluded from results")
        void findNames_nonMatchingPrefix_excluded() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(0, result.length);
        }

        // --- Comprehensive matching tests ---

        @Test
        @DisplayName("multiple names, some match prefix")
        void findNames_mixedNames_returnsOnlyMatches() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Alex", "Charlie", "Amanda"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(2, result.length);
            assertEquals("Alice", result[0]);
            assertEquals("Alex", result[1]);
        }

        @Test
        @DisplayName("all names match prefix")
        void findNames_allMatch_returnsAll() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Alex", "Amanda"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(3, result.length);
        }

        @Test
        @DisplayName("no names match prefix")
        void findNames_nonMatch_returnsEmpty() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Z");

            assertEquals(0, result.length);
        }

        // --- Empty prefix (boundary) ---

        @Test
        @DisplayName("empty prefix matches all names (every string starts with empty)")
        void findNames_emptyPrefix_matchesAll() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "");

            assertEquals(3, result.length);
            assertEquals("Alice", result[0]);
            assertEquals("Bob", result[1]);
            assertEquals("Charlie", result[2]);
        }

        // --- Empty file ---

        @Test
        @DisplayName("file with no names returns empty array")
        void findNames_emptyFile_returnsEmpty() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(0, result.length);
        }

        // --- Case sensitivity ---

        @Test
        @DisplayName("prefix matching is case-sensitive")
        void findNames_caseSensitive_exactMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"alice", "Alice", "ALICE"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("lowercase prefix does not match uppercase names")
        void findNames_lowercasePrefix_noUppercaseMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "a");

            assertEquals(0, result.length);
        }

        // --- Prefix equals name exactly ---

        @Test
        @DisplayName("prefix equal to full name matches")
        void findNames_prefixEqualsName_matches() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Alice");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        // --- Prefix longer than any name ---

        @Test
        @DisplayName("prefix longer than any name matches nothing")
        void findNames_prefixLongerThanNames_noMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Al", "Bo"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Alice");

            assertEquals(0, result.length);
        }

        // --- Verify FileIO interaction ---

        @Test
        @DisplayName("verifies readFile is called once with correct filepath")
        void findNames_verifiesFileIOInteraction() {
            when(mockFileIO.readFile("mypath/names.txt"))
                    .thenReturn(new String[]{"Test"});

            nameSearch.findNamesInFileThatStartWith("mypath/names.txt", mockFileIO, "T");

            verify(mockFileIO, times(1)).readFile("mypath/names.txt");
            verifyNoMoreInteractions(mockFileIO);
        }

        // --- Special characters in prefix ---

        @Test
        @DisplayName("prefix with special characters")
        void findNames_specialCharPrefix_matchesCorrectly() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"C.J", "Caroline", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "C.");

            assertEquals(1, result.length);
            assertEquals("C.J", result[0]);
        }

        // --- Names with empty strings ---

        @Test
        @DisplayName("file content with empty string names, non-empty prefix → empty strings don't match")
        void findNames_emptyStringNames_nonEmptyPrefixNoMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"", "", "Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("empty prefix matches empty string names too")
        void findNames_emptyPrefix_matchesEmptyNames() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"", "Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "");

            assertEquals(2, result.length);
        }

        // --- Parameterized prefix tests ---

        @ParameterizedTest(name = "prefix ''{0}'' finds matching names")
        @ValueSource(strings = {"M", "N", "C", "J", "L", "S", "Lo"})
        @DisplayName("parameterized: various prefixes on names_valid content")
        void findNames_variousPrefixes_returnCorrectCounts(String prefix) {
            // Simulate names_valid.txt content
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{
                            "Mitch", "Newmie", "C.J", "Matt", "Caroline",
                            "Jason", "Lani", "Summer", "Logan"
                    });

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, prefix);

            // Verify each name in result actually starts with prefix
            for (String name : result) {
                assertTrue(name.startsWith(prefix),
                        "Expected '" + name + "' to start with '" + prefix + "'");
            }

            // Verify no matching names were missed
            String[] allNames = {"Mitch", "Newmie", "C.J", "Matt", "Caroline",
                    "Jason", "Lani", "Summer", "Logan"};
            long expectedCount = java.util.Arrays.stream(allNames)
                    .filter(n -> n.startsWith(prefix))
                    .count();
            assertEquals(expectedCount, result.length);
        }
    }

    // =========================================================================
    // Null argument tests (no mocking needed)
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - null arguments")
    @ExtendWith(MockitoExtension.class)
    class NullArgumentTests {

        @Mock
        private FileIO mockFileIO;

        @Test
        @DisplayName("null fileIo throws NullPointerException")
        void findNames_nullFileIO_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", null, "A"));
        }

        @Test
        @DisplayName("null prefix throws NullPointerException (in startsWith)")
        void findNames_nullPrefix_throwsNPE() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice"});

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, null));
        }

        @Test
        @DisplayName("null prefix with empty file → no NPE (loop not entered)")
        void findNames_nullPrefixEmptyFile_noException() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{});

            // Loop never executes, so null prefix doesn't cause startsWith call
            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, null);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("null filepath is passed through to FileIO (behavior depends on FileIO)")
        void findNames_nullFilepath_delegatesToFileIO() {
            when(mockFileIO.readFile(null))
                    .thenThrow(new NullPointerException());

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith(null, mockFileIO, "A"));
        }

        @Test
        @DisplayName("file content with null element causes NPE on startsWith")
        void findNames_fileReturnsNullElement_throwsNPE() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{null, "Alice"});

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A"));
        }
    }
}
```
Unfortunately, mocking was very overused and some of edge
cases used mocking as well. This has also caused some
redundancies. I will not assess the mocking.  
These tests include:
- two normal tests
- empty array
- no match search
- all lines matching the prefix
- empty prefix
- empty file
- case sensitivity
- prefix being equal to a full line
- prefix longer than any names
- special character in prefix
- file content with empty string names
- empty prefix matches empty string
- parametrized test
- null FileIO
- null prefix
- null file path
- null element in readFile output

Some of the test cases were done in FileIO, and I believe they
were unnecessary like file content with empty string.
I would argue that only "C" or "M" should have been tested.
I don't think something like a null element in string array
can possibly happen but was tested anyway? Also like previous
unit test, It is unnecessary to test java library like 
testing special characters, case sensitivity.

#### Conclusion
This time, the agent met my expectation. It was not perfect. there
were many redundancies. boundary checking was well done with this
model. even though I prompted Gemini to do the boundary checking, It
 didn't, but Claude definitely did it, meaning model does matter.
The more intricate prompt also played an important role. still
a very important to do is to check how each model will do with the
same prompt. If I were to prompt Opus again, I would ask it not
to use mocking, try to not include redundant tests specially with
boundary checking and parametrized tests which had many overlaps.
Also, I would tell it to not test java libraries. they are already
tested and trusted.

## Final Conclusion
Model and prompt are both important for sure. this was not a fair
competition as Claude Opus, which is the strongest model, did much
better with a more detailed prompt. The more detail you add, 
the better it gets for sure. a good comparison for finding the
best model that is available with GitHub Copilot is to give
the same prompt to the agent with the same token consumption.
I have also created an Excel file, containing comparison
of each method across all models. I won't compare each method
generated tests here.