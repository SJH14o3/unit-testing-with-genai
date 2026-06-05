package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class NameSearchTestGemini_P3 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void testFindNamesInFileThatStartWith_Normal(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, Arrays.asList("Alice", "Bob", "Charlie", "Albert"));

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "Al");
        Assertions.assertArrayEquals(new String[]{"Alice", "Albert"}, result);
    }

    @Test
    void testFindNamesInFileThatStartWith_NoMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, Arrays.asList("Alice", "Bob"));

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "Z");
        Assertions.assertArrayEquals(new String[]{}, result);
    }

    @Test
    void testFindNamesInFileThatStartWith_EmptyPrefix_Edge(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, Arrays.asList("Alice", "Bob"));

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "");
        Assertions.assertArrayEquals(new String[]{"Alice", "Bob"}, result);
    }

    @Test
    void testFindNamesInFileThatStartWith_EmptyFile_Edge(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "A");
        Assertions.assertArrayEquals(new String[]{}, result);
    }

    @ParameterizedTest
    @CsvSource({
        "A, Alice",
        "B, Bob",
        "C, Charlie"
    })
    void testFindNamesInFileThatStartWith_SingleMatches(String prefix, String expectedName, @TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names_param.txt");
        Files.write(file, Arrays.asList("Alice", "Bob", "Charlie"));

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, prefix);
        Assertions.assertArrayEquals(new String[]{expectedName}, result);
    }
}
