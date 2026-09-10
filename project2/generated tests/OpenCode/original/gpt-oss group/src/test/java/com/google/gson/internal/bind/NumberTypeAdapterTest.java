import com.google.gson.*;
import com.google.gson.stream.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

class NumberTypeAdapterTest {

    @Test
    void readNumber_fromJson() throws IOException {
        NumberTypeAdapter adapter = new NumberTypeAdapter(ToNumberStrategy.LAZILY_PARSED_NUMBER);
        String json = "123";
        JsonReader reader = new JsonReader(new StringReader(json));
        Number n = adapter.read(reader);
        assertEquals(123, n.intValue());
    }

    @Test
    void writeNumber_toJson() throws IOException {
        NumberTypeAdapter adapter = new NumberTypeAdapter(ToNumberStrategy.LAZILY_PARSED_NUMBER);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, 12.34);
        assertEquals("12.34", sw.toString());
    }
}
