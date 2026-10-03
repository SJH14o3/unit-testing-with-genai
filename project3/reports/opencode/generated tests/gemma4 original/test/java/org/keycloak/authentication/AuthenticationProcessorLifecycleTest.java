package org.keycloak.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.authenticators.util.AbstractUsernameFormAuthenticator;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.models.utils.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.managers.UserSessionManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.CommonClientSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.services.messages.Messages;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.MultivaluedHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationProcessorLifecycleTest {

    @Mock
    private KeycloakSession session;
    @Mock
    private RealmModel realm;
    @Mock
    private AuthenticationSessionModel authSession;
    @Mock
    private RootAuthenticationSessionModel rootSession;
    @Mock
    private ClientModel client;
    @Mock
    private UserSessionModel userSession;
    @Mock
    private UserModel user;
    @Mock
    private ClientConnection connection;
    @Mock
    private EventBuilder event;

    private AuthenticationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AuthenticationProcessor();
    }

    @Test
    void testClone() {
        when(authSession.getParentSession()).thenReturn(rootSession);
        when(rootSession.createAuthenticationSession(any())).thenReturn(authSession); // Mocking same object for simplicity in setup, though usually it's a new one
        when(authSession.getRedirectUri()).thenReturn("http://redirect.com");
        when(authSession.getProtocol()).thenReturn("openid-connect");
        when(authSession.getClientNotes()).thenReturn(Collections.singletonMap("client-note", "value"));
        when(authSession.getTabId()).thenReturn("tab-123");
        when(authSession.getAuthNote(AuthenticationProcessor.FORKED_FROM)).thenReturn("parent-tab-id");
        when(authSession.getClient()).thenReturn(client);

        // We need to mock the clone return value to be a different instance to verify notes copy
        AuthenticationSessionModel clonedSession = mock(AuthenticationSessionModel.class);
        when(rootSession.createAuthenticationSession(any())).thenReturn(clonedSession);
        when(authSession.getClient()).thenReturn(client);

        AuthenticationSessionModel result = AuthenticationProcessor.clone(session, authSession);

        assertNotNull(result);
        verify(clonedSession).setRedirectUri("http://redirect.com");
        verify(clonedSession).setProtocol("openid-connect");
        verify(clonedSession).setClientNote("client-note", "value");
        verify(clonedSession).setAuthNote(AuthenticationProcessor.FORKED_FROM, "tab-123");
    }

    @Test
    void testRecreate() {
        try (MockedStatic<AuthenticationSessionManager> managerMock = mockStatic(AuthenticationSessionManager.class)) {
            AuthenticationSessionManager manager = mock(AuthenticationSessionManager.class);
            managerMock.when(() -> new AuthenticationSessionManager(session)).thenReturn(manager);
            
            RootAuthenticationSessionModel rootSessionCreated = mock(RootAuthenticationSessionModel.class);
            AuthenticationSessionModel newAuthSession = mock(AuthenticationSessionModel.class);
            
            when(authSession.getRealm()).thenReturn(realm);
            when(manager.createAuthenticationSession(realm, true)).thenReturn(rootSessionCreated);
            when(rootSessionCreated.createAuthenticationSession(client)).thenReturn(newAuthSession);
            when(authSession.getClient()).thenReturn(client);
            when(authSession.getRedirectUri()).thenReturn("http://redirect.com");
            when(authSession.getProtocol()).thenReturn("oidc");
            when(authSession.getClientNotes()).thenReturn(Collections.singletonMap("key", "val"));

            AuthenticationSessionModel result = AuthenticationProcessor.recreate(session, authSession);

            assertNotNull(result);
            verify(newAuthSession).setRedirectUri("http://redirect.com");
            verify(newAuthSession).setProtocol("oidc");
            verify(newAuthSession).setClientNote("key", "val");
            verify(manager).removeAuthenticationSession(realm, authSession, true);
        }
    }

    @Test
    void testAttachSession_WithAttemptedUsername() {
        when(authSession.getAuthenticatedUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("real-user");
        when(authSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME)).thenReturn("attempted-user");
        when(authSession.getProtocol()).thenReturn("oidc");
        when(authSession.getRedirectUri()).thenReturn("http://redirect.com");
        when(authSession.getClientNotes()).thenReturn(new HashMap<>());
        
        // Mock UserSessionManager to create a session
        try (MockedStatic<UserSessionManager> userSessionManagerMock = mockStatic(UserSessionManager.class)) {
            UserSessionManager usm = mock(UserSessionManager.class);
            userSessionManagerMock.when(() -> new UserSessionManager(session)).thenReturn(usm);
            
            UserSessionModel newSession = mock(UserSessionModel.class);
            when(newSession.getUser()).thenReturn(user);
            when(usm.createUserSession(anyString(), eq(realm), eq(user), eq("attempted-user"), anyString(), eq("oidc"), any(), any(), any())).thenReturn(newSession);
            
            when(authSession.getParentSession()).thenReturn(rootSession);
            when(rootSession.getId()).thenReturn("root-id");
            when(authSession.getClientNotes()).thenReturn(Collections.emptyMap());

            ClientSessionContext ctx = AuthenticationProcessor.attachSession(authSession, null, session, realm, connection, event);

            assertNotNull(ctx);
            verify(usm).createUserSession(eq("root-id"), eq(realm), eq(user), eq("attempted-user"), anyString(), eq("oidc"), any(), any(), any());
        }
    }

    @Test
    void testAttachSession_ExistingValidSession() {
        when(authSession.getAuthenticatedUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("user");
        when(authSession.getProtocol()).thenReturn("oidc");
        when(authSession.getClientNotes()).thenReturn(new HashMap<>());
        when(authSession.getParentSession()).thenReturn(rootSession);
        when(rootSession.getId()).thenReturn("root-id");

        // Mock session lookup
        try (MockedStatic<UserSessionManager> userSessionManagerMock = mockStatic(UserSessionManager.class)) {
            // We don't actually call the manager here, just mock the behavior of session.sessions().getUserSession()
            // Since session.sessions() returns a session factory/manager, we need more mocks.
            // For brevity in this plan, we'll verify that if userSession is provided, it is used.
            
            when(userSession.getUser()).thenReturn(user);
            when(AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);

            ClientSessionContext ctx = AuthenticationProcessor.attachSession(authSession, userSession, session, realm, connection, event);

            assertNotNull(ctx);
            verify(userSession, never()).restartSession(any(), any(), any(), any(), any(), any(), any());
        }
    }
}
