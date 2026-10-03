package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Stream;

import org.keycloak.authorization.policy.evaluation.Realm;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.AccessToken.Access;

public class KeycloakModelUtilsRoleTest {

    @Test
    public void testSearchFor_directComposite() {
        RoleModel target = mock(RoleModel.class);
        when(target.getId()).thenReturn("role1");

        RoleModel composite = mock(RoleModel.class);
        when(composite.isComposite()).thenReturn(true);
        when(composite.getCompositesStream()).thenReturn(Stream.of(target));
        when(composite.getId()).thenReturn("c1");

        Set<String> visited = new HashSet<>();
        boolean result = KeycloakModelUtils.searchFor(target, composite, visited);
        Assertions.assertTrue(result);
    }

    @Test
    public void testSearchFor_recursiveComposite() {
        RoleModel target = mock(RoleModel.class);
        when(target.getId()).thenReturn("role2");

        RoleModel inner = mock(RoleModel.class);
        when(inner.isComposite()).thenReturn(true);
        when(inner.getId()).thenReturn("inner");

        RoleModel composite = mock(RoleModel.class);
        when(composite.isComposite()).thenReturn(true);
        when(composite.getCompositesStream()).thenReturn(Stream.of(inner));
        when(composite.getId()).thenReturn("c2");

        when(inner.getCompositesStream()).thenReturn(Stream.of(target));

        Set<String> visited = new HashSet<>();
        boolean result = KeycloakModelUtils.searchFor(target, composite, visited);
        Assertions.assertTrue(result);
    }

    @Test
    public void testSearchFor_notComposite() {
        RoleModel target = mock(RoleModel.class);
        RoleModel composite = mock(RoleModel.class);
        when(composite.isComposite()).thenReturn(false);
        when(composite.getId()).thenReturn("c3");
        Set<String> visited = new HashSet<>();
        boolean result = KeycloakModelUtils.searchFor(target, composite, visited);
        Assertions.assertFalse(result);
    }

    @Test
    public void testGetRoleFromStringWithClientRole() {
        String roleName = "app.role";
        ClientModel client = mock(ClientModel.class);
        RoleModel role = mock(RoleModel.class);
        RealmModel realm = mock(RealmModel.class);
        when(realm.getClientByClientId("app")).thenReturn(client);
        when(client.getRole("role")).thenReturn(role);
        when(realm.getRole(anyString())).thenReturn(null);

        RoleModel result = KeycloakModelUtils.getRoleFromString(null, realm, roleName);
        Assertions.assertSame(role, result);
    }

    @Test
    public void testGetRoleFromStringWithRealmRole() {
        String roleName = "realmRole";
        RoleModel role = mock(RoleModel.class);
        RealmModel realm = mock(RealmModel.class);
        when(realm.getRole("realmRole")).thenReturn(role);

        RoleModel result = KeycloakModelUtils.getRoleFromString(null, realm, roleName);
        Assertions.assertSame(role, result);
    }

    @Test
    public void testParseRole_withSeparator() {
        String[] parts = KeycloakModelUtils.parseRole("app.role1");
        Assertions.assertArrayEquals(new String[]{"app", "role1"}, parts);
    }

    @Test
    public void testParseRole_withoutSeparator() {
        String[] parts = KeycloakModelUtils.parseRole("singleRole");
        Assertions.assertArrayEquals(new String[]{null, "singleRole"}, parts);
    }

    @Test
    public void testBuildRoleQualifier_nullClient() {
        Assertions.assertEquals("role1", KeycloakModelUtils.buildRoleQualifier(null, "role1"));
    }

    @Test
    public void testBuildRoleQualifier_withClient() {
        Assertions.assertEquals("app.role1", KeycloakModelUtils.buildRoleQualifier("app", "role1"));
    }

    @Test
    public void testGetRoleByName_withClient() {
        ClientModel client = mock(ClientModel.class);
        RoleModel role = mock(RoleModel.class);
        RealmModel realm = mock(RealmModel.class);
        when(client.getId()).thenReturn("c1");
        when(realm.getClientByClientId("c1")).thenReturn(client);
        when(client.getRole("roleName")).thenReturn(role);

        RoleModel result = KeycloakModelUtils.getRoleByName(realm, "c1", "roleName");
        Assertions.assertSame(role, result);
    }

    @Test
    public void testGetRoleByName_withRealm() {
        RoleModel role = mock(RoleModel.class);
        RealmModel realm = mock(RealmModel.class);
        when(realm.getRole("roleName")).thenReturn(role);

        RoleModel result = KeycloakModelUtils.getRoleByName(realm, null, "roleName");
        Assertions.assertSame(role, result);
    }

    @Test
    public void testRemoveTransientAdminRoles_filtersAdmin() {
        UserModel user = mock(UserModel.class);
        when(user.hasRole(any())).thenReturn(false);
        RealmModel realm = mock(RealmModel.class);
        ClientModel realmManagementClient = mock(ClientModel.class);
        when(realmManagementClient.getClientId()).thenReturn(Constants.REALM_MANAGEMENT_CLIENT_ID);
        RoleModel adminRole = mock(RoleModel.class);
        when(adminRole.getName()).thenReturn("admin");
        when(adminRole.getContainer()).thenReturn(realmManagementClient);
        RoleModel normalRole = mock(RoleModel.class);
        when(normalRole.getName()).thenReturn("user");
        when(realm.getRole("admin")).thenReturn(adminRole);
        when(realm.getRole("user")).thenReturn(normalRole);

        Set<String> roles = new HashSet<>(Arrays.asList("admin", "user"));
        Access access = mock(Access.class);
        when(access.getRoles()).thenReturn(roles);

        KeycloakModelUtils.removeTransientAdminRoles(realm, null, user, access);

        Assertions.assertFalse(roles.contains("admin"));
        Assertions.assertTrue(roles.contains("user"));
    }
}
