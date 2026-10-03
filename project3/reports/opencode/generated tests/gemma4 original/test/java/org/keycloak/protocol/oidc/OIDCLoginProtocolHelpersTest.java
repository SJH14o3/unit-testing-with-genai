package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.*;
import org.keycloak.protocol.ClientData;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.util.Time;
import org.mockito.Mockito;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OIDCLoginProtocolHelpersTest {

    private KeycloakSession session;
    private RealmModel realm;
    private UriInfo uriInfo;
    private HttpHeaders headers;
    private org.keycloak.events.EventBuilder event;
    private OIDCLoginProtocol protocol;

    // Subclass to expose protected methods
    private static class TestableOIDCLoginProtocol extends OIDCLoginProtocol {
        public TestableOIDCLoginProtocol(KeycloakSession session, RealmModel realm, UriInfo uriInfo, HttpHeaders headers, org.keycloak.events.EventBuilder event) {
            super(session, realm, uriInfo, headers, event);
        }

        // Expose protected methods
        public boolean testIsPromptLogin(AuthenticationSessionModel authSession) {
            return super.isPromptLogin(authSession);
        }

        public boolean testIsAuthTimeExpired(UserSessionModel userSession, AuthenticationSessionModel authSession) {
            return super.isAuthTimeExpired(userSession, authSession);
        }

        public boolean testIsReAuthRequiredForKcAction(UserSessionModel userSession, AuthenticationSessionModel authSession) {
            return super.isReAuthRequiredForKcAction(userSession, authSession);
        }

        // For private method testing via reflection
        public boolean testIsIdTokenAsDetachedSignature(ClientModel client) throws Exception {
            Method method = OIDCLoginProtocol.class.getDeclaredMethod("isIdTokenAsDetachedSignature", ClientModel.class);
            method.setAccessible(true);
            return (boolean) method.invoke(this, client);
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
    void testGetClientData() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getRedirectUri()).thenReturn("https://example.com/callback");
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)).thenReturn("code");
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_MODE_PARAM)).thenReturn("query");
        when(authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM)).thenReturn("state123");

        ClientData clientData = protocol.getClientData(authSession);

        assertNotNull(clientData);
        assertEquals("https://example.com/callback", clientData.getRedirectUri());
        assertEquals("code", clientData.getResponseType());
        assertEquals("query", clientData.getResponseMode());
        assertEquals("state123", clientData.getState());
    }

    @Test
    void testIsPromptLogin_True() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClientNote(OIDCLoginProtocol.PROMPT_PARAM)).thenReturn("login");
        
        assertTrue(((TestableOIDCLoginProtocol)protocol).testIsPromptLogin(authSession));
    }

    @Test
    void testIsPromptLogin_False() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClientNote(OIDCLoginProtocol.PROMPT_PARAM)).thenReturn("none");
        
        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsPromptLogin(authSession));
    }

    @Test
    void testIsAuthTimeExpired_NullUserSession() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsAuthTimeExpired(null, authSession));
    }

    @Test
    void testIsAuthTimeExpired_NoMaxAge() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClientNote(OIDCLoginProtocol.MAX_AGE_PARAM)).thenReturn(null);

        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsAuthTimeExpired(userSession, authSession));
    }

    @Test
    void testIsAuthTimeExpired_Expired() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        
        // Mock authTime to be in the past
        String authTime = "1000"; 
        when(userSession.getNote(AuthenticationManager.AUTH_TIME)).thenReturn(authTime);
        // Mock maxAge to be small
        when(authSession.getClientNote(OIDCLoginProtocol.MAX_AGE_PARAM)).thenReturn("10");
        
        // Since we can't easily mock Time.currentTime() without PowerMock or similar (which isn't specified), 
        // we rely on the fact that 1000 + 10 < current time will likely be true.
        
        assertTrue(((TestableOIDCLoginProtocol)protocol).testIsAuthTimeExpired(userSession, authSession));
    }

    @Test
    void testIsAuthTimeExpired_NotExpired() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        
        // Mock authTime to be very recent
        String authTime = String.valueOf(Time.currentTime()); 
        when(userSession.getNote(AuthenticationManager.AUTH_TIME)).thenReturn(authTime);
        when(authSession.getClientNote(OIDCLoginProtocol.MAX_AGE_PARAM)).thenReturn("3600");

        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsAuthTimeExpired(userSession, authSession));
    }

    @Test
    void testIsReAuthRequiredForKcAction_NoAction() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClientNote(Constants.KC_ACTION)).thenReturn(null);

        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsReAuthRequiredForKcAction(userSession, authSession));
    }

    @Test
    void testIsReAuthRequiredForKcAction_ProviderNotFound() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClientNote(Constants.KC_ACTION)).thenReturn("non-existent-provider");
        when(session.getProvider(RequiredActionProvider.class, "non-existent-provider")).thenReturn(null);

        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsReAuthRequiredForKcAction(userSession, authSession));
    }

    @Test
    void testIsReAuthRequiredForKcAction_Expired() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        RequiredActionProvider provider = mock(RequiredActionProvider.class);

        when(authSession.getClientNote(Constants.KC_ACTION)).thenReturn("my-provider");
        when(session.getProvider(RequiredActionProvider.class, "my-provider")).thenReturn(provider);
        when(userSession.getNote(AuthenticationManager.AUTH_TIME)).thenReturn("1000");
        when(provider.getMaxAuthAge(session)).thenReturn(10);

        assertTrue(((TestableOIDCLoginProtocol)protocol).testIsReAuthRequiredForKcAction(userSession, authSession));
    }

    @Test
    void testIsReAuthRequiredForKcAction_NotExpired() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        RequiredActionProvider provider = mock(RequiredActionProvider.class);

        when(authSession.getClientNote(Constants.KC_ACTION)).thenReturn("my-provider");
        when(session.getProvider(RequiredActionProvider.class, "my-provider")).thenReturn(provider);
        when(userSession.getNote(AuthenticationManager.AUTH_TIME)).thenReturn(String.valueOf(Time.currentTime()));
        when(provider.getMaxAuthAge(session)).thenReturn(3600);

        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsReAuthRequiredForKcAction(userSession, authSession));
    }

    @Test
    void testRequireReauthentication_AllFalse() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        
        // isPromptLogin -> false
        when(authSession.getClientNote(OIDCLoginProtocol.PROMPT_PARAM)).thenReturn(null);
        // isAuthTimeExpired -> false
        when(authSession.getClientNote(OIDCLoginProtocol.MAX_AGE_PARAM)).thenReturn(null);
        // isReAuthRequiredForKcAction -> false
        when(authSession.getClientNote(Constants.KC_ACTION)).thenReturn(null);

        assertFalse(protocol.requireReauthentication(userSession, authSession));
    }

    @Test
    void testRequireReauthentication_PromptLoginTrue() {
        UserSessionModel userSession = mock(UserSessionModel.class);
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        
        when(authSession.getClientNote(OIDCLoginProtocol.PROMPT_PARAM)).thenReturn("login");

        assertTrue(protocol.requireReauthentication(userSession, authSession));
    }

    @Test
    void testIsIdTokenAsDetachedSignature_True() throws Exception {
        ClientModel client = mock(ClientModel.class);
        when(client.getAttribute(OIDCConfigAttributes.ID_TOKEN_AS_DETACHED_SIGNATURE)).thenReturn("true");

        assertTrue(((TestableOIDCLoginProtocol)protocol).testIsIdTokenAsDetachedSignature(client));
    }

    @Test
    void testIsIdTokenAsDetachedSignature_False() throws Exception {
        ClientModel client = mock(ClientModel.class);
        when(client.getAttribute(OIDCConfigAttributes.ID_TOKEN_AS_DETACHED_SIGNATURE)).thenReturn("false");

        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsIdTokenAsDetachedSignature(client));
    }

    @Test
    void testIsIdTokenAsDetachedSignature_NullClient() throws Exception {
        assertFalse(((TestableOIDCLoginProtocol)protocol).testIsIdTokenAsDetachedSignature(null));
    }
}
