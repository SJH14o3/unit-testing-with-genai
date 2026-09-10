package com.google.gson;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link com.google.gson.FieldNamingPolicy}.
 */
class FieldNamingPolicyTest {

    // Utility field for reflection in tests.
    private Field dummyField(String name) throws Exception {
        class Dummy { }
        java.lang.reflect.Field f = Dummy.class.getDeclaredField("value");
        f.setAccessible(true);
        // Use reflection to set name? Can't. We'll just use static method directly.
        return null;
    }

    @Test
    void identityTranslatesName() {
        Field f = null; // placeholder, not used
        // Using reflection to get a dummy field
        try {
            java.lang.reflect.Field dummy = Dummy.class.getDeclaredField("value");
            assertEquals("value", dummy.getName());
            assertEquals("value", FieldNamingPolicy.IDENTITY.translateName(dummy));
        } catch (Exception e) {
            fail("field creation failed", e);
        }
    }

    private static class Dummy {
        private String value = "example";
        private String someFieldName = "someFieldName";
        private String aURL = "aURL";
    }

    @Test
    void upperCamelCaseAddsUpperCase() throws Exception {
        Field f = Dummy.class.getDeclaredField("value");
        assertEquals("Value", FieldNamingPolicy.UPPER_CAMEL_CASE.translateName(f));
    }

    @Test
    void upperCamelCaseWithSpacesSeparates() throws Exception {
        Field f = Dummy.class.getDeclaredField("someFieldName");
        assertEquals("Some Field Name", FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES.translateName(f));
    }

    @Test
    void upperCaseWithUnderscoresTranslates() throws Exception {
        Field f = Dummy.class.getDeclaredField("someFieldName");
        assertEquals("SOME_FIELD_NAME", FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(f));
        f = Dummy.class.getDeclaredField("aURL");
        assertEquals("A_U_R_L", FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(f));
    }

    @Test
    void lowerCaseWithUnderscoresTranslates() throws Exception {
        Field f = Dummy.class.getDeclaredField("someFieldName");
        assertEquals("some_field_name", FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(f));
        f = Dummy.class.getDeclaredField("aURL");
        assertEquals("a_u_r_l", FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(f));
    }

    @Test
    void lowerCaseWithDashes() throws Exception {
        Field f = Dummy.class.getDeclaredField("someFieldName");
        assertEquals("some-field-name", FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(f));
        f = Dummy.class.getDeclaredField("aURL");
        assertEquals("a-u-r-l", FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(f));
    }

    @Test
    void lowerCaseWithDots() throws Exception {
        Field f = Dummy.class.getDeclaredField("someFieldName");
        assertEquals("some.field.name", FieldNamingPolicy.LOWER_CASE_WITH_DOTS.translateName(f));
        f = Dummy.class.getDeclaredField("aURL");
        assertEquals("a.u.r.l", FieldNamingPolicy.LOWER_CASE_WITH_DOTS.translateName(f));
    }

    @Test
    void separateCamelCaseSeparatesCorrectly() {
        assertEquals("some_Field_Name", FieldNamingPolicy.separateCamelCase("someFieldName", '_'));
        assertEquals("U_R_L", FieldNamingPolicy.separateCamelCase("URL", '_'));
    }

    @Test
    void upperCaseFirstLetterUpperCasePreserved() {
        assertEquals("Test", FieldNamingPolicy.upperCaseFirstLetter("Test"));
        assertEquals("Test", FieldNamingPolicy.upperCaseFirstLetter("test"));
        assertEquals("_Test", FieldNamingPolicy.upperCaseFirstLetter("_Test"));
        assertEquals("_Test", FieldNamingPolicy.upperCaseFirstLetter("_test"));
        assertEquals("123Abc", FieldNamingPolicy.upperCaseFirstLetter("123abc")); // no letter => unchanged
    }
}
