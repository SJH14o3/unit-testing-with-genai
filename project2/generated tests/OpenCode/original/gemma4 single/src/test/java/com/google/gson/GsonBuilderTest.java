package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.internal.Excluder;
import com.google.gson.internal.typeadapters.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.stream.Stream;

@DisplayName("GsonBuilder Test")
class GsonBuilderTest {

  private GsonBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new GsonBuilder();
  }

  @Nested
  @DisplayName("Version Configuration")
  class VersionTests {
    @Test
    @DisplayName("setVersion with valid version returns this")
    void setVersion_valid() {
      assertEquals(builder, builder.setVersion(1.0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, -1.0, -0.5})
    @DisplayName("setVersion with invalid version throws exception")
    void setVersion_invalid(double version) {
      assertThrows(IllegalArgumentException.class, () -> builder.setVersion(version));
    }
  }

  @Nested
  @DisplayName("Modifier Configuration")
  class ModifierTests {
    @Test
    @DisplayName("excludeFieldsWithModifiers with valid modifiers returns this")
    void excludeFieldsWithModifiers_valid() {
      assertEquals(builder, builder.excludeFieldsWithModifiers(Modifier.TRANSIENT));
    }

    @Test
    @DisplayName("excludeFieldsWithModifiers with null throws exception")
    void excludeFieldsWithModifiers_null() {
      assertThrows(NullPointerException.class, () -> builder.excludeFieldsWithModifiers(null));
    }
  }

  @Nested
  @DisplayName("Json Output Configuration")
  class JsonOutputTests {
    @Test
    @DisplayName("generateNonExecutableJson returns this")
    void generateNonExecutableJson_returnsThis() {
      assertEquals(builder, builder.generateNonExecutableJson());
    }

    @Test
    @DisplayName("serializeNulls returns this")
    void serializeNulls_returnsThis() {
      assertEquals(Boolean.TRUE, (Boolean) builder.serializeNulls() != null ? true : false); // Using side effect check if possible, but here we check return value
      assertEquals(builder, builder.serializeNulls());
    }

    @Test
    @DisplayName("enableComplexMapKeySerialization returns this")
    void enableComplexMapKeySerialization_returnsThis() {
      assertEquals(builder, builder.enableComplexMapKeySerialization());
    }

    @Test
    @DisplayName("disableInnerClassSerialization returns this")
    void disableInnerClassSerialization_returnsThis() {
      assertEquals(builder, builder.disableInnerClassSerialization());
    }

    @Test
    @DisplayName("setPrettyPrinting returns this")
    void setPrettyPrinting_returnsThis() {
      assertEquals(builder, builder.setPrettyPrinting());
    }

    @Test
    @DisplayName("disableHtmlEscaping returns this")
    void disableHtmlEscaping_returnsThis() {
      assertEquals(builder, builder.disableHtmlEscaping());
    }
  }

  @Nested
  @DisplayName("Exclusion Strategy Configuration")
  class ExclusionStrategyTests {
    @Test
    @DisplayName("excludeFieldsWithoutExposeAnnotation returns this")
    void excludeFieldsWithoutExposeAnnotation_returnsThis() {
      assertEquals(builder, builder.excludeFieldsWithoutExposeAnnotation());
    }

    @Test
    @DisplayName("setExclusionStrategies with valid strategies returns this")
    void setExclusionStrategies_valid() {
      ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override public boolean shouldSkipField(com.google.gson.reflect.TypeToken<?> typeToken, com.google.gson.internal.ExclusionCriteria exclusionCriteria) { return false; }
        @Override public boolean shouldSkipClass(Class<?> clazz) { return false; }
      };
      assertEquals(builder, builder.setExclusionStrategies(strategy));
    }

    @Test
    @DisplayName("setExclusionStrategies with null throws exception")
    void setExclusionStrategies_null() {
      assertThrows(NullPointerException.class, () -> builder.setExclusionStrategies((ExclusionStrategy[]) null));
    }

    @Test
    @DisplayName("addSerializationExclusionStrategy returns this")
    void addSerializationExclusionStrategy_returnsThis() {
      ExclusionStrategy strategy = new ExclusionStrategy() {
          @Override public boolean shouldSkipField(com.google.gson.reflect.TypeToken<?> typeToken, com.google.gson.internal.ExclusionCriteria exclusionCriteria) { return false; }
          @Override public boolean shouldSkipClass(Class<?> clazz) { return false; }
      };
      assertEquals(builder, builder.addSerializationExclusionStrategy(strategy));
    }

    @Test
    @DisplayName("addDeserializationExclusionStrategy returns this")
    void addDeserializationExclusionStrategy_returnsThis() {
      ExclusionStrategy strategy = new ExclusionStrategy() {
          @Override public boolean shouldSkipField(com.google.gson.reflect.TypeToken<?> typeToken, com.google.gson.internal.ExclusionCriteria exclusionCriteria) { return false; }
          @Override public boolean shouldSkipClass(Class<?> clazz) { return false; }
      };
      assertEquals(builder, builder.addDeserializationExclusionStrategy(strategy));
    }
  }

  @Nested
  @DisplayName("Policy and Strategy Configuration")
  class PolicyStrategyTests {
    @Test
    @DisplayName("setLongSerializationPolicy returns this")
    void setLongSerializationPolicy_returnsThis() {
      assertEquals(builder, builder.setLongSerializationPolicy(LongSerializationPolicy.MAX_VALUE));
    }

    @Test
    @DisplayName("setLongSerializationPolicy with null throws exception")
    void setLongSerializationPolicy_null() {
      assertThrows(NullPointerException.class, () -> builder.setLongSerializationPolicy(null));
    }

    @Test
    @DisplayName("setFieldNamingPolicy returns this")
    void setFieldNamingPolicy_returnsThis() {
      assertEquals(builder, builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE));
    }

    @Test
    @DisplayName("setFieldNamingPolicy with null throws exception")
    void setFieldNamingPolicy_null() {
      assertThrows(NullPointerException.class, () -> builder.setFieldNamingPolicy(null));
    }

    @Test
    @DisplayName("setFieldNamingStrategy returns this")
    void setFieldNamingStrategy_returnsThis() {
      assertEquals(builder, builder.setFieldNamingStrategy(FieldNamingPolicy.IDENTITY));
    }

    @Test
    @DisplayName("setFieldNamingStrategy with null throws exception")
    /
    void setFieldNamingStrategy_null() {
      assertThrows(NullPointerException.class, () -> builder.setFieldNamingStrategy(null));
    }

    @Test
    @DisplayName("setObjectToNumberStrategy returns this")
    void setObjectToNumberStrategy_returnsThis() {
      assertEquals(builder, builder.setObjectToNumberStrategy(ToNumberPolicy.DOUBLE));
    }

    @Test
    @DisplayName("setObjectToNumberStrategy with null throws exception")
    void setObjectToNumberStrategy_null() {
      assertThrows(NullPointerException.class, () -> builder.setObjectToNumberStrategy(null));
    }

    @Test
    @DisplayName("setNumberToNumberStrategy returns this")
    void setNumberToNumberStrategy_returnsThis() {
      assertEquals(builder, builder.setNumberToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER));
    }

    @Test
    @DisplayName("setNumberToNumberStrategy with null throws exception")
    void setNumberToNumberStrategy_null() {
      assertThrows(NullPointerException.class, () -> builder.setNumberToNumberStrategy(null));
    }
  }

  @Nested
  @DisplayName("Date Configuration")
  class DateTests {
    @Test
    @DisplayName("setDateFormat with valid pattern returns this")
    void setDateFormat_valid() {
      assertEquals(builder, builder.setDateFormat("yyyy-MM-dd"));
    }

    @Test
    @DisplayName("setDateFormat with null pattern does nothing and returns this")
    void setDateFormat_null() {
      assertEquals(toString(builder), toString(builder.setDateFormat(null))); 
      // Note: Since we can't easily check private fields, we check return value
      assertEquals(builder, builder.setDateFormat(null));
    }

    @Test
    @DisplayName("setDateFormat with invalid pattern throws exception")
    void setDateFormat_invalid() {
      assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat("invalid-pattern"));
    }

    @Test
    @DisplayName("setDateFormat with style returns this")
    void setDateFormat_style_returnsThis() {
      assertEquals(builder, builder.setDateFormat(DateFormat.SHORT));
    }

    @Test
    @DisplayName("setDateFormat with two styles returns this")
    void setDateFormat_two_styles_returnsThis() {
      assertEquals(builder, builder.setDateFormat(DateFormat.SHORT, DateFormat.LONG));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 4, 10})
    @DisplayName("setDateFormat with invalid style throws exception")
    void setDateFormat_invalid_style(int style) {
      assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat(style));
    }
  }

  @Nested
  @DisplayName("Type Adapter Registration")
  class TypeAdapterTests {
    @Test
    @DisplayName("registerTypeAdapter with valid adapter returns this")
    void registerTypeAdapter_valid() {
      TypeAdapter<String> adapter = new TypeAdapter<String>() {
        @Override public void write(JsonWriter out, String value) {}
        @Override public String read(JsonReader in) { return null; }
      };
      assertEquals(builder, builder.registerTypeAdapter(String.class, adapter));
    }

    @Test
    @DisplayName("registerTypeAdapter with invalid adapter throws exception")
    void registerTypeAdapter_invalid() {
      assertThrows(IllegalArgumentException.class, () -> builder.registerTypeAdapter(String.class, new Object()));
    }

    @Test
    @DisplayName("registerTypeAdapter with null type throws exception")
    void registerTypeAdapter_nullType() {
      assertThrows(NullPointerException.class, () -> builder.registerTypeAdapter(null, new TypeAdapter<String>() {
        @Override public void write(JsonWriter out, String value) {}
        @Override public String read(JsonParsingException in) { return null; }
      }));
    }

    @Test
    @DisplayName("registerTypeAdapter with null adapter throws exception")
    void registerTypeAdapter_nullAdapter() {
      assertThrows(NullPointerException.class, () -> builder.registerTypeAdapter(String.class, null));
    }

    @Test
    @DisplayName("registerTypeAdapter with non-overridable type throws exception")
    void registerTypeAdapter_nonOverridable() {
      TypeAdapter<Object> adapter = new TypeAdapter<Object>() {
        @Override public void write(JsonWriter out, Object value) {}
        @Override public Object read(JsonReader in) { return null; }
      };
      assertThrows(IllegalArgumentException.class, () -> builder.registerTypeAdapter(Object.class, adapter));
    }

    @Test
    @DisplayName("registerTypeAdapterFactory returns this")
    void registerTypeAdapterFactory_returnsThis() {
      TypeAdapterFactory factory = new TypeAdapterFactory() {
        @Override public <T> TypeAdapter<T> create(Type type, TypeAdapterContext context) { return null; }
      };
      assertEquals(builder, builder.registerTypeAdapterFactory(factory));
    }

    @Test
    @DisplayName("registerTypeAdapterFactory with null throws exception")
    void registerTypeAdapterFactory_null() {
      assertThrows(NullPointerException.class, () -> builder.registerTypeAdapterFactory(null));
    }

    @Test
    @DisplayName("registerTypeHierarchyAdapter with valid adapter returns this")
    void registerTypeHierarchyAdapter_valid() {
      TypeAdapter<String> adapter = new TypeAdapter<String>() {
        @Override public void write(JsonWriter out, String value) {}
        @Override public String read(JsonReader in) { return null; }
      };
      assertEquals(builder, builder.registerTypeHierarchyAdapter(CharSequence.class, adapter));
    }

    @Test
    @DisplayName("registerTypeHierarchyAdapter with invalid adapter throws exception")
    void registerTypeHierarchyAdapter_invalid() {
      assertThrows(IllegalArgumentException.class, () -> builder.registerTypeHierarchyAdapter(CharSequence.class, new Object()));
    }
  }

  @Nested
  @DisplayName("Miscellaneous Configuration")
  class MiscTests {
    @Test
    @DisplayName("setStrictness returns this")
    void setStrictness_returnsThis() {
      assertEquals(builder, builder.setStrictness(Strictness.LENIENT));
    }

    @Test
    @DisplayName("setStrictness with null throws exception")
    void setStrictness_null() {
      assertThrows(NullPointerException.class, () -> builder.setStrictness(null));
    }

    @Test
    @DisplayName("setLenient returns this")
    void setLenient_returnsThis() {
      assertEquals(builder, builder.setLenient());
    }

    @Test
    @DisplayName("setFormattingStyle returns this")
    void setFormattingStyle_returnsThis() {
      assertEquals(builder, builder.setFormattingStyle(FormattingStyle.COMPACT));
    }

    @Test
    @DisplayName("setFormattingStyle with null throws exception")
    void setFormattingStyle_null() {
      assertThrows(NullPointerException.class, () -> builder.setFormattingStyle(null));
    }

    @Test
    @DisplayName("disableJdkUnsafe returns this")
    void disableJdkUnsafe_returnsThis() {
      assertEquals(builder, builder.disableJdkUnsafe());
    }

    @Test
    @DisplayName("serializeSpecialFloatingPointValues returns this")
    void serializeSpecialFloatingPointValues_returnsThis() {
      assertEquals(builder, builder.serializeSpecialFloatingPointValues());
    }

    @Test
    @DisplayName("addReflectionAccessFilter returns this")
    void addReflectionAccessFilter_returnsThis() {
      ReflectionAccessFilter filter = new ReflectionAccessFilter() {
        @Override public boolean isAllowed(Class<?> clazz, Class<?> parent, java.lang.reflect.Field field, java.lang.reflect.Method method) { return true; }
      };
      assertEquals(builder, builder.addReflectionAccessFilter(filter));
    }

    @Test
    @DisplayName("create returns a Gson instance")
    void create_returnsGson() {
      assertNotNull(builder.create());
    }
  }

  // Helper to avoid duplicate code for string conversion if needed
  private String toString(GsonBuilder builder) {
    return builder.toString();
  }
}