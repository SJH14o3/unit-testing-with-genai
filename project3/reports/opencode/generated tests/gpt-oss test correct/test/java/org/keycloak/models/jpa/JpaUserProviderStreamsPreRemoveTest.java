package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.client.ClientStorageProvider;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class JpaUserProviderStreamsPreRemoveTest {
    @Mock private KeycloakSession session;
    @Mock private EntityManager em;
    @Mock private RealmModel realm;
    @Mock private IdentityProviderModel ipm;
    @Mock private RoleModel role;
    @Mock private ClientModel client;
    @Mock private ClientScopeModel clientScope;
    @Mock private GroupModel group;
    @Mock private ComponentModel component;

    private static Query queryMock() {
        return mock(Query.class, Answers.RETURNS_SELF);
    }

    @Test
    public void testPreRemove_identityProvider() {
        var provider = new JpaUserProvider(session, em);
        Query tq = queryMock();
        when(em.createNamedQuery("deleteFederatedIdentityByProvider")).thenReturn(tq);
        provider.preRemove(realm, ipm);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("providerAlias", ipm.getAlias());
        verify(tq).executeUpdate();
    }

    @Test
    public void testPreRemove_role() {
        var provider = new JpaUserProvider(session, em);
        Query tq = queryMock();
        when(em.createNamedQuery("deleteUserRoleMappingsByRole")).thenReturn(tq);
        provider.preRemove(realm, role);
        verify(tq).setParameter("roleId", role.getId());
        verify(tq).executeUpdate();
    }

    @Test
    public void testPreRemove_clientLocal() {
        when(client.getId()).thenReturn("local-client");
        when(client.getRealm()).thenReturn(realm);
        var provider = new JpaUserProvider(session, em);
        Query tq1 = queryMock(); // deleteUserConsentClientScopesByClient
        Query tq2 = queryMock(); // deleteUserConsentsByClient
        Query tq3 = queryMock(); // deleteIssuedVcsByClient
        when(em.createNamedQuery("deleteUserConsentClientScopesByClient")).thenReturn(tq1);
        when(em.createNamedQuery("deleteUserConsentsByClient")).thenReturn(tq2);
        when(em.createNamedQuery("deleteIssuedVcsByClient")).thenReturn(tq3);
        provider.preRemove(realm, client);
        verify(tq1).setParameter("clientId", client.getId());
        verify(tq1).executeUpdate();
        verify(tq2).setParameter("clientId", client.getId());
        verify(tq2).executeUpdate();
        verify(tq3).setParameter("clientId", client.getId());
        verify(tq3).executeUpdate();
    }

    @Test
    public void testPreRemove_clientExternal() {
        when(client.getId()).thenReturn("f:provider:clientid");
        when(client.getRealm()).thenReturn(realm);
        var provider = new JpaUserProvider(session, em);
        StorageId sid = new StorageId("f:provider:clientid"); // external client
        Query tq1 = queryMock(); // deleteUserConsentClientScopesByExternalClient
        Query tq2 = queryMock(); // deleteUserConsentsByExternalClient
        when(em.createNamedQuery("deleteUserConsentClientScopesByExternalClient")).thenReturn(tq1);
        when(em.createNamedQuery("deleteUserConsentsByExternalClient")).thenReturn(tq2);
        provider.preRemove(realm, client);
        verify(tq1).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq1).setParameter("externalClientId", sid.getExternalId());
        verify(tq1).executeUpdate();
        verify(tq2).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq2).setParameter("externalClientId", sid.getExternalId());
        verify(tq2).executeUpdate();
    }

    @Test
    public void testPreRemove_clientScope() {
        var provider = new JpaUserProvider(session, em);
        Query tq1 = queryMock();
        Query tq2 = queryMock();
        Query tq3 = queryMock();
        when(em.createNamedQuery("deleteUserConsentClientScopesByClientScope")).thenReturn(tq1);
        when(em.createNamedQuery("deleteIssuedVcsByClientScope")).thenReturn(tq2);
        when(em.createNamedQuery("deleteVerifiableCredentialsByClientScope")).thenReturn(tq3);
        provider.preRemove(clientScope);
        verify(tq1).setParameter("scopeId", clientScope.getId());
        verify(tq1).executeUpdate();
        verify(tq2).setParameter("scopeId", clientScope.getId());
        verify(tq2).executeUpdate();
        verify(tq3).setParameter("scopeId", clientScope.getId());
        verify(tq3).executeUpdate();
    }

    @Test
    public void testPreRemove_componentTypeUserStorage() {
        when(component.getProviderType()).thenReturn(UserStorageProvider.class.getName());
        when(component.getId()).thenReturn("storageId");
        when(realm.getId()).thenReturn("realmId");
        var provider = new JpaUserProvider(session, em);
        // removeImportedUsers issues several named queries, all of them take the realm id and the storage link
        Query tq = queryMock();
        when(em.createNamedQuery(anyString())).thenReturn(tq);
        provider.preRemove(realm, component);
        verify(tq, atLeastOnce()).setParameter("realmId", realm.getId());
        verify(tq, atLeastOnce()).setParameter("link", component.getId());
        verify(tq, atLeastOnce()).executeUpdate();
    }

    @Test
    public void testPreRemove_componentTypeClientStorage() {
        when(component.getProviderType()).thenReturn(ClientStorageProvider.class.getName());
        when(component.getId()).thenReturn("providerId");
        when(realm.getId()).thenReturn("realmId");
        var provider = new JpaUserProvider(session, em);
        // removeConsentByClientStorageProvider only filters by the client storage provider id
        Query tq = queryMock();
        when(em.createNamedQuery(anyString())).thenReturn(tq);
        provider.preRemove(realm, component);
        verify(tq, atLeastOnce()).setParameter("clientStorageProvider", component.getId());
        verify(tq, atLeastOnce()).executeUpdate();
    }
}

