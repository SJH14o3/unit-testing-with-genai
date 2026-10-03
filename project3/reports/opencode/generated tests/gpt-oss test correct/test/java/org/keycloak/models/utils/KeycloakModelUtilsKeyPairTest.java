package org.keycloak.models.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import org.keycloak.common.util.PemUtils;
import org.keycloak.common.util.KeyUtils;
import org.keycloak.common.util.CertificateUtils;
import org.keycloak.representations.idm.CertificateRepresentation;

import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsKeyPairTest {

    @BeforeAll
    public static void installCryptoProvider() {
        TestCryptoProvider.install();
    }

    @Test
    public void testGenerateKeyPairCertificate_default() {
        CertificateRepresentation rep = KeycloakModelUtils.generateKeyPairCertificate("CN=test");
        Assertions.assertNotNull(rep);
        Assertions.assertNotNull(rep.getCertificate());
        Assertions.assertNotNull(rep.getPrivateKey());
    }

    @Test
    public void testGenerateKeyPairCertificate_withParams() {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_YEAR, 365);
        CertificateRepresentation rep = KeycloakModelUtils.generateKeyPairCertificate("CN=test", 2048, endDate);
        Assertions.assertNotNull(rep);
        Assertions.assertNotNull(rep.getCertificate());
        Assertions.assertNotNull(rep.getPrivateKey());
    }
}
