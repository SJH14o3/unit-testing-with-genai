package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestQwen3_P3 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_EmptyFileReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();
        
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void findNamesInFileThatStartWith_NoMatchesReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();
        
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void findNamesInFileThatStartWith_SingleMatch(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nJohn Smith\nBob\nJohn Doe");
        writer.close();
        
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("John Smith", result[0]);
        assertEquals("John Doe", result[1]);
    }

    @Test
    void findNamesInFileThatStartWith_MultipleMatches(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nJohn Smith\nBob\nJohn Doe\nJohnathan\nCharlie");
        writer.close();
        
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("John Smith", result[0]);
        assertEquals("John Doe", result[1]);
        assertEquals("Johnathan", result[2]);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyPrefix(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();
        
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "");
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Bob", result[1]);
        assertEquals("Charlie", result[2]);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyFileName(@TempDir Path tempDir) {
        String[] result = nameSearch.findNamesInFileThatStartWith(
            tempDir.resolve("nonexistent.txt").toString(), fileIO, "John");
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}