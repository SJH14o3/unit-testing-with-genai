package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel.GroupMemberJoinEvent;
import org.keycloak.models.RoleModel.RoleGrantedEvent;
import org.keycloak.models.RoleModel.RoleRevokedEvent;
import org.keycloak.models.jpa.entities.UserRoleMappingEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.utils.RoleUtils;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserAdapterRoleTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private EntityManager em;

    @Mock
    private UserEntity user;

    @Mock
    private RoleModel role;

    @Mock
    private ClientModel client;

    private UserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAdapter = new UserAdapter(session, realm, em, user);
    }

    @Test
    void testHasRoleDirect() {
        try (MockedStatic<RoleUtils> mockedRoleUtils = mockStatic(RoleUtils.class)) {
            // Mocking hasRole to check direct role first
            mockedRoleUtils.when(() -> RoleUtils.hasRole(any(), eq(role))).thenReturn(true);
            
            assertTrue(userAdapter.hasRole(role));
            mockedRoleUtils.verify(() -> RoleUtils.hasRole(any(), eq(role)));
        }
    }

    @Test
    void testHasRoleViaGroup() {
        try (MockedStatic<RoleUtils> mockedRoleUtils = mockStatic(RoleUtils.class)) {
            // Direct role returns false, group role returns true
            mockedRoleUtils.when(() -> RoleUtils.hasRole(any(), eq(role))).thenReturn(false);
            mockedRoleUtils.when(() -> RoleUtils.hasRoleFromGroup(any(), eq(role), eq(true))).thenReturn(true);

            assertTrue(userAdapter.hasRole(role));
        }
    }

    @Test
    void testHasDirectRoleTrue() {
        when(role.getId()).thenReturn("role-id");
        TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
        when(query.setParameter(eq("user"), eq(user))).thenReturn(query);
        when(query.setParameter(eq("roleId"), eq("role-id"))).thenReturn(query);
        when(query.getSingleResultOrNull()).thenReturn(new UserRoleMappingEntity());

        assertTrue(userAdapter.hasDirectRole(role));
        verify(em).detach(any());
    }

    @Test
    void testHasDirectRoleFalse() {
        when(role.getId()).thenReturn("role-id");
        TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
        when(query.setParameter(eq("user"), eq(user))).thenReturn(query);
        when(query.setParameter(eq("roleId"), eq("role-id"))).thenReturn(query);
        when(query.getSingleResultOrNull()).thenReturn(null);

        assertFalse(userAdapter.hasDirectRole(role));
    }

    @Test
    void testGrantRoleNew() {
        try (MockedStatic<EntityManagers> mockedEntityManagers = mockStatic(EntityManagers.class);
             MockedStatic<RoleGrantedEvent> mockedEvent = mockStatic(RoleGrantedEvent.class)) {
            
            mockedEntityManagers.when(EntityManagers::isBatchMode).thenReturn(false);
            when(role.getId()).thenReturn("role-id");
            
            // Mock hasDirectRole to false
            TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
            when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.getSingleResultOrNull()).thenReturn(null);

            userAdapter.grantRole(role);

            verify(em).persist(any(UserRoleMappingEntity.class));
            verify(em).flush();
            verify(em).detach(any());
            mockedEvent.verify(() -> RoleGrantedEvent.fire(eq(role), eq(userAdapter), eq(session)));
        }
    }

    @Test
    void testGrantRoleAlreadyExists() {
        when(role.getId()).thenReturn("role-id");
        TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResultOrNull()).thenReturn(new UserRoleMappingEntity());

        userAdapter.grantRole(role);

        verify(em, never()).persist(any(UserRoleMappingEntity.class));
    }

    @Test
    void testGetRoleMappingsStream() {
        List<String> roleIds = List.of("role-1", "role-2");
        TypedQuery<String> query = mock(TypedQuery.class);
        when(query.getResultStream()).thenReturn(roleIds.stream());
        when(em.createNamedQuery("userRoleMappingIds", String.class)).thenReturn(query);

        RoleModel role1 = mock(RoleModel.class);
        RoleModel role2 = mock(RoleModel.class);
        when(realm.getRoleById("role-1")).thenReturn(role1);
        when(realm.getRoleById("role-2")).thenReturn(role2);

        Stream<RoleModel> result = userAdapter.getRoleMappingsStream();

        assertEquals(2, result.count());
    }

    @Test
    void testGetRealmRoleMappingsStream() {
        try (MockedStatic<RoleUtils> mockedRoleUtils = mockStatic(RoleUtils.class)) {
            RoleModel realmRole = mock(RoleModel.class);
            RoleModel clientRole = mock(RoleModel.class);
            
            mockedRoleUtils.when(() -> RoleUtils.isRealmRole(realmRole)).thenReturn(true);
            mockedRoleUtils.when(() -> RoleUtils.isRealmRole(clientRole)).thenReturn(false);

            // We need to mock the underlying stream. Since getRoleMappingsStream is called,
            // we mock the return of createNamedQuery.
            TypedQuery<String> query = mock(TypedQuery.class);
            when(query.getResultStream()).thenReturn(Stream.of("r1", "r2"));
            when(em.createNamedQuery("userRoleMappingIds", String.class)).thenReturn(query);
            when(realm.getRoleById("r1")).thenReturn(realmRole);
            when(realm.getRoleById("r2")).thenReturn(clientRole);

            Stream<RoleModel> result = userAdapter.getRealmRoleMappingsStream().collect(java.util.stream.Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(realmRole, result.get(0));
        }
    }

    @Test
    void testGetClientRoleMappingsStream() {
        try (MockedStatic<RoleUtils> mockedRoleUtils = mockStatic(RoleUtils.class)) {
            RoleModel role1 = mock(RoleModel.class);
            RoleModel role2 = mock(RoleModel.class);
            
            mockedRoleUtils.when(() -> RoleUtils.isClientRole(role1, client)).thenReturn(true);
            mockedRoleUtils.when(() -> RoleUtils.isClientRole(role2, client)).thenReturn(false);

            TypedQuery<String> query = mock(TypedQuery.class);
            when(query.getResultStream()).thenReturn(Stream.of("r1", "r2"));
            when(em.createNamedQuery("userRoleMappingIds", String.class)).thenReturn(query);
            when(realm.getRoleById("r1")).thenReturn(role1);
            when(realm.getRoleById("r2")).thenReturn(role2);

            Stream<RoleModel> result = userAdapter.getClientRoleMappingsStream(client).collect(java.util.stream.Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(role1, result.get(0));
        }
    }

    @Test
    void testDeleteRoleMapping() {
        try (MockedStatic<RoleRevokedEvent> mockedEvent = mockStatic(RoleRevokedEvent.class)) {
            when(role.getId()).thenReturn("role-id");
            
            TypedQuery<UserRoleMappingEntity> query = mock(TypedQuery.class);
            UserRoleMappingEntity entity = new UserRoleMappingEntity();
            when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(query);
            when(query.setParameter(eq("user"), eq(user))).thenReturn(query);
            when(query.setParameter(eq("roleId"), eq("role-id"))).thenReturn(query);
            when(query.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(entity));

            userAdapter.deleteRoleMapping(role);

            verify(em).remove(entity);
            verify(em).flush();
            mockedEvent.verify(() -> RoleRevokedEvent.fire(eq(role), eq(userAdapter), eq(session)));
        }
    }

    @Test
    void testDeleteRoleMappingNull() {
        userAdapter.deleteRoleMapping(null);
        verify(em, never()).createNamedQuery(anyString(), any());
    }
}
