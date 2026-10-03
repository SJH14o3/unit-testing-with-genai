package org.keycloak.models.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.Profile;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @Mock private CriteriaBuilder cb;
    @Mock private CriteriaQuery<Long> cq;
    @Mock private KeycloakSessionFactory sessionFactory;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        Map<Profile.Feature, Boolean> features = new EnumMap<>(Profile.Feature.class);
        Arrays.stream(Profile.Feature.values()).forEach(feature -> features.put(feature, false));
        features.put(Profile.Feature.ADMIN_FINE_GRAINED_AUTHZ, true);
        Profile.init(Profile.ProfileName.DEFAULT, features);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
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

            TypedQuery<UserRoleMappingEntity> roleQuery = mock(TypedQuery.class);
            when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(roleQuery);
            when(roleQuery.setParameter(anyString(), any())).thenReturn(roleQuery);
            when(roleQuery.getSingleResultOrNull()).thenReturn(null);

            UserModel result = provider.addUser(realm, "username");

            assertNotNull(result);
            verify(em).persist(any(UserEntity.class));
        }
    }

    @Test
    void testGetGroupMembersStream_complex() {
        Root<UserGroupMembershipEntity> groupMembership = mock(Root.class);
        Path<UserEntity> userPath = mock(Path.class);
        CriteriaQuery<UserEntity> query = mock(CriteriaQuery.class);
        GroupModel group = mock(GroupModel.class);
        when(group.getId()).thenReturn("group-id");
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(userTypedQuery);
        when(em.createQuery(query)).thenReturn(userTypedQuery);
        when(userTypedQuery.setFirstResult(anyInt())).thenReturn(userTypedQuery);
        when(userTypedQuery.setMaxResults(anyInt())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultStream()).thenReturn(Stream.of(userEntity));
        when(userEntity.getRealmId()).thenReturn("realm-id");
        when(realm.getId()).thenReturn("realm-id");
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(UserEntity.class)).thenReturn(query);
        when(query.from(UserGroupMembershipEntity.class)).thenReturn(groupMembership);
        when(groupMembership.get("user")).thenReturn((Path) userPath);
        when(userPath.get(anyString())).thenReturn(mock(Path.class));
        when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(cb.lower(any())).thenReturn(mock(Expression.class));
        when(cb.literal(any())).thenReturn(mock(Expression.class));
        when(cb.like(any(Expression.class), any(Expression.class))).thenReturn(mock(Predicate.class));
        when(cb.or(any(Predicate[].class))).thenReturn(mock(Predicate.class));
        when(query.select(any())).thenReturn(query);
        when(query.where(any(Predicate[].class))).thenReturn(query);
        when(query.where(anyList())).thenReturn(query);
        when(query.orderBy(anyList())).thenReturn(query);

        Stream<UserModel> stream = provider.getGroupMembersStream(realm, group, "search", true, 0, 10);

        assertNotNull(stream);
    }

    @Test
    void testGetIssuedVerifiableCredentialsStreamByUser() {
        String userId = "user-id";
        when(em.createNamedQuery(eq("issuedVcsByUser"), eq(IssuedVerifiableCredentialEntity.class))).thenReturn(issuedVcTypedQuery);
        when(issuedVcTypedQuery.getResultStream()).thenReturn(Stream.of(issuedVcEntity));
        when(issuedVcEntity.getId()).thenReturn("ivc-id");
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
        TypedQuery<FederatedIdentityEntity> federatedIdentityEntityQuery = mock(TypedQuery.class);
        when(em.getReference(eq(UserEntity.class), anyString())).thenReturn(userEntity);
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(federatedIdentityEntityQuery);
        when(federatedIdentityEntityQuery.setParameter(anyString(), any())).thenReturn(federatedIdentityEntityQuery);
        when(federatedIdentityEntityQuery.getResultList()).thenReturn(Collections.emptyList());

        assertNull(provider.getFederatedIdentity(realm, userModel, idp));
    }

    @Test
    void testGetUserByEmail_duplicateCheck() {
        String email = "dup@ex.com";
        UserEntity u1 = new UserEntity();
        u1.setEmail(email, false);
        UserEntity u2 = new UserEntity();
        u2.setEmail(email, false);
        
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
        TypedQuery<CredentialEntity> credentialQuery = mock(TypedQuery.class);
        when(em.createNamedQuery("credentialByUser", CredentialEntity.class)).thenReturn(credentialQuery);
        when(credentialQuery.setParameter(anyString(), any())).thenReturn(credentialQuery);
        when(credentialQuery.getResultStream()).thenReturn(Stream.empty());

        provider.createCredential(realm, userModel, cred);

        verify(em).persist(any(CredentialEntity.class));
    }

    @Test
    void testAddIssuedVerifiableCredential_all_branches() {
        // 1. With revision and attributes
        IssuedVerifiableCredentialModel model1 = new IssuedVerifiableCredentialModel();
        model1.setVerifiableCredentialId("vc-id");
        model1.setUserId("user-id");
        model1.setClientId("client-id");
        model1.setRevision("rev-1");

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.getReference(UserEntity.class, "user-id-2")).thenReturn(userEntity);
        when(em.getReference(UserEntity.class, "user-id-3")).thenReturn(userEntity);
        when(userEntity.getId()).thenReturn("user-id");

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
        when(em.find(UserVerifiableCredentialEntity.class, "vc-id-3")).thenReturn(vcEntity);

        provider.addIssuedVerifiableCredential(model3);
        verify(em, atLeast(2)).persist(any(IssuedVerifiableCredentialEntity.class));
    }
}
