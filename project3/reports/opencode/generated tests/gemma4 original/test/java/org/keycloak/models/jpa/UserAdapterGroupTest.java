package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Path;
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
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.utils.RoleUtils;
import org.keycloak.organization.OrganizationProvider;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserAdapterGroupTest {

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

    private UserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAdapter = new UserAdapter(session, realm, em, user);
        when(session.groups()).thenReturn(groupProvider);
    }

    @Test
    void testGetGroupsStream() {
        List<String> groupIds = List.of("group-1", "group-2");
        TypedQuery<String> query = mock(TypedQuery.class);
        when(query.getResultList()).thenReturn(groupIds);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<String> cq = mock(CriteriaQuery.class);
        Root<UserGroupMembershipEntity> root = mock(Root.class);
        
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(String.class)).thenReturn(cq);
        when(cq.from(UserGroupMembershipEntity.class)).thenReturn(root);
        when(root.get(anyString())).thenReturn(mock(Path.class));
        when(em.createQuery(cq)).thenReturn(query);

        when(groupProvider.getGroupsStream(eq(realm), any(), any(), anyInt(), anyInt()))
                .thenReturn(Stream.of(group));

        Stream<GroupModel> result = userAdapter.getGroupsStream();

        assertNotNull(result);
        verify(groupProvider).getGroupsStream(eq(realm), any(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetGroupsCountWithOrganizationEnabled() {
        try (MockedStatic<Profile> mockedProfile = mockStatic(Profile.class)) {
            mockedProfile.when(() -> Profile.isFeatureEnabled(Feature.ORGANIZATION)).thenReturn(true);
            
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
    void testJoinGroupNew() {
        try (MockedStatic<EntityManagers> mockedEntityManagers = mockStatic(EntityManagers.class);
             MockedStatic<GroupMemberJoinEvent> mockedEvent = mockStatic(GroupMemberJoinEvent.class)) {
            
            mockedEntityManagers.when(EntityManagers::isBatchMode).thenReturn(false);
            when(group.getId()).thenReturn("group-id");
            
            TypedQuery<UserGroupMembershipEntity> membershipQuery = mock(TypedQuery.class);
            when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(membershipQuery);
            when(membershipQuery.setParameter(eq("user"), eq(user))).thenReturn(membershipQuery);
            when(membershipQuery.setParameter(eq("groupId"), eq("group-id"))).thenReturn(membershipQuery);
            when(membershipQuery.getSingleResultOrNull()).thenReturn(null);

            userAdapter.joinGroup(group);

            verify(em).persist(any(UserGroupMembershipEntity.class));
            verify(em).flush();
            verify(em).detach(any());
            mockedEvent.verify(() -> GroupMemberJoinEvent.fire(eq(group), eq(userAdapter), eq(session)));
        }
    }

    @Test
    void testLeaveGroup() {
        try (MockedStatic<GroupMemberLeaveEvent> mockedEvent = mockStatic(GroupMemberLeaveEvent.class)) {
            when(group.getId()).thenReturn("group-id");
            
            TypedQuery<UserGroupMembershipEntity> query = mock(TypedQuery.class);
            UserGroupMembershipEntity entity = new UserGroupMembershipEntity();
            when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(query);
            when(query.setParameter(eq("user"), eq(user))).thenReturn(query);
            when(query.setParameter(eq("groupId"), eq("group-id"))).thenReturn(query);
            when(query.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(entity));

            userAdapter.leaveGroup(group);

            verify(em).remove(entity);
            verify(em).flush();
            mockedEvent.verify(() -> GroupMemberLeaveEvent.fire(eq(group), eq(userAdapter), eq(session)));
        }
    }

    @Test
    void testIsMemberOf() {
        try (MockedStatic<RoleUtils> mockedRoleUtils = mockStatic(RoleUtils.class)) {
            when(userAdapter.getGroupsStream()).thenReturn(Stream.empty());
            mockedRoleUtils.when(() -> RoleUtils.isMember(any(), eq(group))).thenReturn(true);

            assertTrue(userAdapter.isMemberOf(group));
        }
    }
}
