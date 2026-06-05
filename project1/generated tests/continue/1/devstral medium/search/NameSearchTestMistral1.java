package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dmst.seip.angor.junitdemo.io.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NameSearchTestMistral1 {

    @Test
    void testFindNamesInFileThatStartWith() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nAnna\nAndrew\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A");

        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "Z");

        assertEquals(0, result.length);
    }
}
