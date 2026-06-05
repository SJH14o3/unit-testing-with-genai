package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class FileIOTestGemini_P3 {

    private final FileIO fileIO = new FileIO();

    @Test
    void testReadFile_Normal(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        List<String> lines = Arrays.asList("line1", "line2", "line3");
        Files.write(file, lines);

        String[] result = fileIO.readFile(file.toString());
        Assertions.assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
    }

    @Test
    void testReadFile_EmptyFile_Edge(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        String[] result = fileIO.readFile(file.toString());
        Assertions.assertArrayEquals(new String[]{}, result);
    }

    @Test
    void testReadFile_SingleLine_Boundary(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.write(file, Arrays.asList("onlyOneLine"));

        String[] result = fileIO.readFile(file.toString());
        Assertions.assertArrayEquals(new String[]{"onlyOneLine"}, result);
    }

    @Test
    void testReadFile_NonExistentFile_Edge() {
        // readFile catches IOException and prints stack trace, then returns what it has (empty list)
        String[] result = fileIO.readFile("non_existent_file.txt");
        Assertions.assertArrayEquals(new String[]{}, result);
    }
}
