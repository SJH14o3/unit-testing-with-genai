package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NameSearch Tests")
class NameSearchTestMiMo_SP {

    private NameSearch nameSearch;
    private FileIO fileIO;
    private File namesFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        namesFile = tempDir.resolve("names.txt").toFile();
        try (FileWriter writer = new FileWriter(namesFile)) {
            writer.write("Alice\nBob\nCharlie\nAmanda\nDavid\nAnna\n");
        }
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns matching names")
    void findNamesInFileThatStartWith_shouldReturnMatchingNames() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(new String[]{"Alice", "Amanda", "Anna"}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns empty array when no matches")
    void findNamesInFileThatStartWith_shouldReturnEmpty_whenNoMatches() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "Z");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns single match")
    void findNamesInFileThatStartWith_shouldReturnSingleMatch() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "D");
        assertArrayEquals(new String[]{"David"}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns all names when prefix is empty")
    void findNamesInFileThatStartWith_shouldReturnAll_whenPrefixIsEmpty() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "");
        assertArrayEquals(new String[]{"Alice", "Bob", "Charlie", "Amanda", "David", "Anna"}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns empty array for empty file")
    void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileIsEmpty() throws IOException {
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();

        String[] result = nameSearch.findNamesInFileThatStartWith(
                emptyFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(new String[]{}, result);
    }
}
