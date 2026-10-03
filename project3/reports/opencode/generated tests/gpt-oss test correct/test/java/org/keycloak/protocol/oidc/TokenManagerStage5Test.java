package org.keycloak.protocol.oidc;

import java.net.URI;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.keycloak.common.ClientConnection;
import org.keycloak.http.HttpRequest;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.dpop.DPoP;
import org.keycloak.util.TokenUtil;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.common.VerificationException;
import org.keycloak.models.*;
import org.keycloak.representations.RefreshToken;
import org.keycloak.services.util.DPoPUtil;
import org.keycloak.services.util.MtlsHoKTokenUtil;
import org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCAResult;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.HttpHeaders;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage5Test {

    @Mock KeycloakSession session;
    @Mock KeycloakContext sessionContext;
    @Mock KeycloakUriInfo keycloakUriInfo;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock
    ClientConnection connection;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock
    HttpRequest request;
    @Mock RefreshToken refreshToken;

    @Mock  org.keycloak.models.TokenManager tokenManager;

    /**
     * {@code verifyRefreshToken} builds the realm issuer out of the session context and delegates the
     * JWT checks to a {@link TokenVerifier} created by a static factory. Both have to be provided,
     * otherwise the test fails before reaching the branch under test.
     */
    private MockedStatic<TokenVerifier> mockTokenVerifier() {
        TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class, Answers.RETURNS_SELF);
        MockedStatic<TokenVerifier> tokenVerifier = mockStatic(TokenVerifier.class);
        tokenVerifier.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(verifier);
        return tokenVerifier;
    }

    @BeforeEach
    void setUp() {
        when(session.getContext()).thenReturn(sessionContext);
        when(sessionContext.getUri()).thenReturn(keycloakUriInfo);
        when(keycloakUriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:8080"));
        when(realm.getName()).thenReturn("test-realm");
        when(client.getRealm()).thenReturn(realm);
        when(session.tokens()).thenReturn(tokenManager);
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
    }

    @Test
    void verifyRefreshToken_invalidTokenType() {
        when(refreshToken.getType()).thenReturn("unknown-type");
        TokenManager manager = new TokenManager();
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "enc", false);
        });
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_invalidIssuer() {
        when(refreshToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("wrong-client");
        when(refreshToken.getIssuer()).thenReturn("wrong-issuer");
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            TokenManager manager = new TokenManager();
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                manager.verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertEquals("Invalid refresh token. Token client and authorized client don't match", ex.getMessage());
        }
    }

    @Test
    void verifyRefreshToken_expired() throws VerificationException {
        when(refreshToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        // Prepare verifier
        TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class, Answers.RETURNS_SELF);
        doThrow(new VerificationException("Expired")).when(verifier).verify();
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(verifier);
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", true);
            });
            assertTrue(ex.getMessage().contains("Expired"));
        }
    }

    @Test
    void verifyRefreshToken_mtlsVerificationFail() {
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        // Configure client to use MTLS
        when(client.getAttribute(OIDCConfigAttributes.USE_MTLS_HOK_TOKEN)).thenReturn("true");
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            try (MockedStatic<MtlsHoKTokenUtil> mtlsStatic = mockStatic(MtlsHoKTokenUtil.class)) {
                mtlsStatic.when(() -> MtlsHoKTokenUtil.verifyTokenBindingWithClientCertificate(refreshToken, request, session)).thenReturn(false);
                OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                    new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
                });
                assertEquals(MtlsHoKTokenUtil.CERT_VERIFY_ERROR_DESC, ex.getMessage());
                assertEquals(OAuthErrorException.UNAUTHORIZED_CLIENT, ex.getError());
            }
        }
    }

    @Test
    void verifyRefreshToken_abcaJkt() {
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCA_JKT_TYPE);
        // No attestation result in the session, so the attestation branch must complain
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).thenReturn(null);
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertEquals("Invalid refresh token. Client attestation must be used with this refresh token", ex.getMessage());
        }
    }

    @Test
    void verifyRefreshToken_dpopJkt() {
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.services.util.DPoPUtil.DPOP_JKT_TYPE);
        when(session.getAttribute(eq(DPoPUtil.DPOP_SESSION_ATTRIBUTE), eq(DPoP.class))).thenReturn(null);
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertEquals("DPoP proof is missing", ex.getMessage());
        }
    }

    @Test
    void verifyRefreshToken_defaultJkt_unknown() {
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn("unknown-type");
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertTrue(ex.getMessage().contains("Unknown jkt type"));
        }
    }
}
