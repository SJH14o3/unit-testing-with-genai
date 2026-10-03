package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.protocol.oidc.TokenManager;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage3Test {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UserProvider userProvider;
    @Mock AccessToken token;
    @Mock UserModel userById;
    @Mock UserModel userByUsername;

    TokenManager tokenManager = new TokenManager();

    @BeforeEach
    void setUp() {
        when(session.users()).thenReturn(userProvider);
    }

    @Test
    void lookupUserFromStatelessToken_bySub() {
        when(token.getSubject()).thenReturn("user-123");
        when(token.getPreferredUsername()).thenReturn(null);
        when(userProvider.getUserById(realm, "user-123")).thenReturn(userById);
        UserModel result = tokenManager.lookupUserFromStatelessToken(session, realm, token);
        assertEquals(userById, result);
    }

    @Test
    void lookupUserFromStatelessToken_byUsername() {
        when(token.getSubject()).thenReturn(null);
        when(token.getPreferredUsername()).thenReturn("alice");
        when(userProvider.getUserByUsername(realm, "alice")).thenReturn(userByUsername);
        UserModel result = tokenManager.lookupUserFromStatelessToken(session, realm, token);
        assertEquals(userByUsername, result);
    }

    @Test
    void lookupUserFromStatelessToken_none() {
        when(token.getSubject()).thenReturn(null);
        when(token.getPreferredUsername()).thenReturn(null);
        UserModel result = tokenManager.lookupUserFromStatelessToken(session, realm, token);
        assertNull(result);
    }
}
