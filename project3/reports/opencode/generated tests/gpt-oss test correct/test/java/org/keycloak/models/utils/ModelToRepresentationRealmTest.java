package org.keycloak.models.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.common.enums.SslRequired;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.common.Profile;
import org.keycloak.common.util.Time;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.WebAuthnPolicy;
import org.keycloak.models.CibaConfig;
import org.keycloak.models.ParConfig;
import org.keycloak.representations.idm.RealmRepresentation;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelToRepresentationRealmTest {

    @Mock
    private KeycloakSession sessionMock;

    @Mock
    private RealmModel realmMock;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        // Profile.isFeatureEnabled is consulted by the conversion, so the profile must be initialized
        Profile.defaults();
        when(sessionMock.clientPolicy()).thenReturn(mock(org.keycloak.services.clientpolicy.ClientPolicyManager.class));
    }

    @Test
    @DisplayName("toRepresentation internal false does not strip secrets")
    public void testToRepresentationInternalFalse() {
        when(realmMock.getId()).thenReturn("realm-1");
        when(realmMock.getName()).thenReturn("testRealm");
        when(realmMock.getDisplayName()).thenReturn("Test Realm");
        when(realmMock.getDisplayNameHtml()).thenReturn("HTML");
        when(realmMock.isEnabled()).thenReturn(true);
        when(realmMock.getNotBefore()).thenReturn(0);
        when(realmMock.getSslRequired()).thenReturn(SslRequired.ALL);
        when(realmMock.isRegistrationAllowed()).thenReturn(true);
        when(realmMock.isRememberMe()).thenReturn(false);
        when(realmMock.isBruteForceProtected()).thenReturn(false);
        when(realmMock.isPermanentLockout()).thenReturn(false);
        when(realmMock.getMaxTemporaryLockouts()).thenReturn(5);
        when(realmMock.getMaxFailureWaitSeconds()).thenReturn(30);
        when(realmMock.getMinimumQuickLoginWaitSeconds()).thenReturn(10);
        when(realmMock.getWaitIncrementSeconds()).thenReturn(5);
        when(realmMock.getQuickLoginCheckMilliSeconds()).thenReturn(20L);
        when(realmMock.getMaxDeltaTimeSeconds()).thenReturn(100);
        when(realmMock.getFailureFactor()).thenReturn(1);
        when(realmMock.getMaxSecondaryAuthFailures()).thenReturn(1);
        when(realmMock.isUserManagedAccessAllowed()).thenReturn(true);
        when(realmMock.isEventsEnabled()).thenReturn(true);
        when(realmMock.getEventsExpiration()).thenReturn(0L);
        when(realmMock.getEventsListenersStream()).thenReturn(Stream.of());
        when(realmMock.getEnabledEventTypesStream()).thenReturn(Stream.of());
        when(realmMock.isAdminEventsEnabled()).thenReturn(false);
        when(realmMock.isAdminEventsDetailsEnabled()).thenReturn(false);
        when(realmMock.isVerifyEmail()).thenReturn(false);
        when(realmMock.isLoginWithEmailAllowed()).thenReturn(false);
        when(realmMock.isDuplicateEmailsAllowed()).thenReturn(false);
        when(realmMock.isResetPasswordAllowed()).thenReturn(false);
        when(realmMock.isEditUsernameAllowed()).thenReturn(false);
        when(realmMock.isOrganizationsEnabled()).thenReturn(false);
        when(realmMock.isAdminPermissionsEnabled()).thenReturn(false);
        when(realmMock.isVerifiableCredentialsEnabled()).thenReturn(false);
        when(realmMock.isScimApiEnabled()).thenReturn(false);
        when(realmMock.getDefaultSignatureAlgorithm()).thenReturn("HS256");
        when(realmMock.isRevokeRefreshToken()).thenReturn(false);
        when(realmMock.getRefreshTokenMaxReuse()).thenReturn(0);
        when(realmMock.getAccessTokenLifespan()).thenReturn(300000);
        when(realmMock.getAccessTokenLifespanForImplicitFlow()).thenReturn(600000);
        when(realmMock.getSsoSessionIdleTimeout()).thenReturn(1800);
        when(realmMock.getSsoSessionMaxLifespan()).thenReturn(86400);
        when(realmMock.getSsoSessionIdleTimeoutRememberMe()).thenReturn(3600);
        when(realmMock.getSsoSessionMaxLifespanRememberMe()).thenReturn(172800);
        when(realmMock.getOfflineSessionIdleTimeout()).thenReturn(7200);
        when(realmMock.isOfflineSessionMaxLifespanEnabled()).thenReturn(true);
        when(realmMock.getOfflineSessionMaxLifespan()).thenReturn(864000);
        when(realmMock.getClientSessionIdleTimeout()).thenReturn(300);
        when(realmMock.getClientSessionMaxLifespan()).thenReturn(600);
        when(realmMock.getClientOfflineSessionIdleTimeout()).thenReturn(120);
        when(realmMock.getClientOfflineSessionMaxLifespan()).thenReturn(240);
        when(realmMock.getAccessCodeLifespan()).thenReturn(120);
        when(realmMock.getAccessCodeLifespanUserAction()).thenReturn(300);
        when(realmMock.getAccessCodeLifespanLogin()).thenReturn(240);
        when(realmMock.getActionTokenGeneratedByAdminLifespan()).thenReturn(600);
        when(realmMock.getActionTokenGeneratedByUserLifespan()).thenReturn(300);
        when(realmMock.getOAuth2DeviceConfig()).thenReturn(mock(org.keycloak.models.OAuth2DeviceConfig.class));
        when(realmMock.getBrowserSecurityHeaders()).thenReturn(new HashMap<>());
        when(realmMock.getAccountTheme()).thenReturn("account");
        when(realmMock.getLoginTheme()).thenReturn("login");
        when(realmMock.getAdminTheme()).thenReturn("admin");
        when(realmMock.getEmailTheme()).thenReturn("email");
        OTPPolicy otp = mock(OTPPolicy.class);
        when(realmMock.getOTPPolicy()).thenReturn(otp);
        when(otp.getAlgorithm()).thenReturn("alg");
        when(otp.getPeriod()).thenReturn(30);
        when(otp.getDigits()).thenReturn(6);
        when(otp.getInitialCounter()).thenReturn(0);
        when(otp.getType()).thenReturn("totp");
        when(otp.getLookAheadWindow()).thenReturn(1);
        when(otp.isCodeReusable()).thenReturn(false);
        // WebAuthn policies
        WebAuthnPolicy web = mock(WebAuthnPolicy.class);
        when(realmMock.getWebAuthnPolicy()).thenReturn(web);
        when(web.getRpEntityName()).thenReturn("rp");
        when(web.getSignatureAlgorithm()).thenReturn(List.of("alg"));
        when(web.getRpId()).thenReturn("id");
        when(web.getAttestationConveyancePreference()).thenReturn("pref");
        when(web.getAuthenticatorAttachment()).thenReturn("attach");
        when(web.getRequireResidentKey()).thenReturn("true");
        when(web.getResidentKey()).thenReturn("true");
        when(web.getUserVerificationRequirement()).thenReturn("required");
        when(web.getCreateTimeout()).thenReturn(120);
        when(web.isAvoidSameAuthenticatorRegister()).thenReturn(false);
        when(web.getAcceptableAaguids()).thenReturn(Collections.emptyList());
        when(web.getExtraOrigins()).thenReturn(Collections.emptyList());
        when(realmMock.getWebAuthnPolicyPasswordless()).thenReturn(web);
        // Ciba config
        CibaConfig ciba = mock(CibaConfig.class);
        when(realmMock.getCibaPolicy()).thenReturn(ciba);
        when(ciba.getBackchannelTokenDeliveryMode()).thenReturn("mode");
        when(ciba.getExpiresIn()).thenReturn(60);
        when(ciba.getPoolingInterval()).thenReturn(10);
        when(ciba.getAuthRequestedUserHint()).thenReturn("hint");
        // Par config
        ParConfig par = mock(ParConfig.class);
        when(realmMock.getParPolicy()).thenReturn(par);
        when(par.getRequestUriLifespan()).thenReturn(60);
        // Default role
        when(realmMock.getDefaultRole()).thenReturn(mock(org.keycloak.models.RoleModel.class));
        when(realmMock.getDefaultGroupsStream()).thenReturn(Stream.of());
        when(realmMock.getRequiredCredentialsStream()).thenReturn(Stream.of());
        when(realmMock.getSupportedLocalesStream()).thenReturn(Stream.of());
        when(realmMock.isInternationalizationEnabled()).thenReturn(false);
        when(realmMock.getDefaultLocale()).thenReturn("en");
        // Exception handling omitted

        RealmRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(sessionMock, realmMock, false, false);
        assertEquals("realm-1", rep.getId());
        assertEquals("testRealm", rep.getRealm());
        assertTrue(rep.isEnabled());
    }
}
