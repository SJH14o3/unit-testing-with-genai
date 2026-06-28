package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestGemma4_P2 {

    NameSearch nameSearch;
    FileIO fileIO;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
        fileIO = new FileIO(); // Initialize FileIO for use in the test
    }

    @Test
    void testFindNamesStartWithNormal() throws IOException {
        // Setup: File content
        Path filePath = tempDir.resolve("names.txt");
        String content = "Apple\nBanana\nApricot\nGrape";
        Files.writeString(filePath, content);
        
        // Execute
        String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Ap");
        
        // Assert
        assertEquals(2, results.length);
        assertArrayEquals(new String[]{"Apple", "Apricot"}, results);
    }
    
    @Test
    void testFindNamesStartWithAllMatches() throws IOException {
        // Setup: File content
        Path filePath = tempDir.resolve("all_names.txt");
        String content = "Cat\nCar\nCoat";
        Files.writeString(filePath, content);
        
        // Execute
        String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "C");
        
        // Assert
        assertEquals(3, results.length);
        assertArrayEquals(new String[]{"Cat", "Car", "Coat"}, results);
    }

    @Test
    void testFindNamesStartWithPrefixNonExistent() throws IOException {
        // Setup: File content
        Path filePath = tempDir.resolve("names.txt");
        String content = "Apple\nBanana\nApricot\nGrape";
        Files.writeString(filePath, content);
        
        // Execute
        String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "XYZ");
        
        // Assert
        assertEquals(0, results.length);
        assertArrayEquals(new String[0], results);
    }
    
    @Test
    void testFindNamesWithEmptyPrefix() throws IOException {
        // Setup: File content
        Path filePath = tempDir.resolve("names.txt");
        String content = "Apple\nBanana\nApricot";
        Files.writeString(filePath, content);
        
        // Execute: Empty prefix should match all lines
        String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");
        
        // Assert
        assertEquals(3, results.length);
        assertArrayEquals(new String[]{"Apple", "Banana", "Apricot"}, results);
    }

    @Test
    void testFindNamesInEmptyFile() {
        // Setup: Create an empty file
        Path emptyFilePath = tempDir.resolve("empty.txt");
        try {
            Files.createFile(emptyFilePath);
        } catch (IOException e) {
            // Ignore cleanup errors for testing structure
        }
        
        // Execute
        // We need to handle the fact that fileIO.readFile might fail/read nothing on a non-existent/empty file
        // Based on FileIO, it reads contents, so an empty file *is* read as an array of size 0.
        String[] results = nameSearch.findNamesInFileThatStartWith(emptyFilePath.toString(), fileIO, "A");
        
        // Assert
        assertEquals(0, results.length);
        assertArrayEquals(new String[0], results);
    }
}