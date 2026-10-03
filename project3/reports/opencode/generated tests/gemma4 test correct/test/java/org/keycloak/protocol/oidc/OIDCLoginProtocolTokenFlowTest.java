package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.common.util.Time;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.services.clientpolicy.ClientPolicyManager;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTokenFlowTest {

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
        when(event.user(any(UserModel.class))).thenReturn(event);
        when(event.session(any(UserSessionModel.class))).thenReturn(event);
        when(event.client(any(ClientModel.class))).thenReturn(event);
        when(event.event(any(EventType.class))).thenReturn(event);
        when(event.detail(anyString(), anyString())).thenReturn(event);
        protocol = new OIDCLoginProtocol(mock(OIDCProviderConfig.class));
        protocol.setSession(session).setRealm(realm).setUriInfo(uriInfo).setHttpHeaders(headers).setEventBuilder(event);
    }

    @Test
    void testAuthenticated_ImplicitFlow_Success() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        UserSessionModel userSession = mock(UserSessionModel.class);
        ClientSessionContext clientSessionCtx = mock(ClientSessionContext.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);

        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);
        when(clientSession.getClient()).thenReturn(client);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)).thenReturn("id_token");
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_MODE_PARAM)).thenReturn("query");
        when(authSession.getRedirectUri()).thenReturn("https://example.com/callback");
        when(clientSession.getNote(OIDCLoginProtocol.ISSUER)).thenReturn("https://issuer.com");
        when(session.clientPolicy()).thenReturn(mock(ClientPolicyManager.class));

        try (MockedStatic<OIDCRedirectUriBuilder> mockedBuilder = mockStatic(OIDCRedirectUriBuilder.class);
             MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedConstruction<org.keycloak.protocol.oidc.TokenManager> mockedTokenManager = mockConstruction(org.keycloak.protocol.oidc.TokenManager.class, (mock, context) -> {
                 org.keycloak.protocol.oidc.TokenManager.AccessTokenResponseBuilder responseBuilder = mock(org.keycloak.protocol.oidc.TokenManager.AccessTokenResponseBuilder.class);
                 AccessTokenResponse tokenResponse = mock(AccessTokenResponse.class);
                 
                 when(mock.responseBuilder(any(), any(), any(), any(), any(), any())).thenReturn(responseBuilder);
                 when(responseBuilder.generateAccessToken()).thenReturn(responseBuilder);
                 when(responseBuilder.generateIDToken(anyBoolean())).thenReturn(responseBuilder);
                 when(responseBuilder.build()).thenReturn(tokenResponse);
                 when(tokenResponse.getIdToken()).thenReturn("id-token-content");
             })) {

            OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
            mockedBuilder.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any())).thenReturn(builder);
            when(builder.addParam(anyString(), anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(Response.ok().build());

            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.isExcludeIssuerFromAuthResponse()).thenReturn(false);

            Response response = protocol.authenticated(authSession, userSession, clientSessionCtx);

            assertNotNull(response);
            verify(builder).addParam(OAuth2Constants.ID_TOKEN, "id-token-content");
        }
    }

    @Test
    void testAuthenticated_ClientPolicyException() throws Exception {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        UserSessionModel userSession = mock(UserSessionModel.class);
        ClientSessionContext clientSessionCtx = mock(ClientSessionContext.class);
        AuthenticatedClientSessionModel clientSession = mock(AuthenticatedClientSessionModel.class);
        ClientModel client = mock(ClientModel.class);

        when(clientSessionCtx.getClientSession()).thenReturn(clientSession);
        when(clientSession.getClient()).thenReturn(client);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)).thenReturn("id_token");
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_MODE_PARAM)).thenReturn("query");
        when(authSession.getRedirectUri()).thenReturn("https://example.com/callback");

        ClientPolicyManager clientPolicyManager = mock(ClientPolicyManager.class);
        doThrow(new ClientPolicyException("error", "detail")).when(clientPolicyManager).triggerOnEvent(any());
        when(session.clientPolicy()).thenReturn(clientPolicyManager);

        try (MockedStatic<OIDCRedirectUriBuilder> mockedBuilder = mockStatic(OIDCRedirectUriBuilder.class);
             MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedConstruction<AuthenticationSessionManager> mockedAuthSessionManager = mockConstruction(AuthenticationSessionManager.class);
             MockedConstruction<org.keycloak.protocol.oidc.TokenManager> mockedTokenManager = mockConstruction(org.keycloak.protocol.oidc.TokenManager.class, (mock, context) -> {
                 org.keycloak.protocol.oidc.TokenManager.AccessTokenResponseBuilder responseBuilder = mock(org.keycloak.protocol.oidc.TokenManager.AccessTokenResponseBuilder.class);
                 when(mock.responseBuilder(any(), any(), any(), any(), any(), any())).thenReturn(responseBuilder);
                 when(responseBuilder.generateAccessToken()).thenReturn(responseBuilder);
             })) {

            OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
            mockedBuilder.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any())).thenReturn(builder);
            when(builder.addParam(anyString(), anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(Response.ok().build());

            OIDCAdvancedConfigWrapper config = mock(OIDCAdvancedConfigWrapper.class);
            mockedConfig.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(config);
            when(config.isExcludeIssuerFromAuthResponse()).thenReturn(false);

            Response response = protocol.authenticated(authSession, userSession, clientSessionCtx);

            assertNotNull(response);
            verify(event).error("error");
        }
    }

    @Test
    void testSendPushRevocationPolicyRequest_Success() throws IOException {
        ClientModel resource = mock(ClientModel.class);
        when(resource.getClientId()).thenReturn("test-client");

        org.keycloak.models.TokenManager tokenManager = mock(org.keycloak.models.TokenManager.class);
        when(tokenManager.encode(any(org.keycloak.Token.class))).thenReturn("encoded-token");
        when(session.tokens()).thenReturn(tokenManager);

        try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {

            mockedTime.when(Time::currentTime).thenReturn(1000);
            HttpClientProvider httpClientProvider = mock(HttpClientProvider.class);
            when(session.getProvider(HttpClientProvider.class)).thenReturn(httpClientProvider);
            when(httpClientProvider.postText(anyString(), anyString())).thenReturn(204);

            boolean success = protocol.sendPushRevocationPolicyRequest(realm, resource, 123, "https://example.com/mgmt");

            assertTrue(success);
        }
    }

    @Test
    void testSendPushRevocationPolicyRequest_Failure() {
        ClientModel resource = mock(ClientModel.class);
        when(resource.getClientId()).thenReturn("test-client");

        org.keycloak.models.TokenManager tokenManager = mock(org.keycloak.models.TokenManager.class);
        when(tokenManager.encode(any(org.keycloak.Token.class))).thenReturn("encoded-token");
        when(session.tokens()).thenReturn(tokenManager);

        try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {

            mockedTime.when(Time::currentTime).thenReturn(1000);
            HttpClientProvider httpClientProvider = mock(HttpClientProvider.class);
            when(session.getProvider(HttpClientProvider.class)).thenReturn(httpClientProvider);
            try {
                when(httpClientProvider.postText(anyString(), anyString())).thenThrow(new java.io.IOException("network error"));
            } catch (java.io.IOException e) {
                // ignore
            }

            boolean success = protocol.sendPushRevocationPolicyRequest(realm, resource, 123, "https://example.com/mgmt");

            assertFalse(success);
        }
    }
}
