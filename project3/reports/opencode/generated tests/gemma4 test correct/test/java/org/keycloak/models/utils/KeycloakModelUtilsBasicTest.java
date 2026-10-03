package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.Key;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KeycloakModelUtilsBasicTest {

    @Test
    void testGenerateId() {
        String id = KeycloakModelUtils.generateId();
        assertNotNull(id);
        assertEquals(36, id.length());
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    void testGenerateShortId() {
        String shortId = KeycloakModelUtils.generateShortId();
        assertNotNull(shortId);
        assertEquals(22, shortId.length());
    }

    @Test
    void testGenerateShortIdWithUuid() {
        UUID uuid = UUID.randomUUID();
        String shortId = KeycloakModelUtils.generateShortId(uuid);
        assertNotNull(shortId);
        assertEquals(22, shortId.length());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "550e8400-e29b-41d4-a716-446655440000",
        "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    })
    void testIsValidUUID_Valid(String uuid) {
        assertTrue(KeycloakModelUtils.isValidUUID(uuid));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "not-a-uuid",
        "550e8400-e29b-41d4-a716",
        "550e8400-e29b-41d4-a716-44665544000g"
    })
    void testIsValidUUID_Invalid(String uuid) {
        assertFalse(KeycloakModelUtils.isValidUUID(uuid));
    }

    @ParameterizedTest
    @NullSource
    void testIsValidUUID_Null(String uuid) {
        assertFalse(KeycloakModelUtils.isValidUUID(uuid));
    }

    @Test
    void testToLowerCaseSafe() {
        assertNull(KeycloakModelUtils.toLowerCaseSafe(null));
        assertEquals("abc", KeycloakModelUtils.toLowerCaseSafe("ABC"));
        assertEquals("abc", KeycloakModelUtils.toLowerCaseSafe("aBc"));
    }

    @Test
    void testParseRole() {
        // Client role: app.role
        String[] result1 = KeycloakModelUtils.parseRole("app.role");
        assertArrayEquals(new String[]{"app", "role"}, result1);

        // Realm role: role
        String[] result2 = KeycloakModelUtils.parseRole("role");
        assertArrayEquals(new String[]{null, "role"}, result2);
    }

    @Test
    void testBuildRoleQualifier() {
        assertEquals("app.role", KeycloakModelUtils.buildRoleQualifier("app", "role"));
        assertEquals("role", KeycloakModelUtils.buildRoleQualifier(null, "role"));
    }

    @Test
    void testConvertClientScopeName() {
        assertEquals("scope_name", KeycloakModelUtils.convertClientScopeName("scope name"));
        assertEquals("scope__name", KeycloakModelUtils.convertClientScopeName("scope  name")); // replaceAll handles all spaces
        assertEquals("scope_name", KeycloakModelUtils.convertClientScopeName("scope_name"));
    }

    @Test
    void testNormalizeGroupPath() {
        assertEquals("/group/subgroup", KeycloakModelUtils.normalizeGroupPath("group/subgroup"));
        assertEquals("/group/subgroup", KeycloakModelUtils.normalizeGroupPath("/group/subgroup"));
        assertEquals("/group/subgroup", KeycloakModelUtils.normalizeGroupPath("/group/subgroup/"));
        assertNull(KeycloakModelUtils.normalizeGroupPath(null));
    }

    @Test
    void testGetRequiredClientSecretLength() {
        int length = KeycloakModelUtils.getRequiredClientSecretLength();
        assertTrue(length > 0);
    }

    @Test
    void testGenerateCodeSecret() {
        String secret = KeycloakModelUtils.generateCodeSecret();
        assertNotNull(secret);
        assertEquals(36, secret.length());
        assertDoesNotThrow(() -> UUID.fromString(secret));
    }

    @Test
    void testGetPublicKey_Null() {
        assertNull(KeycloakModelUtils.getPublicKey(null));
    }

    @Test
    void testGetCertificate_Null() {
        assertNull(KeycloakModelUtils.getCertificate(null));
    }

    @Test
    void testGetPrivateKey_Null() {
        assertNull(KeycloakModelUtils.getPrivateKey(null));
    }

    @Test
    void testGetSecretKey_Null() {
        assertNull(KeycloakModelUtils.getSecretKey(null));
    }

    @Test
    void testGetSecretKey_Valid() {
        String secret = "my-secret-key";
        Key key = KeycloakModelUtils.getSecretKey(secret);
        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }
}
