package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.jpa.entities.UserRoleMappingEntity;

/**
 * Tests focusing on role mapping behaviour of {@link UserAdapter}.
 */
public class UserAdapterRolesTest extends UserAdapterTestBase {

    private RoleModel role;
    private RoleModel clientRole;
    private ClientModel client;

    @BeforeEach
    public void setup() {
        role = mock(RoleModel.class);
        clientRole = mock(RoleModel.class);
        client = mock(ClientModel.class);
        when(role.getId()).thenReturn("role1");
        when(clientRole.getId()).thenReturn("clientRole");
        when(client.getId()).thenReturn("client1");
        // RoleUtils distinguishes realm roles from client roles by their container
        when(role.getContainer()).thenReturn(realm);
        when(clientRole.getContainer()).thenReturn(client);
    }

    /**
     * hasDirectRole returns true when a mapping exists.
     */
    @Test
    public void testHasDirectRoleFound() {
        UserRoleMappingEntity emap = mock(UserRoleMappingEntity.class);
        TypedQuery<UserRoleMappingEntity> tq = mock(TypedQuery.class);
        when(user.getId()).thenReturn("uid");
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getSingleResultOrNull()).thenReturn(emap);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(tq);

        assertTrue(adapter.hasDirectRole(role));
        verify(tq).setParameter("user", user);
        verify(tq).setParameter("roleId", "role1");
        // the mapping is detached again so that it is not kept in the persistence context
        verify(em).detach(emap);
    }

    /**
     * hasDirectRole returns false when no mapping exists.
     */
    @Test
    public void testHasDirectRoleNotFound() {
        TypedQuery<UserRoleMappingEntity> tq = mock(TypedQuery.class);
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getSingleResultOrNull()).thenReturn(null);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(tq);

        assertFalse(adapter.hasDirectRole(role));
        verify(em, never()).detach(any());
    }

    /**
     * grantRole calls grantRoleImpl only when there is no direct role.
     */
    @Test
    public void testGrantRoleNoDirectAdds() {
        TypedQuery<UserRoleMappingEntity> tq = mock(TypedQuery.class);
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getSingleResultOrNull()).thenReturn(null);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(tq);

        adapter.grantRole(role);

        verify(em, times(1)).persist(any(UserRoleMappingEntity.class));
        verify(em).flush();
        verify(em).detach(any(UserRoleMappingEntity.class));
    }

    /**
     * grantRole does nothing when direct role already present.
     */
    @Test
    public void testGrantRoleAlreadyPresent() {
        UserRoleMappingEntity emap = mock(UserRoleMappingEntity.class);
        TypedQuery<UserRoleMappingEntity> tq = mock(TypedQuery.class);
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getSingleResultOrNull()).thenReturn(emap);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(tq);

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
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getResultList()).thenReturn(list);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(tq);

        adapter.deleteRoleMapping(role);

        verify(tq).setLockMode(LockModeType.PESSIMISTIC_WRITE);
        verify(em).remove(emap);
        verify(em).flush();
    }

    /**
     * getClientRoleMappingsStream filters client roles.
     */
    @Test
    public void testClientRoleMappingsFilter() {
        stubRoleMappings();

        List<RoleModel> result = adapter.getClientRoleMappingsStream(client).toList();

        assertEquals(List.of(clientRole), result);
    }

    /**
     * getRealmRoleMappingsStream filters realm roles.
     */
    @Test
    public void testRealmRoleMappingsFilter() {
        stubRoleMappings();

        List<RoleModel> result = adapter.getRealmRoleMappingsStream().toList();

        assertEquals(List.of(role), result);
    }

    /**
     * Role mapping ids are resolved through the realm, unresolved ids are dropped.
     */
    private void stubRoleMappings() {
        TypedQuery<String> tq = mock(TypedQuery.class);
        when(tq.setParameter(anyString(), any())).thenReturn(tq);
        when(tq.getResultStream()).thenReturn(Stream.of("role1", "clientRole", "deleted"));
        when(em.createNamedQuery("userRoleMappingIds", String.class)).thenReturn(tq);
        when(realm.getRoleById("role1")).thenReturn(role);
        when(realm.getRoleById("clientRole")).thenReturn(clientRole);
        when(realm.getRoleById("deleted")).thenReturn(null);
    }
}
