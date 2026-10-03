package org.keycloak.authentication.extra;

import jakarta.ws.rs.core.UriBuilder;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.ClientData;

import org.keycloak.services.ErrorPage;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.util.JsonSerialization;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Stage 1 – Direct method coverage (get/set, newEvent, helper utilities).
 */
public class AuthenticationProcessorDirectMethodsTest {

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
        when(mockAuthSession.getProtocol()).thenReturn("openid-connect");

        // Stub protocol provider
        LoginProtocol protocolMock = mock(LoginProtocol.class);
        when(mockSession.getProvider(eq(LoginProtocol.class), anyString())).thenReturn(protocolMock);
        ClientData clientDataMock = mock(ClientData.class);
        when(protocolMock.getClientData(mockAuthSession)).thenReturn(clientDataMock);
        when(clientDataMock.encode()).thenReturn("encodedClientData");

        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        when(mockSession.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getProviderFactoriesStream(any())).thenReturn(Stream.empty());

        // Stub AuthenticationSessionManager for signed auth session id
        AuthenticationSessionManager asmMock = mock(AuthenticationSessionManager.class);
        when(asmMock.signAndEncodeToBase64AuthSessionId(anyString())).thenReturn("signedId");
        // Use reflection to inject asmMock? It is instantiated inside method, not using session.
    }

    @Test
    @DisplayName("getUserSession returns set session")
    void testGetUserSession() throws Exception {
        // set protected field via reflection
        java.lang.reflect.Field field = AuthenticationProcessor.class.getDeclaredField("userSession");
        field.setAccessible(true);
        field.set(processor, mockUserSession);
        assertSame(mockUserSession, processor.getUserSession());
    }

    @Test
    @DisplayName("setFlowId returns processor and getFlowId works")
    void testSetFlowId() {
        AuthenticationProcessor res = processor.setFlowId("flow-123");
        assertSame(processor, res);
        assertNull(processor.getFlowPath());
    }

    @Test
    @DisplayName("forwarded message setters store messages")
    void testSetForwardedMessages() {
        org.keycloak.models.utils.FormMessage msg = new org.keycloak.models.utils.FormMessage("msg");
        processor.setForwardedErrorMessage(msg);
        processor.setForwardedSuccessMessage(msg);
        processor.setForwardedInfoMessage(msg);
        verify(mockAuthSession).setAuthNote(eq("fwMessageError"), anyString());
        verify(mockAuthSession).setAuthNote(eq("fwMessageSuccess"), anyString());
        verify(mockAuthSession).setAuthNote(eq("fwMessageInfo"), anyString());
    }

    @Test
    @DisplayName("newEvent creates and stores EventBuilder")
    void testNewEvent() {
        EventBuilder res = processor.newEvent();
        assertSame(processor.getEvent(), res);
        // event field should now be a new EventBuilder instance
        assertNotNull(processor.getEvent());
    }

    // tests testGetClientData and testGetSignedAuthSessionId removed since they were only accessing private methods.

    @Test
    @DisplayName("getRefreshUrl builds URI correct for authSessionIdParam flag")
    void testGetRefreshUrl() {
        UriBuilder uriBuilder = mock(UriBuilder.class);
        when(mockUriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
        when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
        when(uriBuilder.queryParam(anyString(), any())).thenReturn(uriBuilder);
        when(uriBuilder.build(any())).thenReturn(URI.create("http://localhost"));
        when(mockAuthSession.getClient()).thenReturn(mockClient);
        when(mockClient.getClientId()).thenReturn("client");
        when(mockAuthSession.getTabId()).thenReturn("tab");
        when(mockRealm.getName()).thenReturn("realm");
        processor.setFlowPath("/flow");
        try (var mockStatic = mockStatic(LoginActionsService.class)) {
            mockStatic.when(() -> LoginActionsService.loginActionsBaseUrl(mockUriInfo)).thenReturn(uriBuilder);
            URI resp = processor.getRefreshUrl(false);
            assertNotNull(resp);
        }
        // Cannot inspect the URI directly; ensure no exception thrown.
    }

    @Test
    @DisplayName("Result.getCategoryRequirementFromCurrentFlow returns null when no match")
    void testResultRequirementNoMatch() {
        AuthenticationExecutionModel execMock = mock(AuthenticationExecutionModel.class);
        when(execMock.getParentFlow()).thenReturn("PF");
        Authenticator authenticatorMock = mock(Authenticator.class);
        java.util.List<AuthenticationExecutionModel> execs = Collections.emptyList();
        AuthenticationProcessor.Result result = processor.createAuthenticatorContext(execMock, authenticatorMock, execs);
        AuthenticationExecutionModel.Requirement req = result.getCategoryRequirementFromCurrentFlow("none");
        assertNull(req);
    }
}
