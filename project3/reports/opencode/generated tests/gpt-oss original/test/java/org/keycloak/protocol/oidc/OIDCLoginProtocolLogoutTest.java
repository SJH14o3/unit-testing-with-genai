import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.quarkus.runtime.session.quarkus.QuarkusSession;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.models.*;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;
import org.keycloak.services.managers.ResourceAdminManager;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.oidc.grants.device.AuthzEndpointParParser;
import org.keycloak.protocol.oidc.grants.device.DeviceGrantType;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.protocol.oidc.utils.OIDCProviderConfig;
import org.keycloak.protocol.oidc.grants.device.OIDCAdvancedConfigWrapper;
import org.keycloak.protocol.oidc.grants.device.OIDCAdvancedConfigWrapper;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;

import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolLogoutTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock UserSessionModel userSession;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock ClientModel client;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void init() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
    }

    @Test
    @DisplayName("backchannelLogout delegates to backend logout when backchannel url present")
    void testBackchannelWithUrl() {
        // Set backchannel logout URL via org.keycloak.protocol.oidc.grants.device.OIDCAdvancedConfigWrapper
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.getBackchannelLogoutUrl()).thenReturn("http://backchannel.url");
        // Mock static method to return our config
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            // Mock resource manager
            ResourceAdminManager managerMock = mock(ResourceAdminManager.class);
            when(session.getProvider(eq(ResourceAdminManager.class))).thenReturn(managerMock);
            // Expect logoutClientSessionWithBackchannelLogoutUrl called
            Response fakeResp = Response.ok("backchannel").build();
            when(managerMock.logoutClientSessionWithBackchannelLogoutUrl(eq(client), eq(clientSession))).thenReturn(fakeResp);
            Response res = protocol.backchannelLogout(userSession, clientSession);
            assertSame(fakeResp, res);
            verify(managerMock).logoutClientSessionWithBackchannelLogoutUrl(eq(client), eq(clientSession));
        }
    }

    @Test
    @DisplayName("backchannelLogout falls back to normal logout when URL absent")
    void testBackchannelNoUrl() {
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.getBackchannelLogoutUrl()).thenReturn(null);
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            ResourceAdminManager managerMock = mock(ResourceAdminManager.class);
            when(session.getProvider(eq(ResourceAdminManager.class))).thenReturn(managerMock);
            Response fakeResp = Response.ok("normal").build();
            when(managerMock.logoutClientSession(eq(realm), eq(client), eq(clientSession))).thenReturn(fakeResp);
            Response res = protocol.backchannelLogout(userSession, clientSession);
            assertSame(fakeResp, res);
            verify(managerMock).logoutClientSession(eq(realm), eq(client), eq(clientSession));
        }
    }

    @Test
    @DisplayName("frontchannelLogout registers client if front channel logout enabled")
    void testFrontchannelLogoutEnabled() {
        // Enable front channel logout via config
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.isFrontChannelLogoutEnabled()).thenReturn(true);
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            // Mock FrontChannelLogoutHandler currentOrCreate
            try (MockedStatic<org.keycloak.protocol.oidc.grants.device.FrontChannelLogoutHandler> fc = mockStatic(org.keycloak.protocol.oidc.grants.device.FrontChannelLogoutHandler.class)) {
                org.keycloak.protocol.oidc.grants.device.FrontChannelLogoutHandler handlerMock = mock(org.keycloak.protocol.oidc.grants.device.FrontChannelLogoutHandler.class);
                fc.when(() -> org.keycloak.protocol.oidc.grants.device.FrontChannelLogoutHandler.currentOrCreate(any(), eq(clientSession))).thenReturn(handlerMock);
                doNothing().when(handlerMock).addClient(eq(client));
                Response res = protocol.frontchannelLogout(userSession, clientSession);
                assertNull(res); // method returns null
                verify(handlerMock).addClient(eq(client));
            }
        }
    }

    @Test
    @DisplayName("frontchannelLogout does nothing when disabled")
    void testFrontchannelLogoutDisabled() {
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.isFrontChannelLogoutEnabled()).thenReturn(false);
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            Response res = protocol.frontchannelLogout(userSession, clientSession);
            assertNull(res);
            // No handler should be created
            verifyNoInteractions(org.keycloak.protocol.oidc.grants.device.FrontChannelLogoutHandler.class);
        }
    }
}
