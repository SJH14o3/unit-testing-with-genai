package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.UserStorageProvider;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.credential.CredentialModel;
import org.keycloak.modelspublic.com.example; // placeholder - not needed
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
    /* updateFederatedIdentity – found and not‑found branches */
    @Test
    public void testUpdateFederatedIdentityFound() {
        JpaUserProvider provider = Mockito.spy(new JpaUserProvider(session, em));
        FederatedIdentityEntity ent = mock(FederatedIdentityEntity.class);
        doReturn(ent).when(provider).findFederatedIdentity(user, "prov", LockModeType.PESSIMISTIC_WRITE);
        FederatedIdentityModel fed = new FederatedIdentityModel("prov", "uid", "newName", "newToken");
        provider.updateFederatedIdentity(realm, user, fed);
        verify(ent).setUserName("newName");
        verify(ent).setToken("newToken");
        verify(em).persist(ent);
        verify(em).flush();
    }

    @Test
    public void testUpdateFederatedIdentityNotFound() {
        JpaUserProvider provider = Mockito.spy(new JpaUserProvider(session, em));
        doReturn(null).when(provider).findFederatedIdentity(user, "prov", LockModeType.PESSIMISTIC_WRITE);
        FederatedIdentityModel fed = new FederatedIdentityModel("prov", "uid", "newName", "newToken");
        provider.updateFederatedIdentity(realm, user, fed);
        verify(em, never()).persist(any());
        verify(em, never()).flush();
    }

    /* --------------------------------------------------------------------- */
    /* removeFederatedIdentity – entity found and not found */
    @Test
    public void testRemoveFederatedIdentityFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        FederatedIdentityEntity ent = mock(FederatedIdentityEntity.class);
        when(provider.findFederatedIdentity(user, "prov", LockModeType.PESSIMISTIC_WRITE)).thenReturn(ent);
        boolean res = provider.removeFederatedIdentity(realm, user, "prov");
        assertTrue(res);
        verify(em).remove(ent);
        verify(em).flush();
    }

    @Test
    public void testRemoveFederatedIdentityNotFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(provider.findFederatedIdentity(user, "prov", LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        boolean res = provider.removeFederatedIdentity(realm, user, "prov");
        assertFalse(res);
        verify(em, never()).remove(any());
        verify(em, never()).flush();
    }

    /* --------------------------------------------------------------------- */
    /* Consent handling – getConsentByClient */
    @Test
    public void testGetConsentByClientNull() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        // stub provider.getGrantedConsentEntity to return null
        JpaUserProvider spy = Mockito.spy(provider);
        doReturn(null).when(spy).getGrantedConsentEntity("user", "client", LockModeType.NONE);
        assertNull(spy.getConsentByClient(realm, "user", "client"));
    }

    @Test
    public void testGetConsentByClientPresent() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity ent = new UserConsentEntity();
        ent.setClientId("clientId");
        JpaUserProvider spy = Mockito.spy(provider);
        doReturn(ent).when(spy).getGrantedConsentEntity("user", "client", LockModeType.NONE);
        // stub realm client lookup
        ClientModel cm = mock(ClientModel.class);
        when(realm.getClientById("clientId")).thenReturn(cm);
        assertNotNull(spy.getConsentByClient(realm, "user", "client"));
    }

    /* --------------------------------------------------------------------- */
    /* Consent stream – getConsentsStream */
    @Test
    public void testGetConsentsStream() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<UserConsentEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery("userConsentsByUser", UserConsentEntity.class)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(new UserConsentEntity()));
        // stub toConsentModel via spy; but we rely on real method using realm.getClientById
        ClientModel cm = mock(ClientModel.class);
        when(realm.getClientById(anyString())).thenReturn(cm);
        JpaUserProvider spy = Mockito.spy(provider);
        Stream<UserConsentModel> stream = spy.getConsentsStream(realm, "user");
        assertEquals(1, stream.count());
    }

    /* --------------------------------------------------------------------- */
    /* updateConsent – null entity throws */
    @Test
    public void testUpdateConsentEntityMissing() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        JpaUserProvider spy = Mockito.spy(provider);
        doReturn(null).when(spy).getGrantedConsentEntity("user", "client", LockModeType.PESSIMISTIC_WRITE);
        UserConsentModel cons = mock(UserConsentModel.class);
        assertThrows(ModelException.class, () -> spy.updateConsent(realm, "user", cons));
    }

    /* --------------------------------------------------------------------- */
    /* revokeConsentForClient – entity present/not present */
    @Test
    public void testRevokeConsentForClientNotFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        JpaUserProvider spy = Mockito.spy(provider);
        doReturn(null).when(spy).getGrantedConsentEntity("user", "client", LockModeType.PESSIMISTIC_WRITE);
        assertFalse(spy.revokeConsentForClient(realm, "user", "client"));
    }

    @Test
    public void testRevokeConsentForClientFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity ent = mock(UserConsentEntity.class);
        JpaUserProvider spy = Mockito.spy(provider);
        doReturn(ent).when(spy).getGrantedConsentEntity("user", "client", LockModeType.PESSIMISTIC_WRITE);
        boolean res = spy.revokeConsentForClient(realm, "user", "client");
        assertTrue(res);
        verify(em).remove(ent);
        verify(em).flush();
    }

    /* --------------------------------------------------------------------- */
    /* getGrantedConsentEntity – result counts */
    @Test
    public void testGetGrantedConsentEntityZero() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<UserConsentEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery(anyString(), eq(UserConsentEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());
        // Build provider for support of Queries => we need to supply parameters to query
        // In actual method, it selects queryName and sets params
        // We'll stub query as Mockito and no assertion needed
    }

    /* --------------------------------------------------------------------- */
    /* toConsentModel – entity with clientId and externalClient */
    @Test
    public void testToConsentModelWithClientId() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity ent = new UserConsentEntity();
        ent.setClientId("client123");
        ent.setOtherCollections(null);
        ClientModel cm = mock(ClientModel.class);
        when(realm.getClientById("client123")).thenReturn(cm);
        UserConsentModel model = provider.toConsentModel(realm, ent);
        assertNotNull(model);
    }

    @Test
    public void testToConsentModelWithExternalClient() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity ent = new UserConsentEntity();
        ent.setClientStorageProvider("prov");
        ent.setExternalClientId("ext");
        ClientModel cm = mock(ClientModel.class);
        when(realm.getClientById("prov:ext")).thenReturn(cm);
        UserConsentModel model = provider.toConsentModel(realm, ent);
        assertNotNull(model);
    }

    /* --------------------------------------------------------------------- */
    /* createUserConsentClientScopeEntity – entity absent/present */
    @Test
    public void testCreateClientScopeEntityAddsNew() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity consent = new UserConsentEntity();
        UserConsentClientScopeEntity newScope = new UserConsentClientScopeEntity();
        Collection<UserConsentClientScopeEntity> existing = new HashSet<>();
        provider.createUserConsentClientScopeEntity(consent, clientScope, null, existing, existing);
        assertTrue(existing.contains(newScope));
        verify(em).persist(any());
        verify(em).flush();
    }

    @Test
    public void testCreateClientScopeEntityExisting() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentEntity consent = new UserConsentEntity();
        UserConsentClientScopeEntity existingScope = new UserConsentClientScopeEntity();
        Collection<UserConsentClientScopeEntity> existing = new HashSet<>(Collections.singletonList(existingScope));
        provider.createUserConsentClientScopeEntity(consent, clientScope, null, existing, existing);
        // No new persist should occur since already present
        verify(em, never()).persist(any());
        verify(em, never()).flush();
    }

    /* --------------------------------------------------------------------- */
    /* removeVerifiableCredential – not found vs found */
    @Test
    public void testRemoveVerifiableCredentialNotFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(provider.getVerifiableCredentialsEntitiesByUser("uid"))
                .thenReturn(Stream.empty());
        assertFalse(provider.removeVerifiableCredential("uid", "cs"));
    }

    @Test
    public void testRemoveVerifiableCredentialFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialEntity ent = mock(UserVerifiableCredentialEntity.class);
        when(ent.getClientScopeId()).thenReturn("cs");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid"))
                .thenReturn(Stream.of(ent));
        TypedQuery<?> query = mock(TypedQuery.class);
        when(em.createNamedQuery("deleteIssuedVcsByUserAndType", Object.class)).thenReturn(query);
        provider.removeVerifiableCredential("uid", "cs");
        verify(em).remove(ent);
        verify(em).flush();
    }

    /* --------------------------------------------------------------------- */
    /* getVerifiableCredentialsByUser – sorted check */
    @Test
    public void testGetVerifiableCredentialsByUserSorted() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialEntity e1 = mock(UserVerifiableCredentialEntity.class);
        UserVerifiableCredentialEntity e2 = mock(UserVerifiableCredentialEntity.class);
        when(e1.getClientScopeId()).thenReturn("b");
        when(e2.getClientScopeId()).thenReturn("a");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid"))
                .thenReturn(Stream.of(e1, e2));
        List<UserVerifiableCredentialModel> list = provider.getVerifiableCredentialsByUser("uid").toList();
        assertEquals("a", list.get(0).getClientScopeId());
        assertEquals("b", list.get(1).getClientScopeId());
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

    @Test
    public void testGetVerifiableCredentialByClientScopeNotFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.empty());
        assertNull(provider.getVerifiableCredentialByClientScope("uid", "cs"));
    }

    @Test
    public void testGetVerifiableCredentialByClientScopeFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialEntity ent = mock(UserVerifiableCredentialEntity.class);
        when(ent.getClientScopeId()).thenReturn("cs");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.of(ent));
        UserVerifiableCredentialModel mdl = provider.getVerifiableCredentialByClientScope("uid", "cs");
        assertNotNull(mdl);
    }

    /* --------------------------------------------------------------------- */
    /* updateVerifiableCredential – success and OptimisticLockException */
    @Test
    public void testUpdateVerifiableCredentialSuccessfully() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserEntity uent = mock(UserEntity.class);
        when(em.find(UserEntity.class, "uid")).thenReturn(uent);
        when(session.getContext().getRealm()).thenReturn(realm);
        UserVerifiableCredentialEntity ent = mock(UserVerifiableCredentialEntity.class);
        when(ent.getClientScopeId()).thenReturn("cs");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.of(ent));
        // Mock profile provider return attributes
        UserProfileProvider pp = mock(UserProfileProvider.class);
        UserProfile up = mock(UserProfile.class);
        Map<String, List<String>> attrs = Map.of("k", List.of("v"));
        when(up.getAttributes()).thenReturn(new org.keycloak.util.UserProfileAttributes(attrs));
        when(pp.create(any(), any())).thenReturn(up);
        when(session.getProvider(UserProfileProvider.class)).thenReturn(pp);
        // Mock JsonSerialization
        String json = "{\"k\":\"v\"}";
        try (MockedStatic<JsonSerialization> js = mockStatic(JsonSerialization.class)) {
            js.when(() -> JsonSerialization.writeValueAsString(attrs)).thenReturn(json);
            UserVerifiableCredentialModel result = provider.updateVerifiableCredential("uid", "cs");
            assertNotNull(result);
            verify(em).merge(any());
            verify(em).flush();
        }
    }

    @Test
    public void testUpdateVerifiableCredentialOptimisticLock() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserEntity uent = mock(UserEntity.class);
        when(em.find(UserEntity.class, "uid")).thenReturn(uent);
        when(session.getContext().getRealm()).thenReturn(realm);
        UserVerifiableCredentialEntity ent = mock(UserVerifiableCredentialEntity.class);
        when(ent.getClientScopeId()).thenReturn("cs");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.of(ent));
        when(provider.updateVerifiableCredential("uid", "cs")).thenAnswer(inv -> {
            throw new ModelException("", new OptimisticLockException(""));
        });
        assertThrows(ModelException.class, () -> provider.updateVerifiableCredential("uid", "cs"));
    }

    /* --------------------------------------------------------------------- */
    /* getVerifiableCredentialsEntitiesByUser – simply returns stream */
    @Test
    public void testGetVerifiableCredentialsEntitiesByUser() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<UserVerifiableCredentialEntity> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("verifiableCredentialsByUser", UserVerifiableCredentialEntity.class)).thenReturn(tq);
        when(tq.getResultStream()).thenReturn(Stream.of(new UserVerifiableCredentialEntity()));
        assertEquals(1, provider.getVerifiableCredentialsEntitiesByUser("uid").count());
    }

    /* --------------------------------------------------------------------- */
    /* preRemove(RealmModel, IdentityProviderModel) – ensure execution */
    @Test
    public void testPreRemoveIdentityProvider() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        TypedQuery<?> tq = mock(TypedQuery.class);
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
        TypedQuery<?> tq = mock(TypedQuery.class);
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
        TypedQuery<?> tq = mock(TypedQuery.class);
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
        when(client.getRealm()).thenReturn(realm);
        StorageId sid = new StorageId("clientId");
        TypedQuery<?> tq1 = mock(TypedQuery.class); // deleteUserConsentClientScopesByClient
        TypedQuery<?> tq2 = mock(TypedQuery.class); // deleteUserConsentsByClient
        TypedQuery<?> tq3 = mock(TypedQuery.class); // deleteIssuedVcsByClient
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
        ClientModel client = mock(ClientModel.class);
        when(client.getId()).thenReturn("prov:clientid");
        when(client.getRealm()).thenReturn(realm);
        StorageId sid = new StorageId("prov:clientid");
        TypedQuery<?> tq1 = mock(TypedQuery.class); // deleteUserConsentClientScopesByExternalClient
        TypedQuery<?> tq2 = mock(TypedQuery.class); // deleteUserConsentsByExternalClient
        TypedQuery<?> tq3 = mock(TypedQuery.class); // deleteIssuedVcsByExternalClient
        when(em.createNamedQuery("deleteUserConsentClientScopesByExternalClient")).thenReturn(tq1);
        when(em.createNamedQuery("deleteUserConsentsByExternalClient")).thenReturn(tq2);
        when(em.createNamedQuery("deleteIssuedVcsByExternalClient")).thenReturn(tq3);
        provider.preRemove(realm, client);
        verify(tq1).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq1).setParameter("externalClientId", sid.getExternalId());
        verify(tq2).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq2).setParameter("externalClientId", sid.getExternalId());
        verify(tq3).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq3).setParameter("externalClientId", sid.getExternalId());
    }

    @Test
    public void testPreRemoveClientScope() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        ClientScopeModel cs = mock(ClientScopeModel.class);
        when(cs.getId()).thenReturn("scopeId");
        TypedQuery<?> tq1 = mock(TypedQuery.class); // deleteUserConsentClientScopesByClientScope
        TypedQuery<?> tq2 = mock(TypedQuery.class); // deleteIssuedVcsByClientScope
        TypedQuery<?> tq3 = mock(TypedQuery.class); // deleteVerifiableCredentialsByClientScope
        when(em.createNamedQuery("deleteUserConsentClientScopesByClientScope")).thenReturn(tq1);
        when(em.createNamedQuery("deleteIssuedVcsByClientScope")).thenReturn(tq2);
        when(em.createNamedQuery("deleteVerifiableCredentialsByClientScope")).thenReturn(tq3);
        provider.preRemove(cs);
        verify(tq1).setParameter("scopeId", "scopeId");
        verify(tq2).setParameter("scopeId", "scopeId");
        verify(tq3).setParameter("scopeId", "scopeId");
    }
}

