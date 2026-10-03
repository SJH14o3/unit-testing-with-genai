package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.GroupModel;
import org.keycloak.models.GroupProvider;
import org.keycloak.models.jpa.entities.UserGroupMembershipEntity;

/**
 * Tests covering group membership logic in {@link UserAdapter}.
 */
public class UserAdapterGroupsTest extends UserAdapterTestBase {

    private GroupModel group;

    @BeforeEach
    public void setUpGroups() {
        group = mock(GroupModel.class);
        when(group.getId()).thenReturn("group1");
    }

    /**
     * Test leaveGroup removes all matching entities.
     */
    @Test
    public void testLeaveGroupRemoves() {
        UserGroupMembershipEntity ent = mock(UserGroupMembershipEntity.class);
        TypedQuery<UserGroupMembershipEntity> tq = mock(TypedQuery.class);
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of(ent));
        when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(tq);

        adapter.leaveGroup(group);

        verify(tq).setParameter("user", user);
        verify(tq).setParameter("groupId", "group1");
        verify(tq).setLockMode(LockModeType.PESSIMISTIC_WRITE);
        verify(em).remove(ent);
        verify(em).flush();
    }

    /**
     * Test leaveGroup does nothing when the user is not a member.
     */
    @Test
    public void testLeaveGroupNoMembership() {
        TypedQuery<UserGroupMembershipEntity> tq = mock(TypedQuery.class);
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getResultList()).thenReturn(List.of());
        when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(tq);

        adapter.leaveGroup(group);

        verify(em, never()).remove(any());
        verify(em, never()).flush();
    }

    /**
     * Test isMemberOf delegates to RoleUtils via getGroupsStream.
     */
    @Test
    public void testIsMemberOfDelegates() {
        GroupProvider groups = mock(GroupProvider.class);
        when(session.groups()).thenReturn(groups);
        stubGroupIdsQuery(List.of("group1"));
        when(groups.getGroupsStream(eq(realm), any(), any(), any(), any())).thenAnswer(invocation -> Stream.of(group));

        assertTrue(adapter.isMemberOf(group));
        assertFalse(adapter.isMemberOf(mock(GroupModel.class)));
    }

    /**
     * Test getGroupsCount is read from the database and untouched while the organization feature is disabled.
     */
    @Test
    public void testGetGroupsCountWithoutOrganization() {
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> queryBuilder = mock(CriteriaQuery.class);
        Root<UserGroupMembershipEntity> root = mock(Root.class);
        Path<Object> path = mock(Path.class);
        Expression<Long> count = mock(Expression.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<Long> tq = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(builder);
        when(builder.createQuery(Long.class)).thenReturn(queryBuilder);
        when(queryBuilder.from(UserGroupMembershipEntity.class)).thenReturn(root);
        when(root.get(anyString())).thenReturn(path);
        when(builder.equal(path, user)).thenReturn(predicate);
        when(builder.count(root)).thenReturn(count);
        when(em.createQuery(queryBuilder)).thenReturn(tq);
        when(tq.getSingleResult()).thenReturn(3L);

        assertEquals(3L, adapter.getGroupsCount());
        verify(session, never()).getProvider(any());
    }

    /**
     * Wires the criteria query used by {@link UserAdapter#getGroupsStream(String, Integer, Integer)} so that
     * the group ids are resolved from the given list.
     */
    private void stubGroupIdsQuery(List<String> ids) {
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        CriteriaQuery<String> queryBuilder = mock(CriteriaQuery.class);
        Root<UserGroupMembershipEntity> root = mock(Root.class);
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<String> tq = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(builder);
        when(builder.createQuery(String.class)).thenReturn(queryBuilder);
        when(queryBuilder.from(UserGroupMembershipEntity.class)).thenReturn(root);
        when(root.get(anyString())).thenReturn(path);
        when(builder.equal(path, user)).thenReturn(predicate);
        when(em.createQuery(queryBuilder)).thenReturn(tq);
        when(tq.getResultList()).thenReturn(ids);
    }
}
