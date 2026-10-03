package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuthErrorException;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.refresh.RefreshTokenException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.util.DPoPUtil;
import org.keycloak.services.util.MtlsHoKTokenUtil;
import org.keycloak.util.JWKSUtils;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.UriInfo;
import org.keycloak.http.HttpRequest;
import org.keycloak.representations.dpop.DPoP;
import org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCAResult;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerLifecycleSecurityTest {

    @Mock
    KeycloakSession session;
    @Mock
    RealmModel realm;
    @Mock
    ClientModel client;
    @Mock
    HttpRequest request;
    @Mock
    AccessToken refreshToken;
    @Mock
    AuthenticatedClientSessionModel clientSession;
    @Mock
    UserSessionModel userSession;
    @Mock
    UserModel user;
    @Mock
    KeycloakUriInfo uriInfo;
    @Mock
    org.keycloak.models.TokenManager tokens;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager = new TokenManager();
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);
        when(context.getUri()).thenReturn(uriInfo);
        when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost/"));
        when(realm.getName()).thenReturn("realm");
        when(client.getRealm()).thenReturn(realm);
    }

    @Test
    void validateTokenReuse_StaleToken_ThrowsException() {
        when(refreshToken.getId()).thenReturn("token-id-1");
        when(refreshToken.getIat()).thenReturn(100L);
        when(clientSession.getRefreshToken(anyString())).thenReturn("token-id-2");
        when(clientSession.getRefreshTokenLastRefresh(anyString())).thenReturn(200);

        assertThrows(OAuthErrorException.class, () -> 
            tokenManager.validateTokenReuse(session, realm, refreshToken, clientSession, false));
    }

    @Test
    void validateTokenReuse_NewToken_WithRefreshFlag_UpdatesSession() throws OAuthErrorException {
        when(refreshToken.getId()).thenReturn("new-token-id");
        when(clientSession.getRefreshToken(anyString())).thenReturn("old-token-id");
        when(clientSession.getRefreshTokenUseCount(anyString())).thenReturn(0);
        when(realm.getRefreshTokenMaxReuse()).thenReturn(5);

        tokenManager.validateTokenReuse(session, realm, refreshToken, clientSession, true);

        verify(clientSession).setRefreshToken(anyString(), eq("new-token-id"));
        verify(clientSession).setRefreshTokenUseCount(anyString(), eq(0));
    }

    @Test
    void validateTokenReuse_MaxReuseExceeded_ThrowsException() {
        when(refreshToken.getId()).thenReturn("token-id");
        when(clientSession.getRefreshToken(anyString())).thenReturn("token-id");
        when(clientSession.getRefreshTokenUseCount(anyString())).thenReturn(10);
        when(realm.getRefreshTokenMaxReuse()).thenReturn(5);

        assertThrows(OAuthErrorException.class, () -> 
            tokenManager.validateTokenReuse(session, realm, refreshToken, clientSession, false));
    }

    @Test
    void verifyRefreshToken_InvalidType_ThrowsException() throws JWSInputException, OAuthErrorException {
        RefreshToken rt = mock(RefreshToken.class);
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(RefreshToken.class))).thenReturn(rt);
        when(rt.getType()).thenReturn("invalid-type");

        assertThrows(OAuthErrorException.class, () -> 
            tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
    }

    @Test
    void verifyRefreshToken_ClientMismatch_ThrowsException() throws JWSInputException, OAuthErrorException, VerificationException {
        RefreshToken rt = mock(RefreshToken.class);
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(RefreshToken.class))).thenReturn(rt);
        when(rt.getType()).thenReturn("Refresh");
        when(client.getClientId()).thenReturn("client-a");
        when(rt.getIssuedFor()).thenReturn("client-b");

        // We need to mock TokenVerifier because it's static and used in verifyRefreshToken
        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class)) {
            TokenVerifier<RefreshToken> verifier = new TestTokenVerifier<>(rt);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
        }
    }

    @Test
    void verifyRefreshToken_MtlsSuccess() throws JWSInputException, OAuthErrorException, VerificationException {
        RefreshToken rt = mock(RefreshToken.class);
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(RefreshToken.class))).thenReturn(rt);
        when(rt.getType()).thenReturn("Refresh");
        when(client.getClientId()).thenReturn("client-id");
        when(rt.getIssuedFor()).thenReturn("client-id");

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class);
             MockedStatic<OIDCAdvancedConfigWrapper> configMock = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedStatic<MtlsHoKTokenUtil> mtlsMock = mockStatic(MtlsHoKTokenUtil.class)) {
            
            TokenVerifier<RefreshToken> verifier = new TestTokenVerifier<>(rt);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);

            OIDCAdvancedConfigWrapper wrapper = mock(OIDCAdvancedConfigWrapper.class);
            configMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(wrapper);
            when(wrapper.isUseMtlsHokToken()).thenReturn(true);
            
            mtlsMock.when(() -> MtlsHoKTokenUtil.verifyTokenBindingWithClientCertificate(any(), any(), any())).thenReturn(true);

            RefreshToken result = tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true);
            assertNotNull(result);
        }
    }

    @Test
    void verifyRefreshToken_DPoPSuccess() throws JWSInputException, OAuthErrorException, VerificationException {
        RefreshToken rt = mock(RefreshToken.class);
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(RefreshToken.class))).thenReturn(rt);
        when(rt.getType()).thenReturn("Refresh");
        when(client.getClientId()).thenReturn("client-id");
        when(rt.getIssuedFor()).thenReturn("client-id");

        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(rt.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumbprint");
        when(cnf.getJktType()).thenReturn("DPoP");

        DPoP dpop = mock(DPoP.class);
        when(session.getAttribute(eq(DPoPUtil.DPOP_SESSION_ATTRIBUTE), eq(DPoP.class))).thenReturn(dpop);

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class);
             MockedStatic<DPoPUtil> dpopUtilMock = mockStatic(DPoPUtil.class)) {
            
            TokenVerifier<RefreshToken> verifier = new TestTokenVerifier<>(rt);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);

            RefreshToken result = tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true);
            assertNotNull(result);
            dpopUtilMock.verify(() -> DPoPUtil.validateBinding(eq(rt), eq(dpop)));
        }
    }

    @Test
    void verifyIDToken_Success() throws OAuthErrorException, JWSInputException, VerificationException {
        IDToken idToken = mock(IDToken.class);
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(IDToken.class))).thenReturn(idToken);

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class)) {
            TokenVerifier<IDToken> verifier = new TestTokenVerifier<>(idToken);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);

            IDToken result = tokenManager.verifyIDToken(session, realm, "encoded");
            assertEquals(idToken, result);
        }
    }

    @Test
    void verifyIDTokenSignature_NullToken_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(tokens);
        when(tokens.decode(anyString(), eq(IDToken.class))).thenReturn(null);

        assertThrows(OAuthErrorException.class, () -> tokenManager.verifyIDTokenSignature(session, "encoded"));
    }

    private static class TestTokenVerifier<T extends JsonWebToken> extends TokenVerifier<T> {
        private TestTokenVerifier(T token) {
            super(token);
        }

        @Override
        public TokenVerifier<T> verify() {
            return this;
        }
    }
}
