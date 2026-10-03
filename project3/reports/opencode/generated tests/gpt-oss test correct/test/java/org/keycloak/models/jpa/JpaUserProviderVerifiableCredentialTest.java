package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.protocol.oidc.grants.OAuth2GrantType;
import org.keycloak.userprofile.Attributes;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        model.setUserAttributes(attrs);
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
        // Mock session.getContext()->realm->id
        KeycloakContext ctx = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(ctx);
        when(ctx.getRealm()).thenReturn(realm);
        when(realm.getId()).thenReturn("realm-id");
        // Mock UserProfileProvider chain
        UserProfileProvider profileProvider = mock(UserProfileProvider.class);
        UserProfile userProfile = mock(UserProfile.class);
        Attributes attributes = mock(Attributes.class);
        Map<String, List<String>> attrs = Map.of("a", List.of("b"));
        when(attributes.getReadable()).thenReturn(attrs);
        when(userProfile.getAttributes()).thenReturn(attributes);
        when(profileProvider.create(eq(UserProfileContext.USER_API), nullable(UserModel.class))).thenReturn(userProfile);
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        // Mock JsonSerialization
        String json = "{\"a\":\"b\"}";
        try (MockedStatic<JsonSerialization> jsonMock = mockStatic(JsonSerialization.class)) {
            jsonMock.when(() -> JsonSerialization.writeValueAsString(attrs)).thenReturn(json);
            JpaUserProvider provider2 = new JpaUserProvider(session, em);
            ArgumentCaptor<UserVerifiableCredentialEntity> captor = ArgumentCaptor.forClass(UserVerifiableCredentialEntity.class);
            provider2.addVerifiableCredential("uid", model);
            verify(em).persist(captor.capture());
            UserVerifiableCredentialEntity entity = captor.getValue();
            assertEquals(json, entity.getUserAttributes());
        }
    }
}

