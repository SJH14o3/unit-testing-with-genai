package org.keycloak.protocol.oidc;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.common.Profile.Feature;
import org.keycloak.common.Profile.ProfileName;
import org.keycloak.migration.migrators.MigrationUtils;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.ws.rs.core.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.protocol.oidc.TokenManager.TokenValidation;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.keycloak.services.managers.*;
import org.keycloak.services.util.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage1Test {

    @Mock KeycloakSession session;
    @Mock UriInfo uriInfo;
    @Mock
    ClientConnection connection;
    @Mock RealmModel realm;
    @Mock HttpHeaders headers;
    @Mock
    RefreshToken oldToken;
    @Mock UserSessionModel userSession;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock UserModel user;
    @Mock ClientModel client;
    @Mock
    KeycloakContext sessionContext;
    @Mock UserSessionProvider sessionProvider;

    TokenManager tokenManager = new TokenManager();

    /**
     * {@link Profile#isFeatureEnabled(Feature)} is used all over {@code TokenManager} and its
     * collaborators, but the {@link Profile} singleton is only initialized by the server at
     * bootstrap. Initialize it with every feature disabled so the feature checks return a
     * deterministic value instead of throwing a {@link NullPointerException}.
     */
    @BeforeAll
    static void initProfile() {
        Map<Feature, Boolean> features = new EnumMap<>(Feature.class);
        for (Feature feature : Feature.values()) {
            features.put(feature, false);
        }
        Profile.init(ProfileName.DEFAULT, features);
    }

    @BeforeEach
    void setUp() {
        when(session.getContext()).thenReturn(sessionContext);
        when(session.sessions()).thenReturn(sessionProvider);
        when(session.sessions().getUserSession(any(), anyString())).thenReturn(userSession);
        when(session.sessions().getOfflineUserSession(any(), anyString())).thenReturn(userSession);
        when(session.sessions().createClientSession(any(), any(), any())).thenReturn(clientSession);

        when(clientSession.getStarted()).thenReturn((int) Time.currentTimeMillis());
        when(clientSession.getClient()).thenReturn(client);
        when(clientSession.getUserSession()).thenReturn(userSession);
        when(clientSession.getRefreshToken(anyString())).thenReturn(null);
        when(clientSession.getRefreshTokenLastRefresh(anyString())).thenReturn(0);
        when(clientSession.getRefreshTokenUseCount(anyString())).thenReturn(0);

        when(userSession.getUser()).thenReturn(user);
        when(user.isEnabled()).thenReturn(true);
        when(userSession.isOffline()).thenReturn(false);
        when(userSession.getStarted()).thenReturn((int) Time.currentTimeMillis());
        when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(clientSession);

        when(client.getId()).thenReturn("client-id");
        when(client.getClientId()).thenReturn("client-id");
        when(client.isFullScopeAllowed()).thenReturn(true);

        when(sessionContext.getClient()).thenReturn(client);
    }

    /**
     * Session validity is decided by {@link AuthenticationManager} through static helpers that
     * depend on realm expiration configuration and would run a full back-channel logout. Stub them
     * so the test only exercises {@link TokenManager#validateToken}.
     */
    private MockedStatic<AuthenticationManager> mockAuthenticationManager(boolean userSessionValid,
                                                                           boolean clientSessionValid) {
        MockedStatic<AuthenticationManager> authenticationManager = mockStatic(AuthenticationManager.class);
        authenticationManager.when(() -> AuthenticationManager.isSessionValid(any(), any()))
                .thenReturn(userSessionValid);
        authenticationManager.when(() -> AuthenticationManager.isClientSessionValid(any(), any(), any(), any()))
                .thenReturn(clientSessionValid);
        return authenticationManager;
    }

    @Test
    void validateToken_offlineSessionFound() throws Exception {
        // Offline token with existing session
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("session1");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(oldToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);

        try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(true, true)) {
            TokenValidation validation = tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
            assertNotNull(validation);
            assertEquals(user, validation.user);
            assertEquals(userSession, validation.userSession);
        }
    }

    @Test
    void validateToken_onlineSessionInvalid() throws Exception {
        // Online token with invalid session
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session2");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.isOffline()).thenReturn(false);
        when(userSession.getStarted()).thenReturn((int) Time.currentTimeMillis());

        when(session.sessions().getUserSession(eq(realm), eq("session2"))).thenReturn(userSession);
        // Simulate invalid session
        try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(false, true)) {
            OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
                tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
            });
            assertEquals("Session not active", e.getMessage());
        }
    }

    @Test
    void validateToken_clientSessionMissing() throws Exception {
        // Client session is missing
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session3");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(null);

        try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(true, true)) {
            OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
                tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
            });
            assertEquals("Session doesn't have required client", e.getMessage());
        }
    }

    @Test
    void validateToken_tokenIssuedBeforeSessionStart() throws Exception {
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session4");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(oldToken.isIssuedBeforeSessionStart(userSession.getStarted())).thenReturn(true);

        try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(true, true)) {
            OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
                tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
            });
            assertEquals("Refresh token issued before the user session started", e.getMessage());
        }
    }

    @Test
    void validateToken_clientMismatch() throws Exception {
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_REFRESH);
        when(oldToken.getSessionState()).thenReturn("session5");
        when(oldToken.getIssuedFor()).thenReturn("other-client");

        try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(true, true)) {
            OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
                tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
            });
            assertEquals("Unmatching clients", e.getMessage());
        }
    }

    @Test
    void validateToken_offlineTokenMissingAccess() throws Exception {
        // Offline token userSession.isOffline == true but access not granted
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("session6");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.isOffline()).thenReturn(true);

        try (MockedStatic<UserSessionUtil> userSessionUtil = mockStatic(UserSessionUtil.class)) {
            userSessionUtil.when(() -> UserSessionUtil.isOfflineAccessGranted(session, clientSession)).thenReturn(false);
            try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(true, true)) {
                OAuthErrorException e = assertThrows(OAuthErrorException.class, () -> {
                    tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, "openid");
                });
                assertTrue(e.getMessage().contains("offline access not granted"));
            }
        }
    }

    @Test
    void validateToken_migrateOfflineToken() throws Exception {
        // Offline token with null oldTokenScope triggers migration
        when(oldToken.getType()).thenReturn(TokenUtil.TOKEN_TYPE_OFFLINE);
        when(oldToken.getSessionState()).thenReturn("session7");
        when(oldToken.getIssuedFor()).thenReturn("client-id");
        when(userSession.isOffline()).thenReturn(true);

        try (MockedStatic<UserSessionUtil> userSessionUtil = mockStatic(UserSessionUtil.class)) {
            userSessionUtil.when(() -> UserSessionUtil.isOfflineAccessGranted(session, clientSession)).thenReturn(true);
            // The migration itself needs a provisioned realm, so only assert it gets triggered
            try (MockedStatic<MigrationUtils> migrationUtils = mockStatic(MigrationUtils.class)) {
                try (MockedStatic<AuthenticationManager> am = mockAuthenticationManager(true, true)) {
                    assertDoesNotThrow(() -> {
                        tokenManager.validateToken(session, uriInfo, connection, realm, oldToken, headers, null);
                    });
                    migrationUtils.verify(() -> MigrationUtils.migrateOldOfflineToken(session, realm, client, user));
                }
            }
        }
    }
}
