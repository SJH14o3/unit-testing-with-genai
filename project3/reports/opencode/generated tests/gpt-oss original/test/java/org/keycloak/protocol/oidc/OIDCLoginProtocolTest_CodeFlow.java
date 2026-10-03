import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenIdGenerator;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.common.util.Time;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.constants.AdapterConstants;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.ClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.services.Urls;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointChecker;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.HttpHeaders;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolTest_CodeFlow {
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
    @Mock AuthenticatedClientSessionModel authClientSession;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(clientSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_TYPE_PARAM))).thenReturn(OIDCResponseType.CODE.toString());
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_MODE_PARAM))).thenReturn(OIDCResponseMode.QUERY.toString());
        when(authSession.getClientNote(eq(OIDCLoginProtocol.STATE_PARAM))).thenReturn("state123");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.NONCE_PARAM))).thenReturn("nonce123");
        when(authSession.getClientNote(eq(OAuth2Constants.SCOPE))).thenReturn("openid email");
        when(authSession.getClientNote(eq(OAuth2Constants.RESOURCE))).thenReturn("resource1");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.REDIRECT_URI_PARAM))).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.CODE_CHALLENGE_PARAM))).thenReturn("challenge");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.CODE_CHALLENGE_METHOD_PARAM))).thenReturn("plain");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.DPOP_JKT))).thenReturn("dpop");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.SESSION_STATE_PARAM))).thenReturn("sessionState");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("issuer");
        when(authSession.getClientNote(eq(Constants.KC_ACTION_STATUS))).thenReturn(null);
        when(authSession.getClientNote(eq(Constants.KC_ACTION))).thenReturn(null);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.PROMPT_PARAM))).thenReturn(null);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.MAX_AGE_PARAM))).thenReturn(null);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.LOGOUT_REDIRECT_URI))).thenReturn(null);

        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.REDIRECT_URI_PARAM))).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.SESSION_STATE_PARAM))).thenReturn("ss");

        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(clientSession.getClient()).thenReturn(client);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.SESSION_STATE_PARAM))).thenReturn("ss");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("issuer");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.LOGOUT_REDIRECT_URI))).thenReturn(null);
        when(clientSessionCtx.getClientSession()).thenReturn(authClientSession);
        when(authClientSession.getClient()).thenReturn(client);
        when(authClientSession.getClientId()).thenReturn("client-id");

        // Stub OIDCAdvancedConfigWrapper
        // Since it's static, we can return a dummy via Mockito.mockStatic(DataClass). But for brevity, we skip detailed stubs; assume defaults.

        // Mock static OAuth2CodeParser.persistCode
        mockStatic(OAuth2CodeParser.class);
        when(OAuth2CodeParser.persistCode(eq(session), eq(clientSession), any(OAuth2Code.class)))
                .thenReturn("mockedCode123");
    }

    @Test
    @DisplayName("authenticated CODE flow adds code param")
    void testAuthenticatedCodeFlow() {
        // Setup minimal client config when needed
        // Note: actual OIDCProviderConfig and OIDCAdvancedConfigWrapper are involved; for test we rely on defaults.

        Response response = protocol.authenticated(clientSessionCtx, userSession, clientSessionCtx);
        assertNotNull(response, "Response should not be null");
        String location = response.getMetadata().getFirst("Location");
        assertNotNull(location, "Location header must exist");
        assertTrue(location.contains("code=mockedCode123"), "Redirect URI should contain code parameter");
    }
}
