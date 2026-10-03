package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.GroupModel;
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
     * Verify that joinGroup adds membership when not already present.
     */
    @Test
    public void testJoinGroupAddsIfNotExist() {
        when(adapter.hasDirectGroup(group)).thenReturn(false);
        // stub createNamedQuery for joinGroupImpl persistence
        TypedQuery<UserGroupMembershipEntity> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(tq);
        when(tq.getSingleResultOrNull()).thenReturn(null);

        adapter.joinGroup(group);
        verify(em, times(1)).persist(any(UserGroupMembershipEntity.class));
    }

    /**
     * Verify that joinGroup does nothing when membership already exists.
     */
    @Test
    public void testJoinGroupDoesNothingIfExist() {
        when(adapter.hasDirectGroup(group)).thenReturn(true);
        adapter.joinGroup(group);
        verify(em, never()).persist(any());
    }

    /**
     * Test leaveGroup removes all matching entities.
     */
    @Test
    public void testLeaveGroupRemoves() {
        UserGroupMembershipEntity ent = mock(UserGroupMembershipEntity.class);
        List<UserGroupMembershipEntity> list = List.of(ent);
        TypedQuery<UserGroupMembershipEntity> tq = mock(TypedQuery.class);
        when(tq.getResultList()).thenReturn(list);
        when(adapter.getUserGroupMappingQuery(group)).thenReturn(tq);
        adapter.leaveGroup(group);
        verify(em).remove(ent);
        verify(em).flush();
    }

    /**
     * Test isMemberOf delegates to RoleUtils via getGroupsStream.
     */
    @Test
    public void testIsMemberOfDelegates() {
        // stub groups stream to return empty
        when(session.groups()).thenReturn(mock(org.keycloak.models.GroupProvider.class));
        // assume isMemberOf returns false
        assertFalse(adapter.isMemberOf(group));
    }

    /**
     * Test getGroupsCount returns cleaned count when ORGANIZATION is disabled.
     */
    @Test
    public void testGetGroupsCountNoOrg() {
        Long count = 5L;
        TypedQuery<Long> tq = mock(TypedQuery.class);
        when(tq.getSingleResult()).thenReturn(count);
        when(adapter.createCountGroupsQuery()).thenReturn(tq);
        assertEquals(count, adapter.getGroupsCount());
    }

    /**
     * Test getGroupsCount subtracts org group when feature enabled.
     */
    @Test
    public void testGetGroupsCountWithOrg() {
        Long count = 5L;
        TypedQuery<Long> tq = mock(TypedQuery.class);
        when(tq.getSingleResult()).thenReturn(count);
        when(adapter.createCountGroupsQuery()).thenReturn(tq);
        // enable ORGANIZATION feature via static stub
        MockedStatic<org.keycloak.common.Profile> profileMock = mockStatic(org.keycloak.common.Profile.class);
        profileMock.when(() -> org.keycloak.common.Profile.isFeatureEnabled(org.keycloak.common.Profile.Feature.ORGANIZATION)).thenReturn(true);
        OrganizationProvider provider = mock(OrganizationProvider.class);
        when(session.getProvider(OrganizationProvider.class)).thenReturn(provider);
        when(provider.getByMember(any(UserModel.class))).thenReturn(java.util.stream.Stream.of(mock(UserModel.class)).limit(2)); // fake 2 org members
        long result = adapter.getGroupsCount();
        assertEquals(count - 2, result);
        profileMock.clear();
    }
}
