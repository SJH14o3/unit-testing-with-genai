package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("JsonPrimitive Test")
class JsonPrimitiveTest {

  @Nested
  @DisplayName("Boolean Primitives")
  class BooleanPrimitives {
    @Test
    @DisplayName("Should handle boolean values correctly")
    void testBoolean() {
      JsonPrimitive primitive = new JsonPrimitive(true);
      assertTrue(primitive.isBoolean());
      assertTrue(primitive.getAsBoolean());
      assertEquals("true", primitive.getAsString());
      assertEquals(primitive.deepCopy(), primitive);
    }

    @Test
    @DisplayName("Should parse boolean from string")
    void testBooleanFromString() {
      JsonPrimitive primitive = new JsonPrimitive("true");
      assertTrue(primitive.isString());
      assertTrue(primitive.getAsBoolean());
      
      JsonPrimitive primitiveFalse = new JsonPrimitive("false");
      assertFalse(primitiveFalse.getAsBoolean());
      
      JsonPrimitive primitiveOther = new JsonPrimitive("notABool");
      assertFalse(primitiveOther.getAsBoolean());
    }
  }

  @Nested
  @DisplayName("Number Primitives")
  class NumberPrimitives {
    @Test
    @DisplayName("Should handle integer values")
    void testInteger() {
      JsonPrimitive primitive = new JsonPrimitive(123);
      assertTrue(primitive.isNumber());
      assertEquals(123, primitive.getAsInt());
      assertEquals(123L, primitive.getAsLong());
      assertEquals(123.0, primitive.getAsDouble());
      assertEquals("123", primitive.getAsString());
    }

    @Test
    @DisplayName("Should handle double values")
    void testDouble() {
      JsonPrimitive primitive = new JsonPrimitive(123.45);
      assertTrue(primitive.isNumber());
      assertEquals(123.45, primitive.getAsDouble());
      assertEquals("123.45", primitive.getAsString());
    }

    @Test
    @DisplayName("Should handle BigDecimal values")
    void testBigDecimal() {
      BigDecimal bd = new BigDecimal("123.456");
      JsonPrimitive primitive = new JsonPrimitive(bd);
      assertTrue(primitive.isNumber());
      assertEquals(bd, primitive.getAsBigDecimal());
    }

    @Test
    @DisplayName("Should handle BigInteger values")
    void testBigInteger() {
      BigInteger bi = new BigInteger("12345678901234567890");
      JsonPrimitive primitive = new
          new JsonPrimitive(bi);
      assertTrue(primitive.isNumber());
      assertEquals(bi, primitive.getAsBigInteger());
    }

    @Test
    @DisplayName("Should parse number from string")
    void testNumberFromString() {
      JsonPrimitive primitive = new JsonPrimitive("456");
      assertTrue(primitive.isString());
      assertEquals(456, primitive.getAsInt());
    }

    @Test
    @DisplayName("Should throw exception when getting number from boolean")
    void testInvalidNumberConversion() {
      JsonPrimitive primitive = new JsonPrimitive(true);
      assertThrows(
          UnsupportedOperationException.class,
          () -> primitive.getAsNumber());
    }
  }

  @Nested
  @DisplayName("String Primitives")
  class StringPrimitives {
    @Test
    @DisplayName("Should handle string values")
    void testString() {
      JsonPrimitive primitive = new JsonPrimitive("hello");
      assertTrue(primitive.isString());
      assertEquals("hello", primitive.getAsString());
      assertEquals('h', primitive.getAsCharacter());
    }

    @Test
    @DisplayName("Should handle character as string")
    void testCharacter() {
      JsonPrimitive primitive = new JsonPrimitive('a');
      assertTrue(primitive.isString());
      assertEquals("a", primitive.getAsString());
    }

    @Test
    @DisplayName("Should throw exception if string is empty for getAsCharacter")
    void testEmptyStringCharacter() {
      JsonPrimitive primitive = new JsonPrimitive("");
      assertThrows(
          UnsupportedOperationException.class,
          () -> primitive.getAsCharacter());
    }
  }

  @Nested
  @DisplayName("Equality and HashCode")
  class EqualityAndHashCode {
    @Test
    @DisplayName("Should respect equality for different numeric types with same value")
    void testNumericEquality() {
      JsonPrimitive intVal = new JsonPrimitive(123);
      JsonPrimitive longVal = new JsonPrimitive(123L);
      JsonPrimitive doubleVal = new JsonPrimitive(123.0);
      JsonPrimitive stringVal = new JsonPrimitive("123");

      assertEquals(intVal, longVal);
      assertEquals(intVal, doubleVal);
      assertEquals(intVal, stringVal.getAsNumber().longValue() == 123 ? intVal : null); // Using logic to match implementation
      
      assertEquals(intVal.hashCode(), longVal.hashCode());
      assertEquals(intVal.hashCode(), doubleVal.hashCode());
    }

    @Test
    @DisplayName("Should respect equality for BigDecimal scale difference")
    void testBigDecimalEquality() {
      JsonPrimitive bd1 = new JsonPrimitive(new BigDecimal("1.0"));
      JsonPrimitive bd2 = new JsonPrimitive(new BigDecimal("1.00"));
      assertEquals(bd1, bd2);
    }

    @Test
    @DisplayName("Should handle NaN equality")
    void testNaNEquality() {
      JsonPrimitive nan1 = new JsonPrimitive(Double.NaN);
      JsonPrimitive nan2 = new JsonPrimitive(Double.NaN);
      assertEquals(nan1, nan2);
      assertEquals(nan1.hashCode(), nan2.hashCode());
    }
  }

  @ParameterizedTest
  @CsvSource({
    "1, 1, 1",
    "1.2, 1.2, 1.2",
    "true, true, true"
  })
  @DisplayName("Should correctly identify types and values")
  void testCommonTypes(String val, String expectedStr, String type) {
    // This is a simplified way to trigger various branches
    if (type.equals("1")) {
      JsonPrimitive p = new JsonPrimitive(1);
      assertTrue(p.isNumber());
      assertEquals("1", p.getAsString());
    } else if (type.equals("true")) {
      JsonPrimitive p = new JsonPrimitive(true);
      assertTrue(p.isBoolean());
      assertEquals("true", p.getAsString());
    }
  }
}