package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NameSearch}.
 */
class NameSearchTestGPT_OSS1 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_matchFound_returnsMatchingNames() throws IOException {
        // Prepare a temporary file with a list of names
        File temp = File.createTempFile("names", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("Alice\n");
            writer.write("Bob\n");
            writer.write("Alex\n");
            writer.write("Eve\n");
        }

        String[] result = nameSearch.findNamesInFileThatStartWith(
                temp.getAbsolutePath(),
                fileIO,
                "Al");

        assertArrayEquals(new String[]{"Alice", "Alex"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatch_returnsEmptyArray() throws IOException {
        File temp = File.createTempFile("names", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("Bob\n");
            writer.write("Eve\n");
        }

        String[] result = nameSearch.findNamesInFileThatStartWith(
                temp.getAbsolutePath(),
                fileIO,
                "Al");

        assertEquals(0, result.length);
    }
}