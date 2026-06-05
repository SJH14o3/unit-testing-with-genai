package test.java.dmst.seip.angor.junitdemo.io;

package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileIOTestQwen3_P3 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_EmptyFileReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void readFile_NormalFileReturnsContent(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("test.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Line 1\nLine 2\nLine 3");
        writer.close();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Line 1", result[0]);
        assertEquals("Line 2", result[1]);
        assertEquals("Line 3", result[2]);
    }

    @Test
    void readFile_FileNotFoundReturnsEmptyArray(@TempDir Path tempDir) {
        String[] result = fileIO.readFile(tempDir.resolve("nonexistent.txt").toString());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void readFile_WithSpecialCharacters(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("special.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Line with spaces\nLine_with_underscores\nLine-with-dashes");
        writer.close();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Line with spaces", result[0]);
        assertEquals("Line_with_underscores", result[1]);
        assertEquals("Line-with-dashes", result[2]);
    }
}