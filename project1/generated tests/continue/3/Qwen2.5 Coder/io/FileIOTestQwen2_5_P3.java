package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileIOTestQwen2_5_P3 {

    @Test
    public void testReadFile_nonEmptyFile() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("src/test/resources/non_empty_file.txt");
        assertEquals(1, result.length);
        assertEquals("Line 1", result[0]);
    }

    @Test
    public void testReadFile_emptyFile() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("src/test/resources/empty_file.txt");
        assertArrayEquals(new String[]{}, result);
    }
}