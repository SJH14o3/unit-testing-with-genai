package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.common.util.Time;

public class KeycloakModelUtilsClientTest {

    @Test
    public void testCreateManagementClient_setsBearerOnly() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        when(realm.addClient(anyString())).thenReturn(client);

        ClientModel result = KeycloakModelUtils.createManagementClient(realm, "mgmt-client");
        Assertions.assertSame(client, result);
        verify(client).setBearerOnly(true);
        verify(client).setClientAuthenticatorType(KeycloakModelUtils.getDefaultClientAuthenticatorType());
    }

    @Test
    public void testCreatePublicClient_setsPublicClient() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        when(realm.addClient(anyString())).thenReturn(client);

        ClientModel result = KeycloakModelUtils.createPublicClient(realm, "public-client");
        Assertions.assertSame(client, result);
        verify(client).setPublicClient(true);
        verify(client).setClientAuthenticatorType(KeycloakModelUtils.getDefaultClientAuthenticatorType());
    }

    @Test
    public void testSetupDefaultRole_createsAndAssignsRole() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel role = mock(RoleModel.class);
        when(realm.addRole("defaultRole")).thenReturn(role);

        KeycloakModelUtils.setupDefaultRole(realm, "defaultRole");
        verify(role).setDescription("${role_default-roles}");
        verify(realm).setDefaultRole(role);
    }

    @Test
    public void testSetupOfflineRole_existingRole() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel existing = mock(RoleModel.class);
        when(realm.getRole("offline_access")).thenReturn(existing);

        RoleModel result = KeycloakModelUtils.setupOfflineRole(realm);
        Assertions.assertSame(existing, result);
    }

    @Test
    public void testSetupOfflineRole_newRole() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel newRole = mock(RoleModel.class);
        when(realm.getRole("offline_access")).thenReturn(null);
        when(realm.addRole("offline_access")).thenReturn(newRole);

        RoleModel result = KeycloakModelUtils.setupOfflineRole(realm);
        Assertions.assertSame(newRole, result);
        verify(newRole).setDescription("${role_offline-access}");
        verify(realm).addToDefaultRoles(newRole);
    }

    @Test
    public void testSetupDeleteAccount_ensuresRoleExistsAndSetsDescription() {
        ClientModel client = mock(ClientModel.class);
        RoleModel deleteRole = mock(RoleModel.class);
        when(client.getRole("delete-account")).thenReturn(null);
        when(client.addRole("delete-account")).thenReturn(deleteRole);

        KeycloakModelUtils.setupDeleteAccount(client);
        verify(client).addRole("delete-account");
        verify(deleteRole).setDescription("${role_delete-account}");
    }
}
