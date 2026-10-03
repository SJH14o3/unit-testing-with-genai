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
import org.keycloak.models.RoleModel;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelToRepresentationGroupFullTest {

    @Mock
    private GroupModel groupMock;

    @Mock
    private RealmModel realmMock;

    @Mock
    private RoleModel realmRole;

    @Mock
    private RoleModel clientRole;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        when(groupMock.getId()).thenReturn("grp-1");
        when(groupMock.getName()).thenReturn("Engineering");
        when(groupMock.getDescription()).thenReturn("Eng team");
        when(groupMock.getParentId()).thenReturn("root");
        when(groupMock.getAttributes()).thenReturn(Collections.singletonMap("k1", Collections.singletonList("v1")));
    }

    @Test
    @DisplayName("toRepresentation full=false returns brief representation")
    public void testBriefWhenNotFull() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(groupMock, false);
        assertEquals("grp-1", rep.getId());
        assertEquals("Engineering", rep.getName());
        assertEquals("Eng team", rep.getDescription());
        assertEquals("root", rep.getParentId());
        assertNull(rep.getRealmRoles(), "Should be null when not full");
        assertNull(rep.getClientRoles(), "Should be null when not full");
        assertNull(rep.getAttributes(), "Should be null when not full");
    }

    @Test
    @DisplayName("toRepresentation full=true populates realm and client roles and attributes")
    public void testFullRepresentationRolesAttributes() {
        // Setup roles
        when(realmRole.getContainer()).thenReturn(realmMock);
        when(realmRole.getName()).thenReturn("realm-admin");
        when(clientRole.getContainer()).thenReturn(mock(org.keycloak.models.ClientModel.class));
        when(clientRole.getContainer()).thenReturn(mock(org.keycloak.models.ClientModel.class));
        when(clientRole.getName()).thenReturn("client-reader");
        // client id
        when(((org.keycloak.models.ClientModel) clientRole.getContainer()).getClientId()).thenReturn("cl-1");
        // Role mappings
        when(groupMock.getRoleMappingsStream()).thenReturn(Stream.of(realmRole, clientRole));

        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(groupMock, true);

        assertNotNull(rep.getRealmRoles());
        assertTrue(rep.getRealmRoles().contains("realm-admin"));
        assertNotNull(rep.getClientRoles());
        assertTrue(rep.getClientRoles().containsKey("cl-1"));
        assertTrue(rep.getClientRoles().get("cl-1").contains("client-reader"));
        assertNotNull(rep.getAttributes());
        assertEquals("v1", rep.getAttributes().get("k1").get(0));
    }
}
