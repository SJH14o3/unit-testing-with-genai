// FileIOTestGPT_OSS_P2.java
package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileIOTestGPT_OSS_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[0], fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_singleLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.writeString(file, "Hello");
        assertArrayEquals(new String[]{"Hello"}, fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_multipleLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.write(file, ("Line1\nLine2\nLine3").getBytes());
        assertArrayEquals(new String[]{"Line1", "Line2", "Line3"},
                fileIO.readFile(file.toString()));
    }
}