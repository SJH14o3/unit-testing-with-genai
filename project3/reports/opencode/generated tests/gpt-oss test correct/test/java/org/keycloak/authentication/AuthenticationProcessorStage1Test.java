package org.keycloak.authentication;

import jakarta.ws.rs.core.UriInfo;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.http.HttpRequest;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.LoginProtocol.Error;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ServicesLogger;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.services.managers.BruteForceProtector;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Stage 1 – basic property getters/setters and simple business logic.
 * Uses Mockito to stub heavy dependencies and focuses on decision points in setters and helpers.
 */
public class AuthenticationProcessorStage1Test {

    @Mock private RealmModel mockRealm;
    @Mock private UserSessionModel mockUserSession;
    @Mock private AuthenticationSessionModel mockAuthSession;
    @Mock private ClientConnection mockConnection;
    @Mock private UriInfo mockUriInfo;
    @Mock private KeycloakSession mockSession;
    @Mock private EventBuilder mockEventBuilder;
    @Mock private HttpRequest mockRequest;
    @Mock private ClientModel mockClient;

    private AuthenticationProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processor = new AuthenticationProcessor();
        processor.setRealm(mockRealm);
        processor.setAuthenticationSession(mockAuthSession);
        processor.setConnection(mockConnection);
        processor.setUriInfo(mockUriInfo);
        processor.setSession(mockSession);
        processor.setEventBuilder(mockEventBuilder);
        processor.setRequest(mockRequest);
        processor.setClient(mockClient);
        when(mockEventBuilder.user(any(UserModel.class))).thenReturn(mockEventBuilder);
        when(mockEventBuilder.detail(anyString(), anyString())).thenReturn(mockEventBuilder);
    }

    @Nested
    @DisplayName("1. Property accessors")
    class PropertyAccessors {

        @Test
        @DisplayName("browserFlow getter/setter")
        void testBrowserFlow() {
            assertFalse(processor.isBrowserFlow());
            processor.setBrowserFlow(true);
            assertTrue(processor.isBrowserFlow());
        }

        @Test
        @DisplayName("set/get Realm")
        void testRealm() {
            assertSame(mockRealm, processor.getRealm());
            RealmModel anotherRealm = mock(RealmModel.class);
            processor.setRealm(anotherRealm);
            assertSame(anotherRealm, processor.getRealm());
        }

        @Test
        @DisplayName("set/get Client")
        void testClient() {
            assertSame(mockClient, processor.getClient());
            ClientModel another = mock(ClientModel.class);
            processor.setClient(another);
            assertSame(another, processor.getClient());
        }

        @Test
        @DisplayName("set/get ClientAuthAttributes map identity")
        void testClientAuthAttributes() {
            assertTrue(processor.getClientAuthAttributes().isEmpty());
            processor.getClientAuthAttributes().put("key", "value");
            assertEquals("value", processor.getClientAuthAttributes().get("key"));
        }

        @Test
        @DisplayName("getAuthenticationSession returns set session")
        void testAuthSession() {
            assertSame(mockAuthSession, processor.getAuthenticationSession());
        }

        @Test
        @DisplayName("getConnection returns set connection")
        void testConnection() {
            assertSame(mockConnection, processor.getConnection());
        }

        @Test
        @DisplayName("getUriInfo returns set uriInfo")
        void testUriInfo() {
            assertSame(mockUriInfo, processor.getUriInfo());
        }

        @Test
        @DisplayName("getSession returns set session")
        void testSession() {
            assertSame(mockSession, processor.getSession());
        }

        @Test
        @DisplayName("getEvent returns set eventBuilder")
        void testEvent() {
            assertSame(mockEventBuilder, processor.getEvent());
        }

        @Test
        @DisplayName("getRequest returns set request")
        void testRequest() {
            assertSame(mockRequest, processor.getRequest());
        }

        @Test
        @DisplayName("set flowPath and retrieve it")
        void testFlowPath() {
            processor.setFlowPath("/my/flow");
            assertEquals("/my/flow", processor.getFlowPath());
        }

        @Test
        @DisplayName("forwarded error message store getAndRemove yields null when none set")
        void testGetAndRemoveForwardedErrorMessageEmpty() {
            assertNull(processor.getAndRemoveForwardedErrorMessage());
        }
    }

    @Nested
    @DisplayName("2. BruteForceProtector lazy load")
    class BruteForceProtectorTest {

        @Test
        @DisplayName("getBruteForceProtector loads provider on first call")
        void testGetBruteForceProtector() {
            BruteForceProtector mockProtector = mock(BruteForceProtector.class);
            when(mockSession.getProvider(BruteForceProtector.class)).thenReturn(mockProtector);
            assertSame(mockProtector, processor.getBruteForceProtector());
            // ensure provider called only once
            verify(mockSession, times(1)).getProvider(BruteForceProtector.class);
        }
    }

    @Nested
    @DisplayName("3. User authentication conflict detection")
    class UserConflictTest {

        @Test
        @DisplayName("setAutheticatedUser throws when existing user id differs")
        void testUserConflict() {
            UserModel existing = mock(UserModel.class);
            UserModel newUser = mock(UserModel.class);
            when(existing.getId()).thenReturn("id1");
            when(newUser.getId()).thenReturn("id2");
            when(mockAuthSession.getAuthenticatedUser()).thenReturn(existing);
            AuthenticationFlowException ex = assertThrows(AuthenticationFlowException.class, () -> processor.setAutheticatedUser(newUser));
            assertEquals(AuthenticationFlowError.USER_CONFLICT, ex.getError());
        }

        @Test
        @DisplayName("setAutheticatedUser delegates validation and sets user when no conflict")
        void testSetAuthenticatedUserSuccess() {
            UserModel user = mock(UserModel.class);
            // no existing user in session
            when(mockAuthSession.getAuthenticatedUser()).thenReturn(null);
            // user must be enabled to pass validation
            when(user.isEnabled()).thenReturn(true);
            when(user.getServiceAccountClientLink()).thenReturn(null);
            // Validate method is private; we can't directly verify, but we can check that authenticated user set
            processor.setAutheticatedUser(user);
            verify(mockAuthSession).setAuthenticatedUser(user);
        }
    }

    @Nested
    @DisplayName("4. clearAuthenticatedUser")
    class ClearAuthenticatedUserTest {
        @Test
        void testClearAuthenticatedUser() {
            processor.clearAuthenticatedUser();
            verify(mockAuthSession).setAuthenticatedUser(null);
        }
    }
}
