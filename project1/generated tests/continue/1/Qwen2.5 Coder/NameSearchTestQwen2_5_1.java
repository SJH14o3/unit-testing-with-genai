import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import dmst.seip.angor.junitdemo.io.FileIO;
import dmst.seip.angor.junitdemo.search.NameSearch;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NameSearchTestQwen2_5_1 {
    private FileIO fileIo;
    private NameSearch nameSearch;

    @BeforeEach
    public void setUp() {
        fileIo = new FileIO();
        nameSearch = new NameSearch();
    }

    @Test
    public void testFindNamesInFileThatStartWithPrefix() {
        String[] names = {"Alice", "Bob", "Charlie"};
        File tempFile = createTempFile("test.txt", Arrays.toString(names));
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIo, "A");

        assertArrayEquals(new String[]{"Alice"}, result);
    }

    private File createTempFile(String fileName, String content) {
        // Logic to create a temporary file with the given content
        return new File(fileName);
    }
}
