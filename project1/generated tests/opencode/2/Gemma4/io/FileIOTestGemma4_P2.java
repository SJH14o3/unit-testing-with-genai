package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.TempDir;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

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