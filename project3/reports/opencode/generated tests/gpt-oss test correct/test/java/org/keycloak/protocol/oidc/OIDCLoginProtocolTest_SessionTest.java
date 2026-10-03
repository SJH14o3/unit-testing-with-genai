package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventBuilder;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.protocol.oidc.par.endpoints.request.AuthzEndpointParParser;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.protocol.oidc.endpoints.request.RequestUriType;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTest_SessionTest {
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
    @Mock ClientModel client;

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
        HttpClientProvider httpClientProvider = mock(HttpClientProvider.class);
        when(session.getProvider(HttpClientProvider.class)).thenReturn(httpClientProvider);
        org.keycloak.models.TokenManager tokenManager = mock(org.keycloak.models.TokenManager.class);
        when(session.tokens()).thenReturn(tokenManager);
        String token = "token";
        when(tokenManager.encode(any())).thenReturn(token);
        // Mock postText
        when(httpClientProvider.postText(anyString(), eq(token))).thenReturn(204);
        boolean result = protocol.sendPushRevocationPolicyRequest(realm, client, 100, "http://example.com/push");
        assertTrue(result, "Push should succeed with 204");
    }

    @Test
    @DisplayName("authenticationComplete removes PAR if present")
    void testAuthenticationCompletePAR() {
        // set request_uri auth note
        when(authSession.getAuthNote(eq(Constants.AUTHORIZATION_REQUEST_URI)))
                .thenReturn("paruri");
        try (MockedStatic<AuthorizationEndpointRequestParserProcessor> parserProcessor =
                     mockStatic(AuthorizationEndpointRequestParserProcessor.class);
             MockedStatic<AuthzEndpointParParser> parParser = mockStatic(AuthzEndpointParParser.class)) {
            parserProcessor.when(() -> AuthorizationEndpointRequestParserProcessor.getRequestUriType("paruri"))
                    .thenReturn(RequestUriType.PAR);
            parParser.when(() -> AuthzEndpointParParser.removeRequestObject(session, "paruri"))
                    .thenReturn(Map.of());
            // Should not throw
            assertDoesNotThrow(() -> protocol.authenticationComplete(authSession));
        }
    }
}
