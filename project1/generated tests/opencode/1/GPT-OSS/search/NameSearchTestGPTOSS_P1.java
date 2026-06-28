package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dmst.seip.angor.junitdemo.io.FileIO;

class NameSearchTestGPTOSS_P1 {

    private File tempFile;

    @AfterEach
    void cleanUp() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    private void writeLines(File file, String... lines) throws Exception {
        try (FileWriter fw = new FileWriter(file)) {
            for (String line : lines) {
                fw.write(line + System.lineSeparator());
            }
        }
    }

    @Test
    void testFindNamesInFileThatStartWith() throws Exception {
        tempFile = File.createTempFile("names", ".txt");
        writeLines(tempFile, "Alice", "Alfred", "Bob", "Charlie", "Alfreda");

        FileIO fileIO = new FileIO();
        NameSearch ns = new NameSearch();
        String[] result = ns.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Al");

        assertArrayEquals(new String[]{"Alice", "Alfred", "Alfreda"}, result);
    }

    @Test
    void testFindNamesInFileThatStartWithNoMatch() throws Exception {
        tempFile = File.createTempFile("names", ".txt");
        writeLines(tempFile, "Alice", "Bob", "Charlie");

        FileIO fileIO = new FileIO();
        NameSearch ns = new NameSearch();
        String[] result = ns.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");

        assertArrayEquals(new String[0], result);
    }
}
