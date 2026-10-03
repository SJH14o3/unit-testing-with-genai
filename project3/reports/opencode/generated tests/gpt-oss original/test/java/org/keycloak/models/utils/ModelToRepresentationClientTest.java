import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.models.AuthorizationProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ComponentModel;
import org.keycloak.models.CibaConfig;
import org.keycloak.models.Constants;
import org.keycloak.models.ParConfig;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.ResourceServer;
import org.keycloak.models.RoleModel;
import org.keycloak.models.StoreFactory;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.storage.StorageId;
import org.keycloak.keycloakServiceVerifyMethodTest; // placeholder for non used

import org.keycloak.models.KeycloakSession;

public class ModelToRepresentationClientTest {

    @Mock
    private ClientModel clientMock;

    @Mock
    private KeycloakSession sessionMock;

    @Mock
    private RealmModel realmMock;

    @Mock
    private AuthorizationProvider authProviderMock;

    @Mock
    private StoreFactory storeFactoryMock;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        when(sessionMock.getProvider(AuthorizationProvider.class)).thenReturn(authProviderMock);
        when(sessionMock.getProvider(org.keycloak.models.KeycloakModelProvider.class)).thenReturn(null);
        when(sessionMock.getProvider(org.keycloak.grpc.internal.InternalProvider.class)).thenReturn(null);
    }

    @Test
    @DisplayName("Client representation maps all fields correctly")
    public void testClientMapping() {
        when(clientMock.getId()).thenReturn("client-1");
        when(clientMock.getClientId()).thenReturn("clientId");
        when(clientMock.getName()).thenReturn("Test Client");
        when(clientMock.getDescription()).thenReturn("desc");
        when(clientMock.getType()).thenReturn("confidential");
        when(clientMock.isEnabled()).thenReturn(true);
        when(clientMock.isAlwaysDisplayInConsole()).thenReturn(false);
        when(clientMock.getManagementUrl()).thenReturn("http://example.com");
        when(clientMock.isPublicClient()).thenReturn(false);
        when(clientMock.isFrontchannelLogout()).thenReturn(true);
        when(clientMock.getProtocol()).thenReturn("openid-connect");
        // attributes
        HashMap<String,String> attrs = new HashMap<>();
        attrs.put("k1","v1");
        when(clientMock.getAttributes()).thenReturn(attrs);
        when(clientMock.getAuthenticationFlowBindingOverrides()).thenReturn(new HashMap<>());
        when(clientMock.isFullScopeAllowed()).thenReturn(true);
        when(clientMock.isBearerOnly()).thenReturn(false);
        when(clientMock.isConsentRequired()).thenReturn(false);
        when(clientMock.isStandardFlowEnabled()).thenReturn(true);
        when(clientMock.isImplicitFlowEnabled()).thenReturn(false);
        when(clientMock.isDirectAccessGrantsEnabled()).thenReturn(true);
        when(clientMock.isServiceAccountsEnabled()).thenReturn(false);
        when(clientMock.isSurrogateAuthRequired()).thenReturn(false);
        when(clientMock.getRootUrl()).thenReturn("rootUrl");
        when(clientMock.getBaseUrl()).thenReturn("baseUrl");
        when(clientMock.getNotBefore()).thenReturn(100);
        when(clientMock.getNodeReRegistrationTimeout()).thenReturn(200);
        when(clientMock.getClientAuthenticatorType()).thenReturn("client-auth");
        when(clientMock.getDefaultClientScopes()).thenReturn(Collections.emptySet());
        when(clientMock.getOptionalClientScopes()).thenReturn(Collections.emptySet());
        when(clientMock.getRedirectUris()).thenReturn(Collections.emptySet());
        when(clientMock.getWebOrigins()).thenReturn(Collections.emptySet());
        when(clientMock.getRegisteredNodes()).thenReturn(Collections.emptyMap());

        // protocol mappers
        when(clientMock.getProtocolMappersStream()).thenReturn(Collections.emptyStream());

        // Authorization disable flag
        when(authProviderMock.getStoreFactory()).thenReturn(storeFactoryMock);
        when(storeFactoryMock.getResourceServerStore()).thenReturn(mock(org.keycloak.authorization.store.ResourceServerStore.class));

        // Mock realm for isRealmClient suffix logic
        when(clientMock.getRealm()).thenReturn(realmMock);
        when(realmMock.getName()).thenReturn("testRealm");

        when(sessionMock.realms()).thenReturn(mock(org.keycloak.models.RealmSession.class));
        when(sessionMock.realms().getRealmByName("testRealm")).thenReturn(realmMock);

        ClientRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(clientMock, sessionMock);

        assertEquals("client-1", rep.getId());
        assertEquals("clientId", rep.getClientId());
        assertEquals("Test Client", rep.getName());
        assertEquals("desc", rep.getDescription());
        assertEquals("confidential", rep.getType());
        assertTrue(rep.isEnabled());
        assertFalse(rep.isAlwaysDisplayInConsole());
        assertEquals("http://example.com", rep.getAdminUrl());
        assertNull(rep.getSecret()); // secret should be null when not public
        assertEquals("openid-connect", rep.getProtocol());
        assertEquals(rep.getAttributes().get("k1"), "v1");
        assertFalse(rep.isBearerOnly());
        assertTrue(rep.isStandardFlowEnabled());
    }

    @Test
    @DisplayName("Public client has null secret")
    public void testPublicClientSecret() {
        when(clientMock.isPublicClient()).thenReturn(true);
        ClientRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(clientMock, sessionMock);
        assertNull(rep.getSecret(), "Secret should be null for public client");
    }
}
