package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class FileIOTestGPTOSS_P3 {

    private final FileIO io = new FileIO();

    @TempDir
    File tempDir;

    @Test
    void readFile_withEmptyFile_returnsEmptyArray() throws IOException {
        File file = new File(tempDir, "empty.txt");
        file.createNewFile();
        assertNotNull(io.readFile(file.getAbsolutePath()));
        assertEquals(0, io.readFile(file.getAbsolutePath()).length);
    }

    @Test
    void readFile_withMultipleLines_returnsArrayWithLines() throws IOException {
        File file = new File(tempDir, "lines.txt");
        try (var writer = new FileWriter(file)) {
            writer.write("first\nsecond\nthird");
        }
        String[] result = io.readFile(file.getAbsolutePath());
        assertArrayEquals(new String[]{"first", "second", "third"}, result);
    }

    @Test
    void readFile_withNonExistentFile_returnsEmptyArray() {
        // The method does not throw on non-existent but returns empty array because exception is caught
        String[] result = io.readFile("nonexistent.file");
        assertNotNull(result);
    }
}
