package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import org.keycloak.util.JsonSerialization;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserProviderVerifiableCredentialTest {
    @Mock
    private KeycloakSession session;
    @Mock
    private EntityManager em;
    @Mock
    private RealmModel realm;
    @Mock
    private UserEntity userEntity;
    @Mock
    private UserAdapter userAdapter;

    @Test
    public void testAddVerifiableCredential_missingScope() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("id", null); // no clientScopeId
        Exception ex = assertThrows(ModelException.class, () -> provider.addVerifiableCredential("uid", model));
        assertTrue(ex.getMessage().contains("Credential scope not specified"));
    }

    @Test
    public void testAddVerifiableCredential_withUserAttributes() throws Exception {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("id", "cs-id");
        Map<String, List<String>> attrs = Map.of("a", List.of("b"));
        when(model.getUserAttributes()).thenReturn(attrs);
        String json = "{\"a\":\"b\"}";
        try (MockedStatic<JsonSerialization> jsonMock = mockStatic(JsonSerialization.class)) {
            jsonMock.when(() -> JsonSerialization.writeValueAsString(attrs)).thenReturn(json);
            JpaUserProvider provider = new JpaUserProvider(session, em);
            UserVerifiableCredentialEntity captured = new UserVerifiableCredentialEntity();
            ArgumentCaptor<UserVerifiableCredentialEntity> captor = ArgumentCaptor.forClass(UserVerifiableCredentialEntity.class);
            provider.addVerifiableCredential("uid", model);
            // Since addVerifiableCredential uses em.persist/flush, verify persisted
            verify(em).persist(captor.capture());
            UserVerifiableCredentialEntity entity = captor.getValue();
            assertEquals(json, entity.getUserAttributes());
        }
    }

    @Test
    public void testAddVerifiableCredential_withoutAttributes() throws Exception {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("id", "cs-id");
        when(model.getUserAttributes()).thenReturn(null);
        // Mock session.getContext()->realm->id
        Context ctx = mock(Context.class);
        when(session.getContext()).thenReturn(ctx);
        when(ctx.getRealm()).thenReturn(realm);
        when(realm.getId()).thenReturn("realm-id");
        // Mock UserProfileProvider chain
        UserProfileProvider provider = mock(UserProfileProvider.class);
        UserProfile userProfile = mock(UserProfile.class);
        Map<String, List<String>> attrs = Map.of("a", List.of("b"));
        when(userProfile.getAttributes()).thenReturn(new UserProfileAttributes(attrs));
        when(provider.create(any(), any())).thenReturn(userProfile);
        when(session.getProvider(UserProfileProvider.class)).thenReturn(provider);
        // Mock JsonSerialization
        String json = "{\"a\":\"b\"}";
        try (MockedStatic<JsonSerialization> jsonMock = mockStatic(JsonSerialization.class)) {
            jsonMock.when(() -> JsonSerialization.writeValueAsString(attrs)).thenReturn(json);
            JpaUserProvider provider = new JpaUserProvider(session, em);
            ArgumentCaptor<UserVerifiableCredentialEntity> captor = ArgumentCaptor.forClass(UserVerifiableCredentialEntity.class);
            provider.addVerifiableCredential("uid", model);
            verify(em).persist(captor.capture());
            UserVerifiableCredentialEntity entity = captor.getValue();
            assertEquals(json, entity.getUserAttributes());
        }
    }

    @Test
    public void testRemoveVerifiableCredential_notFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        // Mock stream to empty
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.empty());
        boolean result = provider.removeVerifiableCredential("uid", "clientScope");
        assertFalse(result);
    }

    @Test
    public void testRemoveVerifiableCredential_found() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialEntity found = mock(UserVerifiableCredentialEntity.class);
        when(found.getClientScopeId()).thenReturn("clientScope");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.of(found));
        provider.removeVerifiableCredential("uid", "clientScope");
        verify(em).createNamedQuery("deleteIssuedVcsByUserAndType");
        verify(em).remove(found);
    }

    @Test
    public void testUpdateVerifiableCredential_userNotFound() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("id", "cs");
        String clientScope="cs";
        when(provider.getVerifiableCredentialsEntitiesByUser("uid"))
                .thenReturn(Stream.of());
        when(em.find(UserEntity.class, "uid")).thenReturn(null);
        Exception ex = assertThrows(ModelException.class, () -> provider.updateVerifiableCredential("uid", clientScope));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    public void testUpdateVerifiableCredential_success() throws Exception {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        // Mock current user entity
        UserEntity userEnt = mock(UserEntity.class);
        when(em.find(UserEntity.class, "uid")).thenReturn(userEnt);
        UserVerifiableCredentialEntity entity = mock(UserVerifiableCredentialEntity.class);
        when(entity.getClientScopeId()).thenReturn("cs");
        when(provider.getVerifiableCredentialsEntitiesByUser("uid")).thenReturn(Stream.of(entity));
        // Mock profile provider to return attributes
        UserProfileProvider profileProv = mock(UserProfileProvider.class);
        UserProfile profile = mock(UserProfile.class);
        Map<String, List<String>> attrs = Map.of("k", List.of("v"));
        when(profile.getAttributes()).thenReturn(new UserProfileAttributes(attrs));
        when(profileProv.create(any(), any())).thenReturn(profile);
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProv);
        // Mock JsonSerialization
        String json = "{\"k\":\"v\"}";
        try (MockedStatic<JsonSerialization> jsonMock = mockStatic(JsonSerialization.class)) {
            jsonMock.when(() -> JsonSerialization.writeValueAsString(attrs)).thenReturn(json);
            provider.updateVerifiableCredential("uid", "cs");
            verify(entity).setUserAttributes(json);
            verify(entity).setRevision(anyString());
            verify(entity).setUpdatedDate(anyLong());
            verify(em).flush();
        }
    }
}

