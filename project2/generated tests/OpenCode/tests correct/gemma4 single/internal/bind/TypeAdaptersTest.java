package com.google.gson.internal.bind;

import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonToken;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.BitSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.junit.jupiter.api.Assertions.*;

class TypeAdaptersTest {

    private JsonReader createReader(String json) {
        return new JsonReader(new StringReader(json));
    }

    private String writeToString(java.util.function.Consumer<JsonWriter> writerAction) throws java.io.IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writerAction.accept(writer);
        return sw.toString();
    }

    @Test
    @DisplayName("CLASS adapter throws exception on write and read")
    void classAdapter_throwsException() throws java.io.IOException {
        JsonWriter writer = new JsonWriter(new StringWriter());
        assertThrows(UnsupportedOperationException.class, () -> TypeAdapters.CLASS.write(writer, String.class));
        JsonReader reader = createReader("\"java.lang.String\"");
        assertThrows(UnsupportedOperationException.class, () -> TypeAdapters.CLASS.read(reader));
    }

    @Test
    @DisplayName("BIT_SET adapter read and write")
    void bitSetAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                BitSet bitset = new BitSet();
                bitset.set(0);
                bitset.set(2);
                TypeAdapters.BIT_SET.write(writer, bitset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        JsonReader reader = createReader(json);
        BitSet readBitset = TypeAdapters.BIT_SET.read(reader);
        assertTrue(readSD(readBitset, 0));
        assertTrue(readSD(readBitset, 2));
        assertFalse(readSD(readBitset, 1));

        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BIT_SET.read(createReader("[2]")));
        assertDoesNotThrow(() -> TypeAdapters.BIT_SET.read(createReader("[true]")));
    }

    private boolean readSD(BitSet bitset, int i) {
        return bitset.get(i);
    }

    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false",
            "\"true\", true",
            "\"false\", false"
    })
    @DisplayName("BOOLEAN adapter read and write")
    void booleanAdapter_works(String input, boolean expected) throws java.io.IOException {
        JsonReader reader = createReader(input);
        assertEquals(expected, TypeAdapters.BOOLEAN.read(reader));

        String json = writeToString(writer -> {
            try {
                TypeAdapters.BOOLEAN.write(writer, expected);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(expected ? "true" : "false", json);
    }

    @Test
    @DisplayName("BOOLEAN_AS_STRING adapter read and write")
    void booleanAsStringAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.BOOLEAN_AS_STRING.write(writer, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("\"true\"", json);

        assertEquals(true, TypeAdapters.BOOLEAN_AS_STRING.read(createReader("\"true\"")));
        assertEquals(false, TypeAdapters.BOOLEAN_AS_STRING.read(createReader("\"false\"")));
        assertNull(TypeAdapters.BOOLEAN_AS_STRING.read(createReader("null")));
    }

    @Test
    @DisplayName("BYTE adapter read and write")
    void byteAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.BYTE.write(writer, (byte) 127);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("127", json);

        assertEquals((byte) 127, TypeAdapters.BYTE.read(createReader("127")));
        assertEquals((byte) 0, TypeAdapters.BYTE.read(createReader("0")));

        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BYTE.read(createReader("256")));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BYTE.read(createReader("-129")));
    }

    @Test
    @DisplayName("SHORT adapter read and write")
    void shortAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.SHORT.write(writer, (short) 32767);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("32767", json);

        assertEquals((short) 32767, TypeAdapters.SHORT.read(createReader("32767")));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.SHORT.read(createReader("65536")));
    }

    @Test
    @DisplayName("INTEGER adapter read and write")
    void integerAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.INTEGER.write(writer, 123);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("123", json);

        assertEquals(123, TypeAdapters.INTEGER.read(createReader("123")));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.INTEGER.read(createReader("\"not_an_int\"")));
    }

    @Test
    @DisplayName("ATOMIC_INTEGER adapter read and write")
    void atomicIntegerAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.ATOMIC_INTEGER.write(writer, new AtomicInteger(42));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("42", json);

        assertEquals(42, TypeAdapters.ATOMIC_INTEGER.read(createReader("42")).get());
    }

    @Test
    @DisplayName("ATOMIC_BOOLEAN adapter read and write")
    void atomicBooleanAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.ATOMIC_BOOLEAN.write(writer, new AtomicBoolean(true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("true", json);

        assertTrue(TypeAdapters.ATOMIC_BOOLEAN.read(createReader("true")).get());
        assertFalse(TypeAdapters.ATOMIC_BOOLEAN.read(createReader("false")).get());
    }

    @Test
    @DisplayName("ATOMIC_INTEGER_ARRAY adapter read and write")
    void atomicIntegerArrayAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.ATOMIC_INTEGER_ARRAY.write(writer, new AtomicIntegerArray(new int[]{1, 2}));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("[1,2]", json);

        AtomicIntegerArray array = TypeAdapters.ATOMIC_INTEGER_ARRAY.read(createReader("[1,2]"));
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
    }

    @Test
    @DisplayName("LONG adapter read and write")
    void longAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.LONG.write(writer, 100L);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("100", json);

        assertEquals(100L, TypeAdapters.LONG.read(createReader("100")));
        assertNull(TypeAdapters.LONG.read(createReader("null")));
    }

    @Test
    @DisplayName("FLOAT adapter read and write")
    void floatAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.FLOAT.write(writer, 1.5f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("1.5", json);

        assertEquals(1.5f, TypeAdapters.FLOAT.read(createReader("1.5")));
        assertNull(TypeAdapters.FLOAT.read(createReader("null")));
    }

    @Test
    @DisplayName("DOUBLE adapter read and write")
    void doubleAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.DOUBLE.write(writer, 1.5d);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("1.5", json);

        assertEquals(1.5d, TypeAdapters.DOUBLE.read(createReader("1.5")));
        assertNull(TypeAdapters.DOUBLE.read(createReader("null")));
    }

    @Test
    @DisplayName("CHARACTER adapter read and write")
    void characterAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.CHARACTER.write(writer, 'a');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("\"a\"", json);

        assertEquals('a', TypeAdapters.CHARACTER.read(createReader("\"a\"")));
        assertNull(TypeAdapters.CHARACTER.read(createReader("null")));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.CHARACTER.read(createReader("\"ab\"")));
    }

    @Test
    @DisplayName("STRING adapter read and write")
    void stringAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.STRING.write(writer, "hello");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("\"hello\"", json);

        assertEquals("hello", TypeAdapters.STRING.read(createReader("\"hello\"")));
        assertEquals("true", TypeAdapters.STRING.read(createReader("true")));
        assertNull(TypeAdapters.STRING.read(createReader("null")));
    }

    @Test
    @DisplayName("BigDecimal adapter read and write")
    void bigDecimalAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.BIG_DECIMAL.write(writer, new BigDecimal("123.456"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("123.456", json);
        assertEquals(new BigDecimal("123.456"), TypeAdapters.BIG_DECIMAL.read(createReader("\"123.456\"")));
        assertNull(TypeAdapters.BIG_DECIMAL.read(createReader("null")));
    }

    @Test
    @DisplayName("BigInteger adapter read and write")
    void bigIntegerAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.BIG_INTEGER.write(writer, new BigInteger("123456789"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("123456789", json); // This is just a placeholder to show intent
        // Actually:
        String realJson = writeToString(writer -> {
            try {
                TypeAdapters.BIG_INTEGER.write(writer, new BigInteger("123"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("123", realJson);
        assertEquals(new BigInteger("123"), TypeAdapers_BIG_INTEGER_read_STUB(createReader("\"123\"")));
    }

    private BigInteger TypeAdapers_BIG_INTEGER_read_STUB(JsonReader reader) throws java.io.IOException {
        return TypeAdapters.BIG_INTEGER.read(reader);
    }

    @Test
    @DisplayName("URL adapter read and write")
    void urlAdapter_works() throws java.io.IOException {
        String json = writeToString(writer -> {
            try {
                TypeAdapters.URL.write(writer, new URL("http://google.com"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("\"http://google.com\"", json);
        JsonReader reader = createReader("\"http://google.com\"");
        reader.setStrictness(Strictness.LENIENT);
        assertEquals(new URL("http://google.com"), TypeAdapters.URL.read(reader));
        assertNull(TypeAdapters.URL.read(createReader("null")));
        assertNull(TypeAdapters.URL.read(createReader("\"null\"")));
    }

    @Test
    @DisplayName("UUID adapter read and write")
    void uuidAdapter_works() throws java.io.IOException {
        UUID uuid = UUID.randomUUID();
        String json = writeToString(writer -> {
            try {
                TypeAdapters.UUID.write(writer, uuid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("\"" + uuid + "\"", json);

        assertEquals(uuid, TypeAdapters.UUID.read(createReader("\"" + uuid.toString() + "\"")));
        assertNull(TypeAdapters.UUID.read(createReader("null")));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.UUID.read(createReader("\"not-a-uuid\"")));
    }
}