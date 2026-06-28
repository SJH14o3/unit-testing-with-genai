package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class FileIOTestGPTOSS_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[]{}, fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_multipleLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("lines.txt");
        Files.write(file, java.util.List.of("one", "two", "three"));
        assertArrayEquals(new String[]{"one","two","three"}, fileIO.readFile(file.toString()));
    }
}
