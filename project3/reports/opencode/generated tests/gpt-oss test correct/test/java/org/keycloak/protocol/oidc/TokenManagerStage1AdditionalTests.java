package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.crypto.KeyType;
import org.keycloak.http.HttpRequest;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.dpop.DPoP;
import org.keycloak.services.Urls;
import org.keycloak.util.TokenUtil;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.URI;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage1AdditionalTests {
    @Mock KeycloakSession session;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock
    ClientConnection connection;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock UserModel user;
    @Mock UserSessionModel userSession;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock
    RefreshToken oldToken;
    @Mock
    HttpRequest request;
    @Mock KeycloakContext context;
    @Mock KeycloakUriInfo keycloakUriInfo;
    @Mock UserSessionProvider sessions;
    @Mock org.keycloak.models.TokenManager tokens;

    // The manager under test must be a real instance, a mock would swallow every call
    private final TokenManager manager = new TokenManager();

    private static final URI BASE_URI = URI.create("http://localhost:8080");
    private static final String REALM_NAME = "test-realm";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_UUID = "client-uuid";
    private static String realmIssuer;

    @BeforeAll
    static void initProfile() {
        Profile.defaults();
        realmIssuer = Urls.realmIssuer(BASE_URI, REALM_NAME);
    }

    /**
     * Makes {@link AuthenticationManager#isSessionValid} return true for the mocked user session.
     */
    private void stubValidUserSession() {
        when(userSession.getStarted()).thenReturn((int) Time.currentTime());
        when(userSession.getLastSessionRefresh()).thenReturn((int) Time.currentTime());
    }

    /**
     * Makes {@link AuthenticationManager#isClientSessionValid} return true for the mocked client session.
     */
    private void stubValidClientSession() {
        when(clientSession.getStarted()).thenReturn((int) Time.currentTime());
        when(clientSession.getTimestamp()).thenReturn((int) Time.currentTime());
    }

    /**
     * Makes {@code session.sessions()} and {@code session.tokens()} return mocks.
     */
    private void stubSessionProviders() {
        when(session.sessions()).thenReturn(sessions);
    }

    private void stubDecodedRefreshToken() {
        stubSessionProviders();
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(RefreshToken.class))).thenReturn(oldToken);
    }

    /**
     * Makes the token issuer match the realm issuer, so the {@code RealmUrlCheck} of the token verifier succeeds.
     */
    private void stubValidTokenIssuer() {
        when(session.getContext()).thenReturn(context);
        when(context.getUri()).thenReturn(keycloakUriInfo);
        when(keycloakUriInfo.getBaseUri()).thenReturn(BASE_URI);
        when(realm.getName()).thenReturn(REALM_NAME);
        when(oldToken.getIssuer()).thenReturn(realmIssuer);
    }

    private void stubCurrentClient() {
        when(session.getContext()).thenReturn(context);
        when(context.getClient()).thenReturn(client);
        when(client.getId()).thenReturn(CLIENT_UUID);
        when(client.getClientId()).thenReturn(CLIENT_ID);
    }

    @Test
    void validateToken_offlineSessionNotFound() {
        // Token type is OFFLINE
        stubSessionProviders();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("offline1");
        // No offline user session is registered for the session state
        when(sessions.getOfflineUserSession(realm, "offline1")).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Offline user session not found", ex.getMessage());
    }

    @Test
    void validateToken_onlineUserNull() {
        // Online token, userSession found but user is null
        stubSessionProviders();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("online1");
        when(sessions.getUserSession(realm, "online1")).thenReturn(userSession);
        stubValidUserSession();
        when(userSession.getUser()).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Unknown user", ex.getMessage());
        assertEquals("Invalid refresh token", ex.getDescription());
    }

    @Test
    void validateToken_clientSessionNull() {
        stubSessionProviders();
        stubCurrentClient();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("online2");
        when(sessions.getUserSession(realm, "online2")).thenReturn(userSession);
        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        stubValidUserSession();
        when(userSession.getAuthenticatedClientSessionByClient(CLIENT_UUID)).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertTrue(ex.getMessage().contains("Session doesn't have required client"));
    }

    @Test
    void validateToken_clientMismatch() {
        stubSessionProviders();
        stubCurrentClient();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("online3");
        when(oldToken.getIssuedFor()).thenReturn("other-client");
        when(sessions.getUserSession(realm, "online3")).thenReturn(userSession);
        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        stubValidUserSession();
        when(userSession.getAuthenticatedClientSessionByClient(CLIENT_UUID)).thenReturn(clientSession);
        stubValidClientSession();
        when(clientSession.getClient()).thenReturn(client);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Unmatching clients", ex.getMessage());
    }

    @Test
    void validateToken_offlineNoAccessGrant() {
        // Offline token for a valid offline user session without the offline_access client scope
        stubSessionProviders();
        stubCurrentClient();
        doReturn(Set.of()).when(session).getAttributeOrDefault(anyString(), any());
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("offline3");
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(sessions.getOfflineUserSession(realm, "offline3")).thenReturn(userSession);
        when(userSession.isOffline()).thenReturn(true);
        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        stubValidUserSession();
        when(userSession.getAuthenticatedClientSessionByClient(CLIENT_UUID)).thenReturn(clientSession);
        stubValidClientSession();
        when(clientSession.getClient()).thenReturn(client);
        when(clientSession.getUserSession()).thenReturn(userSession);
        // The client itself is resolved as client scope, but not the offline_access one
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Offline session invalid because offline access not granted anymore", ex.getMessage());
    }

    // ============================= verifyRefreshToken tests ============================
    @Test
    void verifyRefreshToken_invalidTokenType() {
        stubDecodedRefreshToken();
        when(oldToken.getType()).thenReturn("invalid");
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_clientMismatch() {
        stubDecodedRefreshToken();
        stubValidTokenIssuer();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(client.getClientId()).thenReturn("other-client");
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Invalid refresh token. Token client and authorized client don't match", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_mtlsFailure() {
        stubDecodedRefreshToken();
        stubValidTokenIssuer();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(client.getClientId()).thenReturn(CLIENT_ID);
        when(client.getAttribute(OIDCConfigAttributes.USE_MTLS_HOK_TOKEN)).thenReturn("true");
        // The token has no confirmation, so the client certificate binding cannot be verified
        when(oldToken.getConfirmation()).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals(MtlsHoKTokenUtil.CERT_VERIFY_ERROR_DESC, ex.getDescription());
    }

    @Test
    void verifyRefreshToken_abcaNullAttestation() {
        stubDecodedRefreshToken();
        stubValidTokenIssuer();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(client.getClientId()).thenReturn(CLIENT_ID);
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
        stubDecodedRefreshToken();
        stubValidTokenIssuer();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(client.getClientId()).thenReturn(CLIENT_ID);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(oldToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb-mismatch");
        when(cnf.getJktType()).thenReturn(org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCA_JKT_TYPE);
        ABCAResult result = mock(ABCAResult.class);
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).thenReturn(result);
        JWK jwk = mock(JWK.class);
        when(result.getAttestationJwt()).thenReturn(mock(AttestationBasedClientAuthenticator.ClientAttestationJwt.class));
        when(result.getAttestationJwt().getConfirmation()).thenReturn(mock(AttestationBasedClientAuthenticator.Confirmation.class));
        when(result.getAttestationJwt().getConfirmation().getJwk()).thenReturn(jwk);
        // A real thumbprint is computed for the key, and it can never match "thumb-mismatch"
        when(jwk.getKeyType()).thenReturn(KeyType.RSA);

        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("Attestation-Based Key mismatch", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_dpopMissingProof() {
        stubDecodedRefreshToken();
        stubValidTokenIssuer();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(client.getClientId()).thenReturn(CLIENT_ID);
        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(oldToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumb");
        when(cnf.getJktType()).thenReturn(org.keycloak.services.util.DPoPUtil.DPOP_JKT_TYPE);
        when(session.getAttribute(eq(DPoPUtil.DPOP_SESSION_ATTRIBUTE), eq(DPoP.class))).thenReturn(null);

        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyRefreshToken(session, realm, client, request, "token", false);
        });
        assertEquals("DPoP proof is missing", ex.getMessage());
    }

    @Test
    void verifyRefreshToken_unknownJkt() {
        stubDecodedRefreshToken();
        stubValidTokenIssuer();
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getIssuedFor()).thenReturn(CLIENT_ID);
        when(client.getClientId()).thenReturn(CLIENT_ID);
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
