package org.keycloak.protocol.oidc.endpoints;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.Profile;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakUriInfo;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.services.ErrorPageException;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.services.Urls;
import org.keycloak.services.util.CacheControlUtil;
import org.keycloak.services.util.LocaleUtil;
import org.keycloak.services.messages.Messages;
import org.keycloak.protocol.oidc.utils.AcrUtils;
import org.keycloak.http.HttpRequest;
import jakarta.ws.rs.core.HttpHeaders;

import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.keycloak.OAuth2Constants.AUTHORIZATION_DETAILS;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

@ExtendWith(MockitoExtension.class)
public class AuthorizationEndpointEdgeTest {

    @Mock
    private org.keycloak.models.KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthenticationSessionModel authSession;

    @Mock
    private AuthorizationEndpointRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(realm.getName()).thenReturn("demo-realm");
        
        KeycloakContext context = mock(KeycloakContext.class);
        ClientConnection clientConnection = mock(ClientConnection.class);
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        KeycloakUriInfo uriInfo = mock(KeycloakUriInfo.class);
        
        lenient().when(uriInfo.getBaseUri()).thenReturn(URI.create("https://demo.com/"));
        lenient().when(context.getUri()).thenReturn(uriInfo);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getRequestHeaders()).thenReturn(httpHeaders);
        lenient().when(context.getClient()).thenReturn(client);
        lenient().when(context.getAuthenticationSession()).thenReturn(authSession);
        lenient().when(session.getContext()).thenReturn(context);
    }

    /* ----------  authorizeDevice – feature disabled  ---------- */
    @Test
    public void testAuthorizeDeviceFeatureDisabled_ReturnsNull() {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        try (var staticMock = mockStatic(Profile.class)) {
            staticMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.DEVICE_FLOW))
                      .thenReturn(false);
            Object result = endpoint.authorizeDevice();
            assertNull(result, "device endpoint should be null when feature disabled");
        }
    }

    /* ----------  authorizeDevice – feature enabled  ---------- */
    @Test
    public void testAuthorizeDeviceFeatureEnabled_ReturnsDeviceEndpoint() {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        try (var staticMock = mockStatic(Profile.class)) {
            staticMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.DEVICE_FLOW))
                      .thenReturn(true);
            Object result = endpoint.authorizeDevice();
            assertNotNull(result, "DeviceEndpoint should be returned when enabled");
            assertTrue(result instanceof org.keycloak.protocol.oidc.grants.device.endpoints.DeviceEndpoint,
                       "Returned object should be DeviceEndpoint");
        }
    }

    /* ----------  updateAuthenticationSession stores params  ---------- */
    @Test
    public void testUpdateAuthenticationSession_SetsAdditionalParams() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);

        // Prepare AuthorizationEndpointRequest with extra params
        Map<String, String> extraParams = new HashMap<>();
        extraParams.put("custom-param", "value");
        lenient().when(request.getAdditionalReqParams()).thenReturn(extraParams);
        lenient().when(request.getState()).thenReturn("state123");
        lenient().when(request.getResponseMode()).thenReturn("query");
        lenient().when(request.getClientId()).thenReturn("client-1");
        lenient().when(request.getResponseType()).thenReturn("code");
        lenient().when(request.getRedirectUri()).thenReturn("https://example.com/callback");
        lenient().when(request.getMaxAge()).thenReturn(null);
        lenient().when(request.getUiLocales()).thenReturn(null);
        lenient().when(request.getClaims()).thenReturn(null);
        lenient().when(request.getAcr()).thenReturn(null);
        lenient().when(request.getIdpHint()).thenReturn(null);
        lenient().when(request.getAction()).thenReturn(null);
        lenient().when(request.getDisplay()).thenReturn(null);
        lenient().when(request.getCodeChallengeMethod()).thenReturn(null);
        lenient().when(request.getCodeChallenge()).thenReturn(null);
        lenient().when(request.getLoginHint()).thenReturn(null);
        lenient().when(request.getNonce()).thenReturn(null);
        lenient().when(request.getScope()).thenReturn(null);
        lenient().when(request.getResource()).thenReturn(null);
        lenient().when(request.getDpopJkt()).thenReturn(null);

        // Inject request into the private field
        java.lang.reflect.Field requestField = AuthorizationEndpoint.class.getDeclaredField("request");
        requestField.setAccessible(true);
        requestField.set(endpoint, request);

        // Initialize authenticationSession via reflection
        java.lang.reflect.Field authSessionField = AuthorizationEndpoint.class.getDeclaredField("authenticationSession");
        authSessionField.setAccessible(true);
        authSessionField.set(endpoint, authSession);
        lenient().when(authSession.getClient()).thenReturn(client);

        // Also need to set client, redirectUri, session context
        java.lang.reflect.Field clientField = AuthorizationEndpoint.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(endpoint, client);
        lenient().when(client.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);
        lenient().when(client.getAttribute(org.keycloak.models.Constants.ACR_LOA_MAP)).thenReturn(null);
        lenient().when(client.getRealm()).thenReturn(realm);
        lenient().when(realm.getAttribute(org.keycloak.models.Constants.ACR_LOA_MAP)).thenReturn(null);

        java.lang.reflect.Field redirectUriField = AuthorizationEndpoint.class.getDeclaredField("redirectUri");
        redirectUriField.setAccessible(true);
        redirectUriField.set(endpoint, "https://example.com/callback");

        // Set session context
        KeycloakContext context = mock(KeycloakContext.class);
        KeycloakUriInfo uriInfo = mock(KeycloakUriInfo.class);
        lenient().when(uriInfo.getBaseUri()).thenReturn(URI.create("https://demo.com/"));
        lenient().when(context.getUri()).thenReturn(uriInfo);
        lenient().when(context.getClient()).thenReturn(client);
        lenient().when(session.getContext()).thenReturn(context);

        // Invoke private updateAuthenticationSession()
        java.lang.reflect.Method updateMethod = AuthorizationEndpoint.class.getDeclaredMethod("updateAuthenticationSession");
        updateMethod.setAccessible(true);
        updateMethod.invoke(endpoint);

        // Verify that the client note was set
        verify(authSession).setClientNote("client_request_param_custom-param", "value");
        // authorization_details is null, so setClientNote should not be called for it
        verify(authSession, never()).setClientNote(eq(AUTHORIZATION_DETAILS), anyString());
    }
}
