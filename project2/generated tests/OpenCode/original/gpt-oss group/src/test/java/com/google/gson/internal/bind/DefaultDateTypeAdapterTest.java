import com.google.gson.*;
import com.google.gson.stream.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.text.*;
import java.time.*;

class DefaultDateTypeAdapterTest {

    @Test
    void serializeAndDeserialize_pattern() throws Exception {
        DefaultDateTypeAdapter.DateType<java.util.Date> dateType = DefaultDateTypeAdapter.DateType.DATE;
        DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(dateType, "yyyy-MM-dd", java.util.Date.class, java.util.Date.class);
        java.util.Date now = new java.util.Date();
        java.io.StringWriter sw = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, now);
        String json = sw.toString();
        JsonReader reader = new JsonReader(new java.io.StringReader(json));
        java.util.Date parsed = adapter.read(reader);
        assertEquals(now.getDate(), parsed.getDate());
    }
}
