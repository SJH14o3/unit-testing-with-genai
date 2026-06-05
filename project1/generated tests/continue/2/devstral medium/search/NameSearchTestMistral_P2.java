package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import dmst.seip.angor.junitdemo.io.FileIO;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestMistral_P2 {

    @TempDir
    Path tempDir;

    @Test
    public void testFindNamesInFileThatStartWith() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna\nAndrew\nEve".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "A");
        
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyPrefix() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "");
        
        assertEquals(3, result.length);
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "Z");
        
        assertEquals(0, result.length);
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, "".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "A");
        
        assertEquals(0, result.length);
    }
}
