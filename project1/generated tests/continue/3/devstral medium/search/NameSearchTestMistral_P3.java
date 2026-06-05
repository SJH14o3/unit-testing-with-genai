package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestMistral_P3 {

    @TempDir
    Path tempDir;

    @Test
    void findNamesInFileThatStartWith_WhenFileDoesNotExist_ReturnsEmptyArray() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith("nonexistent.txt", fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_WhenFileIsEmpty_ReturnsEmptyArray() throws IOException {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();
        assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith(emptyFile.getAbsolutePath(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_WhenNoMatches_ReturnsEmptyArray() throws IOException {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        File file = tempDir.resolve("names.txt").toFile();
        Files.write(file.toPath(), ("John\nJane\nDoe").getBytes());
        assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_WhenSingleMatch_ReturnsArrayWithSingleElement() throws IOException {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        File file = tempDir.resolve("names.txt").toFile();
        Files.write(file.toPath(), ("Alice\nJohn\nJane").getBytes());
        assertArrayEquals(new String[]{"Alice"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_WhenMultipleMatches_ReturnsArrayWithMultipleElements() throws IOException {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        File file = tempDir.resolve("names.txt").toFile();
        Files.write(file.toPath(), ("Alice\nAndrew\nJohn\nAnna").getBytes());
        assertArrayEquals(new String[]{"Alice", "Andrew", "Anna"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_WhenPrefixIsEmpty_ReturnsAllNames() throws IOException {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        File file = tempDir.resolve("names.txt").toFile();
        Files.write(file.toPath(), ("Alice\nAndrew\nJohn").getBytes());
        assertArrayEquals(new String[]{"Alice", "Andrew", "John"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, ""));
    }
}
