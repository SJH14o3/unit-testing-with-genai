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
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.OAuth2Constants;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.events.EventBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTest_HybridFlowTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock
    UriInfo uriInfo;
    @Mock
    HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock
    AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock ClientModel client;
    @Mock TokenManager.AccessTokenResponseBuilder responseBuilder;

    OIDCLoginProtocol protocol;

    MockedStatic<OAuth2CodeParser> codeParser;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(clientSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        // Set response type and mode for hybrid flow
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)))
                .thenReturn("code id_token token");
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
        when(userSession.getRealm()).thenReturn(realm);
        when(realm.getAccessCodeLifespan()).thenReturn(60);
        when(userSession.getId()).thenReturn("user-session-id");
        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);
        when(clientSession.getNote(eq(OIDCLoginProtocol.ISSUER))).thenReturn("http://localhost:8080/realms/test-realm");
        when(session.clientPolicy()).thenReturn(mock(org.keycloak.services.clientpolicy.ClientPolicyManager.class));

        // Token generation is out of scope here, so the TokenManager created inside
        // OIDCLoginProtocol#authenticated is replaced by a mocked one.
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setToken("accessTokenValue");
        tokenResponse.setIdToken("idTokenValue");
        tokenResponse.setTokenType("Bearer");
        tokenResponse.setExpiresIn(300);
        when(responseBuilder.build()).thenReturn(tokenResponse);
        when(responseBuilder.generateAccessToken()).thenReturn(responseBuilder);
        when(responseBuilder.generateIDToken(anyBoolean())).thenReturn(responseBuilder);
        when(responseBuilder.generateAccessTokenHash()).thenReturn(responseBuilder);
        when(responseBuilder.generateCodeHash(anyString())).thenReturn(responseBuilder);
        when(responseBuilder.generateStateHash(anyString())).thenReturn(responseBuilder);

        // Mock static OAuth2CodeParser.persistCode
        codeParser = mockStatic(OAuth2CodeParser.class);
        codeParser.when(() -> OAuth2CodeParser.persistCode(eq(session), any(AuthenticatedClientSessionModel.class), any(OAuth2Code.class)))
                .thenReturn("dummyCode123");
    }

    @AfterEach
    void tearDown() {
        codeParser.close();
    }

    @Test
    @DisplayName("authenticated Hybrid flow adds access_token, id_token and code")
    void testAuthenticatedHybridFlow() {
        Response resp;
        try (MockedConstruction<TokenManager> tokenManagers = mockConstruction(TokenManager.class,
                (tokenManager, context) -> when(tokenManager.responseBuilder(any(), any(), any(), any(), any(), any()))
                        .thenReturn(responseBuilder))) {
            resp = protocol.authenticated(authSession, userSession, clientSessionCtx);
        }
        assertNotNull(resp, "Response should not be null");
        String location = String.valueOf(resp.getMetadata().getFirst("Location"));
        assertNotNull(location, "Location header must exist");
        assertTrue(location.contains("access_token=accessTokenValue"), "Redirect must contain access_token");
        assertTrue(location.contains("id_token=idTokenValue"), "Redirect must contain id_token");
        assertTrue(location.contains("code=dummyCode123"), "Redirect must contain the persisted code");
    }
}
