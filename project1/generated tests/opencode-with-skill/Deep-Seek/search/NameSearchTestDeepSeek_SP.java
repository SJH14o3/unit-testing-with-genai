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

@DisplayName("NameSearch Tests (DeepSeek_SP)")
class NameSearchTestDeepSeek_SP {

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
            writer.write("Alice\nBob\nCharlie\nAmanda\nDavid\nAnna\nAlex\n");
        }
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns names matching prefix")
    void findNamesInFileThatStartWith_shouldReturnMatchingNames() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "A");
        assertAll("should find all names starting with A",
                () -> assertArrayEquals(new String[]{"Alice", "Amanda", "Anna", "Alex"}, result));
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns empty array when no match")
    void findNamesInFileThatStartWith_shouldReturnEmptyArray_whenNoMatch() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "Z");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns single match")
    void findNamesInFileThatStartWith_shouldReturnSingleMatch() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "B");
        assertArrayEquals(new String[]{"Bob"}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns all names with empty prefix")
    void findNamesInFileThatStartWith_shouldReturnAllNames_whenPrefixIsEmpty() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "");
        assertArrayEquals(
                new String[]{"Alice", "Bob", "Charlie", "Amanda", "David", "Anna", "Alex"},
                result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns empty for empty file")
    void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileIsEmpty() throws IOException {
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();
        String[] result = nameSearch.findNamesInFileThatStartWith(
                emptyFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith handles prefix with special characters")
    void findNamesInFileThatStartWith_shouldHandleSpecialPrefix() throws IOException {
        File specialFile = tempDir.resolve("special.txt").toFile();
        try (FileWriter writer = new FileWriter(specialFile)) {
            writer.write("@lpha\n#hash\n@lso\n");
        }
        String[] result = nameSearch.findNamesInFileThatStartWith(
                specialFile.getAbsolutePath(), fileIO, "@");
        assertArrayEquals(new String[]{"@lpha", "@lso"}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith is case-sensitive")
    void findNamesInFileThatStartWith_shouldBeCaseSensitive() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                namesFile.getAbsolutePath(), fileIO, "a");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    @DisplayName("findNamesInFileThatStartWith returns empty for non-existent file")
    void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileDoesNotExist() {
        String[] result = nameSearch.findNamesInFileThatStartWith(
                tempDir.resolve("nope.txt").toString(), fileIO, "A");
        assertArrayEquals(new String[]{}, result);
    }
}
