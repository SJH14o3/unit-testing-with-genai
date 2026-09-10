import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonElement type checks and conversions")
class JsonElementTest {

    @Test
    void isXFlagsWork() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        JsonPrimitive prim = new JsonPrimitive("str");
        JsonNull nul = JsonNull.INSTANCE;
        assertTrue(obj.isJsonObject());
        assertTrue(arr.isJsonArray());
        assertTrue(prim.isJsonPrimitive());
        assertTrue(nul.isJsonNull());
        assertFalse(nul.isJsonObject());
    }

    @Test
    void getAsXThrowsOnWrongType() {
        JsonArray arr = new JsonArray();
        arr.add(1);
        assertThrows(IllegalStateException.class, () -> arr.getAsJsonObject());
        JsonObject obj = new JsonObject();
        obj.addProperty("key", 1);
        assertThrows(IllegalStateException.class, () -> obj.getAsJsonArray());
        JsonPrimitive prim = new JsonPrimitive(5);
        assertThrows(IllegalStateException.class, () -> prim.getAsJsonObject());
    }
}
