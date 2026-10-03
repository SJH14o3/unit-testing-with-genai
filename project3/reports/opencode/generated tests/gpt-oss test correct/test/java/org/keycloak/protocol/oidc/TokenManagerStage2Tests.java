package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.organization.protocol.mappers.oidc.OrganizationScope;
import org.keycloak.cookie.CookieProvider;
import org.keycloak.protocol.ProtocolMapper;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.models.*;
import org.keycloak.representations.*;
import org.keycloak.common.VerificationException;
import org.keycloak.services.util.*;
import org.keycloak.authentication.authenticators.util.AcrStore;
import jakarta.ws.rs.core.*;
import org.keycloak.jose.jws.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage2Tests {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock UserModel user;
    @Mock UserSessionModel userSession;
    @Mock
    ClientSessionContext clientSession;
    @Mock  AuthenticatedClientSessionModel authenticatedClientSessionModel;
    @Mock HttpHeaders headers;
    @Mock UriInfo uriInfo;
    @Mock org.keycloak.models.TokenManager tokenProvider;
    @Mock KeycloakContext context;
    @Mock KeycloakSessionFactory sessionFactory;
    @Mock UserSessionProvider sessions;
    @Mock AuthenticationSessionProvider authenticationSessions;

    @BeforeAll
    static void initProfile() {
        Profile.defaults();
    }

    TokenManager manager = new TokenManager();

    /*------------------- verifyIDToken & verifyIDTokenSignature -------------------*/
    @Test
    void verifyIDToken_valid() throws VerificationException, OAuthErrorException {
        IDToken idToken = mock(IDToken.class);
        when(session.tokens()).thenReturn(tokenProvider);
        when(tokenProvider.decode(anyString(), eq(IDToken.class))).thenReturn(idToken);
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            TokenVerifier<IDToken> verifier = mock(TokenVerifier.class);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(idToken)).thenReturn(verifier);
            when(verifier.withChecks(any(), any())).thenReturn(verifier);
            verifier.verify(); // succeed
            assertNotNull(manager.verifyIDToken(session, realm, "encoded"));
        }
    }

    @Test
    void verifyIDToken_invalid() throws VerificationException {
        IDToken idToken = mock(IDToken.class);
        when(session.tokens()).thenReturn(tokenProvider);
        when(tokenProvider.decode(anyString(), eq(IDToken.class))).thenReturn(idToken);
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            TokenVerifier<IDToken> verifier = mock(TokenVerifier.class);
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(idToken)).thenReturn(verifier);
            when(verifier.withChecks(any(), any())).thenReturn(verifier);
            doThrow(new VerificationException("invalid")).when(verifier).verify();
            OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
                manager.verifyIDToken(session, realm, "encoded");
            });
            assertEquals("invalid", ex.getMessage());
        }
    }

    @Test
    void verifyIDTokenSignature_nullToken() {
        when(session.tokens()).thenReturn(tokenProvider);
        when(tokenProvider.decode(anyString(), eq(IDToken.class))).thenReturn(null);
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyIDTokenSignature(session, "encoded");
        });
        assertEquals("Invalid IDToken", ex.getMessage());
    }

    /*------------------- createClientAccessToken -------------------*/
    @Test
    void createClientAccessToken_callsTransform() {
        AccessToken initToken = mock(AccessToken.class);
        AccessToken transformed = mock(AccessToken.class);
        TokenManager spy = Mockito.spy(manager);
        doReturn(initToken).when(spy).initToken(any(), any(), any(), any(), any(), any(), anyBoolean());
        doReturn(transformed).when(spy).transformAccessToken(any(), eq(initToken), any(), any());
        AccessToken result = spy.createClientAccessToken(session, realm, client, user, userSession, clientSession, false);
        assertEquals(transformed, result);
    }

    /*------------------- attachAuthenticationSession -------------------*/
    @Test
    void attachAuthenticationSession_callsSetters() {
        AuthenticationSessionModel authSession = mock(AuthenticationSessionModel.class);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect");
        when(authSession.getProtocol()).thenReturn("openid-connect");
        when(authSession.getClientNote(anyString())).thenReturn("scopeParam");
        when(authSession.getClientNotes()).thenReturn(Map.of());
        when(authSession.getUserSessionNotes()).thenReturn(Map.of());

        // the client session does not exist yet, so a new one is created for the user session
        when(session.getContext()).thenReturn(context);
        when(context.getClient()).thenReturn(client);
        doReturn(Set.of()).when(session).getAttributeOrDefault(anyString(), any());
        when(client.getId()).thenReturn("client-uuid");
        when(userSession.getRealm()).thenReturn(realm);
        when(userSession.getUser()).thenReturn(user);
        when(session.sessions()).thenReturn(sessions);
        AuthenticatedClientSessionModel newClientSession = mock(AuthenticatedClientSessionModel.class);
        when(sessions.createClientSession(realm, client, userSession)).thenReturn(newClientSession);

        // the authentication session (and its whole root session) is removed after the successful authentication
        RootAuthenticationSessionModel rootAuthSession = mock(RootAuthenticationSessionModel.class);
        when(authSession.getParentSession()).thenReturn(rootAuthSession);
        when(rootAuthSession.getAuthenticationSessions()).thenReturn(Map.of());
        when(session.authenticationSessions()).thenReturn(authenticationSessions);
        when(session.getProvider(CookieProvider.class)).thenReturn(mock(CookieProvider.class));
        when(session.getProvider(LoginFormsProvider.class)).thenReturn(mock(LoginFormsProvider.class));

        ClientSessionContext ctx = TokenManager.attachAuthenticationSession(session, userSession, authSession,
                Collections.emptySet(), false);
        assertNotNull(ctx);
        verify(authSession).getClient();
        verify(authSession).getRedirectUri();
        verify(newClientSession).setRedirectUri("http://redirect");
        verify(newClientSession).setProtocol("openid-connect");
    }

    /*------------------- detachClientSession -------------------*/
    @Test
    void detachClientSession_callsDetach() {
        when(authenticatedClientSessionModel.getUserSession()).thenReturn(userSession);
        TokenManager.detachClientSession(authenticatedClientSessionModel);
        verify(authenticatedClientSessionModel).detachFromUserSession();
    }

    /*------------------- getAccess fullScope --------------------------------*/
    @Test
    void getAccess_fullScope_returnsAllRoles() {
        RoleModel role1 = mock(RoleModel.class);
        RoleModel role2 = mock(RoleModel.class);
        when(client.isFullScopeAllowed()).thenReturn(true);
        when(user.getRoleMappingsStream()).thenReturn(Stream.of(role1, role2));
        Set<RoleModel> roles = TokenManager.getAccess(user, client, Stream.empty());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
    }

    /*------------------- isValidScope invalid scopes ---------------------*/
    @Test
    void isValidScope_invalid_rawScopes() {
        String badScopes = "invalidScope"; // not a recognized scope
        when(session.getContext()).thenReturn(context);
        when(context.getClient()).thenReturn(client);
        doReturn(Set.of()).when(session).getAttributeOrDefault(anyString(), any());
        assertFalse(TokenManager.isValidScope(session, badScopes, null, client, null));
    }

    /*------------------- isRepeatableScope default -------------------------*/
    @Test
    void isRepeatableScope_default_true() {
        ClientScopeModel scope = mock(ClientScopeModel.class);
        when(scope.getAttribute(anyString())).thenReturn(null);
        assertTrue(TokenManager.isRepeatableScope(session, scope));
    }

    /*------------------- transformAccessToken simple mapper ----------------- */
    @Test
    void transformAccessToken_appliesMappers() {
        AccessToken token = mock(AccessToken.class);
        AccessToken token2 = mock(AccessToken.class);

        ProtocolMapperModel model = mock(ProtocolMapperModel.class);
        when(model.getProtocolMapper()).thenReturn("test-access-token-mapper");
        // the mapper is resolved through the session factory, so it has to be a protocol mapper and a provider factory
        OIDCAccessTokenMapper mapper = mock(OIDCAccessTokenMapper.class,
                withSettings().extraInterfaces(ProtocolMapper.class, org.keycloak.provider.ProviderFactory.class));
        when(mapper.transformAccessToken(eq(token), eq(model), eq(session), eq(userSession), eq(clientSession)))
                .thenReturn(token2);

        KeycloakSessionFactory factory = mock(KeycloakSessionFactory.class);
        when(factory.getProviderFactory(eq(ProtocolMapper.class), eq("test-access-token-mapper")))
                .thenReturn((org.keycloak.provider.ProviderFactory) mapper);
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        when(clientSession.getProtocolMappersStream()).thenReturn(Stream.of(model));
        when(clientSession.getClientSession()).thenReturn(authenticatedClientSessionModel);
        when(authenticatedClientSessionModel.getClient()).thenReturn(client);
        // a client with another protocol than OIDC does not get the transient DPoP/mTLS protocol mappers
        when(client.getProtocol()).thenReturn("saml");

        AccessToken result = manager.transformAccessToken(session, token, userSession, clientSession);
        assertEquals(token2, result);
    }

    /*------------------- generateUserInfoClaims -------------------------------*/
    @Test
    void generateUserInfoClaims_basic() {
        AccessToken token = mock(AccessToken.class);
        UserModel u = mock(UserModel.class);
        when(token.getSubject()).thenReturn("sub-123");
        when(token.getName()).thenReturn("John");
        when(u.getId()).thenReturn("user-123");
        Map<String,Object> map = manager.generateUserInfoClaims(token, u);
        assertEquals("sub-123", map.get("sub"));
        assertEquals("John", map.get("name"));
    }
}
