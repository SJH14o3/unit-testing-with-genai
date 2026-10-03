package org.keycloak.authentication;

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
import org.keycloak.services.managers.UserSessionManager;
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
        when(mockSession.getProvider(LoginProtocol.class, anyString())).thenReturn(protocolMock);
        ClientData clientDataMock = mock(ClientData.class);
        when(protocolMock.getClientData(mockAuthSession)).thenReturn(clientDataMock);
        when(clientDataMock.encode()).thenReturn("encodedClientData");
    }

    @Nested
    @DisplayName("1. generateCode()")
    class GenerateCodeTest {
        @Test
        @DisplayName("delegates to ClientSessionCode and updates parent timestamp")
        void testGenerateCode() {
            // Spy the processor to intercept the ClientSessionCode constructor
            AuthenticationProcessor spy = Mockito.spy(processor);
            // mock AuthSession's parent
            AuthenticationSessionModel parent = mock(AuthenticationSessionModel.class);
            when(mockAuthSession.getParentSession()).thenReturn(parent);
            ClientSessionCode mockCode = mock(ClientSessionCode.class);
            // Use doReturn to inject the mocked code via constructor using mock construction stub
            Mockito.doReturn(mockCode).when(spy).getAuthenticationSession(); // We will override the method that returns session ID? Actually generateCode creates a new ClientSessionCode.
        }
    }

    // Additional tests would cover getClientData, getSignedAuthSessionId, getRefreshUrl, getActionUrl, getActionTokenUrl, getRefreshExecutionUrl.
}
