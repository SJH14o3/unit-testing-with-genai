import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.representations.AccessToken;
import org.keycloak.common.VerificationException;
import org.keycloak.models.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerTestStage2 {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UserModel user;
    @Mock AccessToken token;

    TokenManager tokenManager = new TokenManager();

    @Test
    void isUserValid_nullUser() {
        assertFalse(tokenManager.isUserValid(session, realm, token, null));
    }

    @Test
    void isUserValid_disabledUser() {
        when(user.isEnabled()).thenReturn(false);
        assertFalse(tokenManager.isUserValid(session, realm, token, user));
    }

    @Test
    void isUserValid_validUser() {
        when(user.isEnabled()).thenReturn(true);
        // Stub validateUserNotBefore to return true
        try (MockedStatic<TokenManager> tmStatic = mockStatic(TokenManager.class)) {
            tmStatic.when(() -> TokenManager.validateUserNotBefore(session, realm, token, user)).thenReturn(true);
            assertTrue(tokenManager.isUserValid(session, realm, token, user));
        }
    }

    @Test
    void validateUserNotBefore_validToken() {
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            TokenVerifier<AccessToken> tvMock = mock(TokenVerifier.class);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(token)).thenReturn(tvMock);
            when(tvMock.withChecks(any())).thenReturn(tvMock);
            when(tvMock.verify()).thenReturn(null); // succeed
            assertTrue(tokenManager.validateUserNotBefore(session, realm, token, user));
        }
    }

    @Test
    void validateUserNotBefore_notBeforeFails() {
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            TokenVerifier<AccessToken> tvMock = mock(TokenVerifier.class);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(token)).thenReturn(tvMock);
            when(tvMock.withChecks(any())).thenReturn(tvMock);
            doThrow(new VerificationException("Not before")).when(tvMock).verify();
            assertFalse(tokenManager.validateUserNotBefore(session, realm, token, user));
        }
    }
}
