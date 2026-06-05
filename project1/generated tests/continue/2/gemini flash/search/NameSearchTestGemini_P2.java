package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class NameSearchTestGemini_P2 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    public void testFindNamesPrefixed(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("names.txt");
        List<String> names = Arrays.asList("Alice", "Bob", "Anna", "Alex");
        Files.write(tempFile, names);
        
        String[] expected = {"Alice", "Anna", "Alex"};
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    public void testFindNamesNoMatch(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("names.txt");
        List<String> names = Arrays.asList("Alice", "Bob", "Anna");
        Files.write(tempFile, names);
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Z");
        
        Assertions.assertEquals(0, result.length);
    }

    @Test
    public void testFindNamesEmptyFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("empty.txt");
        Files.createFile(tempFile);
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        
        Assertions.assertEquals(0, result.length);
    }

    @Test
    public void testFindNamesEmptyPrefix(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("names.txt");
        List<String> names = Arrays.asList("Alice", "Bob");
        Files.write(tempFile, names);
        
        // Every string starts with empty string
        String[] expected = {"Alice", "Bob"};
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "");
        
        Assertions.assertArrayEquals(expected, result);
    }
}
