package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dmst.seip.angor.junitdemo.io.FileIO;

/**
 * Comprehensive unit tests for {@link NameSearch} with MC/DC coverage,
 * boundary value analysis, mocking, and edge case testing.
 */
@DisplayName("NameSearch Tests")
public class NameSearchClaudeTest {

    private NameSearch nameSearch;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
    }

    // =========================================================================
    // findNamesInFileThatStartWith - Integration tests (real FileIO)
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - Integration with real FileIO")
    class IntegrationTests {

        @Test
        @DisplayName("finds names starting with 'M' in test resource file")
        void findNames_prefixM_returnsMatches() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "M");

            assertEquals(2, result.length);
            assertEquals("Mitch", result[0]);
            assertEquals("Matt", result[1]);
        }

        @Test
        @DisplayName("finds names starting with 'C' in test resource file")
        void findNames_prefixC_returnsMatches() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "C");

            assertEquals(2, result.length);
            assertEquals("C.J", result[0]);
            assertEquals("Caroline", result[1]);
        }

        @Test
        @DisplayName("prefix with no match returns empty array")
        void findNames_noMatch_returnsEmpty() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "Z");

            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // =========================================================================
    // findNamesInFileThatStartWith - Mocked FileIO tests
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - Mocked FileIO")
    @ExtendWith(MockitoExtension.class)
    class MockedTests {

        @Mock
        private FileIO mockFileIO;

        // --- MC/DC for Decision: name.startsWith(prefix) ---
        // Single condition: true → add to list, false → skip
        // Two tests: one matching, one not matching → full MC/DC

        @Test
        @DisplayName("MC/DC: name.startsWith(prefix) = true → included in results")
        void findNames_matchingPrefix_included() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("MC/DC: name.startsWith(prefix) = false → excluded from results")
        void findNames_nonMatchingPrefix_excluded() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(0, result.length);
        }

        // --- Comprehensive matching tests ---

        @Test
        @DisplayName("multiple names, some match prefix")
        void findNames_mixedNames_returnsOnlyMatches() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Alex", "Charlie", "Amanda"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(2, result.length);
            assertEquals("Alice", result[0]);
            assertEquals("Alex", result[1]);
        }

        @Test
        @DisplayName("all names match prefix")
        void findNames_allMatch_returnsAll() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Alex", "Amanda"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(3, result.length);
        }

        @Test
        @DisplayName("no names match prefix")
        void findNames_nonMatch_returnsEmpty() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Z");

            assertEquals(0, result.length);
        }

        // --- Empty prefix (boundary) ---

        @Test
        @DisplayName("empty prefix matches all names (every string starts with empty)")
        void findNames_emptyPrefix_matchesAll() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "");

            assertEquals(3, result.length);
            assertEquals("Alice", result[0]);
            assertEquals("Bob", result[1]);
            assertEquals("Charlie", result[2]);
        }

        // --- Empty file ---

        @Test
        @DisplayName("file with no names returns empty array")
        void findNames_emptyFile_returnsEmpty() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(0, result.length);
        }

        // --- Case sensitivity ---

        @Test
        @DisplayName("prefix matching is case-sensitive")
        void findNames_caseSensitive_exactMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"alice", "Alice", "ALICE"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("lowercase prefix does not match uppercase names")
        void findNames_lowercasePrefix_noUppercaseMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "a");

            assertEquals(0, result.length);
        }

        // --- Prefix equals name exactly ---

        @Test
        @DisplayName("prefix equal to full name matches")
        void findNames_prefixEqualsName_matches() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Alice");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        // --- Prefix longer than any name ---

        @Test
        @DisplayName("prefix longer than any name matches nothing")
        void findNames_prefixLongerThanNames_noMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Al", "Bo"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Alice");

            assertEquals(0, result.length);
        }

        // --- Verify FileIO interaction ---

        @Test
        @DisplayName("verifies readFile is called once with correct filepath")
        void findNames_verifiesFileIOInteraction() {
            when(mockFileIO.readFile("mypath/names.txt"))
                    .thenReturn(new String[]{"Test"});

            nameSearch.findNamesInFileThatStartWith("mypath/names.txt", mockFileIO, "T");

            verify(mockFileIO, times(1)).readFile("mypath/names.txt");
            verifyNoMoreInteractions(mockFileIO);
        }

        // --- Special characters in prefix ---

        @Test
        @DisplayName("prefix with special characters")
        void findNames_specialCharPrefix_matchesCorrectly() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"C.J", "Caroline", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "C.");

            assertEquals(1, result.length);
            assertEquals("C.J", result[0]);
        }

        // --- Names with empty strings ---

        @Test
        @DisplayName("file content with empty string names, non-empty prefix → empty strings don't match")
        void findNames_emptyStringNames_nonEmptyPrefixNoMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"", "", "Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("empty prefix matches empty string names too")
        void findNames_emptyPrefix_matchesEmptyNames() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"", "Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "");

            assertEquals(2, result.length);
        }

        // --- Parameterized prefix tests ---

        @ParameterizedTest(name = "prefix ''{0}'' finds matching names")
        @ValueSource(strings = {"M", "N", "C", "J", "L", "S", "Lo"})
        @DisplayName("parameterized: various prefixes on names_valid content")
        void findNames_variousPrefixes_returnCorrectCounts(String prefix) {
            // Simulate names_valid.txt content
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{
                            "Mitch", "Newmie", "C.J", "Matt", "Caroline",
                            "Jason", "Lani", "Summer", "Logan"
                    });

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, prefix);

            // Verify each name in result actually starts with prefix
            for (String name : result) {
                assertTrue(name.startsWith(prefix),
                        "Expected '" + name + "' to start with '" + prefix + "'");
            }

            // Verify no matching names were missed
            String[] allNames = {"Mitch", "Newmie", "C.J", "Matt", "Caroline",
                    "Jason", "Lani", "Summer", "Logan"};
            long expectedCount = java.util.Arrays.stream(allNames)
                    .filter(n -> n.startsWith(prefix))
                    .count();
            assertEquals(expectedCount, result.length);
        }
    }

    // =========================================================================
    // Null argument tests (no mocking needed)
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - null arguments")
    @ExtendWith(MockitoExtension.class)
    class NullArgumentTests {

        @Mock
        private FileIO mockFileIO;

        @Test
        @DisplayName("null fileIo throws NullPointerException")
        void findNames_nullFileIO_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", null, "A"));
        }

        @Test
        @DisplayName("null prefix throws NullPointerException (in startsWith)")
        void findNames_nullPrefix_throwsNPE() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice"});

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, null));
        }

        @Test
        @DisplayName("null prefix with empty file → no NPE (loop not entered)")
        void findNames_nullPrefixEmptyFile_noException() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{});

            // Loop never executes, so null prefix doesn't cause startsWith call
            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, null);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("null filepath is passed through to FileIO (behavior depends on FileIO)")
        void findNames_nullFilepath_delegatesToFileIO() {
            when(mockFileIO.readFile(null))
                    .thenThrow(new NullPointerException());

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith(null, mockFileIO, "A"));
        }

        @Test
        @DisplayName("file content with null element causes NPE on startsWith")
        void findNames_fileReturnsNullElement_throwsNPE() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{null, "Alice"});

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A"));
        }
    }
}
