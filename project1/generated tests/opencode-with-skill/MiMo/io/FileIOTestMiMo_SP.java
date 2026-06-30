package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileIO Tests")
class FileIOTestMiMo_SP {

    private final FileIO fileIO = new FileIO();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("readFile returns lines from a valid file")
    void readFile_shouldReturnLines_whenFileExists() throws IOException {
        File file = tempDir.resolve("names.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Alice\nBob\nCharlie\n");
        }

        String[] result = fileIO.readFile(file.getAbsolutePath());
        assertArrayEquals(new String[]{"Alice", "Bob", "Charlie"}, result);
    }

    @Test
    @DisplayName("readFile returns empty array for empty file")
    void readFile_shouldReturnEmptyArray_whenFileIsEmpty() throws IOException {
        File file = tempDir.resolve("empty.txt").toFile();
        file.createNewFile();

        String[] result = fileIO.readFile(file.getAbsolutePath());
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    @DisplayName("readFile handles single line file")
    void readFile_shouldReturnSingleLine_whenFileHasOneLine() throws IOException {
        File file = tempDir.resolve("single.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("OnlyLine\n");
        }

        String[] result = fileIO.readFile(file.getAbsolutePath());
        assertArrayEquals(new String[]{"OnlyLine"}, result);
    }

    @Test
    @DisplayName("readFile returns empty array for non-existent file")
    void readFile_shouldReturnEmptyArray_whenFileDoesNotExist() {
        String[] result = fileIO.readFile("non_existent_file.txt");
        assertArrayEquals(new String[]{}, result);
    }
}
