package org.keycloak.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.http.HttpRequest;
import org.keycloak.events.EventBuilder;
import org.keycloak.services.messages.Messages;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.authentication.authenticators.client.ClientAuthUtil;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.services.ErrorPage;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationProcessorCoreTest {

    private AuthenticationProcessor processor;

    @Mock
    private KeycloakSession session;
    @Mock
    private RealmModel realm;
    @Mock
    private AuthenticationSessionModel authenticationSession;
    @Mock
    private ClientModel client;
    @Mock
    private UriInfo uriInfo;
    @Mock
    private HttpRequest request;
    @Mock
    private EventBuilder event;
    @Mock
    private UserModel user;
    @Mock
    private AuthenticationExecutionModel execution;
    @Mock
    private AuthenticationFlowModel flow;
    @Mock
    private AuthenticationFlow authenticationFlow;
    @Mock
    private Response response;

    @BeforeEach
    void setUp() {
        processor = new AuthenticationProcessor();
        processor.setSession(session)
                .setRealm(realm)
                .setAuthenticationSession(authenticationSession)
                .setUriInfo(uriInfo)
                .setRequest(request)
                .setEventBuilder(event);
        processor.setClient(client);
    }

    @Test
    void testCreateFlowExecution_Basic() {
        when(realm.getAuthenticationFlowById("flow-id")).thenReturn(flow);
        when(flow.getProviderId()).thenReturn(AuthenticationFlow.BASIC_FLOW);

        AuthenticationFlow result = processor.createFlowExecution("flow-id", execution);

        assertNotNull(result);
    }

    @Test
    void testCreateFlowExecution_Form() {
        when(realm.getAuthenticationFlowById("flow-id")).thenReturn(flow);
        when(flow.getProviderId()).thenReturn(AuthenticationFlow.FORM_FLOW);

        AuthenticationFlow result = processor.createFlowExecution("flow-id", execution);

        assertNotNull(result);
    }

    @Test
    void testCreateFlowExecution_Client() {
        when(realm.getAuthenticationFlowById("flow-id")).thenReturn(flow);
        when(flow.getProviderId()).thenReturn(AuthenticationFlow.CLIENT_FLOW);

        AuthenticationFlow result = processor.createFlowExecution("flow-id", execution);

        assertNotNull(result);
    }

    @Test
    void testCreateFlowExecution_Unknown() {
        when(realm.getAuthenticationFlowById("flow-id")).thenReturn(flow);
        when(flow.getProviderId()).thenReturn("unknown-provider");

        assertThrows(AuthenticationFlowException.class, () -> {
            processor.createFlowExecution("flow-id", execution);
        });
    }

    @Test
    void testCreateFlowExecution_NullFlow() {
        when(realm.getAuthenticationFlowById("flow-id")).thenReturn(null);

        assertThrows(AuthenticationFlowException.class, () -> {
            processor.createFlowExecution("flow-id", execution);
        });
    }

    @Test
    void testAuthenticate_ReturnsChallenge() throws AuthenticationFlowException {
        AuthenticationProcessor spyProcessor = spy(processor);
        doReturn(response).when(spyProcessor).authenticateOnly();

        Response result = spyProcessor.authenticate();

        assertEquals(response, result);
    }

    @Test
    void testAuthenticateClient_Success() throws AuthenticationFlowException {
        // Since we can't easily mock createFlowExecution when it's a real method call in the same object,
        // we'll use a spy to bypass the heavy lifting of createFlowExecution.
        AuthenticationProcessor spyProcessor = spy(processor);
        AuthenticationFlow mockFlow = mock(AuthenticationFlow.class);

        doReturn(mockFlow).when(spyProcessor).createFlowExecution(any(), any());
        when(mockFlow.processFlow()).thenReturn(null); // indicate success (no challenge)
        when(mockFlow.isSuccessful()).thenReturn(true);

        Response result = spyProcessor.authenticateClient();

        assertNull(result);
    }

    @Test
    void testAuthenticateClient_ThrowsException() throws AuthenticationFlowException {
        AuthenticationProcessor spyProcessor = spy(processor);
        AuthenticationFlow mockFlow = mock(AuthenticationFlow.class);

        doReturn(mockFlow).when(spyProcessor).createFlowExecution(any(), any());
        when(mockFlow.processFlow()).thenThrow(new RuntimeException("Unexpected error"));

        // The catch block in authenticateClient handles Exception and calls handleClientAuthException.
        // handleClientAuthException calls ClientAuthUtil.errorResponse.
        // Since ClientAuthUtil is a static utility, we can't mock it easily without Mockito-inline.
        // But we can verify that it at least enters the catch block.

        // For now, we just assert it doesn't crash the test.
        try (MockedStatic<ClientAuthUtil> clientAuthUtilMock = mockStatic(ClientAuthUtil.class)) {
            clientAuthUtilMock.when(() -> ClientAuthUtil.errorResponse(anyInt(), anyString(), anyString())).thenReturn(response);
            assertEquals(response, spyProcessor.authenticateClient());
        }
    }

    @Test
    void testIsSuccessful() {
        String executionId = "exec-id";
        Map<String, AuthenticationSessionModel.ExecutionStatus> statuses = new HashMap<>();
        statuses.put(executionId, AuthenticationSessionModel.ExecutionStatus.SUCCESS);

        when(authenticationSession.getExecutionStatus()).thenReturn(statuses);
        when(execution.getId()).thenReturn(executionId);

        assertTrue(processor.isSuccessful(execution));

        statuses.put(executionId, AuthenticationSessionModel.ExecutionStatus.FAILED);
        assertFalse(processor.isSuccessful(execution));

        statuses.remove(executionId);
        assertFalse(processor.isSuccessful(execution));
    }

    @Test
    void testResetFlow_Static() {
        when(authenticationSession.getParentSession()).thenReturn(mock(RootAuthenticationSessionModel.class));
        when(authenticationSession.getRequiredActions()).thenReturn(Collections.emptySet());

        AuthenticationProcessor.resetFlow(authenticationSession, "/test/path");

        verify(authenticationSession).setAuthenticatedUser(null);
        verify(authenticationSession).clearExecutionStatus();
        verify(authenticationSession).setAuthNote(AuthenticationProcessor.CURRENT_FLOW_PATH, "/test/path");
    }

    @Test
    void testResetFlow_Instance() {
        when(authenticationSession.getParentSession()).thenReturn(mock(RootAuthenticationSessionModel.class));
        when(authenticationSession.getRequiredActions()).thenReturn(Collections.emptySet());

        processor.resetFlow();

        verify(authenticationSession).setAuthenticatedUser(null);
        verify(authenticationSession).clearExecutionStatus();
    }
}
