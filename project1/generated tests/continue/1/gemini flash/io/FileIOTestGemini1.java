package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FileIOTestGemini1 {

    private final FileIO fileIO = new FileIO();

    @Test
    public void testReadFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        String[] expectedContent = {"line1", "line2", "line3"};
        
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            for (String line : expectedContent) {
                writer.write(line + System.lineSeparator());
            }
        }

        String[] actualContent = fileIO.readFile(tempFile.toString());
        Assertions.assertArrayEquals(expectedContent, actualContent);
    }

    @Test
    public void testReadFileNotExists() {
        // FileIO.readFile prints stack trace and returns empty array if file not found
        String[] content = fileIO.readFile("non_existent_file.txt");
        Assertions.assertEquals(0, content.length);
    }
}
