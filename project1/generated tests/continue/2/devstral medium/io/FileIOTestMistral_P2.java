package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class FileIOTestMistral_P2 {

    @TempDir
    Path tempDir;

    @Test
    public void testReadFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "Line 1\nLine 2\nLine 3".getBytes());
        
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(testFile.toString());
        
        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    public void testReadEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, "".getBytes());
        
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(testFile.toString());
        
        assertEquals(0, content.length);
    }

    @Test
    public void testReadNonExistentFile() {
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile("nonexistent.txt");
        
        assertEquals(0, content.length);
    }
}
