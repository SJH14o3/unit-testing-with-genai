package com.google.gson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldNamingPolicyTest {

    private static class TestClass {
        String someField;
        String anotherField;
        String aField;
        String simple;
        String ABC;
    }

    private Field getField(String name) throws NoSuchFieldException {
        return TestClass.class.getDeclaredField(name);
    }

    @ParameterizedTest
    @CsvSource({
            "someField, someField",
            "anotherField, anotherField",
            "aField, aField",
            "simple, simple",
            "ABC, ABC"
    })
    @DisplayName("IDENTITY policy returns the same name")
    void testIdentity(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.IDENTITY.translateName(getField(fieldName)));
    }

    @ParameterizedTest
    @CsvSource({
            "someField, SomeField",
            "anotherField, AnotherField",
            "aField, AField",
            "simple, Simple",
            "ABC, ABC"
    })
    @DisplayName("UPPER_CAMEL_CASE policy capitalizes first letter")
    void testUpperCamelCase(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.UPPER_CAMEL_CASE.translateName(getField(fieldName)));
    }

    @ParameterizedTest
    @CsvSource({
            "someField, Some Field",
            "anotherField, Another Field",
            "aField, A Field",
            "simple, Simple",
            "ABC, A B C"
    })
    @DisplayName("UPPER_CAMEL_CASE_WITH_SPACES policy capitalizes and adds spaces")
    void testUpperCamelCaseWithSpaces(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES.translateName(getField(fieldName)));
    }

    @ParameterizedTest
    @CsvSource({
            "someField, SOME_FIELD",
            "anotherField, ANOTHER_FIELD",
            "aField, A_FIELD",
            "simple, SIMPLE",
            "ABC, A_B_C"
    })
    @DisplayName("UPPER_CASE_WITH_UNDERSCORES policy converts to upper case with underscores")
    void testUpperCaseWithUnderscores(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(getField(fieldName)));
    }

    @ParameterizedTest
    @CsvSource({
            "someField, some_field",
            "anotherField, another_field",
            "aField, a_field",
            "simple, simple",
            "ABC, a_b_c"
    })
    @DisplayName("LOWER_CASE_WITH_UNDERSCORES policy converts to lower case with underscores")
    void testLowerCaseWithUnderscores(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(getField(fieldName)));
    }

    @ParameterizedTest
    @CsvSource({
            "someField, some-field",
            "anotherField, another-field",
            "aField, a-field",
            "simple, simple",
            "ABC, a-b-c"
    })
    @DisplayName("LOWER_CASE_WITH_DASHES policy converts to lower case with dashes")
    void testLowerCaseWithDashes(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(getField(fieldName)));
    }

    @ParameterizedTest
    @CsvSource({
            "someField, some.field",
            "anotherField, another.field",
            "aField, a.field",
            "simple, simple",
            "ABC, a.b.c"
    })
    @DisplayName("LOWER_CASE_WITH_DOTS policy converts to lower case with dots")
    void testLowerCaseWithDots(String fieldName, String expected) throws NoSuchFieldException {
        assertEquals(expected, FieldNamingPolicy.LOWER_CASE_WITH_DOTS.translateName(getField(fieldName)));
    }

    @Test
    @DisplayName("separateCamelCase decision coverage")
    void testSeparateCamelCase() {
        assertEquals("", FieldNamingPolicy.separateCamelCase("", '_'));
        assertEquals("a", FieldNamingPolicy.separateCamelCase("a", '_'));
        assertEquals("A", FieldNamingPolicy.separateCamelCase("A", '_'));
        assertEquals("A_B", FieldNamingPolicy.separateCamelCase("AB", '_'));
        assertEquals("a_B", FieldNamingPolicy.separateCamelCase("aB", '_'));
        assertEquals("abc", FieldNamingPolicy.separateCamelCase("abc", '_'));
    }

    @Test
    @DisplayName("upperCaseFirstLetter decision coverage")
    void testUpperCaseFirstLetter() {
        assertEquals("abc", FieldNamingPolicy.upperCaseFirstLetter("abc"));
        assertEquals("Abc", FieldNamingPolicy.upperCaseFirstLetter("abc"));
        assertEquals("Abc", FieldNamingPolicy.upperCaseFirstLetter("Abc"));
        assertEquals("123", FieldNamingPolicy.upperCaseFirstLetter("123"));
        assertEquals("1A", FieldNamingPolicy.upperCaseFirstLetter("1a"));
        assertEquals("A", FieldNamingPolicy.upperCaseFirstLetter("a"));
        assertEquals("A", FieldNamingPolicy.upperCaseFirstLetter("A"));
    }
}