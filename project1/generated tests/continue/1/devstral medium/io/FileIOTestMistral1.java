package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileIOTestMistral1 {

    @Test
    void testReadFile() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Line 1\nLine 2\nLine 3".getBytes());

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(0, content.length);
    }
}
