// NameSearchTestGPT_OSS_P2.java
package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import dmst.seip.angor.junitdemo.io.FileIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NameSearchTestGPT_OSS_P2 {

    private final NameSearch search = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_noMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Alice\nBob\nCharlie");
        assertArrayEquals(new String[0],
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "Z"));
    }

    @Test
    void findNamesInFileThatStartWith_singleMatch(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Alice\nBob\nAlbert");
        assertArrayEquals(new String[]{"Alice", "Albert"},
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Ann\nAndy\nBob\nAaron");
        assertArrayEquals(new String[]{"Ann", "Andy", "Aaron"},
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[0],
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }
}