package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.internal.FieldNamingPolicy;
import com.google.gson.internal.LongSerializationPolicy;
import com.google.gson.internal.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GsonBuilderTest {

  @DisplayName("Builder configuration methods")
  @Nested
  class Configuration {

    @Test
    void setVersionValid() {
      GsonBuilder b = new GsonBuilder();
      assertSame(b, b.setVersion(1.0));
      assertNotNull(b.excluder);
    }

    @Test
    void setVersionInvalid() {
      GsonBuilder b = new GsonBuilder();
      assertThrows(
          IllegalArgumentException.class, () -> b.setVersion(Double.NaN), "NaN should throw");
      assertThrows(
          IllegalArgumentException.class,
          () -> b.setVersion(-1.0),
          "Negative version should throw");
    }

    @Test
    void excludeFieldsWithModifiersNull() {
      GsonBuilder b = new GsonBuilder();
      assertThrows(NullPointerException.class, () -> b.excludeFieldsWithModifiers((int[]) null));
    }

    @Test
    void generateNonExecutableJsonFlag() {
      GsonBuilder b = new GsonBuilder();
      assertFalse(b.generateNonExecutableJson);
      assertSame(b, b.generateNonExecutableJson());
      assertTrue(b.generateNonExecutableJson);
    }

    @Test
    void serializeNullsFlag() {
      GsonBuilder b = new GsonBuilder();
      assertFalse(b.serializeNulls);
      assertSame(b, b.serializeNulls());
      assertTrue(b.serializeNulls);
    }

    @Test
    void complexMapKeySerializationFlag() {
      GsonBuilder b = new GsonBuilder();
      assertFalse(b.complexMapKeySerialization);
      assertSame(b, b.enableComplexMapKeySerialization());
      assertTrue(b.complexMapKeySerialization);
    }

    @Test
    void setDateFormatStringNull() {
      GsonBuilder b = new GsonBuilder();
      b.setDateFormat((String) null);
      assertNull(b.datePattern);
      // no exception
    }

    @Test
    void setDateFormatStringValid() {
      GsonBuilder b = new GsonBuilder();
      b.setDateFormat("yyyy-MM-dd");
      assertEquals("yyyy-MM-dd", b.datePattern);
    }

    @Test
    void setDateFormatStringInvalid() {
      GsonBuilder b = new GsonBuilder();
      assertThrows(
          IllegalArgumentException.class,
          () -> b.setDateFormat("[invalid]"),
          "invalid pattern should throw");
    }

    @Test
    void setDateFormatIntStyle() {
      GsonBuilder b = new GsonBuilder();
      b.setDateFormat(DateFormat.LONG);
      assertEquals(DateFormat.LONG, b.dateStyle);
      assertNull(b.datePattern);
    }

    @Test
    void setDateFormatIntStyleInvalid() {
      GsonBuilder b = new GsonBuilder();
      assertThrows(
          IllegalArgumentException.class, () -> b.setDateFormat(-123), "invalid style");
    }

    @Test
    void setDateFormatIntStyleTimeStyle() {
      GsonBuilder b = new GsonBuilder();
      b.setDateFormat(DateFormat.SHORT, DateFormat.FULL);
      assertEquals(DateFormat.SHORT, b.dateStyle);
      assertEquals(DateFormat.FULL, b.timeStyle);
      assertNull(b.datePattern);
    }

    @Test
    void setStrictnessNull() {
      GsonBuilder b = new GsonBuilder();
      assertSame(b, b.setStrictness(Strictness.LEGACY_STRICT));
      assertNotNull(b.strictness);
    }

    @Test
    void setFieldNamingPolicy() {
      GsonBuilder b = new GsonBuilder();
      assertSame(b, b.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE));
      assertEquals(FieldNamingPolicy.UPPER_CAMEL_CASE, b.fieldNamingPolicy);
    }

    @Test
    void setLongSerializationPolicy() {
      GsonBuilder b = new GsonBuilder();
      assertSame(
          b,
          b.setLongSerializationPolicy(LongSerializationPolicy.DEFAULT),
          "policy should be set");
      assertEquals(LongSerializationPolicy.DEFAULT, b.longSerializationPolicy);
    }

    @Test
    void setObjectToNumberStrategy() {
      GsonBuilder b = new GsonBuilder();
      assertSame(b, b.setObjectToNumberStrategy(ToNumberPolicy.DOUBLE));
      assertEquals(ToNumberPolicy.DOUBLE, b.objectToNumberStrategy);
    }

    @Test
    void setNumberToNumberStrategy() {
      GsonBuilder b = new GsonBuilder();
      assertSame(b, b.setNumberToNumberStrategy(ToNumberPolicy.DOUBLE));
      assertEquals(ToNumberPolicy.DOUBLE, b.numberToNumberStrategy);
    }
  }

  @DisplayName("Create Gson instance")
  @Nested
  class CreateGson {

    @Test
    void defaultBuilderCreatesGsonWithDefaults() {
      GsonBuilder b = new GsonBuilder();
      Gson gson = b.create();
      assertNotNull(gson);
      // basic behavior
      assertTrue(gson.htmlSafe());
      assertFalse(gson.serializeNulls());
      assertFalse(gson.generateNonExecutableJson());
    }

    @Test
    void customBuilderPropagatesSettings() {
      GsonBuilder b =
          new GsonBuilder()
              .excludeFieldsWithoutExposeAnnotation()
              .serializeNulls()
              .enableComplexMapKeySerialization();
      Gson gson = b.create();
      // these flags are reflected
      assertTrue(gson.serializeNulls());
      // complex map key is a builder flag that affects factories, not directly exposed
      // but we can check that Gson was created without throwing
      assertNotNull(gson);
    }
  }
}