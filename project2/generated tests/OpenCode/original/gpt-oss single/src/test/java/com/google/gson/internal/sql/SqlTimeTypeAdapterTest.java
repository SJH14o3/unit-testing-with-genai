import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.sql.SqlTimeTypeAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class SqlTimeTypeAdapterTest {

    private final SqlTimeTypeAdapter adapter = new SqlTimeTypeAdapter();

    @Test
    @DisplayName("read returns null when JSON value is null")
    void readNull() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("null"));
        // json reader needs to read the token
        assertEquals(JsonToken.NULL, reader.peek());
        Time result = adapter.read(reader);
        assertNull(result);
    }

    @Test
    @DisplayName("read parses valid 12‑hour time string")
    void readValid() throws Exception {
        String json = "\"01:23:45 PM\""; // 13:23:45 in 24‑hour
        JsonReader reader = new JsonReader(new StringReader(json));
        Time result = adapter.read(reader);
        assertNotNull(result);
        // Verify that the time corresponds to 13:23:45
        Time expected = Time.valueOf("13:23:45");
        assertEquals(expected.getTime(), result.getTime());
    }

    @Test
    @DisplayName("read throws JsonSyntaxException for invalid format")
    void readInvalid() {
        JsonReader reader = new JsonReader(new StringReader("\"not a time\""));
        Executable ex = () -> adapter.read(reader);
        JsonSyntaxException thrown = assertThrows(JsonSyntaxException.class, ex);
        assertTrue(thrown.getMessage().contains("Failed parsing"));
    }

    @Test
    @DisplayName("write outputs null when value is null")
    void writeNull() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, null);
        writer.flush();
        assertEquals("null", sw.toString());
    }

    @Test
    @DisplayName("write formats Time object to 12‑hour string")
    void writeValid() throws Exception {
        Time time = Time.valueOf("13:45:59"); // 1:45:59 PM
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, time);
        writer.flush();
        // Result should be "01:45:59 PM"
        assertEquals("\"01:45:59 PM\"", sw.toString());
    }
}
