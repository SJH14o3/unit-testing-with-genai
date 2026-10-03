package org.keycloak.models.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelToRepresentationGroupHierarchyTest {

    @Mock
    private GroupModel rootGroup;

    @Mock
    private GroupModel childGroup;

    @Mock
    private GroupModel grandChild;

    @Mock
    private RealmModel realm; // not used directly in hierarchy

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        // Setup basic group attributes
        when(rootGroup.getName()).thenReturn("root");
        when(rootGroup.getId()).thenReturn("g-root");
        when(rootGroup.getAttributes()).thenReturn(Collections.emptyMap());
        when(rootGroup.getSubGroupsStream()).thenReturn(Stream.of(childGroup));

        when(childGroup.getName()).thenReturn("child");
        when(childGroup.getId()).thenReturn("g-child");
        when(childGroup.getAttributes()).thenReturn(Collections.emptyMap());
        when(childGroup.getSubGroupsStream()).thenReturn(Stream.of(grandChild));

        when(grandChild.getName()).thenReturn("grand");
        when(grandChild.getId()).thenReturn("g-grand");
        when(grandChild.getAttributes()).thenReturn(Collections.emptyMap());
        when(grandChild.getSubGroupsStream()).thenReturn(Stream.empty());
    }

    @Test
    @DisplayName("Full hierarchy created with exact search")
    public void testHierarchyExactSearch() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toGroupHierarchy(rootGroup, true, "child", true);
        assertEquals("g-root", rep.getId());
        assertEquals(1, rep.getSubGroups().size());
        GroupRepresentation childRep = rep.getSubGroups().get(0);
        assertEquals("g-child", childRep.getId());
        assertEquals(0, childRep.getSubGroups().size());
    }

    @Test
    @DisplayName("Substring search returns both child and grand")
    public void testHierarchySubstringSearch() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toGroupHierarchy(rootGroup, true, "child", false);
        assertEquals(1, rep.getSubGroups().size());
        GroupRepresentation childRep = rep.getSubGroups().get(0);
        assertEquals(0, childRep.getSubGroups().size());
    }

    @Test
    @DisplayName("Null search returns subgroups if any exist")
    public void testHierarchyNullSearch() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toGroupHierarchy(rootGroup, true, null, false);
        assertEquals(1, rep.getSubGroups().size());
    }
}
