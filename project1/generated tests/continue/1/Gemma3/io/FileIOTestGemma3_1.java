package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

class FileIOTestGemma3_1 {

    @Test
    void readFile_validFile() throws Exception {
        FileIO fileIO = new FileIO();
        // Create a temporary file for testing
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit(); // Delete when the test finishes.
        
        //Write lines to temp file
        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Line 1\n");
        fw.write("Line 2\n");
        fw.write("Line 3\n");
        fw.close();
        
        String[] expected = {"Line 1", "Line 2", "Line 3"};
        String[] actual = fileIO.readFile(tempFile.getAbsolutePath());
        assertArrayEquals(expected, actual);
    }

    @Test
    void readFile_emptyFile() throws Exception {
        FileIO fileIO = new FileIO();
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();
        
        String[] expected = new String[0];
        String[] actual = fileIO.readFile(tempFile.getAbsolutePath());
        assertArrayEquals(expected, actual);
    }

}
