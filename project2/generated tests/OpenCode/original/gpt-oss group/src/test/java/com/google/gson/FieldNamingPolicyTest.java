package com.google.gson;

import java.lang.reflect.Field;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldNamingPolicyTest {

  @SuppressWarnings("unused")
  private static class TestFields {
    int someFieldName = 0;
    String _someFieldName = "";
    String aURL = "";
    int _123 = 0;
  }

  private Field field(String name) throws NoSuchFieldException {
    return TestFields.class.getDeclaredField(name);
  }

  @Nested
  @DisplayName("Identity")
  class Identity {
    @Test
    void unchanged() throws NoSuchFieldException {
      assertEquals(
          "someFieldName",
          FieldNamingPolicy.IDENTITY.translateName(field("someFieldName")));
    }
  }

  @Nested
  @DisplayName("UPPER_CAMEL_CASE")
  class UpperCamelCase {
    @Test
    void leadsWithUpperCase() throws NoSuchFieldException {
      assertEquals(
          "SomeFieldName",
          FieldNamingPolicy.UPPER_CAMEL_CASE.translateName(field("someFieldName")));
    }

    @Test
    void leadingUnderscorePreserved() throws NoSuchFieldException {
      assertEquals(
          "_SomeFieldName",
          FieldNamingPolicy.UPPER_CAMEL_CASE.translateName(field("_someFieldName")));
    }
  }

  @Nested
  @DisplayName("UPPER_CAMEL_CASE_WITH_SPACES")
  class UpperCamelCaseWithSpaces {
    @Test
    void addsSpacesBetweenWords() throws NoSuchFieldException {
      assertEquals(
          "Some Field Name",
          FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES.translateName(field("someFieldName")));
    }

    @Test
    void leadingUnderscoreWithSpaces() throws NoSuchFieldException {
      assertEquals(
          "_Some Field Name",
          FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES.translateName(field("_someFieldName")));
    }
  }

  @Nested
  @DisplayName("UPPER_CASE_WITH_UNDERSCORES")
  class UpperCaseUnderscores {
    @Test
    void correctUpperCaseUnderscores() throws NoSuchFieldException {
      assertEquals(
          "SOME_FIELD_NAME",
          FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(field("someFieldName")));
    }

    @Test
    void handlesURLs() throws NoSuchFieldException {
      assertEquals(
          "A_U_R_L",
          FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(field("aURL")));
    }

    @Test
    void preservesLeadingUnderscore() throws NoSuchFieldException {
      assertEquals(
          "_SOME_FIELD_NAME",
          FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES.translateName(field("_someFieldName")));
    }
  }

  @Nested
  @DisplayName("LOWER_CASE_WITH_UNDERSCORES")
  class LowerCaseUnderscores {
    @Test
    void correctLowerCaseUnderscores() throws NoSuchFieldException {
      assertEquals(
          "some_field_name",
          FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field("someFieldName")));
    }
  }

  @Nested
  @DisplayName("LOWER_CASE_WITH_DASHES")
  class LowerCaseDashes {
    @Test
    void correctLowerCaseDashes() throws NoSuchFieldException {
      assertEquals(
          "some-field-name",
          FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(field("someFieldName")));
    }
  }

  @Nested
  @DisplayName("LOWER_CASE_WITH_DOTS")
  class LowerCaseDots {
    @Test
    void correctLowerCaseDots() throws NoSuchFieldException {
      assertEquals(
          "some.field.name",
          FieldNamingPolicy.LOWER_CASE_WITH_DOTS.translateName(field("someFieldName")));
    }
  }

  @Nested
  @DisplayName("Static helper methods")
  class StaticHelpers {
    @Test
    void separateCamelCaseSeparatesCorrectly() {
      assertEquals(
          "Some_Field_Name",
          FieldNamingPolicy.separateCamelCase("someFieldName", '_'));
      assertEquals(
          "a_U_R_L",
          FieldNamingPolicy.separateCamelCase("aURL", '_'));
    }

    @Test
    void upperCaseFirstLetterProperlyOpens() {
      assertEquals("Some", FieldNamingPolicy.upperCaseFirstLetter("some"));
      assertEquals(" _some", FieldNamingPolicy.upperCaseFirstLetter(" _some"));
      // no letter
      assertEquals("123abc", FieldNamingPolicy.upperCaseFirstLetter("123abc"));
    }
  }
}