package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.FederatedIdentityEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserProviderStage1Test {

    @Mock
    private KeycloakSession session;

    @Mock
    private EntityManager em;

    @Mock
    private RealmModel realm;

    @Mock
    private UserModel userModel;

    @Mock
    private UserEntity userEntity;

    @Mock
    private TypedQuery<UserEntity> userTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Long> cq;

    @Mock
    private Root<UserEntity> root;

    @Mock
    private Path<Object> path;

    @Mock
    private Join<Object, Object> join;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JpaUserProvider(session, em);
    }

    @Test
    void testAddUser_withId() {
        String id = "test-id";
        String username = "testuser";
        when(realm.getId()).thenReturn("test-realm");
        when(realm.getDefaultRole()).thenReturn(mock(RoleModel.class));
        when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());

        UserModel addedUser = provider.addUser(realm, id, username, false, false);

        assertNotNull(addedUser);
        verify(em).persist(any(UserEntity.class));
        verify(em, atLeastOnce()).flush();
    }

    @Test
    void testRemoveUser_userExists() {
        String userId = "user-123";
        when(userModel.getId()).thenReturn(userId);
        when(em.find(UserEntity.class, userId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(userEntity);
        
        // Mock named queries for deletion
        TypedQuery<?> mockQuery = mock(TypedQuery.class);
        when(em.createNamedQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);

        boolean removed = provider.removeUser(realm, userModel);

        assertTrue(removed);
        verify(em).remove(userEntity);
        verify(em, atLeastOnce()).flush();
    }

    @Test
    void testRemoveUser_userDoesNotExist() {
        String userId = "user-123";
        when(userModel.getId()).thenReturn(userId);
        when(em.find(UserEntity.class, userId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        boolean removed = provider.removeUser(realm, userModel);

        assertFalse(removed);
    }

    @Test
    void testGetUserById_exists() {
        String userId = "user-123";
        when(em.find(UserEntity.class, userId)).thenReturn(userEntity);
        when(userEntity.getRealmId()).thenReturn("test-realm");
        when(realm.getId()).thenReturn("test-realm");

        UserModel result = provider.getUserById(realm, userId);

        assertNotNull(result);
    }

    @Test
    void testGetUserById_notExists() {
        String userId = "user-123";
        when(em.find(UserEntity.class, userId)).thenReturn(null);

        UserModel result = provider.getUserById(realm, userId);

        assertNull(result);
    }

    @Test
    void testGetUserByUsername_exists() {
        String username = "testuser";
        when(realm.getId()).thenReturn("test-realm");
        when(em.createNamedQuery(eq("getRealmUserByUsername"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("test-realm");

        UserModel result = provider.getUserByUsername(realm, username);

        assertNotNull(result);
    }

    @Test
    void testGetUserByUsername_notExists() {
        String username = "testuser";
        when(realm.getId()).thenReturn("test-realm");
        when(em.createNamedQuery(eq("getRealmUserByUsername"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        UserModel result = provider.getUserByUsername(realm, username);

        assertNull(result);
    }

    @Test
    void testGetUserByEmail_exists() {
        String email = "test@example.com";
        when(realm.getId()).thenReturn("test-realm");
        when(em.createNamedQuery(eq("getRealmUserByEmail"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("test-realm");

        UserModel result = provider.getUserByEmail(realm, email);

        assertNotNull(result);
    }

    @Test
    void testGetUserByFederatedIdentity_exists() {
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        when(realm.getId()).thenReturn("test-realm");
        when(em.getReference(UserEntity.class, anyString())).thenReturn(userEntity);
        when(em.createNamedQuery(eq("findUserByFederatedIdentityAndRealm"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("test-realm");

        UserModel result = provider.getUserByFederatedIdentity(realm, userModel, identity);

        assertNotNull(result);
    }

    @Test
    void testGetUserByFederatedIdentity_multipleResults() {
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        when(realm.getId()).thenReturn("test-realm");
        when(em.getReference(UserEntity.class, anyString())).thenReturn(userEntity);
        when(em.createNamedQuery(eq("findUserByFederatedIdentityAndRealm"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(userEntity, userEntity));

        assertThrows(IllegalStateException.class, () -> provider.getUserByFederatedIdentity(realm, userModel, identity));
    }

    @Test
    void testGetFederatedIdentity_exists() {
        String idp = "idp";
        String userId = "user-id";
        FederatedIdentityEntity identityEntity = new FederatedIdentityEntity();
        identityEntity.setIdentityProvider(idp);
        identityEntity.setUserId(userId);
        identityEntity.setUserName("user");
        identityEntity.setToken("token");

        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(mock(TypedQuery.class));
        when(em.getReference(UserEntity.class, userId)).thenReturn(userEntity);
        when(em.createNamedQuery(anyString(), eq(FederatedIdentityEntity.class)).setParameter(anyString(), any()).setParameter(anyString(), any()).setLockMode(any()).getResultList())
                .thenReturn(List.of(identityEntity));

        FederatedIdentityModel result = provider.getFederatedIdentity(realm, userModel, idp);

        assertNotNull(result);
        assertEquals(idp, result.getIdentityProvider());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testGetFederatedIdentitiesStream_exists() {
        String userId = "user-123";
        FederatedIdentityEntity identityEntity = new FederatedIdentityEntity();
        identityEntity.setIdentityProvider("idp");
        identityEntity.setUserId("id");
        identityEntity.setUserName("user");
        identityEntity.setToken("token");

        TypedQuery<FederatedIdentityEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery(eq("findFederatedIdentityByUser"), eq(FederatedIdentityEntity.class))).thenReturn(query);
        when(em.getReference(UserEntity.class, userId)).thenReturn(userEntity);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(identityEntity));

        Stream<FederatedIdentityModel> stream = provider.getFederatedIdentitiesStream(realm, userId);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testGetServiceAccount_exists() {
        ClientModel client = mock(ClientModel.class);
        RealmModel clientRealm = mock(RealmModel.class);
        when(client.getRealm()).thenReturn(clientRealm);
        when(client.getId()).thenReturn("client-id");
        when(clientRealm.getId()).thenReturn("realm-id");

        when(em.createNamedQuery(eq("getRealmUserByServiceAccount"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");

        UserModel result = provider.getServiceAccount(client);

        assertNotNull(result);
    }

    @Test
    void testGetUsersCount_simple() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(UserEntity.class)).thenReturn(root);
        when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);

        int count = provider.getUsersCount(realm, true);

        assertEquals(10, count);
    }

    @Test
    void testGetUsersCount_withGroups() {
        when(realm.getId()).thenReturn("realm-id");
        when(em.createNamedQuery(eq("userCountInGroups"), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(anyString(), any())).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(5L);

        int count = provider.getUsersCount(realm, Collections.singleton("group-id"));

        assertEquals(5, count);
    }

    @Test
    void testGetGroupMembersStream_simple() {
        GroupModel group = mock(GroupModel.class);
        when(group.getId()).thenReturn("group-id");
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(UserEntity.class)).thenReturn(mock(CriteriaQuery.class));
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");
        when(realm.getId()).thenReturn("realm-id");

        Stream<UserModel> stream = provider.getGroupMembersStream(realm, group, 0, 10);

        assertNotNull(stream);
    }

    @Test
    void testGetRoleMembersStream_simple() {
        RoleModel role = mock(RoleModel.class);
        when(role.getId()).thenReturn("role-id");
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(UserEntity.class)).thenReturn(mock(CriteriaQuery.class));
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");

        // Mocking session.users() which is called in getRoleMembersStream
        KeycloakSession usersSession = mock(KeycloakSession.class);
        UserProvider userProvider = mock(UserProvider.class);
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(eq(realm), anyString())).thenReturn(userModel);

        Stream<UserModel> stream = provider.getRoleMembersStream(realm, role, 0, 10);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testSearchForUserStream_simple() {
        String search = "test";
        when(realm.getId()).thenReturn("realm-id");
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(UserEntity.class)).thenReturn(mock(CriteriaQuery.class));
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");
        
        KeycloakSession usersSession = mock(KeycloakSession.class);
        UserProvider userProvider = mock(UserProvider.class);
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(eq(realm), anyString())).thenReturn(userModel);

        Stream<UserModel> stream = provider.searchForUserStream(realm, search, 0, 10);

        assertNotNull(stream);
    }

    @Test
    void testSearchForUserByUserAttributeStream_short() {
        String attrName = "attr";
        String attrValue = "val";
        when(realm.getId()).thenReturn("realm-id");
        when(em.createNamedQuery(eq("getRealmUsersByAttributeNameAndValue"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");

        Stream<UserModel> stream = provider.searchForUserByUserAttributeStream(realm, attrName, attrValue);

        assertNotNull(stream);
    }

    @Test
    void testSearchForUserByUserAttributeStream_long() {
        String attrName = "attr";
        String attrValue = "a".repeat(256);
        when(realm.getId()).thenReturn("realm-id");
        when(em.createNamedQuery(eq("getRealmUsersByAttributeNameAndLongValue"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");

        Stream<UserModel> stream = provider.searchForUserByUserAttributeStream(realm, attrName, attrValue);

        assertNotNull(stream);
    }
}
