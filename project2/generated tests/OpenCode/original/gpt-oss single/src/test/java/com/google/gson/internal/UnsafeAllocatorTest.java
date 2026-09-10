import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UnsafeAllocatorTest {

    @Test
    @DisplayName("INSTANCE should be available")
    void instanceNonNull() {
        assertNotNull(com.google.gson.internal.UnsafeAllocator.INSTANCE);
    }

    @Test
    @DisplayName("Allocating a non-instantiable type throws AssertionError")
    void nonInstantiableThrows() {
        // Integer is a concrete class but final; should be instantiable by unsafe.
        // Instead use an interface to guarantee non-instantiable path.
        assertThrows(AssertionError.class, () -> {
            com.google.gson.internal.UnsafeAllocator.INSTANCE.newInstance(java.util.List.class);
        });
    }
}
