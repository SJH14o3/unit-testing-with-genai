package org.keycloak.models.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.keycloak.common.util.Time;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    private ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);

    @Test
    public void testAddUser_generatesIdWhenNull() {
        try (MockedStatic<KeycloakModelUtils> mockStatic = mockStatic(KeycloakModelUtils.class)) {
            mockStatic.when(KeycloakModelUtils::generateId).thenReturn("generated-id");
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
            verify(em).persist(userEntityCaptor.capture());
            UserEntity entity = userEntityCaptor.getValue();
            assertEquals("generated-id", entity.getId());
        }
    }
}
