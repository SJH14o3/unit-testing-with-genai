// Content for FileIOTestGemma4_P3.java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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