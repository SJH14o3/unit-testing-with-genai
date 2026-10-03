package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void testPreRemove_identityProvider() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<?> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("deleteFederatedIdentityByProvider")).thenReturn(tq);
        provider.preRemove(realm, ipm);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("providerAlias", ipm.getAlias());
        verify(tq).executeUpdate();
    }

    @Test
    public void testPreRemove_role() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<?> tq = mock(TypedQuery.class);
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
        TypedQuery<?> tq1 = mock(TypedQuery.class); // userConsentClientScopesByClient
        TypedQuery<?> tq2 = mock(TypedQuery.class); // userConsentsByClient
        TypedQuery<?> tq3 = mock(TypedQuery.class); // issuedVcsByClient
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
        when(client.getId()).thenReturn("provider:clientid");
        when(client.getRealm()).thenReturn(realm);
        var provider = new JpaUserProvider(session, em);
        StorageId sid = new StorageId("provider:clientid"); // external client
        TypedQuery<?> tq1 = mock(TypedQuery.class); // deleteUserConsentClientScopesByExternalClient
        TypedQuery<?> tq2 = mock(TypedQuery.class); // deleteUserConsentsByExternalClient
        TypedQuery<?> tq3 = mock(TypedQuery.class); // deleteIssuedVcsByExternalClient
        when(em.createNamedQuery("deleteUserConsentClientScopesByExternalClient")).thenReturn(tq1);
        when(em.createNamedQuery("deleteUserConsentsByExternalClient")).thenReturn(tq2);
        when(em.createNamedQuery("deleteIssuedVcsByExternalClient")).thenReturn(tq3);
        provider.preRemove(realm, client);
        verify(tq1).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq1).setParameter("externalClientId", sid.getExternalId());
        verify(tq1).executeUpdate();
        verify(tq2).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq2).setParameter("externalClientId", sid.getExternalId());
        verify(tq2).executeUpdate();
        verify(tq3).setParameter("clientStorageProvider", sid.getProviderId());
        verify(tq3).setParameter("externalClientId", sid.getExternalId());
        verify(tq3).executeUpdate();
    }

    @Test
    public void testPreRemove_clientScope() {
        var provider = new JpaUserProvider(session, em);
        TypedQuery<?> tq1 = mock(TypedQuery.class);
        TypedQuery<?> tq2 = mock(TypedQuery.class);
        TypedQuery<?> tq3 = mock(TypedQuery.class);
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
        TypedQuery<?> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("deleteUserRoleMappingsByRealmAndLink")).thenReturn(tq);
        provider.preRemove(realm, component);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("link", component.getId());
        verify(tq).executeUpdate();
    }

    @Test
    public void testPreRemove_componentTypeClientStorage() {
        when(component.getProviderType()).thenReturn(ClientStorageProvider.class.getName());
        when(component.getId()).thenReturn("providerId");
        when(realm.getId()).thenReturn("realmId");
        var provider = new JpaUserProvider(session, em);
        TypedQuery<?> tq = mock(TypedQuery.class);
        when(em.createNamedQuery("deleteUserConsentClientScopesByClientStorageProvider")).thenReturn(tq);
        provider.preRemove(realm, component);
        verify(tq).setParameter("realmId", realm.getId());
        verify(tq).setParameter("clientStorageProvider", component.getId());
        verify(tq).executeUpdate();
    }
}

