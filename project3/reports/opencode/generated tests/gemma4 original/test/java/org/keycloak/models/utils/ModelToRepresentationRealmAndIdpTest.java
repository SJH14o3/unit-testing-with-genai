package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.authentication.otp.OTPApplicationProvider;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.store.StoreFactory;
import org.keycloak.authorization.store.ResourceServerStore;
import org.keycloak.common.Profile;
import org.keycloak.credential.OTPCredentialModel;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.models.*;
import org.keycloak.models.oid4vci.CredentialScopeModel;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.webauthn.WebAuthnPolicy;
import org.keycloak.webauthn.WebAuthnPolicyPasswordless;
import org.keycloak.ciba.CibaConfig;
import org.keycloak.par.ParConfig;
import org.keycloak.models.otp.OTPPolicy;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelToRepresentationRealmAndIdpTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private IdentityProviderModel idpModel;

    @Mock
    private IdentityProviderManager idpManager;

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private IdentityProviderMapperModel idpMapper;

    @Mock
    private OTPPolicy otpPolicy;

    @Mock
    private WebAuthnPolicy webAuthnPolicy;

    @Mock
    private WebAuthnPolicyPasswordless webAuthnPolicyPasswordless;

    @Mock
    private CibaConfig cibaConfig;

    @Mock
    private ParConfig parConfig;

    @Mock
    private ClientPolicy clientPolicy;

    @Mock
    private AuthenticationFlowModel authFlow;

    @Test
    void testToBriefRepresentationRealm() {
        when(realm.getId()).thenReturn("realm-id");
        when(realm.getName()).thenReturn("realm-name");
        when(realm.getDisplayName()).thenReturn("realm-display-name");
        when(realm.isEnabled()).thenReturn(true);

        RealmRepresentation rep = ModelToRepresentation.toBriefRepresentation(realm);

        assertNotNull(rep);
        assertEquals("realm-id", rep.getId());
        assertEquals("realm-name", rep.getRealm());
        assertEquals("realm-display-name", rep.getDisplayName());
        assertTrue(rep.isEnabled());
    }

    @Test
    void testToRepresentationBriefRealm() {
        when(realm.getId()).thenReturn("realm-id");
        when(realm.getName()).thenReturn("realm-name");
        when(realm.getDisplayName()).thenReturn("realm-display-name");
        when(realm.isEnabled()).thenReturn(true);

        RealmRepresentation rep = ModelToRepresentation.toRepresentation(session, realm, true);

        assertNotNull(rep);
        assertEquals("realm-id", rep.getId());
        assertEquals("realm-name", rep.getRealm());
        assertEquals("realm-display-name", rep.getDisplayName());
        assertTrue(rep.isEnabled());
    }

    @Test
    void testToRepresentationFullRealm() {
        // Mock basic realm properties
        when(realm.getId()).thenReturn("realm-id");
        when(realm.getName()).thenReturn("realm-name");
        when(realm.getDisplayName()).thenReturn("realm-display-name");
        when(realm.isEnabled()).thenReturn(true);
        when(realm.getNotBefore()).thenReturn(12345L);
        when(realm.getSslRequired()).thenReturn(org.keycloak.models.SslRequired.REQUIRED);
        when(realm.isRegistrationAllowed()).thenReturn(true);
        when(realm.isRegistrationEmailAsUsername()).thenReturn(false);
        when(realm.isRememberMe()).thenReturn(true);
        when(realm.isBruteForceProtected()).thenReturn(true);
        when(realm.isPermanentLockout()).thenReturn(false);
        when(realm.getMaxTemporaryLockouts()).thenReturn(5);
        when(realm.getBruteForceStrategy()).thenReturn("incremental");
        when(realm.getMaxFailureWaitSeconds()).thenReturn(10);
        when(realm.getMinimumQuickLoginWaitSeconds()).thenReturn(5);
        when(realm.getWaitIncrementSeconds()).thenReturn(2);
        when(realm.getQuickLoginCheckMilliSeconds()).thenReturn(1000L);
        when(realm.getMaxDeltaTimeSeconds()).thenReturn(60);
        when(realm.getFailureFactor()).thenReturn(2.0);
        when(realm.getMaxSecondaryAuthFailures()).thenReturn(3);
        when(realm.isEventsEnabled()).thenReturn(true);
        when(realm.getEventsExpiration()).thenReturn(3600);
        when(realm.getEventsListenersStream()).thenReturn(Stream.of("event-listener"));
        when(realm.getEnabledEventTypesStream()).thenReturn(Stream.of("LOGIN_EVENT"));
        when(realm.isAdminEventsEnabled()).thenReturn(true);
        when(realm.isAdminEventsDetailsEnabled()).thenReturn(false);
        when(realm.isVerifyEmail()).thenReturn(true);
        when(realm.isLoginWithEmailAllowed()).thenReturn(true);
        when(realm.isDuplicateEmailsAllowed()).thenReturn(false);
        when(realm.isResetPasswordAllowed()).thenReturn(true);
        when(realm.isEditUsernameAllowed()).thenReturn(true);
        when(realm.isOrganizationsEnabled()).thenReturn(false);
        when(realm.isAdminPermissionsEnabled()).thenReturn(false);
        when(realm.isVerifiableCredentialsEnabled()).thenReturn(false);
        when(realm.isScimApiEnabled()).thenReturn(false);
        when(realm.getDefaultSignatureAlgorithm()).thenReturn("RS256");
        when(realm.isRevokeRefreshToken()).thenReturn(false);
        when(realm.getRefreshTokenMaxReuse()).thenReturn(1);
        when(realm.getAccessTokenLifespan()).thenReturn(300);
        when(realm.getAccessTokenLifespanForImplicitFlow()).thenReturn(300);
        when(realm.getSsoSessionIdleTimeout()).thenReturn(300);
        when(realm.getSsoSessionMaxLifespan()).thenReturn(600);
        when(realm.getSsoSessionIdleTimeoutRememberMe()).thenReturn(300);
        when(realm.getSsoSessionMaxLifespanRememberMe()).thenReturn(600);
        when(realm.getOfflineSessionIdleTimeout()).thenReturn(300);
        when(realm.isOfflineSessionMaxLifespanEnabled()).thenReturn(false);
        when(realm.getOfflineSessionMaxLifespan()).thenReturn(600);
        when(realm.getClientSessionIdleTimeout()).thenReturn(300);
        when(realm.getClientSessionMaxLifespan()).thenReturn(600);
        when(realm.getClientOfflineSessionIdleTimeout()).thenReturn(300);
        when(realm.getClientOfflineSessionMaxLifespan()).thenReturn(600);
        when(realm.getAccessCodeLifespan()).thenReturn(300);
        when(realm.getAccessCodeLifespanUserAction()).thenReturn(300);
        when(realm.getAccessCodeLifespanLogin()).thenReturn(300);
        when(realm.getActionTokenGeneratedByAdminLifespan()).thenReturn(300);
        when(realm.getActionTokenGeneratedByUserLifespan()).thenReturn(300);
        when(realm.getOAuth2DeviceConfig()).thenReturn(mock(org.keycloak.models.OAuth2DeviceConfigModel.class));
        when(realm.getOAuth2DeviceConfig().getLifespan()).thenReturn(300);
        when(realm.getOAuth2DeviceConfig().getPoolingInterval()).thenReturn(5);
        when(realm.getSmtpConfig()).thenReturn(new HashMap<>());
        when(realm.getBrowserSecurityHeaders()).thenReturn(new HashMap<>());
        when(realm.getAccountTheme()).thenReturn("account");
        when(realm.getLoginTheme()).thenReturn("login");
        when(realm.getAdminTheme()).thenReturn("admin");
        when(realm.getEmailTheme()).thenReturn("email");
        when(realm.getOTPPolicy()).thenReturn(otpPolicy);
        when(otpPolicy.getAlgorithm()).thenReturn("SHA256");
        when(otpPolicy.getPeriod()).thenReturn(30);
        when(otpPolicy.getDigits()).thenReturn(6);
        when(otpPolicy.getInitialCounter()).thenReturn(0);
        when(otpPolicy.getType()).thenReturn("TOTP");
        when(otpPolicy.getLookAheadWindow()).thenReturn(1);
        when(otpPolicy.isCodeReusable()).thenReturn(true);
        when(realm.getWebAuthnPolicy()).thenReturn(webAuthnPolicy);
        when(webAuthnPolicy.getRpEntityName()).thenReturn("RP");
        when(webAuthnPolicy.getSignatureAlgorithm()).thenReturn("RS256");
        when(webAuthnPolicy.getRpId()).thenReturn("rp-id");
        when(webAuthnPolicy.getAttestationConveyancePreference()).thenReturn("none");
        when(webAuthnPolicy.getAuthenticatorAttachment()).thenReturn("platform");
        when(webAuthnPolicy.getRequireResidentKey()).thenReturn(true);
        when(webAuthnPolicy.getResidentKey()).thenReturn("resident-key");
        when(webAuthnPolicy.getUserVerificationRequirement()).thenReturn("required");
        when(webAuthnPolicy.getCreateTimeout()).thenReturn(60);
        when(webAuthnPolicy.isAvoidSameAuthenticatorRegister()).thenReturn(true);
        when(webAuthnPolicy.getAcceptableAaguids()).thenReturn(Collections.emptyList());
        when(webAuthnPolicy.getExtraOrigins()).thenReturn(Collections.emptyList());
        when(realm.getWebAuthnPolicyPasswordless()).thenReturn(webAuthnPolicyPasswordless);
        when(webAuthnPolicyPasswordless.getRpEntityName()).thenReturn("RP-PW");
        when(webAuthnPolicyPasswordless.getSignatureAlgorithm()).thenReturn("RS256");
        when(webAuthnPolicyPasswordless.getRpId()).thenReturn("rp-id-pw");
        when(webAuthnPolicyPasswordless.getAttestationConveyancePreference()).thenReturn("none");
        when(webAuthnPolicyPasswordless.getAuthenticatorAttachment()).thenReturn("platform");
        when(webAuthnPolicyPasswordless.getRequireResidentKey()).thenReturn(true);
        when(webAuthnPolicyPasswordless.getResidentKey()).thenReturn("resident-key-pw");
        when(webAuthnPolicyPasswordless.getUserVerificationRequirement()).thenReturn("required");
        when(webAuthnPolicyPasswordless.getCreateTimeout()).thenReturn(60);
        when(webAuthnPolicyPasswordless.isAvoidSameAuthenticatorRegister()).thenReturn(true);
        when(webAuthnPolicyPasswordless.getAcceptableAaguids()).thenReturn(Collections.emptyList());
        when(webAuthnPolicyPasswordless.getExtraOrigins()).thenReturn(Collections.emptyList());
        when(webAuthnPolicyPasswordless.isPasskeysEnabled()).thenReturn(true);
        when(webAuthnPolicyPasswordless.getMediation()).thenReturn(false);
        when(realm.getCibaPolicy()).thenReturn(cibaConfig);
        when(cibaConfig.getBackchannelTokenDeliveryMode()).thenReturn("direct");
        when(cibaConfig.getExpiresIn()).thenReturn(3600L);
        when(cibaConfig.getPoolingInterval()).thenReturn(60L);
        when(cibaConfig.getAuthRequestedUserHint()).thenReturn("hint");
        when(realm.getParPolicy()).thenReturn(parConfig);
        when(parConfig.getRequestUriLifespan()).thenReturn(300L);
        when(realm.getBrowserFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getBrowserFlow().getAlias()).thenReturn("browser-flow");
        when(realm.getRegistrationFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getRegistrationFlow().getAlias()).thenReturn("registration-flow");
        when(realm.getDirectGrantFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getDirectGrantFlow().getAlias()).thenReturn("direct-grant-flow");
        when(realm.getResetCredentialsFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getResetCredentialsFlow().getAlias()).thenReturn("reset-credentials-flow");
        when(realm.getClientAuthenticationFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getClientAuthenticationFlow().getAlias()).thenReturn("client-auth-flow");
        when(realm.getDockerAuthenticationFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getDockerAuthenticationFlow().getAlias()).thenReturn("docker-auth-flow");
        when(realm.getFirstBrokerLoginFlow()).thenReturn(mock(org.keycloak.models.AuthenticationFlowModel.class));
        when(realm.getFirstBrokerLoginFlow().getAlias()).thenReturn("first-broker-login-flow");
        when(realm.getDefaultRole()).thenReturn(mock(org.keycloak.models.RoleModel.class));
        when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredCredentialsStream()).thenReturn(Stream.empty());
        when(realm.getAttributes()).thenReturn(new HashMap<>());
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
        when(realm.getAuthenticatorConfigsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        when(session.identityProviders()).thenReturn(idpManager);
        when(session.clientPolicy()).thenReturn(clientPolicy);
        when(session.getAllProviders(OTPApplicationProvider.class)).thenReturn(Collections.emptyList());

        // Mock for stripSecrets
        try (MockedStatic<ModelToRepresentation> mockedModelToRep = mockStatic(ModelToRepresentation.class, CALLS_REAL_METHODS)) {
            // This is complex because stripSecrets is a static method called in toRepresentation. 
            // If we can't easily mock stripSecrets without breaking the real call, we'll assume it works.
            
            RealmRepresentation rep = ModelToRepresentation.toRepresentation(session, realm, false, false);

            assertNotNull(rep);
            assertEquals("realm-id", rep.getId());
            assertEquals("realm-name", rep.getRealm());
            assertEquals("RS256", rep.getDefaultSignatureAlgorithm());
            assertEquals("browser-flow", rep.getBrowserFlow());
        }
    }

    @Test
    void testToBriefRepresentationIdp() {
        when(idpModel.getInternalId()).thenReturn("idp-internal-id");
        when(idpModel.getProviderId()).thenReturn("idp-provider-id");
        when(idpModel.getAlias()).thenReturn("idp-alias");
        when(idpModel.getDisplayName()).thenReturn("idp-display-name");
        when(idpModel.isEnabled()).thenReturn(true);

        IdentityProviderRepresentation rep = ModelToRepresentation.toBriefRepresentation(realm, idpModel);

        assertNotNull(rep);
        assertEquals("idp-internal-id", rep.getInternalId());
        assertEquals("idp-provider-id", rep.getProviderId());
        assertEquals("idp-alias", rep.getAlias());
        assertEquals("idp-display-name", rep.getDisplayName());
        assertTrue(rep.isEnabled());
    }

    @Test
    void testToRepresentationIdp() {
        when(idpModel.getInternalId()).thenReturn("idp-internal-id");
        when(idpModel.getProviderId()).thenReturn("idp-provider-id");
        when(idpModel.getAlias()).thenReturn("idp-alias");
        when(idpModel.getDisplayName()).thenReturn("idp-display-name");
        when(idpModel.isEnabled()).thenReturn(true);
        when(idpModel.isLinkOnly()).thenReturn(false);
        when(idpModel.isHideOnLogin()).thenReturn(false);
        when(idpModel.isStoreToken()).thenReturn(true);
        when(idpModel.isTrustEmail()).thenReturn(false);
        when(idpModel.isAuthenticateByDefault()).thenReturn(false);
        when(idpModel.getConfig()).thenReturn(Map.of("key", "value"));
        when(idpModel.isAddReadTokenRoleOnCreate()).thenReturn(false);
        when(idpModel.getFirstBrokerLoginFlowId()).thenReturn("flow-id-1");
        when(idpModel.getPostBrokerLoginFlowId()).thenReturn("flow-id-2");

        when(realm.getAuthenticationFlowById("flow-id-1")).thenReturn(authFlow);
        when(authFlow.getAlias()).thenReturn("flow-alias-1");
        when(realm.getAuthenticationFlowById("flow-id-2")).thenReturn(authFlow);
        when(authFlow.getAlias()).thenReturn("flow-alias-2");

        IdentityProviderRepresentation rep = ModelToRepresentation.toRepresentation(session, realm, idpModel, false);

        assertNotNull(rep);
        assertEquals("idp-internal-id", rep.getInternalId());
        assertEquals("idp-provider-id", rep.getProviderId());
        assertEquals("flow-alias-1", rep.getFirstBrokerLoginFlowAlias());
        assertEquals("flow-alias-2", rep.getPostBrokerLoginFlowAlias());
        assertEquals("value", rep.getConfig().get("key"));
    }

    @Test
    void testToRepresentationIdpWithMissingFlow() {
        when(idpModel.getFirstBrokerLoginFlowId()).thenReturn("missing-flow-id");
        when(realm.getAuthenticationFlowById("missing-flow-id")).thenReturn(null);

        assertThrows(ModelException.class, () -> {
            ModelToRepresentation.toRepresentation(session, realm, idpModel, false);
        });
    }

    @Test
    void testToEventsConfigReprensetation() {
        when(realm.isEventsEnabled()).thenReturn(true);
        when(realm.getEventsExpiration()).thenReturn(3600);
        when(realm.getEventsListenersStream()).thenReturn(Stream.of("listener1"));
        when(realm.getEnabledEventTypesStream()).thenReturn(Stream.of("EVENT1"));
        when(realm.isAdminEventsEnabled()).thenReturn(true);
        when(realm.isAdminEventsDetailsEnabled()).thenReturn(true);

        RealmEventsConfigRepresentation rep = ModelToRepresentation.toEventsConfigReprensetation(realm);

        assertNotNull(rep);
        assertTrue(rep.isEventsEnabled());
        assertEquals(3600, rep.getEventsExpiration());
        assertTrue(rep.getEventsListeners().contains("listener1"));
        assertTrue(rep.getEnabledEventTypes().contains("EVENT1"));
        assertTrue(rep.isAdminEventsEnabled());
        assertTrue(rep.isAdminEventsDetailsEnabled());
    }
}
