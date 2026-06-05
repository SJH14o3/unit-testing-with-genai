package dmst.seip.angor.junitdemo.io;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FileIOGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testReadFileValid() {
        FileIO fileIO = new FileIO();
        // Using the existing names_valid.txt in src/test/resources
        String filepath = "src/test/resources/names_valid.txt";
        String[] result = fileIO.readFile(filepath);
        Assert.assertNotNull(result);
    }

    @Test
    public void testReadFileInvalid() {
        FileIO fileIO = new FileIO();
        String filepath = "src/test/resources/does_not_exist.txt";
        String[] result = fileIO.readFile(filepath);
        Assert.assertEquals(0, result.length);
    }
}
