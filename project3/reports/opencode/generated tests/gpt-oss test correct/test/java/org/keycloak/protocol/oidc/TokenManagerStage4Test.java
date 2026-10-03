package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.models.*;
import org.keycloak.representations.RefreshToken;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage4Test {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock RefreshToken oldToken;
    @Mock UserModel user;
    @Mock ClientModel client;
    @Mock UserSessionModel userSession;

    TokenManager tokenManager = new TokenManager();

    @Test
    void validateTokenReuse_staleToken() {
        // A more recent refresh token was already used on this tab
        when(clientSession.getRefreshToken(anyString())).thenReturn("newer-id-456");
        when(clientSession.getRefreshTokenLastRefresh(anyString())).thenReturn(2000);
        when(oldToken.getId()).thenReturn("old-id-123");
        when(oldToken.getIat()).thenReturn(1000L);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateTokenReuse(session, realm, oldToken, clientSession, true);
        });
        assertEquals("Stale token", ex.getMessage());
    }

    @Test
    void validateTokenReuse_newTokenRefreshFlagTrue() throws OAuthErrorException {
        when(clientSession.getRefreshToken(anyString())).thenReturn("old-id");
        when(oldToken.getId()).thenReturn("new-id");
        when(oldToken.getIat()).thenReturn(3000L);
        // no stale situation
        when(clientSession.getRefreshTokenLastRefresh(anyString())).thenReturn(0);
        tokenManager.validateTokenReuse(session, realm, oldToken, clientSession, true);
        verify(clientSession).setRefreshToken(anyString(), eq("new-id"));
        verify(clientSession).setRefreshTokenUseCount(anyString(), eq(0));
    }

    @Test
    void validateTokenReuse_newTokenRefreshFlagFalse() throws OAuthErrorException {
        when(clientSession.getRefreshToken(anyString())).thenReturn("old-id");
        when(oldToken.getId()).thenReturn("new-id");
        tokenManager.validateTokenReuse(session, realm, oldToken, clientSession, false);
        // no changes should be made
        verify(clientSession, never()).setRefreshToken(anyString(), anyString());
        verify(clientSession, never()).setRefreshTokenUseCount(anyString(), anyInt());
    }

    @Test
    void validateTokenReuse_reuseLimitExceeded() {
        when(clientSession.getRefreshToken(anyString())).thenReturn("id");
        when(oldToken.getId()).thenReturn("id");
        when(oldToken.getIat()).thenReturn(1000L);
        when(clientSession.getRefreshTokenUseCount(anyString())).thenReturn(5);
        when(realm.getRefreshTokenMaxReuse()).thenReturn(3);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            tokenManager.validateTokenReuse(session, realm, oldToken, clientSession, true);
        });
        assertTrue(ex.getMessage().contains("Maximum allowed refresh token reuse exceeded"));
    }
}
