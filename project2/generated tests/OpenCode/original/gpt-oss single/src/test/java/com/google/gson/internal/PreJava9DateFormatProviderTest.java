import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PreJava9DateFormatProvider unit tests")
class PreJava9DateFormatProviderTest {

    @ParameterizedTest(name = "dateStyle={0}, timeStyle={1} -> pattern={2}")
    @CsvSource({
            // date styles
            "SH_SHORT,TS_SHORT,"+"M/d/yy h:mm a",
            "SH_MEDIUM,TS_SHORT,"+"MMM d, yyyy h:mm a",
            "SH_LONG,TS_SHORT,"+"MMMM d, yyyy h:mm a",
            "SH_FULL,TS_SHORT,"+"EEEE, MMMM d, yyyy h:mm a",
            "SH_SHORT,TS_MEDIUM,"+"M/d/yy h:mm:ss a",
            "SH_SHORT,TS_FULL,"+"M/d/yy h:mm:ss a z",
            "SH_SHORT,TS_LONG,"+"M/d/yy h:mm:ss a z",
            "SH_MEDIUM,TS_MEDIUM,"+"MMM d, yyyy h:mm:ss a",
            "SH_MEDIUM,TS_FULL,"+"MMM d, yyyy h:mm:ss a z",
            "SH_MEDIUM,TS_LONG,"+"MMM d, yyyy h:mm:ss a z",
            "SH_LONG,TS_MEDIUM,"+"MMMM d, yyyy h:mm:ss a",
            "SH_LONG,TS_FULL,"+"MMMM d, yyyy h:mm:ss a z",
            "SH_LONG,TS_LONG,"+"MMMM d, yyyy h:mm:ss a z",
            "SH_FULL,TS_MEDIUM,"+"EEEE, MMMM d, yyyy h:mm:ss a",
            "SH_FULL,TS_FULL,"+"EEEE, MMMM d, yyyy h:mm:ss a z",
            "SH_FULL,TS_LONG,"+"EEEE, MMMM d, yyyy h:mm:ss a z"
    })
    void testPatternConstruction(String dateStyleName, String timeStyleName, String expectedPattern) {
        int dateStyle = styleFromName(dateStyleName);
        int timeStyle = styleFromName(timeStyleName);
        SimpleDateFormat sdf = (SimpleDateFormat) PreJava9DateFormatProvider.getUsDateTimeFormat(dateStyle, timeStyle);
        assertEquals(expectedPattern, sdf.toPattern());
    }

    @Test
    void testUnknownDateStyleThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                PreJava9DateFormatProvider.getUsDateTimeFormat(99, DateFormat.SHORT));
    }

    @Test
    void testUnknownTimeStyleThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.SHORT, 99));
    }

    private int styleFromName(String name) {
        return switch (name) {
            case "SH_SHORT" -> DateFormat.SHORT;
            case "SH_MEDIUM" -> DateFormat.MEDIUM;
            case "SH_LONG" -> DateFormat.LONG;
            case "SH_FULL" -> DateFormat.FULL;
            case "TS_SHORT" -> DateFormat.SHORT;
            case "TS_MEDIUM" -> DateFormat.MEDIUM;
            case "TS_FULL" -> DateFormat.FULL;
            case "TS_LONG" -> DateFormat.LONG;
            default -> throw new IllegalArgumentException("Unknown style name: " + name);
        };
    }
}
