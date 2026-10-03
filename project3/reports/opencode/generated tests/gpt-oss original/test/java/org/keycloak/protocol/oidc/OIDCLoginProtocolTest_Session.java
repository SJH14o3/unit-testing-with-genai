import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.Instant;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.services.Urls;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolTest_Session {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock ClientModel client;
    @Mock ClientSessionModel clientSession;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(authSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
    }

    @Test
    @DisplayName("requireReauthentication: prompt login true")
    void testReAuthPromptLogin() {
        when(authSession.getClientNote(eq(OIDCLoginProtocol.PROMPT_PARAM)))
                .thenReturn("login");
        assertTrue(protocol.requireReauthentication(userSession, authSession), "Re-auth should be true for prompt=login");
    }

    @Test
    @DisplayName("requireReauthentication: auth_time expired")
    void testReAuthAuthTimeExpired() {
        String authTime = String.valueOf(Instant.now().minusSeconds(4000).getEpochSecond());
        when(userSession.getNote(eq(AuthenticationManager.AUTH_TIME)))
                .thenReturn(authTime);
        when(authSession.getClientNote(eq(OIDCLoginProtocol.MAX_AGE_PARAM)))
                .thenReturn("3000");
        assertTrue(protocol.requireReauthentication(userSession, authSession), "Re-auth should be true when auth_time + max_age < now");
    }

    @Test
    @DisplayName("requireReauthentication: required action re-auth true")
    void testReAuthRequiredAction() {
        when(authSession.getClientNote(eq(Constants.KC_ACTION)))
                .thenReturn("some-action");
        RequiredActionProvider rap = mock(RequiredActionProvider.class);
        when(rap.getMaxAuthAge(session)).thenReturn(3000);
        when(session.getProvider(eq(RequiredActionProvider.class), eq("some-action")))
                .thenReturn(rap);
        String authTime = String.valueOf(Instant.now().minusSeconds(4000).getEpochSecond());
        when(userSession.getNote(eq(AuthenticationManager.AUTH_TIME))).thenReturn(authTime);
        assertTrue(protocol.requireReauthentication(userSession, authSession), "Re-auth should be true when required action max age exceeded");
    }

    @Test
    @DisplayName("sendPushRevocationPolicyRequest success")
    void testSendPushSuccess() throws Exception {
        org.keycloak.services.clientpolicy.ClientPolicyException dummy = mock(org.keycloak.services.clientpolicy.ClientPolicyException.class);
        when(session.getProvider(eq(org.keycloak.connections.httpclient.HttpClientProvider.class)))
                .thenReturn(mock(org.keycloak.connections.httpclient.HttpClientProvider.class));
        when(session.tokens()).thenReturn(mock(org.keycloak.TokenProvider.class));
        String token = "token";
        when(session.tokens().encode(any())).thenReturn(token);
        // Mock postText
        when(session.getProvider(eq(org.keycloak.connections.httpclient.HttpClientProvider.class)).postText(anyString(), eq(token)).thenReturn(204);
        boolean result = protocol.sendPushRevocationPolicyRequest(realm, client, 100, "http://example.com/push");
        assertTrue(result, "Push should succeed with 204");
    }

    @Test
    @DisplayName("authenticationComplete removes PAR if present")
    void testAuthenticationCompletePAR() {
        // set request_uri auth note
        when(authSession.getAuthNote(eq(org.keycloak.constants.Constants.AUTHORIZATION_REQUEST_URI)))
                .thenReturn("paruri");
        org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor parserProcessor = mock(org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.class);
        // We'll use static method stub via mockStatic
        mockStatic(org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.class);
        when(org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor.getRequestUriType("paruri"))
                .thenReturn(org.keycloak.protocol.oidc.endpoints.request.RequestUriType.PAR);
        when(org.keycloak.protocol.oidc.grants.device.AuthzEndpointParParser.removeRequestObject(session, "paruri"))
                .thenReturn(Mockito.mock(org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest.class));
        // Should not throw
        assertDoesNotThrow(() -> protocol.authenticationComplete(authSession));
    }
}
