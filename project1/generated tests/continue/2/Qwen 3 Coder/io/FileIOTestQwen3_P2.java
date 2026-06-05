package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TempDir;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

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