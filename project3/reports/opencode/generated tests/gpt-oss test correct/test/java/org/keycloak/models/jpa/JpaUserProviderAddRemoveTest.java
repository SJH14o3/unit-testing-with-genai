package org.keycloak.models.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.KeycloakSessionFactory;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class JpaUserProviderAddRemoveTest {
    @Mock
    private KeycloakSession session;
    @Mock
    private EntityManager em;
    @Mock
    private RealmModel realm;
    @Mock
    private RoleModel defaultRole;
    @Mock
    private GroupModel defaultGroup;
    @Mock
    private RequiredActionProviderModel actionEnabled;
    @Mock
    private RequiredActionProviderModel actionDisabled;

    @Test
    public void testAddUser_generatesIdWhenNull() {
        try (MockedStatic<KeycloakModelUtils> mockStatic = mockStatic(KeycloakModelUtils.class)) {
            mockStatic.when(KeycloakModelUtils::generateId).thenReturn("generated-id");
            // granting the default role and joining the default groups runs queries and publishes events
            TypedQuery<UserRoleMappingEntity> roleQuery = mock(TypedQuery.class, Answers.RETURNS_SELF);
            when(em.createNamedQuery("userHasRole", UserRoleMappingEntity.class)).thenReturn(roleQuery);
            when(session.getKeycloakSessionFactory()).thenReturn(mock(KeycloakSessionFactory.class));
            when(realm.getDefaultRole()).thenReturn(defaultRole);
            when(realm.getDefaultGroupsStream()).thenReturn(Stream.of(defaultGroup));
            when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.of(actionEnabled, actionDisabled));
            when(actionEnabled.isEnabled()).thenReturn(true);
            when(actionEnabled.isDefaultAction()).thenReturn(true);
            when(actionDisabled.isEnabled()).thenReturn(false);
            when(actionDisabled.isDefaultAction()).thenReturn(false);
            when(actionEnabled.getAlias()).thenReturn("alias1");
            JpaUserProvider provider = new JpaUserProvider(session, em);
            UserModel user = provider.addUser(realm, null, "TestUser", true, true);
            ArgumentCaptor<Object> persistedCaptor = ArgumentCaptor.forClass(Object.class);
            verify(em, atLeastOnce()).persist(persistedCaptor.capture());
            // the user entity is persisted together with the default role mapping, group membership and required action
            UserEntity entity = persistedCaptor.getAllValues().stream()
                    .filter(UserEntity.class::isInstance)
                    .map(UserEntity.class::cast)
                    .findFirst().orElseThrow();
            assertEquals("generated-id", entity.getId());
        }
    }
}
