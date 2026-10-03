package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Path;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.Profile;
import org.keycloak.common.Profile.Feature;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.GroupModel.GroupMemberJoinEvent;
import org.keycloak.models.GroupModel.GroupMemberLeaveEvent;
import org.keycloak.models.jpa.entities.UserGroupMembershipEntity;
import org.keycloak.models.jpa.entities.UserRoleMappingEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.utils.RoleUtils;
import org.keycloak.organization.OrganizationProvider;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserAdapterAdvancedTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private EntityManager em;

    @Mock
    private UserEntity user;

    @Mock
    private GroupModel group;

    @Mock
    private GroupProvider groupProvider;

    @Mock
    private OrganizationProvider organizationProvider;

    @Mock
    private org.keycloak.organization.Organization org;

    @Mock
    private RoleModel role;

    @Mock
    private ClientModel client;

    private UserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAdapter = new UserAdapter(session, realm, em, user);
        when(session.groups()).thenReturn(groupProvider);
    }

    @Test
    void testGetGroupsCountByNameContaining() {
        // search == null branch
        assertEquals(10L, userAdapter.getGroupsCountByNameContaining(null));
        
        // search != null branch
        try (MockedStatic<RoleUtils> mockedRoleUtils = mockStatic(RoleUtils.class)) {
            TypedQuery<String> query = mock(TypedQuery.class);
            when(em.createQuery(any(CriteriaQuery.class))).thenReturn(query);
            when(query.getResultStream()).thenReturn(Stream.of("g1"));
            when(groupProvider.getGroupsCount(any(), any(), eq("search"))).thenReturn(1L);

            assertEquals(1L, userAdapter.getGroupsCountByNameContaining("search"));
        }
    }

    @Test
    void testGetGroupsCountWithOrganizationEnabled() {
        try (MockedStatic<Profile> mockedProfile = mockStatic(Profile.class)) {
            mockedProfile.when(() -> Profile.isFeatureEnabled(Feature.ORGANIZATION)).thenReturn(true);
            
            // Mocking createCountGroupsQuery
            TypedQuery<Long> countQuery = mock(TypedQuery.class);
            CriteriaBuilder cb = mock(CriteriaBuilder.class);
            CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
            Root<UserGroupMembershipEntity> root = mock(Root.class);
            
            when(em.getCriteriaBuilder()).thenReturn(cb);
            when(cb.createQuery(Long.class)).thenReturn(cq);
            when(cq.from(UserGroupMembershipEntity.class)).thenReturn(root);
            when(cb.count(root)).thenReturn(mock(jakarta.persistence.criteria.Expression.class));
            when(em.createQuery(cq)).thenReturn(countQuery);
            when(countQuery.getSingleResult()).thenReturn(10L);

            when(session.getProvider(OrganizationProvider.class)).thenReturn(organizationProvider);
            when(organizationProvider.getByMember(user)).thenReturn(org);
            when(org.count()).thenReturn(2L);

            assertEquals(8L, userAdapter.getGroupsCount());
        }
    }

    @Test
    void testGetGroupsStreamWithCache() {
        // First call: populate cache
        TypedQuery<String> query = mock(TypedQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<String> cq = mock(CriteriaQuery.class);
        Root<UserGroupMembershipEntity> root = mock(Root.class);
        
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(String.class)).thenReturn(cq);
        when(cq.from(UserGroupMembershipEntity.class)).thenReturn(root);
        when(root.get(anyString())).thenReturn(mock(Path.class));
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of("g1"));
        
        when(groupProvider.getGroupsStream(eq(realm), any(), any(), anyInt(), anyInt()))
                .thenReturn(Stream.of(group));

        userAdapter.getGroupsStream();
        // Second call: use cache
        userAdapter.getGroupsStream();

        verify(groupProvider, times(1)).getGroupsStream(eq(realm), any(), any(), anyInt(), anyInt());
    }

    @Test
    void testHasDirectGroupTrue() {
        when(group.getId()).thenReturn("g1");
        TypedQuery<UserGroupMembershipEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResultOrNull()).thenReturn(new UserGroupMembershipEntity());

        assertTrue(userAdapter.hasDirectGroup(group));
        verify(em).detach(any());
    }

    @Test
    void testJoinGroupImplBatchModeFalse() {
        try (MockedStatic<EntityManagers> mockedEM = mockStatic(EntityManagers.class);
             MockedStatic<GroupMemberJoinEvent> mockedEvent = mockStatic(GroupMemberJoinEvent.class)) {
            
            mockedEM.when(EntityManagers::isBatchMode).thenReturn(false);
            when(group.getId()).thenReturn("g1");
            
            // hasDirectGroup returns false
            TypedQuery<UserGroupMembershipEntity> membershipQuery = mock(TypedQuery.class);
            when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(membershipQuery);
            when(membershipQuery.setParameter(anyString(), any())).thenReturn(membershipQuery);
            when(membershipQuery.getSingleResultOrNull()).thenReturn(null);

            userAdapter.joinGroup(group);

            verify(em).persist(any(UserGroupMembershipEntity.class));
            verify(em).flush();
            verify(em).detach(any());
            mockedEvent.verify(() -> GroupMemberJoinEvent.fire(eq(group), eq(userAdapter), eq(session)));
        }
    }

    @Test
    void testLeaveGroupEmptyResults() {
        when(group.getId()).thenReturn("g1");
        TypedQuery<UserGroupMembershipEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setLockMode(any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        userAdapter.leaveGroup(group);

        verify(em, never()).remove(any());
    }

    @Test
    void testGrantRoleImplBatchModeFalse() {
        try (MockedStatic<EntityManagers> mockedEM = mockStatic(EntityManagers.class);
             MockedStatic<RoleGrantedEvent> mockedEvent = mockStatic(RoleGrantedEvent.class)) {
            
            mockedEM.when(EntityManagers::isBatchMode).thenReturn(false);
            when(role.getId()).thenReturn("r1");
            
            // hasDirectRole returns false
            TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
            when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.getSingleResultOrNull()).thenReturn(null);

            userAdapter.grantRole(role);

            verify(em).persist(any(UserRoleMappingEntity.class));
            verify(em).flush();
            verify(em).detach(any());
            mockedEvent.verify(() -> RoleGrantedEvent.fire(eq(role), eq(userAdapter), eq(session)));
        }
    }

    @Test
    void testDeleteRoleMappingEmpty() {
        when(role.getId()).thenReturn("r1");
        TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setLockMode(any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        userAdapter.deleteRoleMapping(role);

        verify(em, never()).remove(any());
    }
}
