package org.keycloak.protocol.oidc.endpoints;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.MultivaluedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakUriInfo;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.keycloak.common.ClientConnection;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.clientpolicy.context.AuthorizationRequestContext;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.HttpResponse;
import jakarta.ws.rs.core.HttpHeaders;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

@ExtendWith(MockitoExtension.class)
public class AuthorizationEndpointResponseModeTest {

    @Mock
    private org.keycloak.models.KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthorizationEndpointRequest request;

    @Mock
    private MultivaluedMap<String, String> params;

    @Mock
    private AuthenticationSessionModel authSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        KeycloakContext context = mock(KeycloakContext.class);
        ClientConnection clientConnection = mock(ClientConnection.class);
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        KeycloakUriInfo uriInfo = mock(KeycloakUriInfo.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        
        lenient().when(uriInfo.getBaseUri()).thenReturn(URI.create("https://demo.com/"));
        lenient().when(context.getUri()).thenReturn(uriInfo);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getRequestHeaders()).thenReturn(httpHeaders);
        lenient().when(context.getClient()).thenReturn(client);
        lenient().when(context.getHttpResponse()).thenReturn(httpResponse);
        lenient().when(session.getContext()).thenReturn(context);
        
        lenient().when(realm.getName()).thenReturn("demo-realm");
        lenient().when(client.getProtocol()).thenReturn(org.keycloak.protocol.oidc.OIDCLoginProtocol.LOGIN_PROTOCOL);
    }

    private void setupEndpoint(AuthorizationEndpoint endpoint, OIDCResponseMode responseMode, String redirectUri, String state, String responseModeStr) throws Exception {
        java.lang.reflect.Field parsedResponseModeField = AuthorizationEndpoint.class.getDeclaredField("parsedResponseMode");
        parsedResponseModeField.setAccessible(true);
        parsedResponseModeField.set(endpoint, responseMode);

        java.lang.reflect.Field redirectUriField = AuthorizationEndpoint.class.getDeclaredField("redirectUri");
        redirectUriField.setAccessible(true);
        redirectUriField.set(endpoint, redirectUri);

        java.lang.reflect.Field reqField = AuthorizationEndpoint.class.getDeclaredField("request");
        reqField.setAccessible(true);
        reqField.set(endpoint, request);
        lenient().when(request.getState()).thenReturn(state);
        lenient().when(request.getResponseMode()).thenReturn(responseModeStr);

        java.lang.reflect.Field clientField = AuthorizationEndpoint.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(endpoint, client);

        java.lang.reflect.Field authSessionField = AuthorizationEndpoint.class.getDeclaredField("authenticationSession");
        authSessionField.setAccessible(true);
        authSessionField.set(endpoint, authSession);
    }

    private Response createMockResponse(String location, int status) {
        Response mockResponse = mock(Response.class);
        lenient().when(mockResponse.getLocation()).thenReturn(URI.create(location));
        lenient().when(mockResponse.getStatus()).thenReturn(status);
        lenient().when(mockResponse.getEntity()).thenReturn("");
        return mockResponse;
    }

    /* ---------  redirectErrorToClient – state optional  --------- */
    @Test
    public void testRedirectErrorToClientWithState() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        setupEndpoint(endpoint, OIDCResponseMode.QUERY, "https://demo.com/callback", "xyz", "query");

        Response mockResponse = createMockResponse("https://demo.com/callback?error=invalid_request&error_description=Something+went+wrong&state=xyz", 302);

        try (var staticMock = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder mockBuilder = mock(OIDCRedirectUriBuilder.class);
            staticMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), eq(OIDCResponseMode.QUERY), any(), any()))
                      .thenReturn(mockBuilder);
            when(mockBuilder.addParam(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockResponse);

            java.lang.reflect.Method m = AuthorizationEndpoint.class.getDeclaredMethod("redirectErrorToClient",
                    OIDCResponseMode.class, String.class, String.class);
            m.setAccessible(true);
            Response r = (Response) m.invoke(endpoint, OIDCResponseMode.QUERY,
                    "invalid_request", "Something went wrong");

            String url = r.getLocation().toString();
            assertTrue(url.contains("error=invalid_request"), "URL should contain error");
            assertTrue(url.contains("error_description=Something+went+wrong"), "URL should contain description");
            assertTrue(url.contains("state=xyz"), "state should be present when request.getState() is set");
        }
    }

    /* ---------  RedirectErrorToClient – no errorDescription  --------- */
    @Test
    public void testRedirectErrorWithNoDescription() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        setupEndpoint(endpoint, OIDCResponseMode.QUERY, "https://demo.com/callback", null, "query");

        Response mockResponse = createMockResponse("https://demo.com/callback?error=access_denied", 302);

        try (var staticMock = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder mockBuilder = mock(OIDCRedirectUriBuilder.class);
            staticMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), eq(OIDCResponseMode.QUERY), any(), any()))
                      .thenReturn(mockBuilder);
            when(mockBuilder.addParam(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockResponse);

            java.lang.reflect.Method m = AuthorizationEndpoint.class.getDeclaredMethod("redirectErrorToClient",
                    OIDCResponseMode.class, String.class, String.class);
            m.setAccessible(true);
            Response r = (Response) m.invoke(endpoint, OIDCResponseMode.QUERY,
                    "access_denied", null);

            String url = r.getLocation().toString();
            assertTrue(url.contains("error=access_denied"), "URL should contain error");
            assertFalse(url.contains("error_description"), "No description should be in URL");
            assertFalse(url.contains("state="), "No state should be present");
        }
    }

    /* ---------  Fragment response mode  --------- */
    @Test
    public void testRedirectErrorToClientFragmentMode() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        setupEndpoint(endpoint, OIDCResponseMode.FRAGMENT, "https://demo.com/callback", "state123", "fragment");

        Response mockResponse = createMockResponse("https://demo.com/callback#error=access_denied&error_description=Access+denied&state=state123", 302);

        try (var staticMock = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder mockBuilder = mock(OIDCRedirectUriBuilder.class);
            staticMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), eq(OIDCResponseMode.FRAGMENT), any(), any()))
                      .thenReturn(mockBuilder);
            when(mockBuilder.addParam(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockResponse);

            java.lang.reflect.Method m = AuthorizationEndpoint.class.getDeclaredMethod("redirectErrorToClient",
                    OIDCResponseMode.class, String.class, String.class);
            m.setAccessible(true);
            Response r = (Response) m.invoke(endpoint, OIDCResponseMode.FRAGMENT,
                    "access_denied", "Access denied");

            String url = r.getLocation().toString();
            // For fragment mode, error params should be in fragment
            assertTrue(url.contains("#error=access_denied"), "URL should contain error in fragment");
            assertTrue(url.contains("error_description=Access+denied"), "URL should contain description in fragment");
            assertTrue(url.contains("state=state123"), "state should be present in fragment");
        }
    }
    
    /* ---------  Form Post response mode  --------- */
    @Test
    public void testRedirectErrorToClientFormPostMode() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        setupEndpoint(endpoint, OIDCResponseMode.FORM_POST, "https://demo.com/callback", "state123", "form_post");

        Response mockResponse = createMockResponse("https://demo.com/callback", 200);

        try (var staticMock = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder mockBuilder = mock(OIDCRedirectUriBuilder.class);
            staticMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), eq(OIDCResponseMode.FORM_POST), any(), any()))
                      .thenReturn(mockBuilder);
            when(mockBuilder.addParam(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockResponse);

            java.lang.reflect.Method m = AuthorizationEndpoint.class.getDeclaredMethod("redirectErrorToClient",
                    OIDCResponseMode.class, String.class, String.class);
            m.setAccessible(true);
            Response r = (Response) m.invoke(endpoint, OIDCResponseMode.FORM_POST,
                    "access_denied", "Access denied");

            assertEquals(Response.Status.OK.getStatusCode(), r.getStatus(), "FORM_POST should return 200 OK with form");
        }
    }
}