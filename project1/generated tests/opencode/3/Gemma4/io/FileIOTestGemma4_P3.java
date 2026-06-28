package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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