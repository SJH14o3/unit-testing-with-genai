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
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.protocol.ClientData;
import org.keycloak.events.EventBuilder;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolTest_HybridFlow {
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
        // Set response type and mode for hybrid flow
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)))
                .thenReturn("token id_token");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_MODE_PARAM)))
                .thenReturn("fragment");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.STATE_PARAM)))
                .thenReturn("state123");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.NONCE_PARAM)))
                .thenReturn("nonce123");
        when(authSession.getClientNote(eq(OAuth2Constants.SCOPE)))
                .thenReturn("openid email");
        when(authSession.getClientNote(eq(OAuth2Constants.RESOURCE)))
                .thenReturn("resource1");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.REDIRECT_URI_PARAM)))
                .thenReturn("http://redirect.uri");
        when(clientSession.getClient()).thenReturn(client);
        when(clientSessionCtx.getClientSession()).thenReturn((AuthenticatedClientSessionModel)clientSession);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.SESSION_STATE_PARAM)))
                .thenReturn("sessionState");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.ISSUER)))
                .thenReturn("issuer");
        // Mock static OAuth2CodeParser.persistCode
        mockStatic(OAuth2CodeParser.class);
        when(OAuth2CodeParser.persistCode(eq(session), eq(clientSession), any(OAuth2Code.class)))
                .thenReturn("dummyCode123");
    }

    @Test
    @DisplayName("authenticated Hybrid flow adds access_token and id_token, no code")
    void testAuthenticatedHybridFlow() {
        Response resp = protocol.authenticated(clientSessionCtx, userSession, clientSessionCtx);
        assertNotNull(resp, "Response should not be null");
        String location = resp.getMetadata().getFirst("Location");
        assertNotNull(location, "Location header must exist");
        assertTrue(location.contains("access_token="), "Redirect must contain access_token");
        assertTrue(location.contains("id_token="), "Redirect must contain id_token");
        assertFalse(location.contains("code="), "Redirect must not contain code");
    }
}
