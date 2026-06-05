import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttribute;

import dmst.seip.angor.junitdemo.io.FileIO;
import dmst.seip.angor.junitdemo.search.NameSearch;

public class NameSearchTestGemma3_P2 {

    private NameSearch nameSearch = new NameSearch();
    private FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_emptyFile() {
        Path tempFile = Files.createTempFile("empty");
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() {
        Path tempFile = Files.createTempFile("noMatch");
        Files.write(tempFile, "''\n                line1\n                line2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_singleMatchingPrefix() {
        Path tempFile = Files.createTempFile("singleMatch");
        Files.write(tempFile, "''\n                Aline1\n                line2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[]{"Aline1"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatchingPrefix() {
        Path tempFile = Files.createTempFile("multipleMatch");
        Files.write(tempFile, "''\n                Aline1\n                Aline2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[]{"Aline1", "Aline2"}, result);
    }
}
