package org.keycloak.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.authenticators.client.ClientAuthUtil;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.BruteForceProtector;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.URI;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationProcessorErrorTest {

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
    private BruteForceProtector protector;
    @Mock
    private ClientConnection connection;
    @Mock
    private LoginFormsProvider formsProvider;
    @Mock
    private UserModel user;
    @Mock
    private Response response;

    @BeforeEach
    void setUp() {
        processor = new AuthenticationProcessor();
        processor.setSession(session)
                 .setRealm(realm)
                 .setAuthenticationSession(authenticationSession)
                 .setConnection(connection)
                 .setUriInfo(uriInfo)
                 .setRequest(request)
                 .setEventBuilder(event);
        processor.setClient(client);
    }

    @Test
    void testHandleBrowserExceptionList() {
        // Create AFE list with different errors to hit all switch cases
        AuthenticationFlowException afe1 = mock(AuthenticationFlowException.class);
        when(afe1.getError()).thenReturn(AuthenticationFlowError.INVALID_USER);
        
        AuthenticationFlowException afe2 = mock(AuthenticationFlowException.class);
        when(afe2.getError()).thenReturn(AuthenticationFlowError.USER_DISABLED);
        
        AuthenticationFlowException afe3 = mock(AuthenticationFlowException.class);
        when(afe3.getError()).thenReturn(AuthenticationFlowError.USER_TEMPORARILY_DISABLED);
        
        AuthenticationFlowException afe4 = mock(AuthenticationFlowException.class);
        when(afe4.getError()).thenReturn(AuthenticationFlowError.INVALID_CLIENT_SESSION);
        
        AuthenticationFlowException afe5 = mock(AuthenticationFlowException.class);
        when(afe5.getError()).thenReturn(AuthenticationFlowError.EXPIRED_CODE);
        
        AuthenticationFlowException afe6 = mock(AuthenticationFlowException.class);
        when(afe6.getError()).thenReturn(AuthenticationFlowError.DISPLAY_NOT_SUPPORTED);
        
        AuthenticationFlowException afe7 = mock(AuthenticationFlowException.class);
        when(afe7.getError()).thenReturn(AuthenticationFlowError.CREDENTIAL_SETUP_REQUIRED);

        AuthenticationFlowException mainException = mock(AuthenticationFlowException.class);
        when(mainException.getAfeList()).thenReturn(Arrays.asList(afe1, afe2, afe3, afe4, afe5, afe6, afe7));

        when(session.getProvider(LoginFormsProvider.class)).thenReturn(formsProvider);
        when(formsProvider.setAuthenticationSession(authenticationSession)).thenReturn(formsProvider);
        when(formsProvider.createErrorPage(any())).thenReturn(response);

        Response result = processor.handleBrowserExceptionList(mainException);

        assertEquals(response, result);
        verify(event).error(Errors.USER_NOT_FOUND); // for afe1
        verify(event).error(Errors.USER_DISABLED);  // for afe2
        verify(event).error(Errors.USER_TEMPORARILY_DISABLED); // for afe3
        verify(event).error(Errors.INVALID_CODE); // for afe4
        verify(event).error(Errors.EXPIRED_CODE); // for afe5
        verify(event).error(Errors.DISPLAY_UNSUPPORTED); // for afe6
        verify(event).error(Errors.INVALID_USER_CREDENTIALS); // for afe7
        verify(formsProvider, atLeastOnce()).addError(any());
    }

    @Test
    void testHandleBrowserException_InvalidUser() {
        AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.INVALID_USER);
        
        try (MockedStatic<ErrorPage> errorPageMock = mockStatic(ErrorPage.class)) {
            errorPageMock.when(() -> ErrorPage.error(eq(session), eq(authenticationSession), any(), any()))
                         .thenReturn(response);
            
            Response result = processor.handleBrowserException(e);
            
            assertEquals(response, result);
            verify(event).error(Errors.USER_NOT_FOUND);
        }
    }

    @Test
    void testHandleBrowserException_GenericException() {
        Exception e = new Exception("Something went wrong");
        
        try (MockedStatic<ErrorPage> errorPageMock = mockStatic(ErrorPage.class)) {
            errorPageMock.when(() -> ErrorPage.error(eq(session), eq(authenticationSession), any(), any()))
                         .thenReturn(response);
            
            Response result = processor.handleBrowserException(e);
            
            assertEquals(response, result);
            verify(event).error(Errors.INVALID_USER_CREDENTIALS);
        }
    }

    @Test
    void testHandleClientAuthException_ClientNotFound() {
        AuthenticationFlowException e = new AuthenticationFlowException(AuthenticationFlowError.CLIENT_NOT_FOUND);

        try (MockedStatic<ClientAuthUtil> clientAuthUtilMock = mockStatic(ClientAuthUtil.class)) {
            clientAuthUtilMock.when(() -> ClientAuthUtil.errorResponse(anyInt(), anyString(), anyString())).thenReturn(response);
            Response result = processor.handleClientAuthException(e);
            assertEquals(response, result);
        }

        verify(event).error(Errors.CLIENT_NOT_FOUND);
    }

    @Test
    void testHandleClientAuthException_GenericException() {
        Exception e = new Exception("Unexpected error");

        try (MockedStatic<ClientAuthUtil> clientAuthUtilMock = mockStatic(ClientAuthUtil.class)) {
            clientAuthUtilMock.when(() -> ClientAuthUtil.errorResponse(anyInt(), anyString(), anyString())).thenReturn(response);
            Response result = processor.handleClientAuthException(e);
            assertEquals(response, result);
        }

        verify(event).error(Errors.INVALID_CLIENT_CREDENTIALS);
    }

    @Test
    void testLogFailure_BruteForceProtected() {
        when(realm.isBruteForceProtected()).thenReturn(true);
        when(session.getProvider(BruteForceProtector.class)).thenReturn(protector);

        // Use deep stubs to handle the chain session.getContext().getHttpRequest().getUri()
        KeycloakSession deepSession = mock(KeycloakSession.class, RETURNS_DEEP_STUBS);
        doReturn(protector).when(deepSession).getProvider(BruteForceProtector.class);
        processor.setSession(deepSession);

        try (MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            authManagerMock.when(() -> AuthenticationManager.lookupUserForBruteForceLog(any(), any(), any()))
                    .thenReturn(user);

            authManagerMock.when(() -> AuthenticationManager.getAuthenticationCategory(any(), anyString()))
                    .thenReturn(null);

            processor.logFailure("exec-id");

            verify(protector).failedLogin(eq(realm), eq(user), eq(connection), any(), any());
        }
    }

    @Test
    void testLogFailure_NotBruteForceProtected() {
        when(realm.isBruteForceProtected()).thenReturn(false);
        
        processor.logFailure("exec-id");
        
        verify(protector, never()).failedLogin(any(), any(), any(), any(), any());
    }
}
