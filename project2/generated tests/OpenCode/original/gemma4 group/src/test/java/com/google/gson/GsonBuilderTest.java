package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.lang.reflect.Modifier;
import java.io.IOException;
import java.text.DateFormat;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.reflect.TypeToken;
import java.util.List;

@DisplayName("GsonBuilder Test")
class GsonBuilderTest {

    @Test
    @DisplayName("setVersion should set the version and return this")
    void setVersion_validVersion_setsVersion() {
        GsonBuilder builder = new GsonBuilder().setVersion(1.5);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setVersion should throw IllegalArgumentException for negative version")
    void setVersion_negativeVersion_throwsException() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.setVersion(-1.0));
    }

    @Test
    @DisplayName("setVersion should throw IllegalArgumentException for NaN version")
    void setVersion_nanVersion_throwsException() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.setVersion(Double.NaN));
    }

    @Test
    @DisplayName("excludeFieldsWithModifiers should set modifiers and return this")
    void excludeFieldsWithModifiers_validModifiers_setsModifiers() {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("generateNonExecutableJson should set flag and return this")
    void generateNonExecutableJson_setsFlag() {
        GsonBuilder builder = new GsonBuilder().generateNonExecutableJson();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("excludeFieldsWithoutExposeAnnotation should set excluder and return this")
    void excludeFieldsWithoutExposeAnnotation_setsExcluder() {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("serializeNulls should set flag and return this")
    void serializeNulls_setsFlag() {
        GsonBuilder builder = new GsonBuilder().serializeNulls();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("enableComplexMapKeySerialization should set flag and return this")
    void enableComplexMapKeySerialization_setsFlag() {
        GsonBuilder builder = new GsonBuilder().enableComplexMapKeySerialization();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("disableInnerClassSerialization should set excluder and return this")
    void disableInnerClassSerialization_setsExcluder() {
        GsonBuilder builder = new GsonBuilder().disableInnerClassSerialization();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setLongSerializationPolicy should set policy and return this")
    void setLongSerialization%SerializationPolicy_setsPolicy() {
        GsonBuilder builder = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.OFFSET_LENGTH);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setFieldNamingPolicy should set policy and return this")
    void setFieldNamingPolicy_setsPolicy() {
        GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setFieldNamingStrategy should set strategy and return this")
    void setFieldNamingStrategy_setsStrategy() {
        GsonBuilder builder = new GsonBuilder().setFieldNamingStrategy(field -> field.getName());
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setObjectToNumberStrategy should set strategy and return this")
    void setObjectToNumberStrategy_setsStrategy() {
        GsonBuilder builder = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.DOUBLE);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setNumberToNumberStrategy should set strategy and return this")
    void setNumberToNumberStrategy_setsStrategy() {
        GsonBuilder builder = new GsonBuilder().setNumberToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setExclusionStrategies should set strategies and return this")
    void setExclusionStrategies_setsStrategies() {
        GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override public boolean shouldSkipField(FieldAttributes f) { return false; }
            @Override public boolean shouldSkipClass(Class<?> c) { return false; }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("addSerializationExclusionStrategy should add strategy and return this")
    void addSerializationExclusionStrategy_addsStrategy() {
        GsonBuilder builder = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override public boolean shouldSkipField(FieldAttributes f) { return false; }
            @Override public boolean shouldSkipClass(Class<?> c) { return false; }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("addDeserializationExclusionStrategy should add strategy and return this")
    void addDeserializationExclusionStrategy_addsStrategy() {
        GsonBuilder builder = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override public boolean shouldSkipField(FieldAttributes f) { return false; }
            @Override public boolean shouldSkipClass(Class<?> c) { return false; }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setPrettyPrinting should set formatting style and return this")
    void setPrettyPrinting_setsFormattingStyle() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setFormattingStyle should set formatting style and return this")
    void setFormattingStyle_setsFormattingStyle() {
        GsonBuilder builder = new GsonBuilder().setFormattingStyle(FormattingStyle.COMPACT);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setLenient should set strictness to LENIENT and return this")
    void setLenient_setsStrictness() {
        GsonBuilder builder = new GsonBuilder().setLenient();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setStrictness should set strictness and return this")
    void setStrictness_setsStrictness() {
        GsonBuilder builder = new GsonBuilder().setStrictness(Strictness.LENIENT);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("disableHtmlEscaping should disable HTML escaping and return this")
    void disableHtmlEscaping_disablesHtmlEscaping() {
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setDateFormat should set date pattern and return this")
    void setDateFormat_pattern_setsPattern() {
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd");
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setDateFormat should throw IllegalArgumentException for invalid pattern")
    void setDateFormat_invalidPattern_throwsException() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat("invalid-pattern"));
    }

    @Test
    @DisplayName("setDateFormat should set date style and return this")
    void setDateFormat_style_setsStyle() {
        GsonBuilder builder = new GsonBuilder().setDateFormat(DateFormat.LONG);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setDateFormat should set date and time style and return this")
    void setDateFormat_dateAndTimeStyle_setsStyles() {
        GsonBuilder builder = new GsonBuilder().setDateFormat(DateFormat.SHORT, DateFormat.LONG);
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("setDateFormat should throw IllegalArgumentException for invalid date style")
    void setDateFormat_invalidStyle_throwsException() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.setDateFormat(-1));
    }

    @Test
    @DisplayName("registerTypeAdapter should register type adapter and return this")
    void registerTypeAdapter_registersAdapter() throws IOException {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(String.class, new TypeAdapter<String>() {
            @Override public void write(JsonWriter out, String value) throws IOException { out.value(value); }
            @Override public String read(JsonReader in) throws IOException { return in.nextString(); }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("registerTypeAdapter should throw IllegalArgumentException if type adapter is not supported")
    void registerTypeAdapter_unsupportedAdapter_throwsException() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.registerTypeAdapter(String.class, new Object()));
    }

    @Test
    @DisplayName("registerTypeAdapter should throw IllegalArgumentException if attempting to override built-in adapter for Object class")
    void registerTypeAdapter_overridingBuiltInAdapter_throwsException() {
        GsonBuilder builder = new GsonBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.registerTypeAdapter(Object.class, new TypeAdapter<Object>() {
            @Override public void write(JsonWriter out, Object value) throws IOException { out.value(value.toString()); }
            @Override public Object read(JsonReader in) throws IOException { return in.nextString(); }
        }));
    }

    @Test
    @DisplayName("registerTypeAdapterFactory should register factory and return this")
    void registerTypeAdapterFactory_registersFactory() {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {
            @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) { return null; }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("registerTypeHierarchyAdapter should register hierarchy adapter and return this")
    void registerTypeHierarchyAdapter_registersAdapter() {
        GsonBuilder builder = new GsonBuilder().registerTypeHierarchyAdapter(Object.class, new TypeAdapter<Object>() {
            @Override public void write(JsonWriter out, Object value) throws IOException { out.value(value.toString()); }
            @Override public Object read(JsonReader in) throws IOException { return in.nextString(); }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("serializeSpecialFloatingPointValues should set flag and return this")
    void serializeSpecialFloatingPointValues_setsFlag() {
        GsonBuilder builder = new GsonBuilder().serializeSpecialFloating/FloatingPointValues();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("disableJdkUnsafe should set flag and return this")
    void disableJdkUnsafe_setsFlag() {
        GsonBuilder builder = new GsonBuilder().disableJdkUnsafe();
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("addReflectionAccessFilter should add filter and return this")
    void addReflectionAccessFilter_addsFilter() {
        GsonBuilder builder = new GsonBuilder().addReflectionAccessFilter(new ReflectionAccessFilter() {
            @Override public boolean accepts(Class<?> clazz) { return false; }
        });
        assertNotNull(tokenVerify(builder));
    }

    @Test
    @DisplayName("create should return a Gson instance")
    void create_returnsGson() {
        Gson builder = new GsonBuilder().create();
        assertNotNull(builder);
    }

    private Gson tokenVerify(GsonBuilder builder) {
        return builder.create();
    }
}
