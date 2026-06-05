package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGemma3_1 {

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = {"Alice"};
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(expected, actual);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = new String[0];
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        assertArrayEquals(expected, actual);
    }
}

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGemma3_1 {

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = {"Alice"};
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(expected, actual);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = new String[0];
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        assertArrayEquals(expected, actual);
    }
}
