package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.keycloak.models.jpa.entities.UserRequiredActionEntity;
import org.keycloak.models.jpa.entities.UserEntity;

/**
 * Tests for required‑action handling in {@link UserAdapter}.
 */
public class UserAdapterRequiredActionsTest extends UserAdapterTestBase {

    /**
     * Verify that adding a new action persists an entity and does not add a duplicate.
     */
    @Test
    public void testAddRequiredActionNew() {
        UserRequiredActionEntity e = mock(UserRequiredActionEntity.class);
        when(user.getRequiredActions()).thenReturn(new ArrayList<>());
        adapter.addRequiredAction("ACTION_ONE");
        verify(em).persist(any(UserRequiredActionEntity.class));
        // ensure the user now contains the action
        assertEquals(1, user.getRequiredActions().size());
    }

    /**
     * Verify that adding an action that already exists performs no persist.
     */
    @Test
    public void testAddRequiredActionDuplicate() {
        UserRequiredActionEntity e = mock(UserRequiredActionEntity.class);
        when(e.getAction()).thenReturn("ACTION_ONE");
        List<UserRequiredActionEntity> list = new ArrayList<>();
        list.add(e);
        when(user.getRequiredActions()).thenReturn(list);
        adapter.addRequiredAction("ACTION_ONE");
        verify(em, never()).persist(any());
    }

    /**
     * Test removing a required action removes the entity.
     */
    @Test
    public void testRemoveRequiredAction() {
        UserRequiredActionEntity e = mock(UserRequiredActionEntity.class);
        when(e.getAction()).thenReturn("REMOVABLE");
        List<UserRequiredActionEntity> list = new ArrayList<>();
        list.add(e);
        when(user.getRequiredActions()).thenReturn(list);
        adapter.removeRequiredAction("REMOVABLE");
        verify(em).remove(e);
        assertTrue(list.isEmpty());
    }

    /**
     * Test getRequiredActionsStream returns distinct action names.
     */
    @Test
    public void testGetRequiredActionsStreamDistinct() {
        UserRequiredActionEntity e1 = mock(UserRequiredActionEntity.class);
        UserRequiredActionEntity e2 = mock(UserRequiredActionEntity.class);
        when(e1.getAction()).thenReturn("ACTION_ONE");
        when(e2.getAction()).thenReturn("ACTION_ONE");
        when(user.getRequiredActions()).thenReturn(List.of(e1, e2));
        List<String> actions = new ArrayList<>();
        adapter.getRequiredActionsStream().forEach(actions::add);
        assertEquals(1, actions.size());
        assertEquals("ACTION_ONE", actions.get(0));
    }
}
