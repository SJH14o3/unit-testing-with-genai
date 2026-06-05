package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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