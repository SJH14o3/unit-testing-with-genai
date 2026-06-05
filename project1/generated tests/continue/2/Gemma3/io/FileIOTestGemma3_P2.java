import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttribute;

import dmst.seip.angor.junitdemo.io.FileIO;

public class FileIOTestGemma3_P2 {

    private FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile() throws IOException {
        Path tempFile = Files.createTempFile("empty");
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[0], content);
    }

    @Test
    void readFile_singleLineFile() throws IOException {
        Path tempFile = Files.createTempFile("single");
        Files.write(tempFile, "single line".getBytes());
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[]{"single line"}, content);
    }

    @Test
    void readFile_multipleLinesFile() throws IOException {
        Path tempFile = Files.createTempFile("multiple");
        Files.write(tempFile, "''\n                line1\n                line2\n                line3\n                ''".getBytes());
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[]{"line1", "line2", "line3"}, content);
    }
}
