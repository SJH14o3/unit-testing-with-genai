package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(value = "dmst.seip.angor.junitdemo.io.FileIOTestGemma3_P3")
public class FileIOTestGemma3_P3 {

    private FileIO fileIO;

    @Test
    void readFile_validFile() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "test.txt");
        Files.write(filePath, Arrays.asList("line1", "line2", "line3").toArray(String[]::new));

        String[] expectedArray = {"line1", "line2", "line3"};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void readFile_emptyFile() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "empty.txt");
        Files.createFile(filePath);

        String[] expectedArray = {};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void readFile_invalidFile_throwsException() {
        fileIO = new FileIO();
        String invalidFilePath = "nonexistent_file.txt";
        assertThrows(Exception.class, () -> fileIO.readFile(invalidFilePath));
    }

    @Test
    void readFile_fileWithOneLine() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "one_line.txt");
        Files.write(filePath, Arrays.asList("single line").toArray(String[]::new));

        String[] expectedArray = {"single line"};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }
}