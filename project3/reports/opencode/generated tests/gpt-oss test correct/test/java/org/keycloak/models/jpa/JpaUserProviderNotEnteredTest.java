package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.storage.StorageId;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import org.keycloak.util.JsonSerialization;
import org.mockito.MockedStatic;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserProviderNotEnteredTest {
    @Mock private KeycloakSession session;
    @Mock private EntityManager em;
    @Mock private RealmModel realm;
    @Mock private UserModel user;
    @Mock private ClientModel client;
    @Mock private ClientScopeModel clientScope;
    @Mock private RoleModel role;
    @Mock private GroupModel group;
    @Mock private IdentityProviderModel ipm;
    @Mock private ComponentModel comp;

    /* --------------------------------------------------------------------- */
    /* addUser(String username) – ensured that default values are applied */
    @Test
    public void testAddUserByUsernameAddsDefaults() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        RoleModel defaultRole = mock(RoleModel.class);
        when(defaultRole.getId()).thenReturn("defaultRoleId");
        when(realm.getDefaultRole()).thenReturn(defaultRole);
        when(realm.getDefaultGroupsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        // the default role is already mapped, so no role mapping is created
        TypedQuery<UserRoleMappingEntity> hasRoleQuery = mock(TypedQuery.class, RETURNS_SELF);
        when(hasRoleQuery.getSingleResultOrNull()).thenReturn(new UserRoleMappingEntity());
        when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(hasRoleQuery);

        UserModel result = provider.addUser(realm, "TestUser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    /* --------------------------------------------------------------------- */
    /* removeUser(UserModel) – null entity path */
    @Test
    public void testRemoveUserNullEntity() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(em.find(UserEntity.class, user.getId(), LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        boolean res = provider.removeUser(realm, user);
        assertFalse(res);
    }

    /* --------------------------------------------------------------------- */
    /* addFederatedIdentity – persistence check */
    @Test
    public void testAddFederatedIdentityPersists() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        FederatedIdentityModel fed = new FederatedIdentityModel("prov", "uid", "un", "tk");
        UserEntity nEntity = new UserEntity();
        when(em.getReference(UserEntity.class, user.getId())).thenReturn(nEntity);
        provider.addFederatedIdentity(realm, user, fed);
        ArgumentCaptor<FederatedIdentityEntity> captor = ArgumentCaptor.forClass(FederatedIdentityEntity.class);
        verify(em).persist(captor.capture());
        FederatedIdentityEntity e = captor.getValue();
        assertEquals(fed.getIdentityProvider(), e.getIdentityProvider());
        assertEquals(fed.getUserName(), e.getUserName());
    }

    /* --------------------------------------------------------------------- */
    /* Consent stream – getConsentsStream */
    @Test
    public void testGetConsentsStream() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<UserConsentEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userConsentsByUser", UserConsentEntity.class)).thenReturn(query);
        UserConsentEntity entity = new UserConsentEntity();
        entity.setId("consentId");
        entity.setClientId("clientId");
        entity.setCreatedDate(1L);
        entity.setLastUpdatedDate(2L);
        when(query.getResultStream()).thenReturn(Stream.of(entity));
        // stub toConsentModel via spy; but we rely on real method using realm.getClientById
        ClientModel cm = mock(ClientModel.class);
        when(realm.getClientById(anyString())).thenReturn(cm);
        JpaUserProvider spy = Mockito.spy(provider);
        // count() would short-circuit on the known stream size, so materialize the elements
        List<UserConsentModel> consents = spy.getConsentsStream(realm, "user").toList();
        assertEquals(1, consents.size());
        assertEquals(cm, consents.get(0).getClient());
    }

    /* --------------------------------------------------------------------- */
    /* getGrantedConsentEntity – result counts */
    @Test
    public void testGetGrantedConsentEntityZero() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<UserConsentEntity> query = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createNamedQuery("userConsentByUserAndClient", UserConsentEntity.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());
        // no consent stored for the user/client pair
        assertNull(provider.getConsentByClient(realm, "user", "client"));
    }

    /* --------------------------------------------------------------------- */
    /* getVerifiableCredentialById and byClientScope */
    @Test
    public void testGetVerifiableCredentialByIdNotFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(em.find(UserVerifiableCredentialEntity.class, "id")).thenReturn(null);
        assertNull(provider.getVerifiableCredentialById("id"));
    }

    @Test
    public void testGetVerifiableCredentialByIdFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialEntity ent = new UserVerifiableCredentialEntity();
        ent.setId("id");
        when(em.find(UserVerifiableCredentialEntity.class, "id")).thenReturn(ent);
        UserVerifiableCredentialModel mdl = provider.getVerifiableCredentialById("id");
        assertNotNull(mdl);
        assertEquals("id", mdl.getId());
    }

    /* --------------------------------------------------------------------- */
    /* preRemove(RealmModel, IdentityProviderModel) – ensure execution */
    @Test
    public void testPreRemoveIdentityProvider() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<?> tq = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createNamedQuery("deleteFederatedIdentityByProvider")).thenReturn(tq);
        provider.preRemove(realm, ipm);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("providerAlias", ipm.getAlias());
        verify(tq).executeUpdate();
    }

    /* --------------------------------------------------------------------- */
    /* preRemove(RealmModel, RoleModel) */
    @Test
    public void testPreRemoveRole() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        RoleModel role = mock(RoleModel.class);
        when(role.getId()).thenReturn("roleId");
        TypedQuery<?> tq = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createNamedQuery("deleteUserRoleMappingsByRole")).thenReturn(tq);
        provider.preRemove(realm, role);
        verify(tq).setParameter("roleId", "roleId");
        verify(tq).executeUpdate();
    }

    @Test
    public void testPreRemoveGroup() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        GroupModel group = mock(GroupModel.class);
        when(group.getId()).thenReturn("groupId");
        TypedQuery<?> tq = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createNamedQuery("deleteUserGroupMembershipsByGroup")).thenReturn(tq);
        provider.preRemove(realm, group);
        verify(tq).setParameter("groupId", "groupId");
        verify(tq).executeUpdate();
    }

    @Test
    public void testPreRemoveClient() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        ClientModel client = mock(ClientModel.class);
        when(client.getId()).thenReturn("clientId");
        TypedQuery<?> tq1 = mock(TypedQuery.class, RETURNS_SELF); // deleteUserConsentClientScopesByClient
        TypedQuery<?> tq2 = mock(TypedQuery.class, RETURNS_SELF); // deleteUserConsentsByClient
        TypedQuery<?> tq3 = mock(TypedQuery.class, RETURNS_SELF); // deleteIssuedVcsByClient
        when(em.createNamedQuery("deleteUserConsentClientScopesByClient")).thenReturn(tq1);
        when(em.createNamedQuery("deleteUserConsentsByClient")).thenReturn(tq2);
        when(em.createNamedQuery("deleteIssuedVcsByClient")).thenReturn(tq3);
        provider.preRemove(realm, client);
        verify(tq1).setParameter("clientId", "clientId");
        verify(tq2).setParameter("clientId", "clientId");
        verify(tq3).setParameter("clientId", "clientId");
    }

    @Test
    public void testPreRemoveClientExternal() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        // external clients are stored with the "f:<providerId>:<clientId>" id format
        when(client.getId()).thenReturn("f:prov:clientid");
        StorageId sid = new StorageId("f:prov:clientid");
        TypedQuery<?> tq1 = mock(TypedQuery.class, RETURNS_SELF); // deleteUserConsentClientScopesByExternalClient
        TypedQuery<?> tq2 = mock(TypedQuery.class, RETURNS_SELF); // deleteUserConsentsByExternalClient
        when(em.createNamedQuery("deleteUserConsentClientScopesByExternalClient")).thenReturn(tq1);
        when(em.createNamedQuery("deleteUserConsentsByExternalClient")).thenReturn(tq2);
        provider.preRemove(realm, client);
        verify(tq1).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq1).setParameter("externalClientId", sid.getExternalId());
        verify(tq2).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq2).setParameter("externalClientId", sid.getExternalId());
    }

    @Test
    public void testPreRemoveClientScope() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        ClientScopeModel cs = mock(ClientScopeModel.class);
        when(cs.getId()).thenReturn("scopeId");
        TypedQuery<?> tq1 = mock(TypedQuery.class, RETURNS_SELF); // deleteUserConsentClientScopesByClientScope
        TypedQuery<?> tq2 = mock(TypedQuery.class, RETURNS_SELF); // deleteIssuedVcsByClientScope
        TypedQuery<?> tq3 = mock(TypedQuery.class, RETURNS_SELF); // deleteVerifiableCredentialsByClientScope
        when(em.createNamedQuery("deleteUserConsentClientScopesByClientScope")).thenReturn(tq1);
        when(em.createNamedQuery("deleteIssuedVcsByClientScope")).thenReturn(tq2);
        when(em.createNamedQuery("deleteVerifiableCredentialsByClientScope")).thenReturn(tq3);
        provider.preRemove(cs);
        verify(tq1).setParameter("scopeId", "scopeId");
        verify(tq2).setParameter("scopeId", "scopeId");
        verify(tq3).setParameter("scopeId", "scopeId");
    }
}

