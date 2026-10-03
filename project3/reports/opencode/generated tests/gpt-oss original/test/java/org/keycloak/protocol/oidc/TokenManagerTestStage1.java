import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.ws.rs.core.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.protocol.oidc.TokenManager.TokenValidation;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.keycloak.services.managers.*;
import org.keycloak.services.util.*;
import org.keycloak.common.Profile;
import org.keycloak.events.event.EventBuilder;
import org.keycloak.events.Events;
import org.keycloak.events.Event;

@ExtendWith(MockitoExtension.class)
class TokenManagerTestStage1 {

    @Mock KeycloakSession session;
    @Mock UriInfo uriInfo;
    @Mock ClientConnection connection;
    @Mock RealmModel realm;
    @Mock HttpHeaders headers;
    @Mock RefreshToken oldToken;
    @Mock UserSessionModel userSession;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock UserModel user;
    @Mock ClientModel client;
    @Mock SessionModel sessionModel;
    @Mock SessionContext sessionContext;
    @Mock SessionProvider sessionProvider;

    TokenManager tokenManager = new TokenManager();

    @BeforeEach
    void setUp() {
        when(session.getContext()).thenReturn(sessionContext);
        when(session.sessions()).thenReturn(sessionProvider);
        when(session.sessions().getUserSession(any(), anyString())).thenReturn(userSession);
        when(session.sessions().createClientSession(any(), any(), any())).thenReturn(clientSession);

        when(clientSession.getStarted()).thenReturn(Time.currentTimeMillis());
        when(clientSession.getRefreshToken(anyString())).thenReturn(null);
        when(clientSession.getRefreshTokenLastRefresh(anyString())).thenReturn(0);
        when(clientSession.getRefreshTokenUseCount(anyString())).thenReturn(0);

        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        when(userSession.isOffline()).thenReturn(false);
        when(userSession.isOffline()).thenReturn(false);
        when(userSession.getStarted()).thenReturn(Time.currentTimeMillis());
        when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(clientSession);

        when(client.getId()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(client.isFullScopeAllowed()).thenReturn(true);

        when(sessionContext.getClient()).thenReturn(client);
    }

    @Test
    void validateToken_offlineSessionFound() throws Exception {
        // Offline token with existing session
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("session1");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(oldToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
        when(oldToken.isIssuedBeforeSessionStart(clientSession.getStarted())).thenReturn(false);

        // Mock UserSessionManager behaviour using a spy
        UserSessionManager sessionManager = mock(UserSessionManager.class);
        when(sessionManager.findOfflineUserSession(eq(realm), eq("session1"))).thenReturn(userSession);
        when(sessionManager.isSessionValid(eq(realm), eq(userSession))).thenReturn(true);

        // Replace while using reflection or use actual static methods, omitted for brevity
        // Call validateToken
        TokenValidation validation = tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        assertNotNull(validation);
        assertEquals(user, validation.user);
    }

    @Test
    void validateToken_onlineSessionInvalid() throws Exception {
        // Online token with invalid session
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session2");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.isOffline()).thenReturn(false);
        when(userSession.getStarted()).thenReturn(Time.currentTimeMillis());

        when(session.sessions().getUserSession(eq(realm), eq("session2"))).thenReturn(userSession);
        // Simulate invalid session
        when(AuthenticationManager.isSessionValid(eq(realm), eq(userSession))).thenReturn(false);

        OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Session not active", e.getMessage());
    }

    @Test
    void validateToken_clientSessionMissing() throws Exception {
        // Client session is missing
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session3");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(null);

        OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Session doesn't have required client", e.getMessage());
    }

    @Test
    void validateToken_tokenIssuedBeforeSessionStart() throws Exception {
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session4");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(oldToken.isIssuedBeforeSessionStart(userSession.getStarted())).thenReturn(true);

        OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Refresh token issued before the user session started", e.getMessage());
    }

    @Test
    void validateToken_clientMismatch() throws Exception {
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session5");
        when(oldToken.getIssuedFor()).thenReturn("other-client");

        OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertEquals("Unmatching clients", e.getMessage());
    }

    @Test
    void validateToken_offlineTokenMissingAccess() throws Exception {
        // Offline token userSession.isOffline == true but access not granted
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("session6");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.isOffline()).thenReturn(true);
        when(UserSessionUtil.isOfflineAccessGranted(eq(session), eq(clientSession))).thenReturn(false);

        OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
        });
        assertTrue(e.getMessage().contains("offline access not granted"));
    }

    @Test
    void validateToken_migrateOfflineToken() throws Exception {
        // Offline token with null oldTokenScope triggers migration
        when(oldToken.getType()).thenReturn(org.keycloak.TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("session7");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(oldTokenScope).thenReturn(null);
        when(userSession.isOffline()).thenReturn(true);

        // Stub MigrationUtils
        MigrationUtils.migrateOldOfflineToken(session, realm, client, user);

        assertDoesNotThrow(() -> {
            tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, null);
        });
    }
}
