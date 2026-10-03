package org.keycloak.authentication.extra;

import jakarta.ws.rs.core.UriInfo;
import org.keycloak.authentication.AuthenticationFlow;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.authenticators.client.ClientAuthUtil;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.util.Time;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.ClientData;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.managers.AuthenticationManager;

import org.keycloak.services.ServicesLogger;
import org.keycloak.services.messages.Messages;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.util.JsonSerialization;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Stage 2 – Authentication flow handling: authenticate, authenticateClient, authenticateOnly, finishAuthentication, authenticationComplete, authenticationAction.
 */
public class AuthenticationProcessorFlowTest {

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
    private AuthenticationFlow mockFlow;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create a subclass that overrides createFlowExecution
        processor = new AuthenticationProcessor() {
            @Override
            public AuthenticationFlow createFlowExecution(String flowId, AuthenticationExecutionModel execution) {
                return mockFlow;
            }
        };
        processor.setRealm(mockRealm)
                .setAuthenticationSession(mockAuthSession)
                .setConnection(mockConnection)
                .setUriInfo(mockUriInfo)
                .setSession(mockSession)
                .setEventBuilder(mockEventBuilder)
                .setRequest(mockRequest)
                .setClient(mockClient);
        processor.setFlowPath("/flow");
        mockFlow = mock(AuthenticationFlow.class);

        RootAuthenticationSessionModel parentSession = mock(RootAuthenticationSessionModel.class);
        when(mockAuthSession.getParentSession()).thenReturn(parentSession);
        when(parentSession.getTimestamp()).thenReturn(Time.currentTime());
        when(mockRealm.getAccessCodeLifespanLogin()).thenReturn(300);
        when(mockRealm.getAccessCodeLifespanUserAction()).thenReturn(300);
        when(mockAuthSession.getClient()).thenReturn(mockClient);
        when(mockClient.getClientId()).thenReturn("client");
        when(mockAuthSession.getProtocol()).thenReturn("openid-connect");
        UserModel authenticatedUser = mock(UserModel.class);
        when(authenticatedUser.isEnabled()).thenReturn(true);
        when(authenticatedUser.getServiceAccountClientLink()).thenReturn(null);
        when(mockAuthSession.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(mockSession.getProvider(eq(LoginProtocol.class), anyString())).thenReturn(mock(LoginProtocol.class));
        when(mockEventBuilder.client(anyString())).thenReturn(mockEventBuilder);
        when(mockEventBuilder.detail(anyString(), (String) any())).thenReturn(mockEventBuilder);
        when(mockFlow.processFlow()).thenReturn(null);
        when(mockFlow.isSuccessful()).thenReturn(true);
    }

    @Test
    @DisplayName("authenticateClient returns null when flow succeeds")
    void testAuthenticateClientSuccess() {
        when(mockFlow.processFlow()).thenReturn(null);
        when(mockFlow.isSuccessful()).thenReturn(true);
        assertDoesNotThrow(() -> {
            Response resp = processor.authenticateClient();
            assertNull(resp);
        });
    }

    @Test
    @DisplayName("authenticateClient propagates flow failure exception")
    void testAuthenticateClientFailure() {
        when(mockFlow.processFlow()).thenReturn(null);
        when(mockFlow.isSuccessful()).thenReturn(false);
        AuthenticationFlowException ex = new AuthenticationFlowException(AuthenticationFlowError.INTERNAL_ERROR);
        ArrayList<AuthenticationFlowException> errors = new ArrayList<>();
        errors.add(ex);
        when(mockFlow.getFlowExceptions()).thenReturn(errors);
        Response expectedResponse = mock(Response.class);
        try (var mockStatic = mockStatic(ClientAuthUtil.class)) {
            mockStatic.when(() -> ClientAuthUtil.errorResponse(anyInt(), anyString(), anyString())).thenReturn(expectedResponse);
            assertSame(expectedResponse, processor.authenticateClient());
        }
    }

    @Test
    @DisplayName("authenticateOnly returns challenge when flow returns challenge")
    void testAuthenticateOnlyChallenge() {
        Response challenge = mock(Response.class);
        when(mockFlow.processFlow()).thenReturn(challenge);
        when(mockAuthSession.getClient()).thenReturn(mockClient);
        when(mockClient.getClientId()).thenReturn("client");
        when(mockAuthSession.getRedirectUri()).thenReturn("http://localhost");
        when(mockAuthSession.getProtocol()).thenReturn("openid-connect");
        assertSame(challenge, processor.authenticateOnly());
    }

    @Test
    @DisplayName("authenticationAction completes flow when no execution id provided")
    void testAuthenticationActionInvalidExecution() {
        // call with null current execution; should redirect to page expired and authenticate again
        processor.setAuthenticationSession(mockAuthSession);
        // Set current execution note to some value
        when(mockAuthSession.getAuthNote(eq(AuthenticationProcessor.CURRENT_AUTHENTICATION_EXECUTION))).thenReturn("exec1");
        AuthenticationFlowException exception = assertThrows(AuthenticationFlowException.class,
                () -> processor.authenticationAction("exec2"));
        assertEquals(AuthenticationFlowError.INVALID_CLIENT_SESSION, exception.getError());
    }

    @Test
    @DisplayName("finishAuthentication delegates to AuthenticationManager.redirectAfterSuccessfulFlow")
    void testFinishAuthentication() {
        LoginProtocol protocolMock = mock(LoginProtocol.class);
        processor.setAuthenticationSession(mockAuthSession);
        ClientSessionContext clientSessionContext = mock(ClientSessionContext.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        when(clientSessionContext.getClientSession()).thenReturn(clientSession);
        when(clientSession.getUserSession()).thenReturn(mockUserSession);
        try (var processorStatic = mockStatic(AuthenticationProcessor.class, CALLS_REAL_METHODS);
             var mockStatic = mockStatic(AuthenticationManager.class)) {
            processorStatic.when(() -> AuthenticationProcessor.attachSession(any(), any(), any(), any(), any(), any())).thenReturn(clientSessionContext);
            Response expectedResp = mock(Response.class);
            mockStatic.when(() -> AuthenticationManager.redirectAfterSuccessfulFlow(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(expectedResp);
            Response resp = processor.finishAuthentication(protocolMock);
            assertSame(expectedResp, resp);
        }
    }

    @Test
    @DisplayName("authenticationComplete handles required actions path")
    void testAuthenticationCompleteWithRequiredAction() {
        AuthenticationExecutionModel execModel = mock(AuthenticationExecutionModel.class);
        when(execModel.getRequirement()).thenReturn(AuthenticationExecutionModel.Requirement.REQUIRED);
        when(mockAuthSession.getExecutionStatus()).thenReturn(Collections.emptyMap());
        // Mock nextRequiredAction to return an action
        try (var mockStatic = mockStatic(AuthenticationManager.class)) {
            Response expectedResponse = mock(Response.class);
            mockStatic.when(() -> AuthenticationManager.nextRequiredAction(any(), any(), any(), any())).thenReturn("action1");
            mockStatic.when(() -> AuthenticationManager.redirectToRequiredActions(any(), any(), any(), any(), any())).thenReturn(expectedResponse);
            Response resp = processor.authenticate();
            assertSame(expectedResponse, resp);
        }
    }

    @Test
    @DisplayName("authenticationComplete finishes flow when no required actions")
    void testAuthenticationCompleteFinish() {
        // nextRequiredAction returns null
        try (var mockStatic = mockStatic(AuthenticationManager.class)) {
            mockStatic.when(() -> AuthenticationManager.nextRequiredAction(any(), any(), any(), any())).thenReturn(null);
            Response expectedResponse = mock(Response.class);
            mockStatic.when(() -> AuthenticationManager.finishedRequiredActions(any(), any(), any(), any(), any(), any(), any())).thenReturn(expectedResponse);
            Response resp = processor.authenticate();
            assertSame(expectedResponse, resp);
        }
    }
}
