package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.broker.provider.IdentityProviderModel;
import org.keycloak.broker.provider.IdentityProviderRepresentation;
import org.keycloak.credential.model.CredentialManager;
import org.keycloak.credential.model.PasswordCredentialModel;
import org.keycloak.credential.model.OTPCredentialModel;
import org.keycloak.credential.dto.OTPCredentialData;
import org.keycloak.credential.dto.OTPSecretData;
import org.keycloak.credential.dto.PasswordCredentialData;
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

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepresentationToModelIdentityTest {

    @Test
    void testImportRealm() {
        KeycloakSession session = mock(KeycloakSession.class);
        DatastoreProvider datastoreProvider = mock(DatastoreProvider.class);
        org.keycloak.authorization.ExportImportManager exportImportManager = mock(org.keycloak.authorization.ExportImportManager.class);
        
        when(session.getProvider(DatastoreProvider.class)).thenReturn(datastoreProvider);
        when(datastoreProvider.getExportImportManager()).thenReturn(exportImportManager);

        RealmRepresentation rep = new RealmRepresentation();
        RealmModel newRealm = mock(RealmModel.class);
        Runnable userImport = mock(Runnable.class);

        RepresentationToModel.importRealm(session, rep, newRealm, userImport);

        verify(exportImportManager).importRealm(rep, newRealm, userImport);
    }

    @Test
    void testImportRoles() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel defaultRole = mock(RoleModel.class);
        when(defaultRole.getName()).thenReturn("default");
        when(realm.getDefaultRole()).thenReturn(defaultRole);

        RolesRepresentation rolesRep = new RolesRepresentation();
        
        // 1. Null roles
        RepresentationToModel.importRoles(null, realm);

        // 2. Realm roles
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("new-role");
        rolesRep.setRealm(List.of(roleRep));
        
        RoleModel newRole = mock(RoleModel.class);
        when(realm.getRole("new-role")).thenReturn(newRole);
        when(realm.addRole("new-role")).thenReturn(newRole);
        
        RepresentationToModel.importRoles(rolesRep, realm);
        verify(realm).addRole("new-role");

        // Skip default role
        RoleRepresentation defaultRoleRep = new RoleRepresentation();
        defaultRoleRep.setName("default");
        rolesRep.setRealm(List.of(defaultRoleRep));
        RepresentationToModel.importRoles(rolesRep, realm);
        verify(realm, never()).addRole("default");

        // 3. Client roles
        ClientModel client = mock(ClientModel.class);
        when(realm.getClientByClientId("client1")).thenReturn(client);
        
        RoleRepresentation clientRoleRep = new RoleRepresentation();
        clientRoleRep.setName("client-role");
        rolesRep.setClient(Map.of("client1", List.of(clientRoleRep)));

        RoleModel clientRole = mock(RoleModel.class);
        when(client.getRole("client-role")).thenReturn(clientRole);
        when(client.addRole("client-role")).thenReturn(clientRole);

        RepresentationToModel.importRoles(rolesRep, realm);
        verify(client).addRole("client-role");

        // Error when client doesn't exist
        rolesRep.setClient(Map.of("missing-client", List.of(clientRoleRep)));
        assertThrows(RuntimeException.class, () -> RepresentationToModel.importRoles(rolesRep, realm));
    }

    @Test
    void testImportRolesWithComposites() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel defaultRole = mock(RoleModel.class);
        when(defaultRole.getName()).thenReturn("default");
        when(realm.getDefaultRole()).thenReturn(defaultRole);

        ClientModel client = mock(ClientModel.class);
        when(realm.getClientByClientId("client1")).thenReturn(client);

        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("composite-role");
        
        RolesRepresentation rolesRep = new RolesRepresentation();
        rolesRep.setRealm(List.of(roleRep));

        // Add realm composite
        RoleRepresentation realmComp = new RoleRepresentation();
        realmComp.setName("realm-comp");
        rolesRep.setRealm(List.of(roleRep, realmComp));

        RoleModel compositeRole = mock(RoleModel.class);
        RoleModel realmCompRole = mock(RoleModel.class);
        when(realm.addRole("composite-role")).thenReturn(compositeRole);
        when(realm.addRole("realm-comp")).thenReturn(realmCompRole);
        when(realm.getRole("realm-comp")).thenReturn(realmCompRole);

        rolesRep.getRealm().get(0).setComposites(new RolesRepresentation.Composites());
        rolesRep.getRealm().get(0).getComposites().setRealm(List.of("realm-comp"));

        RepresentationToModel.importRoles(rolesRep, realm);
        verify(compositeRole).addCompositeRole(realmCompRole);
    }

    @Test
    void testUpdateRealm() {
        KeycloakSession session = mock(KeycloakSession.class);
        DatastoreProvider datastoreProvider = mock(DatastoreProvider.class);
        org.keycloak.authorization.ExportImportManager exportImportManager = mock(org.keycloak.authorization.ExportImportManager.class);
        
        when(session.getProvider(DatastoreProvider.class)).thenReturn(datastoreProvider);
        when(datastoreProvider.getExportImportManager()).thenReturn(exportImportManager);

        RealmRepresentation rep = new RealmRepresentation();
        RealmModel realm = mock(RealmModel.class);

        RepresentationToModel.updateRealm(rep, realm, session);
        verify(exportImportManager).updateRealm(rep, realm);
    }

    @Test
    void testCreateUser() {
        KeycloakSession session = mock(KeycloakSession.class);
        DatastoreProvider datastoreProvider = mock(DatastoreProvider.class);
        org.keycloak.authorization.ExportImportManager exportImportManager = mock(org.keycloak.authorization.ExportImportManager.class);
        
        when(session.getProvider(DatastoreProvider.class)).thenReturn(datastoreProvider);
        when(datastoreProvider.getExportImportManager()).thenReturn(exportImportManager);

        UserRepresentation userRep = new UserRepresentation();
        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);

        when(exportImportManager.createUser(realm, userRep)).thenReturn(user);

        UserModel result = RepresentationToModel.createUser(session, realm, userRep);
        assertEquals(user, result);
    }

    @Test
    void testCreateGroups() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);
        UserRepresentation userRep = new UserRepresentation();
        
        // 1. No groups
        RepresentationToModel.createGroups(session, userRep, realm, user);

        // 2. Valid groups
        userRep.setGroups(List.of("group/path"));
        GroupModel group = mock(GroupModel.class);
        
        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.findGroupByPath(eq(session), eq(realm), eq("group/path"))).thenReturn(group);
            RepresentationToModel.createGroups(session, userRep, realm, user);
            verify(user).joinGroup(group);
        }

        // 3. Invalid groups
        userRep.setGroups(List.of("invalid/path"));
        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.findGroupByPath(any(), any(), anyString())).thenReturn(null);
            assertThrows(RuntimeException.class, () -> RepresentationToModel.createGroups(session, userRep, realm, user));
        }

        // 4. With membership handler
        userRep.setGroups(List.of("group/path"));
        Consumer<GroupModel> handler = mock(Consumer.class);
        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.findGroupByPath(any(), any(), anyString())).thenReturn(group);
            RepresentationToModel.createGroups(session, userRep, realm, user, handler);
            verify(handler).accept(group);
        }
    }

    @Test
    void testCreateFederatedIdentities() {
        KeycloakSession session = mock(KeycloakSession.class);
        UserRepresentation userRep = new UserRepresentation();
        FederatedIdentityRepresentation fed = new FederatedIdentityRepresentation();
        fed.setIdentityProvider("google");
        fed.setUserId("123");
        fed.setUserName("user");
        userRep.setFederatedIdentities(List.of(fed));

        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);
        UserProvider userProvider = mock(UserProvider.class);
        when(session.users()).thenReturn(userProvider);

        RepresentationToModel.createFederatedIdentities(userRep, session, realm, user);
        verify(userProvider).addFederatedIdentity(eq(realm), eq(user), any(FederatedIdentityModel.class));
    }

    @Test
    void testCreateCredentials() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        UserModel user = mock(UserModel.class);
        CredentialManager credentialManager = mock(CredentialManager.class);
        when(user.credentialManager()).thenReturn(credentialManager);
        
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);

        // 1. Standard credential
        UserRepresentation userRep = new UserRepresentation();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setValue("secret-value");
        userRep.setCredentials(List.of(cred));

        RepresentationToModel.createCredentials(userRep, session, realm, user, false);
        verify(credentialManager).updateCredential(any());

        // 2. Deprecated password
        userRep.setCredentials(null); // clear
        CredentialRepresentation passCred = new CredentialRepresentation();
        passCred.setType(PasswordCredentialModel.TYPE);
        passCred.setHashedSaltedValue("hash");
        passCred.setSalt("salt");
        passCred.setAlgorithm("pbkdf2");
        passCred.setHashIterations(1000);
        passCred.setValue(null);
        userRep.setCredentials(List.of(passCred));

        RepresentationToModel.createCredentials(userRep, session, realm, user, false);
        assertNotNull(passCred.getCredentialData());
        assertNotNull(passCred.getSecretData());

        // 3. Deprecated OTP
        userRep.setCredentials(null);
        CredentialRepresentation otpCred = new CredentialRepresentation();
        otpCred.setType(OTPCredentialModel.TOTP);
        otpCred.setHashedSaltedValue("otp-hash");
        otpCred.setValue(null);
        userRep.setCredentials(List.of(otpCred));

        RepresentationToModel.createCredentials(userRep, session, realm, user, false);
        assertEquals(OTPCredentialModel.TYPE, otpCred.getType());
    }

    @Test
    void testCreateRoleMappings() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setRealmRoles(List.of("role1"));
        userRep.setClientRoles(Map.of("client1", List.of("client-role1")));

        UserModel user = mock(UserModel.class);
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        RoleModel realmRole = mock(RoleModel.class);
        RoleModel clientRole = mock(RoleModel.class);

        when(realm.getRole("role1")).thenReturn(realmRole);
        when(realm.getClientByClientId("client1")).thenReturn(client);
        when(client.getRole("client-role1")).thenReturn(clientRole);

        RepresentationToModel.createRoleMappings(userRep, user, realm);
        verify(user).grantRole(realmRole);
        verify(user).grantRole(clientRole);

        // Error: user is null
        assertThrows(RuntimeException.class, () -> RepresentationToModel.createRoleMappings(userRep, null, realm));

        // Error: client missing
        userRep.setClientRoles(Map.of("missing", List.of("role")));
        when(realm.getClientByClientId("missing")).thenReturn(null);
        assertThrows(RuntimeException.class, () -> RepresentationToModel.createRoleMappings(userRep, user, realm));
    }

    @Test
    void testToModelUserConsent() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        UserConsentRepresentation consentRep = new UserConsentRepresentation();
        consentRep.setClientId("client-id");

        when(realm.getClientByClientId("client-id")).thenReturn(client);

        // 1. Basic
        UserConsentModel model = RepresentationToModel.toModel(realm, consentRep, session);
        assertNotNull(model);

        // 2. With scopes
        consentRep.setGrantedClientScopes(List.of("scope1"));
        ClientScopeModel scope = mock(ClientScopeModel.class);
        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.getClientScopeByName(eq(realm), eq("scope1"))).thenReturn(scope);
            RepresentationToModel.toModel(realm, consentRep, session);
            verify(client).addGrantedClientScope(scope);
        }

        // 3. Offline access (backwards compatibility)
        consentRep.setGrantedRealmRoles(List.of(OAuth2Constants.OFFLINE_ACCESS));
        ClientScopeModel offlineScope = mock(ClientScopeModel.class);
        when(client.getClientScopes(false)).thenReturn(Map.of(OAuth2Constants.OFFLINE_ACCESS, offlineScope));
        RepresentationToModel.toModel(realm, consentRep, session);
        verify(client).addGrantedClientScope(offlineScope);
    }
}
