import com.google.gson.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LongSerializationPolicyTest {

    private static final Long NON_NULL_VALUE = 123L;

    @Test
    @DisplayName("DEFAULT serialize non‑null long as number")
    void defaultSerializeNonNull() {
        JsonElement result = LongSerializationPolicy.DEFAULT.serialize(NON_NULL_VALUE);
        assertTrue(result.isJsonPrimitive());
        assertTrue(result.getAsJsonPrimitive().isNumber());
        assertEquals(123L, result.getAsJsonPrimitive().getAsLong());
    }

    @Test
    @DisplayName("DEFAULT serialize null as JsonNull")
    void defaultSerializeNull() {
        JsonElement result = LongSerializationPolicy.DEFAULT.serialize(null);
        assertEquals(JsonNull.INSTANCE, result);
    }

    @Test
    @DisplayName("STRING serialize non‑null long as string")
    void stringSerializeNonNull() {
        JsonElement result = LongSerializationPolicy.STRING.serialize(NON_NULL_VALUE);
        assertTrue(result.isJsonPrimitive());
        assertTrue(result.getAsJsonPrimitive().isString());
        assertEquals("123", result.getAsJsonPrimitive().getAsString());
    }

    @Test
    @DisplayName("STRING serialize null as JsonNull")
    void stringSerializeNull() {
        JsonElement result = LongSerializationPolicy.STRING.serialize(null);
        assertEquals(JsonNull.INSTANCE, result);
    }

    @Test
    @DisplayName("Both policies provide non‑null TypeAdapters")
    void typeAdaptersNonNull() {
        assertNotNull(LongSerializationPolicy.DEFAULT.typeAdapter());
        assertNotNull(LongSerializationPolicy.STRING.typeAdapter());
    }
}
