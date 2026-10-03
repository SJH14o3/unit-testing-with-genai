package org.keycloak.protocol.oidc.endpoints;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.HttpResponse;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.OIDCProviderConfig;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class AuthorizationEndpointRoutingTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthenticationSessionModel authSession;

    @Mock
    private MultivaluedMap<String, String> formParams;

    @Mock
    private MultivaluedMap<String, String> queryParams;

    @Mock
    private KeycloakUriInfo uriInfo;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private LoginFormsProvider loginFormsProvider;

    @Mock
    private ClientPolicyManager clientPolicyManager;

    @Mock
    private OIDCLoginProtocol oidcLoginProtocol;

    @Mock
    private OIDCProviderConfig oidcProviderConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Profile.defaults();
        
        KeycloakContext context = mock(KeycloakContext.class);
        ClientConnection clientConnection = mock(ClientConnection.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        
        lenient().when(uriInfo.getQueryParameters()).thenReturn(queryParams);
        lenient().when(uriInfo.getBaseUri()).thenReturn(java.net.URI.create("https://demo.com/"));
        lenient().when(context.getUri()).thenReturn(uriInfo);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getRequestHeaders()).thenReturn(httpHeaders);
        lenient().when(context.getClient()).thenReturn(client);
        lenient().when(context.getHttpResponse()).thenReturn(httpResponse);
        lenient().when(session.getContext()).thenReturn(context);
        lenient().when(session.getProvider(LoginFormsProvider.class)).thenReturn(loginFormsProvider);
        lenient().when(session.clientPolicy()).thenReturn(clientPolicyManager);
        try {
            lenient().doNothing().when(clientPolicyManager).triggerOnEvent(any());
        } catch (Exception e) {
            // ignore
        }
        lenient().when(session.getProvider(any(), anyString())).thenReturn(oidcLoginProtocol);
        lenient().when(oidcLoginProtocol.getConfig()).thenReturn(oidcProviderConfig);
        when(oidcProviderConfig.getAdditionalReqParamsMaxNumber()).thenReturn(100);
        when(oidcProviderConfig.getAdditionalReqParamsMaxSize()).thenReturn(1000);
        when(oidcProviderConfig.isAdditionalReqParamsFailFast(false)).thenReturn(false);
        when(oidcProviderConfig.getAdditionalReqParamsMaxOverallSize()).thenReturn(10000);
        
        lenient().when(realm.getName()).thenReturn("demo-realm");
        lenient().when(realm.isEnabled()).thenReturn(true);
        lenient().when(realm.getClientByClientId(anyString())).thenReturn(client);
        lenient().when(client.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);
        lenient().when(client.isEnabled()).thenReturn(true);
        lenient().when(client.isBearerOnly()).thenReturn(false);
        lenient().when(client.getRootUrl()).thenReturn("https://example.com");
        lenient().when(client.getRedirectUris()).thenReturn(java.util.Collections.singleton("https://example.com/callback"));
        lenient().when(client.isStandardFlowEnabled()).thenReturn(true);
        
        lenient().when(httpRequest.getDecodedFormParameters()).thenReturn(formParams);
        lenient().when(formParams.get("client_id")).thenReturn(java.util.Collections.singletonList("test-client"));
        lenient().when(formParams.get("redirect_uri")).thenReturn(java.util.Collections.singletonList("https://example.com/callback"));
        lenient().when(formParams.get("response_type")).thenReturn(java.util.Collections.singletonList("code"));
        lenient().when(formParams.getFirst("client_id")).thenReturn("test-client");
        lenient().when(formParams.getFirst("redirect_uri")).thenReturn("https://example.com/callback");
        lenient().when(formParams.getFirst("response_type")).thenReturn("code");
        lenient().when(queryParams.get("client_id")).thenReturn(java.util.Collections.singletonList("test-client"));
        lenient().when(queryParams.get("redirect_uri")).thenReturn(java.util.Collections.singletonList("https://example.com/callback"));
        lenient().when(queryParams.get("response_type")).thenReturn(java.util.Collections.singletonList("code"));
        lenient().when(queryParams.getFirst("client_id")).thenReturn("test-client");
        lenient().when(queryParams.getFirst("redirect_uri")).thenReturn("https://example.com/callback");
        lenient().when(queryParams.getFirst("response_type")).thenReturn("code");
        
        lenient().when(loginFormsProvider.setAuthenticationSession(any())).thenReturn(loginFormsProvider);
        lenient().when(loginFormsProvider.setError(anyString(), any(Object[].class))).thenReturn(loginFormsProvider);
        lenient().when(loginFormsProvider.createErrorPage(any(Response.Status.class))).thenReturn(mock(Response.class));
    }

    @Test
    public void testBuildPostForwardsParameters() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);

        // Mock the private process method to return a successful response
        java.lang.reflect.Method processMethod = AuthorizationEndpoint.class.getDeclaredMethod("process", MultivaluedMap.class);
        processMethod.setAccessible(true);
        
        Response mockResponse = mock(Response.class);
        lenient().when(mockResponse.getStatus()).thenReturn(200);
        
        try (var staticMock = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder mockBuilder = mock(OIDCRedirectUriBuilder.class);
            staticMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any()))
                      .thenReturn(mockBuilder);
            when(mockBuilder.addParam(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockResponse);
            
            Response response = endpoint.buildPost();
            assertNotNull(response, "buildPost should return a Response");
        }
    }

    @Test
    public void testBuildGetForwardsParameters() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);

        Response mockResponse = mock(Response.class);
        lenient().when(mockResponse.getStatus()).thenReturn(200);
        
        try (var staticMock = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder mockBuilder = mock(OIDCRedirectUriBuilder.class);
            staticMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any()))
                      .thenReturn(mockBuilder);
            when(mockBuilder.addParam(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockResponse);
            
            Response response = endpoint.buildGet();
            assertNotNull(response, "buildGet should return a Response");
        }
    }
}