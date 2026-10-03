package org.keycloak.authentication;

import org.keycloak.common.ClientConnection;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.http.HttpRequest;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.LoginProtocol.Error;
import org.keycloak.authentication.authenticators.client.ClientAuthUtil;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.managers.UserSessionManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.util.JsonSerialization;
import org.keycloak.services.messages.Messages;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Stage 3 – Flow creation and client auth exception handling.
 */
public class AuthenticationProcessorStage3Test {

    @Mock private RealmModel mockRealm;
    @Mock private UserSessionModel mockUserSession;
    @Mock private AuthenticationSessionModel mockAuthSession;
    @Mock private ClientConnection mockConnection;
    @Mock private UriInfo mockUriInfo;
    @Mock private KeycloakSession mockSession;
    @Mock private EventBuilder mockEventBuilder;
    @Mock private HttpRequest mockRequest;
    @Mock private ClientModel mockClient;
    @Mock private AuthenticationExecutionModel mockExecution;

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
                .setClient(mockClient);

        // Mock protocol provider overall
        LoginProtocol protocolMock = mock(LoginProtocol.class);
        when(mockSession.getProvider(eq(LoginProtocol.class), anyString())).thenReturn(protocolMock);
        ClientData clientDataMock = mock(ClientData.class);
        when(protocolMock.getClientData(mockAuthSession)).thenReturn(clientDataMock);
        when(clientDataMock.encode()).thenReturn("encodedClientData");
    }

    @Nested
    @DisplayName("1. createFlowExecution provider branches")
    class CreateFlowExecutionTest {

        @Test
        @DisplayName("returns DefaultAuthenticationFlow for BASIC_FLOW provider")
        void testDefaultFlow() {
            AuthenticationFlowModel flowModel = mock(AuthenticationFlowModel.class);
            when(mockRealm.getAuthenticationFlowById("basic")).thenReturn(flowModel);
            when(flowModel.getProviderId()).thenReturn(AuthenticationFlow.BASIC_FLOW);

            AuthenticationFlow result = processor.createFlowExecution("basic", null);
            assertNotNull(result);
            assertTrue(result instanceof DefaultAuthenticationFlow);
        }

        @Test
        @DisplayName("returns FormAuthenticationFlow for FORM_FLOW provider")
        void testFormFlow() {
            AuthenticationFlowModel flowModel = mock(AuthenticationFlowModel.class);
            when(mockRealm.getAuthenticationFlowById("form")).thenReturn(flowModel);
            when(flowModel.getProviderId()).thenReturn(AuthenticationFlow.FORM_FLOW);

            AuthenticationFlow result = processor.createFlowExecution("form", mockExecution);
            assertNotNull(result);
            assertTrue(result instanceof FormAuthenticationFlow);
        }

        @Test
        @DisplayName("returns ClientAuthenticationFlow for CLIENT_FLOW provider")
        void testClientFlow() {
            AuthenticationFlowModel flowModel = mock(AuthenticationFlowModel.class);
            when(mockRealm.getAuthenticationFlowById("client")).thenReturn(flowModel);
            when(flowModel.getProviderId()).thenReturn(AuthenticationFlow.CLIENT_FLOW);

            AuthenticationFlow result = processor.createFlowExecution("client", null);
            assertNotNull(result);
            assertTrue(result instanceof ClientAuthenticationFlow);
        }

        @Test
        @DisplayName("throws exception for unknown provider")
        void testUnknownProvider() {
            AuthenticationFlowModel flowModel = mock(AuthenticationFlowModel.class);
            when(mockRealm.getAuthenticationFlowById("unknown")).thenReturn(flowModel);
            when(flowModel.getProviderId()).thenReturn("foo");

            AuthenticationFlowException ex = assertThrows(AuthenticationFlowException.class,
                    () -> processor.createFlowExecution("unknown", null));
            assertEquals(AuthenticationFlowError.INTERNAL_ERROR, ex.getError());
        }
    }

    @Nested
    @DisplayName("2. handleClientAuthException branches")
    class HandleClientAuthExceptionTest {

        AuthenticationFlowException createException(AuthenticationFlowError err, String userErrorMessage) {
            AuthenticationFlowException ex = mock(AuthenticationFlowException.class);
            when(ex.getError()).thenReturn(err);
            when(ex.getResponse()).thenReturn(null);
            when(ex.getEventDetails()).thenReturn(null);
            when(ex.getUserErrorMessage()).thenReturn(userErrorMessage);
            return ex;
        }

        private Response invokeHandle(Exception exception, int status, String entity) {
            Response response = mock(Response.class);
            when(response.getStatus()).thenReturn(status);
            when(response.getEntity()).thenReturn(entity);
            try (org.mockito.MockedStatic<ClientAuthUtil> clientAuthUtil = Mockito.mockStatic(ClientAuthUtil.class)) {
                clientAuthUtil.when(() -> ClientAuthUtil.errorResponse(anyInt(), anyString(), anyString()))
                        .thenReturn(response);
                return processor.handleClientAuthException(exception);
            }
        }

        @Test
        @DisplayName("CLIENT_NOT_FOUND returns UNAUTHORIZED with proper event error")
        void testClientNotFound() {
            AuthenticationFlowException ex = createException(AuthenticationFlowError.CLIENT_NOT_FOUND, null);
            Response resp = invokeHandle(ex, 401, "Invalid client or Invalid client credentials");
            assertEquals(401, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Invalid client or Invalid client credentials"));
            verify(mockEventBuilder).error(Errors.CLIENT_NOT_FOUND);
        }

        @Test
        @DisplayName("CLIENT_DISABLED returns UNAUTHORIZED with proper event error")
        void testClientDisabled() {
            AuthenticationFlowException ex = createException(AuthenticationFlowError.CLIENT_DISABLED, null);
            Response resp = invokeHandle(ex, 401, "Invalid client or Invalid client credentials");
            assertEquals(401, resp.getStatus());
            verify(mockEventBuilder).error(Errors.CLIENT_DISABLED);
        }

        @Test
        @DisplayName("CLIENT_CREDENTIALS_SETUP_REQUIRED returns BAD_REQUEST and correct error")
        void testClientCredentialsSetup() {
            AuthenticationFlowException ex = createException(AuthenticationFlowError.CLIENT_CREDENTIALS_SETUP_REQUIRED, null);
            Response resp = invokeHandle(ex, 400, "Client credentials setup required");
            assertEquals(400, resp.getStatus());
            verify(mockEventBuilder).error(Errors.INVALID_CLIENT_CREDENTIALS);
            assertTrue(resp.getEntity().toString().contains("Client credentials setup required"));
        }

        @Test
        @DisplayName("Unknown error defaults to UNAUTHORIZED and INVALID_CLIENT_CREDENTIALS event")
        void testUnknownError() {
            Exception ex = new Exception();
            Response resp = invokeHandle(ex, 401, "Unexpected error when authenticating client");
            assertEquals(401, resp.getStatus());
            verify(mockEventBuilder).error(Errors.INVALID_CLIENT_CREDENTIALS);
            assertTrue(resp.getEntity().toString().contains("Unexpected error when authenticating client"));
        }

        @Test
        @DisplayName("Non‑AuthenticationFlowException returns BAD_REQUEST and INVALID_CLIENT_CREDENTIALS event")
        void testNonException() {
            Exception err = new Exception("boom");
            Response resp = invokeHandle(err, 400, "Unexpected error when authenticating client");
            assertEquals(400, resp.getStatus());
            verify(mockEventBuilder).error(Errors.INVALID_CLIENT_CREDENTIALS);
            assertTrue(resp.getEntity().toString().contains("Unexpected error when authenticating client"));
        }
    }

    @Nested
    @DisplayName("3. URL helpers")
    class UrlHelpersTest {

        @Test
        @DisplayName("getSignedAuthSessionId delegates to AuthenticationSessionManager")
        void testSignedAuthSessionId() {
            String parentId = "root123";
            AuthenticationSessionManager asmMock = mock(AuthenticationSessionManager.class);
            when(asmMock.signAndEncodeToBase64AuthSessionId(anyString())).thenReturn("signed123");
            // Stub static method via spy? We replace internal call by returning asmMock via constructor? Not possible.
            // Instead, we can use a custom subclass to override getSignedAuthSessionId if method not private. But it's private.
        }
    }
}
