package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestGemma4_P3 {

    private NameSearch nameSearch;
    
    // Note: Since NameSearch depends on FileIO, and FileIO relies on the actual file system,
    // we will mock the FileIO behavior using @TempDir to simulate successful file reads.

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
    }

    /**
     * Test case for finding names starting with a specific prefix in a well-formed file.
     */
    @Test
    void findNamesInFileThatStartWith_FoundNames(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("names_file.txt");
        String content = "Alice Smith\nBob Johnson\nAlex Brown\nAndrew Taylor";
        Files.write(filePath, content.getBytes());
        
        // We use the FileIO class we tested previously, assuming a successful read operation.
        FileIO fileIO = new FileIO(); 

        String prefix = "A";
        String[] expectedArray = {"Alice Smith", "Alex Brown", "Andrew Taylor"};
        
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

        assertArrayEquals(expectedArray, actualArray);
    }
    
    /**
     * Test case for finding no names matching the prefix.
     */
    @Test
    void findNamesInFileThatStartWith_NoMatches(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("names_file_2.txt");
        String content = "Alpha\nBeta\nGamma";
        Files.write(filePath, content.getBytes());

        FileIO fileIO = new FileIO();

        String prefix = "X";
        String[] expectedArray = {};
        
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

        assertArrayEquals(expectedArray, actualArray);
    }

    /**
     * Test case for an empty file, simulating zero names.
     */
    @Test
    void findNamesInFileThatStartWith_EmptyFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("empty_names_file.txt");
        Files.write(filePath, new byte[0]);

        FileIO fileIO = new FileIO();

        String prefix = "N/A";
        String[] expectedArray = {};
        
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

        assertArrayEquals(expectedArray, actualArray);
    }
    
    /**
     * Test case to check when the name list only contains names that match the prefix.
     */
    @Test
    void findNamesInFileThatStartWith_AllMatch(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("all_names_file.txt");
        String content = "apple\napricot\napple pie";
        Files.write(filePath, content.getBytes());
        
        FileIO fileIO = new FileIO();

        String prefix = "app";
        String[] expectedArray = {"apple", "apricot", "apple pie"};
        
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

        assertArrayEquals(expectedArray, actualArray);
    }
}