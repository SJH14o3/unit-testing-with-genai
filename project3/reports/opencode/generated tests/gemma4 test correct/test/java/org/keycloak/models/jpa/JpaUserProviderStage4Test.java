package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JpaUserProviderStage4Test {

    @Mock
    private KeycloakSession session;

    @Mock
    private EntityManager em;

    @Mock
    private RealmModel realm;

    @Mock
    private IdentityProviderModel identityProvider;

    @Mock
    private RoleModel role;

    @Mock
    private ClientModel client;

    @Mock
    private IdentityProviderModel clientScope;

    @Mock
    private GroupModel group;

    @Mock
    private ProtocolMapperModel protocolMapper;

    @Mock
    private ComponentModel component;

    @Mock
    private Query mockQuery;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JpaUserProvider(session, em);
        lenient().when(em.createNamedQuery(anyString())).thenReturn(mockQuery);
        lenient().when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    }

    @Test
    void testPreRemove_IdentityProvider() {
        when(realm.getId()).thenReturn("realm-id");
        when(identityProvider.getAlias()).thenReturn("idp-alias");

        provider.preRemove(realm, identityProvider);

        verify(mockQuery).executeUpdate();
        verify(em).createNamedQuery("deleteFederatedIdentityByProvider");
        verify(mockQuery).setParameter("realmId", "realm-id");
        verify(mockQuery).setParameter("providerAlias", "idp-alias");
    }

    @Test
    void testPreRemove_Realm() {
        when(realm.getId()).thenReturn("realm-id");

        provider.preRemove(realm);

        verify(em, times(11)).createNamedQuery(anyString());
        verify(mockQuery, times(11)).executeUpdate();
    }

    @Test
    void testPreRemove_Role() {
        when(role.getId()).thenReturn("role-id");

        provider.preRemove(realm, role);

        verify(em).createNamedQuery("deleteUserRoleMappingsByRole");
        verify(mockQuery).setParameter("roleId", "role-id");
        verify(mockQuery).executeUpdate();
    }

    @Test
    void testPreRemove_Client_Local() {
        when(client.getId()).thenReturn("local-client-id");

        provider.preRemove(realm, client);

        verify(em).createNamedQuery("deleteUserConsentClientScopesByClient");
        verify(em).createNamedQuery("deleteUserConsentsByClient");
        verify(em).createNamedQuery("deleteIssuedVcsByClient");
        verify(mockQuery, times(3)).setParameter("clientId", "local-client-id");
        verify(mockQuery, times(3)).executeUpdate();
    }

    @Test
    void testPreRemove_Client_External() {
        // A clientId with ":" is treated as external by StorageId
        when(client.getId()).thenReturn("provider-id:external-client-id");

        provider.preRemove(realm, client);

        verify(em).createNamedQuery("deleteUserConsentClientScopesByClient");
        verify(em).createNamedQuery("deleteUserConsentsByClient");
        verify(em).createNamedQuery("deleteIssuedVcsByClient");
        verify(mockQuery, times(3)).setParameter("clientId", "provider-id:external-client-id");
        verify(mockQuery, times(3)).executeUpdate();
    }

    @Test
    void testPreRemove_ProtocolMapper() {
        assertDoesNotThrow(() -> provider.preRemove(protocolMapper));
        verify(em, never()).createNamedQuery(anyString());
    }

    @Test
    void testPreRemove_ClientScope() {
        when(realm.getId()).thenReturn("realm-id");
        provider.preRemove(realm, clientScope);

        verify(em).createNamedQuery("deleteFederatedIdentityByProvider");
        verify(mockQuery).setParameter("realmId", "realm-id");
        verify(mockQuery).executeUpdate();
    }

    @Test
    void testPreRemove_Group() {
        when(group.getId()).thenReturn("group-id");

        provider.preRemove(realm, group);

        verify(em).createNamedQuery("deleteUserGroupMembershipsByGroup");
        verify(mockQuery).setParameter("groupId", "group-id");
        verify(mockQuery).executeUpdate();
    }

    @Test
    void testRemoveImportedUsers() {
        when(realm.getId()).thenReturn("realm-id");

        provider.removeImportedUsers(realm, "storage-id");

        verify(em, times(9)).createNamedQuery(anyString());
        verify(mockQuery, times(18)).setParameter(anyString(), any());
        verify(mockQuery, times(9)).executeUpdate();
    }

    @Test
    void testUnlinkUsers() {
        when(realm.getId()).thenReturn("realm-id");

        provider.unlinkUsers(realm, "storage-id");

        verify(em).createNamedQuery("unlinkUsers");
        verify(mockQuery).setParameter("realmId", "realm-id");
        verify(mockQuery).setParameter("link", "storage-id");
        verify(mockQuery).executeUpdate();
    }

    @Test
    void testPreRemove_Component_UserStorage() {
        when(component.getProviderType()).thenReturn(org.keycloak.storage.UserStorageProvider.class.getName());
        when(component.getId()).thenReturn("storage-id");
        when(realm.getId()).thenReturn("realm-id");

        provider.preRemove(realm, component);

        verify(em, atLeastOnce()).createNamedQuery(anyString());
        verify(mockQuery, atLeastOnce()).executeUpdate();
    }

    @Test
    void testPreRemove_Component_ClientStorage() {
        when(component.getProviderType()).thenReturn(org.keycloak.storage.client.ClientStorageProvider.class.getName());
        when(component.getId()).thenReturn("client-storage-id");

        provider.preRemove(realm, component);

        verify(em).createNamedQuery("deleteUserConsentClientScopesByClientStorageProvider");
        verify(em).createNamedQuery("deleteUserConsentsByClientStorageProvider");
        verify(mockQuery, times(2)).setParameter("clientStorageProvider", "client-storage-id");
        verify(mockQuery, times(2)).executeUpdate();
    }
}
