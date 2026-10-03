package org.keycloak.authentication;

import org.keycloak.events.Error;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.LoginProtocol.Error as ProtocolError;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.models.*;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Stage 4 – Browser exception handling & resetFlow behaviour.
 */
public class AuthenticationProcessorStage4DetailedTest {

    @Mock private RealmModel mockRealm;
    @Mock private UserSessionModel mockUserSession;
    @Mock private AuthenticationSessionModel mockAuthSession;
    @Mock private ClientConnection mockConnection;
    @Mock private UriInfo mockUriInfo;
    @Mock private KeycloakSession mockSession;
    @Mock private EventBuilder mockEventBuilder;
    @Mock private HttpRequest mockRequest;
    @Mock private HttpHeaders mockHeaders;
    @Mock private ClientModel mockClient;

    private AuthenticationProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processor = new AuthenticationProcessor();
        processor.setRealm(mockRealm)
                .setAuthenticationSession(mockAuthSession)
                .setConnection(mockConnection)
                .setUriInfo(mockUriInfo)
                .setSession(mockSession)
                .setEventBuilder(mockEventBuilder)
                .setRequest(mockRequest)
                .setClient(mockClient)
                .setFlowPath("/flow");

        // Setup protocol provider mock
        LoginProtocol protocolMock = mock(LoginProtocol.class);
        when(mockSession.getProvider(LoginProtocol.class, anyString())).thenReturn(protocolMock);
        when(protocolMock.sendError(any(), any(), any())).thenReturn(Response.status(Response.Status.BAD_REQUEST).build());
    }

    @Nested
    @DisplayName("1. handleBrowserExceptionList handling")
    class HandleBrowserExceptionListTest {

        @Test
        @DisplayName("delegates errors from list to event and form errors")
        void testListDelegation() {
            AuthenticationFlowException child = new AuthenticationFlowException(AuthenticationFlowError.USER_DISABLED);
            List<AuthenticationFlowException> list = new ArrayList<>();
            list.add(child);
            AuthenticationFlowException parent = new AuthenticationFlowException(list);

            // Mock form provider
            LoginFormsProvider formsMock = mock(LoginFormsProvider.class);
            when(mockSession.getProvider(LoginFormsProvider.class)).thenReturn(formsMock);
            when(formsMock.setAuthenticationSession(mockAuthSession)).thenReturn(formsMock);
            when(formsMock.createErrorPage(Response.Status.BAD_REQUEST.getStatusCode())).thenReturn(Response.status(Response.Status.BAD_REQUEST).build());

            Response resp = processor.handleBrowserExceptionList(parent);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
            verify(mockEventBuilder).error(Errors.USER_DISABLED);
        }
    }

    @Nested
    @DisplayName("2. handleBrowserException branches")
    class HandleBrowserExceptionBranchTest {

        private Response invokeHandle(AuthenticationFlowException ex) {
            return processor.handleBrowserException(ex);
        }

        @Test
        @DisplayName("INVALID_USER maps to USER_NOT_FOUND")
        void testInvalidUser() {
            AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.INVALID_USER);
            Response resp = invokeHandle(e);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Invalid user"));
            verify(mockEventBuilder).error(Errors.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("USER_DISABLED maps to USER_DISABLED")
        void testUserDisabled() {
            AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.USER_DISABLED);
            Response resp = invokeHandle(e);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Account disabled"));
            verify(mockEventBuilder).error(Errors.USER_DISABLED);
        }

        @Test
        @DisplayName("EXPIRED_CODE maps to EXPIRED_CODE")
        void testExpiredCode() {
            AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.EXPIRED_CODE);
            Response resp = invokeHandle(e);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Expired code"));
            verify(mockEventBuilder).error(Errors.EXPIRED_CODE);
        }

        @Test
        @DisplayName("INVALID_CLIENT_SESSION maps to INVALID_CODE")
        void testInvalidClientSession() {
            AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.INVALID_CLIENT_SESSION);
            Response resp = invokeHandle(e);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Invalid code"));
            verify(mockEventBuilder).error(Errors.INVALID_CODE);
        }

        @Test
        @DisplayName("DEFAULT maps to INVALID_USER_CREDENTIALS")
        void testDefaultError() {
            AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.UNKNOWN_USER);
            Response resp = invokeHandle(e);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Invalid user"));
            verify(mockEventBuilder).error(Errors.INVALID_USER_CREDENTIALS);
        }
    }

    @Nested
    @DisplayName("3. resetFlow behaviour")
    class ResetFlowTest {

        @Test
        @DisplayName("resetFlow clears session state and runs listener")
        void testResetFlowWithListener() {
            Runnable mockListener = mock(Runnable.class);
            // Set protected field via reflection
            java.lang.reflect.Field afterField;
            try {
                afterField = AuthenticationProcessor.class.getDeclaredField("afterResetListener");
                afterField.setAccessible(true);
                afterField.set(processor, mockListener);
            } catch (Exception ignored) {}

            processor.setFlowPath("/some/path");
            processor.resetFlow();

            // Verify methods called on session
            verify(mockAuthSession).clearExecutionStatus();
            verify(mockAuthSession).clearUserSessionNotes();
            verify(mockAuthSession).clearAuthNotes();
            verify(mockAuthSession).clearRequiredActions();
            verify(mockAuthSession).setAction(CommonClientSessionModel.Action.AUTHENTICATE.name());
            verify(mockAuthSession).setAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH, "/some/path");
            verify(mockListener).run();
        }

        @Test
        @DisplayName("resetFlow without listener still updates auth session")
        void testResetFlowNoListener() {
            processor.setFlowPath("/other/path");
            processor.resetFlow();
            verify(mockAuthSession).setAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH, "/other/path");
        }
    }
}
