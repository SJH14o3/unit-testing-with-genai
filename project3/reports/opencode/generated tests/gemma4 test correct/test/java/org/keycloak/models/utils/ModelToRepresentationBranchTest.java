package org.keycloak.models.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.authentication.otp.OTPApplicationProvider;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.store.StoreFactory;
import org.keycloak.authorization.store.ResourceServerStore;
import org.keycloak.common.Profile;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.credential.CredentialMetadata;
import org.keycloak.credential.CredentialModel;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.*;
import org.keycloak.models.oid4vci.CredentialScopeModel;
import org.keycloak.representations.account.CredentialMetadataRepresentation;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.*;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.userprofile.UserProfileProvider;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModelToRepresentationBranchTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private UserModel user;

    @Mock
    private SubjectCredentialManager credentialManager;

    @Mock
    private AdminEvent adminEvent;

    @Mock
    private AuthDetails authDetails;

    @Mock
    private IdentityProviderModel idpModel;

    @Mock
    private ClientModel clientModel;

    @Mock
    private ClientScopeModel clientScopeModel;

    @Mock
    private CredentialMetadata credentialMetadata;

    @Mock
    private CredentialModel credentialModel;

    @BeforeEach
    void initializeProfile() {
        Profile.init(Profile.ProfileName.DEFAULT, Map.of(
                Profile.Feature.AUTHORIZATION, false,
                Profile.Feature.TRANSIENT_USERS, false));
    }

    @Test
    void testToRepresentationUserBranches() {
        when(user.getId()).thenReturn("user-id");
        when(user.getCreatedTimestamp()).thenReturn(12345L);
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn(null);
        when(user.credentialManager()).thenReturn(credentialManager);
        when(session.users()).thenReturn(mock(UserProvider.class));
        when(credentialManager.isConfiguredFor(anyString())).thenReturn(false);
        when(credentialManager.getDisableableCredentialTypesStream()).thenAnswer(invocation -> Stream.empty());
        when(user.getRequiredActionsStream()).thenAnswer(invocation -> Stream.empty());
        
        // Case 1: setUserAttributes = true, attributes null
        org.keycloak.representations.idm.UserRepresentation rep1 = ModelToRepresentation.toRepresentation(session, realm, user, new org.keycloak.representations.idm.UserRepresentation(), true);
        assertNotNull(rep1);

        // Case 2: setUserAttributes = true, attributes non-null, but copy empty (all removed)
        when(user.getAttributes()).thenReturn(Map.of(UserModel.FIRST_NAME, List.of("John")));
        org.keycloak.representations.idm.UserRepresentation rep2 = ModelToRepresentation.toRepresentation(session, realm, user, new org.keycloak.representations.idm.UserRepresentation(), true);
        // After removing FIRST_NAME, copy is empty.

        // Case 3: setUserAttributes = true, attributes non-null, copy not empty
        when(user.getAttributes()).thenReturn(Map.of("custom", List.of("val"), UserModel.FIRST_NAME, List.of("John")));
        org.keycloak.representations.idm.UserRepresentation rep3 = ModelToRepresentation.toRepresentation(session, realm, user, new org.keycloak.representations.idm.UserRepresentation(), true);
        assertTrue(rep3.getAttributes().containsKey("custom"));

        // Case 4: setUserAttributes = false
        org.keycloak.representations.idm.UserRepresentation rep4 = ModelToRepresentation.toRepresentation(session, realm, user, new org.keycloak.representations.idm.UserRepresentation(), false);
        assertNull(rep4.getUsername());
    }

    @Test
    void testToBriefRepresentationBranches() {
        when(user.getId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("user-name");
        when(user.getCreatedTimestamp()).thenReturn(123L);
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn(null);

        // setUserAttributes = true
        org.keycloak.representations.idm.UserRepresentation rep1 = ModelToRepresentation.toBriefRepresentation(user, new org.keycloak.representations.idm.UserRepresentation(), true);
        assertEquals("user-name", rep1.getUsername());

        // setUserAttributes = false
        org.keycloak.representations.idm.UserRepresentation rep2 = ModelToRepresentation.toBriefRepresentation(user, new org.keycloak.representations.idm.UserRepresentation(), false);
        assertNull(rep2.getUsername());
    }

    @Test
    void testAdminEventBranches() {
        when(adminEvent.getId()).thenReturn("ae-id");
        when(adminEvent.getTime()).thenReturn(123L);
        when(adminEvent.getOperationType()).thenReturn(OperationType.CREATE);
        
        // authDetails == null
        AdminEventRepresentation rep1 = ModelToRepresentation.toRepresentation(adminEvent);
        assertNull(rep1.getAuthDetails());

        // authDetails != null
        when(adminEvent.getAuthDetails()).thenReturn(authDetails);
        when(authDetails.getRealmId()).thenReturn("r");
        AdminEventRepresentation rep2 = ModelToRepresentation.toRepresentation(adminEvent);
        assertNotNull(rep2.getAuthDetails());

        // resourceTypeAsString == null
        when(adminEvent.getResourceTypeAsString()).thenReturn(null);
        AdminEventRepresentation rep3 = ModelToRepresentation.toRepresentation(adminEvent);
        assertNull(rep3.getResourceType());

        // resourceTypeAsString != null
        when(adminEvent.getResourceTypeAsString()).thenReturn("CLIENT");
        AdminEventRepresentation rep4 = ModelToRepresentation.toRepresentation(adminEvent);
        assertEquals("CLIENT", rep4.getResourceType());
    }

    @Test
    void testRealmRepresentationBranches() {
        when(realm.getId()).thenReturn("r");
        when(realm.getName()).thenReturn("realm");
        when(realm.getSslRequired()).thenReturn(SslRequired.EXTERNAL);
        when(realm.getEventsExpiration()).thenReturn(0L); // Branch 520
        when(realm.getEventsListenersStream()).thenReturn(Stream.empty());
        when(realm.getEnabledEventTypesStream()).thenReturn(Stream.empty());
        when(realm.getAttributes()).thenReturn(new HashMap<>());
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
        when(realm.getAuthenticatorConfigsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        when(realm.getRequiredCredentialsStream()).thenReturn(Stream.empty());
        when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
        when(realm.getDefaultRole()).thenReturn(mock(RoleModel.class));
        OAuth2DeviceConfig oAuth2DeviceConfig = mock(OAuth2DeviceConfig.class);
        when(realm.getOAuth2DeviceConfig()).thenReturn(oAuth2DeviceConfig);
        when(oAuth2DeviceConfig.getLifespan()).thenReturn(100);
        when(oAuth2DeviceConfig.getPoolingInterval()).thenReturn(10);
        when(realm.getSmtpConfig()).thenReturn(new HashMap<>());
        when(realm.getBrowserSecurityHeaders()).thenReturn(new HashMap<>());
        when(realm.getOTPPolicy()).thenReturn(mock(OTPPolicy.class));
        when(realm.getWebAuthnPolicy()).thenReturn(mock(WebAuthnPolicy.class));
        when(realm.getWebAuthnPolicyPasswordless()).thenReturn(mock(WebAuthnPolicy.class));
        when(realm.getCibaPolicy()).thenReturn(mock(CibaConfig.class));
        when(realm.getParPolicy()).thenReturn(mock(ParConfig.class));
        when(session.clientPolicy()).thenReturn(mock(ClientPolicyManager.class));
        when(realm.getBrowserFlow()).thenReturn(null);
        when(realm.getRegistrationFlow()).thenReturn(null);
        when(realm.getDirectGrantFlow()).thenReturn(null);
        when(realm.getResetCredentialsFlow()).thenReturn(null);
        when(realm.getClientAuthenticationFlow()).thenReturn(null);
        when(realm.getDockerAuthenticationFlow()).thenReturn(null);
        when(realm.getFirstBrokerLoginFlow()).thenReturn(null);

        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class);
             MockedStatic<Config> configMock = mockStatic(Config.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.AUTHORIZATION)).thenReturn(false);
            configMock.when(Config::getAdminRealm).thenReturn("admin");

            // Test basic representation (no export, no internal)
            RealmRepresentation rep = ModelToRepresentation.toRepresentation(session, realm, false, false);
            assertFalse(rep.isUserManagedAccessAllowed());
        }
    }

    @Test
    void testToRepresentationIdpBranches() {
        when(idpModel.getInternalId()).thenReturn("idp-id");
        when(idpModel.getProviderId()).thenReturn("provider");
        when(idpModel.getAlias()).thenReturn("alias");
        when(idpModel.getDisplayName()).thenReturn("display");
        when(idpModel.isEnabled()).thenReturn(true);
        when(idpModel.isLinkOnly()).thenReturn(false);
        when(idpModel.isHideOnLogin()).thenReturn(false);
        when(idpModel.isStoreToken()).thenReturn(false);
        when(idpModel.isTrustEmail()).thenReturn(false);
        when(idpModel.isAuthenticateByDefault()).thenReturn(false);
        when(idpModel.getConfig()).thenReturn(new HashMap<>());
        when(idpModel.isAddReadTokenRoleOnCreate()).thenReturn(false);
        when(session.getKeycloakSessionFactory()).thenReturn(mock(KeycloakSessionFactory.class));

        // export = false
        IdentityProviderRepresentation rep1 = ModelToRepresentation.toRepresentation(session, realm, idpModel, false);
        assertNull(rep1.getOrganizationId());

        // firstBrokerLoginFlowId handling
        when(idpModel.getFirstBrokerLoginFlowId()).thenReturn("flow-id");
        when(realm.getAuthenticationFlowById("flow-id")).thenReturn(mock(AuthenticationFlowModel.class));
        // Mocking alias via returning mock
        when(realm.getAuthenticationFlowById("flow-id").getAlias()).thenReturn("flow-alias");

        IdentityProviderRepresentation rep2 = ModelToRepresentation.toRepresentation(session, realm, idpModel, false);
        assertEquals("flow-alias", rep2.getFirstBrokerLoginFlowAlias());

        // missing flow exception
        when(idpModel.getFirstBrokerLoginFlowId()).thenReturn("missing");
        when(realm.getAuthenticationFlowById("missing")).thenReturn(null);
        assertThrows(ModelException.class, () -> ModelToRepresentation.toRepresentation(session, realm, idpModel, false));
    }

    @Test
    void testToRepresentationClientBranches() {
        when(clientModel.getId()).thenReturn("c-id");
        when(clientModel.getClientId()).thenReturn("c-str");
        when(clientModel.getName()).thenReturn("name");
        when(clientModel.getAttributes()).thenReturn(new HashMap<>());
        when(clientModel.getAuthenticationFlowBindingOverrides()).thenReturn(Collections.emptyMap());
        when(clientModel.getClientScopes(anyBoolean())).thenReturn(new HashMap<>());
        when(clientModel.getRedirectUris()).thenReturn(null);
        when(clientModel.getWebOrigins()).thenReturn(null);
        when(clientModel.getRegisteredNodes()).thenReturn(Collections.emptyMap());
        when(clientModel.getProtocolMappersStream()).thenAnswer(invocation -> Stream.empty());
        when(clientModel.getRealm()).thenReturn(realm);

        // redirectUris == null
        ClientRepresentation rep1 = ModelToRepresentation.toRepresentation(clientModel, session);
        assertNull(rep1.getRedirectUris());

        // redirectUris != null
        when(clientModel.getRedirectUris()).thenReturn(Set.of("uri1"));
        ClientRepresentation rep2 = ModelToRepresentation.toRepresentation(clientModel, session);
        assertEquals(1, rep2.getRedirectUris().size());

        // registeredNodes not empty
        when(clientModel.getRegisteredNodes()).thenReturn(Map.of("node1", 1));
        ClientRepresentation rep3 = ModelToRepresentation.toRepresentation(clientModel, session);
        assertEquals(1, rep3.getRegisteredNodes().size());
    }
}
