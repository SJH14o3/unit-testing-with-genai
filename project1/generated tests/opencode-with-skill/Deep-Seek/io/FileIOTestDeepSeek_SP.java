package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileIO Tests (DeepSeek_SP)")
class FileIOTestDeepSeek_SP {

    private final FileIO fileIO = new FileIO();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("readFile returns all lines from valid file")
    void readFile_shouldReturnLines_whenFileExists() throws IOException {
        File file = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("line1\nline2\nline3\n");
        }
        assertArrayEquals(new String[]{"line1", "line2", "line3"},
                fileIO.readFile(file.getAbsolutePath()));
    }

    @Test
    @DisplayName("readFile returns empty array for empty file")
    void readFile_shouldReturnEmptyArray_whenFileIsEmpty() throws IOException {
        File file = tempDir.resolve("empty.txt").toFile();
        file.createNewFile();
        assertArrayEquals(new String[]{}, fileIO.readFile(file.getAbsolutePath()));
    }

    @Test
    @DisplayName("readFile handles single line file")
    void readFile_shouldReturnSingleLine_whenFileHasOneLine() throws IOException {
        File file = tempDir.resolve("single.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("soleEntry\n");
        }
        assertArrayEquals(new String[]{"soleEntry"},
                fileIO.readFile(file.getAbsolutePath()));
    }

    @Test
    @DisplayName("readFile handles file with trailing newline")
    void readFile_shouldHandleTrailingNewline() throws IOException {
        File file = tempDir.resolve("trailing.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("first\nsecond\n");
        }
        assertArrayEquals(new String[]{"first", "second"},
                fileIO.readFile(file.getAbsolutePath()));
    }

    @Test
    @DisplayName("readFile returns empty array for non-existent file")
    void readFile_shouldReturnEmptyArray_whenFileDoesNotExist() {
        assertArrayEquals(new String[]{},
                fileIO.readFile(tempDir.resolve("nonexistent.txt").toString()));
    }

    @Test
    @DisplayName("readFile handles file with special characters")
    void readFile_shouldHandleSpecialCharacters() throws IOException {
        File file = tempDir.resolve("special.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("héllo\nwörld\n");
        }
        assertArrayEquals(new String[]{"héllo", "wörld"},
                fileIO.readFile(file.getAbsolutePath()));
    }
}
