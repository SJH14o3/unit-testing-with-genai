package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.client.clienttype.ClientType;
import org.keycloak.client.clienttype.ClientTypeManager;
import org.keycloak.client.clienttype.ClientTypeException;
import org.keycloak.common.Profile;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.PolicyEnforcementMode;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class RepresentationToModelClientTest {

    @Test
    void testCreateClientBasic() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("test-client");

        ClientModel client = mock(ClientModel.class);
        when(realm.addClient("test-client")).thenReturn(client);
        when(client.getRealm()).thenReturn(realm);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);

            ClientModel result = RepresentationToModel.createClient(session, realm, rep);

            assertNotNull(result);
            verify(realm).addClient("test-client");
        }
    }

    @Test
    void testCreateClientWithId() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setId("client-id");
        rep.setClientId("test-client");

        ClientModel client = mock(ClientModel.class);
        when(realm.addClient("client-id", "test-client")).thenReturn(client);
        when(client.getRealm()).thenReturn(realm);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);

            ClientModel result = RepresentationToModel.createClient(session, realm, rep);

            assertNotNull(result);
            verify(realm).addClient("client-id", "test-client");
        }
    }

    @Test
    void testCreateClientWithClientType() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("test-client");
        rep.setType("oidc");

        ClientModel client = mock(ClientModel.class);
        when(realm.addClient("test-client")).thenReturn(client);
        when(client.getRealm()).thenReturn(realm);

        ClientTypeManager typeManager = mock(ClientTypeManager.class);
        ClientType clientType = mock(ClientType.class);
        when(session.getProvider(ClientTypeManager.class)).thenReturn(typeManager);
        when(typeManager.getClientType(realm, "oidc")).thenReturn(clientType);
        when(clientType.augment(client)).thenReturn(client);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(true);

            ClientModel result = RepresentationToModel.createClient(session, realm, rep);

            assertNotNull(result);
            verify(clientType).augment(client);
        }
    }

    @Test
    void testUpdateClientPropertyChange() {
        KeycloakSession session = mock(KeycloakSession.class);
        when(session.getKeycloakSessionFactory()).thenReturn(mock(KeycloakSessionFactory.class));
        RealmModel realm = mock(RealmModel.class);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("new-id");
        rep.setName("New Name");

        ClientModel client = mock(ClientModel.class);
        when(client.getClientId()).thenReturn("old-id");
        when(client.getRealm()).thenReturn(realm);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);

            RepresentationToModel.updateClient(rep, client, session);

            verify(client).setClientId("new-id");
            verify(client).setName("New Name");
        }
    }

    @Test
    void testUpdateClientProtocolMappers() {
        ClientRepresentation rep = new ClientRepresentation();
        ProtocolMapperRepresentation mapperRep = new ProtocolMapperRepresentation();
        mapperRep.setName("mapper1");
        mapperRep.setProtocol("oidc");
        rep.setProtocolMappers(List.of(mapperRep));

        ClientModel client = mock(ClientModel.class);
        ProtocolMapperModel mapperModel = new ProtocolMapperModel();
        mapperModel.setName("mapper1");
        mapperModel.setProtocol("oidc");
        
        // wait, the code uses client.getProtocolMappersStream().collect(Collectors.toList()).forEach(...)
        // I need to mock the stream return
        
        List<ProtocolMapperModel> existingMappers = new ArrayList<>();
        when(client.getProtocolMappersStream()).thenReturn(existingMappers.stream());

        RepresentationToModel.updateClientProtocolMappers(rep, client);

        verify(client).addProtocolMapper(any(ProtocolMapperModel.class));
    }

    @Test
    void testUpdateClientScopes() {
        ClientRepresentation rep = new ClientRepresentation();
        rep.setDefaultClientScopes(List.of("scope-default"));
        rep.setOptionalClientScopes(List.of("scope-optional"));

        ClientModel client = mock(ClientModel.class);
        RealmModel realm = mock(RealmModel.class);
        when(client.getRealm()).thenReturn(realm);
        when(client.getClientScopes(anyBoolean())).thenReturn(new HashMap<>());

        ClientScopeModel defaultScope = mock(ClientScopeModel.class);
        ClientScopeModel optionalScope = mock(ClientScopeModel.class);

        try (MockedStatic<KeycloakModelUtils> utils = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utils.when(() -> KeycloakModelUtils.getClientScopeByName(eq(realm), eq("scope-default"))).thenReturn(defaultScope);
            utils.when(() -> KeycloakModelUtils.getClientScopeByName(eq(realm), eq("scope-optional"))).thenReturn(optionalScope);

            RepresentationToModel.updateClientScopes(rep, client);

            verify(client).addClientScope(defaultScope, true);
            verify(client).addClientScope(optionalScope, false);
        }
    }

    @Test
    void testUpdateComponentInternal() {
        KeycloakSession session = mock(KeycloakSession.class);
        ComponentRepresentation rep = new ComponentRepresentation();
        rep.setName("new-name");
        MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
        params.put("key", List.of("new-val"));
        rep.setConfig(params);

        ComponentModel component = new ComponentModel();
        component.setName("old-name");
        component.setConfig(new MultivaluedHashMap<>());

        // For internal=true, providerConfiguration is not fetched
        RepresentationToModel.updateComponent(session, rep, component, true);

        assertEquals("new-name", component.getName());
        assertEquals(List.of("new-val"), component.getConfig().get("key"));
    }

    @Test
    void testCreateClientScope() {
        RealmModel realm = mock(RealmModel.class);
        ClientScopeRepresentation rep = new ClientScopeRepresentation();
        rep.setName("scope-name");
        rep.setProtocol("oidc");

        ClientScopeModel scope = mock(ClientScopeModel.class);
        when(realm.addClientScope("scope-name")).thenReturn(scope);

        ClientScopeModel result = RepresentationToModel.createClientScope(realm, rep);

        assertEquals(scope, result);
        verify(scope).setName("scope-name");
        verify(scope).setProtocol("oidc");
    }

    @Test
    void testUpdateClientScope() {
        ClientScopeRepresentation rep = new ClientScopeRepresentation();
        rep.setName("new-scope-name");
        rep.setProtocol("oidc");

        ClientScopeModel scope = mock(ClientScopeModel.class);

        RepresentationToModel.updateClientScope(rep, scope);

        verify(scope).setName("new-scope-name");
        verify(scope).setProtocol("oidc");
    }
}
