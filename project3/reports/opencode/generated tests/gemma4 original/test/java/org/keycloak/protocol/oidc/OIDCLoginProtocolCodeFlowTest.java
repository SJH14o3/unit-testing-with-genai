package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.AuthenticationSessionModel;
import org.keycloak.authentication.AuthenticationSessionModel.Action;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;
import org.keycloak.representations.AccessTokenResponse;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OIDCLoginProtocolCodeFlowTest {

    private KeycloakSession session;
    private RealmModel realm;
    private UriInfo uriInfo;
    private HttpHeaders headers;
    private org.keycloak.events.EventBuilder event;
    private OIDCLoginProtocol protocol;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        realm = mock(RealmModel.class);
        uriInfo = mock(UriInfo.class);
        headers = mock(HttpHeaders.class);
        event = mock(org.keycloak.events.EventBuilder.class);
        protocol = new OIDCLoginProtocol(mock(OIDCProviderConfig.class));
        protocol.setSession(session).setRealm(realm).setUriInfo(uriInfo).setHttpHeaders(headers).setEventBuilder(event);
    }

    @Test
    void testAuthenticated_DeviceFlow() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientSessionContext clientSessionCtx = mock(ClientSessionContext.class);
        Response expectedResponse = Response.ok().build();

        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);

        try (MockedStatic<org.keycloak.protocol.oidc.grants.device.DeviceGrantType> mockedDevice = mockStatic(org.keycloak.protocol.oidc.grants.device.DeviceGrantType.class)) {
            mockedDevice.when(() -> org.keycloak.protocol.oidc.grants.device.DeviceGrantType.isOAuth2DeviceVerificationFlow(authSession)).thenReturn(true);
            mockedDevice.when(() -> org.keycloak.protocol.oidc.grants.device.DeviceGrantType.approveOAuth2DeviceAuthorization(authSession, clientSession, session)).thenReturn(expectedResponse);

            Response response = protocol.authenticated(authSession, null, clientSessionCtx);

            assertNotNull(response);
            assertEquals(expectedResponse, response);
        }
    }

    @Test
    void testAuthenticated_CodeFlow_Success() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        UserSessionModel userSession = mock(UserSessionModel.class);
        ClientSessionContext clientSessionCtx = mock(ClientSessionContext.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);

        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);
        when(clientSession.getClient()).thenReturn(client);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)).thenReturn("code");
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_MODE_PARAM)).thenReturn("query");
        when(authSession.getRedirectUri()).thenReturn("https://example.com/callback");
        when(authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM)).thenReturn("state123");
        when(authSession.getClientNote(OIDCLoginProtocol.NONCE_PARAM)).thenReturn("nonce123");
        when(userSession.getId()).thenReturn("user-session-id");
        when(userSession.getRealm()).thenReturn(realm);
        when(realm.getAccessCodeLifespan()).thenReturn(300);
        when(clientSession.getNote(OIDCLoginProtocol.ISSUER)).thenReturn("https://issuer.com");
        when(client.getId()).thenReturn("test-client");

        try (MockedStatic<OIDCRedirectUriBuilder> mockedBuilder = mockStatic(OIDCRedirectUriBuilder.class);
             MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedStatic<OAuth2CodeParser> mockedCodeParser = mockStatic(OAuth2CodeParser.class);
             MockedStatic<SecretGenerator> mockedSecretGen = mockStatic(SecretGenerator.class);
             MockedStatic<Time> mockedTime = mockStatic(Time.class);
             MockedConstruction<OAuth2Code> mockedCode = mockConstruction(OAuth2Code.class)) {

            OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
            mockedBuilder.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any())).thenReturn(builder);
            when(builder.addParam(anyString(), anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(Response.ok().build());

            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.isExcludeSessionStateFromAuthResponse()).thenReturn(false);
            when(config.isExcludeIssuerFromAuthResponse()).thenReturn(false);

            SecretGenerator secretGenerator = mock(SecretGenerator.class);
            mockedSecretGen.when(SecretGenerator::getInstance).thenReturn(secretGenerator);
            when(secretGenerator.generateSecureID()).thenReturn("secure-id");
            
            mockedTime.when(Time::currentTime).thenReturn(1000L);
            mockedCodeParser.when(() -> OAuth2CodeParser.persistCode(any(), any(), any())).thenReturn("persisted-code");

            Response response = protocol.authenticated(authSession, userSession, clientSessionCtx);

            assertNotNull(response);
            verify(builder).addParam(OAuth2Constants.CODE, "persisted-code");
            verify(builder).addParam(OAuth2Constants.STATE, "state123");
            verify(builder).addParam(OAuth2Constants.SESSION_STATE, "user-session-id");
            verify(builder).addParam(OAuth2Constants.ISSUER, "https://issuer.com");
            verify(builder).addParam(OIDCLoginProtocol.NONCE_PARAM, "nonce123");
        }
    }
}
