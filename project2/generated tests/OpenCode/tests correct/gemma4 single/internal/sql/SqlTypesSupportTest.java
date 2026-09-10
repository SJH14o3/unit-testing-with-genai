package com.google.gson.internal.sql;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.DefaultDateTypeAdapter.DateType;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SqlTypesSupport Test")
class SqlTypesSupportTest {

    @Test
    @DisplayName("Verify SQL types support constants based on environment")
    void testSqlTypesSupportConstants() {
        if (SqlTypesSupport.SUPPORTS_SQL_TYPES) {
            assertAll("When java.sql types are supported",
                    () -> assertNotNull(SqlTypesSupport.DATE_DATE_TYPE),
                    () -> assertNotNull(SqlTypesSupport.TIMESTAMP_DATE_TYPE),
                    () -> assertNotNull(SqlTypesSupport.DATE_FACTORY),
                    () -> assertNotNull(SqlTypesSupport.TIME_FACTORY),
                    () -> assertNotNull(SqlTypesSupport.TIMESTAMP_FACTORY),
                    () -> {
                        List<TypeAdapterFactory> factories = SqlTypesSupport.SQL_TYPE_FACTORIES;
                        assertEquals(3, factories.size(), "Should have 3 factories");
                    }
            );
        } else {
            assertAll("When java.sql types are NOT supported",
                    () -> assertNull(SqlTypesSupport.DATE_DATE_TYPE),
                    () -> assertNull(SqlTypesSupport.TIMESTAMP_DATE_TYPE),
                    () -> assertNull(SqlTypesSupport.DATE_FACTORY),
                    () -> assertNull(SqlTypesSupport.TIME_FACTORY),
                    () -> assertNull(SqlTypesSupport.TIMESTAMP_FACTORY),
                    () -> assertTrue(SqlTypesSupport.SQL_TYPE_FACTORIES.isEmpty(), "Factories list should be empty")
            );
        }
    }
}