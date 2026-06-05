package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class NameSearchTestQwen2_5_P3 {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources/names.txt, A, [Alicia]",
        "src/test/resources/names.txt, J, [John, Jessica]"
    })
    public void testFindNamesInFileThatStartWith(String filepath, String prefix, String[] expected) {
        FileIO fileIo = new FileIO();
        NameSearch nameSearch = new NameSearch();
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIo, prefix);
        assertArrayEquals(expected, result);
    }
}