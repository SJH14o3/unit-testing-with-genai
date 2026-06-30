package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class FileIOTestGPTOSS_SP {

    private FileIO fileIO;

    @BeforeEach
    void setUp() {
        fileIO = new FileIO();
    }

    @Test
    @DisplayName("readFile returns lines as array for a small file")
    void readSmallFile() throws IOException {
        Path temp = Files.createTempFile("fileio-test", ".txt");
        Files.writeString(temp, "line1\nline2\nline3");
        String[] result = fileIO.readFile(temp.toString());
        assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
        Files.deleteIfExists(temp);
    }

    @Test
    @DisplayName("readFile returns empty array for empty file")
    void readEmptyFile() throws IOException {
        Path temp = Files.createTempFile("fileio-empty", ".txt");
        String[] result = fileIO.readFile(temp.toString());
        assertArrayEquals(new String[]{}, result);
        Files.deleteIfExists(temp);
    }
}
