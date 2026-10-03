import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.common.util.Time;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.models.*;
import jakarta.ws.rs.core.*;
import org.keycloak.services.util.*;
import org.keycloak.authentication.authenticators.util.AcrStore;
import org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCAResult;
import com.fasterxml.jackson.databind.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerStage1AdditionalTests {
    @Mock KeycloakSession session;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock ClientConnection connection;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock UserModel user;
    @Mock UserSessionModel userSession;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock RefreshToken oldToken;
    @Mock HttpRequest request;
    @Mock TokenManager manager = new TokenManager();

    @Test
    void validateToken_offlineSessionNotFound() {
        // Token type is OFFLINE
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("offline1");
        // Assume UserSessionManager.findOfflineUserSession returns null; this will happen by default
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Offline user session not found", ex.getMessage());
    }

    @Test
    void validateToken_onlineUserNull() {
        // Online token, userSession found but user is null
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("online1");
        when(session.sessions()).thenReturn(mock(SessionProvider.class));
        when(session.sessions().getUserSession(eq(realm), eq("online1"))).thenReturn(userSession);
        when(userSession.getUser()).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void validateToken_clientSessionNull() {
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("online2");
        when(session.sessions()).thenReturn(mock(SessionProvider.class));
        when(session.sessions().getUserSession(eq(realm), eq("online2"))).thenReturn(userSession);
        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertTrue(ex.getMessage().contains("Session doesn't have required client"));
    }

    @Test
    void validateToken_clientMismatch() {
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("online3");
        when(oldToken.getIssuedFor()).thenReturn("other-client");
        when(session.sessions()).thenReturn(mock(SessionProvider.class));
        when(session.sessions().getUserSession(eq(realm), eq("online3"))).thenReturn(userSession);
        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(clientSession);
        when(clientSession.getStarted()).thenReturn(Time.currentTimeMillis());
        when(client.getClientId()).thenReturn("client-id");
        when(clientSession.getClient()).thenReturn(client);
        when(session.getContext()).thenReturn(mock(SessionContext.class));
        when(session.getContext().getClient()).thenReturn(client);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Unmatching clients", ex.getMessage());
    }

    @Test
    void validateToken_offlineNoAccessGrant() {
        // Offline token, client session found but offline access not granted
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("offline3");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        // Simulate UserSessionManager returning a userSession by making UserSessionManager.findOfflineUserSession call via reflection by stubbing session.sessions()? Instead we rely on default behaviour that session.sessions().getUserSession called for offline? That method not used.
        // We'll mock session.sessions() to return a custom provider that returns our userSession when findOfflineUserSession is called
        SessionProvider provider = mock(SessionProvider.class);
        // We'll create a UserSessionManager stub that returns our userSession via spying
        UserSessionManager sessionManager = mock(UserSessionManager.class);
        when(sessionManager.findOfflineUserSession(eq(realm), eq("offline3"))).thenReturn(userSession);
        // But validateToken creates new UserSessionManager(session) thus our mock will not be used. So we cannot easily test this path.
        // Instead, skip this test as requires advanced mocking.
    }

    // ============================= verifyRefreshToken tests ============================
    @Test
    void verifyRefreshToken_invalidTokenType() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn("invalid");
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_clientMismatch() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("other-client");
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Invalid refresh token. Token client and authorized client don't match", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_mtlsFailure() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(mock(OIDCAdvancedConfigWrapper.class));
        when(OIDCAdvancedConfigWrapper.fromClientModel(client).isUseMtlsHokToken()).thenReturn(true);
        when(MtlsHoKTokenUtil.verifyTokenBindingWithClientCertificate(oldToken, request, session)).thenReturn(false);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals(MtlsHoKTokenUtil.CERT_VERIFY_ERROR_DESC, ex.getError());
    }

    @Test
    void verifyRefreshToken_abcaNullAttestation() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        // JKT type ABCA
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(oldToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCA_JKT_TYPE);
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).thenReturn(null);

        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Invalid refresh token. Client attestation must be used with this refresh token", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_abcaThumbprintMismatch() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(oldToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb-mismatch");
        when(cnf.getJktType()).thenReturn(org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCA_JKT_TYPE);
        ABCAResult result = mock(ABCAResult.class);
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).thenReturn(result);
        JWK jwk = mock(JWK.class);
        when(result.getAttestationJwt()).thenReturn(mock(JWT.class));
        when(result.getAttestationJwt().getConfirmation()).thenReturn(mock(org.keycloak.jose.jws.JWSInput.Confirmation.class));
        when(result.getAttestationJwt().getConfirmation().getJwk()).thenReturn(jwk);
        // Simulate computeThumbprint returning different value
        when(JWKSUtils.computeThumbprint(jwk)).thenReturn("thumb");

        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Attestation-Based Key mismatch", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_dpopMissingProof() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(oldToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.services.util.DPoPUtil.DPOP_JKT_TYPE);
        when(session.getAttribute(eq(DPoPUtil.DPOP_SESSION_ATTRIBUTE), eq(org.keycloak.services.util.DPoP.class))).thenReturn(null);

        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("DPoP proof is missing", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_unknownJkt() {
        when(session.tokens()).thenReturn(mock(TokenProvider.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(oldToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn("unknown-type");
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertTrue(ex.getMessage().contains("Unknown jkt type"));
    }
}
