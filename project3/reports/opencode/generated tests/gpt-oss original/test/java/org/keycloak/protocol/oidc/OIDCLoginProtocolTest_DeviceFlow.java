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
import org.keycloak.OAuth2Constants;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.protocol.oidc.grants.device.DeviceGrantType;
import org.jboss.logging.Logger;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolTest_DeviceFlow {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock ClientSessionModel clientSession;
    @Mock ClientModel client;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(clientSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        // Create dummy Redirect builder returns redirect location
        when(clientSession.getClient()).thenReturn(client);
        when(clientSessionCtx.getClientSession()).thenReturn((AuthenticatedClientSessionModel)clientSession);

        mockStatic(DeviceGrantType.class);
        // Default behavior: isOAuth2DeviceVerificationFlow returns false; we override in test cases
    }

    @Test
    @DisplayName("authenticated device flow approval path")
    void testAuthenticatedDeviceApproval() {
        // Mock the static method that detects device flow to true
        when(DeviceGrantType.isOAuth2DeviceVerificationFlow(authSession)).thenReturn(true);
        // Mock approveOAuth2DeviceAuthorization to return a custom response
        Response expected = Response.ok("approved").build();
        when(DeviceGrantType.approveOAuth2DeviceAuthorization(eq(authSession), any(AuthenticatedClientSessionModel.class), eq(session)))
                .thenReturn(expected);

        Response actual = protocol.authenticated(clientSessionCtx, userSession, clientSessionCtx);
        assertEquals(expected, actual, "Response should be the mocked approval response");
    }

    @Test
    @DisplayName("sendError device flow denial path")
    void testSendErrorDeviceDenial() {
        // Mock the static method that detects device flow to true
        when(DeviceGrantType.isOAuth2DeviceVerificationFlow(authSession)).thenReturn(true);
        // Mock denyOAuth2DeviceAuthorization to return a custom response
        Response expected = Response.status(Response.Status.BAD_REQUEST).entity("denied").build();
        when(DeviceGrantType.denyOAuth2DeviceAuthorization(eq(authSession), any(Error.class), eq(session)))
                .thenReturn(expected);

        Response actual = protocol.sendError(authSession, Error.CANCELED_AIA, "user cancelled");
        assertEquals(expected, actual, "Response should be the mocked denial response");
    }
}
