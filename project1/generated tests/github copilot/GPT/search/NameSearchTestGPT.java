package dmst.seip.angor.junitdemo.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dmst.seip.angor.junitdemo.io.FileIO;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NameSearchTestGPT {

    private final NameSearch search = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_returnsMatchingNames(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        List<String> names = List.of("Anna", "Bob", "Ansel", "Charlie");
        Files.write(file, names, StandardCharsets.UTF_8);

        String[] matches = search.findNamesInFileThatStartWith(file.toString(), fileIO, "An");

        assertArrayEquals(new String[]{"Anna", "Ansel"}, matches);
    }

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmpty(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, List.of("Bob", "Charlie"), StandardCharsets.UTF_8);

        String[] matches = search.findNamesInFileThatStartWith(file.toString(), fileIO, "Z");

        assertEquals(0, matches.length);
    }
}
