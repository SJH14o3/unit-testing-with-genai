package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.OAuthErrorException;
import org.keycloak.authentication.authenticators.client.AttestationBasedClientAuthenticator.ABCAResult;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.TokenUtil;
import org.keycloak.crypto.JWK;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.*;
import org.keycloak.models.utils.UserSessionUtil;
import org.keycloak.organization.protocol.mappers.oidc.OrganizationScope;
import org.keycloak.organization.utils.Organizations;
import org.keycloak.protocol.oidc.refresh.RefreshTokenException;
import org.keycloak.protocol.oidc.scope.DefaultScopeType;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.services.ErrorResponseException;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.managers.UserSessionManager;
import org.keycloak.services.util.AuthorizationContextUtil;
import org.keycloak.services.util.DPoPUtil;
import org.keycloak.services.util.MtlsHoKTokenUtil;
import org.keycloak.util.JWKSUtils;
import org.keycloak.migration.migrators.MigrationUtils;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import org.keycloak.http.HttpRequest;
import org.keycloak.representations.dpop.DPoP;
import org.keycloak.common.Profile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerLogicDeepDiveTest {

    @Mock KeycloakSession session;
    @Mock UriInfo uriInfo;
    @Mock ClientConnection connection;
    @Mock RealmModel realm;
    @Mock RefreshToken refreshToken;
    @Mock HttpHeaders headers;
    @Mock UserModel user;
    @Mock UserSessionModel userSession;
    @Mock ClientModel client;
    @Mock AuthenticatedClientSessionModel clientSession;
    @Mock HttpRequest request;
    @Mock UserSessions userSessions;
    @Mock UserSessionManager userSessionManager;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager = new TokenManager();
    }

    // --- validateToken Tests ---

    @Test
    void validateToken_Offline_SessionNotFound_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<UserSessionManager> usmMock = mockStatic(UserSessionManager.class)) {
            
            when(refreshToken.getType()).thenReturn("offline");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("offline")).thenReturn(true);
            when(refreshToken.getSessionState()).thenReturn("state");

            usmMock.when(() -> new UserSessionManager(session)).thenReturn(userSessionManager);
            when(userSessionManager.findOfflineUserSession(realm, "state")).thenReturn(null);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_Offline_SessionInvalid_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<UserSessionManager> usmMock = mockStatic(UserSessionManager.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("offline");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("offline")).thenReturn(true);
            when(refreshToken.getSessionState()).thenReturn("state");

            usmMock.when(() -> new UserSessionManager(session)).thenReturn(userSessionManager);
            when(userSessionManager.findOfflineUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(false);
            when(userSessionManager.revokeOfflineUserSession(userSession)).thenReturn(null);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_Online_SessionInvalid_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(false);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_UserNull_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            when(userSession.getUser()).thenReturn(null);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_UserDisabled_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(false);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_TokenIssuedBeforeSessionStart_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            
            when(userSession.getStarted()).thenReturn(2000L);
            when(refreshToken.isIssuedBeforeSessionStart(2000L)).thenReturn(true);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_ClientSessionNull_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            when(userSession.getStarted()).thenReturn(1000L);
            when(refreshToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
            
            when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
            when(session.getContext().getClient()).thenReturn(client);
            when(userSession.getAuthenticatedClientSessionByClient(anyString())).thenReturn(null);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_ClientSessionInvalid_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            when(userSession.getStarted()).thenReturn(1000L);
            when(refreshToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
            
            when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
            when(session.getContext().getClient()).thenReturn(client);
            when(client.getId()).thenReturn("client-id");
            when(userSession.getAuthenticatedClientSessionByClient("client-id")).thenReturn(clientSession);
            authManagerMock.when(() -> AuthenticationManager.isClientSessionValid(any(), any(), any(), any())).thenReturn(false);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_UnmatchingClients_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class)) {
            
            when(refreshToken.getType()).thenReturn("refresh");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("refresh")).thenReturn(false);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            when(userSession.getStarted()).thenReturn(1000L);
            when(refreshToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
            
            when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
            when(session.getContext().getClient()).thenReturn(client);
            when(client.getId()).thenReturn("client-id");
            when(userSession.getAuthenticatedClientSessionByClient("client-id")).thenReturn(clientSession);
            authManagerMock.when(() -> AuthenticationManager.isClientSessionValid(any(), any(), any(), any())).thenReturn(true);
            when(clientSession.getStarted()).thenReturn(1000L);
            when(refreshToken.getIssuedFor()).thenReturn("wrong-client");
            when(client.getClientId()).thenReturn("client-id");

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_OfflineAccessNotGranted_ThrowsException() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class);
             MockedStatic<UserSessionUtil> userSessionUtilMock = mockStatic(UserSessionUtil.class)) {
            
            when(refreshToken.getType()).thenReturn("offline");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("offline")).thenReturn(true);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessions.getUserSession(realm, "state")).thenReturn(userSession);
            authManagerMock.when(() -> AuthenticationManager.isSessionValid(realm, userSession)).thenReturn(true);
            
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            when(userSession.getStarted()).thenReturn(1000L);
            when(refreshToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
            
            when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
            when(session.getContext().getClient()).thenReturn(client);
            when(client.getId()).thenReturn("client-id");
            when(userSession.getAuthenticatedClientSessionByClient("client-id")).thenReturn(clientSession);
            authManagerMock.when(() -> AuthenticationManager.isClientSessionValid(any(), any(), any(), any())).thenReturn(true);
            when(clientSession.getStarted()).thenReturn(1000L);
            when(client.getClientId()).thenReturn("client-id");
            when(refreshToken.getIssuedFor()).thenReturn("client-id");
            
            when(userSession.isOffline()).thenReturn(true);
            userSessionUtilMock.when(() -> UserSessionUtil.isOfflineAccessGranted(any(), any())).thenReturn(false);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null));
        }
    }

    @Test
    void validateToken_MigrationPath_Success() {
        try (MockedStatic<TokenUtil> tokenUtilMock = mockStatic(TokenUtil.class);
             MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class);
             MockedStatic<MigrationUtils> migrationUtilsMock = mockStatic(MigrationUtils.class);
             MockedStatic<UserSessionUtil> userSessionUtilMock = mockStatic(UserSessionUtil.class)) {
            
            when(refreshToken.getType()).thenReturn("offline");
            tokenUtilMock.when(() -> TokenUtil.TOKEN_TYPE_OFFLINE.equals("offline")).thenReturn(true);
            when(refreshToken.getSessionState()).thenReturn("state");

            when(session.sessions()).thenReturn(userSessions);
            when(userSessionManager.findOfflineUserSession(realm, "state")).thenReturn(userSession);
            when(userSession.getUser()).thenReturn(user);
            when(user.isEnabled()).thenReturn(true);
            when(userSession.getStarted()).thenReturn(1000L);
            when(refreshToken.isIssuedBeforeSessionStart(anyLong())).thenReturn(false);
            
            when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
            when(session.getContext().getClient()).thenReturn(client);
            when(client.getId()).thenReturn("client-id");
            when(userSession.getAuthenticatedClientSessionByClient("client-id")).thenReturn(clientSession);
            authManagerMock.when(() -> AuthenticationManager.isClientSessionValid(any(), any(), any(), any())).thenReturn(true);
            when(clientSession.getStarted()).thenReturn(1000L);
            when(client.getClientId()).thenReturn("client-id");
            when(refreshToken.getIssuedFor()).thenReturn("client-id");
            
            when(userSession.isOffline()).thenReturn(true);
            userSessionUtilMock.when(() -> UserSessionUtil.isOfflineAccessGranted(any(), any())).thenReturn(true);

            // For migration path: oldTokenScope == null
            TokenManager.TokenValidation result = tokenManager.validateToken(session, uriInfo, connection, realm, refreshToken, headers, null);
            
            assertNotNull(result);
            migrationUtilsMock.verify(() -> MigrationUtils.migrateOldOfflineToken(any(), any(), any(), any()));
        }
    }

    // --- verifyRefreshToken Tests ---

    @Test
    void verifyRefreshToken_InvalidType_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("invalid");

        assertThrows(OAuthErrorException.class, () -> 
            tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
    }

    @Test
    void verifyRefreshToken_ClientMismatch_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("refresh");
        when(client.getClientId()).thenReturn("client-a");
        when(refreshToken.getIssuedFor()).thenReturn("client-b");

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class)) {
            TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);
            when(verifier.withChecks(any())).thenReturn(verifier);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
        }
    }

    @Test
    void verifyRefreshToken_MtlsFailure_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("refresh");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getIssuedFor()).thenReturn("client-id");

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class);
             MockedStatic<OIDCAdvancedConfigWrapper> configMock = mockStatic(OIDCAdvancedConfigWrapper.class);
             MockedStatic<MtlsHoKTokenUtil> mtlsMock = mockStatic(MtlsHoKTokenUtil.class)) {
            
            TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);
            when(verifier.withChecks(any())).thenReturn(verifier);

            OIDCAdvancedConfigWrapper wrapper = mock(OIDCAdvancedConfigWrapper.class);
            configMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(wrapper);
            when(wrapper.isUseMtlsHokToken()).thenReturn(true);
            mtlsMock.when(() -> MtlsHoKTokenUtil.verifyTokenBindingWithClientCertificate(any(), any(), any())).thenReturn(false);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
        }
    }

    @Test
    void verifyRefreshToken_AbcaResultNull_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("refresh");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getIssuedFor()).thenReturn("client-id");

        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumbprint");
        when(cnf.getJktType()).thenReturn("abca");

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class)) {
            TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);
            when(verifier.withChecks(any())).thenReturn(verifier);

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
        }
    }

    @Test
    void verifyRefreshToken_AbcaThumbprintMismatch_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("refresh");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getIssuedFor()).thenReturn("client-id");

        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumbprint");
        when(cnf.getJktType()).thenReturn("abca");

        ABCAResult abcaResult = mock(ABCAResult.class);
        AccessToken.Confirmation abcaCnf = mock(AccessToken.Confirmation.class);
        JWK jwk = mock(JWK.class);
        when(abcaResult.getAttestationJwt().getConfirmation()).thenReturn(abcaCnf);
        when(abcaCnf.getJwk()).thenReturn(jwk);
        when(session.getAttribute(eq(ABCAResult.ABCA_RESULT), eq(ABCAResult.class))).thenReturn(abcaResult);

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class);
             MockedStatic<JWKSUtils> jwksMock = mockStatic(JWKSUtils.class)) {
            
            TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);
            when(verifier.withChecks(any())).thenReturn(verifier);
            
            when(jwksMock.computeThumbprint(jwk)).thenReturn("different-thumbprint");

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
        }
    }

    @Test
    void verifyRefreshToken_DpopValidationFailure_ThrowsException() throws JWSInputException, OAuthErrorException {
        when(session.tokens()).thenReturn(mock(TokenManager.Tokens.class));
        when(session.tokens().decode(anyString(), eq(RefreshToken.class))).thenReturn(refreshToken);
        when(refreshToken.getType()).thenReturn("refresh");
        when(client.getClientId()).thenReturn("client-id");
        when(refreshToken.getIssuedFor()).thenReturn("client-id");

        AccessToken.Confirmation cnf = mock(AccessToken.Confirmation.class);
        when(refreshToken.getConfirmation()).thenReturn(cnf);
        when(cnf.getKeyThumbprint()).thenReturn("thumbprint");
        when(cnf.getJktType()).thenReturn("dpop");

        DPoP dpop = mock(DPoP.class);
        when(session.getAttribute(eq(DPoPUtil.DPOP_SESSION_ATTRIBUTE), eq(DPoP.class))).thenReturn(dpop);

        try (MockedStatic<TokenVerifier> tokenVerifierMock = mockStatic(TokenVerifier.class);
             MockedStatic<DPoPUtil> dpopUtilMock = mockStatic(DPoPUtil.class)) {
            
            TokenVerifier<RefreshToken> verifier = mock(TokenVerifier.class);
            tokenVerifierMock.when(() -> TokenVerifier.createWithoutSignature(any())).thenReturn(verifier);
            when(verifier.withChecks(any())).thenReturn(verifier);
            
            dpopUtilMock.when(() -> DPoPUtil.validateBinding(any(), any())).thenThrow(new VerificationException("DPoP failure"));

            assertThrows(OAuthErrorException.class, () -> 
                tokenManager.verifyRefreshToken(session, realm, client, request, "encoded", true));
        }
    }

    // --- isValidScope Tests ---

    @Test
    void isValidScope_OrgScopeMixing_ReturnsFalse() {
        try (MockedStatic<Organizations> orgsMock = mockStatic(Organizations.class);
             MockedStatic<OrganizationScope> orgScopeMock = mockStatic(OrganizationScope.class)) {
            
            orgsMock.when(() -> Organizations.isEnabled(session)).thenReturn(true);
            
            OrganizationScope type1 = mock(OrganizationScope.class);
            OrganizationScope type2 = mock(OrganizationScope.class);
            
            orgScopeMock.when(() -> OrganizationScope.valueOfScope(eq(session), eq("any"))).thenReturn(type1);
            orgScopeMock.when(() -> OrganizationScope.valueOfScope(eq(session), eq("specific"))).thenReturn(type2);

            assertFalse(TokenManager.isValidScope(session, "any specific", client));
        }
    }

    @Test
    void isValidScope_NoClientScopesFound_ReturnsFalse() {
        // We need to mock getRequestedClientScopes to return an empty stream
        try (MockedStatic<TokenManager> tokenManagerMock = mockStatic(TokenManager.class, CALLS_REAL_METHODS)) {
            tokenManagerMock.when(() -> TokenManager.getRequestedClientScopes(any(), anyString(), any(), any()))
                    .thenReturn(Stream.empty());

            // If clientScopes is empty, it returns false
            assertFalse(TokenManager.isValidScope(session, "some-scope", client));
        }
    }

    @Test
    void isValidScope_AlwaysConsentNonConsentClient_ReturnsFalse() {
        try (MockedStatic<TokenManager> tokenManagerMock = mockStatic(TokenManager.class, CALLS_REAL_METHODS)) {
            ClientScopeModel alwaysConsentScope = mock(ClientScopeModel.class);
            when(alwaysConsentScope.isAlwaysConsent()).thenReturn(true);
            
            tokenManagerMock.when(() -> TokenManager.getRequestedClientScopes(any(), anyString(), any(), any()))
                    .thenReturn(Stream.of(alwaysConsentScope));

            when(client.isConsentRequired()).thenReturn(false);

            assertFalse(TokenManager.isValidScope(session, "some-scope", client));
        }
    }

    @Test
    void isValidScope_ParameterizedNonRepeatable_ReturnsFalse() {
        try (MockedStatic<TokenManager> tokenManagerMock = mockStatic(TokenManager.class, CALLS_REAL_METHODS);
             MockedStatic<Profile> profileMock = mockStatic(Profile.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.PARAMETERIZED_SCOPES)).thenReturn(true);
            
            ClientScopeModel paramScope = mock(ClientScopeModel.class);
            when(paramScope.isParameterizedScope()).thenReturn(true);
            when(paramScope.getName()).thenReturn("param-scope");
            when(paramScope.isRepeatable()).thenReturn(false); // Not repeatable
            
            tokenManagerMock.when(() -> TokenManager.getRequestedClientScopes(any(), anyString(), any(), any()))
                    .thenReturn(Stream.of(paramScope));
            
            // Requesting same scope twice with different params
            // Note: the loop checks seenParameterized.add(scopeName)
            // If we call isValidScope with "param-scope:a param-scope:b", it should fail.
            
            // We need to mock isRepeatableScope
            tokenManagerMock.when(() -> TokenManager.isRepeatableScope(any(), any())).thenReturn(false);

            assertFalse(TokenManager.isValidScope(session, "param-scope:a param-scope:b", client));
        }
    }

    // --- verifyConsentStillAvailable / isGrantedConsent Tests ---

    @Test
    void verifyConsentStillAvailable_Parameterized_ConsentNotGranted_ReturnsFalse() {
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class);
             MockedStatic<UserConsentManager> consentManagerMock = mockStatic(UserConsentManager.class);
             MockedStatic<AuthorizationContextUtil> authContextMock = mockStatic(AuthorizationContextUtil.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.PARAMETERIZED_SCOPES)).thenReturn(true);
            when(client.isConsentRequired()).thenReturn(true);
            when(clientSession.getNote(OIDCLoginProtocol.CONSENT_NOTE)).thenReturn("scope1");

            AuthorizationRequestContext ctx = mock(AuthorizationRequestContext.class);
            authContextMock.when(() -> AuthorizationContextUtil.getAuthorizationRequestContextFromScopesWithClient(any(), any(), any()))
                    .thenReturn(ctx);
            
            AuthorizationRequestContext.AuthorizationDetailEntry detail = mock(AuthorizationRequestContext.AuthorizationDetailEntry.class);
            when(ctx.getAuthorizationDetailEntries()).thenReturn(List.of(detail));
            when(detail.getClientScope()).thenReturn(mock(ClientScopeModel.class));
            when(detail.getParameterizedScopeParam()).thenReturn("param1");
            
            UserConsentModel grantedConsent = mock(UserConsentModel.class);
            consentManagerMock.when(() -> UserConsentManager.getConsentByClient(any(), any(), any(), any()))
                    .thenReturn(grantedConsent);
            when(grantedConsent.isClientScopeGranted(any(), any())).thenReturn(false); // Not granted

            assertFalse(TokenManager.verifyConsentStillAvailable(session, user, client, clientSession, "scope1:param1"));
        }
    }

    @Test
    void isGrantedConsent_AlwaysConsentMissing_ReturnsFalse() {
        ClientScopeModel scope = mock(ClientScopeModel.class);
        when(scope.isAlwaysConsent()).thenReturn(true);
        
        Set<String> alwaysConsent = Set.of("other-scope"); // Missing "scope"

        // Note: isGrantedConsent is private. To test it, we must use a public method that calls it.
        // verifyConsentStillAvailable (non-parameterized) calls it.
        
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class);
             MockedStatic<UserConsentManager> consentManagerMock = mockStatic(UserConsentManager.class)) {
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.PARAMETERIZED_SCOPES)).thenReturn(false);
            
            // Mock getRequestedClientScopes to return the alwaysConsent scope
            try (MockedStatic<TokenManager> tokenManagerStaticMock = mockStatic(TokenManager.class, CALLS_REAL_METHODS)) {
                tokenManagerStaticMock.when(() -> TokenManager.getRequestedClientScopes(any(), anyString(), any(), any()))
                        .thenReturn(Stream.of(scope));

                UserConsentModel grantedConsent = mock(UserConsentModel.class);
                consentManagerMock.when(() -> UserConsentManager.getConsentByClient(any(), any(), any(), any()))
                        .thenReturn(grantedConsent);

                assertFalse(TokenManager.verifyConsentStillAvailable(session, user, client, clientSession, "scope"));
            }
        }
    }
}

// Mocking AuthorizationRequestContext as it was needed in previous stages
abstract class AuthorizationRequestContext {
    public abstract List<AuthorizationDetailEntry> getAuthorizationDetailEntries();
    public static class AuthorizationDetailEntry {
        public abstract String getScopeNameFromCustomData();
        public abstract ClientScopeModel getClientScope();
        public abstract String getParameterizedScopeParam();
    }
}
