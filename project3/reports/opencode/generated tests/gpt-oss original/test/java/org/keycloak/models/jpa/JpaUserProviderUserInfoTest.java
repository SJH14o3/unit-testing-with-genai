package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.util.Time;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserProviderUserInfoTest {
    @Mock
    private KeycloakSession session;
    @Mock
    private EntityManager em;
    @Mock
    private RealmModel realm;
    @Mock
    private UserModel user;
    @Mock
    private ClientModel client;
    @Mock
    private RoleModel role;
    @Mock
    private GroupModel group;

    @Test
    public void testGetServiceAccount_noResults() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<UserEntity> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("getRealmUserByServiceAccount", UserEntity.class))
                .thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of());
        assertNull(provider.getServiceAccount(client));
    }

    @Test
    public void testGetServiceAccount_multipleResults() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<UserEntity> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("getRealmUserByServiceAccount", UserEntity.class))
                .thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of(new UserEntity(), new UserEntity()));
        assertThrows(IllegalStateException.class, () -> provider.getServiceAccount(client));
    }

    @Test
    public void testGetServiceAccount_singleResult() {
        var provider = new JpaUserProvider(session, em);
        UserEntity ue = mock(UserEntity.class);
        TypedQuery<UserEntity> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("getRealmUserByServiceAccount", UserEntity.class))
                .thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of(ue));
        UserModel result = provider.getServiceAccount(client);
        assertNotNull(result);
    }

    @Test
    public void testGrantToAllUsers_matchingContainer() {
        var provider = new JpaUserProvider(session, em);
        when(realm.equals(role.isClientRole() ? ((ClientModel)role.getContainer()).getRealm() : role.getContainer()))
                .thenReturn(true);
        TypedQuery<?> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("grantRoleToAllUsers", null)).thenReturn(tq);
        provider.grantToAllUsers(realm, role);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("roleId", role.getId());
        verify(tq).executeUpdate();
    }

    @Test
    public void testGrantToAllUsers_nonMatchingContainer() {
        var provider = new JpaUserProvider(session, em);
        when(realm.equals(role.isClientRole() ? ((ClientModel)role.getContainer()).getRealm() : role.getContainer()))
                .thenReturn(false);
        provider.grantToAllUsers(realm, role);
        verify(em, never()).createNamedQuery("grantRoleToAllUsers");
    }

    @Test
    public void testSetNotBeforeForUser_nullEntity() {
        var provider = new JpaUserProvider(session, em);
        when(em.getReference(UserEntity.class, user.getId())).thenReturn(null);
        assertThrows(ModelException.class, () -> provider.setNotBeforeForUser(realm, user, 42));
    }

    @Test
    public void testSetNotBeforeForUser_success() {
        var provider = new JpaUserProvider(session, em);
        UserEntity ue = mock(UserEntity.class);
        when(em.getReference(UserEntity.class, user.getId())).thenReturn(ue);
        provider.setNotBeforeForUser(realm, user, 42);
        verify(ue).setNotBefore(42);
    }

    @Test
    public void testGetNotBeforeOfUser_nullEntity() {
        var provider = new JpaUserProvider(session, em);
        when(em.getReference(UserEntity.class, user.getId())).thenReturn(null);
        assertThrows(ModelException.class, () -> provider.getNotBeforeOfUser(realm, user));
    }

    @Test
    public void testGetNotBeforeOfUser_success() {
        var provider = new JpaUserProvider(session, em);
        UserEntity ue = mock(UserEntity.class);
        when(em.getReference(UserEntity.class, user.getId())).thenReturn(ue);
        when(ue.getNotBefore()).thenReturn(99);
        int result = provider.getNotBeforeOfUser(realm, user);
        assertEquals(99, result);
    }

    @Test
    public void testGetUsersCount_groupIdsNull_returnsZero() {
        var provider = new JpaUserProvider(session, em);
        int count = provider.getUsersCount(realm, (Set<String>) null);
        assertEquals(0, count);
    }

    @Test
    public void testGetUsersCount_includesServiceAccountFalse() {
        var provider = new JpaUserProvider(session, em);
        // Setup minimal named query execution
        TypedQuery<Long> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("userCountInGroups", Long.class)).thenReturn(tq);
        when(tq.getSingleResult()).thenReturn(5L);
        int count = provider.getUsersCount(realm, true);
        assertEquals(5, count);
    }
}

