package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGPTOSS_P3 {

    private final NameSearch ns = new NameSearch();
    private final FileIO io = new FileIO();

    @TempDir
    File tempDir;

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmptyArray() throws IOException {
        File file = new File(tempDir, "names.txt");
        try (var w = new FileWriter(file)) {
            w.write("alice\nbob\ncarl");
        }
        String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "z");
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches_returnsArray() throws IOException {
        File file = new File(tempDir, "names.txt");
        try (var w = new FileWriter(file)) {
            w.write("charlie\ncharles\nalice\ncharles");
        }
        String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "char");
        assertArrayEquals(new String[]{"charlie", "charles", "charles"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile_returnsEmptyArray() throws IOException {
        File file = new File(tempDir, "empty.txt");
        file.createNewFile();
        String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "a");
        assertEquals(0, result.length);
    }
}
