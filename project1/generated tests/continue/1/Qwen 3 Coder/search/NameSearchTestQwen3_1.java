package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmst.seip.angor.junitdemo.io.FileIO;

/**
 * Test class for NameSearch
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class NameSearchTestQwen3_1 {

    private NameSearch nameSearch = new NameSearch();
    private FileIO fileIO = new FileIO();

    @Test
    public void testFindNamesInFileThatStartWith() throws IOException {
        // Create a temporary file for testing
        File tempFile = File.createTempFile("names", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n");
        writer.write("Bob\n");
        writer.write("Charlie\n");
        writer.write("Amanda\n");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "A");

        assertEquals(2, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Amanda", result[1]);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatch() throws IOException {
        // Create a temporary file for testing
        File tempFile = File.createTempFile("names", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n");
        writer.write("Bob\n");
        writer.write("Charlie\n");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "Z");

        assertEquals(0, result.length);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyFile() throws IOException {
        // Create a temporary empty file for testing
        File tempFile = File.createTempFile("names", ".txt");
        
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "A");

        assertEquals(0, result.length);

        // Clean up
        tempFile.delete();
    }
}