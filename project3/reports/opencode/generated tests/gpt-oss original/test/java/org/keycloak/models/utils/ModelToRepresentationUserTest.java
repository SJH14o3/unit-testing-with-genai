import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.Config;
import org.keycloak.common.util.Time;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.CibaConfig;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.Constants;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.IdentityProviderQuery;
import org.keycloak.models.IdentityProviderType;
import org.keycloak.models.IssuedVerifiableCredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelException;
import org.keycloak.models.ModelIllegalStateException;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.OrganizationDomainModel;
import org.keycloak.models.OrganizationModel;
import org.keycloak.models.ParConfig;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RequiredActionConfigModel;
import org.keycloak.models.RequiredActionProviderModel;
import org.keycloak.models.RequiredCredentialModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserConsentModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.WebAuthnPolicy;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.account.CredentialMetadataRepresentation;
import org.keycloak.representations.account.LocalizedMessage;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.AuthDetailsRepresentation;
import org.keycloak.representations.idm.AuthenticationExecutionExportRepresentation;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.ConfigPropertyRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RequiredActionConfigRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserConsentRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.representations.idm.authorization.AbstractPolicyRepresentation;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.oid4vc.IssuedVerifiableCredentialRepresentation;
import org.keycloak.representations.idm.oid4vc.UserVerifiableCredentialRepresentation;
import org.keycloak.storage.StorageId;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import org.keycloak.util.JsonSerialization;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class ModelToRepresentationUserTest {

    @Mock
    private KeycloakSession sessionMock;

    @Mock
    private RealmModel realmMock;

    @Mock
    private UserModel userMock;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        when(sessionMock.getProvider(UserProfileProvider.class)).thenReturn(null);
    }

    @Test
    @DisplayName("toRepresentation copies all user attributes when setUserAttributes=true")
    public void testToRepresentationCopiesAttributes() {
        // setup mock user data
        when(userMock.getId()).thenReturn("user-1");
        when(userMock.getUsername()).thenReturn("john");
        when(userMock.getFirstName()).thenReturn("John");
        when(userMock.getLastName()).thenReturn("Doe");
        when(userMock.getEmail()).thenReturn("john@example.com");
        when(userMock.isEnabled()).thenReturn(true);
        when(userMock.isEmailVerified()).thenReturn(false);
        when(userMock.getFederationLink()).thenReturn("link");
        when(userMock.credentialManager()).thenReturn(mock(UserCredentialManager.class));
        when(userMock.isAdmin()).thenReturn(false);
        when(userMock.getNotBefore()).thenReturn(0);
        when(userMock.getRequiredActionsStream()).thenReturn(Collections.emptyStream());
        when(userMock.getAttributes()).thenReturn(new HashMap<String, List<String>>() {{
            put("attr1", Collections.singletonList("value1"));
        }});

        // mocking credential manager
        UserCredentialManager credMgr = mock(UserCredentialManager.class);
        when(userMock.credentialManager()).thenReturn(credMgr);
        when(credMgr.isConfiguredFor(OTPCredentialModel.TYPE)).thenReturn(true);
        when(credMgr.getDisableableCredentialTypesStream()).thenReturn(Collections.emptyStream());

        org.keycloak.representations.idm.UserRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(sessionMock, realmMock, userMock);

        assertEquals("user-1", rep.getId());
        assertEquals("john", rep.getUsername());
        assertEquals("John", rep.getFirstName());
        assertEquals("Doe", rep.getLastName());
        assertEquals("john@example.com", rep.getEmail());
        assertTrue(rep.isEnabled());
        assertFalse(rep.isEmailVerified());
        assertEquals("link", rep.getFederationLink());
        assertTrue(rep.isTotp());
        assertEquals(0, rep.getNotBefore());
    }

    @Nested
    @DisplayName("Brief representation tests")
    class BriefTests {
        @Test
        @DisplayName("brief representation excludes attributes except specified")
        public void testBriefFiltersAttributes() {
            when(userMock.getId()).thenReturn("user-2");
            when(userMock.getUsername()).thenReturn("alice");
            when(userMock.isEnabled()).thenReturn(true);
            when(userMock.getRequiredActionsStream()).thenReturn(Collections.emptyStream());
            Map<String, List<String>> attrs = new HashMap<>();
            attrs.put(UserModel.LAST_NAME, Collections.singletonList("Smith"));
            attrs.put(UserModel.FIRST_NAME, Collections.singletonList("Alice"));
            attrs.put(UserModel.EMAIL, Collections.singletonList("alice@example.com"));
            attrs.put(UserModel.USERNAME, Collections.singletonList("alice"));
            attrs.put("custom", Collections.singletonList("val"));
            when(userMock.getAttributes()).thenReturn(attrs);

            org.keycloak.representations.idm.UserRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toBriefRepresentation(userMock);
            assertEquals("user-2", rep.getId());
            assertEquals("alice", rep.getUsername());
            assertNull(rep.getAttributes()); // custom attr removed
        }
    }

    @Test
    @DisplayName("addAttributeToBriefRep adds admin attribute when present")
    public void testAddAttributeToBriefRep() {
        org.keycloak.representations.idm.UserRepresentation rep = new org.keycloak.representations.idm.UserRepresentation();
        when(userMock.getFirstAttribute("temp_admin")).thenReturn("true");
        org.keycloak.models.utils.ModelToRepresentation.addAttributeToBriefRep(userMock, rep, "temp_admin");
        assertNotNull(rep.getAttributes());
        assertTrue(rep.getAttributes().containsKey("temp_admin"));
    }
}
