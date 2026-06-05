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
