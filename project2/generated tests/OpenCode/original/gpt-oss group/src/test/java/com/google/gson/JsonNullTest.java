import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonNull unit tests")
class JsonNullTest {

    @Test
    void singletonInstanceVerification() {
        JsonNull a = JsonNull.INSTANCE;
        JsonNull b = new JsonNull();
        // The constructor is deprecated but still usable;
        // all instances should behave as the singleton for equals/hashCode.
        assertSame(a, JsonNull.INSTANCE);
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void deepCopyReturnsSameInstance() {
        JsonNull a = JsonNull.INSTANCE;
        assertSame(a, a.deepCopy());
    }

    @Test
    void equalsHandlesNonJsonNull() {
        JsonElement e = JsonNull.INSTANCE;
        assertFalse(e.equals(new Object()));
    }
}
