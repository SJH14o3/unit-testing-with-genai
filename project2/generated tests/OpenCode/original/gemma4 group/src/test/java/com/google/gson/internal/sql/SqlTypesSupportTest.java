package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlTypesSupportTest {

  @Test
  @DisplayName("Check if SUPPORTS_SQL_TYPES and factories are initialized correctly")
  void checkInitialization() {
    if (SqlTypesSupport.SUPPORTS_SQL_TYPES) {
      assertNotNull(SqlTypesSupport.DATE_DATE_TYPE);
      assertNotNull(SqlTypesSupport.TIMESTAMP_DATE_TYPE);
      assertNotNull(SqlTypesSupport.DATE_FACTORY);
      assertNotNull(SqlTypesSupport.TIME_FACTORY);
      assertNotNull(SqlTypesSupport.TIMESTAMP_FACTORY);
      assertNotNull(SqlTypesSupport.SQL_TYPE_FACTORIES);
    } else {
      assertNull(SqlTypesSupport.DATE_DATE_TYPE);
      assertNull(SqlTypesSupport.TIMESTAMP_DATE_TYPE);
      assertNull(SqlTypesSupport.DATE_FACTORY);
      assertNull(SqlTypesSupport.TIME_FACTORY);
      assertNull(SqlTypesSupport.TIMESTAMP_FACTORY);
      assertNotNull(SqlTypesSupport.SQL_TYPE_features); // SQL_TYPE_FACTORIES is empty list
    }
  }
}
