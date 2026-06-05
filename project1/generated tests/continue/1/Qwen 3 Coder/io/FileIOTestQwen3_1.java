package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Test class for FileIO
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class FileIOTestQwen3_1 {

    private FileIO fileIO = new FileIO();

    @Test
    public void testReadFile() throws IOException {
        // Create a temporary file for testing
        File tempFile = File.createTempFile("test", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n");
        writer.write("Bob\n");
        writer.write("Charlie\n");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Bob", result[1]);
        assertEquals("Charlie", result[2]);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        File tempFile = File.createTempFile("testEmpty", ".txt");
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertEquals(0, result.length);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testReadFileNonExistent() {
        // Test with a non-existent file - should not throw exception but return empty array
        String[] result = fileIO.readFile("non_existent_file.txt");
        assertEquals(0, result.length);
    }
}