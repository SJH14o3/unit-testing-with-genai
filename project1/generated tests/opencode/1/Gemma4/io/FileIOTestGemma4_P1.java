package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.Collectors;

public class FileIOTestGemma4_P1 {

    private FileIO fileIO;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        fileIO = new FileIO();
        // Create a temporary file for testing
        tempFile = File.createTempFile("testfile", ".txt");
    }

    @Test
    void testReadFileSuccess() throws IOException {
        String content = "Line 1\nLine 2\nLine 3";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        // Testing the behavior of the public method
        String[] lines = fileIO.readFile(tempFile.getAbsolutePath());

        assertNotNull(lines);
        assertEquals(3, lines.length);
        assertEquals("Line 1", lines[0]);
        assertEquals("Line 2", lines[1]);
        assertEquals("Line 3", lines[2]);
    }

    @Test
    void testReadFileEmptyFile() throws IOException {
        // Create an empty file
        tempFile.createNewFile();
        
        // The method uses BufferedReader and readLine(), which correctly handles an empty file resulting in an empty array
        String[] lines = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertNotNull(lines);
        assertEquals(0, lines.length);
    }
    
    @Test
    void testReadFileNonExistentFile() {
        // Use a path that definitely does not exist
        String nonExistentPath = "non_existent_file_" + System.currentTimeMillis() + ".txt";
        
        // The original implementation catches IOException and prints stack trace, 
        // but still returns an empty list via stream().toArray().
        String[] lines = fileIO.readFile(nonExistentPath);
        
        assertNotNull(lines);
        assertTrue(lines.length == 0);
    }
}