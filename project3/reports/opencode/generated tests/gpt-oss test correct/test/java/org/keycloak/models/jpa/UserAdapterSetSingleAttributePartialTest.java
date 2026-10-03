package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests for partial branches of {@link UserAdapter#setSingleAttribute(String, String)}.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserAdapterSetSingleAttributePartialTest extends UserAdapterTestBase {

    /**
     * Branch where the new value equals the old value (no change).
     * The method should return without performing any persistence or delete queries.
     */
    @Test
    public void testSetSingleAttribute_collectionEqualsBranch() {
        UserAttributeEntity attr1 = mock(UserAttributeEntity.class);
        when(attr1.getName()).thenReturn("custom");
        when(attr1.getValue()).thenReturn("old");
        List<UserAttributeEntity> attrs = List.of(attr1);
        when(user.getAttributes()).thenReturn(attrs);
        // oldEntries gets sorted list of "old"; newEntries lists "old"
        adapter.setSingleAttribute("custom", "old");
        // No interaction with entityManager
        verify(em, never()).createNamedQuery(anyString());
        verify(attr1, never()).setValue(anyString());
    }

    /**
     * Branch where the new value differs, leading to deletion of existing attributes except one.
     * The method should update the first existing attribute, delete the rest via HQL, and remove them from the local collection.
     */
    @Test
    public void testSetSingleAttribute_updateExistingAttributes() {
        // Existing two attributes with same name
        UserAttributeEntity attr1 = mock(UserAttributeEntity.class);
        UserAttributeEntity attr2 = mock(UserAttributeEntity.class);
        when(attr1.getName()).thenReturn("custom");
        when(attr2.getName()).thenReturn("custom");
        when(attr1.getValue()).thenReturn("old1");
        when(attr2.getValue()).thenReturn("old2");
        when(attr1.getId()).thenReturn("id1");
        when(attr2.getId()).thenReturn("id2");
        List<UserAttributeEntity> mutableList = new ArrayList<>(List.of(attr1, attr2));
        when(user.getAttributes()).thenReturn(mutableList);

        // Mock delete query
        Query query = mock(Query.class);
        when(em.createNamedQuery("deleteUserAttributesByNameAndUserOtherThan")).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        adapter.setSingleAttribute("custom", "newValue");

        // Verify first attribute updated
        verify(attr1).setValue("newValue");
        // Verify delete query executed with correct parameters
        verify(query).setParameter("name", "custom");
        verify(query).setParameter("userId", user.getId());
        verify(query).setParameter("attrId", "id1");
        verify(query).executeUpdate();
        // Verify attr2 removed from local collection
        assertFalse(mutableList.contains(attr2));
    }
}
