import com.google.gson.Gson;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

class CollectionTypeAdapterFactoryTest {
    private Gson gson;
    private CollectionTypeAdapterFactory factory;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        factory = new CollectionTypeAdapterFactory(new ConstructorConstructor(gson));
    }

    @Test
    @DisplayName("create returns null for non-collection types")
    void createNonCollection() {
        var result = factory.create(gson, TypeToken.get(String.class));
        assertNull(result, "Type adapter should be null for non-Collection types");
    }

    @Test
    @DisplayName("create returns non-null for collection types")
    void createCollection() {
        var typeToken = new TypeToken<List<Integer>>() {};
        var adapter = factory.create(gson, typeToken);
        assertNotNull(adapter, "Adapter should not be null for Collection types");
    }

    @Test
    @DisplayName("Adapter read returns null for JSON null token")
    void adapterReadNull() throws Exception {
        var typeToken = new TypeToken<List<Integer>>() {};
        var adapter = factory.create(gson, typeToken);
        JsonReader reader = new JsonReader(new StringReader("null"));
        var result = adapter.read(reader);
        assertNull(result, "Adapter should return null when JSON token is null");
    }

    @Test
    @DisplayName("Adapter read parses array of integers")
    void adapterReadArray() throws Exception {
        var typeToken = new TypeToken<List<Integer>>() {};
        var adapter = factory.create(gson, typeToken);
        JsonReader reader = new JsonReader(new StringReader("[1,2,3]"));
        var result = adapter.read(reader);
        assertNotNull(result);
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    @DisplayName("Adapter write outputs null for null collection")
    void adapterWriteNull() throws Exception {
        var typeToken = new TypeToken<List<Integer>>() {};
        var adapter = factory.create(gson, typeToken);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, null);
        assertEquals("null", writer.toString());
    }

    @Test
    @DisplayName("Adapter write outputs array for collection")
    void adapterWriteArray() throws Exception {
        var typeToken = new TypeToken<List<Integer>>() {};
        var adapter = factory.create(gson, typeToken);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        List<Integer> list = List.of(4,5,6);
        adapter.write(jsonWriter, list);
        assertEquals("[4,5,6]", writer.toString());
    }
}
