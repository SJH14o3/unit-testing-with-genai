package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.common.util.Time;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import org.keycloak.util.JsonSerialization;
import org.mockito.MockedStatic;
import org.keycloak.userprofile.UserProfileProvider;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserProviderPartialBranchTest {
    @Mock private KeycloakSession session;
    @Mock private EntityManager em;
    @Mock private RealmModel realm;
    @Mock private UserModel user;
    @Mock private RoleModel role;
    @Mock private ClientModel client;
    @Mock private ClientScopeModel enabledScope;
    @Mock private FederatedIdentityModel fedModel;
    @Mock private IdentityProviderModel ipm;
    @Mock private ComponentModel comp;

    /* ----------------------------------------------------------------- */
    /* addUser(RealmModel, String, String, boolean, boolean) – false branches */
    @Test
    public void testAddUserFalseBranches() {
        when(realm.getDefaultRole()).thenReturn(null); // ensure no default role added
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserModel u = provider.addUser(realm, "uid", "TestUser", false, false);
        assertNotNull(u);
        // Verify persists entity regardless of defaults
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(em).persist(captor.capture());
        UserEntity ent = captor.getValue();
        assertEquals("testuser", ent.getUsername());
    }

    /* ----------------------------------------------------------------- */
    /* updateGrantedConsentEntity – verifying removal when scope missing */
    @Test
    public void testCreateUserConsentClientScopeEntityRemoval() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity consent = new UserConsentEntity();
        ClientScopeModel scope = mock(ClientScopeModel.class);
        when(scope.getId()).thenReturn("scope1");
        // existing scope present in entity
        UserConsentClientScopeEntity existing = new UserConsentClientScopeEntity();
        existing.setScopeId("scope1");
        existing.setUserConsent(consent);
        consent.getGrantedClientScopes().add(existing);
        Collection<UserConsentClientScopeEntity> existingSet = new HashSet<>(Collections.singletonList(existing));
        Collection<UserConsentClientScopeEntity> removalSet = new HashSet<>(Collections.singletonList(existing));

        provider.createUserConsentClientScopeEntity(consent, scope, null, existingSet, removalSet);
        // Since scope already present, removalSet should no longer contain it
        assertTrue(removalSet.isEmpty());
        verify(em, never()).persist(any());
    }

    /* ----------------------------------------------------------------- */
    /* addVerifiableCredential – missing clientScopeId */
    @Test
    public void testAddVerifiableCredentialMissingClientScope() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("id", null);
        assertThrows(ModelException.class, () -> provider.addVerifiableCredential("uid", model));
    }

    /* ----------------------------------------------------------------- */
    /* addVerifiableCredential – null revision, created/updated dates */
    @Test
    public void testAddVerifiableCredentialRevisionAndDates() throws Exception {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("id", "cs");
        // no revision, createdDate, updatedDate, userAttributes
        model.setRevision(null);
        model.setCreatedDate(null);
        model.setUpdatedDate(null);
        List<String> attrVal = List.of("a");
        Map<String, List<String>> attrs = Map.of("key", attrVal);
        model.setUserAttributes(attrs);

        try (MockedStatic<SecretGenerator> sgMock = mockStatic(SecretGenerator.class)) {
            sgMock.when(() -> SecretGenerator.getInstance().generateSecureID()).thenReturn("rev123");
            try (MockedStatic<JsonSerialization> jsMock = mockStatic(JsonSerialization.class)) {
                jsMock.when(() -> JsonSerialization.writeValueAsString(attrs)).thenReturn("{\"key\":\"a\"}");
                JpaUserProvider provider = new JpaUserProvider(session, em);
                UserVerifiableCredentialEntity captured = new UserVerifiableCredentialEntity();
                ArgumentCaptor<UserVerifiableCredentialEntity> captor = ArgumentCaptor.forClass(UserVerifiableCredentialEntity.class);
                provider.addVerifiableCredential("uid", model);
                verify(em).persist(captor.capture());
                UserVerifiableCredentialEntity ent = captor.getValue();
                assertEquals("rev123", ent.getRevision());
                long now = Time.currentTimeMillis();
                assertTrue(ent.getCreatedDate() > now - 1000L && ent.getCreatedDate() < now + 1000L);
                assertTrue(ent.getUpdatedDate() > now - 1000L && ent.getUpdatedDate() < now + 1000L);
                assertEquals("{\"key\":\"a\"", ent.getUserAttributes());
            }
        }
    }

    /* ----------------------------------------------------------------- */
    /* grantToAllUsers – non‑client role container */
    @Test
    public void testGrantToAllUsersNonClientContainer() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(role.isClientRole()).thenReturn(false);
        when(role.getContainer()).thenReturn(realm);
        TypedQuery<?> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("grantRoleToAllUsers")).thenReturn(tq);
        provider.grantToAllUsers(realm, role);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("roleId", role.getId());
        verify(tq).executeUpdate();
    }

    /* ----------------------------------------------------------------- */
    /* getUserById – false branch (entity==null) */
    @Test
    public void testGetUserByIdNullEntity() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(em.find(UserEntity.class, "user1")).thenReturn(null);
        assertNull(provider.getUserById(realm, "user1"));
    }

    /* ----------------------------------------------------------------- */
    /* getUsersCount(boolean) – includeServiceAccount true */
    @Test
    public void testGetUsersCountIncludeServiceAccountTrue() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // Would need full Criteria API mock; simpler: assume that without predicate still executed.
        TypedQuery<Long> tq = mock(TypedQuery.class);
        when(em.createQuery(any())).thenReturn(tq);
        when(tq.getSingleResult()).thenReturn(10L);
        int count = provider.getUsersCount(realm, true);
        assertEquals(10, count);
        verify(em, never()).createNamedQuery("deleteMerged");
    }

    /* ----------------------------------------------------------------- */
    /* getUsersCount(Set) – groupIds not empty */
    @Test
    public void testGetUsersCountGroupsNonEmpty() {
        Set<String> groupIds = Set.of("g1", "g2");
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<Long> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("userCountInGroups", Long.class)).thenReturn(tq);
        when(tq.getSingleResult()).thenReturn(5L);
        int count = provider.getUsersCount(realm, groupIds);
        assertEquals(5, count);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("groupIds", groupIds);
    }

    /* ----------------------------------------------------------------- */
    /* toConsentModel – entity null */
    @Test
    public void testToConsentModelNullEntity() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        assertNull(provider.toConsentModel(realm, null));
    }
}

