package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.Strictness;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlTypesSupportTest {

    @Test
    @DisplayName("SUPPORTS_SQL_TYPES reflects presence of java.sql package")
    void testSupportsSqlTypesFlag() {
        // The flag is set by checking Class.forName("java.sql.Date")
        // In this environment, java.sql is present, so the flag should be true.
        assertTrue(SqlTypesSupport.SUPPORTS_SQL_TYPES);
    }

    @Test
    @DisplayName("Factories are non‑null when SQL types are supported")
    void testFactoriesWhenSupported() {
        if (SqlTypesSupport.SUPPORTS_SQL_TYPES) {
            assertNotNull(SqlTypesSupport.DATE_FACTORY);
            assertNotNull(SqlTypesSupport.TIME_FACTORY);
            assertNotNull(SqlTypesSupport.TIMESTAMP_FACTORY);
            assertEquals(3, SqlTypesSupport.SQL_TYPE_FACTORIES.size());
            assertSame(SqlTypesSupport.TIME_FACTORY,
                    (SqlTypesSupport.SQL_TYPE_FACTORIES.get(0)));
            assertSame(SqlTypesSupport.DATE_FACTORY,
                    SqlTypesSupport.SQL_TYPE_FACTORIES.get(1));
            assertSame(SqlTypesSupport.TIMESTAMP_FACTORY,
                    SqlTypesSupport.SQL_TYPE_FACTORIES.get(2));
        }
    }

    @Test
    @DisplayName("Factories are null and list empty when SQL types are unsupported")
    void testFactoriesWhenUnsupported() {
        // We cannot toggle SUPPORTS_SQL_TYPES at runtime.
        // This test asserts the opposite branch’s invariants if the flag were false.
        if (!SqlTypesSupport.SUPPORTS_SQL_TYPES) {
            assertNull(SqlTypesSupport.DATE_FACTORY);
            assertNull(SqlTypesSupport.TIME_FACTORY);
            assertNull(SqlTypesSupport.TIMESTAMP_FACTORY);
            assertTrue(SqlTypesSupport.SQL_TYPE_FACTORIES.isEmpty());
        }
    }

    @Test
    @DisplayName("DateType deserializers preserve SQL type")
    void testDateTypeDeserialization() throws IOException {
        // Build a mock DateType for java.sql.Date via the factory that uses the adapter
        var adapter = SqlDateTypeAdapter.FACTORY.create(new Gson(), TypeToken.get(java.sql.Date.class));
        JsonReader reader = new JsonReader(new StringReader("'Jan 1, 2020'"));
        reader.setStrictness(Strictness.LENIENT);
        var sqlDate = (java.sql.Date) adapter.read(reader);
        assertEquals(java.sql.Date.valueOf("2020-01-01"), sqlDate);
    }
}