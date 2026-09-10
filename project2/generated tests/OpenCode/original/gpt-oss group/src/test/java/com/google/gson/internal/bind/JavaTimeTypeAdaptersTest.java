import com.google.gson.*;
import com.google.gson.stream.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class JavaTimeTypeAdaptersTest {

    @Test
    void localDateTimeRoundTrip() throws IOException {
        Gson gson = new Gson();
        TypeAdapters.TypeAdapterFactory factory = com.google.gson.internal.bind.TypeAdapters.javaTimeTypeAdapterFactory();
        TypeAdapter<LocalDateTime> adapter = factory.create(gson, TypeToken.get(LocalDateTime.class));
        LocalDateTime now = LocalDateTime.now();
        StringWriter sw = new StringWriter();
        adapter.write(new JsonWriter(sw), now);
        JsonReader reader = new JsonReader(new StringReader(sw.toString()));
        LocalDateTime back = adapter.read(reader);
        assertEquals(now, back);
    }
}
