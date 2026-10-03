package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventBuilder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.models.*;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;
import org.keycloak.services.managers.ResourceAdminManager;
import org.keycloak.protocol.ClientData;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;

import static org.mockito.Mockito.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolLogoutTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock
    UriInfo uriInfo;
    @Mock
    HttpHeaders headers;
    @Mock
    EventBuilder event;
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
        when(clientSession.getClient()).thenReturn(client);
        // Set backchannel logout URL via org.keycloak.protocol.oidc.grants.device.OIDCAdvancedConfigWrapper
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.getBackchannelLogoutUrl()).thenReturn("http://backchannel.url");
        // Mock static method to return our config
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            // The protocol instantiates the manager itself, so intercept the construction
            Response fakeResp = Response.ok("backchannel").build();
            try (MockedConstruction<ResourceAdminManager> construction = mockConstruction(ResourceAdminManager.class,
                    (manager, ctx) -> when(manager.logoutClientSessionWithBackchannelLogoutUrl(eq(client), eq(clientSession)))
                            .thenReturn(fakeResp))) {
                Response res = protocol.backchannelLogout(userSession, clientSession);
                assertSame(fakeResp, res);
                ResourceAdminManager managerMock = construction.constructed().get(0);
                verify(managerMock).logoutClientSessionWithBackchannelLogoutUrl(eq(client), eq(clientSession));
            }
        }
    }

    @Test
    @DisplayName("backchannelLogout falls back to normal logout when URL absent")
    void testBackchannelNoUrl() {
        when(clientSession.getClient()).thenReturn(client);
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.getBackchannelLogoutUrl()).thenReturn(null);
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            Response fakeResp = Response.ok("normal").build();
            try (MockedConstruction<ResourceAdminManager> construction = mockConstruction(ResourceAdminManager.class,
                    (manager, ctx) -> when(manager.logoutClientSession(eq(realm), eq(client), eq(clientSession)))
                            .thenReturn(fakeResp))) {
                Response res = protocol.backchannelLogout(userSession, clientSession);
                assertSame(fakeResp, res);
                ResourceAdminManager managerMock = construction.constructed().get(0);
                verify(managerMock).logoutClientSession(eq(realm), eq(client), eq(clientSession));
            }
        }
    }

    @Test
    @DisplayName("frontchannelLogout registers client if front channel logout enabled")
    void testFrontchannelLogoutEnabled() {
        when(clientSession.getClient()).thenReturn(client);
        // Enable front channel logout via config
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.isFrontChannelLogoutEnabled()).thenReturn(true);
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            // Mock FrontChannelLogoutHandler currentOrCreate
            try (MockedStatic<FrontChannelLogoutHandler> fc = mockStatic(FrontChannelLogoutHandler.class)) {
                FrontChannelLogoutHandler handlerMock = mock(FrontChannelLogoutHandler.class);
                fc.when(() -> FrontChannelLogoutHandler.currentOrCreate(any(), eq(clientSession))).thenReturn(handlerMock);
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
        when(clientSession.getClient()).thenReturn(client);
        OIDCAdvancedConfigWrapper configMock = mock(OIDCAdvancedConfigWrapper.class);
        when(configMock.isFrontChannelLogoutEnabled()).thenReturn(false);
        try (MockedStatic<OIDCAdvancedConfigWrapper> wrapper = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            wrapper.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(configMock);
            try (MockedStatic<FrontChannelLogoutHandler> fc = mockStatic(FrontChannelLogoutHandler.class)) {
                Response res = protocol.frontchannelLogout(userSession, clientSession);
                assertNull(res);
                // No handler should be created
                fc.verifyNoInteractions();
            }
        }
    }
}
