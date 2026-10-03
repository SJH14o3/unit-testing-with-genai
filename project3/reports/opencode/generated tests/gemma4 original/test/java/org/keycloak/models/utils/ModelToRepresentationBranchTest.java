package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.authentication.otp.OTPApplicationProvider;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.store.StoreFactory;
import org.keycloak.authorization.store.ResourceServerStore;
import org.keycloak.common.Profile;
import org.keycloak.credential.CredentialMetadata;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.OTPCredentialModel;
import org.keycloak.events.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.*;
import org.keycloak.models.oid4vci.CredentialScopeModel;
import org.keycloak.models.otp.OTPPolicy;
import org.keycloak.representations.account.CredentialMetadataRepresentation;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.*;
import org.keycloak.userprofile.UserProfileProvider;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelToRepresentationBranchTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private UserModel user;

    @Mock
    private CredentialManager credentialManager;

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

    @Test
    void testToRepresentationUserBranches() {
        when(user.getId()).thenReturn("user-id");
        when(user.getCreatedTimestamp()).thenReturn(12345L);
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn(null);
        when(user.credentialManager()).thenReturn(credentialManager);
        when(credentialManager.isConfiguredFor(anyString())).thenReturn(false);
        when(credentialManager.getDisableableCredentialTypesStream()).thenReturn(Stream.empty());
        when(user.getRequiredActionsStream()).thenReturn(Stream.empty());
        
        // Case 1: setUserAttributes = true, attributes null
        UserRepresentation rep1 = ModelToRepresentation.toRepresentation(session, realm, user, new UserRepresentation(), true);
        assertNotNull(rep1);

        // Case 2: setUserAttributes = true, attributes non-null, but copy empty (all removed)
        when(user.getAttributes()).thenReturn(Map.of(UserModel.FIRST_NAME, "John"));
        UserRepresentation rep2 = ModelToRepresentation.toRepresentation(session, realm, user, new UserRepresentation(), true);
        // After removing FIRST_NAME, copy is empty.

        // Case 3: setUserAttributes = true, attributes non-null, copy not empty
        when(user.getAttributes()).thenReturn(Map.of("custom", "val", UserModel.FIRST_NAME, "John"));
        UserRepresentation rep3 = ModelToRepresentation.toRepresentation(session, realm, user, new UserRepresentation(), true);
        assertTrue(rep3.getAttributes().containsKey("custom"));

        // Case 4: setUserAttributes = false
        UserRepresentation rep4 = ModelToRepresentation.toRepresentation(session, realm, user, new UserRepresentation(), false);
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
        UserRepresentation rep1 = ModelToRepresentation.toBriefRepresentation(user, new UserRepresentation(), true);
        assertEquals("user-name", rep1.getUsername());

        // setUserAttributes = false
        UserRepresentation rep2 = ModelToRepresentation.toBriefRepresentation(user, new UserRepresentation(), false);
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
        when(realm.getSslRequired()).thenReturn(org.keycloak.models.SslRequired.REQUIRED);
        when(realm.getEventsExpiration()).thenReturn(0); // Branch 520
        when(realm.getEventsListenersStream()).thenReturn(Stream.empty());
        when(realm.getEnabledEventTypesStream()).thenReturn(Stream.empty());
        when(realm.getAttributes()).thenReturn(new HashMap<>());
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
        when(realm.getAuthenticatorConfigsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        when(realm.getRequiredCredentialsStream()).thenReturn(Stream.empty());
        when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
        when(realm.getDefaultRole()).thenReturn(null);
        when(realm.isAdminPermissionsClient()).thenReturn(null);
        when(realm.getOAuth2DeviceConfig()).thenReturn(mock(org.keycloak.models.OAuth2DeviceConfigModel.class));
        when(realm.getOAuth2DeviceConfig().getLifespan()).thenReturn(100);
        when(realm.getOAuth2DeviceConfig().getPoolingInterval()).thenReturn(10);
        when(realm.getSmtpConfig()).thenReturn(new HashMap<>());
        when(realm.getBrowserSecurityHeaders()).thenReturn(new HashMap<>());
        when(realm.getOTPPolicy()).thenReturn(mock(OTPPolicy.class));
        when(realm.getWebAuthnPolicy()).thenReturn(mock(org.keycloak.webauthn.WebAuthnPolicy.class));
        when(realm.getWebAuthnPolicyPasswordless()).thenReturn(mock(org.keycloak.webauthn.WebAuthnPolicyPasswordless.class));
        when(realm.getCibaPolicy()).thenReturn(mock(org.keycloak.ciba.CibaConfig.class));
        when(realm.getParPolicy()).thenReturn(mock(org.keycloak.par.ParConfig.class));
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
    void testIsRealmClientBranches() {
        when(realm.getName()).thenReturn("admin");
        
        // clientId == null
        assertFalse(ModelToRepresentation.isRealmClient(null, realm, session));

        // Constants.BROKER_SERVICE_CLIENT_ID
        assertTrue(ModelToRepresentation.isRealmClient(Constants.BROKER_SERVICE_CLIENT_ID, realm, session));

        // Admin realm logic
        // case: clientId ends with -realm and realm exists
        when(session.realms()).thenReturn(mock(org.keycloak.realms.RealmProvider.class));
        when(session.realms().getRealmByName("other")).thenReturn(mock(RealmModel.class));
        assertTrue(ModelToRepresentation.isRealmClient("other-realm", realm, session));
        assertFalse(ModelToRepresentation.isRealmClient("other", realm, session));

        // Non-admin realm logic
        when(realm.getName()).thenReturn("user-realm");
        assertTrue(ModelToRepresentation.isRealmClient(Constants.REALM_MANAGEMENT_CLIENT_ID, realm, session));
        assertFalse(ModelToRepresentation.isRealmClient("some-client", realm, session));
    }

    @Test
    void testToLocalizedMessageBranches() {
        // message == null
        assertNull(ModelToRepresentation.toLocalizedMessage(null));

        // message.getParameters() == null
        CredentialMetadata.LocalizedMessage msg1 = mock(CredentialMetadata.LocalizedMessage.class);
        when(msg1.getKey()).thenReturn("key1");
        when(msg1.getParameters()).thenReturn(null);
        var res1 = ModelToRepresentation.toLocalizedMessage(msg1);
        assertEquals("key1", res1.getKey());

        // message.getParameters() != null
        CredentialMetadata.LocalizedMessage msg2 = mock(CredentialMetadata.LocalizedMessage.class);
        when(msg2.getKey()).thenReturn("key2");
        when(msg2.getParameters()).thenReturn(new String[]{"p1"});
        var res2 = ModelToRepresentation.toLocalizedMessage(msg2);
        assertArrayEquals(new String[]{"p1"}, res2.getParameters());
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
        when(clientModel.getAuthenticationFlowBindingOverrides()).thenReturn(new ArrayList<>());
        when(clientModel.getClientScopes(anyBoolean())).thenReturn(new HashMap<>());
        when(clientModel.getRedirectUris()).thenReturn(null);
        when(clientModel.getWebOrigins()).thenReturn(null);
        when(clientModel.getRegisteredNodes()).thenReturn(Collections.emptyMap());
        when(clientModel.getProtocolMappersStream()).thenReturn(Stream.empty());
        when(clientModel.getRealm()).thenReturn(realm);

        // redirectUris == null
        ClientRepresentation rep1 = ModelToRepresentation.toRepresentation(clientModel, session);
        assertNull(rep1.getRedirectUris());

        // redirectUris != null
        when(clientModel.getRedirectUris()).thenReturn(Set.of("uri1"));
        ClientRepresentation rep2 = ModelToRepresentation.toRepresentation(clientModel, session);
        assertEquals(1, rep2.getRedirectUris().size());

        // registeredNodes not empty
        when(clientModel.getRegisteredNodes()).thenReturn(Map.of("node1", "val1"));
        ClientRepresentation rep3 = ModelToRepresentation.toRepresentation(clientModel, session);
        assertEquals(1, rep3.getRegisteredNodes().size());
    }
}
