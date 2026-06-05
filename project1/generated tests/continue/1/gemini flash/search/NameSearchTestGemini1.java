package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class NameSearchTestGemini1 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    public void testFindNamesInFileThatStartWith(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("names.txt");
        String[] names = {"Antonis", "Anna", "Bob", "Alice"};
        
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            for (String name : names) {
                writer.write(name + System.lineSeparator());
            }
        }

        String[] resultA = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        Assertions.assertArrayEquals(new String[]{"Antonis", "Anna", "Alice"}, resultA);

        String[] resultAn = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "An");
        Assertions.assertArrayEquals(new String[]{"Antonis", "Anna"}, resultAn);

        String[] resultZ = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Z");
        Assertions.assertEquals(0, resultZ.length);
    }
}
