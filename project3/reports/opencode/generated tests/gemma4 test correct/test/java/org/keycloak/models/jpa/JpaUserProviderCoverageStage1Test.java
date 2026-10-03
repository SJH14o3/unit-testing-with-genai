package org.keycloak.models.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.Profile;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.userprofile.Attributes;
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
class JpaUserProviderCoverageStage1Test {

    @Mock private KeycloakSession session;
    @Mock private EntityManager em;
    @Mock private RealmModel realm;
    @Mock private UserModel userModel;
    @Mock private UserEntity userEntity;
    @Mock private ClientModel clientModel;
    @Mock private ClientScopeModel clientScopeModel;
    @Mock private RoleModel roleModel;
    @Mock private GroupModel groupModel;
    @Mock private UserProfileProvider profileProvider;
    @Mock private UserProfile profile;
    @Mock private TypedQuery<UserEntity> userTypedQuery;
    @Mock private TypedQuery<UserConsentEntity> consentTypedQuery;
    @Mock private TypedQuery<Long> longTypedQuery;
    @Mock private CriteriaBuilder cb;
    @Mock private CriteriaQuery<Long> cq;
    @Mock private Root<UserEntity> root;
    @Mock private Predicate predicate;
    @Mock private KeycloakContext context;
    @Mock private KeycloakSessionFactory sessionFactory;
    @Mock private Attributes attributes;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        Map<Profile.Feature, Boolean> features = new EnumMap<>(Profile.Feature.class);
        Arrays.stream(Profile.Feature.values()).forEach(feature -> features.put(feature, false));
        features.put(Profile.Feature.ADMIN_FINE_GRAINED_AUTHZ, true);
        features.put(Profile.Feature.PARAMETERIZED_SCOPES, true);
        Profile.init(Profile.ProfileName.DEFAULT, features);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        provider = new JpaUserProvider(session, em);
    }

    // --- addUser branches ---

    @Test
    void testAddUser_nullId() {
        try (MockedStatic<KeycloakModelUtils> utils = mockStatic(KeycloakModelUtils.class)) {
            utils.when(KeycloakModelUtils::generateId).thenReturn("generated-id");
            when(realm.getId()).thenReturn("realm-id");
            when(realm.getDefaultRole()).thenReturn(roleModel);
            when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
            when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());

            UserModel result = provider.addUser(realm, null, "user", false, false);

            assertNotNull(result);
            verify(em).persist(any(UserEntity.class));
        }
    }

    @Test
    void testAddUser_batchMode() {
        try (MockedStatic<EntityManagers> entityManagers = mockStatic(EntityManagers.class)) {
            entityManagers.when(EntityManagers::isBatchMode).thenReturn(true);
            when(realm.getId()).thenReturn("realm-id");
            when(realm.getDefaultRole()).thenReturn(roleModel);
            when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
            when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());

            provider.addUser(realm, "user-id", "user", false, false);

            verify(em, never()).flush();
        }
    }

    @Test
    void testAddUser_withDefaults() {
        when(realm.getId()).thenReturn("realm-id");
        when(realm.getDefaultRole()).thenReturn(roleModel);
        when(realm.getDefaultGroupsStream()).thenReturn(Stream.of(groupModel));

        TypedQuery<UserRoleMappingEntity> roleQuery = mock(TypedQuery.class);
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(roleQuery);
        when(roleQuery.setParameter(anyString(), any())).thenReturn(roleQuery);
        when(roleQuery.getSingleResultOrNull()).thenReturn(null);

        TypedQuery<UserGroupMembershipEntity> groupQuery = mock(TypedQuery.class);
        when(em.createNamedQuery("userMemberOf", UserGroupMembershipEntity.class)).thenReturn(groupQuery);
        when(groupQuery.setParameter(anyString(), any())).thenReturn(groupQuery);
        when(groupQuery.getSingleResultOrNull()).thenReturn(null);
        
        RequiredActionProviderModel action = mock(RequiredActionProviderModel.class);
        when(action.isEnabled()).thenReturn(true);
        when(action.isDefaultAction()).thenReturn(true);
        when(action.getAlias()).thenReturn("action");
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.of(action));
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        when(profileProvider.create(UserProfileContext.USER_API, userModel)).thenReturn(profile);

        provider.addUser(realm, "user-id", "user", true, true);

        verify(em, atLeastOnce()).persist(any(UserEntity.class));
    }

    // --- addConsent / getConsent branches ---

    @Test
    void testAddConsent_externalClient() {
        UserConsentModel consent = mock(UserConsentModel.class);
        ClientModel client = mock(ClientModel.class);
        when(consent.getClient()).thenReturn(client);
        // Mocking StorageId logic: external client usually has a colon
        when(client.getId()).thenReturn("provider:external-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.addConsent(realm, "user-id", consent);

        verify(em).persist(any(UserConsentEntity.class));
    }

    @Test
    void testGetConsentByClient_multipleResults() {
        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(List.of(mock(UserConsentEntity.class), mock(UserConsentEntity.class)));

        assertThrows(ModelException.class, () -> provider.getConsentByClient(realm, "user-id", "client-id"));
    }

    // --- toConsentModel & updateGrantedConsentEntity branches ---

    @Test
    void testToConsentModel_clientNotFound() {
        UserConsentEntity entity = new UserConsentEntity();
        entity.setClientId("non-existent");
        when(realm.getClientById("non-existent")).thenReturn(null);
        when(em.createNamedQuery("userConsentByUserAndClient", UserConsentEntity.class)).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(List.of(entity));


        assertThrows(ModelException.class, () -> provider.getConsentByClient(realm, "user-id", "non-existent"));
    }

    @Test
    void testUpdateGrantedConsentEntity_parameterizedScope() {
        UserConsentEntity consentEntity = new UserConsentEntity();
        consentEntity.setGrantedClientScopes(new ArrayList<>());
        
        UserConsentModel consentModel = mock(UserConsentModel.class);
        ClientScopeModel parameterizedScope = mock(ClientScopeModel.class);
        when(parameterizedScope.getId()).thenReturn("scope-id");
        when(parameterizedScope.isParameterizedScope()).thenReturn(true);
        when(consentModel.getGrantedClientScopes()).thenReturn(Set.of(parameterizedScope));
        when(consentModel.getParameters(parameterizedScope)).thenReturn(List.of("param-value"));
        when(consentModel.getClient()).thenReturn(clientModel);
        when(clientModel.getId()).thenReturn("client-id");

        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(List.of(consentEntity));
        when(clientModel.getId()).thenReturn("client-id");
        when(realm.getClientById("client-id")).thenReturn(clientModel);

        provider.updateConsent(realm, "user-id", consentModel);

        verify(em, atLeastOnce()).persist(any(UserConsentClientScopeEntity.class));
    }

    // --- addVerifiableCredential branches ---

    @Test
    void testAddVerifiableCredential_withRevision() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("user-id", "scope-id");
        model.setRevision("existing-rev");

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        when(provider.getUserById(eq(realm), eq("user-id"))).thenReturn(userModel);
        when(profileProvider.create(eq(UserProfileContext.USER_API), nullable(UserModel.class))).thenReturn(profile);
        when(profile.getAttributes()).thenReturn(attributes);
        when(attributes.getReadable()).thenReturn(Collections.emptyMap());

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    @Test
    void testAddVerifiableCredential_withoutRevision() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("user-id", "scope-id");
        model.setRevision(null);

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.find(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);
        when(realm.getId()).thenReturn("realm-id");
        when(userEntity.getRealmId()).thenReturn("realm-id");
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        when(profileProvider.create(eq(UserProfileContext.USER_API), any(UserModel.class))).thenReturn(profile);
        when(profile.getAttributes()).thenReturn(attributes);
        when(attributes.getReadable()).thenReturn(Collections.emptyMap());

        provider.addVerifiableCredential("user-id", model);


        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    @Test
    void testAddVerifiableCredential_withAttributes() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("user-id", "scope-id");
        model.setUserAttributes(Map.of("attr", List.of("val")));

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    @Test
    void testAddVerifiableCredential_withoutAttributes() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("user-id", "scope-id");
        model.setUserAttributes(null);

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.find(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);
        when(realm.getId()).thenReturn("realm-id");
        when(userEntity.getRealmId()).thenReturn("realm-id");
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        when(profileProvider.create(eq(UserProfileContext.USER_API), any(UserModel.class))).thenReturn(profile);
        when(profile.getAttributes()).thenReturn(attributes);
        when(attributes.getReadable()).thenReturn(Collections.emptyMap());
        when(context.getRealm()).thenReturn(realm);

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    // --- ensureEmailConstraint branches ---

    @Test
    void testEnsureEmailConstraint_duplicates() {
        UserEntity u1 = new UserEntity();
        u1.setEmail("dup@ex.com", true);
        UserEntity u2 = new UserEntity();
        u2.setEmail("dup@ex.com", true);
        List<UserEntity> users = List.of(u1, u2);

        when(em.createNamedQuery(eq("getRealmUserByEmail"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(users);

        assertThrows(ModelDuplicateException.class, () -> provider.getUserByEmail(realm, "dup@ex.com"));
    }

    @Test
    void testEnsureEmailConstraint_updateConstraint() {
        UserEntity user = new UserEntity();
        user.setEmail("new@ex.com", true);
        user.setEmailConstraint("old@ex.com");
        List<UserEntity> users = List.of(user);
        
        when(realm.isDuplicateEmailsAllowed()).thenReturn(false);
        when(em.createNamedQuery(eq("getRealmUserByEmail"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(users);

        provider.getUserByEmail(realm, "new@ex.com");

        verify(em).persist(user);
    }

    // --- getFederatedIdentity / getServiceAccount branches ---

    @Test
    void testGetFederatedIdentity_notExists() {
        TypedQuery<FederatedIdentityEntity> federatedIdentityEntityQuery = mock(TypedQuery.class);
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class)))
                .thenReturn(federatedIdentityEntityQuery);
        when(federatedIdentityEntityQuery.setParameter(anyString(), any())).thenReturn(federatedIdentityEntityQuery);
        when(federatedIdentityEntityQuery.getResultList()).thenReturn(Collections.emptyList());

        assertNull(provider.getFederatedIdentity(realm, userModel, "idp"));
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

    // --- getUsersCount variants ---

    @Test
    void testGetUsersCount_variants() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(UserEntity.class)).thenReturn(root);
        when(cq.select(any())).thenReturn(cq);
        when(cq.where(anyList())).thenReturn(cq);
        when(cb.equal(any(), any())).thenReturn(predicate);
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);

        Join<UserEntity, UserAttributeEntity> attributesJoin = mock(Join.class);
        when(root.join("attributes", JoinType.LEFT)).thenReturn((Join) attributesJoin);

        // 1. includeServiceAccount = true
        assertEquals(10, provider.getUsersCount(realm, true));

        // 2. with groupIds
        when(em.createNamedQuery(eq("userCountInGroups"), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(anyString(), any())).thenReturn(longTypedQuery);
        assertEquals(10, provider.getUsersCount(realm, Set.of("group-1")));

        // 3. with params
        assertEquals(10, provider.getUsersCount(realm, Map.of("attr", "val")));
    }
}
