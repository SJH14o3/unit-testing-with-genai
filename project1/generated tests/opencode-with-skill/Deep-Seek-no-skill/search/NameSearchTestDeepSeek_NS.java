package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

import dmst.seip.angor.junitdemo.io.FileIO;

public class NameSearchTestDeepSeek_NS {

    @TempDir
    Path tempDir;

    @Test
    void testFindNamesInFileThatStartWithMatchingPrefix() throws Exception {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, "Alice\nBob\nCharlie\nAlex".getBytes());

        NameSearch ns = new NameSearch();
        String[] result = ns.findNamesInFileThatStartWith(
                file.toString(), new FileIO(), "Al");
        assertArrayEquals(new String[]{"Alice", "Alex"}, result);
    }

    @Test
    void testFindNamesInFileThatStartWithNoMatch() throws Exception {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, "Bob\nCharlie".getBytes());

        NameSearch ns = new NameSearch();
        String[] result = ns.findNamesInFileThatStartWith(
                file.toString(), new FileIO(), "X");
        assertEquals(0, result.length);
    }

    @Test
    void testFindNamesInFileThatStartWithEmptyFile() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        NameSearch ns = new NameSearch();
        String[] result = ns.findNamesInFileThatStartWith(
                file.toString(), new FileIO(), "A");
        assertEquals(0, result.length);
    }
}
