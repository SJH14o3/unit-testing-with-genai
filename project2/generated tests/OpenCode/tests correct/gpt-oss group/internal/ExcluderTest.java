package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.internal.reflect.ReflectionHelper;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcluderTest {

    private static class Dummy {
        @SuppressWarnings("unused")
        int normalField;

        @SuppressWarnings("unused")
        @Expose
        String exposedField;

        @SuppressWarnings("unused")
        @Expose(serialize = false)
        String nonSerialized;

        @SuppressWarnings("unused")
        static int staticField;

        @SuppressWarnings("unused")
        transient int transientField;
    }

    @Nested
    @DisplayName("Version handling")
    class VersionTest {
        @Test
        void ignoresVersionWhenNotSet() {
            assertFalse(Excluder.DEFAULT.excludeClass(String.class, true));
        }

        @Test
        void excludesAnnotatedWithSince() {
            Excluder excluder = Excluder.DEFAULT.withVersion(1.0);
            assertTrue(excluder.excludeClass(WithSince.class, true));
        }

        @Test
        void excludesAnnotatedWithUntil() {
            Excluder excluder = Excluder.DEFAULT.withVersion(3.0);
            assertTrue(excluder.excludeClass(WithUntil.class, true));
        }

        @SuppressWarnings("unused")
        @Since(2.0)
        private static class WithSince {}

        @SuppressWarnings("unused")
        @Until(2.0)
        private static class WithUntil {}
    }

    @Nested
    @DisplayName("Modifier handling")
    class ModifierTest {

        @Test
        void excludesTransientAndStatic() throws NoSuchFieldException {
            Excluder excluder = Excluder.DEFAULT;
            Field staticField =
                    Dummy.class.getDeclaredField("staticField");
            Field transientField =
                    Dummy.class.getDeclaredField("transientField");
            assertTrue(excluder.excludeField(staticField, true));
            assertTrue(excluder.excludeField(transientField, false));
        }
    }

    @Nested
    @DisplayName("Expose annotation handling")
    class ExposeTest {

        @Test
        void respectsRequireExpose() throws NoSuchFieldException {
            Excluder excluder = Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation();
            Field exposed = Dummy.class.getDeclaredField("exposedField");
            Field nonSerialized = Dummy.class.getDeclaredField("nonSerialized");
            assertFalse(excluder.excludeField(exposed, true));
            // excluded because serialize() == false
            assertTrue(excluder.excludeField(nonSerialized, true));
            // no annotation – must be excluded
            Field normal = Dummy.class.getDeclaredField("normalField");
            assertTrue(excluder.excludeField(normal, false));
        }
    }

    @Nested
    @DisplayName("Inner and anonymous classes")
    class InnerAnonymousTest {

        @Test
        void serializesInnerClassWhenAllowed() {
            Excluder excluder = Excluder.DEFAULT;
            assertFalse(excluder.excludeClass(InnerHolder.Inner.class, true));
        }

        @Test
        void excludesInnerClassWhenDisabled() {
            Excluder excluder = Excluder.DEFAULT.disableInnerClassSerialization();
            assertTrue(excluder.excludeClass(InnerHolder.Inner.class, true));
        }

        @Test
        void excludesAnonymousForDeserializationOnly() {
            Excluder excluder = Excluder.DEFAULT;
            Class<?> anon = new Object() {}.getClass();
            assertFalse(excluder.excludeClass(anon, true));
            assertTrue(excluder.excludeClass(anon, false));
        }

        @SuppressWarnings("unused")
        private static class InnerHolder {
            @SuppressWarnings("unused")
            class Inner {}
        }
    }

    @Nested
    @DisplayName("Custom exclusion strategies")
    class StrategyTest {

        private static final ExclusionStrategy STRATEGY =
                new ExclusionStrategy() {
                    @Override public boolean shouldSkipField(FieldAttributes f) { return true; }
                    @Override public boolean shouldSkipClass(Class<?> clazz) { return false; }
                };

        private static final ExclusionStrategy STRATEGY_CLASS =
                new ExclusionStrategy() {
                    @Override public boolean shouldSkipField(FieldAttributes f) { return false; }
                    @Override public boolean shouldSkipClass(Class<?> clazz) { return true; }
                };

        @Test
        void fieldExcludedByStrategy() throws NoSuchFieldException {
            Excluder excluder =
                    Excluder.DEFAULT.withExclusionStrategy(STRATEGY, true, false);
            Field field = Dummy.class.getDeclaredField("normalField");
            assertTrue(excluder.excludeField(field, true));
        }

        @Test
        void classExcludedByStrategy() {
            Excluder excluder =
                    Excluder.DEFAULT.withExclusionStrategy(STRATEGY_CLASS, true, false);
            assertTrue(excluder.excludeClass(String.class, true));
        }
    }
}