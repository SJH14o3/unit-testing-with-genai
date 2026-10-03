package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.AuthorizationProviderFactory;
import org.keycloak.authorization.model.*;
import org.keycloak.authorization.policy.provider.PolicyProviderFactory;
import org.keycloak.authorization.store.PermissionTicketStore;
import org.keycloak.authorization.store.PolicyStore;
import org.keycloak.authorization.store.ResourceServerStore;
import org.keycloak.authorization.store.ResourceStore;
import org.keycloak.authorization.store.ScopeStore;
import org.keycloak.authorization.store.StoreFactory;
import org.keycloak.client.clienttype.ClientType;
import org.keycloak.common.util.Profile;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.keycloak.representations.idm.oid4vc.UserVerifiableCredentialRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepresentationToModelAuthzTest {

    @Test
    void testImportAuthorizationSettings() {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setAuthorizationServicesEnabled(true);

        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        AuthorizationProviderFactory authFactory = mock(AuthorizationProviderFactory.class);
        when(sessionFactory.getProviderFactory(org.keycloak.authorization.AuthorizationProvider.class)).thenReturn(authFactory);
        AuthorizationProvider authProvider = mock(AuthorizationProvider.class);
        when(authFactory.create(eq(session), eq(realm))).thenReturn(authProvider);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.AUTHORIZATION)).thenReturn(true);

            RepresentationToModel.importAuthorizationSettings(clientRep, client, session);

            verify(client).setServiceAccountsEnabled(true);
            verify(client).setBearerOnly(false);
            verify(client).setPublicClient(false);
        }
    }

    @Test
    void testToModelResourceServer() {
        AuthorizationProvider auth = mock(AuthorizationProvider.class);
        StoreFactory storeFactory = mock(StoreFactory.class);
        ResourceServerStore resourceServerStore = mock(ResourceServerStore.class);
        when(auth.getStoreFactory()).thenReturn(storeFactory);
        when(storeFactory.getResourceServerStore()).thenReturn(resourceServerStore);

        ClientModel client = mock(ClientModel.class);
        when(client.getId()).thenReturn("client-id");

        ResourceServerRepresentation rep = new ResourceServerRepresentation();
        rep.setPolicyEnforcementMode(PolicyEnforcementMode.ENFORCING);
        rep.setAllowRemoteResourceManagement(true);
        rep.setDecisionStrategy(DecisionStrategy.UNANIMOUS);

        ResourceServer resourceServer = mock(ResourceServer.class);
        when(resourceServerStore.findByClient(client)).thenReturn(null);
        when(resourceServerStore.create(eq(client))).thenReturn(resourceServer);

        ResourceServer result = RepresentationToModel.toModel(rep, auth, client);

        assertNotNull(result);
        verify(resourceServer).setPolicyEnforcementMode(PolicyEnforcementMode.ENFORCING);
        verify(resourceServer).setAllowRemoteResourceManagement(true);
        verify(resourceServer).setDecisionStrategy(DecisionStrategy.UNANIMOUS);
    }

    @Test
    void testToModelPolicy() {
        AuthorizationProvider auth = mock(AuthorizationProvider.class);
        StoreFactory storeFactory = mock(StoreFactory.class);
        PolicyStore policyStore = mock(PolicyStore.class);
        when(auth.getStoreFactory()).thenReturn(storeFactory);
        when(storeFactory.getPolicyStore()).thenReturn(policyStore);

        Policy model = mock(Policy.class);
        PolicyRepresentation rep = new PolicyRepresentation();
        rep.setName("policy-name");
        rep.setDescription("policy-desc");
        rep.setDecisionStrategy(DecisionStrategy.PERMISSIVE);
        rep.setLogic("POSITIVE");
        rep.setResourceType("resource");

        // Mocking the provider factory to avoid NPE in provider.onImport
        PolicyProviderFactory providerFactory = mock(PolicyProviderFactory.class);
        when(auth.getProviderFactory(anyString())).thenReturn(providerFactory);

        when(policyStore.findById(any(), anyString())).thenReturn(model);
        when(policyStore.findByName(any(), anyString())).thenReturn(model);

        Policy result = RepresentationToModel.toModel(rep, auth, model);

        assertNotNull(result);
        verify(model).setName("policy-name");
        verify(model).setDecisionStrategy(DecisionStrategy.PERMISSIVE);
        verify(providerFactory).onImport(eq(model), eq(rep), eq(auth));
    }

    @Test
    void testToModelResource() {
        AuthorizationProvider auth = mock(AuthorizationProvider.class);
        StoreFactory storeFactory = mock(StoreFactory.class);
        ResourceStore resourceStore = mock(ResourceStore.class);
        when(auth.getStoreFactory()).thenReturn(storeFactory);
        when(storeFactory.getResourceStore()).thenReturn(resourceStore);

        ResourceServer resourceServer = mock(ResourceServer.class);
        when(resourceServer.getClientId()).thenReturn("client-id");
        RealmModel realm = mock(RealmModel.class);
        when(auth.getRealm()).thenReturn(realm);

        ResourceRepresentation rep = new ResourceRepresentation();
        rep.setName("resource-name");
        rep.setType("urn:my-resource");
        rep.setOwner(new ResourceOwnerRepresentation());
        rep.getOwner().setId("owner-id");

        Resource resource = mock(Resource.class);
        when(resourceStore.findById(eq(resourceServer), anyString())).thenReturn(null);
        when(resourceStore.findByName(eq(resourceServer), eq("resource-name"), eq("owner-id"))).thenReturn(resource);

        Resource result = RepresentationToModel.toModel(rep, resourceServer, auth);

        assertNotNull(result);
        verify(resource).setName("resource-name");
        verify(resource).setType("urn:my-resource");
    }

    @Test
    void testToModelScope() {
        AuthorizationProvider auth = mock(AuthorizationProvider.class);
        StoreFactory storeFactory = mock(StoreFactory.class);
        ScopeStore scopeStore = mock(ScopeStore.class);
        when(auth.getStoreFactory()).thenReturn(storeFactory);
        when(storeFactory.getScopeStore()).thenReturn(scopeStore);

        ResourceServer resourceServer = mock(ResourceServer.class);
        ScopeRepresentation rep = new ScopeRepresentation();
        rep.setName("scope-name");

        Scope scope = mock(Scope.class);
        when(scopeStore.findByName(resourceServer, "scope-name")).thenReturn(scope);

        Scope result = RepresentationToModel.toModel(rep, resourceServer, auth, true);

        assertNotNull(result);
        verify(scope).setName("scope-name");
    }

    @Test
    void testToModelPermissionTicket() {
        AuthorizationProvider auth = mock(AuthorizationProvider.class);
        StoreFactory storeFactory = mock(StoreFactory.class);
        PermissionTicketStore ticketStore = mock(PermissionTicketStore.class);
        when(auth.getStoreFactory()).thenReturn(storeFactory);
        when(storeFactory.getPermissionTicketStore()).thenReturn(ticketStore);

        ResourceServer resourceServer = mock(ResourceServer.class);
        PermissionTicketRepresentation rep = new PermissionTicketRepresentation();
        rep.setId("ticket-id");
        rep.setGranted(true);

        PermissionTicket ticket = mock(PermissionTicket.class);
        when(ticketStore.findById(resourceServer, "ticket-id")).thenReturn(ticket);
        when(ticket.isGranted()).thenReturn(false);

        PermissionTicket result = RepresentationToModel.toModel(rep, resourceServer, auth);

        assertNotNull(result);
        verify(ticket).setGrantedTimestamp(anyLong());
    }

    @Test
    void testCreateResourceServer() {
        KeycloakSession session = mock(KeycloakSession.class);
        ClientModel client = mock(ClientModel.class);
        when(client.isBearerOnly()).thenReturn(false);
        when(client.isPublicClient()).thenReturn(false);
        when(client.getClientId()).thenReturn("my-client");

        AuthorizationProvider authProvider = mock(AuthorizationProvider.class);
        when(session.getProvider(AuthorizationProvider.class)).thenReturn(authProvider);

        ResourceServerRepresentation rep = new ResourceServerRepresentation();
        rep.setClientId("my-client");

        ResourceServer resourceServer = mock(ResourceServer.class);
        // toModel in createResourceServer will be called. 
        // We need to mock the parts of toModel.
        
        // However, for a simple check of the first part:
        assertThrows(RuntimeException.class, () -> {
            // Try to create for a public client
            when(client.isPublicClient()).thenReturn(true);
            RepresentationToModel.createResourceServer(client, session, true);
        });
    }
}
