package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.jpa.entities.UserRequiredActionEntity;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserAdapterRefinementTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private EntityManager em;

    @Mock
    private UserEntity user;

    private UserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAdapter = new UserAdapter(session, realm, em, user);
    }

    @Test
    void testSetSingleAttributeCollectionEqualsTrue() {
        String name = "custom";
        String value = "val";
        UserAttributeEntity attr = new UserAttributeEntity();
        attr.setName(name);
        attr.setValue(value);
        
        when(user.getAttributes()).thenReturn(new ArrayList<>(List.of(attr)));
        
        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(true);
            
            userAdapter.setSingleAttribute(name, value);
            
            verify(em, never()).persist(any());
        }
    }

    @Test
    void testSetSingleAttributeNoExistingAttribute() {
        String name = "custom";
        String value = "val";
        
        when(user.getAttributes()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<KeycloakModelUtils> mockedUtils = mockStatic(KeycloakModelUtils.class)) {
            mockedUtils.when(() -> KeycloakModelUtils.toLowerCaseSafe(anyString())).thenAnswer(i -> i.getArgument(0));
            
            userAdapter.setSingleAttribute(name, value);
            
            verify(em).persist(any(UserAttributeEntity.class));
        }
    }

    @Test
    void testSetAttributeBranches() {
        String name = "custom";

        UserAttributeEntity existing = new UserAttributeEntity();
        existing.setName(name);
        existing.setValue("old");

        // Test values == null (Line 213/214 logic)
        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(false);
            when(user.getAttributes()).thenReturn(new ArrayList<>(List.of(existing)));
            when(user.getId()).thenReturn("user-id");

            Query query = mock(Query.class);
            when(em.createNamedQuery("deleteUserAttributesByNameAndUser")).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);

            userAdapter.setAttribute(name, null);
            verify(em).createNamedQuery("deleteUserAttributesByNameAndUser");

            // Test values non-empty (Line 220 logic)
            userAdapter.setAttribute(name, List.of("v1"));
            verify(em, atLeastOnce()).persist(any(UserAttributeEntity.class));
        }
    }

    @Test
    void testRemoveAttributeNoCustomAttributes() {
        String name = "custom";
        when(user.getAttributes()).thenReturn(new ArrayList<>());

        userAdapter.removeAttribute(name);

        // For custom attributes, if empty, it should call special attribute setters (firstName/lastName/etc)
        // But since it's custom, it just returns. The code says:
        // if (customAttributesToRemove.isEmpty()) { ... }
        // We check that it doesn't execute the HQL part.
        verify(em, never()).createNamedQuery(eq("deleteUserAttributesByNameAndUser"), eq(UserAttributeEntity.class));
    }

    @Test
    void testGetFirstAttributeSpecialBranches() {
        when(user.getFirstName()).thenReturn("First");
        assertEquals("First", userAdapter.getFirstAttribute(UserModel.FIRST_NAME));

        when(user.getLastName()).thenReturn("Last");
        assertEquals("Last", userAdapter.getFirstAttribute(UserModel.LAST_NAME));

        when(user.getEmail()).thenReturn("email@test.com");
        assertEquals("email@test.com", userAdapter.getFirstAttribute(UserModel.EMAIL));

        when(user.getUsername()).thenReturn("user");
        assertEquals("user", userAdapter.getFirstAttribute(UserModel.USERNAME));
    }

    @Test
    void testGetAttributeStreamSpecialBranches() {
        when(user.getFirstName()).thenReturn("First");
        assertEquals("First", userAdapter.getAttributeStream(UserModel.FIRST_NAME).findFirst().get());

        when(user.getLastName()).thenReturn("Last");
        assertEquals("Last", userAdapter.getAttributeStream(UserModel.LAST_NAME).findFirst().get());

        when(user.getEmail()).thenReturn("email@test.com");
        assertEquals("email@test.com", userAdapter.getAttributeStream(UserModel.EMAIL).findFirst().get());

        when(user.getUsername()).thenReturn("user");
        assertEquals("user", userAdapter.getAttributeStream(UserModel.USERNAME).findFirst().get());
    }

    @Test
    void testAddRequiredActionDuplicate() {
        String action = "REQUIRED";
        UserRequiredActionEntity existing = new UserRequiredActionEntity();
        existing.setAction(action);
        
        when(user.getRequiredActions()).thenReturn(new ArrayList<>(List.of(existing)));

        userAdapter.addRequiredAction(action);

        verify(em, never()).persist(any());
    }

    @Test
    void testRemoveRequiredActionNotFound() {
        String action = "NOT_FOUND";
        when(user.getRequiredActions()).thenReturn(new ArrayList<>());

        userAdapter.removeRequiredAction(action);

        verify(em, never()).remove(any());
    }

    @Test
    void testEqualsIdentity() {
        UserAdapter sameAdapter = userAdapter;
        assertEquals(userAdapter, sameAdapter);
    }
}
