package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.Profile;
import org.keycloak.credential.CredentialMetadata;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.representations.account.CredentialMetadataRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModelToRepresentationUserTest {

    @BeforeEach
    void initializeProfile() {
        Profile.defaults();
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getNotBeforeOfUser(any(), any())).thenReturn(123);
    }

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private UserModel user;

    @Mock
    private SubjectCredentialManager credentialManager;

    @Mock
    private UserSessionModel userSession;

    @Mock
    private AuthenticatedClientSessionModel clientSession;

    @Mock
    private ClientModel clientModel;

    @Mock
    private UserCredentialModel userCredentialModel;

    @Mock
    private CredentialModel credentialModel;

    @Mock
    private CredentialMetadata credentialMetadata;

    @Mock
    private UserProfileProvider userProfileProvider;

    @Mock
    private UserProfile userProfile;

    @Mock
    private UserProvider userProvider;

    @Mock
    private KeycloakContext keycloakContext;

    @Mock
    private RealmManager realmManager;

    @Test
    void testToBriefRepresentation() {
        when(user.getId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("user-name");
        when(user.getCreatedTimestamp()).thenReturn(12345L);
        when(user.getLastName()).thenReturn("last-name");
        when(user.getFirstName()).thenReturn("first-name");
        when(user.getEmail()).thenReturn("email@example.com");
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn(null);
        when(user.getFirstAttribute(UserModel.IS_TEMP_ADMIN_ATTR_NAME)).thenReturn("true");

        org.keycloak.representations.idm.UserRepresentation rep = ModelToRepresentation.toBriefRepresentation(user);

        assertNotNull(rep);
        assertEquals("user-id", rep.getId());
        assertEquals("user-name", rep.getUsername());
        assertEquals(12345L, rep.getCreatedTimestamp());
        assertEquals("last-name", rep.getLastName());
        assertEquals("first-name", rep.getFirstName());
        assertEquals("email@example.com", rep.getEmail());
        assertTrue(rep.isEnabled());
        assertTrue(rep.isEmailVerified());
        assertEquals("true", rep.getAttributes().get(UserModel.IS_TEMP_ADMIN_ATTR_NAME).get(0));
    }

    @Test
    void testToRepresentationFull() {
        when(user.getId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("user-name");
        when(user.getCreatedTimestamp()).thenReturn(12345L);
        when(user.getLastName()).thenReturn("last-name");
        when(user.getFirstName()).thenReturn("first-name");
        when(user.getEmail()).thenReturn("email@example.com");
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn("fed-link");
        when(user.credentialManager()).thenReturn(credentialManager);
        when(credentialManager.isConfiguredFor(OTPCredentialModel.TYPE)).thenReturn(true);
        when(credentialManager.getDisableableCredentialTypesStream()).thenReturn(Stream.empty());
        when(user.getRequiredActionsStream()).thenReturn(Stream.of("action1"));
        when(user.getAttributes()).thenReturn(Map.of("attr1", List.of("val1"), UserModel.FIRST_NAME, List.of("ignored")));

        org.keycloak.representations.idm.UserRepresentation rep = ModelToRepresentation.toRepresentation(session, realm, user);

        assertNotNull(rep);
        assertEquals("user-id", rep.getId());
        assertEquals("user-name", rep.getUsername());
        assertEquals("last-name", rep.getLastName());
        assertEquals("first-name", rep.getFirstName());
        assertEquals("email@example.com", rep.getEmail());
        assertTrue(rep.isEnabled());
        assertTrue(rep.isEmailVerified());
        assertTrue(rep.isTotp());
        assertEquals("fed-link", rep.getFederationLink());
        assertEquals(123, rep.getNotBefore());
        assertTrue(rep.getRequiredActions().contains("action1"));
        assertEquals("val1", rep.getAttributes().get("attr1").get(0));
        assertFalse(rep.getAttributes().containsKey(UserModel.FIRST_NAME));
    }

    @Test
    void testToRepresentationWithUserProfile() {
        when(session.getProvider(UserProfileProvider.class)).thenReturn(userProfileProvider);
        when(userProfileProvider.create(eq(UserProfileContext.USER_API), eq(user))).thenReturn(userProfile);
        when(userProfile.toRepresentation(anyBoolean())).thenReturn(new UserRepresentation());
        when(session.getContext()).thenReturn(keycloakContext);
        when(keycloakContext.getRealm()).thenReturn(realm);
        when(user.getId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("user-name");
        when(user.getCreatedTimestamp()).thenReturn(12345L);
        when(user.getLastName()).thenReturn("last-name");
        when(user.getFirstName()).thenReturn("first-name");
        when(user.getEmail()).thenReturn("email@example.com");
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn(null);
        when(user.credentialManager()).thenReturn(credentialManager);
        when(credentialManager.isConfiguredFor(anyString())).thenReturn(false);
        when(credentialManager.getDisableableCredentialTypesStream()).thenReturn(Stream.empty());
        when(user.getRequiredActionsStream()).thenReturn(Stream.empty());

        org.keycloak.representations.idm.UserRepresentation rep = ModelToRepresentation.toRepresentation(session, user, false);

        assertNotNull(rep);
        assertEquals("user-id", rep.getId());
    }

    @Test
    void testToRepresentationUserSession() {
        when(userSession.getId()).thenReturn("session-id");
        when(userSession.getStarted()).thenReturn(1000);
        when(userSession.getLastSessionRefresh()).thenReturn(2000);
        when(userSession.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("user-name");
        when(user.getId()).thenReturn("user-id");
        when(userSession.getIpAddress()).thenReturn("127.0.0.1");
        when(userSession.isRememberMe()).thenReturn(true);
        when(userSession.getAuthenticatedClientSessions()).thenReturn(Map.of("client-id", clientSession));
        when(clientSession.getClient()).thenReturn(clientModel);
        when(clientModel.getId()).thenReturn("client-id");
        when(clientModel.getClientId()).thenReturn("client-client-id");

        UserSessionRepresentation rep = ModelToRepresentation.toRepresentation(userSession);

        assertNotNull(rep);
        assertEquals("session-id", rep.getId());
        assertEquals("user-name", rep.getUsername());
        assertEquals("user-id", rep.getUserId());
        assertEquals("127.0.0.1", rep.getIpAddress());
        assertTrue(rep.isRememberMe());
        assertTrue(rep.getClients().containsKey("client-id"));
        assertEquals("client-client-id", rep.getClients().get("client-id"));
    }

    @Test
    void testToRepresentationUserCredentialModel() {
        when(userCredentialModel.getChallengeResponse()).thenReturn("secret");
        CredentialRepresentation rep = ModelToRepresentation.toRepresentation(userCredentialModel);
        assertEquals(UserCredentialModel.SECRET, rep.getType());
        assertEquals("secret", rep.getValue());
    }

    @Test
    void testToRepresentationCredentialModel() {
        when(credentialModel.getId()).thenReturn("cred-id");
        when(credentialModel.getType()).thenReturn("type");
        when(credentialModel.getUserLabel()).thenReturn("label");
        when(credentialModel.getCreatedDate()).thenReturn(123L);
        when(credentialModel.getSecretData()).thenReturn("secret-data");
        when(credentialModel.getCredentialData()).thenReturn("cred-data");
        when(credentialModel.getFederationLink()).thenReturn("fed-link");

        CredentialRepresentation rep = ModelToRepresentation.toRepresentation(credentialModel);

        assertEquals("cred-id", rep.getId());
        assertEquals("type", rep.getType());
        assertEquals("label", rep.getUserLabel());
        assertEquals(123L, rep.getCreatedDate());
        assertEquals("secret-data", rep.getSecretData());
        assertEquals("cred-data", rep.getCredentialData());
        assertEquals("fed-link", rep.getFederationLink());
    }

    @Test
    void testToRepresentationCredentialMetadata() {
        when(credentialMetadata.getCredentialModel()).thenReturn(credentialModel);
        when(credentialMetadata.getInfoMessage()).thenReturn(null);
        when(credentialMetadata.getInfoProperties()).thenReturn(null);
        when(credentialMetadata.getWarningMessageDescription()).thenReturn(null);
        when(credentialMetadata.getWarningMessageTitle()).thenReturn(null);
        when(credentialMetadata.getIconLight()).thenReturn("light-icon");
        when(credentialMetadata.getIconDark()).thenReturn("dark-icon");
        
        when(credentialModel.getId()).thenReturn("cred-id");
        when(credentialModel.getType()).thenReturn("type");

        CredentialMetadataRepresentation rep = ModelToRepresentation.toRepresentation(credentialMetadata);

        assertNotNull(rep);
        assertEquals("light-icon", rep.getIconLight());
        assertEquals("dark-icon", rep.getIconDark());
    }
}
