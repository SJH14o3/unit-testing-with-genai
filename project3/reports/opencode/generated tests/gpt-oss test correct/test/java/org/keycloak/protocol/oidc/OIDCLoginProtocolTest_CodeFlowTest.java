package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
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
import org.keycloak.models.*;
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
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.HttpHeaders;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OIDCLoginProtocolTest_CodeFlowTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock
    AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock ClientModel client;
    @Mock AuthenticatedClientSessionModel authClientSession;

    OIDCLoginProtocol protocol;

    MockedStatic<OAuth2CodeParser> codeParser;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(client.getId()).thenReturn("client-id");
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_TYPE_PARAM))).thenReturn(OIDCResponseType.CODE.toString());
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_MODE_PARAM))).thenReturn(OIDCResponseMode.QUERY.value());
        when(authSession.getClientNote(eq(OIDCLoginProtocol.STATE_PARAM))).thenReturn("state123");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.NONCE_PARAM))).thenReturn("nonce123");
        when(authSession.getClientNote(eq(OAuth2Constants.SCOPE))).thenReturn("openid email");
        when(authSession.getClientNote(eq(OAuth2Constants.RESOURCE))).thenReturn("resource1");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.REDIRECT_URI_PARAM))).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.CODE_CHALLENGE_PARAM))).thenReturn("challenge");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.CODE_CHALLENGE_METHOD_PARAM))).thenReturn("plain");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.DPOP_JKT))).thenReturn("dpop");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("issuer");
        when(authSession.getClientNote(eq(Constants.KC_ACTION_STATUS))).thenReturn(null);
        when(authSession.getClientNote(eq(Constants.KC_ACTION))).thenReturn(null);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.PROMPT_PARAM))).thenReturn(null);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.MAX_AGE_PARAM))).thenReturn(null);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.LOGOUT_REDIRECT_URI))).thenReturn(null);

        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.REDIRECT_URI_PARAM))).thenReturn("http://redirect.uri");

        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(clientSession.getClient()).thenReturn(client);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("issuer");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.LOGOUT_REDIRECT_URI))).thenReturn(null);
        when(clientSessionCtx.getClientSession()).thenReturn(authClientSession);
        when(authClientSession.getClient()).thenReturn(client);
        when(userSession.getRealm()).thenReturn(realm);
        when(realm.getAccessCodeLifespan()).thenReturn(60);
        when(userSession.getId()).thenReturn("user-session-id");
        when(authClientSession.getNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("issuer");

        // Stub OIDCAdvancedConfigWrapper
        // Since it's static, we can return a dummy via Mockito.mockStatic(DataClass). But for brevity, we skip detailed stubs; assume defaults.

        // Mock static OAuth2CodeParser.persistCode
        codeParser = mockStatic(OAuth2CodeParser.class);
        codeParser.when(() -> OAuth2CodeParser.persistCode(eq(session), any(AuthenticatedClientSessionModel.class), any(OAuth2Code.class)))
                .thenReturn("mockedCode123");
    }

    @AfterEach
    void tearDown() {
        codeParser.close();
    }

    @Test
    @DisplayName("authenticated CODE flow adds code param")
    void testAuthenticatedCodeFlow() {
        // Setup minimal client config when needed
        // Note: actual OIDCProviderConfig and OIDCAdvancedConfigWrapper are involved; for test we rely on defaults.

        Response response = protocol.authenticated(authSession, userSession, clientSessionCtx);
        assertNotNull(response, "Response should not be null");
        String location = String.valueOf(response.getMetadata().getFirst("Location"));
        assertNotNull(location, "Location header must exist");
        assertTrue(location.contains("code=mockedCode123"), "Redirect URI should contain code parameter");
    }
}
