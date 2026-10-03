package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.RoleModel;
import org.keycloak.models.jpa.entities.UserRoleMappingEntity;

/**
 * Tests focusing on role mapping behaviour of {@link UserAdapter}.
 */
public class UserAdapterRolesTest extends UserAdapterTestBase {

    private RoleModel role;
    private RoleModel clientRole;

    @BeforeEach
    public void setup() {
        role = mock(RoleModel.class);
        clientRole = mock(RoleModel.class);
        when(role.getId()).thenReturn("role1");
        when(clientRole.getId()).thenReturn("clientRole");
    }

    /**
     * hasDirectRole returns true when a mapping exists.
     */
    @Test
    public void testHasDirectRoleFound() {
        UserRoleMappingEntity emap = mock(UserRoleMappingEntity.class);
        TypedQuery<UserRoleMappingEntity> tq = mock(TypedQuery.class);
        when(user.getId()).thenReturn("uid");
        when(tq.getSingleResultOrNull()).thenReturn(emap);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(tq);
        when(tq.getSingleResultOrNull()).thenReturn(emap);
        // stub detach -> no effect
        assertTrue(adapter.hasDirectRole(role));
    }

    /**
     * grantRole calls grantRoleImpl only when there is no direct role.
     */
    @Test
    public void testGrantRoleNoDirectAdds() {
        when(adapter.hasDirectRole(role)).thenReturn(false);
        adapter.grantRole(role);
        verify(em, times(1)).persist(any(UserRoleMappingEntity.class));
    }

    /**
     * grantRole does nothing when direct role already present.
     */
    @Test
    public void testGrantRoleAlreadyPresent() {
        when(adapter.hasDirectRole(role)).thenReturn(true);
        adapter.grantRole(role);
        verify(em, never()).persist(any());
    }

    /**
     * deleteRoleMapping removes all mapping entities.
     */
    @Test
    public void testDeleteRoleMapping() {
        List<UserRoleMappingEntity> list = new ArrayList<>();
        UserRoleMappingEntity emap = mock(UserRoleMappingEntity.class);
        list.add(emap);
        TypedQuery<UserRoleMappingEntity> tq = mock(TypedQuery.class);
        when(tq.getResultList()).thenReturn(list);
        when(adapter.getUserRoleMappingEntityTypedQuery(role)).thenReturn(tq);
        adapter.deleteRoleMapping(role);
        verify(em).remove(emap);
        verify(em).flush();
    }

    /**
     * getClientRoleMappingsStream filters client roles.
     */
    @Test
    public void testClientRoleMappingsFilter() {
        UserRoleMappingEntity cMap = mock(UserRoleMappingEntity.class);
        when(clientRole.getId()).thenReturn("clientRole");
        List<RoleModel> all = List.of(role, clientRole);
        when(adapter.getRoleMappingsStream()).thenReturn(all.stream());
        List<RoleModel> result = adapter.getClientRoleMappingsStream(clientRole).toList();
        assertTrue(result.contains(clientRole));
        assertFalse(result.contains(role));
    }

    /**
     * getRealmRoleMappingsStream filters realm roles.
     */
    @Test
    public void testRealmRoleMappingsFilter() {
        List<RoleModel> all = List.of(role, clientRole);
        when(adapter.getRoleMappingsStream()).thenReturn(all.stream());
        List<RoleModel> result = adapter.getRealmRoleMappingsStream().toList();
        // role assumed realm for this test
        assertTrue(result.contains(role));
    }
}
