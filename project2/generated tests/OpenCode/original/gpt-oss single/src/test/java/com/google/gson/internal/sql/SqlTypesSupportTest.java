import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.TypeAdapterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Unit tests for {@link SqlTypesSupport}.
 *
 * <p>Tests both the real‑world runtime values (when java.sql is available)
 * and the alternative branch where java.sql classes cannot be loaded.
 */
class SqlTypesSupportTest {

    @Test
    @DisplayName("Static initialisation succeeds when java.sql is available")
    void whenSqlAvailable_thenAllConstantsInitialized() throws Exception {
        // When the class is loaded normally, java.sql is present.
        assertTrue(SqlTypesSupport.SUPPORTS_SQL_TYPES,
                "SUPPORTS_SQL_TYPES should be true when java.sql is on the classpath");

        assertNotNull(SqlTypesSupport.DATE_DATE_TYPE, "DATE_DATE_TYPE must not be null");
        assertNotNull(SqlTypesSupport.TIMESTAMP_DATE_TYPE,
                "TIMESTAMP_DATE_TYPE must not be null");
        assertNotNull(SqlTypesSupport.DATE_FACTORY, "DATE_FACTORY must not be null");
        assertNotNull(SqlTypesSupport.TIME_FACTORY, "TIME_FACTORY must not be null");
        assertNotNull(SqlTypesSupport.TIMESTAMP_FACTORY,
                "TIMESTAMP_FACTORY must not be null");

        List<TypeAdapterFactory> factories = SqlTypesSupport.SQL_TYPE_FACTORIES;
        assertNotNull(factories, "SQL_TYPE_FACTORIES must not be null");
        assertEquals(3, factories.size(), "SQL_TYPE_FACTORIES should contain three factories");
        // Ensure the list is unmodifiable.
        assertThrows(UnsupportedOperationException.class, () -> factories.add(null));
    }

    @Test
    @DisplayName("Static initialisation when java.sql is missing")
    void whenSqlMissing_thenConstantsAreNull() throws Exception {
        // Create a ClassLoader that pretends java.sql.Date does not exist.
        ClassLoader parent = SqlTypesSupportTest.class.getClassLoader();
        URLClassLoader fakeLoader = new URLClassLoader(new java.net.URL[] {}, parent) {
            @Override
            public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if ("java.sql.Date".equals(name)) {
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
        };

        Class<?> clazz = Class.forName("com.google.gson.internal.sql.SqlTypesSupport", true, fakeLoader);
        Field supportsField = clazz.getDeclaredField("SUPPORTS_SQL_TYPES");
        supportsField.setAccessible(true);
        boolean supports = supportsField.getBoolean(null);
        assertFalse(supports, "SUPPORTS_SQL_TYPES should be false when java.sql is missing");

        Field dateTypeField = clazz.getDeclaredField("DATE_DATE_TYPE");
        dateTypeField.setAccessible(true);
        assertNull(dateTypeField.get(null), "DATE_DATE_TYPE should be null");

        Field timestampTypeField = clazz.getDeclaredField("TIMESTAMP_DATE_TYPE");
        timestampTypeField.setAccessible(true);
        assertNull(timestampTypeField.get(null), "TIMESTAMP_DATE_TYPE should be null");

        Field dateFactoryField = clazz.getDeclaredField("DATE_FACTORY");
        dateFactoryField.setAccessible(true);
        assertNull(dateFactoryField.get(null), "DATE_FACTORY should be null");

        Field timeFactoryField = clazz.getDeclaredField("TIME_FACTORY");
        timeFactoryField.setAccessible(true);
        assertNull(timeFactoryField.get(null), "TIME_FACTORY should be null");

        Field timestampFactoryField = clazz.getDeclaredField("TIMESTAMP_FACTORY");
        timestampFactoryField.setAccessible(true);
        assertNull(timestampFactoryField.get(null), "TIMESTAMP_FACTORY should be null");

        Field listField = clazz.getDeclaredField("SQL_TYPE_FACTORIES");
        listField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<?> factories = (List<?>) listField.get(null);
        assertNotNull(factories, "SQL_TYPE_FACTORIES must not be null even when unsupported");
        assertTrue(factories.isEmpty(), "SQL_TYPE_FACTORIES should be empty when java.sql is missing");
    }
}
