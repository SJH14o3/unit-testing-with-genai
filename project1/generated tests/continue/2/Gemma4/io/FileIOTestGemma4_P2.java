package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileIO.
 * @author Gemma 4
 */
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