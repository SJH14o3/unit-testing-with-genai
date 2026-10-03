package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.OAuthErrorException;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.TokenUtil;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.encode.AccessTokenContextEncoder;
import org.keycloak.protocol.oidc.encode.TokenContextEncoderProvider;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.UserSessionManager;
import org.keycloak.util.TokenUtil;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerIdentityTest {

    @Mock
    KeycloakSession session;
    @Mock
    UriInfo uriInfo;
    @Mock
    ClientConnection connection;
    @Mock
    RealmModel realm;
    @Mock
    RefreshToken refreshToken;
    @Mock
    HttpHeaders headers;
    @Mock
    UserModel user;
    @Mock
    UserSessionModel userSession;
    @Mock
    ClientModel client;
    @Mock
    AuthenticatedClientSessionModel clientSession;
    @Mock
    ClientSessionContext clientSessionCtx;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager = new TokenManager();
    }

    @Test
    void validateToken_Online_Success() throws OAuthErrorException {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals(refreshToken.getType())).thenReturn(false);
            when(refreshToken.getType()).thenReturn("refresh");
            when(refreshToken.getSessionState()).thenReturn("session-state");
            
            when(session.sessions()).thenReturn(mock(UserSessions.class));
            when(session.sessions().getUserSession(any(), anyString())).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(any(), any())).thenReturn(true);
            
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            when(userSession.getStarted()).thenReturn(1000L);
            when(refreshToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
            
            when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
            when(session.getContext().getClient()).thenReturn(client);
            when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(clientSession);
            authManagerMock.when(() -> AuthenticationManager.isClientSessionValid(any(), any(), any(), any())).thenReturn(true);
            when(clientSession.getStarted()).thenReturn(1000L);
            when(clientSession.getId()).thenReturn("client-id");
            when(client.getClientId()).thenReturn("client-id");
            when(refreshToken.getIssuedFor()).thenReturn("client-id");
            when(userSession.isOffline()).thenReturn(false);
            
            TokenManager.TokenValidation result = tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null);
            
            assertNotNull(result);
            assertEquals(user, result.user);
            assertEquals(userSession, result.userSession);
        }
    }

    @Test
    void validateToken_Online_SessionNotActive_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals(refreshToken.getType())).thenReturn(false);
            when(refreshToken.getType()).thenReturn("refresh");
            when(refreshToken.getSessionState()).thenReturn("session-state");
            
            when(session.sessions()).thenReturn(mock(UserSessions.class));
            when(session.sessions().getUserSession(any(), anyString())).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(any(), any())).thenReturn(false);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_Online_UserDisabled_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals(refreshToken.getType())).thenReturn(false);
            when(refreshToken.getType()).thenReturn("refresh");
            when(refreshToken.getSessionState()).thenReturn("session-state");
            
            when(session.sessions()).thenReturn(mock(UserSessions.class));
            when(session.sessions().getUserSession(any(), anyString())).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(any(), any())).thenReturn(true);
            
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(false);

            OAuthErrorException exception = assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
            assertEquals("User disabled", exception.getError());
        }
    }

    @Test
    void isUserValid_UserNull_ReturnsFalse() {
        assertFalse(TokenManager.isUserValid(session, realm, mock(AccessToken.class), null));
    }

    @Test
    void isUserValid_UserDisabled_ReturnsFalse() {
        when(user.isEnabled()).thenReturn(false);
        assertFalse(TokenManager.isUserValid(session, realm, mock(AccessToken.class), user));
    }

    @Test
    void isUserValid_UserValid_ReturnsTrue() {
        when(user.isEnabled()).thenReturn(true);
        // Mock validateUserNotBefore to return true (it's static and might call TokenVerifier)
        // For simplicity in this test, we assume validateUserNotBefore works or we mock its effect.
        // Since it's a static method in the same class, we might need mockStatic for TokenVerifier.
        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class)) {
            TokenVerifier<AccessToken> verifier = mock(TokenVerifier.class);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);
            when(verifier.withChecks(any())).thenReturn(verifier);
            
            assertTrue(TokenManager.isUserValid(session, realm, mock(AccessToken.class), user));
        }
    }

    @Test
    void lookupUserFromStatelessToken_BySubject_Success() {
        AccessToken token = mock(AccessToken.class);
        when(token.getSubject()).thenReturn("user-id");
        when(session.users()).thenReturn(mock(UserRegistration.class));
        when(session.users().getUserById(realm, "user-id")).thenReturn(user);

        UserModel result = TokenManager.lookupUserFromStatelessToken(session, realm, token);

        assertEquals(user, result);
    }

    @Test
    void lookupUserFromStatelessToken_ByUsername_Success() {
        AccessToken token = mock(AccessToken.class);
        when(token.getSubject()).thenReturn(null);
        when(token.getPreferredUsername()).thenReturn("test-user");
        when(session.users()).thenReturn(mock(UserRegistration.class));
        when(session.users().getUserByUsername(realm, "test-user")).thenReturn(user);

        UserModel result = TokenManager.lookupUserFromStatelessToken(session, realm, token);

        assertEquals(user, result);
    }

    @Test
    void toRefreshToken_Success() throws JWSInputException, OAuthErrorException {
        AccessToken token = mock(AccessToken.class);
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(eq("encoded-token"), eq(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = tokenManager.toRefreshToken(session, "encoded-token");

        assertEquals(refreshToken, result);
    }

    @Test
    void toRefreshToken_NullToken_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(eq("encoded-token"), eq(RefreshToken.class))).thenReturn(null);

        assertThrows(OAuthErrorException.class, () -> tokenManager.toRefreshToken(session, "encoded-token"));
    }
}
