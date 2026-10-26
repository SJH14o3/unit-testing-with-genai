# Experiment with continue.dev in Unit Test Generation
## Continue.dev Introduction
Continue.dev is a open-source coding agent available as CLI, VS Code extension and JetBrains plugins.
Continue.dev allows access to many model provides. here is a list of its popular provides
with some of their models:
- Anthropic
  - Claude Opus 4.6
  - Claude Sonnet 4.6
  - Claude Haiku 4.5
- Azure OpenAI
  - GPT-4o
- Google Gemini API
  - Gemini 3.1 Pro
  - Gemini 3 Flash
  - Gemini 3.1 Flash Lite
- Mistral
  - Devstral Medium
  - Magistral Medium
  - Codestral
- Ollama
  - gpt-oss-20b
  - Llama 3.1 8b
  - Mistral Chat
  - DeepSeek Coder
  - Gemma3 12b
- OpenAI
  - GPT-5.2
  - GPT-5 Mini
  - GPT-5 Codex


In must cases, to use a provider models, user will need an API key. Continue has a handy
feature that opens the link to generate an API key for that provider, although you need
to be logged in first. However, this would mean user would have to buy credits for each
individual provider, which is a disadvantage compared to GitHub copilot, though user could
possibly use providers that give access to the majority of models like OpenRouter.  
It also can run a local model using Ollama. Users can run the model on their own device
if you don't have a value for apiBase in config file, continue will run the model on
your system (you must have it installed). You could also run the local model on a server
by providing its IP address and port in the apiBase field.  

Users can also apply some rules by creating markdown files in 
"<project folder>/.continue/rules/". Each markdown file can be considered a group
of rules and each group can be toggled separately in Continue's settings.

### How to install on Visual Studio Code
Head to the extensions tab and search "Continue".  
You must see "Continue - open-source AI code agent". install this extension.  
When installed, a new tab will be added to the sidebar.  you should be good to
go now.  

### How to use Continue.dev
When you open Continue's tab, you will have a chat box. when clicking on the chat box, 
you can select a mode, a model and also link something.  

#### mode
Mode has four options:
- Chat
- Plan
- Agent
- Background


##### Chat mode
Chat mode provides a conventional sidebar where users can chat with a model.
It doesn't use any tools (won't read or edit any files unless you manually link a file).

##### Plan mode
Plan mode operates as a read-only environment, helping to strategize and understand
the code. basically it can only read, but it won't modify any part of the code.

##### Agent
In Agent mode, all tools are available and AI agent can read, create and modify files.
By default, it asks for your permission before executing a tool, but policies can be
set to automatic.

##### Background mode
background execution is powered by Agent Mode and automated Cloud Agents. Instead of
waiting for you to type prompts in a chat window, these agents run asynchronously in
the background to handle tasks like fixing bugs, checking code quality, or generating
documentation.

#### Selecting models
Continue stores models configuration in this directory:  
"C\Users\<user>\.continue\config.yaml".  
In order to add model, first you select a provider. then you select a model, and then
you enter your API key. Below the API key box, a quick link to generate an API key for
that model is present. After filling all fields, you can click on the connect button. this
will update the config file. you could also do all of this yourself in the config file.  
The added model now can be selected.

#### Linking file/context
User can attach files, images, etc. to the AI chatbot either by clicking "@" button or
typing "@" in the chat box. User could link files, images (if the selected model supports it),
Git diff, Terminal (Referencing last terminal command), Problems (Problems in the current
open file) and also the rules which could be useful when you have rules that you don't want to
apply them to all prompts.  

After You have written your prompt, you can click on the enter button and the model will begin working.

## Generating Unit tests with Continue.dev
This time, I will use 2 large models and 5 local models. I will compare the same tier models 
in 3 rounds. In each round, for all models, the prompt will be the same. selected models are:
- Large
  - Mistral Devstral Medium
  - Google Gemini 3 Flash
- Local
  - GPT OSS 20b
  - Gemma3 12b
  - Gemma4 e4b
  - Qwen 2.5 Coder 7b
  - Qwen 3 coder 30b

I will start with a simple prompt in the first round, then I will evolve the prompt into a more
complex and detailed one with the previous generated unit test flaws.
Also, all four units are linked to the prompt.

## Round 1
Let us start with a simple prompt. In order to prevent mock, I will explicitly include it in the prompt.
> For these units, generate unit test using JUnit5. do not use mock for dependencies. the generated
> test classes must end with "Test(ModelName)_1"  

for the suffix, if the model name is gemma3, then a test class name will have "TestGemma3_1" suffix.
Although after generating all the tests, I realized "Test(ModelName)_P1" is better since many
models end with numbers, and for example, Gemma3_1 can possibly confuse some.

### Mistral Devstral Medium
The agent generated test files and put them in the test directory.
#### MyMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestMistral1 {

    @Test
    void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    void testCheckSignZero() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    void testReverseNumber() {
        MyMath myMath = new MyMath();
        assertEquals(-10, myMath.reverseNumber(10));
        assertEquals(10, myMath.reverseNumber(-10));
        assertEquals(0, myMath.reverseNumber(0));
    }
}
```
Something that I noticed is that a unit is for each method, a new unit is instantiated.
this could have been simplified by having the unit as a private variable.  
##### *checkSign(int)* method
Three methods were generated which include:
- a normal positive number
- a normal negative number
- 0 (boundary and exception)

Tests achieve MC/DC.  
Instead of 10 and -10, it would have been better to test 1 and -1.

##### *reverseNumber(int)* method
Only one method was generated with three assertions. It would have been better to have 3 separate
methods for each assertion. generated tests include:
- a normal positive number
- a normal negative number
- 0 (boundary)

Tests achieve MC/DC.  
Instead of 10 and -10, it would have been better to test 1 and -1.

#### MyAdvancedMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestMistral1 {

    @Test
    void testAddPositiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(15, advancedMath.add(10, 5));
    }

    @Test
    void testAddNegativeNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(10, -5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-10, -5));
    }

    @Test
    void testAddOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void testPowerOfTwo() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
        assertEquals(2, advancedMath.powerOfTwo(1));
        assertEquals(4, advancedMath.powerOfTwo(2));
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void testPowerOfTwoInvalid() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void testReverseArrayNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] input = {10, -5, 0};
        int[] expected = {-10, 5, 0};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(input, myMath));
    }
}
```
##### *add(int, int)* method
Three methods with 5 assertions were created which include:
- x and y both positive
- x is positive, y is negative and vise versa
- x and y both negative
- overflow test

Tests achieve MC/DC.  
There is no boundary value testing.

##### *powerOfTwo(int)* method
Two methods with six assertions generated which include:
- just above and below the lower boundary
- on the upper boundary
- just above the upper boundary
- two normal values  

Since *power == 0* is not tested, we don't even have statement coverage.  
Boundaries were tested here although implicitly.  
Testing 4 as input is redundant since we test 2 (another normal value).  
This method tests had quite the opposite vibe with the previous methods.

##### *reverseArrayNumbers(int[], MyMath)* method
a single method and a single assertion was generated. It is just a normal array.  

The Tested method is just a loop and MC/DC is achieved.  
many different edge cases could have been tested here.  

#### FileIO Unit
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileIOTestMistral1 {

    @Test
    void testReadFile() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Line 1\nLine 2\nLine 3".getBytes());

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(0, content.length);
    }
}
```
Two methods were generated including:
- a normal test
- an empty file test

We don't run into exception, so there is no statement coverage.
Temporary Storage is used, but it would have been better to use 
JUnit feature for temporary storage (@TempDir Annotation).  
Many edge cases like non-existing file are missing.

#### NameSearch Unit
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dmst.seip.angor.junitdemo.io.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NameSearchTestMistral1 {

    @Test
    void testFindNamesInFileThatStartWith() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nAnna\nAndrew\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A");

        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "Z");

        assertEquals(0, result.length);
    }
}

```
Two methods were generated including:
- test with multiple results
- test with no result

Tests achieve MC/DC.
Temporary Storage has the same story as FileIO tests.  
Edge cases like empty prefix are missing.  

#### Conclusion
For a simple prompt, I believe it did good enough.
The generated tests problems are:
- many methods had multiple assertions.  
- no testing at boundary values for most of the methods.
- Many edge cases are not tested.  

method names have "test" prefix.  
method names do comply with the assertions they do.

### Gemini 3 Flash
The agent generated the test files and put them in the test directory which is correct.
#### MyMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyMathTestGemini1 {

    private final MyMath mm = new MyMath();

    @Test
    public void testCheckSignPositive() {
        Assertions.assertEquals("positive", mm.checkSign(10));
    }

    @Test
    public void testCheckSignNegative() {
        Assertions.assertEquals("negative", mm.checkSign(-5));
    }

    @Test
    public void testCheckSignZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mm.checkSign(0);
        });
    }

    @Test
    public void testReverseNumberPositive() {
        Assertions.assertEquals(-10, mm.reverseNumber(10));
    }

    @Test
    public void testReverseNumberNegative() {
        Assertions.assertEquals(5, mm.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        Assertions.assertEquals(0, mm.reverseNumber(0));
    }
}
```
Unlike mistral, gemini used a private variable for unit.
##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 10 and -5.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 10 and -5.

#### MyAdvancedMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyAdvancedMathTestGemini1 {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @Test
    public void testAddSuccess() {
        Assertions.assertEquals(10, mam.add(4, 6));
    }

    @Test
    public void testAddNegativeFirst() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(-1, 5);
        });
    }

    @Test
    public void testAddNegativeSecond() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(5, -1);
        });
    }

    @Test
    public void testAddOverflow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(Integer.MAX_VALUE, 1);
        });
    }

    @Test
    public void testPowerOfTwoSuccess() {
        Assertions.assertEquals(1, mam.powerOfTwo(0));
        Assertions.assertEquals(2, mam.powerOfTwo(1));
        Assertions.assertEquals(1024, mam.powerOfTwo(10));
        Assertions.assertEquals(1073741824, mam.powerOfTwo(30));
    }

    @Test
    public void testPowerOfTwoNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.powerOfTwo(-1);
        });
    }

    @Test
    public void testPowerOfTwoTooLarge() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.powerOfTwo(31);
        });
    }

    @Test
    public void testReverseArrayNumbers() {
        MyMath mm = new MyMath();
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }
}

```
##### *add(int, int)* method
Four methods were generated that include:
- positive x and y
- positive x and negative y and vise versa
- overflow test

Tests do have MC/DC.  
boundary values, especially 0 were not tested.

##### *powerOfTwo(int)* method
Three methods were generated. one method included four assertions. The tests include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- a normal test
- the upper boundary
- just above the upper boundary

We have MC/DC.  
boundary values are tested very well.  
testing *power=10* is redundant.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated. many edge cases like empty array are not tested.
The Tested method is just a loop and MC/DC is achieved.

#### FileIO Unit
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FileIOTestGemini1 {

    private final FileIO fileIO = new FileIO();

    @Test
    public void testReadFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        String[] expectedContent = {"line1", "line2", "line3"};
        
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            for (String line : expectedContent) {
                writer.write(line + System.lineSeparator());
            }
        }

        String[] actualContent = fileIO.readFile(tempFile.toString());
        Assertions.assertArrayEquals(expectedContent, actualContent);
    }

    @Test
    public void testReadFileNotExists() {
        // FileIO.readFile prints stack trace and returns empty array if file not found
        String[] content = fileIO.readFile("non_existent_file.txt");
        Assertions.assertEquals(0, content.length);
    }
}
```
Two methods were generated that include:
- a normal test
- a non-existing file

The true way of testing with temporary directory is used.  
We do run into exception, and we achieve MC/DC.

#### NameSearch Unit
```java
package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class NameSearchTestGemini1 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    public void testFindNamesInFileThatStartWith(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("names.txt");
        String[] names = {"Antonis", "Anna", "Bob", "Alice"};
        
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            for (String name : names) {
                writer.write(name + System.lineSeparator());
            }
        }

        String[] resultA = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        Assertions.assertArrayEquals(new String[]{"Antonis", "Anna", "Alice"}, resultA);

        String[] resultAn = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "An");
        Assertions.assertArrayEquals(new String[]{"Antonis", "Anna"}, resultAn);

        String[] resultZ = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Z");
        Assertions.assertEquals(0, resultZ.length);
    }
}
```
A single method with multiple assertions is generated which includes:
- two normal test
- a test without result.  

It is faster to create a temporary file only once rather than three times. although we would
have to read it three times.  
The test achieves MC/DC.

#### Conclusion
This model also did okay for a simple prompt.  
I Would have preferred more boundary values testing.  
method names do comply with what they test, and they also start with test.  
Many edge cases have not been tested.  
some methods had multiple assertions.  

### Devstral vs Gemini
Now that we have seen the generated tests of our two large models, we can compare them.  
Mostly they did the same. I'll only cover their differences:
- Devstral tested both negative for add function while Gemini didn't.
- Gemini used temporary storage in JUnit conventional way but Devstral didn't.
- Devstral tested an empty file for FileIO but Gemini didn't.
- Gemini tested a non-existing file for FileIO but Devstral didn't.
- Gemini used private variables for units but Devstral instantiated required
variables in the method themselves.

### GPT-OSS:20b
After giving the prompt, It generated the test files, but it didn't create them in. I had
to manually create them and put them in the right directory.

#### MyMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MyMath}.
 */
class MyMathTestGPT_OSS1 {

  private final MyMath myMath = new MyMath();

  @Test
  void checkSign_positiveNumber_returnsPositive() {
    assertEquals("positive", myMath.checkSign(5));
  }

  @Test
  void checkSign_negativeNumber_returnsNegative() {
    assertEquals("negative", myMath.checkSign(-3));
  }

  @Test
  void checkSign_zero_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @Test
  void reverseNumber_positiveNumber_returnsNegative() {
    assertEquals(-10, myMath.reverseNumber(10));
  }

  @Test
  void reverseNumber_negativeNumber_returnsPositive() {
    assertEquals(7, myMath.reverseNumber(-7));
  }

  @Test
  void reverseNumber_zero_returnsZero() {
    assertEquals(0, myMath.reverseNumber(0));
  }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 5 and -3.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 10 and -7.

#### MyAdvancedMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MyAdvancedMath}.
 */
class MyAdvancedMathTestGPT_OSS1 {

  private final MyAdvancedMath advancedMath = new MyAdvancedMath();
  private final MyMath myMath = new MyMath();

  @Test
  void add_validInputs_returnsSum() {
    assertEquals(15, advancedMath.add(7, 8));
  }

  @Test
  void add_negativeInput_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    assertThrows(IllegalArgumentException.class, () -> advancedMath.add(3, -2));
  }

  @Test
  void add_overflow_throwsIllegalArgumentException() {
    int large = Integer.MAX_VALUE - 10;
    assertThrows(IllegalArgumentException.class, () -> advancedMath.add(large, 20));
  }

  @Test
  void powerOfTwo_zero_returnsOne() {
    assertEquals(1, advancedMath.powerOfTwo(0));
  }

  @Test
  void powerOfTwo_withinBounds_returnsCorrectResult() {
    assertEquals(32, advancedMath.powerOfTwo(5));
    assertEquals(1 << 10, advancedMath.powerOfTwo(10));
  }

  @Test
  void powerOfTwo_negativeOrTooLarge_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
  }

  @Test
  void reverseArrayNumbers_correctlyReversesSigns() {
    int[] input = {1, -2, 3, -4};
    int[] expected = {-1, 2, -3, 4};
    int[] result = advancedMath.reverseArrayNumbers(input.clone(), myMath);
    assertArrayEquals(expected, result);
  }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- positive x and negative y and vise versa
- overflow test

Tests do have MC/DC.  
boundary values, especially 0 were not tested.  
The overflow test was interesting. while all previous models tested *Integer.MAX_VALUE + 1*,
the model used *Integer.MAX_VALUE - 10 + 20*. Claude Opus although did something similar.

##### *powerOfTwo(int)* method
Three methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- two normal tests
- just above the upper boundary

We have MC/DC.  
boundary values are somewhat tested.
testing *power=10* is redundant.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated. many edge cases like empty array are not tested.
The Tested method is just a loop and MC/DC is achieved.

#### FileIO Unit
```java
package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FileIO}.
 */
class FileIOTestGPT_OSS1 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_existingFile_returnsLines() throws IOException {
        // Create a temporary file with known content
        File temp = File.createTempFile("testFileIO", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("first line\n");
            writer.write("second line\n");
            writer.write("third line");
        }

        String[] lines = fileIO.readFile(temp.getAbsolutePath());
        assertArrayEquals(new String[]{"first line", "second line", "third line"}, lines);
    }

    @Test
    void readFile_nonexistentFile_returnsEmptyArray() {
        // Non‑existent file
        String[] lines = fileIO.readFile("this_file_does_not_exist.txt");
        // The implementation prints a stack trace and returns an empty array
        assertEquals(0, lines.length);
    }
}
```
Two methods were generated that include:
- a normal test
- a non-existing file

A non-JUnit way of temporary directory is used.  
We do run into exception thus we achieve MC/DC.

#### NameSearch Unit
```java
package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NameSearch}.
 */
class NameSearchTestGPT_OSS1 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_matchFound_returnsMatchingNames() throws IOException {
        // Prepare a temporary file with a list of names
        File temp = File.createTempFile("names", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("Alice\n");
            writer.write("Bob\n");
            writer.write("Alex\n");
            writer.write("Eve\n");
        }

        String[] result = nameSearch.findNamesInFileThatStartWith(
                temp.getAbsolutePath(),
                fileIO,
                "Al");

        assertArrayEquals(new String[]{"Alice", "Alex"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatch_returnsEmptyArray() throws IOException {
        File temp = File.createTempFile("names", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("Bob\n");
            writer.write("Eve\n");
        }

        String[] result = nameSearch.findNamesInFileThatStartWith(
                temp.getAbsolutePath(),
                fileIO,
                "Al");

        assertEquals(0, result.length);
    }
}
```
Two methods with multiple are generated which include:
- a normal test
- a test without result.

Again, Many edge cases are missing.  
The test achieves MC/DC.

#### Conclusion
For the first local model, It almost did the same as the cloud models, although
with a more complex prompt and unit, this probably won't be the case, we will se in the
near future.
looks like I should emphasize on boundary value for the next prompt.  
method names do comply with what they test, but they don't start with test.  
Many edge cases have not been tested.  
few methods had multiple assertions.  
It did use private variables for units instead of instantiating them inside each method.

### Gemma3:12b
After giving it the prompt, it asked me to write the generated tests into the current open file?!
That caused me to create test folder and test unit files manually.

#### MyMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestGemma3_1 {

    @Test
    void checkSign_positiveNumber() {
        MyMath math = new MyMath();
        assertEquals("positive", math.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber() {
        MyMath math = new MyMath();
        assertEquals("negative", math.checkSign(-3));
    }

    @Test
    void checkSign_zeroNumber() {
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void reverseNumber_positiveNumber() {
        MyMath math = new MyMath();
        assertEquals(-7, math.reverseNumber(7));
    }

    @Test
    void reverseNumber_negativeNumber() {
        MyMath math = new MyMath();
        assertEquals(5, math.reverseNumber(-5));
    }

    @Test
    void reverseNumber_zero() {
        MyMath math = new MyMath();
        assertEquals(0, math.reverseNumber(0));
    }
}
```
Something you can notice is that for every method, a unit is generated at the beginning,
this could have been made simpler by having the unit as a private variable.
##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 5 and -3. these numbers are closer to
the boundary compared to previous models.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 7 and -5.

#### MyAdvancedMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestGemma3_1 {

    @Test
    void add_positiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumberInput() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-2, 3));
    }

    @Test
    void add_overflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void powerOfTwo_validPower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(16, advancedMath.powerOfTwo(4));
    }

    @Test
    void powerOfTwo_zeroPower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_negativePower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_powerTooLarge() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_validArray() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        int[] array = {1, 2, 3};
        int[] reversedArray = advancedMath.reverseArrayNumbers(array, math);
        assertArrayEquals(new int[]{-1, -2, -3}, reversedArray);
    }
}

```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- negative x and positive y
- overflow test

since *y < 0* is never true in tests, we don't have MC/DC and condition coverage, instead
we have decision coverage.
boundary values like 0 were not tested.

##### *powerOfTwo(int)* method
four methods were generated that include:
- just below the lower boundary
- the lower boundary
- a normal test
- just the upper boundary

We have MC/DC.  
boundary values are somewhat tested.  
There is no redundant test.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated. many edge cases like empty array are not tested.
The Tested method provides MC/DC.

#### FileIO Unit
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

class FileIOTestGemma3_1 {

    @Test
    void readFile_validFile() throws Exception {
        FileIO fileIO = new FileIO();
        // Create a temporary file for testing
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit(); // Delete when the test finishes.
        
        //Write lines to temp file
        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Line 1\n");
        fw.write("Line 2\n");
        fw.write("Line 3\n");
        fw.close();
        
        String[] expected = {"Line 1", "Line 2", "Line 3"};
        String[] actual = fileIO.readFile(tempFile.getAbsolutePath());
        assertArrayEquals(expected, actual);
    }

    @Test
    void readFile_emptyFile() throws Exception {
        FileIO fileIO = new FileIO();
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();
        
        String[] expected = new String[0];
        String[] actual = fileIO.readFile(tempFile.getAbsolutePath());
        assertArrayEquals(expected, actual);
    }

}

```
Two methods were generated that include:
- a normal test
- an empty file

The JUnit way of testing with temporary directory is not used.  
We do not exception, thus we don't achieve statement coverage.  
many edge cases like non-existing file are not covered.

#### NameSearch Unit
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGemma3_1 {

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = {"Alice"};
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(expected, actual);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = new String[0];
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        assertArrayEquals(expected, actual);
    }
}
```
Two methods are generated which include:
- a single result test
- a test without result.

The test achieves MC/DC.  
many edge cases are not covered.

#### Conclusion
This model did only slightly worse than GPT OSS. you can blame it on having lower parameters.  
not many boundary values were tested.  
method names do comply with what they test, but they don't start with test.  
Many edge cases have not been tested.  
It is the first model that all its generated methods had one assertion.
private variables were not used.

### Gemma4:e4b
This model somehow managed to mess up indentation of FileIO and NameSearch test files and 
made a large single line file that had "\n" instead of new line. I had to fix this.  
The model did not obey the said naming convention at all!
And also I had to write the files into the current open file.

#### MyMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MyMath class.
 * Must end with TestGemma4_1
 */
public class MyMathTest {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    @Test
    void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(10), "Should return 'positive' for positive numbers.");
    }

    @Test
    void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-5), "Should return 'negative' for negative numbers.");
    }

    @Test
    void testCheckSignZero() {
        // Testing for IllegalArgumentException when input is 0
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void testReverseNumberPositive() {
        assertEquals(-123, myMath.reverseNumber(123), "Should correctly reverse a positive number.");
    }

    @Test
    void testReverseNumberNegative() {
        assertEquals(456, myMath.reverseNumber(-456), "Should correctly reverse a negative number.");
    }

    @Test
    void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0), "Should return 0 for zero.");
    }
}
```
Private variables are used, and they are set up with @BeforeEach annotation which is
done different compared to previous models.

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 10 and -5.  
It also has asserted the exception message which is unnecessary.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
Choosing 123 and -456 is interesting but still 1 and -1 should have been tested.

#### MyAdvancedMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Unit tests for the MyAdvancedMath class.
 * Must end with TestGemma4_1
 */
public class MyAdvancedMathTest {

    private MyAdvancedMath myAdvancedMath;
    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
        myMath = new MyMath();
    }

    @Test
    void testAddPositiveNumbers() {
        // Test case 1: Simple addition
        assertEquals(5, myAdvancedMath.add(2, 3), "Should correctly add two small positive integers.");
        // Test case 2: Larger numbers
        assertEquals(100, myAdvancedMath.add(50, 50), "Should handle larger positive integers.");
    }

    @Test
    void testAddWithNegativeNumber() {
        // Testing for IllegalArgumentException when one or more inputs are negative
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void testAddOverflow() {
        // Testing for Integer overflow
        // MAX_VALUE is 2,147,483,647. (MAX_VALUE - 1) + 2 > MAX_VALUE
        int x = Integer.MAX_VALUE - 5;
        int y = 10;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y + 1); // This will overflow
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0), "Power of two for 0 should be 1.");
    }

    @Test
    void testPowerOfTwoPositive() {
        assertEquals(16, myAdvancedMath.powerOfTwo(4), "2^4 should be 16.");
        assertEquals(1024, myAdvancedMath.powerOfTwo(10), "2^10 should be 1024.");
    }

    @Test
    void testPowerOfTwoNegative() {
        // Testing for IllegalArgumentException when power is negative
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void testPowerOfTwoTooLarge() {
        // Testing for IllegalArgumentException when power > 30
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void testReverseArrayNumbers() {
        // Input array: {1, 2, 3}
        int[] originalArray = {1, 2, 3};
        // Expected: {-1, -2, -3}
        int[] expected = {-1, -2, -3};
        
        // We must copy the array to ensure the original state isn't unexpectedly changed by the method signature
        int[] arrayCopy = Arrays.copyOf(originalArray, originalArray.length);

        int[] actual = myAdvancedMath.reverseArrayNumbers(arrayCopy, myMath);
        
        assertArrayEquals(expected, actual, "Should reverse the sign of every element in the array.");
    }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- positive x and negative y and vise versa
- overflow test

Tests do have MC/DC.  
boundary values, especially 0 were not tested.  
It's wrong to pass y+1 in overflow assertion. should have had y=11. still, y=10 would
have caused overflow.  
Multiple assertions are used inside some of the methods.

##### *powerOfTwo(int)* method
Four methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- two normal tests
- just above the upper boundary

We have MC/DC.  
boundary values somewhat tested.
testing *power=10* is redundant and instead of *power=4*, *power=1* should have been tested.

##### *reverseArrayNumbers(int[], MyMath)* method
Only one method is generated. many edge cases like empty array are not tested.
MC/DC is achieved.

#### FileIO Unit
```java
package dmst.seip.angor.junitdemo.io;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the FileIO class.
 * Must end with TestGemma4_1
 */
public class FileIOTest {

    private FileIO fileIO;
    private Path tempFile; // Path to the temporary test file

    @BeforeEach
    void setUp() throws IOException {
        fileIO = new FileIO();
        // Create a temporary file for testing purposes
        tempFile = Files.createTempFile("test_input", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary file
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testReadFileSuccess() throws IOException {
        // Setup: Write known content to the temporary file
        String content = "Line 1\nLine 2\nLine 3";
        Files.write(tempFile, content.getBytes());

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertions
        assertNotNull(result, "The returned array should not be null.");
        assertEquals(3, result.length, "Should read exactly 3 lines.");
        assertArrayEquals(new String[]{"Line 1\n", "Line 2\n", "Line 3"}, result, "The content read should match the written content.");
    }

    @Test
    void testReadFileEmptyFile() throws IOException {
        // Setup: Create an empty temporary file
        Files.write(tempFile, "".getBytes());

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertions (Note: BufferedReader reading an empty file might result in an array of size 1 containing an empty string or size 0 depending on OS/implementation, but expecting an empty array is safest for content check).
        // Given the implementation, reading an empty file usually results in an array containing one empty string or size 0 depending on how the stream conversion handles nulls/EOF. Let's check for size 1 with empty string if the OS/stream keeps one slot, or size 0 if it truly reads nothing.
        // Testing against the most robust expected outcome for 'no content' read.
        if (result.length == 1 && result[0].isEmpty()) {
            assertTrue(true, "Should handle reading an empty file gracefully.");
        } else if (result.length == 0) {
            assertTrue(true, "Should handle reading an empty file gracefully by returning empty array.");
        }
    }

    @Test
    void testReadFileFileNotFound() {
        // Setup: Use a file path guaranteed not to exist
        String nonExistentPath = tempFile.toString() + "_nonexistent";

        // Execution
        String[] result = fileIO.readFile(nonExistentPath);

        // Assertions: The implementation prints stack trace and returns an array based on the stream of null/empty list. We check for an empty/null array to indicate failure to read.
        assertNotNull(result);
        // Since IOException is caught and printed, the list remains empty.
        assertEquals(0, result.length, "Should return an empty array when the file does not exist.");
    }
}
```
Three methods were generated that include:
- a normal test
- an empty file test
- a non-existing file

The JUnit way of testing with temporary directory is not used.  
We do run into exception, and we achieve MC/DC.  
This has been the only model so far to cover both empty file and non-existing file.  
It has written in binary to temporary files. this was unnecessary.  
Using *@BeforeEach* and *@AfterEach* annotations for temporary files were unique.

#### NameSearch Unit
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dmst.seip.angor.junitdemo.io.FileIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for the NameSearch class.
 * Must end with TestGemma4_1
 */
public class NameSearchTest {

    private NameSearch nameSearch;
    private FileIO fileIO;
    private Path tempFile; // Path to the temporary test file

    @BeforeEach
    void setUp() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        // Create a temporary file for testing purposes
        tempFile = Files.createTempFile("search_input", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary file
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testFindNamesStartingWithPrefix() throws IOException {
        // Setup: Write known content to the temporary file
        String content = "Alice Smith\nBob Johnson\nAlicia Green\nCharles Brown";

        Files.write(tempFile, content.getBytes());

        // Execution: Search for names starting with 'A'
        String prefix = "A";
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, prefix);

        // Assertions
        assertNotNull(result);
        // We expect "Alice Smith" and "Alicia Green"
        Set<String> expectedNames = new HashSet<>(Arrays.asList("Alice Smith", "Alicia Green"));
        Set<String> actualNames = new HashSet<>(Arrays.asList(result));
        
        assertEquals(2, result.length, "Should find exactly 2 names starting with 'A'.");
        assertTrue(actualNames.containsAll(expectedNames) && expectedNames.containsAll(actualNames), "The found names should match the expected names.");
    }

    @Test
    void testFindNamesNoMatches() throws IOException {
        // Setup: Write content
        String content = "John Doe\nJane Doe";

        Files.write(tempFile, content.getBytes());

        // Execution: Search for names starting with 'Z'
        String prefix = "Z";
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, prefix);

        // Assertions
        assertNotNull(result);
        assertEquals(0, result.length, "Should find no names starting with 'Z'.");
    }

    @Test
    void testFindNamesEmptyFile() throws IOException {
        // Setup: Create an empty temporary file
        Files.write(tempFile, "".getBytes());

        // Execution: Search for anything
        String prefix = "A";
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, prefix);

        // Assertions
        assertNotNull(result);
        assertEquals(0, result.length, "Should return an empty array for an empty input file.");
    }
}
```
Three methods are generated which include:
- a normal test
- a test without result
- an empty file test

The test achieves MC/DC.  
So far it has been the only model to generate an empty file test.

#### Conclusion
This model has been the edge case champion so far! (had tested two more edge cases)
It was a hassle for me to fix the wrongly indented files.
More boundary values testing is needed.  
method names do comply with what they test, and they also start with test.
some methods had multiple assertions.  
private variables are used instead of instantiating needed units in each method.

### Qwen-Coder2.5:7b
This model acted very weirdly. it combined MyMath and MyAdvancedMath unit tests into one single
file. It did not test FileIO at all! It generated the test files,
and I had to manually create  them and put them in the right directory.
This model also only generated files and did not have any other context or thought process.
Output was just two files!  
I double-checked to verify that I had all four files linked to the prompt.
#### MyMath & MyAdvancedMath Units
```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dmst.seip.angor.junitdemo.math.MyMath;
import dmst.seip.angor.junitdemo.math.MyAdvancedMath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyMathTestQwen2_5_1 {
    private MyMath myMath;
    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    public void setUp() {
        myMath = new MyMath();
        myAdvancedMath = new MyAdvancedMath();
    }

    @Test
    public void testCheckSignPositiveNumber() {
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    public void testCheckSignNegativeNumber() {
        assertEquals("negative", myMath.checkSign(-20));
    }

    @Test
    public void testCheckSignZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    public void testReverseNumber() {
        assertEquals(-15, myMath.reverseNumber(15));
    }

    @Test
    public void testAddPositiveNumbers() {
        int result = myAdvancedMath.add(10, 20);
        assertEquals(30, result);
    }

    @Test
    public void testAddNegativeNumbersThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-5, -10));
    }

    @Test
    public void testAddIntegerOverflowThrowsException() {
        int maxInt = Integer.MAX_VALUE;
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(maxInt, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositiveNumber() {
        assertEquals(16, myAdvancedMath.powerOfTwo(4));
    }

    @Test
    public void testPowerOfTwoNegativeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-2));
    }

    @Test
    public void testPowerOfTwoTooLargeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }
}

```
I can already tell this has been the worst generated test yet.

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 10 and -20.

##### *reverseNumber(int)* method
Only a single method was generated which is just a normal negative number.

The tests have MC/DC. I mean this method is only a single assignment.  
This needs many more tests.

##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- negative x and y
- overflow test

Tests have Condition/Decision coverage.
boundary values are neglected and even there is no MC/DC.

##### *powerOfTwo(int)* method
Four methods were generated. The tests include:
- a negative power test
- the lower boundary
- a normal test
- just above the upper boundary

We have MC/DC.  
boundary values are not tested.  
instead of -2, -1 should have been tested.

##### *reverseArrayNumbers(int[], MyMath)* method
This method is not tested at all!

#### FileIO Unit
This unit is not tested!

#### NameSearch Unit
```java
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import dmst.seip.angor.junitdemo.io.FileIO;
import dmst.seip.angor.junitdemo.search.NameSearch;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NameSearchTestQwen2_5_1 {
    private FileIO fileIo;
    private NameSearch nameSearch;

    @BeforeEach
    public void setUp() {
        fileIo = new FileIO();
        nameSearch = new NameSearch();
    }

    @Test
    public void testFindNamesInFileThatStartWithPrefix() {
        String[] names = {"Alice", "Bob", "Charlie"};
        File tempFile = createTempFile("test.txt", Arrays.toString(names));
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIo, "A");

        assertArrayEquals(new String[]{"Alice"}, result);
    }

    private File createTempFile(String fileName, String content) {
        // Logic to create a temporary file with the given content
        return new File(fileName);
    }
}

```
A single method for testing is generated which includes a normal test.
It looks like it doesn't write the content to the file! this test is just wrong.  
It also doesn't remove the temporary file.

#### Conclusion
This model did horrible. I believe I have explained enough.
method names do comply with what they test, and they also start with test.  
Many edge cases have not been tested.
private variables are used.

### Qwen-Coder3:30b
Final model to test. It has the highest parameters between local models, so expectations
are high. I had to manually create the files. this was mostly the case for local models.
This is more likely a problem with Ollama provider.  
Also, I love the fact that it included @author while the author is the chatbot!

#### MyMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyMath
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class MyMathTestQwen3_1 {

    private MyMath myMath = new MyMath();

    @Test
    public void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    public void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    public void testCheckSignZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myMath.checkSign(0));
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    public void testReverseNumberPositive() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        assertEquals(3, myMath.reverseNumber(-3));
    }

    @Test
    public void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 5 and -3. these numbers are closer to
the boundary compared to previous models.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 5 and -3.

#### MyAdvancedMath Unit
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyAdvancedMath
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class MyAdvancedMathTestQwen3_1 {

    private MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private MyMath myMath = new MyMath();

    @Test
    public void testAddPositiveNumbers() {
        assertEquals(8, myAdvancedMath.add(3, 5));
    }

    @Test
    public void testAddNegativeNumbers() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.add(-3, 5));
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    public void testAddOverflow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    public void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoValid() {
        assertEquals(8, myAdvancedMath.powerOfTwo(3));
    }

    @Test
    public void testPowerOfTwoNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.powerOfTwo(-1));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    public void testPowerOfTwoTooLarge() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.powerOfTwo(31));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    public void testReverseArrayNumbers() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- negative x and positive y
- overflow test

Tests have decision coverage. y is never negative.  
boundary values are not tested.

##### *powerOfTwo(int)* method
Four methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- a normal test
- just above the upper boundary

We have MC/DC.  
boundary values are somewhat tested.

##### *reverseArrayNumbers(int[], MyMath)* method
This is the only model with two tests for this method. it covers a normal test and an empty
array test.

#### FileIO Unit
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Test class for FileIO
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class FileIOTestQwen3_1 {

    private FileIO fileIO = new FileIO();

    @Test
    public void testReadFile() throws IOException {
        // Create a temporary file for testing
        File tempFile = File.createTempFile("test", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n");
        writer.write("Bob\n");
        writer.write("Charlie\n");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Bob", result[1]);
        assertEquals("Charlie", result[2]);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        File tempFile = File.createTempFile("testEmpty", ".txt");
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertEquals(0, result.length);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testReadFileNonExistent() {
        // Test with a non-existent file - should not throw exception but return empty array
        String[] result = fileIO.readFile("non_existent_file.txt");
        assertEquals(0, result.length);
    }
}
```
Three methods were generated that include:
- a normal test
- an empty file test
- a non-existing file

Temporary directory could have been tested with @TempDir annotation.

#### NameSearch Unit
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmst.seip.angor.junitdemo.io.FileIO;

/**
 * Test class for NameSearch
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class NameSearchTestQwen3_1 {

  private NameSearch nameSearch = new NameSearch();
  private FileIO fileIO = new FileIO();

  @Test
  public void testFindNamesInFileThatStartWith() throws IOException {
    // Create a temporary file for testing
    File tempFile = File.createTempFile("names", ".txt");
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\n");
    writer.write("Bob\n");
    writer.write("Charlie\n");
    writer.write("Amanda\n");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "A");

    assertEquals(2, result.length);
    assertEquals("Alice", result[0]);
    assertEquals("Amanda", result[1]);

    // Clean up
    tempFile.delete();
  }

  @Test
  public void testFindNamesInFileThatStartWithNoMatch() throws IOException {
    // Create a temporary file for testing
    File tempFile = File.createTempFile("names", ".txt");
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\n");
    writer.write("Bob\n");
    writer.write("Charlie\n");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "Z");

    assertEquals(0, result.length);

    // Clean up
    tempFile.delete();
  }

  @Test
  public void testFindNamesInFileThatStartWithEmptyFile() throws IOException {
    // Create a temporary empty file for testing
    File tempFile = File.createTempFile("names", ".txt");

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "A");

    assertEquals(0, result.length);

    // Clean up
    tempFile.delete();
  }
}
```
Three methods are generated which include:
- a normal test
- a test without result
- an empty file test

The test achieves MC/DC.  
some edge cases like empty prefix are not tested.

#### Conclusion
Model did okay.
It needs more boundary values  
method names do comply with what they test, and they do start with test.  
More edge cases can be tested although this model did the best.
private variables were used.

### Local models comparison
All models were equal with MyMath unit test other than qwen 2.5, which only tested
for a negative normal number in reverse method.

For MyAdvancedMath, with handling more cases, Qwen3 is the winner while its kin
Qwen2.5 did the worst.  

For FileIO and NameSearch units, Gemma4 and Qwen3 are the winners.  

If I were to rank these models, here is how it would look:
1. Qwen 3 Coder 30b
2. Gemma4 e4b
3. GPT OSS 20b
4. Gemma3 12b
5. Qwen 2.5 Coder 7b


## Round 2
Now that round 1 is finished. I will expand the prompt into a  more complex one based on the previous round's
generated tests flaws. All models did poorly with boundary values testing,
so the main focus of this prompt is that, while also
trying to reduce the amount of tests. many of the models had multiple assertions in them, so I'll also
prompt the agents to use one assertion per method. Only a single model used JUnit's temporary storage,
so that would also be included. I also want the models to create tests for edge cases.
> For these units, generate unit test using JUnit5. do not use mock for dependencies.
> focus on boundary values and replace them with normal tests (e.g. if 0 is a boundary, test
> -1, 0 and 1). Try to use only one assertion per method. Use JUnit's @TempDir for temporary
> storage. make sure to test many edge cases like empty arrays or strings. the generated
> test classes must end with "Test(ModelName)_P2".

### Mistral Devstral Medium
The agent generated test files and put them in the test directory.

#### MyMath Unit
```java
public class MyMathTestMistral_P2 {

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    public void testCheckSignZero() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    public void testReverseNumberPositive() {
        MyMath myMath = new MyMath();
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        MyMath myMath = new MyMath();
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        MyMath myMath = new MyMath();
        assertEquals(0, myMath.reverseNumber(0));
    }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.
This is exactly what I was expecting.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
Well for this function there is no boundary value checking, and
basically, it's the same with the previous round

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestMistral_P2 {

    @Test
    public void testAddPositiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    public void testAddNegativeNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    }

    @Test
    public void testAddOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositive() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(8, advancedMath.powerOfTwo(3));
    }

    @Test
    public void testPowerOfTwoNegative() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    public void testPowerOfTwoOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {};
        int[] expected = {};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(array, myMath));
    }
}

```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- x is just below the boundary and positive y
- overflow test

Tests have decision coverage. y is never negative.  
boundary values are not tested enough!  
many more tests probably should have been included.  
surprisingly, agent did worse that previous round for this method.

##### *powerOfTwo(int)* method
Four methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- a normal test
- just above the upper boundary

We have MC/DC.  
In the previous round, agent covered the upper boundary and just above the lower boundary,
but it didn't cover those here. although the lower boundary is covered here and there are fewer
redundancies.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods were generated. they include:
- a normal test
- an empty array

#### FileIO Unit
```java
public class FileIOTestMistral_P2 {

    @TempDir
    Path tempDir;

    @Test
    public void testReadFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "Line 1\nLine 2\nLine 3".getBytes());
        
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(testFile.toString());
        
        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    public void testReadEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, "".getBytes());
        
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(testFile.toString());
        
        assertEquals(0, content.length);
    }

    @Test
    public void testReadNonExistentFile() {
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile("nonexistent.txt");
        
        assertEquals(0, content.length);
    }
}

```
Three methods were generated that include:
- a normal test
- an empty file test
- a non-existing file

JUnit's Temporary storage has been used.  
Non-existing file test is new compared to the previous round and MC/DC is achieved.

#### NameSearch Unit
```java
public class NameSearchTestMistral_P2 {

    @TempDir
    Path tempDir;

    @Test
    public void testFindNamesInFileThatStartWith() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna\nAndrew\nEve".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "A");
        
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyPrefix() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "");
        
        assertEquals(3, result.length);
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "Z");
        
        assertEquals(0, result.length);
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, "".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "A");
        
        assertEquals(0, result.length);
    }
}
```
four methods are generated which include:
- a normal test
- a test without result
- an empty file test
- an empty prefix test

The test achieves MC/DC.  
I believe these tests are enough.

#### Conclusion
There are definitely some improvements. I believe FileIO and NameSearch don't really need improvement.
MyAdvancedMath tests can be better. It did not use private variables though.


### Gemini 3 Flash
The agent generated the test files and put them in the test directory which is correct.
#### MyMath Unit
```java
public class MyMathTestGemini_P2 {

    private final MyMath mm = new MyMath();

    @Test
    public void testCheckSignPositive() {
        Assertions.assertEquals("positive", mm.checkSign(1));
    }

    @Test
    public void testCheckSignPositiveMoreThanOne() {
        Assertions.assertEquals("positive", mm.checkSign(2));
    }

    @Test
    public void testCheckSignNegative() {
        Assertions.assertEquals("negative", mm.checkSign(-1));
    }

    @Test
    public void testCheckSignNegativeLessThanMinusOne() {
        Assertions.assertEquals("negative", mm.checkSign(-2));
    }

    @Test
    public void testCheckSignZeroThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    public void testReverseNumberPositive() {
        Assertions.assertEquals(-5, mm.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        Assertions.assertEquals(5, mm.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        Assertions.assertEquals(0, mm.reverseNumber(0));
    }
}

```
Unlike mistral, gemini used a private variable for unit Again!
##### *checkSign(int)* method
five methods were generated that include:
- a normal positive number
- a normal negative number
- just below the boundary
- just above the boundary
- zero (boundary)

The tests have MC/DC.  
The only improvement that comes to my mind for these tests is parametrized tests.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 5 and -5.  
It did the same compared to the previous round.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestGemini_P2 {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @Test
    public void testAddBothZero() {
        Assertions.assertEquals(0, mam.add(0, 0));
    }

    @Test
    public void testAddOneZeroFirst() {
        Assertions.assertEquals(5, mam.add(0, 5));
    }

    @Test
    public void testAddOneZeroSecond() {
        Assertions.assertEquals(5, mam.add(5, 0));
    }

    @Test
    public void testAddNormalPositive() {
        Assertions.assertEquals(10, mam.add(4, 6));
    }

    @Test
    public void testAddNegativeXThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(-1, 5));
    }

    @Test
    public void testAddNegativeYThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(5, -1));
    }

    @Test
    public void testAddOverflowBoundary() {
        Assertions.assertEquals(Integer.MAX_VALUE, mam.add(Integer.MAX_VALUE, 0));
    }

    @Test
    public void testAddOverflowThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        Assertions.assertEquals(1, mam.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoOne() {
        Assertions.assertEquals(2, mam.powerOfTwo(1));
    }

    @Test
    public void testPowerOfTwoThirty() {
        Assertions.assertEquals(1073741824, mam.powerOfTwo(30));
    }

    @Test
    public void testPowerOfTwoNegativeThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(-1));
    }

    @Test
    public void testPowerOfTwoThirtyOneThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(31));
    }

    @Test
    public void testReverseArrayNumbersNormal() {
        MyMath mm = new MyMath();
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        MyMath mm = new MyMath();
        int[] input = {};
        int[] expected = {};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }
}
```
I can tell there has been a massive improvement.
##### *add(int, int)* method
Eight methods were generated that include:
- positive x and y
- positive x and negative y and vise versa
- both zeros
- x is zero and y is positive and vise versa
- just below the overflow
- overflow test

Tests do have MC/DC.  
There could have been more boundary value testing for sure.

##### *powerOfTwo(int)* method
Five methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- the upper boundary
- just above the upper boundary

We have MC/DC.  
boundary values are tested very well.  
Interestingly compared to the last round, just a redundant test is removed.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated that include:
- a normal test
- an empty array test

The empty array test was not generated in the previous round.  
MC/DC is achieved.

#### FileIO Unit
```java
public class FileIOTestGemini_P2 {

  private final FileIO fileIO = new FileIO();

  @Test
  public void testReadFileNormal(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("test.txt");
    List<String> expectedLines = Arrays.asList("Alice", "Bob", "Charlie");
    Files.write(tempFile, expectedLines);

    String[] result = fileIO.readFile(tempFile.toString());
    Assertions.assertArrayEquals(expectedLines.toArray(new String[0]), result);
  }

  @Test
  public void testReadFileEmpty(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("empty.txt");
    Files.createFile(tempFile);

    String[] result = fileIO.readFile(tempFile.toString());
    Assertions.assertEquals(0, result.length);
  }

  @Test
  public void testReadNonExistentFile() {
    // The implementation catches IOException and prints stack trace, returning empty array.
    String[] result = fileIO.readFile("non_existent_file.txt");
    Assertions.assertEquals(0, result.length);
  }
}
```
Three methods were generated that include:
- a normal test
- an empty file test
- a non-existing file

We run into exception, thus we achieve MC/DC.  
compared to the last round, empty file test has been added.

#### NameSearch Unit
```java
public class NameSearchTestGemini_P2 {

  private final NameSearch nameSearch = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  public void testFindNamesPrefixed(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("names.txt");
    List<String> names = Arrays.asList("Alice", "Bob", "Anna", "Alex");
    Files.write(tempFile, names);

    String[] expected = {"Alice", "Anna", "Alex"};
    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

    Assertions.assertArrayEquals(expected, result);
  }

  @Test
  public void testFindNamesNoMatch(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("names.txt");
    List<String> names = Arrays.asList("Alice", "Bob", "Anna");
    Files.write(tempFile, names);

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Z");

    Assertions.assertEquals(0, result.length);
  }

  @Test
  public void testFindNamesEmptyFile(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("empty.txt");
    Files.createFile(tempFile);

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

    Assertions.assertEquals(0, result.length);
  }

  @Test
  public void testFindNamesEmptyPrefix(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("names.txt");
    List<String> names = Arrays.asList("Alice", "Bob");
    Files.write(tempFile, names);

    // Every string starts with empty string
    String[] expected = {"Alice", "Bob"};
    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "");

    Assertions.assertArrayEquals(expected, result);
  }
}
```
Four methods with multiple assertions are generated which include:
- a normal test
- a test without result
- empty file test
- empty prefix test

The test achieves MC/DC.  
compared to the previous round, empty file and empty prefix tests have been added.

#### Conclusion
Definitely, improvements can be seen.
method names do comply with what they test, and they also start with test.  
Edge cases are tested well.

### Devstral vs Gemini
Now that we have seen the generated tests of our two large models, we can compare them.  
Gemini definitely did better. let's cover the differences:
- Gemini had a normal test for both positive and negative number for checkSing (although it was unnecessary).
- Gemini covers more scenarios for add method.
- Gemini covers two more scenarios for powerOfTwo methods.
- Gemini used private variables.

Yeah, Gemini dominated this time.

### Gemma3:12b
I should mention that I just have installed all 5 local models on my computer and I tested them
with my own hardware. I sent the prompt, and it created the file in the test folder. A problem
was that it generated the files, but it took it a long time to create them?
#### MyMath Unit
```java
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
```
Unlike last time, it is using private variables now.
##### *checkSign(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.  
It's good enough. It covered the last round flaw.

##### *reverseNumber(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (at the boundary)

The tests have MC/DC.  
unlike large models, it tested all boundary values for this method.

#### MyAdvancedMath Unit
```java
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
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- x is just below the boundary and positive y
- overflow test

since *y < 0* is never true in tests, we don't have MC/DC and condition coverage, instead
we have decision coverage.
It did poorly. Three tests are not enough.  
There is just a slight improvement compared to the last round of testing(-1, 2)

##### *powerOfTwo(int)* method
five methods were generated that include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- the upper boundary
- just above the boundary

We have MC/DC.  
boundary values are tested well.  
There is no redundant test.  
Just above the lower boundary and the upper boundary are new compared to the last round.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated that include:
- a normal test
- an empty array test
- a single element array test

The Tested methods have MC/DC.
The last two tests are new compared to the previous round.

#### FileIO Unit
```java
public class FileIOTestGemma3_P2 {

    private FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile() throws IOException {
        Path tempFile = Files.createTempFile("empty");
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[0], content);
    }

    @Test
    void readFile_singleLineFile() throws IOException {
        Path tempFile = Files.createTempFile("single");
        Files.write(tempFile, "single line".getBytes());
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[]{"single line"}, content);
    }

    @Test
    void readFile_multipleLinesFile() throws IOException {
        Path tempFile = Files.createTempFile("multiple");
        Files.write(tempFile, "''\n                line1\n                line2\n                line3\n                ''".getBytes());
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[]{"line1", "line2", "line3"}, content);
    }
}
```
Three methods were generated that include:
- a normal test
- an empty file
- a single line file

We don't have statement coverage since we don't run into exception.  
non-existing file edge case is not covered.  
The last test is actually wrong because FileIO does not ignore white spaces!  
by ignoring the last test which is has a wrong assertion, there was no improvement to the last round!

#### NameSearch Unit
```java
public class NameSearchTestGemma3_P2 {

    private NameSearch nameSearch = new NameSearch();
    private FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_emptyFile() {
        Path tempFile = Files.createTempFile("empty");
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() {
        Path tempFile = Files.createTempFile("noMatch");
        Files.write(tempFile, "''\n                line1\n                line2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_singleMatchingPrefix() {
        Path tempFile = Files.createTempFile("singleMatch");
        Files.write(tempFile, "''\n                Aline1\n                line2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[]{"Aline1"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatchingPrefix() {
        Path tempFile = Files.createTempFile("multipleMatch");
        Files.write(tempFile, "''\n                Aline1\n                Aline2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[]{"Aline1", "Aline2"}, result);
    }
}
```
Four methods are generated which include:
- an empty file test
- a test without result
- a single result test
- a multiple results test

The test achieves MC/DC.  
empty prefix edge case is not covered.  
I don't understand why the agent writes white spaces in the temporary files, and forgets about them in
the assertion. at least a multiple results test and an empty file test are new compared to the last round.

#### Conclusion
It was the first local model I tested.
The test methods do not start with test.
definitely, there are some improvements with boundary values testing but not enough.


### Gemma4:e4b
Gemma4 continued to annoy me. It stopped generating output twice, I had to send
"continue" prompt. It struggled with creating the files that second time when it stopped,
I said to it to just generate the test files, I would manually create their files.
from this point, for each small model I will prompt it to only generate the test file and to not create the
file, I will manually create them.

#### MyMath Unit
```java
public class MyMathTestGemma4_P2 {

    private final MyMath myMath = new MyMath();

    // --- Tests for checkSign(int number) ---

    @Test
    void checkSign_PositiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_NegativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_BoundaryZeroMinusOne() {
        // Testing -1 (should be negative)
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_BoundaryZero() {
        // Testing 0 (should throw exception)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void checkSign_BoundaryOne() {
        // Testing 1 (should be positive)
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_LargePositiveNumber() {
        assertEquals("positive", myMath.checkSign(Integer.MAX_VALUE));
    }

    @Test
    void checkSign_LargeNegativeNumber() {
        assertEquals("negative", myMath.checkSign(Integer.MIN_VALUE));
    }

    // --- Tests for reverseNumber(int number) ---

    @Test
    void reverseNumber_PositiveNumber() {
        assertEquals(-123, myMath.reverseNumber(123));
    }

    @Test
    void reverseNumber_NegativeNumber() {
        assertEquals(456, myMath.reverseNumber(-456));
    }

    @Test
    void reverseNumber_Zero() {
        assertEquals(0, myMath.reverseNumber(0));
    }

    @Test
    void reverseNumber_One() {
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    void reverseNumber_BoundaryMinInt() {
        // Reversing Integer.MIN_VALUE = -2147483648 results in 2147483648 which overflows int.
        // The current implementation will wrap around, testing for predictable wrapping behavior or the expected result if it were handled.
        // Since the method returns int, we test the predictable result of the unary negation on the extreme boundary case.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}
```

##### *checkSign(int)* method
Five methods were generated that include:
- just below the boundary
- zero (boundary)
- just above the boundary
- Integer.MAX_VALUE
- Integer.MIN_VALUE


The tests have MC/DC.  
The last two tests are unnecessary, in my opinion.  
Still, it has improved.

##### *reverseNumber(int)* method
Five methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)
- just above the boundary
- Integer.MIN_VALUE

The tests have MC/DC.  
it is the only model to test the Integer.MIN_VALUE edge case.  
Definitely improved compared to the previous round. although I'm not pleased with
testing normal values instead of boundary values. I mentioned this in the prompt.

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
This was the most different *add* method test so far!  
Seven methods were generated that include:
- positive x and y
- x and y both 1. twice!
- near overflow test
- x is -1 and y is 5 and vice versa
- overflow test with adding two *Integer.MAX_VALUE / 2 + 1*

Tests do have MC/DC.  
Between first three tests, two of them are redundant.  
There was an attempt for boundary value testing.  
Exception message is asserted which is unnecessary.  
still improved compared to the previous round.

##### *powerOfTwo(int)* method
Six methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- two normal tests
- the upper boundary
- just above the upper boundary

We have MC/DC.  
boundary values are tested.  
Normal tests are redundant.  
Still, this was quite an improvement than the previous round.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated. they include:
- two normal tests
- an empty array

MC/DC is achieved.
a normal test is redundant.  
The only improvement is the empty array test.

#### FileIO Unit
```java
public class FileIOTestGemma4_P2 {

    private FileIO fileIO = new FileIO();

    @Test
    void readFile_ReadsFileWithMultipleLines() throws Exception {
        // Setup: Create a temporary file
        Path tempFile = tempDir.resolve("test_multi.txt");
        java.nio.file.Files.writeString(tempFile, "Line One\nLine Two\nLine Three");

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertion: Expect 3 lines
        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"Line One", "Line Two", "Line Three"}, result);
    }

    @Test
    void readFile_ReadsEmptyFile() throws Exception {
        // Setup: Create an empty temporary file
        Path tempFile = tempDir.resolve("test_empty.txt");
        java.nio.file.Files.createFile(tempFile);

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertion: Expect an empty array
        assertArrayEquals(new String[0], result);
    }

    @Test
    void readFile_FileDoesNotExist() {
        // Setup: Use a path that definitely does not exist
        String nonExistentPath = tempDir.resolve("non_existent_file_xyz123.txt").toString();

        // Execution & Assertion: Expect an empty array because IOException is caught and prints stack trace
        String[] result = fileIO.readFile(nonExistentPath);

        // In case of non-existence, the catch block executes, resulting in an array of size 0 or unexpected content depending on runtime state.
        // For robust testing adhering to the method's contract (returning String[]), we assert the size based on the IOException handling.
        assertEquals(0, result.length, "Should return an empty array when file is not found.");
    }
}
```
Three methods were generated that include:
- a normal test
- an empty file test
- a non-existing file

MC/DC is achieved.  
It did not use JUnit's temporary file.  
It's basically the same compared to the previous round.

#### NameSearch Unit
```java
public class NameSearchTestGemma4_P2 {

    private FileIO fileIO = new FileIO();
    private NameSearch nameSearch = new NameSearch();

    // --- Test Cases ---

    @Test
    void findNamesInFileThatStartWith_MultipleMatches() throws Exception {
        // Setup: Create a temporary file with multiple matches
        Path tempFile = tempDir.resolve("names_match.txt");
        String content = "Alice Smith\nAndrew Johnson\nBob Green\nAlicia Brown";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Ali");

        // Assertion: Expect 3 matches
        assertArrayEquals(new String[]{"Alice Smith", "Andrew Johnson", "Alicia Brown"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_NoMatches() throws Exception {
        // Setup: Create a temporary file with no matching names
        Path tempFile = tempDir.resolve("names_no_match.txt");
        String content = "David Lee\nEva Green\nFrank White";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Xyz");

        // Assertion: Expect an empty array
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyPrefix() throws Exception {
        // Setup: Create a temporary file
        Path tempFile = tempDir.resolve("names_empty_prefix.txt");
        String content = "Apple\nBanana\nCherry";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "");

        // Assertion: Empty prefix should match all names
        assertArrayEquals(new String[]{"Apple", "Banana", "Cherry"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyFile() throws Exception {
        // Setup: Create an empty temporary file
        Path tempFile = tempDir.resolve("names_empty_file.txt");
        java.nio.file.Files.createFile(tempFile);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

        // Assertion: Should return an empty array
        assertArrayEquals(new String[0], result);
    }
}
```
Four methods are generated which include:
- a normal test
- a test without result
- an empty file test
- a test with empty prefix

The test achieves MC/DC.  
The only improvement compared to the previous model is the empty prefix test.


#### Conclusion
There were many redundancies, especially in *MyAdvancedMath* unit.
The test methods do not start with test!  
method names do comply with what they test.  
Exception messages were asserted.

### GPT-OSS:20b
From now on, I will ask the local models to only generate the test files and to not create the files.
This is a significant boost to output even though this model is larger.
#### MyMath Unit
```java
class MyMathTestGPT_OSS_P2 {

    private final MyMath math = new MyMath();

    @Test
    void checkSign_negativeNumber() {
        assertEquals("negative", math.checkSign(-1));
    }

    @Test
    void checkSign_zeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void checkSign_positiveNumber() {
        assertEquals("positive", math.checkSign(1));
    }

    @Test
    void reverseNumber_negative() {
        assertEquals(1, math.reverseNumber(-1));
    }

    @Test
    void reverseNumber_zero() {
        assertEquals(0, math.reverseNumber(0));
    }

    @Test
    void reverseNumber_positive() {
        assertEquals(-1, math.reverseNumber(1));
    }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.  
This is exactly what I wanted with this prompt.

##### *reverseNumber(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (at the boundary)

The tests have MC/DC.  
the Integer.MIN_VALUE edge case is missing.  
It did cover the previous round flaw.

#### MyAdvancedMath Unit
```java
class MyAdvancedMathTestGPT_OSS_P2 {

    private final MyAdvancedMath adv = new MyAdvancedMath();

    @Test
    void add_validInputs() {
        assertEquals(5, adv.add(2, 3));
    }

    @Test
    void add_negativeInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> adv.add(-1, 5));
    }

    @Test
    void add_overflowThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void powerOfTwo_zero() {
        assertEquals(1, adv.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_one() {
        assertEquals(2, adv.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_maxBoundary() {
        assertEquals(1073741824, adv.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_negativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_aboveMaxThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        assertArrayEquals(new int[0],
                adv.reverseArrayNumbers(new int[0], new MyMath()));
    }

    @Test
    void reverseArrayNumbers_mixedValues() {
        int[] arr = {1, -2, 0};
        assertArrayEquals(new int[]{-1, 2, 0},
                adv.reverseArrayNumbers(arr, new MyMath()));
    }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- y is -1 and x is positive
- overflow test

It actually performed worse than the previous round!  
We have lost the MC/DC since x is never negative.

##### *powerOfTwo(int)* method
Five methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- just above the boundary
- the upper boundary
- just above the upper boundary

We have MC/DC.  
boundary values are tested well enough.  
It's quite an improvement to the last round.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated which include:
- empty array test
- a normal test

MC/DC is achieved.  
the only improvement is empty array test.

#### FileIO Unit
```java
class FileIOTestGPT_OSS_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[0], fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_singleLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.writeString(file, "Hello");
        assertArrayEquals(new String[]{"Hello"}, fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_multipleLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.write(file, ("Line1\nLine2\nLine3").getBytes());
        assertArrayEquals(new String[]{"Line1", "Line2", "Line3"},
                fileIO.readFile(file.toString()));
    }
}
```
Three methods were generated that include:
- an empty file
- a file with a single line
- a file with multiple lines

Non-existing test case is missing, this was covered previously. This has resulted in
losing the MC/DC. Still empty file, and the single line file tests are new.

#### NameSearch Unit
```java
class NameSearchTestGPT_OSS_P2 {

    private final NameSearch search = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_noMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Alice\nBob\nCharlie");
        assertArrayEquals(new String[0],
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "Z"));
    }

    @Test
    void findNamesInFileThatStartWith_singleMatch(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Alice\nBob\nAlbert");
        assertArrayEquals(new String[]{"Alice", "Albert"},
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Ann\nAndy\nBob\nAaron");
        assertArrayEquals(new String[]{"Ann", "Andy", "Aaron"},
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[0],
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }
}
```
Four methods with multiple are generated which include:
- a test without result.
- a test with a single result
- a test with multiple results
- an empty file test.

The test achieves MC/DC.  
Empty prefix test case is missing.  
It has improved compared to last time.

#### Conclusion
Definitely more test methods could have been generated, especially for *MyAdvancedMath* unit.  
method names do comply with what they test, but they don't start with test.  
Some edge cases have not been tested. Still, this is an improvement.
It did use private variables.

### Qwen-Coder2.5:7b
Qwen again acted weirdly. I gave it the same prompt, but it only generated unit test
for math units. I had to send the prompt "You did not generate tests for
FileIO and NameSearch. Generate them.", to generate two other unit tests.
#### MyMath Unit
```java
public class MyMathTestQwen2_5_P2 {

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    public void testCheckSignZeroThrowsException() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0), "Expected IllegalArgumentException for zero");
    }

    @Test
    public void testReverseNumberPositive() {
        MyMath myMath = new MyMath();
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    public void testReverseNumberNegative() {
        MyMath myMath = new MyMath();
        assertEquals(1, myMath.reverseNumber(-1));
    }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- just below the boundary
- just above the boundary
- zero (boundary)

The tests have MC/DC.  
It has improved for sure.

##### *reverseNumber(int)* method
Two methods were generated which include:
- just above the boundary
- just below the boundary

MC/DC for a single statement method is achieved.  
Unlike other models, there is no zero test.  
Slight improvement by replacing boundary value testing instead of normal values.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestQwen2_5_P2 {

    @Test
    public void testAddPositiveNumbers() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(3, myAdvancedMath.add(1, 2));
    }

    @Test
    public void testAddNegativeNumberThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 2), "Expected IllegalArgumentException for negative number");
    }

    @Test
    public void testAddOverflowThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1), "Expected IllegalArgumentException for overflow");
    }

    @Test
    public void testPowerOfTwoZero() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositive() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(256, myAdvancedMath.powerOfTwo(8));
    }

    @Test
    public void testPowerOfTwoNegativeThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1), "Expected IllegalArgumentException for negative power");
    }

    @Test
    public void testPowerOfTwoTooLargeThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31), "Expected IllegalArgumentException for too large power");
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1, -2, 3, -4};
        int[] expectedOutputArray = {-1, 2, -3, 4};
        int[] resultArray = myAdvancedMath.reverseArrayNumbers(inputArray, myAdvancedMath);
        assertArrayEquals(expectedOutputArray, resultArray);
    }
}
```
Again, this model did the worst compared to other models.

##### *add(int, int)* method
Three methods were generated that include:
- positive x and y
- negative x and positive y
- overflow test

Tests have decision coverage.  
There could have been more tests for this method.  
Not quite have been changed compared to the previous round.

##### *powerOfTwo(int)* method
Four methods were generated. The tests include:
- just below the boundary
- the lower boundary
- a normal test
- just above the upper boundary

We have MC/DC.  
more boundary values could have been tested.
The only improvement is testing -1 instead of a normal negative number.

##### *reverseArrayNumbers(int[], MyMath)* method
only a single method is generated which includes a normal test.
hey, it's an improvement. For the last time, agent didn't even test this method.

#### FileIO Unit
```java
public class FileIOTestQwen2_5_P2 {

    @Test
    public void testReadFileWithValidFilePath() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("test.txt");
            List<String> lines = Arrays.asList("line1", "line2", "line3");
            Files.write(filePath, lines);

            FileIO fileIO = new FileIO();

            // Act
            String[] result = fileIO.readFile(filePath.toString());

            // Assert
            assertArrayEquals(lines.toArray(new String[0]), result);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testReadFileWithEmptyFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();

        // Act & Assert
        assertThrows(IOException.class, () -> fileIO.readFile(""), "Expected IOException for empty file path");
    }

    @Test
    public void testReadFileWithNonexistentFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();
        Path nonExistentPath = Files.createTempDirectory("nonexistent").resolve("test.txt");

        // Act & Assert
        assertThrows(IOException.class, () -> fileIO.readFile(nonExistentPath.toString()), "Expected IOException for nonexistent file path");
    }
}
```
Three methods were generated that include:
- normal test
- empty file path
- non-existence file path

Interestingly, it generated an empty file path. no model has done this so far.  
MC/DC coverage is achieved.  
an empty file test case is missing.  
previously, this method was not tested.

#### NameSearch Unit
```java
public class NameSearchTestQwen2_5_P2 {

    @Test
    public void testFindNamesInFileThatStartWithValidPrefix() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");

            // Assert
            assertArrayEquals(new String[]{"Alice"}, result);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithMultiplePrefixes() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] resultA = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
            String[] resultB = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "B");

            // Assert
            assertArrayEquals(new String[]{"Alice"}, resultA);
            assertArrayEquals(new String[]{"Bob"}, resultB);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithNoPrefix() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");

            // Assert
            assertArrayEquals(names.toArray(new String[0]), result);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithNonexistentPrefix() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Z");

            // Assert
            assertTrue(result.length == 0);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        // Act & Assert
        assertThrows(IOException.class, () -> nameSearch.findNamesInFileThatStartWith("", fileIO, "A"), "Expected IOException for empty file path");
    }

    @Test
    public void testFindNamesInFileThatStartWithNonexistentFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();
        Path nonExistentPath = Files.createTempDirectory("nonexistent").resolve("names.txt");

        // Act & Assert
        assertThrows(IOException.class, () -> nameSearch.findNamesInFileThatStartWith(nonExistentPath.toString(), fileIO, "A"), "Expected IOException for nonexistent file path");
    }
}
```
Six methods are generated. they include:
- a test with single result
- a test with multiple results
- a test with empty prefix
- a test with no result
- an empty file test
- a non-existing file test

The empty prefix test is actually wrong! it will include all the names, not an empty
result.  
Empty file test and non-existing file test are already done in FileIO, and they are
completely redundant.
It's better than the last round.

#### Conclusion
This model did bad again. It generated fewer good tests than other models.
method names do comply with what they test, and they also start with test.
Some edge cases like empty files are not tested.
There are improvements, maybe just because last time this model did a
horrible job.

### Qwen-Coder3:30b
Previous round champion. let's see how this model does.  
It actually took some time to generate the result most likely for being a 30b
model and my PC not being able to handle this model properly.

#### MyMath Unit
```java
public class MyMathTestQwen3_P2 {

    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("Test checkSign with positive number")
    public void testCheckSignWithPositiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    @DisplayName("Test checkSign with negative number")
    public void testCheckSignWithNegativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    @DisplayName("Test checkSign with zero - should throw IllegalArgumentException")
    public void testCheckSignWithZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    @DisplayName("Test reverseNumber with positive number")
    public void testReverseNumberWithPositiveNumber() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    @DisplayName("Test reverseNumber with negative number")
    public void testReverseNumberWithNegativeNumber() {
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    @DisplayName("Test reverseNumber with zero")
    public void testReverseNumberWithZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
```

##### *checkSign(int)* method
Three methods were generated that include:
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.
Instead of normal values, it is testing the boundaries.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
I would have tested 1 and -1 instead of 5 and -5.  
there is no improvement compared to the previous round.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestQwen3_P2 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("Test add with normal positive numbers")
    public void testAddWithNormalNumbers() {
        assertEquals(10, myAdvancedMath.add(3, 7));
    }

    @Test
    @DisplayName("Test add with negative first number - should throw IllegalArgumentException")
    public void testAddWithNegativeFirstNumber() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
    }

    @Test
    @DisplayName("Test add with negative second number - should throw IllegalArgumentException")
    public void testAddWithNegativeSecondNumber() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(5, -1));
    }

    @Test
    @DisplayName("Test add with both negative numbers - should throw IllegalArgumentException")
    public void testAddWithBothNegativeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-2, -3));
    }

    @Test
    @DisplayName("Test add with overflow - should throw IllegalArgumentException")
    public void testAddWithOverflow() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("Test powerOfTwo with zero power")
    public void testPowerOfTwoWithZeroPower() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    @DisplayName("Test powerOfTwo with power 1")
    public void testPowerOfTwoWithPower1() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
    }

    @Test
    @DisplayName("Test powerOfTwo with maximum allowed power")
    public void testPowerOfTwoWithMaximumPower() {
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }

    @Test
    @DisplayName("Test powerOfTwo with negative power - should throw IllegalArgumentException")
    public void testPowerOfTwoWithNegativePower() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
    }

    @Test
    @DisplayName("Test powerOfTwo with power greater than 30 - should throw IllegalArgumentException")
    public void testPowerOfTwoWithPowerGreaterThan30() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with normal array")
    public void testReverseArrayNumbersWithNormalArray() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with empty array")
    public void testReverseArrayNumbersWithEmptyArray() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with single element")
    public void testReverseArrayNumbersWithSingleElement() {
        int[] input = {5};
        int[] expected = {-5};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with zero element")
    public void testReverseArrayNumbersWithZeroElement() {
        int[] input = {0};
        int[] expected = {0};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }
}
```
##### *add(int, int)* method
Five methods were generated that include:
- positive x and y
- positive x and negative y and vice versa
- negative x and y
- overflow test

MD/DC is achieved.  
It has improved compared to the previous round.

##### *powerOfTwo(int)* method
Five methods were generated. The tests include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- the upper boundary
- just above the upper boundary

We have MC/DC.  
boundary values are tested.  
It has improved with just above the lower boundary and the upper boundary tests.

##### *reverseArrayNumbers(int[], MyMath)* method
Four methods were generated that include:
- a normal test
- an empty array test
- an array with single element test
- an array with a single 0

I would say the last test is redundant. testing reverse of 0 is already done in
MyMath.
MC/DC is achieved.  
Empty array and single element arrays are improvement compared to the last round.

#### FileIO Unit
```java
public class FileIOTestQwen3_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    @DisplayName("Test readFile with normal file")
    public void testReadFileWithNormalFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("test.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {"Alice", "Bob", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with empty file")
    public void testReadFileWithEmptyFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary empty file
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with file with empty lines")
    public void testReadFileWithEmptyLines(@TempDir Path tempDir) throws IOException {
        // Create a temporary file with empty lines
        File tempFile = tempDir.resolve("withEmptyLines.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n\nBob\n\nCharlie");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {"Alice", "", "Bob", "", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with single line file")
    public void testReadFileWithSingleLineFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary file with single line
        File tempFile = tempDir.resolve("singleLine.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("SingleLine");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {"SingleLine"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with file that doesn't exist")
    public void testReadFileWithNonExistentFile(@TempDir Path tempDir) {
        // Try to read a file that doesn't exist
        String[] result = fileIO.readFile(tempDir.resolve("nonexistent.txt").toString());
        String[] expected = {};
        assertArrayEquals(expected, result);
    }
}
```
Five methods were generated that include:
- a normal test
- an empty file test
- a file with empty lines between content
- a single line file
- a non-existing file

Temporary directory is tested with @TempDir annotation.
Definitely an improvement compared to the last round.
MC/DC is achieved.

#### NameSearch Unit
```java
public class NameSearchTestQwen3_P2 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with matching names")
    public void testFindNamesInFileThatStartWithMatchingNames(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie\nAmanda\nBella");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice", "Amanda"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with no matching names")
    public void testFindNamesInFileThatStartWithNoMatchingNames(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with empty prefix")
    public void testFindNamesInFileThatStartWithEmptyPrefix(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "");
        String[] expected = {"Alice", "Bob", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with empty file")
    public void testFindNamesInFileThatStartWithEmptyFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary empty file
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with single name matching")
    public void testFindNamesInFileThatStartWithSingleNameMatching(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with case sensitive matching")
    public void testFindNamesInFileThatStartWithCaseSensitive(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("alice\nAlice\nALICE");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice"};
        assertArrayEquals(expected, result);
    }
}
```
Six methods are generated which include:
- a normal test
- a test without result
- an empty prefix test
- an empty file test
- a test with single result
- a case-sensitive test

The test achieves MC/DC.  
The last test is redundant since it's tests an of java library which is
already tested, and it's trusted.
It has improved compared to the last round with the single result test and the empty prefix test.

#### Conclusion
Model did well.
method names do comply with what they test, and they do start with test.
private variables were used.
Overall, this model did better than last round.

### Local models comparison
All models did the same with *checkSign method.  
For *reverseNumber*, only Gemma4 tested Integer.MIN_VALUE test case. Qwen 2.5 was the only model to
not test 0 for this method.  
For *add* method, GPT-OSS, Gemma3 and Qwen 2.5 did the same while Gemma4 and Qwen 3 included
more tests. Qwen 3 did even better.  
For *powerOfTwo* method, all models did the same but only Qwen 2.5 had fewer scenarios.  
In *reverseArray* method, Qwen 2.5 did the worst, GPT-OSS and Gemma4 were mid and Gemma3 and
Qwen 3 did the best.  
For *readFile* method, Qwen 2.5 and Gemma3 are last, then we have GPT-OSS and Gemma4. of course,
Qwen 3 did the best.  
Last but not least, for *NameSearch* unit, we can say other than Qwen 3, all models were equal and Qwen 3
did the best.  
And generally Qwen 3 did better. I'm surprised that GPT-OSS, Gemma4 and Qwen 2.5 did not use @TempDir even though
I had brought it up in the prompt.

here is my ranking:
1. Qwen 3 Coder 30b
2. Gemma4 e4b
3. GPT OSS 20b
4. Gemma3 12b
5. Qwen 2.5 Coder 7b

It's actually the same with the previous round!

## Round 3
This is the final round. I want the best result from our models. So let us utilize parametrized
testing and more robust boundary values testing. I will include achieving MC/DC, which is overkill
but all methods are not large, and it's easy to reach MC/DC. This round prompt is:
> For these units, generate unit test using JUnit5. do not use mock for dependencies.
> make sure to have a robust boundary values testing (test the boundary itself, just above
> it and just below it). Try to use only one assertion per method. Use JUnit's @TempDir
> for temporary storage. make sure to test many edge cases like empty arrays or strings.
> achieve MC/DC coverage. use parametrized tests for normal values. avoid generating redundant
> tests.  the generated test classes must end with "Test(modelName)_P3".

Let's try Mistral and Gemini, and compare them.

### Mistral Devstral Medium
I actually ran into some problem this round. It generated MyMath test, but it got stuck,
and it was not responsive for MyAdvancedMath, possibly duo to weak network connection.
stopped it from generating and I sent "continue from MyAdvancedMath unit".
it finished the job this time.
The agent generated test files, but it didn't put them in the correct directory.

#### MyMath Unit
```java
class MyMathTestMistral_P3 {

 @Test
 void checkSign_WhenZero_ThrowsIllegalArgumentException() {
  MyMath myMath = new MyMath();
  assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
 }

 @ParameterizedTest
 @ValueSource(ints = {1, 10, 100, Integer.MAX_VALUE})
 void checkSign_WhenPositive_ReturnsPositive(int number) {
  MyMath myMath = new MyMath();
  assertEquals("positive", myMath.checkSign(number));
 }

 @ParameterizedTest
 @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE})
 void checkSign_WhenNegative_ReturnsNegative(int number) {
  MyMath myMath = new MyMath();
  assertEquals("negative", myMath.checkSign(number));
 }

 @ParameterizedTest
 @ValueSource(ints = {0, 1, -1, 100, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
 void reverseNumber_ReversesSign(int number) {
  MyMath myMath = new MyMath();
  assertEquals(-number, myMath.reverseNumber(number));
 }
}
```
##### *checkSign(int)* method
Three methods were generated. they include:
- zero (boundary)
- a positive values parametrized test
- a negative values parametrized test

The tests have MC/DC.
I don't see any flaw here. only maybe MAX and MIN values could be unnecessary.

##### *reverseNumber(int)* method
a single method is generated that includes:
- a variety of values, 0, negatives and positive

The tests have MC/DC.  
I prefer separating values by their sign like the previous method tests.
otherwise, this is also good. perhaps testing Integer.MAX_VALUE is unnecessary.

#### MyAdvancedMath Unit
```java
class MyAdvancedMathTestMistral_P3 {

 @Test
 void add_WhenXIsNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 1));
 }

 @Test
 void add_WhenYIsNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(1, -1));
 }

 @Test
 void add_WhenBothAreNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, -1));
 }

 @Test
 void add_WhenOverflow_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
 }

 @ParameterizedTest
 @CsvSource({"0, 0, 0", "1, 1, 2", "10, 20, 30", "100, 200, 300"})
 void add_WhenValidInputs_ReturnsSum(int x, int y, int expected) {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertEquals(expected, myAdvancedMath.add(x, y));
 }

 @Test
 void powerOfTwo_WhenPowerIsNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
 }

 @Test
 void powerOfTwo_WhenPowerIsGreaterThan30_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
 }

 @ParameterizedTest
 @CsvSource({"0, 1", "1, 2", "2, 4", "3, 8", "10, 1024", "30, 1073741824"})
 void powerOfTwo_WhenValidPower_ReturnsCorrectResult(int power, int expected) {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertEquals(expected, myAdvancedMath.powerOfTwo(power));
 }

 @Test
 void reverseArrayNumbers_WhenEmptyArray_ReturnsEmptyArray() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  MyMath myMath = new MyMath();
  int[] array = {};
  assertArrayEquals(new int[]{}, myAdvancedMath.reverseArrayNumbers(array, myMath));
 }

 @Test
 void reverseArrayNumbers_WhenSingleElement_ReturnsReversedArray() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  MyMath myMath = new MyMath();
  int[] array = {5};
  assertArrayEquals(new int[]{-5}, myAdvancedMath.reverseArrayNumbers(array, myMath));
 }

 @Test
 void reverseArrayNumbers_WhenMultipleElements_ReturnsReversedArray() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  MyMath myMath = new MyMath();
  int[] array = {1, -2, 3, -4};
  assertArrayEquals(new int[]{-1, 2, -3, 4}, myAdvancedMath.reverseArrayNumbers(array, myMath));
 }
}
```
##### *add(int, int)* method
Five methods were generated that include:
- x is negative and y is positive and vise versa
- both negative
- overflow test
- a valid parametrized test
  - both zeros
  - both positives

MC/DC is achieved.
There could have been more tests with 0.  
many more tests probably should have been included.  
Agent really did emphasize on boundary values this time.  
Overall, it's not perfect.

##### *powerOfTwo(int)* method
Three methods are generated. they include:
- just below the lower boundary
- just above the upper boundary
- valid values parametrized test
  - the lower boundary
  - just above the lower boundary
  - normal values
  - the upper boundary

We have MC/DC.  
Definitely improved than last round.  
An invalid parametrized test could have been good, but it is not
generated because I said to generate a parametrized test for normal values.


##### *reverseArrayNumbers(int[], MyMath)* method
Three methods were generated. they include:
- a normal test
- a single element array test
- an empty array test

I'll guess from this method to the end, probably there won't be a noticeable
improvement. a single element array test is new compared to the last round.  
MC/DC is achieved.

#### FileIO Unit
```java
class FileIOTestMistral_P3 {

 @TempDir
 Path tempDir;

 @Test
 void readFile_WhenFileDoesNotExist_ReturnsEmptyArray() {
  FileIO fileIO = new FileIO();
  assertArrayEquals(new String[]{}, fileIO.readFile("nonexistent.txt"));
 }

 @Test
 void readFile_WhenFileIsEmpty_ReturnsEmptyArray() throws IOException {
  FileIO fileIO = new FileIO();
  File emptyFile = tempDir.resolve("empty.txt").toFile();
  emptyFile.createNewFile();
  assertArrayEquals(new String[]{}, fileIO.readFile(emptyFile.getAbsolutePath()));
 }

 @Test
 void readFile_WhenFileHasSingleLine_ReturnsArrayWithSingleElement() throws IOException {
  FileIO fileIO = new FileIO();
  File singleLineFile = tempDir.resolve("singleLine.txt").toFile();
  Files.write(singleLineFile.toPath(), "Hello, World!".getBytes());
  assertArrayEquals(new String[]{"Hello, World!"}, fileIO.readFile(singleLineFile.getAbsolutePath()));
 }

 @Test
 void readFile_WhenFileHasMultipleLines_ReturnsArrayWithMultipleElements() throws IOException {
  FileIO fileIO = new FileIO();
  File multiLineFile = tempDir.resolve("multiLine.txt").toFile();
  Files.write(multiLineFile.toPath(), ("Line 1\nLine 2\nLine 3").getBytes());
  assertArrayEquals(new String[]{"Line 1", "Line 2", "Line 3"}, fileIO.readFile(multiLineFile.getAbsolutePath()));
 }
}

```
Four methods are generated that include:
- a single line test
- a multiple line
- an empty file test
- a non-existing file

JUnit's Temporary storage has been used.  
single line file test is new compared to the previous round and MC/DC is achieved.

#### NameSearch Unit
```java
class NameSearchTestMistral_P3 {

  @TempDir
  Path tempDir;

  @Test
  void findNamesInFileThatStartWith_WhenFileDoesNotExist_ReturnsEmptyArray() {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith("nonexistent.txt", fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenFileIsEmpty_ReturnsEmptyArray() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File emptyFile = tempDir.resolve("empty.txt").toFile();
    emptyFile.createNewFile();
    assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith(emptyFile.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenNoMatches_ReturnsEmptyArray() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("John\nJane\nDoe").getBytes());
    assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenSingleMatch_ReturnsArrayWithSingleElement() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("Alice\nJohn\nJane").getBytes());
    assertArrayEquals(new String[]{"Alice"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenMultipleMatches_ReturnsArrayWithMultipleElements() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("Alice\nAndrew\nJohn\nAnna").getBytes());
    assertArrayEquals(new String[]{"Alice", "Andrew", "Anna"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenPrefixIsEmpty_ReturnsAllNames() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("Alice\nAndrew\nJohn").getBytes());
    assertArrayEquals(new String[]{"Alice", "Andrew", "John"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, ""));
  }
}
```
Six methods are generated which include:
- a test with multiple result
- a test with a single result
- a test without result
- an empty file test
- an empty prefix test
- a non-existing file test

The test achieves MC/DC.  
a single result and non-existing file tests are new. Non-existing test is
unnecessary since it was tested for FileIO.

#### Conclusion
Just some more methods and this would be a great unit test.  
The test methods do not start with test. this flaw happened in the two last rounds. Perhaps  
the model forgot this convention when the prompt got more complex.  
It was just the fact that it got stuck during the generation that annoyed me.

### Gemini 3 Flash
I did not run into any problem.  The agent generated the test files and put them
in the correct directory.
#### MyMath Unit
```java
class MyMathTestGemini_P3 {

  private final MyMath mm = new MyMath();

  @ParameterizedTest
  @CsvSource({
          "1, positive",
          "2, positive",
          "100, positive",
          "-1, negative",
          "-2, negative",
          "-100, negative"
  })
  void testCheckSign_NormalValues(int number, String expected) {
    Assertions.assertEquals(expected, mm.checkSign(number));
  }

  @Test
  void testCheckSign_Zero_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void testCheckSign_AboveZero_Boundary() {
    Assertions.assertEquals("positive", mm.checkSign(1));
  }

  @Test
  void testCheckSign_BelowZero_Boundary() {
    Assertions.assertEquals("negative", mm.checkSign(-1));
  }

  @ParameterizedTest
  @CsvSource({
          "1, -1",
          "-1, 1",
          "0, 0",
          "2147483647, -2147483647",
          "-2147483647, 2147483647"
  })
  void testReverseNumber_NormalAndEdges(int number, int expected) {
    Assertions.assertEquals(expected, mm.reverseNumber(number));
  }

  @Test
  void testReverseNumber_MinInt_Edge() {
    // Integer.MIN_VALUE is -2147483648. -(-2147483648) is still -2147483648 due to overflow.
    Assertions.assertEquals(Integer.MIN_VALUE, mm.reverseNumber(Integer.MIN_VALUE));
  }
}
```
Looks like Gemini uses private variable by default.
##### *checkSign(int)* method
Four methods were generated that include:
- zero (boundary)
- just below the boundary
- just above the boundary
- a parametrized test
  - some positives and some negatives

The tests have MC/DC.  
I would have separated the positive and negative parametrized test for sure.  
There is redundancy with 1 and -1.  
Honestly, not really an improvement than the previous round.

##### *reverseNumber(int)* method
Two methods were generated that include:
- MIN_VALUE edge case
- a parametrized test with
  - 1
  - -1
  - 0
  - Integer.MAX_VALUE
  - -Integer.MAX_VALUE

- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
the max values at the parametrized test are reoccurring, I believe.  
boundary have been taken care about here, the same goes for MIN_VALUE edge case.

#### MyAdvancedMath Unit
```java
class MyAdvancedMathTestGemini_P3 {

  private final MyAdvancedMath am = new MyAdvancedMath();
  private final MyMath mm = new MyMath();

  @ParameterizedTest
  @CsvSource({
          "1, 1, 2",
          "0, 0, 0",
          "100, 200, 300",
          "2147483647, 0, 2147483647"
  })
  void testAdd_NormalValues(int x, int y, int expected) {
    Assertions.assertEquals(expected, am.add(x, y));
  }

  @Test
  void testAdd_XNegative_MCDC() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(-1, 0));
  }

  @Test
  void testAdd_YNegative_MCDC() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(0, -1));
  }

  @Test
  void testAdd_Overflow_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(Integer.MAX_VALUE, 1));
  }

  @Test
  void testAdd_JustAtMax_Boundary() {
    Assertions.assertEquals(Integer.MAX_VALUE, am.add(Integer.MAX_VALUE, 0));
  }

  @Test
  void testAdd_JustBelowMax_Boundary() {
    Assertions.assertEquals(Integer.MAX_VALUE - 1, am.add(Integer.MAX_VALUE - 1, 0));
  }

  @ParameterizedTest
  @CsvSource({
          "1, 2",
          "2, 4",
          "5, 32",
          "10, 1024"
  })
  void testPowerOfTwo_NormalValues(int power, int expected) {
    Assertions.assertEquals(expected, am.powerOfTwo(power));
  }

  @Test
  void testPowerOfTwo_Zero_Boundary() {
    Assertions.assertEquals(1, am.powerOfTwo(0));
  }

  @Test
  void testPowerOfTwo_One_Boundary() {
    Assertions.assertEquals(2, am.powerOfTwo(1));
  }

  @Test
  void testPowerOfTwo_Negative_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.powerOfTwo(-1));
  }

  @Test
  void testPowerOfTwo_30_Boundary() {
    Assertions.assertEquals(1073741824, am.powerOfTwo(30));
  }

  @Test
  void testPowerOfTwo_29_Boundary() {
    Assertions.assertEquals(536870912, am.powerOfTwo(29));
  }

  @Test
  void testPowerOfTwo_31_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.powerOfTwo(31));
  }

  @Test
  void testReverseArrayNumbers_Normal() {
    int[] input = {1, -2, 0};
    int[] expected = {-1, 2, 0};
    Assertions.assertArrayEquals(expected, am.reverseArrayNumbers(input, mm));
  }

  @Test
  void testReverseArrayNumbers_Empty_Edge() {
    int[] input = {};
    int[] expected = {};
    Assertions.assertArrayEquals(expected, am.reverseArrayNumbers(input, mm));
  }
}
```
##### *add(int, int)* method
Six methods were generated that include:
- a parametrized test
  - both positive
  - both zeros
  - just below the overflow
- x is negative and y is non-negative and vice versa
- overflow test
- just below the overflow
- just below the max boundary

- x is zero and y is positive and vise versa


Tests do have MC/DC.  
just below the overflow is tested twice.  
just below the max boundary can be called redundant.  
I guess this is an improvement compared to the last round overall.

##### *powerOfTwo(int)* method
Seven methods were generated. The tests include:
- a parametrized test
  - just above the lower boundary
  - three normal values
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- just below the upper boundary
- the upper boundary
- just above the upper boundary



We have MC/DC.  
just above the lower boundary is tested twice.  
This is the first model to test just below the upper boundary.  
An invalid parametrized test and this would have been perfect. Prompt probably limited this.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated that include:
- a normal test
- an empty array test

MC/DC is achieved.  
Nothing is changed compared to the previous round.

#### FileIO Unit
```java

```
Four methods are generated that include:
- a normal test
- an empty file test
- a non-existing file
- a single line file test.

We run into exception, thus we achieve MC/DC.  
compared to the last round, single line file test is added.

#### NameSearch Unit
```java
class NameSearchTestGemini_P3 {

  private final NameSearch nameSearch = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  void testFindNamesInFileThatStartWith_Normal(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, Arrays.asList("Alice", "Bob", "Charlie", "Albert"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "Al");
    Assertions.assertArrayEquals(new String[]{"Alice", "Albert"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWith_NoMatches(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, Arrays.asList("Alice", "Bob"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "Z");
    Assertions.assertArrayEquals(new String[]{}, result);
  }

  @Test
  void testFindNamesInFileThatStartWith_EmptyPrefix_Edge(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, Arrays.asList("Alice", "Bob"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "");
    Assertions.assertArrayEquals(new String[]{"Alice", "Bob"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWith_EmptyFile_Edge(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "A");
    Assertions.assertArrayEquals(new String[]{}, result);
  }

  @ParameterizedTest
  @CsvSource({
          "A, Alice",
          "B, Bob",
          "C, Charlie"
  })
  void testFindNamesInFileThatStartWith_SingleMatches(String prefix, String expectedName, @TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names_param.txt");
    Files.write(file, Arrays.asList("Alice", "Bob", "Charlie"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, prefix);
    Assertions.assertArrayEquals(new String[]{expectedName}, result);
  }
}
```
Four methods with multiple assertions are generated which includes:
- a normal test
- a test without result
- empty file test
- empty prefix test
- a parametrized test
  - three single result test

The test achieves MC/DC.  
compared to the previous round, single result and parametrized test are new.  
Parametrized test might be unnecessary for this.

#### Conclusion
method names do comply with what they test, and they also start with test.  
Edge cases are tested even better.  
There are some reoccurring inputs.

### Devstral vs Gemini
This is the final showdown between these two models.
Devstral made more improvement, but let's see if it managed to beat gemini this round. the differences are:
- Gemini had some reoccurring input while Devstral had some redundant tests.
- Devstral did slightly better in *add* method
- Gemini covered just below the upper boundary in *powerOfTwo* method but Mistral didn't
- Devstral tested an array with a single element for *reverseArray* method
- Generally gemini did better (method names start with test and private variables were used)

This is actually closer than the previous round which gemini dominated. We can call this a tie!



### Gemma3:12b
After giving it the prompt, it generated MyMath unit test, but it stopped. I prompted it to continue
from MyAdvancedMath unit. It fully generated the unit test, but it stopped again. I prompt it to continue
from FileIO unit, and it generated both units.

#### MyMath Unit
```java
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
```
Unlike last time, it is using private variables now.
##### *checkSign(int)* method
It's weird that reverseNumber methods tests were generated between this method test.
Five methods were generated that include:
- a normal positive value
- a normal negative value
- just above the boundary
- just below the boundary
- zero (boundary)

The tests have MC/DC.  
There is no parametrized test.  
compared to the previous round, normal values are new.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
It actually did worse than the previous round!

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestGemma3_P3 {

    private MyAdvancedMath advancedMath;
    private MyMath math;

    @Test
    void add_positiveNumbers() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumber_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-2, 3));
    }

    @Test
    void add_zeroNumber_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(2, -3));
    }

    @Test
    void powerOfTwo_zeroPower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_onePower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(2, advancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_smallPower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(8, advancedMath.powerOfTwo(3));
    }

    @Test
    void powerOfTwo_largePower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_negativePower_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_powerGreaterThanThirty_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_validArray() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        int[] inputArray = {1, 2, 3};
        int[] expectedArray = {-1, -2, -3};
        int[] actualArray = advancedMath.reverseArrayNumbers(inputArray, math);
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        int[] inputArray = {};
        int[] expectedArray = {};
        int[] actualArray = advancedMath.reverseArrayNumbers(inputArray, math);
        assertArrayEquals(expectedArray, actualArray);
    }
}
```
Again, no parametrized tests.
##### *add(int, int)* method
Three methods are generated that include:
- positive x and y
- x is negative and y is positive and vice versa

There is no overflow test. so we don't even have statement coverage.  
Instead, we have positive x and negative y.  
This was terrible.

##### *powerOfTwo(int)* method
Six methods were generated that include:
- just below the lower boundary
- the lower boundary
- just above the lower boundary
- a normal test
- the upper boundary
- just above the upper boundary

We have MC/DC.  
boundary values are tested well.  
A normal test is added compared to the previous round.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated that include:
- a normal test
- an empty array test

The Tested methods have MC/DC.
Last round did slightly better with a single element array test.

#### FileIO Unit
```java
public class FileIOTestGemma3_P3 {

    private FileIO fileIO;

    @Test
    void readFile_validFile() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "test.txt");
        Files.write(filePath, Arrays.asList("line1", "line2", "line3").toArray(String[]::new));

        String[] expectedArray = {"line1", "line2", "line3"};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void readFile_emptyFile() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "empty.txt");
        Files.createFile(filePath);

        String[] expectedArray = {};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void readFile_invalidFile_throwsException() {
        fileIO = new FileIO();
        String invalidFilePath = "nonexistent_file.txt";
        assertThrows(Exception.class, () -> fileIO.readFile(invalidFilePath));
    }

    @Test
    void readFile_fileWithOneLine() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "one_line.txt");
        Files.write(filePath, Arrays.asList("single line").toArray(String[]::new));

        String[] expectedArray = {"single line"};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }
}
```
Four methods were generated that include:
- a normal test
- an empty file
- a single line file
- a non-existing test

MC/DC is achieved.  
It did not do the "ignoring white spaces" error from the last round.  
Non-existing test was not generated in the previous round.  
JUnit's temporary storage is not used despite being in the prompt.

#### NameSearch Unit
```java
public class NameSearchTestGemma3_P3 {

    private NameSearch nameSearch;
    private FileIO fileIO;

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie", "Adam").toArray(String[]::new));

        String[] expectedArray = {"Alice", "Adam"};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie").toArray(String[]::new));

        String[] expectedArray = {};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Z");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "empty.txt");
        Files.createFile(filePath);

        String[] expectedArray = {};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_emptyPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie").toArray(String[]::new));

        String[] expectedArray = {"Alice", "Bob", "Charlie"};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

}
```
Four methods are generated which includes:
- an empty file test
- a test without result
- a multiple result test
- an empty prefix test

The test achieves MC/DC.  
Compared to the previous round, we have empty prefix test but, we lost the single result test.

#### Conclusion
There was no parametrized test and JUnit's temporary storage despite being in the prompt.  
a method did not achieve MC/DC despite being prompted.  
boundary values are tested but not good enough.  
The test methods do not start with test.  
There wasn't a significant improvement compared to the previous round.

### Gemma4:e4b
This model generated all the unit tests but not name search. it stopped in the middle of it. I prompted
the model to regenerate it, then it generated a new file that was a unit to generate an empty file!
It did not complete it, so I prompted it to continue the previously generated file. It did generate
the unit test, but it didn't do it correctly. It generated a useless utility method.

#### MyMath Unit
```java
public class MyMathTestGemma4_P3 {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    // --- Tests for checkSign(int number) ---

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    @DisplayName("Should return 'positive' for positive numbers")
    void checkSign_Positive(int number) {
        assertEquals("positive", myMath.checkSign(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should return 'negative' for negative numbers")
    void checkSign_Negative(int number) {
        assertEquals("negative", myMath.checkSign(number));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when input is zero (Boundary Test)")
    void checkSign_Zero() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", thrown.getMessage());
    }

    // --- Tests for reverseNumber(int number) ---

    @Test
    @DisplayName("Should correctly reverse a positive number")
    void reverseNumber_Positive() {
        assertEquals(-123, myMath.reverseNumber(123));
    }

    @Test
    @DisplayName("Should correctly reverse a negative number")
    void reverseNumber_Negative() {
        assertEquals(99, myMath.reverseNumber(-99));
    }

    @Test
    @DisplayName("Should return zero when input is zero")
    void reverseNumber_Zero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
    
    @Test
    @DisplayName("Should handle Integer.MIN_VALUE correctly (Boundary Test)")
    void reverseNumber_MinValue() {
        // -Integer.MIN_VALUE overflows positive range, but the method returns -number, 
        // so we test the direct result of negation which is the expected behavior based on the code.
        // For Integer.MIN_VALUE (-2147483648), -number is still -2147483648 due to 2's complement behavior in Java int.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}
```

##### *checkSign(int)* method
Three methods are generated that include:
- a positive values parametrized test
  - a normal positive
  - just above the boundary
- a negative values parametrized test
  - a normal negative
  - just below the boundary
- a test for 0


The tests have MC/DC.  
This is my ideal tests for this method.

##### *reverseNumber(int)* method
Four methods were generated that include:
- a normal positive number
- a normal negative number
- zero
- Integer.MIN_VALUE

The tests have MC/DC.
A just above the boundary test has been removed from the previous round.  
There is also no parametrized test.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestGemma4_P3 {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    // --- Tests for add(int x, int y) ---

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "100, 200",
        "Integer.MAX_VALUE - 1, 1" // Just below overflow
    })
    @DisplayName("Should correctly add two positive integers")
    void add_Positive(int x, int y) {
        assertEquals(x + y, myAdvancedMath.add(x, y));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when first number is negative (Boundary Test)")
    void add_FirstNegative() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when second number is negative (Boundary Test)")
    void add_SecondNegative() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException upon overflow (Boundary Test)")
    void add_Overflow() {
        // Test case where x + y > Integer.MAX_VALUE
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(Integer.MAX_VALUE, 1);
        });
        assertEquals("Cannot fit the result in an Integer.", thrown.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @Test
    @DisplayName("Should return 1 when power is 0 (Boundary Test)")
    void powerOfTwo_Zero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "10, 1024",
        "30, 1073741824" // Boundary high
    })
    @DisplayName("Should calculate power of two correctly for valid inputs")
    void powerOfTwo_Valid(int power, int expected) {
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when power is -1 (Boundary Test)")
    void powerOfTwo_NegativePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when power is 31 (Boundary Test)")
    void powerOfTwo_TooLargePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    @DisplayName("Should reverse signs of all elements in an array")
    void reverseArrayNumbers_MixedSigns() {
        int[] input = {1, -2, 3, -4};
        // Expected: [-1, 2, -3, 4]
        int[] expected = {-1, 2, -3, 4};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle an array containing only zero")
    void reverseArrayNumbers_OnlyZeros() {
        int[] input = {0, 0, 0};
        int[] expected = {0, 0, 0};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return the array unchanged when it is empty")
    void reverseArrayNumbers_EmptyArray() {
        int[] input = {};
        int[] expected = {};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }
}
```
##### *add(int, int)* method
Four methods are generated that include:
- a parametrized test:
  - two both normal positive test
  - a just below overflow test
- x is negative and y is positive and vice versa
- overflow test

MC/DC achieved.  
Other than the parametrized test, there is no other improvement compared to the previous round.  
Exception message is asserted here, which is unnecessary.

##### *powerOfTwo(int)* method
Four methods are generated. They include:
- just below the lower boundary
- the lower boundary
- a parametrized test with
  - just above the boundary
  - a normal test
  - the upper boundary
- just above the boundary

We have MC/DC.  
boundary values are tested.  
Other than the parametrized test, there is no other improvement compared to the previous round.  
Exception message is asserted here, which is unnecessary.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated. they include:
- a normal test
- an all zeros test
- an empty array

MC/DC is achieved.  
Perhaps the all zero test is redundant.

#### FileIO Unit
```java
public class FileIOTestGemma4_P3 {

    private FileIO fileIO;

    @BeforeEach
    void setUp() {
        fileIO = new FileIO();
    }

    @Test
    @DisplayName("Should read content from a file with multiple lines")
    void readFile_MultipleLines(@TempDir Path tempDir) throws IOException {
        String content = "First line\nSecond line\nThird line";
        Path filePath = tempDir.resolve("testfile.txt");
        Files.writeString(filePath, content);

        String[] result = fileIO.readFile(filePath.toString());
        
        String[] expected = {"First line", "Second line", "Third line"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return an empty array for an empty file")
    void readFile_EmptyFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("emptyfile.txt");
        Files.createFile(filePath); // Creates a 0-byte file

        String[] result = fileIO.readFile(filePath.toString());
        
        // The implementation reads content line by line, an empty file results in an empty list/array.
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle file with only empty lines (Boundary Test)")
    void readFile_OnlyEmptyLines(@TempDir Path tempDir) throws IOException {
        String content = "\n\n\n"; // Three empty lines
        Path filePath = tempDir.resolve("empty_lines.txt");
        Files.writeString(filePath, content);

        String[] result = fileIO.readFile(filePath.toString());
        
        // BufferedReader reads empty lines as empty strings ""
        String[] expected = {"", "", ""};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle a file containing only one line")
    void readFile_SingleLine(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("single.txt");
        Files.writeString(filePath, "Just one line.");

        String[] result = fileIO.readFile(filePath.toString());
        
        String[] expected = {"Just one line."};
        assertArrayEquals(expected, result);
    }
    
    @Test
    @DisplayName("Should handle non-existent file gracefully (Exception Handling Test)")
    void readFile_NonExistentFile() {
        // Testing non-existent file, which should trigger IOException and print stack trace, 
        // but the return value should still be an array, potentially empty or whatever the exception handling leaves it at.
        String nonExistentPath = "path/that/definitely/does/not/exist/xyz123.txt";
        String[] result = fileIO.readFile(nonExistentPath);
        
        // Given the implementation catches IOException and proceeds, it returns the initial empty list converted to array.
        String[] expected = {};
        assertArrayEquals(expected, result);
    }
}
```
Five methods are generated that include:
- a normal test
- an empty file test
- a non-existing file
- a file with only empty lines test
- a single line test

MC/DC is achieved.  
It did use JUnit's temporary file. It did not do it in the previous round.  
The two last tests are new compared to the previous round.

#### NameSearch Unit
```java
public class NameSearchTestGemma4_P3 {

    private NameSearch nameSearch;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
    }

    @Test
    @DisplayName("Should find all names starting with a specific prefix")
    void findNamesInFileThatStartWith_AllMatch(@TempDir Path tempDir) throws IOException {
        String content = "Alice Smith\nAlbert Jones\nAndrew Lee";
        Path filePath = tempDir.resolve("names.txt");
        Files.writeString(filePath, content);

        FileIO fileIo = new FileIO();
        String prefix = "Ali";

        String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIo, prefix);
        
        String[] expected = {"Alice Smith", "Albert Jones"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should find no names when prefix does not match any entry")
    void findNamesInFileThatStartWith_NoMatch(@TempDir Path tempDir) throws IOException {
        String content = "Bob Brown\nCharlie Day";
        Path filePath = tempDir.resolve("names.txt");
        Files.writeString(filePath, content);

        FileIO fileIo = new FileIO();
        String prefix = "Zzz";

        String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIo, prefix);
        
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return an empty array when the input file is empty")
    void findNamesInFileThatStartWith_EmptyFile(@TempDir Path tempDir) throws IOException {
        // Ensure the file is created and is truly empty
        Path emptyFile = tempDir.resolve("empty.txt");
        java.nio.file.Files.write(emptyFile, new byte[0]);
        
        // Use the path of the empty file for testing
        String filePath = emptyFile.toString();

        // Assuming the implementation handles empty file gracefully by returning empty array
        String[] result = getPrivateMethod(this, "getPrivateMethod", this, "this", filePath, false); // Placeholder for actual method call
        
        // Given the actual method signature is hard to replicate, we rely on the test expectation:
        // If the file is empty, the result array should be empty.
        String[] expectedEmpty = new String[0];
        // For robust testing, we assume the method returns an array matching the expected size.
        // If we cannot call the actual private method, we document the expected outcome.
    }
    
    // Helper method placeholder to simulate calling a potentially private method
    private String[] getPrivateMethod(Object instance, String methodName, Object self, Object dummy, String filePath, boolean isPrivate) {
        // In a real testing environment, reflection would be used here.
        // For manual review, we confirm the goal: testing against an empty file yields an empty array.
        return new String[0]; 
    }


    // --- Test Case for Prefix Matching on a Known Good File ---
    @Test
    void testPrefixMatchingOnValidFile() throws IOException {
        String content = "Alice\nBob\nCharlie";
        Path validFile = tempDir.resolve("valid.txt");
        java.nio.file.Files.write(validFile, content.getBytes());
        
        String filePath = validFile.toString();
        
        // Assume we can call the method under test which reads and filters the file
        // String[] result = methodUnderTest(this, filePath); 
        
        // Expected result based on the content and a hypothetical prefix (e.g., "A")
        // String[] expected = {"Alice"}; 
        
        // Assertions would compare the actual result with the expected array.
    }
}
```
I just realized it did not complete the last test since there is no assertion, but it has the closing
bracket for it. The test method name says that it is a redundant test. it did not even
use the helper function at all. Agent really messed this unit testing.  
Three test methods are generated which include:
- a normal test
- a test without result
- an empty file test

The test achieves MC/DC.  
We have lost the empty prefix test from the last round.


#### Conclusion
Perhaps there were fewer redundancies.  
The test methods do not start with test.  
Exception messages were asserted.  
Parametrized tests are used although *reverseNumber* method could also have one.  
MC/DC is achieved in all methods.


### GPT-OSS:20b
This model thought for 154.3 seconds and generated all the tests.

#### MyMath Unit
```java
class MyMathTestGPT_OSS_P3 {

    private final MyMath myMath = new MyMath();

    // ------------------------------------------------------------------------
    // checkSign – boundary values (0, -1, 1)
    // ------------------------------------------------------------------------

    @Test
    void checkSign_positiveNumber_returnsPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber_returnsNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    void checkSign_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    // ------------------------------------------------------------------------
    // reverseNumber – typical values and zero
    // ------------------------------------------------------------------------

    @Test
    void reverseNumber_positiveNumber_returnsNegative() {
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    void reverseNumber_negativeNumber_returnsPositive() {
        assertEquals(7, myMath.reverseNumber(-7));
    }

    @Test
    void reverseNumber_zero_returnsZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
```
This looks like a round one generation, but it's not. you can see that it has commented
"checkSign – boundary values (0, -1, 1)" but it did not test -1 and 1.  
There is also no parametrized test here.

##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive
- a normal negative
- zero (boundary)

The tests have MC/DC.  
This is worse than the previous round as expected.

##### *reverseNumber(int)* method
Three methods were generated that include:
- a normal positive
- a normal negative
- zero (at the boundary)

The tests have MC/DC.  
the Integer.MIN_VALUE edge case is missing.  
This is also worse than the previous round.

#### MyAdvancedMath Unit
```java
class MyAdvancedMathTestGPT_OSS_P3 {

    private final MyAdvancedMath math = new MyAdvancedMath();

    // ------------------------------------------------------------------------
    // add – valid, negative, and overflow cases
    // ------------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(ints = {1, 42, 1000})
    void add_validNumbers_returnsSum(int x) {
        int y = 10;
        assertEquals(x + y, math.add(x, y));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
    }

    @Test
    void add_overflowInput_throwsIllegalArgumentException() {
        int x = Integer.MAX_VALUE - 1;
        int y = 3; // overflow
        assertThrows(IllegalArgumentException.class, () -> math.add(x, y));
    }

    // ------------------------------------------------------------------------
    // powerOfTwo – boundary and normal values
    // ------------------------------------------------------------------------

    @Test
    void powerOfTwo_zero_returnsOne() {
        assertEquals(1, math.powerOfTwo(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 10})
    void powerOfTwo_normalValues_returnsCorrectPower(int power) {
        int expected = 1 << power; // 2^power
        assertEquals(expected, math.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_negativePower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_exceedingPower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
    }

    // ------------------------------------------------------------------------
    // reverseArrayNumbers – empty, normal, mixed values
    // ------------------------------------------------------------------------

    @Test
    void reverseArrayNumbers_emptyArray_returnsEmptyArray() {
        int[] input = {};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    void reverseArrayNumbers_normalArray_reversesSigns() {
        int[] input = {1, -2, 0, 5};
        int[] expected = {-1, 2, 0, -5};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(expected, result);
    }
}
```
##### *add(int, int)* method
Three methods were generated that include:
- a parametrized test for both positive
- x is negative and y is positive
- overflow test

Like the previous round, there is no MC/DC since y is never negative.  
Other than the parametrized test, there is no improvement.

##### *powerOfTwo(int)* method
Four methods are generated that include:
- just below the lower boundary
- the lower boundary
- a parametrized test
  - just above the boundary
- just above the upper boundary

We have MC/DC.  
The upper boundary test is gone compared to the previous round.

##### *reverseArrayNumbers(int[], MyMath)* method
Two methods are generated which include:
- empty array test
- a normal test

MC/DC is achieved.  
This is basically the same as the previous round.

#### FileIO Unit
```java
class FileIOTestGPT_OSS_P3 {

    @TempDir Path tempDir;

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile_returnsEmptyArray() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    void readFile_singleLine_returnsSingleElementArray() throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.write(file, "Hello World".getBytes());

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{"Hello World"}, result);
    }

    @Test
    void readFile_multipleLines_returnsAllLines() throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.write(file, ("Line1\nLine2\nLine3").getBytes());

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{"Line1", "Line2", "Line3"}, result);
    }
}
```
Three methods were generated that include:
- an empty file
- a file with a single line
- a file with multiple lines

Non-existing test case is missing, thus MC/DC is not achieved.
This is basically the same as the previous round.

#### NameSearch Unit
```java
class NameSearchTestGPT_OSS_P3 {

    @TempDir Path tempDir;

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmptyArray() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Alice\nBob\nCharlie").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "D");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    void findNamesInFileThatStartWith_singleMatch_returnsCorrectArray() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Anna\nBrian\nCatherine").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "B");
        assertArrayEquals(new String[]{"Brian"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches_returnsAllMatches() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Sam\nSara\nSeth\nTom").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "S");
        assertArrayEquals(new String[]{"Sam", "Sara", "Seth"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_prefixMatchesAll_returnsAllNames() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Mike\nMia\nMark").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "M");
        assertArrayEquals(new String[]{"Mike", "Mia", "Mark"}, result);
    }
}
```
Four methods with multiple are generated which include:
- a test without result.
- a test with a single result
- a test with multiple result
- a test that prefix matches all.

The test achieves MC/DC.  
Empty prefix test and empty file test are missing.  
The last test is not necessary in my opinion.  
This was worse than the previous round.

#### Conclusion
This model not only did not improve the last round but also did worse!
method names do comply with what they test, but they don't start with test.  
Some edge cases have not been tested.  
parametrized tests are somewhat used.

### Qwen-Coder2.5:7b
The most annoying model actually did better. Only it generated all the unit tests inside a single
window with multiple public classes. I just had to paste each class in its own file.
#### MyMath Unit
```java
public class MyMathTestQwen2_5_P3 {

    @Test
    public void testCheckSign_positiveNumber() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    public void testCheckSign_negativeNumber() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-10));
    }

    @Test
    public void testCheckSign_zero() {
        MyMath myMath = new MyMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertTrue(exception.getMessage().contains("Number had no sign"));
    }

    @Test
    public void testReverseNumber_positiveNumber() {
        MyMath myMath = new MyMath();
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    public void testReverseNumber_negativeNumber() {
        MyMath myMath = new MyMath();
        assertEquals(10, myMath.reverseNumber(-10));
    }
}
```
This is so poor. it's like I gave it a simple prompt. there is no parametrized test and no boundary value
checking.
##### *checkSign(int)* method
Three methods were generated that include:
- a normal positive
- a normal negative
- zero (boundary)

The tests have MC/DC.  
This is worse than the previous round!

##### *reverseNumber(int)* method
Two methods were generated which include:
- a normal positive
- a normal negative

MC/DC for a single statement method is achieved.  
There is no zero test.  
This is also worse than the previous round.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestQwen2_5_P3 {

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "0, 0, 0"
    })
    public void testAdd_positiveNumbers(int x, int y, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    public void testAdd_negativeNumber() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 2);
        });
        assertTrue(exception.getMessage().contains("Input numbers should be positive."));
    }

    @Test
    public void testAdd_integerOverflow() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(Integer.MAX_VALUE - 1, 2);
        });
        assertTrue(exception.getMessage().contains("Cannot fit the result in an Integer."));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 1",
        "5, 0"
    })
    public void testPowerOfTwo_validPower(int power, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    public void testPowerOfTwo_negativePower() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertTrue(exception.getMessage().contains("power should be 0<=power<=30"));
    }

    @Test
    public void testPowerOfTwo_powerAboveLimit() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertTrue(exception.getMessage().contains("power should be 0<=power<=30"));
    }

    @Test
    public void testReverseArrayNumbers_emptyArray() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    public void testReverseArrayNumbers_arrayWithSingleElement() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{-1}, result);
    }

    @Test
    public void testReverseArrayNumbers_arrayWithMultipleElements() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1, 2, 3};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{-1, -2, -3}, result);
    }
}
```
At least there are some parametrized tests.

##### *add(int, int)* method
Three methods were generated that include:
- a parametrized test
  - both positives
  - both zeros
- negative x and positive y
- overflow test

Tests have decision coverage since y is never negative.  
Both zeros and parametrized tests are the improvements compared to the last round.

##### *powerOfTwo(int)* method
Three methods were generated. The tests include:
- just below the boundary
- a parametrized test
  - the lower boundary
  - a normal test
- just above the upper boundary


We have MC/DC.  
The normal test is actually wrong! 2^5 is 32, not 0.
Parametrized test is the only improvement.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods are generated:
- an empty array test
- a single element array test
- a multiple element array test

Empty array and single element array tests are improvements.

#### FileIO Unit
```java
public class FileIOTestQwen2_5_P3 {

    @Test
    public void testReadFile_nonEmptyFile() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("src/test/resources/non_empty_file.txt");
        assertEquals(1, result.length);
        assertEquals("Line 1", result[0]);
    }

    @Test
    public void testReadFile_emptyFile() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("src/test/resources/empty_file.txt");
        assertArrayEquals(new String[]{}, result);
    }
}
```
Two methods are generated:
- normal test
- empty file path

No temporary file is generated, meaning this is just two non-existence files.  
MC/DC is not achieved since we don't run into exception or at least intentionally.  
This was worse than the previous round.

#### NameSearch Unit
```java
public class NameSearchTestQwen2_5_P3 {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources/names.txt, A, [Alicia]",
        "src/test/resources/names.txt, J, [John, Jessica]"
    })
    public void testFindNamesInFileThatStartWith(String filepath, String prefix, String[] expected) {
        FileIO fileIo = new FileIO();
        NameSearch nameSearch = new NameSearch();
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIo, prefix);
        assertArrayEquals(expected, result);
    }
}
```
Only a single method is generated which is a parametrized test that include:
- a test with single result
- a test with multiple results

Again, there is no file generation, and the test is wrong.
This is far worse than the previous round.

#### Conclusion
This is definitely the worst model of all I have tested.  
It did worse than the previous round.  
A lot of what I prompted the agent like boundary values and temporary directory are ignored.
method names do comply with what they test, and they also start with test.
Many edge cases were not tested.

### Qwen-Coder3:30b
Hopefully you do much better than your kin. I did not run into any issue in this round.
That's a great start.

#### MyMath Unit
```java
public class MyMathTestQwen3_P3 {

    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, positive",
        "100, positive",
        "1000, positive",
        "-1, negative",
        "-100, negative",
        "-1000, negative"
    })
    void checkSign_NormalValues(int number, String expected) {
        assertEquals(expected, myMath.checkSign(number));
    }

    @Test
    void checkSign_ZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000, -1, -100, -1000})
    void reverseNumber_NormalValues(int number) {
        assertEquals(-number, myMath.reverseNumber(number));
    }
}
```

##### *checkSign(int)* method
Two methods are generated that include:
- a parametrized test
  - just above the boundary
  - normal positive values
  - just below the boundary
  - normal negative values
- zero (boundary)

The tests have MC/DC.  
This is actually perfect.

##### *reverseNumber(int)* method
a single method with parametrized test is generated:
- just above the boundary
- normal positive values
- just below the boundary
- normal negative values

Three methods were generated that include:
- a normal positive number
- a normal negative number
- zero (at the boundary)

The tests have MC/DC.  
There is no zero test which was tested previously.  
Parametrized test is at least an improvement.

#### MyAdvancedMath Unit
```java
public class MyAdvancedMathTestQwen3_P3 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "10, 10",
        "100, 100",
        "1000, 1000"
    })
    void add_NormalValues(int x, int y) {
        assertEquals(x + y, myAdvancedMath.add(x, y));
    }

    @Test
    void add_NegativeNumbersThrowException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(5, -1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, -1));
    }

    @Test
    void add_OverflowThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(1000000, Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 15, 20, 25, 30})
    void powerOfTwo_NormalValues(int power) {
        assertEquals(1 << power, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_ZeroReturnsOne() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_NegativePowerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-10));
    }

    @Test
    void powerOfTwo_PowerAbove30ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(50));
    }

    @Test
    void reverseArrayNumbers_NormalArray() {
        int[] array = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_EmptyArray() {
        int[] array = {};
        assertArrayEquals(new int[0], myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_SingleElementArray() {
        int[] array = {5};
        int[] expected = {-5};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }
}
```
There are many methods with multiple assertions.

##### *add(int, int)* method
Four methods with a total of nine assertions are generated:
- a parametrized test for both positive
- x is negative and y is positive and vice versa
- x and y both negative
- two overflow tests

MD/DC is achieved.  
There are minor improvements compared to the last round

##### *powerOfTwo(int)* method
Four methods with a total of fourteen assertions are generated:
- a normal negative value
- just below the lower boundary
- a parametrized test
  - the lower boundary
  - just above the lower boundary
  - many values in between
  - the upper boundary
- just above the upper boundary
- a positive number above the boundary

We have MC/DC.  
boundary values are tested.  
This is an improvement, but it's not significant.

##### *reverseArrayNumbers(int[], MyMath)* method
Three methods were generated that include:
- a normal test
- an empty array test
- an array with single element test

MC/DC is achieved.  
It has removed a redundant test. I will take that as an improvement.

#### FileIO Unit
```java
public class FileIOTestQwen3_P3 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_EmptyFileReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void readFile_NormalFileReturnsContent(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("test.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Line 1\nLine 2\nLine 3");
        writer.close();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Line 1", result[0]);
        assertEquals("Line 2", result[1]);
        assertEquals("Line 3", result[2]);
    }

    @Test
    void readFile_FileNotFoundReturnsEmptyArray(@TempDir Path tempDir) {
        String[] result = fileIO.readFile(tempDir.resolve("nonexistent.txt").toString());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void readFile_WithSpecialCharacters(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("special.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Line with spaces\nLine_with_underscores\nLine-with-dashes");
        writer.close();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Line with spaces", result[0]);
        assertEquals("Line_with_underscores", result[1]);
        assertEquals("Line-with-dashes", result[2]);
    }
}
```
Four methods are generated that include:

- a normal test
- an empty file test
- a non-existing file
- a file with special characters


- a file with empty lines between content
- a single line file

MC/DC is achieved.  
Compared to the previous round, a file with empty lines between content and
a single line file are gone, but a file with special characters is new, but it's not really
important. So it did worse than the last round.

#### NameSearch Unit
```java
public class NameSearchTestQwen3_P3 {

  private final NameSearch nameSearch = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  void findNamesInFileThatStartWith_EmptyFileReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("empty.txt").toFile();
    tempFile.createNewFile();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void findNamesInFileThatStartWith_NoMatchesReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nBob\nCharlie");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void findNamesInFileThatStartWith_SingleMatch(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nJohn Smith\nBob\nJohn Doe");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(2, result.length);
    assertEquals("John Smith", result[0]);
    assertEquals("John Doe", result[1]);
  }

  @Test
  void findNamesInFileThatStartWith_MultipleMatches(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nJohn Smith\nBob\nJohn Doe\nJohnathan\nCharlie");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(3, result.length);
    assertEquals("John Smith", result[0]);
    assertEquals("John Doe", result[1]);
    assertEquals("Johnathan", result[2]);
  }

  @Test
  void findNamesInFileThatStartWith_EmptyPrefix(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nBob\nCharlie");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "");
    assertNotNull(result);
    assertEquals(3, result.length);
    assertEquals("Alice", result[0]);
    assertEquals("Bob", result[1]);
    assertEquals("Charlie", result[2]);
  }

  @Test
  void findNamesInFileThatStartWith_EmptyFileName(@TempDir Path tempDir) {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempDir.resolve("nonexistent.txt").toString(), fileIO, "John");
    assertNotNull(result);
    assertEquals(0, result.length);
  }
}
```
Six methods are generated which include:
- a test with single result
- a test with multiple results
- an empty file test
- a test without result
- an empty prefix test
- an empty file path test


The test achieves MC/DC.  
The last test is redundant and wrong. It still gave a file name to TempDir.  
multiple assertions are used in a single method.  
It's basically the same as the previous round.

#### Conclusion
The Model did well, but there were no significant improvements.
method names do comply with what they test, but they don't start with test.
It did that in the previous round.
private variables were used.

### Local models comparison
For *checkSign* method, Qwen 3 and Gemma4 did the best with parametrized testing. Then Gemma3 did slightly
better than GPT OSS and Qwen 2.5.
For *reverseNumber*, only Qwen 3 used parametrized test while gemma4 was the only model to test Integer.MIN_VALUE.
Qwen 2.5 did not even test 0!
For *add* method, GPT-OSS, Qwen 3 and Gemma4 used parametrized test. Qwen 3 did the best overall.
Gemma3 did the worst here.
For *powerOfTwo* method, Qwen 3 and Gemma4 did the same. Gemma3 did better here than GPT-OSS and
Qwen 2.5.
In *reverseArray* method, Qwen 3 and Gemma4 did the best. then GPT-OSS and Gemma3. Qwen 2.5 did the worst.
For *readFile* method, Qwen 2.5 and Gemma3 are last, then we have GPT-OSS and Gemma4. of course,
Qwen 3 did the best.  
For *NameSearch* unit, Qwen 3 was the superior. Gemma3 is next. Qwen 2.5 did the worst.
And generally Qwen 3 and Gemma4 did the best. Qwen 2.5 was terrible.
here is my ranking:
1. Qwen 3 Coder 30b
2. Gemma4 e4b
3. Gemma3 12b
4. GPT OSS 20b
5. Qwen 2.5 Coder 7b

The only difference compared to the previous round is that Gemma3 did better than GPT-OSS.

## Cloud models vs Local models
I have tested two small cloud models and five local models varying in their parameter counts.  
It's a good idea to compare these models to ask ourselves, is it worth it to use these
local models over the small cloud models?  
I will compare both clouds and local models overall, and Gemini 3 Flash and Qwen 3 since
they were the best in their category.  
With the first prompt, They were close. Cloud models performed slightly better in
MyAdvancedMath and FileIO units and in other units, they were equal. 
With the second prompt, Cloud models did slightly better in MyMath and MyAdvancedMath
units while local models did slightly better in NameSearch.  
With the final prompt, cloud models did more than slightly better. This shows these models can
handle complex prompts better.  
Surprisingly, across all three prompts, Gemini and Qwen3 performed the same. Maybe in a prompt
Gemini did only slightly better, but overall, there is no concrete winner!
It should be noted that It does take some time for Qwen3 to generate the code, especially if
the computer that is running it does not have a good specification, but it's completely free, and
you can be confident about your codes not being stolen from cloud companies. However, these online
models are cheap and you can use them anywhere without additional setup.


## Final Conclusion
This is a powerful tool. Users can access a larger variety of models compared
to GitHub Copilot. The cloud models are definitely performing better than
the local models. If I really had to use a local model, I would probably use
Gemma4 since it doesn't have many parameters, but it is very powerful still.
This was a more fair comparison since last time because all models got the
same prompt in three rounds. Some general things like method starting with
test should have been included in the prompt but also a lot of local models
forgot to use @TempDir while it was in the prompt. For each round, an Excel
file exists comparing the models in a table.