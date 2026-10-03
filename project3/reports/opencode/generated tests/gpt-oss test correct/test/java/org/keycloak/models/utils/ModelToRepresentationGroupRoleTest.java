package org.keycloak.models.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.models.GroupModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelToRepresentationGroupRoleTest {

    @Mock
    private GroupModel groupMock;

    @Mock
    private RealmModel realmMock;

    @Mock
    private ClientModel clientMock;

    @Mock
    private RoleModel realmRole;

    @Mock
    private RoleModel clientRole;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(groupMock.getId()).thenReturn("gr-1");
        when(groupMock.getName()).thenReturn("Ops");
        when(groupMock.getDescription()).thenReturn("Operations group");
        when(groupMock.getParentId()).thenReturn("root");

        // realm role
        when(realmRole.getContainer()).thenReturn(realmMock);
        when(realmRole.getName()).thenReturn("role-realm");
        // client role
        when(clientRole.getContainer()).thenReturn(clientMock);
        when(clientRole.getName()).thenReturn("role-client");
        when(clientMock.getClientId()).thenReturn("client-1");
        // role mappings
        when(groupMock.getRoleMappingsStream()).thenReturn(Stream.of(realmRole, clientRole));
    }

    @Test
    @DisplayName("Realm role added to realmRoles list")
    public void testRealmRoleMapping() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(groupMock, true);
        assertNotNull(rep.getRealmRoles());
        assertTrue(rep.getRealmRoles().contains("role-realm"));
    }

    @Test
    @DisplayName("Client role added under correct client id")
    public void testClientRoleMapping() {
        GroupRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(groupMock, true);
        assertNotNull(rep.getClientRoles());
        assertTrue(rep.getClientRoles().containsKey("client-1"));
        assertTrue(rep.getClientRoles().get("client-1").contains("role-client"));
    }
}
