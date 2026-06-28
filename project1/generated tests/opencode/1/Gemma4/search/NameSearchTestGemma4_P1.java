package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestGemma4_P1 {

    private NameSearch nameSearch;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        nameSearch = new NameSearch();
        // Create a temporary file for testing
        tempFile = File.createTempFile("testnames", ".txt");
    }

    @Test
    void testFindNamesInFileWhenNamesStartWithPrefix() throws IOException {
        String content = "apple\napricot\nbanana\napply\n";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        // Dependency injection requires FileIO. We mock file creation for testing, 
        // but since dependencies cannot be mocked, we use a TestFileIO implementation 
        // or assume a controlled environment. For this controlled test, we use a helper approach
        // to handle the file dependency if FileIO needs *actual* file operations.
        // Since we cannot strictly mock FileIO in a dependency-free way within this scope,
        // we will pass a file object that the concrete FileIO handles.
        
        // For clean testing, we must ensure the FileIO used here is robust.
        // The internal implementation of FileIO, when reading from this tempFile, should work.
        
        // Running the test with the dependency injection
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "ap");

        assertNotNull(result);
        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"apple", "apricot", "apply"}, result);
    }

    @Test
    void testFindNamesInFileWithNoMatches() throws IOException {
        String content = "banana\norange\norange";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "z");

        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    void testFindNamesInFileWithEmptyContent() throws IOException {
        // Create an empty file
        tempFile.createNewFile();
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "a");

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testFindNamesInFileWithNonExistFile() {
        // Test failure case for FileIO dependency
        String nonExistentPath = "non_existent_file_" + System.currentTimeMillis() + ".txt";
        
        // The FileIO implementation catches IOException and returns an empty array.
        String[] result = nameSearch.findNamesInFileThatStartWith(nonExistentPath, new dmst.seip.angor.junitdemo.io.FileIO(), "test");

        assertNotNull(result);
        assertEquals(0, result.length);
    }
}