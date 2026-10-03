package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.broker.provider.IdentityProviderModel;
import org.keycloak.broker.provider.IdentityProviderRepresentation;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.dto.OTPCredentialData;
import org.keycloak.credential.dto.PasswordCredentialData;
import org.keycloak.credential.dto.OTPSecretData;
import org.keycloak.credential.model.CredentialManager;
import org.keycloak.credential.model.PasswordCredentialModel;
import org.keycloak.credential.model.OTPCredentialModel;
import org.keycloak.credential.model.CredentialManager;
import org.keycloak.deployment.DeployedConfigurationsManager;
import org.keycloak.models.*;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;
import org.keycloak.models.credential.dto.PasswordCredentialData;
import org.keycloak.models.group.GroupModel;
import org.keycloak.models.user.UserProvider;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.oid4vc.IssuedVerifiableCredentialRepresentation;
import org.keycloak.representations.idm.oid4vc.UserVerifiableCredentialRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepresentationToModelUserTest {

    @Test
    void testImportGroup() {
        RealmModel realm = mock(RealmModel.class);
        GroupModel parent = mock(GroupModel.class);
        GroupRepresentation groupRep = new GroupRepresentation();
        groupRep.setId("group-id");
        groupRep.setName("group-name");
        groupRep.setDescription("group-desc");
        groupRep.setAttributes(Map.of("attr1", List.of("val1")));
        groupRep.setRealmRoles(List.of("role1"));
        groupRep.setClientRoles(Map.of("client1", List.of("client-role1")));
        groupRep.setSubGroups(List.of(new GroupRepresentation()));

        GroupModel newGroup = mock(GroupModel.class);
        RoleModel realmRole = mock(RoleModel.class);
        ClientModel client = mock(ClientModel.class);
        RoleModel clientRole = mock(RoleModel.class);
        GroupRepresentation subGroupRep = groupRep.getSubGroups().get(0);
        subGroupRep.setName("sub-group");

        when(realm.createGroup(eq("group-id"), eq("group-name"), eq(parent))).thenReturn(newGroup);
        when(realm.getRole("role1")).thenReturn(realmRole);
        when(realm.getClientByClientId("client1")).thenReturn(client);
        when(client.getRole("client-role1")).thenReturn(clientRole);

        RepresentationToModel.importGroup(realm, parent, groupRep);

        verify(newGroup).setDescription("group-desc");
        verify(newGroup).setAttribute("attr1", List.of("val1"));
        verify(newGroup).grantRole(realmRole);
        verify(newGroup).grantRole(clientRole);
        verify(realm).createGroup(eq("group-id"), eq("group-name"), eq(parent));
        // Verify recursion: subGroupRep is passed to importGroup
    }

    @Test
    void testImportGroupClientRoleMissing() {
        RealmModel realm = mock(RealmModel.class);
        GroupModel parent = mock(GroupModel.class);
        GroupRepresentation groupRep = new GroupRepresentation();
        groupRep.setClientRoles(Map.of("missing-client", List.of("role")));

        when(realm.getClientByClientId("missing-client")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> RepresentationToModel.importGroup(realm, parent, groupRep));
    }

    @Test
    void testCreateGroups() {
        KeycloakSession session = mock(KeycloakSession.class);
        UserRepresentation userRep = new UserRepresentation();
        userRep.setGroups(List.of("group/path"));
        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);

        GroupModel group = mock(GroupModel.class);
        when(realm.getGroupByPath(anyString(), eq(realm))).thenReturn(group); // Note: KeycloakModelUtils.findGroupByPath uses session/realm

        // Actually KeycloakModelUtils.findGroupByPath uses session, realm, and path.
        // Since it's a static utility, we might need to mock it or ensure the dependency is met.
        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.findGroupByPath(any(), any(), anyString())).thenReturn(group);
            
            RepresentationToModel.createGroups(session, userRep, realm, user);
            verify(user).joinGroup(group);
        }

        // Test exception
        userRep.setGroups(List.of("invalid/path"));
        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.findGroupByPath(any(), any(), anyString())).thenReturn(null);
            assertThrows(RuntimeException.class, () -> RepresentationToModel.createGroups(session, userRep, realm, user));
        }
    }

    @Test
    void testCreateFederatedIdentities() {
        KeycloakSession session = mock(KeycloakSession.class);
        UserRepresentation userRep = new UserRepresentation();
        FederatedIdentityRepresentation fed = new FederatedIdentityRepresentation();
        fed.setIdentityProvider("idp");
        fed.setUserId("user-id");
        fed.setUserName("username");
        userRep.setFederatedIdentities(List.of(fed));

        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);
        UserProvider userProvider = mock(UserProvider.class);
        when(session.users()).thenReturn(userProvider);

        RepresentationToModel.createFederatedIdentities(userRep, session, realm, user);

        verify(userProvider).addFederatedIdentity(eq(realm), eq(user), any(FederatedIdentityModel.class));
    }

    @Test
    void testCreateCredentialsDeprecatedPassword() {
        KeycloakSession session = mock(KeycloakSession.class);
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("test-user");
        
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(PasswordCredentialModel.TYPE);
        cred.setAlgorithm("pbkdf2-sha256");
        cred.setHashIterations(1000);
        cred.setHashedSaltedValue("hash");
        cred.setSalt("salt");
        cred.setValue(null); // trigger deprecated path
        userRep.setCredentials(List.of(cred));

        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);
        CredentialManager credentialManager = mock(CredentialManager.class);
        when(user.credentialManager()).thenReturn(credentialManager);
        
        // Mock context for session.getContext().setRealm(realm)
        KeycloakContext context = mock(KeycloakContext.class);
        RealmModel origRealm = mock(RealmModel.class);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(origRealm);

        RepresentationToModel.createCredentials(userRep, session, realm, user, false);

        assertNotNull(cred.getCredentialData());
        assertNotNull(cred.getSecretData());
        assertTrue(cred.getSecretData().contains("hash"));
    }

    @Test
    void testCreateRoleMappings() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setRealmRoles(List.of("realm-role"));
        userRep.setClientRoles(Map.of("client1", List.of("client-role")));

        UserModel user = mock(UserModel.class);
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        RoleModel realmRole = mock(RoleModel.class);
        RoleModel clientRole = mock(RoleModel.class);

        when(realm.getRole("realm-role")).thenReturn(realmRole);
        when(realm.getClientByClientId("client1")).thenReturn(client);
        when(client.getRole("client-role")).thenReturn(clientRole);

        RepresentationToModel.createRoleMappings(userRep, user, realm);

        verify(user).grantRole(realmRole);
        verify(user).grantRole(clientRole);
    }

    @Test
    void testCreateRoleMappingsClientMissing() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setClientRoles(Map.of("missing-client", List.of("role")));
        UserModel user = mock(UserModel.class);
        RealmModel realm = mock(RealmModel.class);

        when(realm.getClientByClientId("missing-client")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> RepresentationToModel.createRoleMappings(userRep, user, realm));
    }

    @Test
    void testToModelUserConsent() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        UserConsentRepresentation consentRep = new UserConsentRepresentation();
        consentRep.setClientId("client-id");
        consentRep.setGrantedClientScopes(List.of("scope1"));
        consentRep.setGrantedRealmRoles(List.of(OAuth2Constants.OFFLINE_ACCESS));

        ClientScopeModel scope = mock(ClientScopeModel.class);
        when(realm.getClientByClientId("client-id")).thenReturn(client);
        when(client.getClientScopes(false)).thenReturn(Map.of(OAuth2Constants.OFFLINE_ACCESS, scope));

        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.getClientScopeByName(eq(realm), anyString())).thenReturn(scope);

            UserConsentModel model = RepresentationToModel.toModel(realm, consentRep, session);

            assertNotNull(model);
            verify(client).getClientScopes(false);
        }
    }
}
