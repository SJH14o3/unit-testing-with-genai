package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsMiscTest {

    @Test
    public void testGetMasterRealmAdminManagementClientId() {
        String clientId = KeycloakModelUtils.getMasterRealmAdminManagementClientId("myrealm");
        Assertions.assertEquals("myrealm-realm", clientId);
    }

    @Test
    public void testToLowerCaseSafe_null() {
        Assertions.assertNull(KeycloakModelUtils.toLowerCaseSafe(null));
    }

    @Test
    public void testToLowerCaseSafe_value() {
        Assertions.assertEquals("abc", KeycloakModelUtils.toLowerCaseSafe("AbC"));
    }

    @Test
    public void testFindGroupByPath_escapedSlashes() {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        GroupProviderFactory groupProviderFactory = mock(GroupProviderFactory.class);
        RealmModel realm = mock(RealmModel.class);
        GroupProvider groupProvider = mock(GroupProvider.class);
        GroupModel parent = mock(GroupModel.class);
        GroupModel child = mock(GroupModel.class);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        doReturn(groupProviderFactory).when(sessionFactory).getProviderFactory(GroupProvider.class);
        when(groupProviderFactory.escapeSlashesInGroupPath()).thenReturn(true);
        when(session.groups()).thenReturn(groupProvider);
        when(parent.getParentId()).thenReturn(null);
        when(groupProvider.getGroupByName(realm, null, "my\\~group")).thenReturn(parent);
        when(groupProvider.getGroupByName(realm, parent, "child")).thenReturn(child);

        GroupModel result = KeycloakModelUtils.findGroupByPath(session, realm, "/my\\~group/child");
        Assertions.assertSame(child, result);
    }
}
