package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class FileIOTestGPTOSS_P1 {

    private File tempFile;

    @AfterEach
    void cleanUp() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    private void writeLines(File file, String... lines) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            for (String line : lines) {
                fw.write(line + System.lineSeparator());
            }
        }
    }

    @Test
    void testReadFile() throws Exception {
        tempFile = File.createTempFile("fileio_test", ".txt");
        writeLines(tempFile, "first", "second", "third");

        FileIO fio = new FileIO();
        String[] result = fio.readFile(tempFile.getAbsolutePath());

        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"first", "second", "third"}, result);
    }
}
