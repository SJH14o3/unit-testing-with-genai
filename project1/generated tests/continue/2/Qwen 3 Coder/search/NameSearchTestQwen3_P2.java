package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TempDir;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestQwen3_P2 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with matching names")
    public void testFindNamesInFileThatStartWithMatchingNames(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie\nAmanda\nBella");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice", "Amanda"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with no matching names")
    public void testFindNamesInFileThatStartWithNoMatchingNames(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with empty prefix")
    public void testFindNamesInFileThatStartWithEmptyPrefix(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "");
        String[] expected = {"Alice", "Bob", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with empty file")
    public void testFindNamesInFileThatStartWithEmptyFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary empty file
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with single name matching")
    public void testFindNamesInFileThatStartWithSingleNameMatching(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with case sensitive matching")
    public void testFindNamesInFileThatStartWithCaseSensitive(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("alice\nAlice\nALICE");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice"};
        assertArrayEquals(expected, result);
    }
}