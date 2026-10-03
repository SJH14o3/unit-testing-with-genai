package org.keycloak.models.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class CreateGroupTest {
    @Test
    public void importGroup_withRolesAttributes() {
        RealmModel realm = mock(RealmModel.class);
        GroupModel parent = mock(GroupModel.class);
        GroupRepresentation groupRep = new GroupRepresentation();
        groupRep.setName("parentGroup");
        groupRep.setId("grp1");
        groupRep.setDescription("desc");
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("attr1", List.of("value1"));
        groupRep.setAttributes(attrs);
        RoleModel realmRole = mock(RoleModel.class);
        when(realm.getRole("realmRoleName")).thenReturn(realmRole);
        groupRep.setRealmRoles(List.of("realmRoleName"));
        GroupModel newGroup = mock(GroupModel.class);
        when(realm.createGroup(eq("grp1"), eq("parentGroup"), eq(parent))).thenReturn(newGroup);
        when(newGroup.getName()).thenReturn("parentGroup");
        RepresentationToModel.importGroup(realm, parent, groupRep);
        verify(newGroup).setDescription("desc");
        verify(newGroup).setAttribute("attr1", attrs.get("attr1"));
        verify(newGroup).grantRole(realmRole);
    }

    @Test
    public void importGroup_withSubGroups() {
        RealmModel realm = mock(RealmModel.class);
        GroupModel parent = mock(GroupModel.class);
        GroupRepresentation parentRep = new GroupRepresentation();
        parentRep.setName("root");
        parentRep.setId("rootId");
        GroupRepresentation subRep = new GroupRepresentation();
        subRep.setName("child");
        subRep.setId("childId");
        parentRep.setSubGroups(List.of(subRep));
        GroupModel rootGroup = mock(GroupModel.class);
        when(realm.createGroup(eq("rootId"), eq("root"), eq(parent))).thenReturn(rootGroup);
        GroupModel childGroup = mock(GroupModel.class);
        when(realm.createGroup(eq("childId"), eq("child"), eq(rootGroup))).thenReturn(childGroup);
        RepresentationToModel.importGroup(realm, parent, parentRep);
        verify(realm).createGroup("childId", "child", rootGroup);
    }
}
