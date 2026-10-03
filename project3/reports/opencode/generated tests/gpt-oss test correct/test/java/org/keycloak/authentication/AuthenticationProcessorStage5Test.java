package org.keycloak.authentication;

import jakarta.ws.rs.core.UriInfo;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.authentication.Authenticator;
import org.keycloak.http.HttpRequest;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.messages.Messages;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Stage 5 – Core flow helpers: validateUser, authenticator context creation.
 */
public class AuthenticationProcessorStage5Test {

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
        processor.setRealm(mockRealm)
                .setAuthenticationSession(mockAuthSession)
                .setConnection(mockConnection)
                .setUriInfo(mockUriInfo)
                .setSession(mockSession)
                .setEventBuilder(mockEventBuilder)
                .setRequest(mockRequest)
                .setClient(mockClient);
        when(mockEventBuilder.user(any(UserModel.class))).thenReturn(mockEventBuilder);
        when(mockEventBuilder.detail(anyString(), anyString())).thenReturn(mockEventBuilder);
    }

    @Test
    @DisplayName("validateUser accepts null")
    void testValidateUserNull() {
        // should not throw
        assertDoesNotThrow(() -> processor.validateUser(null));
    }

    @Test
    @DisplayName("validateUser throws when user disabled")
    void testValidateUserDisabled() {
        UserModel disabledUser = mock(UserModel.class);
        when(disabledUser.isEnabled()).thenReturn(false);
        AuthenticationFlowException ex = assertThrows(AuthenticationFlowException.class,
                () -> processor.validateUser(disabledUser));
        assertEquals(AuthenticationFlowError.USER_DISABLED, ex.getError());
    }

    @Test
    @DisplayName("validateUser throws when user has service account link")
    void testValidateUserServiceAccount() {
        UserModel svcUser = mock(UserModel.class);
        when(svcUser.isEnabled()).thenReturn(true);
        when(svcUser.getServiceAccountClientLink()).thenReturn("service-account");
        AuthenticationFlowException ex = assertThrows(AuthenticationFlowException.class,
                () -> processor.validateUser(svcUser));
        assertEquals(AuthenticationFlowError.UNKNOWN_USER, ex.getError());
    }

    @Test
    @DisplayName("createAuthenticatorContext returns instance with correct execution model")
    void testCreateAuthenticatorContext() {
        AuthenticationExecutionModel model = mock(AuthenticationExecutionModel.class);
        Authenticator authenticator = mock(Authenticator.class);
        java.util.List<AuthenticationExecutionModel> execs = new ArrayList<>();
        AuthenticationProcessor.Result result = processor.createAuthenticatorContext(model, authenticator, execs);
        assertEquals(model, result.getExecution());
    }

    @Test
    @DisplayName("createClientAuthenticatorContext returns instance with correct execution model")
    void testCreateClientAuthenticatorContext() {
        AuthenticationExecutionModel model = mock(AuthenticationExecutionModel.class);
        ClientAuthenticator clientAuthenticator = mock(ClientAuthenticator.class);
        java.util.List<AuthenticationExecutionModel> execs = new ArrayList<>();
        AuthenticationProcessor.Result result = processor.createClientAuthenticatorContext(model, clientAuthenticator, execs);
        assertEquals(model, result.getExecution());
    }
}
