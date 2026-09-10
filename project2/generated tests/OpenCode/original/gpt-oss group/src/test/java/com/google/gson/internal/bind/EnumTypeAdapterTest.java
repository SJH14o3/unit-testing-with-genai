import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class EnumTypeAdapterTest {

    enum Color { RED, GREEN @java.lang.SuppressWarnings("unused") , BLUE }

    @Test
    void readAndWrite_simpleEnum() {
        Gson gson = new Gson();
        var adapter = new com.google.gson.internal.bind.EnumTypeAdapter<>(Color.class);
        String json = "\"RED\"";
        Color color = adapter.read(new com.google.gson.stream.JsonReader(new java.io.StringReader(json)));
        assertEquals(Color.RED, color);
        java.io.StringWriter writer = new java.io.StringWriter();
        adapter.write(new com.google.gson.stream.JsonWriter(writer), Color.BLUE);
        assertEquals("\"BLUE\"", writer.toString());
    }
}
