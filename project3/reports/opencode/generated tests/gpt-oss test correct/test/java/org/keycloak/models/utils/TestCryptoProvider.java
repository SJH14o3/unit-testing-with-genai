package org.keycloak.models.utils;

import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.keycloak.common.crypto.CertificateUtilsProvider;
import org.keycloak.common.crypto.CryptoIntegration;
import org.keycloak.common.crypto.CryptoProvider;
import org.keycloak.common.crypto.PemUtilsProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Minimal {@link CryptoProvider} test double installed into {@link CryptoIntegration}.
 * <p>
 * {@code CryptoIntegration} is only initialized when a {@code CryptoProvider} is discoverable on the
 * classpath, which is not the case for plain unit tests, so every call reaching
 * {@code KeyUtils}/{@code PemUtils}/{@code CertificateUtils} would fail with
 * {@code IllegalStateException: Please init first before obtaining provider}.
 */
public final class TestCryptoProvider {

    private TestCryptoProvider() {
    }

    /**
     * Installs the test provider, unless a real one is already registered.
     */
    public static void install() {
        if (CryptoIntegration.isInitialised()) {
            return;
        }

        CryptoProvider provider = mock(CryptoProvider.class);
        try {
            when(provider.getKeyPairGen(anyString())).thenAnswer(invocation -> keyPairGenerator(invocation.getArgument(0)));
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException(e);
        }
        // the stubs have to be created before starting the stubbing of the provider itself
        PemUtilsProvider pemUtils = new TestPemUtilsProvider();
        CertificateUtilsProvider certificateUtils = certificateUtils();
        when(provider.getPemUtils()).thenReturn(pemUtils);
        when(provider.getCertificateUtils()).thenReturn(certificateUtils);

        CryptoIntegration.setProvider(provider);
    }

    private static KeyPairGenerator keyPairGenerator(String algorithm) {
        try {
            return KeyPairGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return a certificate stub exposing a non-null encoding, so that it can be PEM encoded
     */
    public static X509Certificate certificate() {
        X509Certificate certificate = mock(X509Certificate.class);
        try {
            when(certificate.getEncoded()).thenAnswer(invocation -> new byte[] {0x01, 0x02, 0x03, 0x04});
        } catch (CertificateEncodingException e) {
            throw new IllegalStateException(e);
        }
        return certificate;
    }

    private static CertificateUtilsProvider certificateUtils() {
        X509Certificate certificate = certificate();
        CertificateUtilsProvider utils = mock(CertificateUtilsProvider.class);
        when(utils.generateV1SelfSignedCertificate(any(), any(), any(), any())).thenReturn(certificate);
        return utils;
    }

    /**
     * Behaves like the production PEM provider: {@code null} (or a key/certificate without encoding)
     * is encoded as {@code null}, everything else is base64 encoded between PEM boundaries.
     */
    public static class TestPemUtilsProvider extends PemUtilsProvider {

        @Override
        public PrivateKey decodePrivateKey(String privateKeyPem) {
            throw new UnsupportedOperationException("Not needed for the tests using this provider");
        }

        @Override
        protected String encode(Object object) {
            byte[] encoded = encoded(object);
            if (encoded == null) {
                return null;
            }
            String type = object instanceof Certificate ? "CERTIFICATE" : "KEY";
            return "-----BEGIN " + type + "-----\n"
                    + Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(encoded)
                    + "\n-----END " + type + "-----\n";
        }

        private byte[] encoded(Object object) {
            byte[] encoded;
            if (object == null) {
                encoded = null;
            } else if (object instanceof Key) {
                encoded = ((Key) object).getEncoded();
            } else if (object instanceof Certificate) {
                encoded = certificateEncoding((Certificate) object);
            } else {
                encoded = (byte[]) object;
            }
            return encoded == null || encoded.length == 0 ? null : encoded;
        }

        private byte[] certificateEncoding(Certificate certificate) {
            try {
                return certificate.getEncoded();
            } catch (CertificateEncodingException e) {
                return null;
            }
        }
    }
}
