package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.credential.UserCredentialManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.models.jpa.entities.UserRequiredActionEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserAdapterRequiredActionTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private EntityManager em;

    @Mock
    private UserEntity user;

    @Mock
    private UserProvider userProvider;

    @Mock
    private UserCredentialManager credentialManager;

    private UserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAdapter = new UserAdapter(session, realm, em, user);
    }

    @Test
    void testGetRequiredActionsStream() {
        UserRequiredActionEntity action1 = new UserRequiredActionEntity();
        action1.setAction("UPDATE_PASSWORD");
        UserRequiredActionEntity action2 = new UserRequiredActionEntity();
        action2.setAction("VERIFY_EMAIL");

        when(user.getRequiredActions()).thenReturn(List.of(action1, action2));

        List<String> actions = userAdapter.getRequiredActionsStream().collect(Collectors.toList());

        assertEquals(2, actions.size());
        assertTrue(actions.contains("UPDATE_PASSWORD"));
        assertTrue(actions.contains("VERIFY_EMAIL"));
    }

    @Test
    void testAddRequiredActionNew() {
        String actionName = "NEW_ACTION";
        List<UserRequiredActionEntity> actions = new ArrayList<>();
        when(user.getRequiredActions()).thenReturn(actions);

        userAdapter.addRequiredAction(actionName);

        verify(em).persist(any(UserRequiredActionEntity.class));
        assertEquals(1, actions.size());
    }

    @Test
    void testAddRequiredActionDuplicate() {
        String actionName = "EXISTING_ACTION";
        UserRequiredActionEntity existingAction = new UserRequiredActionEntity();
        existingAction.setAction(actionName);

        List<UserRequiredActionEntity> actions = new ArrayList<>(List.of(existingAction));
        when(user.getRequiredActions()).thenReturn(actions);

        userAdapter.addRequiredAction(actionName);

        verify(em, never()).persist(any(UserRequiredActionEntity.class));
        assertEquals(List.of(existingAction), actions);
    }

    @Test
    void testRemoveRequiredAction() {
        String actionName = "REMOVE_ME";
        UserRequiredActionEntity action = new UserRequiredActionEntity();
        action.setAction(actionName);

        List<UserRequiredActionEntity> actions = new ArrayList<>(List.of(action));
        when(user.getRequiredActions()).thenReturn(actions);

        userAdapter.removeRequiredAction(actionName);

        assertTrue(actions.isEmpty());
        verify(em).remove(action);
    }

    @Test
    void testRemoveRequiredActionNonExistent() {
        String actionName = "NON_EXISTENT";
        when(user.getRequiredActions()).thenReturn(new ArrayList<>());

        userAdapter.removeRequiredAction(actionName);

        verify(em, never()).remove(any());
    }

    @Test
    void testCredentialManager() {
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserCredentialManager(userAdapter)).thenReturn(credentialManager);

        assertEquals(credentialManager, userAdapter.credentialManager());
    }
}
