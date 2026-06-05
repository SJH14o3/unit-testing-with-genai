package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NameSearch.
 * @author Gemma 4
 */
public class NameSearchTestGemma4_P2 {

    private FileIO fileIO = new FileIO();
    private NameSearch nameSearch = new NameSearch();

    // --- Test Cases ---

    @Test
    void findNamesInFileThatStartWith_MultipleMatches() throws Exception {
        // Setup: Create a temporary file with multiple matches
        Path tempFile = tempDir.resolve("names_match.txt");
        String content = "Alice Smith\nAndrew Johnson\nBob Green\nAlicia Brown";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Ali");

        // Assertion: Expect 3 matches
        assertArrayEquals(new String[]{"Alice Smith", "Andrew Johnson", "Alicia Brown"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_NoMatches() throws Exception {
        // Setup: Create a temporary file with no matching names
        Path tempFile = tempDir.resolve("names_no_match.txt");
        String content = "David Lee\nEva Green\nFrank White";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Xyz");

        // Assertion: Expect an empty array
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyPrefix() throws Exception {
        // Setup: Create a temporary file
        Path tempFile = tempDir.resolve("names_empty_prefix.txt");
        String content = "Apple\nBanana\nCherry";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "");

        // Assertion: Empty prefix should match all names
        assertArrayEquals(new String[]{"Apple", "Banana", "Cherry"}, result);
    }
    
    @Test
    void findNamesInFileThatStartWith_EmptyFile() throws Exception {
        // Setup: Create an empty temporary file
        Path tempFile = tempDir.resolve("names_empty_file.txt");
        java.nio.file.Files.createFile(tempFile);
        
        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

        // Assertion: Should return an empty array
        assertArrayEquals(new String[0], result);
    }
}