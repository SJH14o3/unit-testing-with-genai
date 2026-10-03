import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.RefreshToken;
import org.keycloak.services.managers.*;
import org.keycloak.services.util.DPoPUtil;
import org.keycloak.services.util.MtlsHoKTokenUtil;
import org.keycloak.authentication.authenticators.util.AcrStore;
import org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCAResult;
import com.fasterxml.jackson.core.JacksonException;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class TokenManagerTestStage5 {

    @Mock KeycloakSession session;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock ClientConnection connection;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock HttpRequest request;
    @Mock RefreshToken refreshToken;
    @Mock TokenVerifier<RefreshToken> tvMock;
    @Mock EmailAddressUtils utils;

    @Test
    void verifyRefreshToken_invalidTokenType() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("unknown-type");
        TokenManager manager = new TokenManager();
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "enc", false);
        });
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_invalidIssuer() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("wrong-client");
        when(refreshToken.getIssuer()).thenReturn("wrong-issuer");
        TokenManager manager = new TokenManager();
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "enc", false);
        });
        assertEquals("Invalid refresh token. Token client and authorized client don't match", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_expired() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(realm.getRefreshTokenExpiry()).thenReturn(3600L); // placeholder
        // Prepare verifier
        when(tvMock.withChecks(any(), any(), any())).thenReturn(tvMock);
        doThrow(new VerificationException("Expired")).when(tvMock).verify();
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(any(RefreshToken.class))).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            // Need to set checks chain to include TokenVerifier.IS_ACTIVE
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).withChecks(any(), any(), any())).thenReturn(tvMock);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(refreshToken)).withChecks(any(), any(), any()).verify();
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", true);
            });
            assertTrue(ex.getMessage().contains("Expired"));
        }
    }

    @Test
    void verifyRefreshToken_mtlsVerificationFail() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        // Configure client to use MTLS
        when(OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(mock(OIDCAdvancedConfigWrapper.class));
        when(OIDCAdvancedConfigWrapper.fromClientModel(client).isUseMtlsHokToken()).thenReturn(true);
        when(MtlsHoKTokenUtil.verifyTokenBindingWithClientCertificate(refreshToken, request, session)).thenReturn(false);
        try (MockedStatic<MtlsHoKTokenUtil> mtlsStatic = mockStatic(MtlsHoKTokenUtil.class)) {
            mtlsStatic.when(() -> MtlsHoKTokenUtil.verifyTokenBindingWithClientCertificate(refreshToken, request, session)).thenReturn(false);
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertEquals(MtlsHoKTokenUtil.CERT_VERIFY_ERROR_DESC, ex.getError());
        }
    }

    @Test
    void verifyRefreshToken_abcaJkt() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCA_JKT_TYPE);
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).thenReturn(mock(ABCAResult.class));
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).getAttestationJwt().getConfirmation().getJwk()).thenReturn(null);
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            // Let verifier pass
            when(TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            when(tvMock.verify()).thenReturn(null);
            when(tvMock.withChecks(any())).thenReturn(tvMock);
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertEquals("Invalid refresh token. Client attestation must be used with this refresh token", ex.getMessage());
        }
    }

    @Test
    void verifyRefreshToken_dpopJkt() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.services.util.DPoPUtil.DPOP_JKT_TYPE);
        when(session.getAttribute(eq(DPoPUtil.DPOP_SESSION_ATTRIBUTE), eq(org.keycloak.services.util.DPoP.class))).thenReturn(null);
        try (MockedStatic<MigrationUtils> mtStatic = mockStatic(MigrationUtils.class)) {
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertEquals("DPoP proof is missing", ex.getMessage());
        }
    }

    @Test
    void verifyRefreshToken_defaultJkt_unknown() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn("unknown-type");
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            when(TokenVerifier.createWithoutSignature(refreshToken)).thenReturn(tvMock);
            when(tvMock.withChecks(any())).thenReturn(tvMock);
            when(tvMock.verify()).thenReturn(null);
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                new TokenManager().verifyRefreshToken(session, realm, client, request, "enc", false);
            });
            assertTrue(ex.getMessage().contains("Unknown jkt type"));
        }
    }
}
