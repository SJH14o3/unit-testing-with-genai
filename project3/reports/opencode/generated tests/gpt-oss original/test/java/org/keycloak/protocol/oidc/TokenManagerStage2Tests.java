import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
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

@ExtendWith(MockitoExtension.class)
class TokenManagerStage2Tests {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock UserModel user;
    @Mock UserSessionModel userSession;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock HttpHeaders headers;
    @Mock UriInfo uriInfo;
    @Mock ClientConnection connection;
    @Mock HttpRequest request;
    @Mock AdvancedConfigWrapper advanced;
    @Mock TokenProvider tokenProvider;
    @Mock ProtocolMapperUtils protocolMapperUtils;

    TokenManager manager = new TokenManager();

    /*------------------- verifyIDToken & verifyIDTokenSignature -------------------*/
    @Test
    void verifyIDToken_valid() {
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
    void verifyIDToken_invalid() {
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
        OAuthErrorException ex = assertThrows(OAuthErrorException.class, () -> {
            manager.verifyIDTokenSignature(session, null);
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

        doNothing().when(session).registerProvider(eq(MetadataProvider.class));
        ClientSessionContext ctx = TokenManager.attachAuthenticationSession(session, userSession, authSession, 
                Collections.emptySet(), false);
        assertNotNull(ctx);
        verify(authSession).getClient();
        verify(authSession).getRedirectUri();
    }

    /*------------------- detachClientSession -------------------*/
    @Test
    void detachClientSession_callsDetach() {
        when(clientSession.getUserSession()).thenReturn(userSession);
        TokenManager.detachClientSession(clientSession);
        verify(clientSession).detachFromUserSession();
    }

    /*------------------- getAccess fullScope --------------------------------*/
    @Test
    void getAccess_fullScope_returnsAllRoles() {
        RoleModel role1 = mock(RoleModel.class);
        RoleModel role2 = mock(RoleModel.class);
        when(client.isFullScopeAllowed()).thenReturn(true);
        when(client.getRolesStream()).thenReturn(Stream.of(role1, role2));
        Set<RoleModel> roles = TokenManager.getAccess(user, client, Stream.empty());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
    }

    /*------------------- isValidScope invalid scopes ---------------------*/
    @Test
    void isValidScope_invalid_rawScopes() {
        String badScopes = "invalidScope"; // not a recognized scope
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
        ProtocolMapper mapper = mock(ProtocolMapper.class);
        ProtocolMapperModel model = mock(ProtocolMapperModel.class);
        Map.Entry<ProtocolMapperModel,ProtocolMapper> entry = new AbstractMap.SimpleEntry<>(model, mapper);
        List<Map.Entry<ProtocolMapperModel,ProtocolMapper>> list = List.of(entry);
        doReturn(list).when(protocolMapperUtils).getSortedProtocolMappers(eq(session), eq(clientSession), argThat(p -> p instanceof OIDCAccessTokenMapper));
        // set up mapper logic
        doAnswer(inv -> token2).when(mapper).transformAccessToken(eq(token), eq(model), any(), any(), any());
        AccessToken result = TokenManager.transformAccessToken(session, token, userSession, clientSession);
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

    /*------------------- tryResolveOrganizationScope ------------------------*/
    @Test
    void tryResolveOrganizationScope_enabled() {
        String scopeParam = "org:read";
        OrganizationScope scope = mock(OrganizationScope.class);
        try (MockedStatic<Profile> pStatic = mockStatic(Profile.class)) {
            pStatic.when(() -> Profile.isFeatureEnabled(Profile.Feature.ORGANIZATION)).thenReturn(true);
            try (MockedStatic<OrganizationScope> oStatic = mockStatic(OrganizationScope.class)) {
                oStatic.when(() -> OrganizationScope.valueOfScope(session, scopeParam)).thenReturn(scope);
                OrganizationScope result = TokenManager.tryResolveOrganizationScope(session, scopeParam, user);
                assertEquals(scope, result);
            }
        }
    }

    @Test
    void tryResolveOrganizationScope_disabled() {
        String scopeParam = "org:read";
        try (MockedStatic<Profile> pStatic = mockStatic(Profile.class)) {
            pStatic.when(() -> Profile.isFeatureEnabled(Profile.Feature.ORGANIZATION)).thenReturn(false);
            OrganizationScope result = TokenManager.tryResolveOrganizationScope(session, scopeParam, user);
            assertNull(result);
        }
    }

    /*------------------- tryResolveOrganizationClientScope null org -------- */
    @Test
    void tryResolveOrganizationClientScope_nullOrg() {
        String name = "myScope";
        ClientScopeModel csm = mock(ClientScopeModel.class);
        OrganizationScope orgScope = null;
        OrganizationScope result = TokenManager.tryResolveOrganizationClientScope(session, user, orgScope, name);
        assertNull(result);
    }
}
