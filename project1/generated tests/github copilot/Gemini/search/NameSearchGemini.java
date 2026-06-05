package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class NameSearchGemini {

    @Test
    public void testFindNamesInFileThatStartWith() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIOMock = mock(FileIO.class);
        
        String filepath = "dummy_path.txt";
        String[] mockNames = {"Alice", "Bob", "Charlie", "Alex", "David"};
        when(fileIOMock.readFile(filepath)).thenReturn(mockNames);

        String[] expected = {"Alice", "Alex"};
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIOMock, "Al");

        Assert.assertArrayEquals(expected, result);
        verify(fileIOMock, times(1)).readFile(filepath);
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatch() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIOMock = mock(FileIO.class);
        
        String filepath = "dummy_path.txt";
        String[] mockNames = {"Alice", "Bob", "Charlie", "Alex", "David"};
        when(fileIOMock.readFile(filepath)).thenReturn(mockNames);

        String[] expected = {};
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIOMock, "Z");

        Assert.assertArrayEquals(expected, result);
        verify(fileIOMock, times(1)).readFile(filepath);
    }
}
