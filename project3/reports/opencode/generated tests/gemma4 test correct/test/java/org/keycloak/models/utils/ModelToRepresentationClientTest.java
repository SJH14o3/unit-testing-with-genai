package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.common.Profile;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.store.StoreFactory;
import org.keycloak.authorization.store.ResourceServerStore;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModelToRepresentationClientTest {

    @BeforeEach
    void initializeProfile() {
        Profile.init(Profile.ProfileName.DEFAULT, Map.of(Profile.Feature.AUTHORIZATION, false));
    }

    @Mock
    private ClientScopeModel clientScopeModel;

    @Mock
    private ClientModel clientModel;

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private AuthorizationProvider authorizationProvider;

    @Mock
    private StoreFactory storeFactory;

    @Mock
    private ResourceServerStore resourceServerStore;

    @Mock
    private ResourceServer resourceServer;

    @Test
    void testToRepresentationClientScope() {
        when(clientScopeModel.getId()).thenReturn("scope-id");
        when(clientScopeModel.getName()).thenReturn("scope-name");
        when(clientScopeModel.getDescription()).thenReturn("scope-desc");
        when(clientScopeModel.getProtocol()).thenReturn("openid-connect");
        when(clientScopeModel.getProtocolMappersStream()).thenReturn(Stream.of(mock(org.keycloak.models.ProtocolMapperModel.class)));
        when(clientScopeModel.getAttributes()).thenReturn(Map.of("attr", "val"));

        ClientScopeRepresentation rep = ModelToRepresentation.toRepresentation(clientScopeModel);

        assertNotNull(rep);
        assertEquals("scope-id", rep.getId());
        assertEquals("scope-name", rep.getName());
        assertEquals("scope-desc", rep.getDescription());
        assertEquals("openid-connect", rep.getProtocol());
        assertEquals(1, rep.getProtocolMappers().size());
        assertEquals("val", rep.getAttributes().get("attr"));
    }

    @Test
    void testToRepresentationClientBasic() {
        when(clientModel.getId()).thenReturn("client-id");
        when(clientModel.getClientId()).thenReturn("client-id-str");
        when(clientModel.getName()).thenReturn("client-name");
        when(clientModel.getDescription()).thenReturn("client-desc");
        when(clientModel.getType()).thenReturn("oidc");
        when(clientModel.isEnabled()).thenReturn(true);
        when(clientModel.isAlwaysDisplayInConsole()).thenReturn(false);
        when(clientModel.getManagementUrl()).thenReturn("mgmt-url");
        when(clientModel.isPublicClient()).thenReturn(true);
        when(clientModel.isFrontchannelLogout()).thenReturn(false);
        when(clientModel.getProtocol()).thenReturn("openid-connect");
        when(clientModel.getAttributes()).thenReturn(new HashMap<>());
        when(clientModel.getAuthenticationFlowBindingOverrides()).thenReturn(Collections.emptyMap());
        when(clientModel.isFullScopeAllowed()).thenReturn(true);
        when(clientModel.isBearerOnly()).thenReturn(false);
        when(clientModel.isConsentRequired()).thenReturn(false);
        when(clientModel.isStandardFlowEnabled()).thenReturn(true);
        when(clientModel.isImplicitFlowEnabled()).thenReturn(false);
        when(clientModel.isDirectAccessGrantsEnabled()).thenReturn(true);
        when(clientModel.isServiceAccountsEnabled()).thenReturn(false);
        when(clientModel.isSurrogateAuthRequired()).thenReturn(false);
        when(clientModel.getRootUrl()).thenReturn("root-url");
        when(clientModel.getBaseUrl()).thenReturn("base-url");
        when(clientModel.getNotBefore()).thenReturn(12345);
        when(clientModel.getNodeReRegistrationTimeout()).thenReturn(300);
        when(clientModel.getClientAuthenticatorType()).thenReturn("type");
        when(clientModel.getRealm()).thenReturn(realm);
        when(clientModel.getClientScopes(anyBoolean())).thenReturn(new HashMap<>());
        when(clientModel.getRedirectUris()).thenReturn(Set.of("redir-uri"));
        when(clientModel.getWebOrigins()).thenReturn(Set.of("web-origin"));
        when(clientModel.getRegisteredNodes()).thenReturn(Collections.emptyMap());
        when(clientModel.getProtocolMappersStream()).thenReturn(Stream.empty());

        ClientRepresentation rep = ModelToRepresentation.toRepresentation(clientModel, session);

        assertNotNull(rep);
        assertEquals("client-id", rep.getId());
        assertEquals("client-id-str", rep.getClientId());
        assertEquals("client-name", rep.getName());
        assertEquals("client-desc", rep.getDescription());
        assertEquals("oidc", rep.getType());
        assertTrue(rep.isEnabled());
        assertNull(rep.getSecret()); // Public client
        assertTrue(rep.getRedirectUris().contains("redir-uri"));
        assertTrue(rep.getWebOrigins().contains("web-origin"));
    }

    @Test
    void testToRepresentationClientNonPublic() {
        when(clientModel.getId()).thenReturn("client-id");
        when(clientModel.getClientId()).thenReturn("client-id-str");
        when(clientModel.getName()).thenReturn("client-name");
        when(clientModel.getDescription()).thenReturn("client-desc");
        when(clientModel.getType()).thenReturn("oidc");
        when(clientModel.isEnabled()).thenReturn(true);
        when(clientModel.isAlwaysDisplayInConsole()).thenReturn(false);
        when(clientModel.getManagementUrl()).thenReturn("mgmt-url");
        when(clientModel.isPublicClient()).thenReturn(false);
        when(clientModel.isFrontchannelLogout()).thenReturn(false);
        when(clientModel.getProtocol()).thenReturn("openid-connect");
        when(clientModel.getAttributes()).thenReturn(new HashMap<>());
        when(clientModel.getAuthenticationFlowBindingOverrides()).thenReturn(Collections.emptyMap());
        when(clientModel.isFullScopeAllowed()).thenReturn(true);
        when(clientModel.isBearerOnly()).thenReturn(false);
        when(clientModel.isConsentRequired()).thenReturn(false);
        when(clientModel.isStandardFlowEnabled()).thenReturn(true);
        when(clientModel.isImplicitFlowEnabled()).thenReturn(false);
        when(clientModel.isDirectAccessGrantsEnabled()).thenReturn(true);
        when(clientModel.isServiceAccountsEnabled()).thenReturn(false);
        when(clientModel.isSurrogateAuthRequired()).thenReturn(false);
        when(clientModel.getRootUrl()).thenReturn("root-url");
        when(clientModel.getBaseUrl()).thenReturn("base-url");
        when(clientModel.getNotBefore()).thenReturn(12345);
        when(clientModel.getNodeReRegistrationTimeout()).thenReturn(300);
        when(clientModel.getClientAuthenticatorType()).thenReturn("type");
        when(clientModel.getRealm()).thenReturn(realm);
        when(clientModel.getClientScopes(anyBoolean())).thenReturn(new HashMap<>());
        when(clientModel.getRedirectUris()).thenReturn(Collections.emptySet());
        when(clientModel.getWebOrigins()).thenReturn(Collections.emptySet());
        when(clientModel.getRegisteredNodes()).thenReturn(Collections.emptyMap());
        when(clientModel.getProtocolMappersStream()).thenReturn(Stream.empty());
        when(clientModel.getSecret()).thenReturn("secret-value");

        ClientRepresentation rep = ModelToRepresentation.toRepresentation(clientModel, session);

        assertEquals("secret-value", rep.getSecret());
    }

    @Test
    void testToRepresentationWithAuthorization() {
        when(clientModel.getId()).thenReturn("client-id");
        when(clientModel.getClientId()).thenReturn("client-id-str");
        when(clientModel.getName()).thenReturn("client-name");
        when(clientModel.getDescription()).thenReturn("client-desc");
        when(clientModel.getType()).thenReturn("oidc");
        when(clientModel.isEnabled()).thenReturn(true);
        when(clientModel.isAlwaysDisplayInConsole()).thenReturn(false);
        when(clientModel.getManagementUrl()).thenReturn("mgmt-url");
        when(clientModel.isPublicClient()).thenReturn(false);
        when(clientModel.isFrontchannelLogout()).thenReturn(false);
        when(clientModel.getProtocol()).thenReturn("openid-connect");
        when(clientModel.getAttributes()).thenReturn(new HashMap<>());
        when(clientModel.getAuthenticationFlowBindingOverrides()).thenReturn(Collections.emptyMap());
        when(clientModel.isFullScopeAllowed()).thenReturn(true);
        when(clientModel.isBearerOnly()).thenReturn(false);
        when(clientModel.isConsentRequired()).thenReturn(false);
        when(clientModel.isStandardFlowEnabled()).thenReturn(true);
        when(clientModel.isImplicitFlowEnabled()).thenReturn(false);
        when(clientModel.isDirectAccessGrantsEnabled()).thenReturn(true);
        when(clientModel.isServiceAccountsEnabled()).thenReturn(false);
        when(clientModel.isSurrogateAuthRequired()).thenReturn(false);
        when(clientModel.getRootUrl()).thenReturn("root-url");
        when(clientModel.getBaseUrl()).thenReturn("base-url");
        when(clientModel.getNotBefore()).thenReturn(12345);
        when(clientModel.getNodeReRegistrationTimeout()).thenReturn(300);
        when(clientModel.getClientAuthenticatorType()).thenReturn("type");
        when(clientModel.getRealm()).thenReturn(realm);
        when(clientModel.getClientScopes(anyBoolean())).thenReturn(new HashMap<>());
        when(clientModel.getRedirectUris()).thenReturn(Collections.emptySet());
        when(clientModel.getWebOrigins()).thenReturn(Collections.emptySet());
        when(clientModel.getRegisteredNodes()).thenReturn(Collections.emptyMap());
        when(clientModel.getProtocolMappersStream()).thenReturn(Stream.empty());
        when(clientModel.getSecret()).thenReturn("secret-value");

        try (MockedStatic<Profile> mockedProfile = mockStatic(Profile.class)) {
            mockedProfile.when(() -> Profile.isFeatureEnabled(Profile.Feature.AUTHORIZATION)).thenReturn(true);
            when(session.getProvider(org.keycloak.authorization.AuthorizationProvider.class)).thenReturn(authorizationProvider);
            when(authorizationProvider.getStoreFactory()).thenReturn(storeFactory);
            when(storeFactory.getResourceServerStore()).thenReturn(resourceServerStore);
            when(resourceServerStore.findByClient(clientModel)).thenReturn(resourceServer);

            ClientRepresentation rep = ModelToRepresentation.toRepresentation(clientModel, session);

            //assertTrue(rep.isAuthorizationServicesEnabled());
        }
    }

    @Test
    void testStripClientAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("keep", "value");
        attributes.put(ClientModel.TYPE, "client-type");
        attributes.put("other", "other-value");

        Map<String, String> stripped = ModelToRepresentation.stripClientAttributesIncludedAsFields(attributes);

        assertEquals(2, stripped.size());
        assertTrue(stripped.containsKey("keep"));
        assertTrue(stripped.containsKey("other"));
        assertFalse(stripped.containsKey(ClientModel.TYPE));
    }
}
