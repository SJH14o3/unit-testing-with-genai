package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelToRepresentationGroupAndRoleTest {

    @Mock
    private GroupModel group;

    @Mock
    private RoleModel role;

    @Mock
    private ClientModel client;

    @Mock
    private RealmModel realm;

    @Test
    void testGroupToBriefRepresentation() {
        when(group.getId()).thenReturn("group-id");
        when(group.getName()).thenReturn("group-name");
        when(group.getDescription()).thenReturn("group-description");
        when(group.getPath()).thenReturn("/group");
        when(group.getParentId()).thenReturn(null);

        GroupRepresentation rep = ModelToRepresentation.groupToBriefRepresentation(group);

        assertNotNull(rep);
        assertEquals("group-id", rep.getId());
        assertEquals("group-name", rep.getName());
        assertEquals("group-description", rep.getDescription());
        assertEquals("/group", rep.getPath());
        assertNull(rep.getParentId());
    }

    @Test
    void testToRepresentationFullWithRealmRoles() {
        when(group.getId()).thenReturn("group-id");
        when(group.getName()).thenReturn("group-name");
        when(group.getDescription()).thenReturn("group-description");
        when(group.getPath()).thenReturn("/group");
        when(group.getParentId()).thenReturn(null);
        when(group.getRoleMappingsStream()).thenReturn(Stream.of(role));
        when(group.getAttributes()).thenReturn(new HashMap<>());
        when(role.getContainer()).thenReturn(realm);
        when(role.getName()).thenReturn("realm-role");

        GroupRepresentation rep = ModelToRepresentation.toRepresentation(group, true);

        assertNotNull(rep);
        assertEquals("group-id", rep.getId());
        assertEquals("group-name", rep.getName());
        assertEquals("group-description", rep.getDescription());
        assertEquals("/group", rep.getPath());
        assertNull(rep.getParentId());
        assertTrue(rep.getRealmRoles().contains("realm-role"));
        assertTrue(rep.getClientRoles().isEmpty());
        assertEquals(0, rep.getAttributes().size());
    }

    @Test
    void testToRepresentationFullWithClientRoles() {
        when(group.getId()).thenReturn("group-id");
        when(group.getName()).thenReturn("group-name");
        when(group.getDescription()).thenReturn("group-description");
        when(group.getPath()).thenReturn("/group");
        when(group.getParentId()).thenReturn(null);
        when(group.getRoleMappingsStream()).thenReturn(Stream.of(role));
        when(group.getAttributes()).thenReturn(new HashMap<>());
        when(role.getContainer()).thenReturn(client);
        when(role.getName()).thenReturn("client-role");
        when(client.getClientId()).thenReturn("client-id");

        GroupRepresentation rep = ModelToRepresentation.toRepresentation(group, true);

        assertNotNull(rep);
        assertTrue(rep.getRealmRoles().isEmpty());
        Map<String, List<String>> clientRoles = rep.getClientRoles();
        assertNotNull(clientRoles);
        assertEquals(1, clientRoles.size());
        assertTrue(clientRoles.get("client-id").contains("client-role"));
    }

    @Test
    void testToGroupHierarchyWithSearchMatch() {
        when(group.getId()).thenReturn("group-id");
        when(group.getName()).thenReturn("group-name");
        when(group.getDescription()).thenReturn("group-description");
        when(group.getPath()).thenReturn("/group");
        when(group.getParentId()).thenReturn(null);
        when(group.getSubGroupsStream()).thenReturn(Stream.empty());

        GroupRepresentation rep = ModelToRepresentation.toGroupHierarchy(group, true, "group", true);

        assertNotNull(rep);
        assertEquals("group-id", rep.getId());
        assertTrue(rep.getSubGroups().isEmpty());
    }

    @Test
    void testToGroupHierarchyWithSearchMismatchNoSubGroups() {
        when(group.getId()).thenReturn("group-id");
        when(group.getName()).thenReturn("group-name");
        when(group.getDescription()).thenReturn("group-description");
        when(group.getPath()).thenReturn("/group");
        when(group.getParentId()).thenReturn(null);
        when(group.getSubGroupsStream()).thenReturn(Stream.empty());

        GroupRepresentation rep = ModelToRepresentation.toGroupHierarchy(group, true, "non-existent", false);

        assertNotNull(rep);
        assertTrue(rep.getSubGroups().isEmpty());
    }

    @Test
    void testToRepresentationRole() {
        when(role.getId()).thenReturn("role-id");
        when(role.getName()).thenReturn("role-name");
        when(role.getDescription()).thenReturn("role-description");
        when(role.isComposite()).thenReturn(true);
        when(role.isClientRole()).thenReturn(false);
        when(role.getContainerId()).thenReturn("container-id");
        when(role.getAttributes()).thenReturn(new HashMap<>());

        RoleRepresentation rep = ModelToRepresentation.toRepresentation(role);

        assertNotNull(rep);
        assertEquals("role-id", rep.getId());
        assertEquals("role-name", rep.getName());
        assertEquals("role-description", rep.getDescription());
        assertTrue(rep.isComposite());
        assertFalse(rep.isClientRole());
        assertEquals("container-id", rep.getContainerId());
        assertEquals(0, rep.getAttributes().size());
    }

    @Test
    void testToBriefRepresentationRole() {
        when(role.getId()).thenReturn("role-id");
        when(role.getName()).thenReturn("role-name");
        when(role.getDescription()).thenReturn("role-description");
        when(role.isComposite()).thenReturn(true);
        when(role.isClientRole()).thenReturn(false);
        when(role.getContainerId()).thenReturn("container-id");

        RoleRepresentation rep = ModelToRepresentation.toBriefRepresentation(role);

        assertNotNull(rep);
        assertEquals("role-id", rep.getId());
        assertEquals("role-name", rep.getName());
        assertEquals("role-description", rep.getDescription());
        assertTrue(rep.isComposite());
        assertFalse(rep.isClientRole());
        assertEquals("container-id", rep.getContainerId());
    }
}
