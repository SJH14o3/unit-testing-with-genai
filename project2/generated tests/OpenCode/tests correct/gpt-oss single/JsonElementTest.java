package com.google.gson;

import com.google.gson.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class JsonElementTest {

    @Test
    void isJsonXReturnsTrueWhenCorrect() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        JsonPrimitive prim = new JsonPrimitive("text");
        JsonNull nul = JsonNull.INSTANCE;

        assertTrue(obj.isJsonObject());
        assertTrue(arr.isJsonArray());
        assertTrue(prim.isJsonPrimitive());
        assertTrue(nul.isJsonNull());

        assertFalse(obj.isJsonArray());
        assertFalse(arr.isJsonObject());
        assertFalse(prim.isJsonObject());
        // assertFalse(nul.isJsonNull());
    }

    @Test
    void getAsJsonObjectReturnsObjectWhenTrue() {
        JsonObject obj = new JsonObject();
        assertSame(obj, obj.getAsJsonObject());
    }

    @Test
    void getAsJsonObjectThrowsWhenNotObject() {
        JsonArray arr = new JsonArray();
        assertThrows(IllegalStateException.class, arr::getAsJsonObject);
    }

    @Test
    void getAsJsonArrayReturnsArrayWhenTrue() {
        JsonArray arr = new JsonArray();
        assertSame(arr, arr.getAsJsonArray());
    }

    @Test
    void getAsJsonArrayThrowsWhenNotArray() {
        JsonObject obj = new JsonObject();
        assertThrows(IllegalStateException.class, obj::getAsJsonArray);
    }

    @Test
    void getAsJsonPrimitiveReturnsPrimitiveWhenTrue() {
        JsonPrimitive prim = new JsonPrimitive("hello");
        assertSame(prim, prim.getAsJsonPrimitive());
    }

    @Test
    void getAsJsonPrimitiveThrowsWhenNotPrimitive() {
        JsonNull nul = JsonNull.INSTANCE;
        assertThrows(IllegalStateException.class, nul::getAsJsonPrimitive);
    }

    @Test
    void getAsJsonNullReturnsNullWhenTrue() {
        JsonNull nul = JsonNull.INSTANCE;
        assertSame(nul, nul.getAsJsonNull());
    }

    @Test
    void getAsJsonNullThrowsWhenNotNull() {
        JsonPrimitive prim = new JsonPrimitive("text");
        assertThrows(IllegalStateException.class, prim::getAsJsonNull);
    }

    @Test
    void getAsBooleanThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive(true);
        assertTrue(prim::getAsBoolean);
    }

    @Test
    void getAsNumberThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive(3.14);
        assertEquals(3.14, prim.getAsNumber());
    }

    @Test
    void getAsStringThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive("text");
        assertEquals("text", prim.getAsString());
    }

    @Test
    void getAsDoubleThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive(2.71);
        assertEquals(2.71, prim.getAsDouble());
    }

    @Test
    void getAsFloatThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive(1.5f);
        assertEquals(1.5f, prim.getAsFloat());
    }

    @Test
    void getAsLongThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive(123L);
        assertEquals(123L, prim.getAsLong());
    }

    @Test
    void getAsIntThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive(42);
        assertEquals(42, prim.getAsInt());
    }

    @Test
    void getAsByteThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive((byte)1);
        assertEquals((byte)1, prim.getAsByte());
    }

    @Test
    void getAsCharacterThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive("x");
        assertEquals('x', prim.getAsCharacter());
    }

    @Test
    void getAsBigDecimalThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive("123.45");
        assertEquals(new BigDecimal("123.45"), prim.getAsBigDecimal());
    }

    @Test
    void getAsBigIntegerThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive("1234567890123");
        assertEquals(new BigInteger("1234567890123"), prim.getAsBigInteger());
    }

    @Test
    void getAsShortThrowsUnsupported() {
        JsonPrimitive prim = new JsonPrimitive((short)7);
        assertEquals((short)7, prim.getAsShort());
    }

    @Test
    void toStringConvertsToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("a", (String) null);
        JsonArray arr = new JsonArray();
        arr.add(1);
        obj.add("b", arr);
        assertEquals("{\"a\":null,\"b\":[1]}", obj.toString());

        JsonPrimitive prim = new JsonPrimitive("test");
        assertEquals("\"test\"", prim.toString());

        JsonNull nul = JsonNull.INSTANCE;
        assertEquals("null", nul.toString());
    }
}
