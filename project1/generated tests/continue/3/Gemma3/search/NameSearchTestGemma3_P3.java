package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(value = "dmst.seip.angor.junitdemo.search.NameSearchTestGemma3_P3")
public class NameSearchTestGemma3_P3 {

    private NameSearch nameSearch;
    private FileIO fileIO;

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie", "Adam").toArray(String[]::new));

        String[] expectedArray = {"Alice", "Adam"};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie").toArray(String[]::new));

        String[] expectedArray = {};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Z");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "empty.txt");
        Files.createFile(filePath);

        String[] expectedArray = {};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_emptyPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie").toArray(String[]::new));

        String[] expectedArray = {"Alice", "Bob", "Charlie"};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

}