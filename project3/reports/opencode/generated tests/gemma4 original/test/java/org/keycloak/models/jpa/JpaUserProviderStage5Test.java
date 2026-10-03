package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.UserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.CredentialEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserProviderStage5Test {

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
    private JpaUserCredentialStore credentialStore;

    @Mock
    private CredentialModel credentialModel;

    @Mock
    private CredentialEntity credentialEntity;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JpaUserProvider(session, em);
        
        // Inject the mocked credentialStore into the private final field
        Field field = JpaUserProvider.class.getDeclaredField("credentialStore");
        field.setAccessible(true);
        field.set(provider, credentialStore);
    }

    @Test
    void testUpdateCredential() {
        provider.updateCredential(realm, userModel, credentialModel);
        verify(credentialStore).updateCredential(realm, userModel, credentialModel);
    }

    @Test
    void testCreateCredential_success() {
        when(credentialStore.createCredentialEntity(realm, userModel, credentialModel)).thenReturn(credentialEntity);
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.contains(userEntity)).thenReturn(true);
        when(userEntity.getCredentials()).thenReturn(new java.util.ArrayList<>());
        
        // toModel is protected and calls credentialStore.toModel
        when(credentialStore.toModel(credentialEntity)).thenReturn(credentialModel);

        CredentialModel result = provider.createCredential(realm, userModel, credentialModel);

        assertNotNull(result);
        verify(userEntity).getCredentials().add(credentialEntity);
    }

    @Test
    void testRemoveStoredCredential_exists() {
        String credId = "cred-id";
        when(credentialStore.removeCredentialEntity(realm, userModel, credId)).thenReturn(credentialEntity);
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.contains(userEntity)).thenReturn(true);
        when(userEntity.getCredentials()).thenReturn(new java.util.ArrayList<>());

        boolean removed = provider.removeStoredCredential(realm, userModel, credId);

        assertTrue(removed);
        verify(userEntity).getCredentials().remove(credentialEntity);
    }

    @Test
    void testRemoveStoredCredential_notExists() {
        String credId = "cred-id";
        when(credentialStore.removeCredentialEntity(realm, userModel, credId)).thenReturn(null);
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.contains(userEntity)).thenReturn(true);

        boolean removed = provider.removeStoredCredential(realm, userModel, credId);

        assertFalse(removed);
    }

    @Test
    void testGetStoredCredentialById() {
        String credId = "cred-id";
        when(credentialStore.getStoredCredentialById(realm, userModel, credId)).thenReturn(credentialModel);

        CredentialModel result = provider.getStoredCredentialById(realm, userModel, credId);

        assertEquals(credentialModel, result);
    }

    @Test
    void testGetStoredCredentialsStream() {
        when(credentialStore.getStoredCredentialsStream(realm, userModel)).thenReturn(Stream.of(credentialModel));

        Stream<CredentialModel> stream = provider.getStoredCredentialsStream(realm, userModel);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testGetStoredCredentialsByTypeStream_userInContext() {
        String type = "password";
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.contains(userEntity)).thenReturn(true);
        when(userEntity.getCredentials()).thenReturn(List.of(credentialEntity));
        when(credentialEntity.getType()).thenReturn(type);
        when(credentialStore.toModel(credentialEntity)).thenReturn(credentialModel);

        Stream<CredentialModel> stream = provider.getStoredCredentialsByTypeStream(realm, userModel, type);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testGetStoredCredentialsByTypeStream_notInContext() {
        String type = "password";
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.contains(userEntity)).thenReturn(false);
        when(credentialStore.getStoredCredentialsByTypeStream(realm, userModel, type)).thenReturn(Stream.of(credentialModel));

        Stream<CredentialModel> stream = provider.getStoredCredentialsByTypeStream(realm, userModel, type);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testGetStoredCredentialByNameAndType() {
        when(credentialStore.getStoredCredentialByNameAndType(realm, userModel, "name", "type")).thenReturn(credentialModel);

        CredentialModel result = provider.getStoredCredentialByNameAndType(realm, userModel, "name", "type");

        assertEquals(credentialModel, result);
    }

    @Test
    void testMoveCredentialTo() {
        when(credentialStore.moveCredentialTo(realm, userModel, "id1", "id2")).thenReturn(true);

        boolean result = provider.moveCredentialTo(realm, userModel, "id1", "id2");

        assertTrue(result);
    }

    @Test
    void testSetNotBeforeForUser() {
        int notBefore = 12345;
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.setNotBeforeForUser(realm, userModel, notBefore);

        verify(userEntity).setNotBefore(notBefore);
    }

    @Test
    void testGetNotBeforeOfUser() {
        int notBefore = 12345;
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(userEntity.getNotBefore()).thenReturn(notBefore);

        int result = provider.getNotBeforeOfUser(realm, userModel);

        assertEquals(notBefore, result);
    }

    @Test
    void testGrantToAllUsers() {
        RoleModel role = mock(RoleModel.class);
        when(role.isClientRole()).thenReturn(false);
        when(role.getContainer()).thenReturn(realm);
        when(role.getId()).thenReturn("role-id");
        when(realm.getId()).thenReturn("realm-id");

        provider.grantToAllUsers(realm, role);

        verify(em).createNamedQuery("grantRoleToAllUsers");
    }

    @Test
    void testGetUserCredentialManager() {
        when(session.getContext()).thenReturn(mock(org.keycloak.models.KeycloakContext.class));
        when(session.getContext().getRealm()).thenReturn(realm);

        UserCredentialManager manager = provider.getUserCredentialManager(userModel);

        assertNotNull(manager);
    }
}
