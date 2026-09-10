package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Modifier;
import java.text.DateFormat;

import com.google.gson.internal.Excluder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GsonBuilder}. Covers decision branches without external dependencies.
 */
@DisplayName("GsonBuilder Unit Tests")
class GsonBuilderTest {

    @Test
    @DisplayName("setVersion accepts valid positive double")
    void setVersionValid() {
        GsonBuilder builder = new GsonBuilder();
        assertDoesNotThrow(() -> builder.setVersion(1.2));
        // create() should succeed
        assertNotNull(builder.create());
    }

    @Test
    @DisplayName("setVersion throws for NaN and negative values")
    void setVersionInvalid() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.setVersion(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> builder.setVersion(-0.5));
    }

    @Test
    @DisplayName("setDateFormat(String) accepts valid pattern and rejects invalid one")
    void setDateFormatString() {
        GsonBuilder builder = new GsonBuilder();
        assertDoesNotThrow(() -> builder.setDateFormat("yyyy-MM-dd"));
        assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat("invalid#pattern"));
        // null should be accepted and reset pattern
        assertDoesNotThrow(() -> builder.setDateFormat(null));
    }

    @Test
    @DisplayName("setDateFormat(int) accepts valid constants, throws for invalid style")
    void setDateFormatInt() {
        GsonBuilder builder = new GsonBuilder();
        assertDoesNotThrow(() -> builder.setDateFormat(DateFormat.LONG));
        assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat(5));
    }

    @Test
    @DisplayName("setDateFormat(int,int) accepts valid styles and throws for invalid one")
    void setDateFormatIntInt() {
        GsonBuilder builder = new GsonBuilder();
        // valid styles
        assertDoesNotThrow(() -> builder.setDateFormat(DateFormat.FULL, DateFormat.FULL));
        // first style invalid
        assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat(5, DateFormat.LONG));
        // second style invalid
        assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat(DateFormat.SHORT, 10));
    }

    @Test
    @DisplayName("enableComplexMapKeySerialization sets flag correctly")
    void enableComplexMapKeySerialization() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        // access private field via reflection
        boolean flag = (boolean) GsonBuilder.class.getDeclaredField("complexMapKeySerialization").get(builder);
        assertTrue(flag);
    }

    @Test
    @DisplayName("excludeFieldsWithModifiers handles null and applies modifiers")
    void excludeFieldsWithModifiers() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        // Passing null should throw NPE
        assertThrows(NullPointerException.class, () -> builder.excludeFieldsWithModifiers((int[]) null));
        // Verify that calling with a modifier changes the internal excluder state (non‑null)
        Excluder old = builder.excluder;
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC);
        Excluder updated = builder.excluder;
        assertNotSame(old, updated);
    }

    @Test
    @DisplayName("serializeSpecialFloatingPointValues sets flag correctly")
    void serializeSpecialFloatingPointValues() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        boolean flag = (boolean) GsonBuilder.class.getDeclaredField("serializeSpecialFloatingPointValues").get(builder);
        assertTrue(flag);
    }

    @Test
    @DisplayName("disableHtmlEscaping sets flag correctly")
    void disableHtmlEscaping() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        boolean flag = (boolean) GsonBuilder.class.getDeclaredField("escapeHtmlChars").get(builder);
        assertFalse(flag);
    }

    @Test
    @DisplayName("setStrictness accepts non‑null values")
    void setStrictness() {
        GsonBuilder builder = new GsonBuilder();
        assertDoesNotThrow(() -> builder.setStrictness(Strictness.LEGACY_STRICT));
        assertDoesNotThrow(() -> builder.setStrictness(Strictness.LENIENT));
        assertThrows(NullPointerException.class, () -> builder.setStrictness(null));
    }

    @Test
    @DisplayName("setFieldNamingStrategy updates policy")
    void setFieldNamingStrategy() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingStrategy(FieldNamingPolicy.IDENTITY);
        FieldNamingStrategy actual = (FieldNamingStrategy) GsonBuilder.class.getDeclaredField("fieldNamingPolicy").get(builder);
        assertSame(FieldNamingPolicy.IDENTITY, actual);
    }

    @Test
    @DisplayName("setLongSerializationPolicy updates policy")
    void setLongSerializationPolicy() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.setLongSerializationPolicy(LongSerializationPolicy.DEFAULT);
        LongSerializationPolicy actual = (LongSerializationPolicy) GsonBuilder.class.getDeclaredField("longSerializationPolicy").get(builder);
        assertSame(LongSerializationPolicy.DEFAULT, actual);
    }
}
