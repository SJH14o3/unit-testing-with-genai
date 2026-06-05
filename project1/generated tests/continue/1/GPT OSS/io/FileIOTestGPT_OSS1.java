package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FileIO}.
 */
class FileIOTestGPT_OSS1 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_existingFile_returnsLines() throws IOException {
        // Create a temporary file with known content
        File temp = File.createTempFile("testFileIO", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("first line\n");
            writer.write("second line\n");
            writer.write("third line");
        }

        String[] lines = fileIO.readFile(temp.getAbsolutePath());
        assertArrayEquals(new String[]{"first line", "second line", "third line"}, lines);
    }

    @Test
    void readFile_nonexistentFile_returnsEmptyArray() {
        // Non‑existent file
        String[] lines = fileIO.readFile("this_file_does_not_exist.txt");
        // The implementation prints a stack trace and returns an empty array
        assertEquals(0, lines.length);
    }
}