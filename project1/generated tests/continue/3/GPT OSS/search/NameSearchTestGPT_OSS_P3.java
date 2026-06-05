// src/test/java/dmst/seip/angor/junitdemo/search/NameSearchTestGPT_OSS_P3.java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import dmst.seip.angor.junitdemo.io.FileIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGPT_OSS_P3 {

    @TempDir Path tempDir;

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmptyArray() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Alice\nBob\nCharlie").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "D");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    void findNamesInFileThatStartWith_singleMatch_returnsCorrectArray() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Anna\nBrian\nCatherine").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "B");
        assertArrayEquals(new String[]{"Brian"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches_returnsAllMatches() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Sam\nSara\nSeth\nTom").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "S");
        assertArrayEquals(new String[]{"Sam", "Sara", "Seth"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_prefixMatchesAll_returnsAllNames() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Mike\nMia\nMark").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "M");
        assertArrayEquals(new String[]{"Mike", "Mia", "Mark"}, result);
    }
}