package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileIOTestMistral_P3 {

    @TempDir
    Path tempDir;

    @Test
    void readFile_WhenFileDoesNotExist_ReturnsEmptyArray() {
        FileIO fileIO = new FileIO();
        assertArrayEquals(new String[]{}, fileIO.readFile("nonexistent.txt"));
    }

    @Test
    void readFile_WhenFileIsEmpty_ReturnsEmptyArray() throws IOException {
        FileIO fileIO = new FileIO();
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();
        assertArrayEquals(new String[]{}, fileIO.readFile(emptyFile.getAbsolutePath()));
    }

    @Test
    void readFile_WhenFileHasSingleLine_ReturnsArrayWithSingleElement() throws IOException {
        FileIO fileIO = new FileIO();
        File singleLineFile = tempDir.resolve("singleLine.txt").toFile();
        Files.write(singleLineFile.toPath(), "Hello, World!".getBytes());
        assertArrayEquals(new String[]{"Hello, World!"}, fileIO.readFile(singleLineFile.getAbsolutePath()));
    }

    @Test
    void readFile_WhenFileHasMultipleLines_ReturnsArrayWithMultipleElements() throws IOException {
        FileIO fileIO = new FileIO();
        File multiLineFile = tempDir.resolve("multiLine.txt").toFile();
        Files.write(multiLineFile.toPath(), ("Line 1\nLine 2\nLine 3").getBytes());
        assertArrayEquals(new String[]{"Line 1", "Line 2", "Line 3"}, fileIO.readFile(multiLineFile.getAbsolutePath()));
    }
}
