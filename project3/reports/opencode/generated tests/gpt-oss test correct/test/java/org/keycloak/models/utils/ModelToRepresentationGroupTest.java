package org.keycloak.models.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelToRepresentationGroupTest {

    @Mock
    private GroupModel groupMock;

    @Mock
    private RealmModel realmMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(groupMock.getId()).thenReturn("group-1");
        when(groupMock.getName()).thenReturn("Engineering");
        when(groupMock.getDescription()).thenReturn("Engineering team");
        when(groupMock.getParentId()).thenReturn("root");
        when(groupMock.getSubGroupsStream()).thenReturn(Stream.of());
        when(groupMock.getAttributes()).thenReturn(Collections.emptyMap());
        // Role mapping stream will be empty by default
    }

    @Test
    @DisplayName("groupToBriefRepresentation returns minimal representation")
    public void testBrief() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.groupToBriefRepresentation(groupMock);
        assertEquals("group-1", rep.getId());
        assertEquals("Engineering", rep.getName());
        assertEquals("Engineering team", rep.getDescription());
        assertEquals(0, rep.getSubGroups() == null ? 0 : rep.getSubGroups().size());
    }

    @Nested
    @DisplayName("Full group representation")
    class FullGroup {
        @Test
        @DisplayName("toRepresentation adds role mappings and attributes when full=true")
        public void testFullWithRolesAndAttributes() {
            RoleModel role1 = mock(RoleModel.class);
            when(role1.getContainer()).thenReturn(realmMock);
            when(role1.getName()).thenReturn("admin");

            RoleModel role2 = mock(RoleModel.class);
            when(role2.getContainer()).thenReturn(realmMock);
            when(role2.getName()).thenReturn("user");

            when(groupMock.getRoleMappingsStream()).thenReturn(Stream.of(role1, role2));
            when(groupMock.getAttributes()).thenReturn(Collections.singletonMap("key", Collections.singletonList("value")));

            GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(groupMock, true);
            assertEquals("group-1", rep.getId());
            // role mappings are collected into an unordered set, so assert the content only
            assertEquals(2, rep.getRealmRoles().size());
            assertTrue(rep.getRealmRoles().containsAll(List.of("admin", "user")));
            assertEquals("value", rep.getAttributes().get("key").get(0));
        }

        @Test
        @DisplayName("toRepresentation handles nested subgroups with search filtering")
        public void testHierarchyWithSearch() {
            GroupModel child = mock(GroupModel.class);
            when(child.getName()).thenReturn("Backend");
            when(child.getId()).thenReturn("sub-1");
            when(child.getAttributes()).thenReturn(Collections.emptyMap());
            when(child.getSubGroupsStream()).thenReturn(Stream.of());

            when(groupMock.getSubGroupsStream()).thenReturn(Stream.of(child));

            // Search contains 'Back'
            GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toGroupHierarchy(groupMock, true, "Back", false);
            assertNotNull(rep.getSubGroups());
            assertEquals(1, rep.getSubGroups().size());
        }
    }
}
