package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class NameSearchTestGPTOSS_P2 {

    private final NameSearch ns = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_match() throws IOException {
        @TempDir Path tempDir = java.nio.file.Files.createTempDirectory("names");
        Path file = tempDir.resolve("names.txt");
        Files.write(file, java.util.List.of("alice","bob","alex","carol"));
        String[] result = ns.findNamesInFileThatStartWith(file.toString(), fileIO, "al");
        assertArrayEquals(new String[]{"alice","alex"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatch() throws IOException {
        @TempDir Path tempDir = java.nio.file.Files.createTempDirectory("names");
        Path file = tempDir.resolve("names.txt");
        Files.write(file, java.util.List.of("bob","carol"));
        String[] result = ns.findNamesInFileThatStartWith(file.toString(), fileIO, "al");
        assertArrayEquals(new String[]{}, result);
    }
}
