import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonPrimitive conversion tests")
class JsonPrimitiveTest {

    @Test
    void booleanConversion() {
        JsonPrimitive p = new JsonPrimitive(true);
        assertTrue(p.isBoolean());
        assertTrue(p.getAsBoolean());
        assertEquals("true", p.getAsString());

        JsonPrimitive stringTrue = new JsonPrimitive("true");
        assertTrue(stringTrue.getAsBoolean());
        JsonPrimitive stringFalse = new JsonPrimitive("FALSE");
        assertFalse(stringFalse.getAsBoolean());
    }

    @Test
    void numberParsing() {
        JsonPrimitive num = new JsonPrimitive(42);
        assertEquals(42, num.getAsInt());
        assertEquals(42L, num.getAsLong());

        JsonPrimitive stringNum = new JsonPrimitive("123.45");
        assertEquals(123.45, stringNum.getAsDouble(), 1e-9);
        assertEquals(123.45F, stringNum.getAsFloat(), 1e-6);
    }

    @Test
    void stringConversion() {
        JsonPrimitive s = new JsonPrimitive("abc");
        assertEquals("abc", s.getAsString());
    }

    @Test
    void characterHandling() {
        JsonPrimitive p = new JsonPrimitive('x');
        assertEquals("x", p.getAsString());
        assertEquals('x', p.getAsCharacter());
    }

    @Test
    void emptyStringCharacterException() {
        JsonPrimitive p = new JsonPrimitive("");
        assertThrows(UnsupportedOperationException.class, () -> p.getAsCharacter());
    }
}
