import com.google.gson.*;
import com.google.gson.stream.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonAdapterAnnotationTypeAdapterFactoryTest {

    static class TestClass {
        @com.google.gson.annotations.JsonAdapter(MyAdapter.class)
        int value;
    }

    static class MyAdapter extends TypeAdapter<Integer> {
        @Override void write(JsonWriter out, Integer v) throws IOException { out.value(v); }
        @Override Integer read(JsonReader in) throws IOException { return in.nextInt(); }
    }

    @Test
    void factoryProvidesAdapter() {
        ConstructorConstructor cc = new ConstructorConstructor(new Gson());
        JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(cc);
        Gson gson = new Gson();
        TypeAdapter<?> adapter = factory.create(gson, TypeToken.get(TestClass.class));
        assertNotNull(adapter);
    }
}
