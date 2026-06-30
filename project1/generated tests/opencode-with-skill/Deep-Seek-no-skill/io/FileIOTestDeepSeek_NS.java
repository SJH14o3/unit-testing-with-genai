package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

public class FileIOTestDeepSeek_NS {

    @TempDir
    Path tempDir;

    @Test
    void testReadFileWithMultipleLines() throws Exception {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, "Alice\nBob\nCharlie".getBytes());

        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile(file.toString());
        assertArrayEquals(new String[]{"Alice", "Bob", "Charlie"}, result);
    }

    @Test
    void testReadFileEmptyFile() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile(file.toString());
        assertEquals(0, result.length);
    }

    @Test
    void testReadFileNonExistent() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("nonexistent.txt");
        assertEquals(0, result.length);
    }
}
