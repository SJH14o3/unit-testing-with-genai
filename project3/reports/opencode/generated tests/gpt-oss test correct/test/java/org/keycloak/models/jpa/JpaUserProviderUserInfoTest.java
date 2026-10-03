package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.Profile;
import org.keycloak.common.util.Time;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @BeforeAll
    public static void initProfile() {
        // the authorization filters applied to criteria queries rely on the resolved profile
        if (Profile.getInstance() == null) {
            Profile.defaults();
        }
    }

    private static UserEntity userEntity(String id) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        return entity;
    }

    @Test
    public void testGetServiceAccount_noResults() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<UserEntity> tq = mock(TypedQuery.class);
        when(client.getRealm()).thenReturn(realm);
        when(em.createNamedQuery("getRealmUserByServiceAccount", UserEntity.class))
                .thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of());
        assertNull(provider.getServiceAccount(client));
    }

    @Test
    public void testGetServiceAccount_multipleResults() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<UserEntity> tq = mock(TypedQuery.class);
        when(client.getRealm()).thenReturn(realm);
        when(em.createNamedQuery("getRealmUserByServiceAccount", UserEntity.class))
                .thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of(userEntity("u1"), userEntity("u2")));
        assertThrows(IllegalStateException.class, () -> provider.getServiceAccount(client));
    }

    @Test
    public void testGetServiceAccount_singleResult() {
        var provider = new JpaUserProvider(session, em);
        UserEntity ue = mock(UserEntity.class);
        TypedQuery<UserEntity> tq = mock(TypedQuery.class);
        when(client.getRealm()).thenReturn(realm);
        when(em.createNamedQuery("getRealmUserByServiceAccount", UserEntity.class))
                .thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of(ue));
        UserModel result = provider.getServiceAccount(client);
        assertNotNull(result);
    }

    @Test
    public void testGrantToAllUsers_matchingContainer() {
        var provider = new JpaUserProvider(session, em);
        when(role.isClientRole()).thenReturn(false);
        when(role.getContainer()).thenReturn(realm);
        Query tq = mock(Query.class, Answers.RETURNS_SELF);
        when(em.createNamedQuery("grantRoleToAllUsers")).thenReturn(tq);
        provider.grantToAllUsers(realm, role);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("roleId", role.getId());
        verify(tq).executeUpdate();
    }

    @Test
    public void testGrantToAllUsers_nonMatchingContainer() {
        var provider = new JpaUserProvider(session, em);
        when(role.isClientRole()).thenReturn(false);
        when(role.getContainer()).thenReturn(mock(RealmModel.class));
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
        // The count is built with a criteria query, so the whole criteria API has to be mocked
        KeycloakContext ctx = mock(KeycloakContext.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
        Root<UserEntity> root = mock(Root.class);
        TypedQuery<Long> tq = mock(TypedQuery.class);
        when(session.getContext()).thenReturn(ctx);
        when(ctx.getRealm()).thenReturn(realm);
        when(realm.getId()).thenReturn("realm-id");
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(UserEntity.class)).thenReturn(root);
        when(cq.select(any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(tq);
        when(tq.getSingleResult()).thenReturn(5L);
        int count = provider.getUsersCount(realm, false);
        assertEquals(5, count);
    }
}

