package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class FileIOTestGemini_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    public void testReadFileNormal(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        List<String> expectedLines = Arrays.asList("Alice", "Bob", "Charlie");
        Files.write(tempFile, expectedLines);
        
        String[] result = fileIO.readFile(tempFile.toString());
        Assertions.assertArrayEquals(expectedLines.toArray(new String[0]), result);
    }

    @Test
    public void testReadFileEmpty(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("empty.txt");
        Files.createFile(tempFile);
        
        String[] result = fileIO.readFile(tempFile.toString());
        Assertions.assertEquals(0, result.length);
    }

    @Test
    public void testReadNonExistentFile() {
        // The implementation catches IOException and prints stack trace, returning empty array.
        String[] result = fileIO.readFile("non_existent_file.txt");
        Assertions.assertEquals(0, result.length);
    }
}
