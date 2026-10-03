package org.keycloak.models.utils;

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
import org.keycloak.models.*;
import org.keycloak.models.credential.OTPCredentialModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.common.util.Time;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@MockitoSettings(strictness = Strictness.LENIENT)
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
        // Profile.isFeatureEnabled is consulted by the conversion, so the profile must be initialized
        Profile.defaults();
        when(sessionMock.getProvider(UserProfileProvider.class)).thenReturn(null);
        when(sessionMock.users()).thenReturn(mock(UserProvider.class));
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
        when(userMock.credentialManager()).thenReturn(mock(SubjectCredentialManager.class));
        when(userMock.getRequiredActionsStream()).thenReturn(Stream.of());
        when(userMock.getAttributes()).thenReturn(new HashMap<String, List<String>>() {{
            put("attr1", Collections.singletonList("value1"));
        }});

        // mocking credential manager
        UserCredentialManager credMgr = mock(UserCredentialManager.class);
        when(userMock.credentialManager()).thenReturn(credMgr);
        when(credMgr.isConfiguredFor(OTPCredentialModel.TYPE)).thenReturn(true);
        when(credMgr.getDisableableCredentialTypesStream()).thenReturn(Stream.of());

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
            when(userMock.getRequiredActionsStream()).thenReturn(Stream.of());
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
}
