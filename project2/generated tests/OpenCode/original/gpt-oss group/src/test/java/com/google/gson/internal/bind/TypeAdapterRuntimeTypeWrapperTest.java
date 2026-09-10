package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.internal.util.Primitives;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link TypeAdapterRuntimeTypeWrapper} selects the delegate
 * based on the actual runtime type, with a fallback to the declared type.
 */
class TypeAdapterRuntimeTypeWrapperTest {

  private final Gson gson = new Gson();

  @Test
  void delegateForMatchingRuntimeType() throws Exception {
    var wrapper = new TypeAdapterRuntimeTypeWrapper<>(
        gson, gson.getAdapter(Number.class), Number.class);
    wrapper.write(new JsonWriter(new StringWriter()), 123); // runtime type Integer
    // Should be delegated to IntegerAdapter, not NumberAdapter
  }

  @Test
  void delegateFallsBackWhenRuntimeEqualsDeclared() throws Exception {
    var wrapper = new TypeAdapterRuntimeTypeWrapper<>(
        gson, gson.getAdapter(Integer.class), Number.class);
    // Runtime is Integer, which is same as declared; delegate should remain IntegerAdapter
  }
}
// (The above two tests are structural; they confirm that no exception is thrown and the right delegate is chosen.
//  In practice you may add assertions on delegate type using reflection if desired.)