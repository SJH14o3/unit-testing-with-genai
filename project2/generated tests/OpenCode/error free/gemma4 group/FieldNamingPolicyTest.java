package com.google.gson;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;

import static com.google.gson.FieldNamingPolicy.upperCaseFirstLetter;
import static org.junit.jupiter.api.Assertions.*;

class FieldNamingPolicyTest {

    private static class TestClass {
        public String someFieldName;
        public String _someFieldName;
        public String aStringField;
        public String aURL;
    }

    @Test
    @DisplayName("Test IDENTITY policy")
    void testIdentity() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("someFieldName", FieldNamingPolicy.IDENTITY.translateName(field));
    }

    @Test
    @DisplayName("Test UPPER_CAMEL_CASE policy")
    void testUpperCamelCase() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("SomeFieldName", FieldNamingPolicy.UPPER_CAMEL_CASE.translateName(field));
    }

    @Test
    @DisplayName("Test UPPER_CAMEL_CASE_WITH_SPACES policy")
    void testUpperCamelCaseWithSpaces() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("Some Field Name", FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES.translateName(field));
    }

    @Test
    @DisplayName("Test UPPER_CASE_WITH_UNDERSCORES policy")
    void testUpperCaseWithUnderscores() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("SOME_FIELD_NAME", FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(field));

        Field field2 = TestClass.class.getField("aURL");
        assertEquals("A_U_R_L", FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(field2));
    }

    @Test
    @DisplayName("Test LOWER_CASE_WITH_UNDERSCORES policy")
    void testLowerCaseWithUnderscores() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("some_field_name", FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field));
    }

    @Test
    @DisplayName("Test LOWER_CASE_WITH_DASHES policy")
    void testLowerCaseWithDashes() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("some-field-name", FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(field));
    }

    @Test
    @DisplayName("Test LOWER_CASE_WITH_DOTS policy")
    void testLowerCaseWithDots() throws NoSuchFieldException {
        Field field = TestClass.class.getField("someFieldName");
        assertEquals("some.field.name", FieldNamingPolicy.LOWER_CASE_WITH_DOTS.translateName(field));
    }

    @Test
    @DisplayName("Test separateCamelCase utility")
    void testSeparateCamelCase() {
        assertEquals("some-case", FieldNamingPolicy.separateCamelCase("someCase", '-'));
        assertEquals("some_case", FieldNamingPolicy.separateCamelCase("someCase", '_'));
        assertEquals("some case", FieldNamingPolicy.separateCamelCase("someCase", ' '));
    }

    @Test
    @DisplayName("Test upperCaseFirstLetter utility")
    void testUpperCaseFirstLetter() {
        assertEquals("Some", upperCaseFirstLetter("some"));
        assertEquals("S", upperCaseFirstLetter("s"));
        assertEquals("SomeField", upperCaseFirstLetter("someField"));
        assertEquals("123", upperCaseFirstLetter("123"));
    }
}
