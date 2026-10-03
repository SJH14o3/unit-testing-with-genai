package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.HttpHeaders;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.protocol.oidc.par.endpoints.request.AuthzEndpointParParser;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.protocol.oidc.endpoints.request.RequestUriType;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTest_AdditionalSessionTest {
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
    void init() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(authSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
    }

    @Test
    @DisplayName("sendPushRevocationPolicyRequest failure on IOException")
    void testT1() throws Exception {
        // The admin action is signed before the request is sent
        org.keycloak.models.TokenManager tokenManager = mock(org.keycloak.models.TokenManager.class);
        when(session.tokens()).thenReturn(tokenManager);
        when(tokenManager.encode(any())).thenReturn("token");
        // Mock HttpClientProvider to throw IOException
        HttpClientProvider httpProvider = mock(HttpClientProvider.class);
        when(session.getProvider(HttpClientProvider.class)).thenReturn(httpProvider);
        when(httpProvider.postText(anyString(), anyString())).thenThrow(new java.io.IOException("fail"));
        boolean result = protocol.sendPushRevocationPolicyRequest(realm, client, 200, "http://example.com/notify");
        assertFalse(result, "Push should fail on IOException");
    }

    @Test
    @DisplayName("authenticationComplete throws if PAR not found")
    void testT2() {
        // Set auth note to PAR but parser returns null
        when(authSession.getAuthNote(eq(Constants.AUTHORIZATION_REQUEST_URI)))
                .thenReturn("parUri");
        try (MockedStatic<AuthorizationEndpointRequestParserProcessor> parserMock =
                     mockStatic(AuthorizationEndpointRequestParserProcessor.class);
             MockedStatic<AuthzEndpointParParser> parMock = mockStatic(AuthzEndpointParParser.class)) {
            parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getRequestUriType("parUri"))
                    .thenReturn(RequestUriType.PAR);
            parMock.when(() -> AuthzEndpointParParser.removeRequestObject(session, "parUri"))
                    .thenReturn(null);
            assertThrows(org.keycloak.authentication.AuthenticationFlowException.class,
                    () -> protocol.authenticationComplete(authSession),
                    "AuthenticationFlowException should be thrown when PAR cannot be removed");
        }
    }

    @Test
    @DisplayName("authenticated code flow does not throw when offline token is requested")
    void testT3() {
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)))
                .thenReturn("code");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_MODE_PARAM)))
                .thenReturn("query");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.REDIRECT_URI_PARAM)))
                .thenReturn("http://redirect.uri");
        // Setup client session context
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        when(clientSession.getClient()).thenReturn(client);
        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);
        // Ensure offline token requested flag
        when(clientSessionCtx.isOfflineTokenRequested()).thenReturn(true);
        when(userSession.getRealm()).thenReturn(realm);
        when(realm.getAccessCodeLifespan()).thenReturn(60);
        when(userSession.getId()).thenReturn("user-session-id");
        when(clientSession.getNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("http://localhost:8080/realms/test-realm");
        // No exception should occur
        try (MockedStatic<OAuth2CodeParser> codeParser = mockStatic(OAuth2CodeParser.class)) {
            codeParser.when(() -> OAuth2CodeParser.persistCode(eq(session), any(AuthenticatedClientSessionModel.class), any(OAuth2Code.class)))
                    .thenReturn("code123");
            assertDoesNotThrow(() -> protocol.authenticated(authSession, userSession, clientSessionCtx));
        }
    }
}
