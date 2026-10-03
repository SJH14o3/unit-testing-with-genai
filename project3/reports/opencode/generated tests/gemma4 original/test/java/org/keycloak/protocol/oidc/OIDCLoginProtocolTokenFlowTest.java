package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.AuthenticationSessionModel;
import org.keycloak.common.util.Time;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.representations.AccessTokenResponse;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testAuthenticated_ClientPolicyException() {
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

        try (MockedStatic<OIDCRedirectUriBuilder> mockedBuilder = mockStatic(OIDCRedirectUriBuilder.class);
             MockedStatic<OIDCAdvancedConfigWrapper> mockedConfig = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedConstruction<org.keycloak.protocol.oidc.TokenManager> mockedTokenManager = mockConstruction(org.keycloak.protocol.oidc.TokenManager.class, (mock, context) -> {
                 org.keycloak.protocol.oidc.TokenManager.AccessTokenResponseBuilder responseBuilder = mock(org.keycloak.protocol.oidc.TokenManager.AccessTokenResponseBuilder.class);
                 when(mock.responseBuilder(any(), any(), any(), any(), any(), any())).thenReturn(responseBuilder);
                 when(responseBuilder.generateAccessToken()).thenReturn(responseBuilder);
                 // Trigger exception during triggerOnEvent
                 doThrow(new ClientPolicyException("error", "detail")).when(session).clientPolicy();
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
    void testSendPushRevocationPolicyRequest_Success() {
        ClientModel resource = mock(ClientModel.class);
        when(resource.getClientId()).thenReturn("test-client");
        
        try (MockedStatic<Time> mockedTime = mockStatic(Time.class);
             MockedStatic<HttpClientProvider> mockedHttp = mockStatic(HttpClientProvider.class)) {
            
            mockedTime.when(Time::currentTime).thenReturn(1000L);
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
        
        try (MockedStatic<Time> mockedTime = mockStatic(Time.class);
             MockedStatic<HttpClientProvider> mockedHttp = mockStatic(HttpClientProvider.class)) {
            
            mockedTime.when(Time::currentTime).thenReturn(1000L);
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
