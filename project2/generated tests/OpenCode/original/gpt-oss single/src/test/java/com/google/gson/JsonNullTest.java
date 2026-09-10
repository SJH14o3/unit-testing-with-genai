import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonNull;

@DisplayName("JsonNull unit tests")
class JsonNullTest {

    @Test
    @DisplayName("deepCopy returns the singleton instance")
    void deepCopyReturnsInstance() {
        JsonNull instance = JsonNull.INSTANCE;
        JsonNull copy = instance.deepCopy();
        assertSame(instance, copy, "deepCopy should return the same singleton instance");
    }

    @Test
    @DisplayName("hashCode is constant across instances")
    void hashCodeConsistency() {
        JsonNull a = JsonNull.INSTANCE;
        JsonNull b = new JsonNull();
        assertEquals(a.hashCode(), b.hashCode(), "All JsonNull instances share the same hash code");
    }

    @Test
    @DisplayName("equals returns true for another JsonNull instance")
    void equalsWithAnotherInstance() {
        JsonNull a = JsonNull.INSTANCE;
        JsonNull b = new JsonNull();
        assertTrue(a.equals(b), "JsonNull should be equal to another JsonNull instance");
        assertTrue(b.equals(a), "equals should be symmetric");
    }

    @Test
    @DisplayName("equals returns false for non-JsonNull objects")
    void equalsWithOtherTypes() {
        JsonNull jsonNull = JsonNull.INSTANCE;
        assertFalse(jsonNull.equals(null), "equals should be false when compared to null");
        assertFalse(jsonNull.equals("not a JsonNull"), "equals should be false for unrelated types");
    }
}
