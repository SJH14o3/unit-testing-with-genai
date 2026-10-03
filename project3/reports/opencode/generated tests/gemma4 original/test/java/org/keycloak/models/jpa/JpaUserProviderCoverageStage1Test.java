package org.keycloak.models.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.util.KeycloakModelUtils;
import org.keycloak.common.util.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.userprofile.UserProfile;
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

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
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
        
        RequiredActionProviderModel action = mock(RequiredActionProviderModel.class);
        when(action.isEnabled()).thenReturn(true);
        when(action.isDefaultAction()).thenReturn(true);
        when(action.getAlias()).thenReturn("action");
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.of(action));

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

        assertThrows(ModelException.class, () -> provider.getConsentByClient(realm, "user-id", "non-existent"));
    }

    @Test
    void testUpdateGrantedConsentEntity_parameterizedScope() {
        UserConsentEntity consentEntity = new UserConsentEntity();
        consentEntity.setGrantedClientScopes(new ArrayList<>());
        
        UserConsentModel consentModel = mock(UserConsentModel.class);
        ClientScopeModel parameterizedScope = mock(ClientScopeModel.class);
        when(parameterizedScope.getId()).thenReturn("scope-id");
        when(ClientScopeModel.isParameterizedScope(parameterizedScope)).thenReturn(true);
        when(consentModel.getGrantedClientScopes()).thenReturn(List.of(parameterizedScope));
        when(consentModel.getParameters(parameterizedScope)).thenReturn(List.of("param-value"));

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
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("scope-id");
        model.setRevision("existing-rev");

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    @Test
    void testAddVerifiableCredential_withoutRevision() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("scope-id");
        model.setRevision(null);

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    @Test
    void testAddVerifiableCredential_withAttributes() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("scope-id");
        model.setUserAttributes(Map.of("attr", List.of("val")));

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    @Test
    void testAddVerifiableCredential_withoutAttributes() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("scope-id");
        model.setUserAttributes(null);

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        when(profileProvider.create(any(), any())).thenReturn(profile);
        when(profile.getAttributes()).thenReturn(mock(org.keycloak.userprofile.UserProfileAttributes.class));
        when(profile.getAttributes().getReadable()).thenReturn(Map.of());

        provider.addVerifiableCredential("user-id", model);

        verify(em).persist(any(UserVerifiableCredentialEntity.class));
    }

    // --- ensureEmailConstraint branches ---

    @Test
    void testEnsureEmailConstraint_duplicates() {
        UserEntity u1 = new UserEntity();
        u1.setEmail("dup@ex.com");
        UserEntity u2 = new UserEntity();
        u2.setEmail("dup@ex.com");
        List<UserEntity> users = List.of(u1, u2);

        when(em.createNamedQuery(eq("getRealmUserByEmail"), eq(UserEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), anyString())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(users);

        assertThrows(ModelDuplicateException.class, () -> provider.getUserByEmail(realm, "dup@ex.com"));
    }

    @Test
    void testEnsureEmailConstraint_updateConstraint() {
        UserEntity user = new UserEntity();
        user.setEmail("new@ex.com");
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
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(userTypedQuery);
        when(userTypedQuery.setParameter(anyString(), any())).thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(Collections.emptyList());

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
        when(cb.equal(any(), any())).thenReturn(predicate);
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);

        // 1. includeServiceAccount = true
        assertEquals(10, provider.getUsersCount(realm, true));

        // 2. with groupIds
        when(em.createNamedQuery(eq("userCountInGroups"), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(anyString(), any())).thenReturn(longTypedQuery);
        assertEquals(5, provider.getUsersCount(realm, Set.of("group-1")));

        // 3. with params
        assertEquals(10, provider.getUsersCount(realm, Map.of("attr", "val")));
    }
}
