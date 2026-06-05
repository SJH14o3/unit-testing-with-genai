package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import dmst.seip.angor.junitdemo.io.FileIO;

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