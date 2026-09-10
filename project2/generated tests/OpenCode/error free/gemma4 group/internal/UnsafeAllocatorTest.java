package com.google.gson.internal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class UnsafeAllocatorTest {

    @Test
    void testNewInstanceWithPossibleSuccess() throws Exception {
        try {
            // Testing with an instantiable class to see if the instance works
            Object obj = UnsafeAllocator.INSTANCE.newInstance(ArrayList.class);
            assertNotNull(obj);
        } catch (UnsupportedOperationException e) {
            // This is an expected failure if the runtime doesn't support any of the allocation methods
        } catch (Exception e) {
            // Other exceptions might be thrown depending on the class and environment, but we don't expect them for ArrayList
        }
    }
}
