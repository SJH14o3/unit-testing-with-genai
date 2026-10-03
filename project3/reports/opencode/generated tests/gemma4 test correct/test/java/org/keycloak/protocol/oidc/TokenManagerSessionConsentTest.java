package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.common.Profile;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.rar.AuthorizationDetails;
import org.keycloak.rar.AuthorizationRequestContext;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.managers.UserConsentManager;
import org.keycloak.services.util.AuthorizationContextUtil;
import org.keycloak.services.util.UserSessionUtil;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerSessionConsentTest {

    @Mock
    KeycloakSession session;
    @Mock
    UserSessionModel userSession;
    @Mock
    AuthenticationSessionModel authSession;
    @Mock
    ClientModel client;
    @Mock
    AuthenticatedClientSessionModel clientSession;
    @Mock
    RealmModel realm;
    @Mock
    UserModel user;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager = new TokenManager();
    }

    @Test
    void attachAuthenticationSession_NewSession_Success() {
        when(authSession.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("client-id");
        when(userSession.getAuthenticatedClientSessionByClient("client-id")).thenReturn(null);
        when(userSession.getRealm()).thenReturn(realm);
        when(session.sessions()).thenReturn(mock(UserSessionProvider.class));
        when(session.sessions().createClientSession(any(), any(), any())).thenReturn(clientSession);
        
        when(authSession.getRedirectUri()).thenReturn("http://redirect");
        when(authSession.getProtocol()).thenReturn("oidc");
        when(authSession.getClientNote(OAuth2Constants.SCOPE)).thenReturn("openid profile");
        when(authSession.getClientNotes()).thenReturn(Map.of("client-note", "value"));
        when(authSession.getUserSessionNotes()).thenReturn(Map.of("user-note", "value"));
        when(userSession.getLastSessionRefresh()).thenReturn(1000);

           try (MockedStatic<AuthenticationManager> authManagerMock = mockStatic(AuthenticationManager.class);
               MockedConstruction<AuthenticationSessionManager> authSessionManagerMock = mockConstruction(AuthenticationSessionManager.class);
             MockedStatic<Profile> profileMock = mockStatic(Profile.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.PARAMETERIZED_SCOPES)).thenReturn(false);
            
            // Mock getRequestedClientScopes (static)
            try (MockedStatic<TokenManager> tokenManagerStaticMock = mockStatic(TokenManager.class, CALLS_REAL_METHODS)) {
                tokenManagerStaticMock.when(() -> TokenManager.getRequestedClientScopes(any(), anyString(), any(), any()))
                        .thenReturn(Stream.of(mock(ClientScopeModel.class)));

                ClientSessionContext result = TokenManager.attachAuthenticationSession(session, userSession, authSession, false);

                assertNotNull(result);
                verify(clientSession).setRedirectUri("http://redirect");
                verify(clientSession).setProtocol("oidc");
                verify(clientSession).setNote("client-note", "value");
                verify(userSession).setNote("user-note", "value");
                verify(clientSession).setTimestamp(1000);
            }
        }
    }

    @Test
    void verifyConsentStillAvailable_NoConsentRequired_ReturnsTrue() {
        when(client.isConsentRequired()).thenReturn(false);
        
        // Mocking Profile.isFeatureEnabled for Parameterized Scopes
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class)) {
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.PARAMETERIZED_SCOPES)).thenReturn(false);

            boolean result = TokenManager.verifyConsentStillAvailable(session, user, client, clientSession, "openid");

            assertTrue(result);
        }
    }

    @Test
    void verifyConsentStillAvailable_ParameterizedScopes_ChecksConsents() {
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class);
             MockedStatic<UserConsentManager> consentManagerMock = mockStatic(UserConsentManager.class);
             MockedStatic<AuthorizationContextUtil> authContextMock = mockStatic(AuthorizationContextUtil.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.PARAMETERIZED_SCOPES)).thenReturn(true);
            when(client.isConsentRequired()).thenReturn(true);
            
            // Mocking clientSession.getNote for OIDCLoginProtocol.CONSENT_NOTE
            when(clientSession.getNote(OIDCLoginProtocol.CONSENT_NOTE)).thenReturn("scope1");

            // Mocking AuthorizationRequestContext
            AuthorizationRequestContext ctx = mock(AuthorizationRequestContext.class);
            authContextMock.when(() -> AuthorizationContextUtil.getAuthorizationRequestContextFromScopesWithClient(any(), any(), any()))
                    .thenReturn(ctx);
            
            // Mocking detail entries
            AuthorizationDetails detail = mock(AuthorizationDetails.class);
            when(ctx.getAuthorizationDetailEntries()).thenReturn(List.of(detail));
            when(detail.getClientScope()).thenReturn(mock(ClientScopeModel.class));
            when(detail.getParameterizedScopeParam()).thenReturn("param1");
            
            // Mocking granted consent
            UserConsentModel grantedConsent = mock(UserConsentModel.class);
            consentManagerMock.when(() -> UserConsentManager.getConsentByClient(any(), any(), any(), any()))
                    .thenReturn(grantedConsent);
            when(grantedConsent.isClientScopeGranted(any(), any())).thenReturn(true);

            boolean result = TokenManager.verifyConsentStillAvailable(session, user, client, clientSession, "scope1:param1");

            assertTrue(result);
        }
    }
}
