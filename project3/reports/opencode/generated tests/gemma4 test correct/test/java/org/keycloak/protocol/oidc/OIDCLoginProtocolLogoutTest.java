package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.endpoints.request.RequestUriType;
import org.keycloak.protocol.oidc.par.endpoints.request.AuthzEndpointParParser;
import org.keycloak.protocol.oidc.utils.LogoutUtil;
import org.keycloak.services.managers.ResourceAdminManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import jakarta.ws.rs.core.Response;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolLogoutTest {

    private KeycloakSession session;
    private RealmModel realm;
    private org.keycloak.events.EventBuilder event;
    private org.keycloak.protocol.oidc.OIDCLoginProtocol protocol;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        realm = mock(RealmModel.class);
        event = mock(org.keycloak.events.EventBuilder.class);
        when(event.user(any(UserModel.class))).thenReturn(event);
        when(event.session(any(UserSessionModel.class))).thenReturn(event);
        when(event.client(any(ClientModel.class))).thenReturn(event);
        when(event.event(any(EventType.class))).thenReturn(event);
        protocol = new OIDCLoginProtocol(mock(OIDCProviderConfig.class));
        protocol.setSession(session).setRealm(realm).setEventBuilder(event);
    }

    @Test
    void testBackchannelLogout_WithBackchannelUrl() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);
        
        when(clientSession.getClient()).thenReturn(client);
        
        // We need to handle OIDCAdvancedConfigWrapper.fromClientModel(client).getBackchannelLogoutUrl()
        // Since OIDCAdvancedConfigWrapper is likely a wrapper, we might need to mock its behavior if possible,
        // or it might be hard to mock if it's not an interface.
        // For the sake of this plan, let's assume we can mock the static call or the behavior.
        // In a real scenario, we'd use MockedStatic for OIDCAdvancedConfigWrapper.

        try (MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedConstruction<ResourceAdminManager> mockedAdmin = mockConstruction(ResourceAdminManager.class, (mock, context) -> {
                 when(mock.logoutClientSessionWithBackchannelLogoutUrl(any(), any())).thenReturn(Response.ok().build());
             })) {
            
            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.getBackchannelLogoutUrl()).thenReturn("https://example.com/backchannel");

            Response response = protocol.backchannelLogout(userSession, clientSession);

            assertNotNull(response);
            assertEquals(1, mockedAdmin.constructed().size());
            verify(mockedAdmin.constructed().get(0)).logoutClientSessionWithBackchannelLogoutUrl(client, clientSession);
        }
    }

    @Test
    void testBackchannelLogout_WithoutBackchannelUrl() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);
        
        when(clientSession.getClient()).thenReturn(client);

        try (MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedConstruction<ResourceAdminManager> mockedAdmin = mockConstruction(ResourceAdminManager.class, (mock, context) -> {
                 when(mock.logoutClientSession(any(), any(), any())).thenReturn(Response.ok().build());
             })) {
            
            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.getBackchannelLogoutUrl()).thenReturn(null);

            Response response = protocol.backchannelLogout(userSession, clientSession);

            assertNotNull(response);
            assertEquals(1, mockedAdmin.constructed().size());
            verify(mockedAdmin.constructed().get(0)).logoutClientSession(realm, client, clientSession);
        }
    }

    @Test
    void testFrontchannelLogout_Enabled() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);
        
        when(clientSession.getClient()).thenReturn(client);

        try (MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedStatic<org.keycloak.protocol.oidc.FrontChannelLogoutHandler> mockedHandler = mockStatic(org.keycloak.protocol.oidc.FrontChannelLogoutHandler.class)) {
            
            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.isFrontChannelLogoutEnabled()).thenReturn(true);

            org.keycloak.protocol.oidc.FrontChannelLogoutHandler handler = mock(org.keycloak.protocol.oidc.FrontChannelLogoutHandler.class);
            mockedHandler.when(() -> org.keycloak.protocol.oidc.FrontChannelLogoutHandler.currentOrCreate(eq(session), eq(clientSession))).thenReturn(handler);

            Response response = protocol.frontchannelLogout(userSession, clientSession);

            assertNull(response);
            verify(handler).addClient(client);
            verify(clientSession).setAction(AuthenticationSessionModel.Action.LOGGED_OUT.name());
        }
    }

    @Test
    void testFrontchannelLogout_Disabled() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);
        
        when(clientSession.getClient()).thenReturn(client);

        try (MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.isFrontChannelLogoutEnabled()).thenReturn(false);

            Response response = protocol.frontchannelLogout(userSession, clientSession);

            assertNull(response);
        }
    }

    @Test
    void testFinishBrowserLogout_WithHandler() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        UserModel user = mock(UserModel.class);
        AuthenticationSessionModel logoutSession = mock(AuthenticationSessionModel.class);
        ClientModel client = mock(ClientModel.class);
        String redirectUri = "https://example.com/logout-redirect";

        when(userSession.getUser()).thenReturn(user);
        when(logoutSession.getClient()).thenReturn(client);
        when(logoutSession.getAuthNote(OIDCLoginProtocol.LOGOUT_REDIRECT_URI)).thenReturn(redirectUri);

        try (MockedStatic<org.keycloak.protocol.oidc.FrontChannelLogoutHandler> mockedHandler = mockStatic(org.keycloak.protocol.oidc.FrontChannelLogoutHandler.class);
             MockedStatic<LogoutUtil> mockedLogoutUtil = mockStatic(LogoutUtil.class)) {
            
            org.keycloak.protocol.oidc.FrontChannelLogoutHandler handler = mock(org.keycloak.protocol.oidc.FrontChannelLogoutHandler.class);
            mockedHandler.when(() -> org.keycloak.protocol.oidc.FrontChannelLogoutHandler.current(session)).thenReturn(handler);
            
            when(LogoutUtil.getRedirectUriWithAttachedState(eq(redirectUri), any())).thenReturn(java.net.URI.create(redirectUri));
            when(handler.renderLogoutPage(anyString())).thenReturn(Response.ok().build());

            Response response = protocol.finishBrowserLogout(userSession, logoutSession);

            assertNotNull(response);
            verify(handler).renderLogoutPage(redirectUri);
        }
    }

    @Test
    void testFinishBrowserLogout_WithoutHandler() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        UserModel user = mock(UserModel.class);
        AuthenticationSessionModel logoutSession = mock(AuthenticationSessionModel.class);

        when(userSession.getUser()).thenReturn(user);
        when(logoutSession.getAuthNote(OIDCLoginProtocol.LOGOUT_REDIRECT_URI)).thenReturn(null);

        try (MockedStatic<org.keycloak.protocol.oidc.FrontChannelLogoutHandler> mockedHandler = mockStatic(org.keycloak.protocol.oidc.FrontChannelLogoutHandler.class);
             MockedStatic<LogoutUtil> mockedLogoutUtil = mockStatic(LogoutUtil.class)) {
            
            mockedHandler.when(() -> org.keycloak.protocol.oidc.FrontChannelLogoutHandler.current(session)).thenReturn(null);
            when(LogoutUtil.sendResponseAfterLogoutFinished(any(), any())).thenReturn(Response.ok().build());

            Response response = protocol.finishBrowserLogout(userSession, logoutSession);

            assertNotNull(response);
        }
    }

    @Test
    void testAuthenticationComplete_PARValid() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        String requestUri = "https://example.com/auth?request_uri=...";
        when(authSession.getAuthNote(Constants.AUTHORIZATION_REQUEST_URI)).thenReturn(requestUri);

        try (MockedStatic<org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor> mockedParser = mockStatic(org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.class);
             MockedStatic<AuthzEndpointParParser> mockedParParser = mockStatic(AuthzEndpointParParser.class)) {
            
            mockedParser.when(() -> org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.getRequestUriType(requestUri)).thenReturn(RequestUriType.PAR);
            mockedParParser.when(() -> AuthzEndpointParParser.removeRequestObject(eq(session), eq(requestUri))).thenReturn(java.util.Map.of("key", "value"));

            assertDoesNotThrow(() -> protocol.authenticationComplete(authSession));
        }
    }

    @Test
    void testAuthenticationComplete_PARNotFound_ThrowsException() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        String requestUri = "https://example.com/auth?request_uri=...";
        when(authSession.getAuthNote(Constants.AUTHORIZATION_REQUEST_URI)).thenReturn(requestUri);

        try (MockedStatic<org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor> mockedParser = mockStatic(org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.class);
             MockedStatic<AuthzEndpointParParser> mockedParParser = mockStatic(AuthzEndpointParParser.class)) {
            
            mockedParser.when(() -> org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.getRequestUriType(requestUri)).thenReturn(RequestUriType.PAR);
            mockedParParser.when(() -> AuthzEndpointParParser.removeRequestObject(eq(session), eq(requestUri))).thenReturn(null);

            assertThrows(AuthenticationFlowException.class, () -> protocol.authenticationComplete(authSession));
        }
    }
}
