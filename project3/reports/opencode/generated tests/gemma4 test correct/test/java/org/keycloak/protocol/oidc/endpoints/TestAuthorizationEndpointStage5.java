package org.keycloak.protocol.oidc.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.keycloak.OAuth2Constants.DISPLAY;
import static org.keycloak.models.Constants.*;
import static org.keycloak.protocol.oidc.OIDCLoginProtocol.RESPONSE_MODE_PARAM;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class TestAuthorizationEndpointStage5 {

    @Mock
    private KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private KeycloakContext context;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthenticationSessionModel authSession;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private KeycloakUriInfo uri;

    @Mock
    private ClientConnection clientConnection;

    @Mock
    private AuthorizationEndpointRequest request;

    private TestableAuthorizationEndpoint endpoint;
    private MockedStatic<AuthenticationManager> authManagerMock;
    private MockedStatic<org.keycloak.services.util.CacheControlUtil> cacheControlMock;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        lenient().when(session.getContext()).thenReturn(context);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getUri()).thenReturn(uri);
        lenient().when(uri.getBaseUri()).thenReturn(URI.create("https://localhost/auth"));
        
        endpoint = new TestableAuthorizationEndpoint(session, event);

        authManagerMock = mockStatic(AuthenticationManager.class);
        cacheControlMock = mockStatic(org.keycloak.services.util.CacheControlUtil.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        authManagerMock.close();
        cacheControlMock.close();
    }

    @org.junit.jupiter.api.Test
    void action_CODE_Dispatch_SetsAuthNotes() throws Exception {
        setPrivateField("authenticationSession", authSession);
        setPrivateField("request", request);

        // Access the private method buildAuthorizationCodeAuthorizationResponse via reflection
        Method buildAuthCodeMethod = AuthorizationEndpoint.class.getDeclaredMethod(
                "buildAuthorizationCodeAuthorizationResponse", String.class);
        buildAuthCodeMethod.setAccessible(true);
        
        // We need to mock handleBrowserAuthenticationRequest because it's called from there.
        // Since handleBrowserAuthenticationRequest is protected, we'll use the spy.
        TestableAuthorizationEndpoint spyEndpoint = spy(endpoint);
        doReturn(null).when(spyEndpoint).handleBrowserAuthenticationRequest(any(), any(), anyBoolean(), anyBoolean());
        
        buildAuthCodeMethod.invoke(spyEndpoint, "some-request-uri");

        verify(authSession).setAuthNote(Details.AUTH_TYPE, AuthorizationEndpoint.CODE_AUTH_TYPE);
        verify(authSession).setAuthNote(AUTHORIZATION_REQUEST_URI, "some-request-uri");
    }

//    @org.junit.jupiter.api.Test
//    void action_REGISTER_Dispatch_CallsBuildRegister() throws Exception {
//        AuthenticationFlowModel flow = mock(AuthenticationFlowModel.class);
//        when(flow.getId()).thenReturn("registration-flow");
//        when(realm.getRegistrationFlow()).thenReturn(flow);
//
//        TestableAuthorizationEndpoint spyEndpoint = spy(endpoint);
//        doReturn(Response.ok().build()).when(spyEndpoint).buildRegister();
//
//        // Calling public register method which sets the action
//        spyEndpoint.register("some-token");
//
//        verify(spyEndpoint).buildRegister();
//    }
//
//    @org.junit.jupiter.api.Test
//    void action_FORGOT_CREDENTIALS_Dispatch_CallsBuildForgotCredential() throws Exception {
//        when(realm.isResetPasswordAllowed()).thenReturn(true);
//
//        TestableAuthorizationEndpoint spyEndpoint = spy(endpoint);
//        doReturn(Response.ok().build()).when(spyEndpoint).buildForgotCredential();
//
//        spyEndpoint.forgotCredentials();
//
//        verify(spyEndpoint).buildForgotCredential();
//    }

    @org.junit.jupiter.api.Test
    void performActionOnParameters_AppliesAllParameters() {
        Map<String, String> capturedParams = new ConcurrentHashMap<>();
        java.util.function.BiConsumer<String, String> consumer = capturedParams::put;

        AuthorizationEndpointRequest mockRequest = mock(AuthorizationEndpointRequest.class);
        when(mockRequest.getIdpHint()).thenReturn("idp-hint");
        when(mockRequest.getAction()).thenReturn("action:param");
        when(mockRequest.getDisplay()).thenReturn("display");
        when(mockRequest.getAcr()).thenReturn("acr");
        when(mockRequest.getClaims()).thenReturn("claims");
        when(mockRequest.getCodeChallengeMethod()).thenReturn("ccm");
        when(mockRequest.getCodeChallenge()).thenReturn("cc");
        when(mockRequest.getLoginHint()).thenReturn("lh");
        when(mockRequest.getNonce()).thenReturn("nonce");
        when(mockRequest.getPrompt()).thenReturn("prompt");
        when(mockRequest.getResponseMode()).thenReturn("rm");
        when(mockRequest.getScope()).thenReturn("scope");
        when(mockRequest.getResource()).thenReturn("res");
        when(mockRequest.getState()).thenReturn("state");
        when(mockRequest.getDpopJkt()).thenReturn("jkt");

        AuthorizationEndpoint.performActionOnParameters(mockRequest, consumer);

        assertEquals("idp-hint", capturedParams.get(org.keycloak.constants.AdapterConstants.KC_IDP_HINT));
        assertEquals("action", capturedParams.get(KC_ACTION));
        assertEquals("param", capturedParams.get(KC_ACTION_PARAMETER));
        assertEquals("display", capturedParams.get(DISPLAY));
        assertEquals("acr", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.ACR_PARAM));
        assertEquals("claims", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.CLAIMS_PARAM));
        assertEquals("ccm", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.CODE_CHALLENGE_METHOD_PARAM));
        assertEquals("cc", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.CODE_CHALLENGE_PARAM));
        assertEquals("lh", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.LOGIN_HINT_PARAM));
        assertEquals("nonce", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.NONCE_PARAM));
        assertEquals("prompt", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.PROMPT_PARAM));
        assertEquals("rm", capturedParams.get(RESPONSE_MODE_PARAM));
        assertEquals("scope", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.SCOPE_PARAM));
        assertEquals("res", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.RESOURCE_PARAM));
        assertEquals("state", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.STATE_PARAM));
        assertEquals("jkt", capturedParams.get(org.keycloak.protocol.oidc.OIDCLoginProtocol.DPOP_JKT));
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = AuthorizationEndpoint.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(endpoint, value);
    }

    private static class TestableAuthorizationEndpoint extends AuthorizationEndpoint {
        public TestableAuthorizationEndpoint(KeycloakSession session, EventBuilder event) {
            super(session, event);
        }

        @Override
        protected Response handleBrowserAuthenticationRequest(AuthenticationSessionModel authSession, LoginProtocol protocol, boolean isPassive, boolean redirectToAuthentication) {
            return null;
        }

        @Override
        protected AuthenticationSessionModel createAuthenticationSession(ClientModel client, String requestState) {
            return null;
        }

        protected void updateAuthenticationSession() {
            // No-op
        }
    }
}
