// src/test/java/dmst/seip/angor/junitdemo/io/FileIOTestGPT_OSS_P3.java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class FileIOTestGPT_OSS_P3 {

    @TempDir Path tempDir;

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile_returnsEmptyArray() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    void readFile_singleLine_returnsSingleElementArray() throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.write(file, "Hello World".getBytes());

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{"Hello World"}, result);
    }

    @Test
    void readFile_multipleLines_returnsAllLines() throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.write(file, ("Line1\nLine2\nLine3").getBytes());

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{"Line1", "Line2", "Line3"}, result);
    }
}