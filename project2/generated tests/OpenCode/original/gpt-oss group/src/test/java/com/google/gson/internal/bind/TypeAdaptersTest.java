import com.google.gson.*;
import com.google.gson.stream.*;
import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class TypeAdaptersTest {

    @Test
    void stringAdapterReadsAndWrites() throws IOException {
        TypeAdapter<String> adapter = com.google.gson.internal.bind.TypeAdapters.STRING;
        String json = "\"hello\"";
        JsonReader reader = new JsonReader(new StringReader(json));
        String value = adapter.read(reader);
        assertEquals("hello", value);
        StringWriter sw = new StringWriter();
        adapter.write(new JsonWriter(sw), "world");
        assertEquals("\"world\"", sw.toString());
    }
}
