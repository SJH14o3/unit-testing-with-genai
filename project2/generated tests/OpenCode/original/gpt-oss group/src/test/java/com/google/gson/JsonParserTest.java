import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonParser tests")
class JsonParserTest {

    @Test
    void parseStringValid() {
        JsonElement e = JsonParser.parseString("{\"name\": \"John\"}");
        assertTrue(e.isJsonObject());
        JsonObject obj = e.getAsJsonObject();
        assertEquals("John", obj.get("name").getAsString());
    }

    @Test
    void parseStringMultipleElementsThrows() {
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> JsonParser.parseString("1 2"));
    }

    @Test
    void parseReaderTrailingDataThrows() {
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> JsonParser.parseReader(new java.io.StringReader("{\"a\":1\n\"b\":2")) );
    }

    @Test
    void parseReaderEmptyReturnsJsonNull() {
        JsonElement e = JsonParser.parseString("" );
        assertTrue(e.isJsonNull());
    }
}
