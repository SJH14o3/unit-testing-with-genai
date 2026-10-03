package org.keycloak.models.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class CreateRoleTest {

    private RealmModel realmMock;
    private RoleModel roleMock;

    @BeforeEach
    public void setUp() {
        realmMock = mock(RealmModel.class);
        roleMock = mock(RoleModel.class);
        when(realmMock.addRole(anyString())).thenReturn(roleMock);
        when(realmMock.addRole(anyString(), anyString())).thenReturn(roleMock);
        when(realmMock.getDefaultRole()).thenReturn(roleMock);
    }

    @Test
    public void createRole_regular() {
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("role1");
        when(roleMock.getName()).thenReturn("role1");
        RoleModel role = RepresentationToModel.createRole(realmMock, rep);
        assertNotNull(role);
        assertEquals(rep.getName(), role.getName());
        verify(realmMock).addRole("role1");
    }

    @Test
    public void createRole_withAttributes() {
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("role2");
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("attr1", Arrays.asList("val1", "val2"));
        rep.setAttributes(attrs);
        RepresentationToModel.createRole(realmMock, rep);
        verify(roleMock).setAttribute("attr1", Arrays.asList("val1", "val2"));
    }
}
