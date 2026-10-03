package org.keycloak.models.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.cert.X509Certificate;

import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsPemTest {

    @BeforeAll
    public static void installCryptoProvider() {
        TestCryptoProvider.install();
    }

    @Test
    public void testGetPemFromKey_nullReturnsNull() {
        Assertions.assertNull(KeycloakModelUtils.getPemFromKey(null));
    }

    @Test
    public void testGetPemFromKey_validKey() {
        Key key = new SecretKeySpec("testkey".getBytes(), "HmacSHA256");
        String pem = KeycloakModelUtils.getPemFromKey(key);
        Assertions.assertNotNull(pem);
    }

    @Test
    public void testGetPemFromCertificate_nullReturnsNull() {
        Assertions.assertNull(KeycloakModelUtils.getPemFromCertificate(null));
    }

    @Test
    public void testGetPemFromCertificate_validCertificate() {
        X509Certificate cert = TestUtils.createDummyCert();
        String pem = KeycloakModelUtils.getPemFromCertificate(cert);
        Assertions.assertNotNull(pem);
    }
}

// Dummy utility to create a self‑signed certificate for testing purposes.
class TestUtils {
    public static X509Certificate createDummyCert() {
        // A real certificate cannot be built without a crypto provider, a stub with a
        // non-null encoding is enough to exercise the PEM encoding path.
        return TestCryptoProvider.certificate();
    }
}
