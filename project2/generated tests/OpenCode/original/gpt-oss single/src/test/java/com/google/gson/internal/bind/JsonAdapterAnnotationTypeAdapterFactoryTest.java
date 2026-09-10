import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

/**
 * Unit tests for {@link JsonAdapterAnnotationTypeAdapterFactory} covering decision points in
 * {@code create} and {@code getTypeAdapter}.
 */
public class JsonAdapterAnnotationTypeAdapterFactoryTest {

  // Helper constructor constructor using a real Gson instance.  The constructor constructor
  // will simply instantiate adapters via reflection, which is sufficient for our dummy
  // adapters/serializers.
  private static final ConstructorConstructor CONSTRUCTOR_CONSTRUCTOR =
      new ConstructorConstructor(new GsonBuilder().create());

  private static final Gson GSON = new Gson();

  /** Dummy {@link TypeAdapter} implementation used to verify the TypeAdapter branch. */
  static class DummyAdapter extends TypeAdapter<Object> {
    @Override public void write(com.google.gson.stream.JsonWriter out, Object value) throws IOException {
      out.nullValue();
    }

    @Override public Object read(com.google.gson.stream.JsonReader in) throws IOException {
      return null;
    }
  }

  @JsonAdapter(DummyAdapter.class)
  static class DummyTypeAdapterClass {}

  /** Dummy {@link TypeAdapterFactory} implementation used to verify the factory branch. */
  static class DummyFactory implements TypeAdapterFactory {
    @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      // Return a generic adapter that simply yields null.
      return new TypeAdapter<T>() {
        @Override public void write(com.google.gson.stream.JsonWriter out, T value) throws IOException { out.nullValue(); }

        @Override public T read(com.google.gson.stream.JsonReader in) throws IOException { return null; }
      };
    }
  }

  @JsonAdapter(DummyFactory.class)
  static class DummyFactoryClass {}

  /** Dummy {@link JsonSerializer} implementation. */
  static class DummySerializer implements JsonSerializer<Object> {
    @Override public com.google.gson.JsonElement serialize(Object src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
      return new com.google.gson.JsonPrimitive("dummy");
    }
  }

  /** Dummy {@link JsonDeserializer} implementation. */
  static class DummyDeserializer implements JsonDeserializer<Object> {
    @Override public Object deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
      return "deserialized";
    }
  }

  @JsonAdapter(value = DummySerializer.class, nullSafe = true)
  static class DummySerializerClass {}

  @JsonAdapter(value = DummyDeserializer.class)
  static class DummyDeserializerClass {}

  /** Class without any annotation – create() should return null. */
  static class NoAnnotationClass {}

  @Test
  public void testCreateWithTypeAdapterAnnotation() {
    JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(CONSTRUCTOR_CONSTRUCTOR);
    TypeAdapter<Object> adapter = factory.create(GSON, TypeToken.get(DummyTypeAdapterClass.class));
    Assertions.assertNotNull(adapter, "Adapter must not be null for @JsonAdapter of TypeAdapter");
    Assertions.assertTrue(adapter instanceof DummyAdapter);
  }

  @Test
  public void testCreateWithFactoryAnnotation() {
    JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(CONSTRUCTOR_CONSTRUCTOR);
    TypeAdapter<Object> adapter = factory.create(GSON, TypeToken.get(DummyFactoryClass.class));
    Assertions.assertNotNull(adapter, "Adapter must not be null for @JsonAdapter of TypeAdapterFactory");
  }

  @Test
  public void testCreateWithSerializerAnnotation() {
    JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(CONSTRUCTOR_CONSTRUCTOR);
    TypeAdapter<Object> adapter = factory.create(GSON, TypeToken.get(DummySerializerClass.class));
    Assertions.assertNotNull(adapter, "Adapter must not be null for @JsonAdapter of JsonSerializer");
  }

  @Test
  public void testCreateWithDeserializerAnnotation() {
    JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(CONSTRUCTOR_CONSTRUCTOR);
    TypeAdapter<Object> adapter = factory.create(GSON, TypeToken.get(DummyDeserializerClass.class));
    Assertions.assertNotNull(adapter, "Adapter must not be null for @JsonAdapter of JsonDeserializer");
  }

  @Test
  public void testCreateWithInvalidAdapterClassThrows() {
    JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(CONSTRUCTOR_CONSTRUCTOR);
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        factory.create(GSON, TypeToken.get(String.class)),
        "Expected IllegalArgumentException for invalid @JsonAdapter target"
    );
  }

  @Test
  public void testCreateWithoutAnnotationReturnsNull() {
    JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(CONSTRUCTOR_CONSTRUCTOR);
    TypeAdapter<Object> adapter = factory.create(GSON, TypeToken.get(NoAnnotationClass.class));
    Assertions.assertNull(adapter, "Adapter must be null when no @JsonAdapter present");
  }
}
