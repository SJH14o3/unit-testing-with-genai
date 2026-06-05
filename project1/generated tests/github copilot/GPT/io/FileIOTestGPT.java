package dmst.seip.angor.junitdemo.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileIOTestGPT {

    @Test
    void readFile_returnsLinesInSameOrder(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        List<String> lines = List.of("Alice", "Bob", "Charlie");
        Files.write(file, lines, StandardCharsets.UTF_8);

        FileIO fileIO = new FileIO();
        String[] result = fileIO.readFile(file.toString());

        assertArrayEquals(lines.toArray(new String[0]), result);
    }

    @Test
    void readFile_emptyFile_returnsEmptyArray(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        FileIO fileIO = new FileIO();
        String[] result = fileIO.readFile(file.toString());

        assertEquals(0, result.length);
    }
}
