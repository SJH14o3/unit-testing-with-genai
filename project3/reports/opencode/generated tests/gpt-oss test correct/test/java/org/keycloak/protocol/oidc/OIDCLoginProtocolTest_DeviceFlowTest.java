package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventBuilder;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.grants.device.DeviceGrantType;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.keycloak.protocol.LoginProtocol.Error;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTest_DeviceFlowTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock
    UriInfo uriInfo;
    @Mock
    HttpHeaders headers;
    @Mock
    EventBuilder event;
    @Mock
    AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock ClientModel client;

    OIDCLoginProtocol protocol;

    MockedStatic<DeviceGrantType> deviceGrantType;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(clientSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);

        deviceGrantType = mockStatic(DeviceGrantType.class);
        // Default behavior: isOAuth2DeviceVerificationFlow returns false; we override in test cases
    }

    @AfterEach
    void tearDown() {
        deviceGrantType.close();
    }

    @Test
    @DisplayName("authenticated device flow approval path")
    void testAuthenticatedDeviceApproval() {
        // Mock the static method that detects device flow to true
        deviceGrantType.when(() -> DeviceGrantType.isOAuth2DeviceVerificationFlow(authSession)).thenReturn(true);
        // Mock approveOAuth2DeviceAuthorization to return a custom response
        Response expected = Response.ok("approved").build();
        deviceGrantType.when(() -> DeviceGrantType.approveOAuth2DeviceAuthorization(authSession, clientSession, session))
                .thenReturn(expected);

        Response actual = protocol.authenticated(authSession, userSession, clientSessionCtx);
        assertEquals(expected, actual, "Response should be the mocked approval response");
    }

    @Test
    @DisplayName("sendError device flow denial path")
    void testSendErrorDeviceDenial() {
        // Mock the static method that detects device flow to true
        deviceGrantType.when(() -> DeviceGrantType.isOAuth2DeviceVerificationFlow(authSession)).thenReturn(true);
        // Mock denyOAuth2DeviceAuthorization to return a custom response
        Response expected = Response.status(Response.Status.BAD_REQUEST).entity("denied").build();
        deviceGrantType.when(() -> DeviceGrantType.denyOAuth2DeviceAuthorization(authSession, Error.CANCELLED_AIA, session))
                .thenReturn(expected);

        Response actual = protocol.sendError(authSession, Error.CANCELLED_AIA, "user cancelled");
        assertEquals(expected, actual, "Response should be the mocked denial response");
    }
}
