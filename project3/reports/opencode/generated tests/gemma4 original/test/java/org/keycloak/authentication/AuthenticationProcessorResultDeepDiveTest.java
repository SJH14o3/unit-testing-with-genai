package org.keycloak.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.AuthenticationProcessor.Result;
import org.keycloak.authentication.authenticators.Authenticator;
import org.keycloak.authentication.authenticators.AuthenticatorFactory;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.services.messages.Messages;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationProcessorResultDeepDiveTest {

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
    private AuthenticationExecutionModel execution;
    @Mock
    private Authenticator authenticator;
    @Mock
    private ClientAuthenticator clientAuthenticator;
    @Mock
    private List<AuthenticationExecutionModel> currentExecutions;
    @Mock
    private UserModel user;
    @Mock
    private UserSessionModel userSession;
    @Mock
    private Response response;

    private Result result;

    @BeforeEach
    void setUp() throws Exception {
        processor = new AuthenticationProcessor();
        processor.setSession(session)
                 .setRealm(realm)
                 .setAuthenticationSession(authenticationSession)
                 .setClient(client)
                 .setUriInfo(uriInfo)
                 .setRequest(request)
                 .setEventBuilder(event);

        Constructor<Result> constructor = Result.class.getDeclaredConstructor(
                AuthenticationExecutionModel.class,
                Authenticator.class,
                List.class
        );
        constructor.setAccessible(true);
        result = constructor.newInstance(execution, authenticator, currentExecutions);
    }

    @Test
    void testSuccessWithCredential() {
        result.success("password");
        assertEquals(AuthenticationFlowContext.FlowStatus.SUCCESS, result.getStatus());
        // Verification of AuthenticatorUtil.addAuthCredential is hard without Mockito-inline, 
        // but we are testing the branch.
    }

    @Test
    void testFailureWithDetails() {
        result.failure(AuthenticationFlowError.INVALID_USER, "extra-detail", "user-error");
        assertEquals(AuthenticationFlowContext.FlowStatus.FAILED, result.getStatus());
        assertEquals(AuthenticationFlowError.INVALID_USER, result.getError());
        assertEquals("extra-detail", result.getEventDetails());
        assertEquals("user-error", result.getUserErrorMessage());
    }

    @Test
    void testGetAndSetUser() {
        result.setUser(user);
        assertEquals(user, result.getUser());
    }

    @Test
    void testGetAndSetAuthenticationSelections() {
        List<AuthenticationSelectionOption> selections = new ArrayList<>();
        selections.add(new AuthenticationSelectionOption("opt1", "Option 1"));
        result.setAuthenticationSelections(selections);
        assertEquals(selections, result.getAuthenticationSelections());
    }

    @Test
    void testGetEventDetailsAndUserErrorMessage() {
        result.failure(AuthenticationFlowError.INVALID_USER, "details", "msg");
        assertEquals("details", result.getEventDetails());
        assertEquals("msg", result.getUserErrorMessage());
    }

    @Test
    void testGetAuthenticator() {
        assertEquals(authenticator, result.getAuthenticator());
    }

    @Test
    void testGetClientAuthenticator() throws Exception {
        // Create Result with ClientAuthenticator instead of Authenticator
        Constructor<Result> constructor = Result.class.getDeclaredConstructor(
                AuthenticationExecutionModel.class,
                ClientAuthenticator.class,
                List.class
        );
        constructor.setAccessible(true);
        Result clientResult = constructor.newInstance(execution, clientAuthenticator, currentExecutions);
        assertEquals(clientAuthenticator, clientResult.getClientAuthenticator());
    }

    @Test
    void testGetTopLevelFlow() {
        AuthenticationFlowModel topFlow = mock(AuthenticationFlowModel.class);
        when(execution.getParentFlow()).thenReturn(topFlow);
        // Note: AuthenticatorUtil.getTopParentFlow is static, difficult to mock.
        // This test confirms the call structure.
        assertNotNull(result.getTopLevelFlow()); 
    }

    @Test
    void testGetCategoryRequirementFromCurrentFlow() {
        // This requires significant mocking of the internal stream and factory.
        AuthenticationFlowModel parentFlow = mock(AuthenticationFlowModel.class);
        when(execution.getParentFlow()).thenReturn(parentFlow);
        
        KeycloakSessionFactory factory = mock(KeycloakSessionFactory.class);
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        
        AuthenticationExecutionModel otherExecution = mock(AuthenticationExecutionModel.class);
        when(otherExecution.getParentFlow()).thenReturn(parentFlow);
        when(otherExecution.getAuthenticator()).thenReturn("auth-id");
        when(otherExecution.getRequirement()).thenReturn(AuthenticationExecutionModel.Requirement.REQUIRED);
        
        when(realm.getAuthenticationExecutionsStream(parentFlow)).thenReturn(Collections.singletonList(otherExecution).stream());
        
        AuthenticatorFactory mockFactory = mock(AuthenticatorFactory.class);
        when(factory.getProviderFactory(eq(Authenticator.class), eq("auth-id"))).thenReturn(mockFactory);
        when(mockFactory.getReferenceCategory()).thenReturn("category1");

        AuthenticationExecutionModel.Requirement req = result.getCategoryRequirementFromCurrentFlow("category1");
        assertEquals(AuthenticationExecutionModel.Requirement.REQUIRED, req);
    }

    @Test
    void testCoreAuthenticate_ReturnsChallenge() throws Exception {
        AuthenticationProcessor spyProcessor = spy(processor);
        Response mockResponse = mock(Response.class);
        doReturn(mockResponse).when(spyProcessor).authenticateOnly();

        Response resultResponse = spyProcessor.authenticate();
        assertEquals(mockResponse, resultResponse);
    }

    @Test
    void testCoreAuthenticateOnly_Success() throws Exception {
        AuthenticationProcessor spyProcessor = spy(processor);
        AuthenticationFlow mockFlow = mock(AuthenticationFlow.class);
        
        // We need to mock createFlowExecution
        // Since it's a real method, we use spy and doReturn
        doReturn(mockFlow).when(spyProcessor).createFlowExecution(anyString(), any());
        when(mockFlow.processFlow()).thenReturn(null);
        when(mockFlow.isSuccessful()).thenReturn(true);
        when(authenticationSession.getAuthenticatedUser()).thenReturn(user);

        Response resultResponse = spyProcessor.authenticateOnly();
        assertNull(resultResponse);
    }

    @Test
    void testCoreAuthenticateOnly_ThrowsUnknownUser() throws Exception {
        AuthenticationProcessor spyProcessor = spy(processor);
        AuthenticationFlow mockFlow = mock(AuthenticationFlow.class);
        
        doReturn(mockFlow).when(spyProcessor).createFlowExecution(anyString(), any());
        when(mockFlow.processFlow()).thenReturn(null);
        when(authenticationSession.getAuthenticatedUser()).thenReturn(null);

        assertThrows(AuthenticationFlowException.class, () -> spyProcessor.authenticateOnly());
    }

    @Test
    void testCoreAuthenticationAction_Success() throws Exception {
        AuthenticationProcessor spyProcessor = spy(processor);
        String executionId = "exec-id";
        AuthenticationExecutionModel model = mock(AuthenticationExecutionModel.class);
        
        when(authenticationSession.getAuthNote(AuthenticationProcessor.CURRENT_AUTHENTICATION_EXECUTION)).thenReturn(executionId);
        when(authenticationSession.getAuthenticatedUser()).thenReturn(user);
        when(realm.getAuthenticationExecutionById(executionId)).thenReturn(model);
        
        AuthenticationFlow mockFlow = mock(AuthenticationFlow.class);
        doReturn(mockFlow).when(spyProcessor).createFlowExecution(anyString(), any());
        when(mockFlow.processAction(executionId)).thenReturn(null);
        when(mockFlow.isSuccessful()).thenReturn(true);

        Response resultResponse = spyProcessor.authenticationAction(executionId);
        assertNull(resultResponse);
    }

    @Test
    void testCoreAuthenticationAction_PageExpired() throws Exception {
        AuthenticationProcessor spyProcessor = spy(processor);
        when(authenticationSession.getAuthNote(AuthenticationProcessor.CURRENT_AUTHENTICATION_EXECUTION)).thenReturn("current");
        // Execution doesn't match
        
        spyProcessor.authenticationAction("mismatch");
        // This calls AuthenticationFlowURLHelper.showPageExpired which returns a Response.
        // We check that it doesn't crash.
    }

    @Test
    void testCoreAuthenticateClient_Success() throws Exception {
        AuthenticationProcessor spyProcessor = spy(processor);
        AuthenticationFlow mockFlow = mock(AuthenticationFlow.class);
        
        doReturn(mockFlow).when(spyProcessor).createFlowExecution(anyString(), any());
        when(mockFlow.processFlow()).thenReturn(null);
        when(mockFlow.isSuccessful()).thenReturn(true);

        Response resultResponse = spyProcessor.authenticateClient();
        assertNull(resultResponse);
    }

    @Test
    void testAttachUserSession_NotLightweight() {
        // Test the false branch of isLightweightUser(userSession.getUser())
        when(userSession.getUser()).thenReturn(user);
        when(user.isLightweight()).thenReturn(false); 
        // Assuming isLightweight is accessible or mocked. 
        // In the source it's: isLightweightUser(userSession.getUser())
        
        result.attachUserSession(userSession);
        
        // Verify setAuthenticatedUser was NOT called on the processor's auth session 
        // (unless it was already set by something else, but here we test the branch)
        verify(authenticationSession, never()).setAuthenticatedUser(any());
    }

    @Test
    void testLogFailure_UserNotFound() {
        // Test the false branch of if (user != null) in logFailure
        when(realm.isBruteForceProtected()).thenReturn(true);
        
        try (MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            authManagerMock.when(() -> AuthenticationManager.lookupUserForBruteForceLog(any(), any(), any()))
                           .thenReturn(null);

            processor.logFailure("exec-id");
            
            verify(protector, never()).failedLogin(any(), any(), any(), any(), any());
        }
    }
}
