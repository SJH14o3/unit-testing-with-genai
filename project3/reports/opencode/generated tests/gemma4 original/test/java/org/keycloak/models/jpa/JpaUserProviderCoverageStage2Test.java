package org.keycloak.models.jpa;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.util.KeycloakModelUtils;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileAttributes;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserProviderCoverageStage2Test {

    @Mock private KeycloakSession session;
    @Mock private EntityManager em;
    @Mock private RealmModel realm;
    @Mock private UserModel userModel;
    @Mock private UserEntity userEntity;
    @Mock private UserVerifiableCredentialEntity vcEntity;
    @Mock private IssuedVerifiableCredentialEntity issuedVcEntity;
    @Mock private TypedQuery<UserEntity> userTypedQuery;
    @Mock private TypedQuery<IssuedVerifiableCredentialEntity> issuedVcTypedQuery;
    @Mock private UserProfileProvider profileProvider;
    @Mock private UserProfile profile;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JpaUserProvider(session, em);
    }

    @Test
    void testAddUser_shortcut() {
        try (MockedStatic<KeycloakModelUtils> utils = mockStatic(KeycloakModelUtils.class)) {
            utils.when(KeycloakModelUtils::generateId).thenReturn("gen-id");
            when(realm.getId()).thenReturn("realm-id");
            when(realm.getDefaultRole()).thenReturn(mock(RoleModel.class));
            when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
            when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());

            UserModel result = provider.addUser(realm, "username");

            assertNotNull(result);
            verify(em).persist(any(UserEntity.class));
        }
    }

    @Test
    void testGetGroupMembersStream_complex() {
        GroupModel group = mock(GroupModel.class);
        when(group.getId()).thenReturn("group-id");
        when(em.getCriteriaBuilder()).thenReturn(mock(CriteriaBuilder.class));
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");
        when(realm.getId()).thenReturn("realm-id");

        Stream<UserModel> stream = provider.getGroupMembersStream(realm, group, "search", true, 0, 10);

        assertNotNull(stream);
    }

    @Test
    void testGetIssuedVerifiableCredentialsStreamByUser() {
        String userId = "user-id";
        when(em.createNamedQuery(eq("issuedVcsByUser"), eq(IssuedVerifiableCredentialEntity.class))).thenReturn(issuedVcTypedQuery);
        when(issuedVcTypedQuery.getResultStream()).thenReturn(Stream.of(issuedVcEntity));
        when(issuedVcEntity.getId()).thenReturn("ivc-id");
        when(issuedVcEntity.getUserId()).thenReturn(userId);
        when(issuedVcEntity.getVerifiableCredentialId()).thenReturn("vc-id");

        Stream<IssuedVerifiableCredentialModel> stream = provider.getIssuedVerifiableCredentialsStreamByUser(userId);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testGetSessionAndEntityManager() {
        assertEquals(session, provider.getSession());
        assertEquals(em, provider.getEntityManager());
    }

    @Test
    void testGetFederatedIdentity_not_found() {
        String idp = "idp";
        when(em.getReference(UserEntity.class, anyString())).thenReturn(userEntity);
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        assertNull(provider.getFederatedIdentity(realm, userModel, idp));
    }

    @Test
    void testGetUserByEmail_duplicateCheck() {
        String email = "dup@ex.com";
        UserEntity u1 = new UserEntity();
        u1.setEmail(email);
        UserEntity u2 = new UserEntity();
        u2.setEmail(email);
        
        when(em.createNamedQuery(eq("getRealmUserByEmail"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(u1, u2));

        assertThrows(ModelDuplicateException.class, () -> provider.getUserByEmail(realm, email));
    }

    @Test
    void testGetServiceAccount_multipleResults() {
        ClientModel client = mock(ClientModel.class);
        RealmModel clientRealm = mock(RealmModel.class);
        when(client.getRealm()).thenReturn(clientRealm);
        when(client.getId()).thenReturn("client-id");
        when(clientRealm.getId()).thenReturn("realm-id");

        when(em.createNamedQuery(eq("getRealmUserByServiceAccount"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(List.of(userEntity, userEntity));

        assertThrows(IllegalStateException.class, () -> provider.getServiceAccount(client));
    }

    @Test
    void testCreateCredential_userNotInContext() {
        CredentialModel cred = mock(CredentialModel.class);
        when(userModel.getId()).thenReturn("user-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.contains(userEntity)).thenReturn(false);

        provider.createCredential(realm, userModel, cred);

        verify(em, never()).persist(any(CredentialEntity.class));
    }

    @Test
    void testAddIssuedVerifiableCredential_all_branches() {
        // 1. With revision and attributes
        IssuedVerifiableCredentialModel model1 = new IssuedVerifiableCredentialModel();
        model1.setVerifiableCredentialId("vc-id");
        model1.setUserId("user-id");
        model1.setClientId("client-id");
        model1.setRevision("rev-1");
        model1.setUserAttributes(Map.of("a", List.of("b")));

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        IssuedVerifiableCredentialModel res1 = provider.addIssuedVerifiableCredential(model1);
        assertNotNull(res1);
        verify(em, atLeastOnce()).persist(any(IssuedVerifiableCredentialEntity.class));

        // 2. Without revision (fallback)
        IssuedVerifiableCredentialModel model2 = new IssuedVerifiableCredentialModel();
        model2.setVerifiableCredentialId("vc-id-2");
        model2.setUserId("user-id-2");
        model2.setClientId("client-id-2");
        model2.setRevision(null);

        when(em.find(UserVerifiableCredentialEntity.class, "vc-id-2")).thenReturn(vcEntity);
        when(vcEntity.getRevision()).thenReturn("vc-rev");

        IssuedVerifiableCredentialModel res2 = provider.addIssuedVerifiableCredential(model2);
        assertNotNull(res2);

        // 3. Without timestamp
        IssuedVerifiableCredentialModel model3 = new IssuedVerifiableCredentialModel();
        model3.setVerifiableCredentialId("vc-id-3");
        model3.setUserId("user-id-3");
        model3.setClientId("client-id-3");
        model3.setIssuedAt(null);

        when(em.getReference(UserEntity.class, "user-id-3")).thenReturn(userEntity);

        provider.addIssuedVerifiableCredential(model3);
        verify(em, atLeast(2)).persist(any(IssuedVerifiableCredentialEntity.class));
    }
}
