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
}"