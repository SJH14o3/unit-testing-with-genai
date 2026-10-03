package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationSessionManager;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.constants.Constants;
import org.keycloak.models.*;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.oidc.endpoints.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.representations.idm.OAuth2ErrorRepresentation;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.util.OAuth2Constants;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OIDCLoginProtocolErrorTest {

    private KeycloakSession session;
    private RealmModel realm;
    private UriInfo uriInfo;
    private HttpHeaders headers;
    private org.keycloak.events.EventBuilder event;
    private OIDCLoginProtocol protocol;

    private static class TestableOIDCLoginProtocol extends OIDCLoginProtocol {
        public TestableOIDCLoginProtocol(KeycloakSession session, RealmModel realm, UriInfo uriInfo, HttpHeaders headers, org.keycloak.events.EventBuilder event) {
            super(session, realm, uriInfo, headers, event);
        }

        public OAuth2ErrorRepresentation testTranslateError(Error error, String errorMessage) throws Exception {
            Method method = OIDCLoginProtocol.class.getDeclaredMethod("translateError", Error.class, String.class);
            method.setAccessible(true);
            return (OAuth2ErrorRepresentation) method.invoke(this, error, errorMessage);
        }

        public Response testBuildErrorRedirectUri(OIDCRedirectUriBuilder redirectUri, AuthenticationSessionModel authSession, UserSessionModel userSession, ClientSessionContext clientSessionCtx, Exception ex, Error oidcError) {
            // The original method is private but we want to test it via sendError or just through reflection
            // However, since buildErrorRedirectUri is private, we'll test it through the public sendError or use reflection
            return null; // Placeholder, we will use reflection if needed
        }
        
        public Response callBuildErrorRedirectUri(OIDCRedirectUriBuilder redirectUri, AuthenticationSessionModel authSession, UserSessionModel userSession, ClientSessionContext clientSessionCtx, Exception ex, Error oidcError) throws Exception {
            Method method = OIDCLoginProtocol.class.getDeclaredMethod("buildErrorRedirectUri", OIDCRedirectUriBuilder.class, AuthenticationSessionModel.class, UserSessionModel.class, ClientSessionContext.class, Exception.class, Error.class);
            method.setAccessible(true);
            return (Response) method.invoke(this, redirectUri, authSession, userSession, clientSessionCtx, ex, oidcError);
        }
    }

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        realm = mock(RealmModel.class);
        uriInfo = mock(UriInfo.class);
        headers = mock(HttpHeaders.class);
        event = mock(org.keycloak.events.EventBuilder.class);
        protocol = new TestableOIDCLoginProtocol(session, realm, uriInfo, headers, event);
    }

    @Test
    void testTranslateError_CancelledAIA() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.CANCELLED_AIA, "msg");
        assertNull(res.getError());
        assertNull(res.getErrorDescription());
    }

    @Test
    void testTranslateError_CancelledAIA_Silent() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.CANCELLED_AIA_SILENT, "msg");
        assertNull(res.getError());
        assertNull(res.getErrorDescription());
    }

    @Test
    void testTranslateError_CancelledByUser() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.CANCELLED_BY_USER, "User cancelled");
        assertEquals(org.keycloak.OAuthErrorException.ACCESS_DENIED, res.getError());
        assertEquals("User cancelled", res.getErrorDescription());
    }

    @Test
    void testTranslateError_ConsentDenied() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.CONSENT_DENIED, "No consent");
        assertEquals(org.keycloak.OAuthErrorException.ACCESS_DENIED, res.getError());
        assertEquals("No consent", res.getErrorDescription());
    }

    @Test
    void testTranslateError_LoaInvalid() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.LOA_INVALID, "Bad LoA");
        assertEquals(org.keycloak.OAuthErrorException.ACCESS_DENIED, res.getError());
        assertEquals("Bad LoA", res.getErrorDescription());
    }

    @Test
    void testTranslateError_PassiveInteractionRequired() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.PASSIVE_INTERACTION_REQUIRED, "msg");
        assertEquals(org.keycloak.OAuthErrorException.INTERACTION_REQUIRED, res.getError());
        assertNull(res.getErrorDescription());
    }

    @Test
    void testTranslateError_PassiveLoginRequired() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.PASSIVE_LOGIN_REQUIRED, "msg");
        assertEquals(org.keycloak.OAuthErrorException.LOGIN_REQUIRED, res.getError());
        assertNull(res.getErrorDescription());
    }

    @Test
    void testTranslateError_AlreadyLoggedIn() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.ALREADY_LOGGED_IN, "msg");
        assertEquals(org.keycloak.OAuthErrorException.TEMPORARILY_UNAVAILABLE, res.getError());
        assertEquals(Constants.AUTHENTICATION_EXPIRED_MESSAGE, res.getErrorDescription());
    }

    @Test
    void testTranslateError_Default() throws Exception {
        OAuth2ErrorRepresentation res = ((TestableOIDCLoginProtocol)protocol).testTranslateError(LoginProtocol.Error.SOME_OTHER_ERROR, "msg");
        assertEquals(org.keycloak.OAuthErrorException.SERVER_ERROR, res.getError());
        assertNull(res.getErrorDescription());
    }

    @Test
    void testSendError_StandardFlow() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)).thenReturn("code");
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_MODE_PARAM)).thenReturn("query");
        when(authSession.getRedirectUri()).thenReturn("https://example.com/callback");
        when(authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM)).thenReturn("state");

        try (MockedStatic<OIDCRedirectUriBuilder> mockedBuilder = mockStatic(OIDCRedirectUriBuilder.class);
             MockedStatic<AuthenticationSessionManager> mockedManager = mockStatic(AuthenticationSessionManager.class)) {
            
            OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
            mockedBuilder.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any())).thenReturn(builder);
            when(builder.addParam(anyString(), anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(Response.ok().build());

            Response response = protocol.sendError(authSession, LoginProtocol.Error.CONSENT_DENIED, "No consent");

            assertNotNull(response);
            verify(builder).addParam(OAuth2Constants.ERROR, org.keycloak.OAuthErrorException.ACCESS_DENIED.toString());
            verify(builder).addParam(OAuth2Constants.ERROR_DESCRIPTION, "No consent");
            verify(builder).addParam(OIDCLoginProtocol.STATE_PARAM, "state");
        }
    }

    @Test
    void testSendError_ClientDataFlow() {
        ClientModel client = mock(ClientModel.class);
        ClientData clientData = mock(ClientData.class);
        when(client.getClientId()).thenReturn("test-client");
        when(clientData.getResponseType()).thenReturn("code");
        when(clientData.getResponseMode()).thenReturn("query");
        when(clientData.getRedirectUri()).thenReturn("https://example.com/callback");
        when(clientData.getState()).thenReturn("state");

        try (MockedStatic<OIDCRedirectUriBuilder> mockedBuilder = mockStatic(OIDCRedirectUriBuilder.class)) {
            OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
            mockedBuilder.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any())).thenReturn(builder);
            when(builder.addParam(anyString(), anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(Response.ok().build());

            Response response = protocol.sendError(client, clientData, LoginProtocol.Error.CONSENT_DENIED);

            assertNotNull(response);
            verify(builder).addParam(OAuth2Constants.ERROR, org.keycloak.OAuthErrorException.ACCESS_DENIED.toString());
        }
    }

    @Test
    void testBuildRedirectUri_Overload1() {
        OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
        when(builder.build()).thenReturn(Response.ok().build());

        Response response = protocol.buildRedirectUri(builder, null, null, null);

        assertNotNull(response);
        verify(builder).build();
    }

    @Test
    void testBuildRedirectUri_Overload2() {
        OIDCRedirectUriBuilder builder = mock(OIDCRedirectUriBuilder.class);
        when(builder.build()).thenReturn(Response.ok().build());

        Response response = protocol.buildRedirectUri(builder, null, null, null, null, null);

        assertNotNull(response);
        verify(builder).build();
    }
}
