package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;

/**
 * Tests covering attribute manipulation methods of {@link UserAdapter}.
 * Each test focuses on a single decision point to achieve high coverage.
 */
public class UserAdapterAttributesTest extends UserAdapterTestBase {

    /**
     * Test setSingleAttribute when name is FIRST_NAME.
     */
    @Test
    public void testSetSingleAttributeFirstName() {
        adapter.setSingleAttribute(UserModel.FIRST_NAME, "John");
        verify(user).setFirstName("John");
    }

    /**
     * Test setSingleAttribute when name is LAST_NAME.
     */
    @Test
    public void testSetSingleAttributeLastName() {
        adapter.setSingleAttribute(UserModel.LAST_NAME, "Doe");
        verify(user).setLastName("Doe");
    }

    /**
     * Test setSingleAttribute when name is EMAIL.
     */
    @Test
    public void testSetSingleAttributeEmail() {
        adapter.setSingleAttribute(UserModel.EMAIL, "user@example.com");
        verify(user).setEmail("user@example.com", false);
    }

    /**
     * Test setSingleAttribute when name is USERNAME.
     */
    @Test
    public void testSetSingleAttributeUsername() {
        adapter.setSingleAttribute(UserModel.USERNAME, "alice");
        verify(user).setUsername("alice");
    }

    /**
     * Test setSingleAttribute when value is null – triggers attribute removal.
     */
    @Test
    public void testSetSingleAttributeNullRemoves() {
        UserAttributeEntity attr = mock(UserAttributeEntity.class);
        when(attr.getName()).thenReturn("customAttr");
        List<UserAttributeEntity> attributes = new ArrayList<>();
        attributes.add(attr);
        when(user.getAttributes()).thenReturn(attributes);

        Query deleteQuery = mock(Query.class);
        when(em.createNamedQuery("deleteUserAttributesByNameAndUser")).thenReturn(deleteQuery);

        adapter.setSingleAttribute("customAttr", null);

        verify(deleteQuery).setParameter("name", "customAttr");
        verify(deleteQuery).setParameter("userId", user.getId());
        verify(deleteQuery).executeUpdate();
        assertTrue(attributes.isEmpty());
        verify(em, never()).persist(any());
    }

    /**
     * Test setSingleAttribute updates existing attribute when multiple entries exist.
     */
    @Test
    public void testSetSingleAttributeUpdatesAllButOne() {
        UserAttributeEntity attr1 = mock(UserAttributeEntity.class);
        UserAttributeEntity attr2 = mock(UserAttributeEntity.class);
        when(attr1.getName()).thenReturn("custom");
        when(attr2.getName()).thenReturn("custom");
        when(attr1.getId()).thenReturn("id1");
        when(attr2.getId()).thenReturn("id2");
        when(attr1.getValue()).thenReturn("old");
        when(attr2.getValue()).thenReturn("old");
        // UserAdapter removes the surplus entries from the collection returned by the entity, so it has to be mutable
        when(user.getAttributes()).thenReturn(new ArrayList<>(List.of(attr1, attr2)));

        Query deleteQuery = mock(Query.class);
        when(em.createNamedQuery("deleteUserAttributesByNameAndUserOtherThan")).thenReturn(deleteQuery);
        when(deleteQuery.executeUpdate()).thenReturn(1);

        adapter.setSingleAttribute("custom", "newValue");

        verify(attr1).setValue("newValue");
        verify(deleteQuery).setParameter("name", "custom");
        verify(deleteQuery).setParameter("userId", user.getId());
        verify(deleteQuery).setParameter("attrId", "id1");
        verify(deleteQuery).executeUpdate();
        assertFalse(user.getAttributes().contains(attr2));
    }

    /**
     * Test setSingleAttribute when no existing attribute – uses persist.
     */
    @Test
    public void testSetSingleAttributePersistNew() {
        List<UserAttributeEntity> attributes = new ArrayList<>();
        when(user.getAttributes()).thenReturn(attributes);
        adapter.setSingleAttribute("custom", "value");
        verify(em).persist(any(UserAttributeEntity.class));
        assertEquals(1, attributes.size());
        assertEquals("value", attributes.get(0).getValue());
    }

    /**
     * Test setAttribute with empty list – results in removal.
     */
    @Test
    public void testSetAttributeEmptyListRemoves() {
        UserAttributeEntity attr = mock(UserAttributeEntity.class);
        when(attr.getName()).thenReturn("custom");
        when(attr.getValue()).thenReturn("v1");
        List<UserAttributeEntity> attributes = new ArrayList<>();
        attributes.add(attr);
        when(user.getAttributes()).thenReturn(attributes);

        Query deleteQuery = mock(Query.class);
        when(em.createNamedQuery("deleteUserAttributesByNameAndUser")).thenReturn(deleteQuery);

        List<String> empty = new ArrayList<>();
        adapter.setAttribute("custom", empty);

        verify(deleteQuery).setParameter("name", "custom");
        verify(deleteQuery).executeUpdate();
        assertTrue(attributes.isEmpty());
        verify(em, never()).persist(any(UserAttributeEntity.class));
    }

    /**
     * Test setAttribute when values match existing – no change.
     */
    @Test
    public void testSetAttributeNoChangeOnSameValues() {
        List<String> existing = List.of("a", "b");
        UserAttributeEntity e1 = mock(UserAttributeEntity.class);
        UserAttributeEntity e2 = mock(UserAttributeEntity.class);
        when(e1.getName()).thenReturn("custom");
        when(e2.getName()).thenReturn("custom");
        when(e1.getValue()).thenReturn("a");
        when(e2.getValue()).thenReturn("b");
        when(user.getAttributes()).thenReturn(new ArrayList<>(List.of(e1, e2)));

        adapter.setAttribute("custom", existing);

        verify(em, never()).persist(any());
    }

    /**
     * Test setAttribute when value list not null and contains non-null entries – persists each.
     */
    @Test
    public void testSetAttributePersistEach() {
        adapter.removeAttribute("cust"); // stub removal
        List<String> values = List.of("v1", "v2");
        adapter.setAttribute("cust", values);
        verify(em, times(2)).persist(any(UserAttributeEntity.class));
    }
}
