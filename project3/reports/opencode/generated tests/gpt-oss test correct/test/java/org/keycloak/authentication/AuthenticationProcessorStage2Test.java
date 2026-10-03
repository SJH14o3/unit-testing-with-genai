package org.keycloak.authentication;

import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.http.HttpRequest;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.LoginProtocol.Error;
import org.keycloak.protocol.ClientData;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.managers.ClientSessionCode;
import org.keycloak.services.managers.UserSessionManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.util.JsonSerialization;
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
 * Stage 2 – URL and code helpers.
 */
public class AuthenticationProcessorStage2Test {

    @Mock private RealmModel mockRealm;
    @Mock private UserSessionModel mockUserSession;
    @Mock private AuthenticationSessionModel mockAuthSession;
    @Mock private ClientConnection mockConnection;
    @Mock private UriInfo mockUriInfo;
    @Mock private KeycloakSession mockSession;
    @Mock private EventBuilder mockEventBuilder;
    @Mock private HttpRequest mockRequest;
    @Mock private ClientModel mockClient;
    @Mock private KeycloakSessionFactory mockSessionFactory;
    @Mock private KeycloakTransactionManager mockTransactionManager;

    private AuthenticationProcessor processor;

    @BeforeEach
    void setUp() throws Exception {
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
        // Mock minimal protocol expectations
        LoginProtocol protocolMock = mock(LoginProtocol.class);
        when(mockSession.getProvider(eq(LoginProtocol.class), anyString())).thenReturn(protocolMock);
        ClientData clientDataMock = mock(ClientData.class);
        when(protocolMock.getClientData(mockAuthSession)).thenReturn(clientDataMock);
        when(clientDataMock.encode()).thenReturn("encodedClientData");

        // Mock parent session as RootAuthenticationSessionModel for generateCode()
        RootAuthenticationSessionModel mockParentSession = mock(RootAuthenticationSessionModel.class);
        when(mockAuthSession.getParentSession()).thenReturn(mockParentSession);

        // Mock transaction manager for ClientSessionCode
        when(mockSession.getTransactionManager()).thenReturn(mockTransactionManager);
        when(mockSession.getKeycloakSessionFactory()).thenReturn(mockSessionFactory);
    }

    @Nested
    @DisplayName("1. generateCode()")
    class GenerateCodeTest {
        @Test
        @DisplayName("delegates to ClientSessionCode and updates parent timestamp")
        void testGenerateCode() {
            // Verify parent session timestamp is updated
            processor.generateCode();
            verify(mockAuthSession.getParentSession()).setTimestamp(anyInt());
        }
    }

    // Additional tests would cover getClientData, getSignedAuthSessionId, getRefreshUrl, getActionUrl, getActionTokenUrl, getRefreshExecutionUrl.
}
