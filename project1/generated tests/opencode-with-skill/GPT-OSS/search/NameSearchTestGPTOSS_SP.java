package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGPTOSS_SP {

    private NameSearch nameSearch;
    private FileIO fileIO;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns matching names")
    void findMatchingNames() throws IOException {
        Path temp = Files.createTempFile("names", ".txt");
        Files.writeString(temp, "Alice\nAlfred\nbob\nALBERT");
        String[] result = nameSearch.findNamesInFileThatStartWith(temp.toString(), fileIO, "Al");
        assertArrayEquals(new String[]{"Alice", "Alfred", "ALBERT"}, result);
        Files.deleteIfExists(temp);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns empty array when no match")
    void noMatches() throws IOException {
        Path temp = Files.createTempFile("emptysearch", ".txt");
        Files.writeString(temp, "John\nJane");
        String[] result = nameSearch.findNamesInFileThatStartWith(temp.toString(), fileIO, "Xy");
        assertArrayEquals(new String[]{}, result);
        Files.deleteIfExists(temp);
    }
}
