package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.common.util.ObjectUtil;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;
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
class UserAdapterAttributeTest {

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
    void testSetSingleAttributeSpecial() {
        try (MockedStatic<KeycloakModelUtils> mockedKeycloakUtils = mockStatic(KeycloakModelUtils.class)) {
            mockedKeycloakUtils.when(() -> KeycloakModelUtils.toLowerCaseSafe(anyString())).thenAnswer(i -> i.getArgument(0));

            userAdapter.setSingleAttribute(UserModel.FIRST_NAME, "Foo");
            verify(user).setFirstName("Foo");

            userAdapter.setSingleAttribute(UserModel.LAST_NAME, "Bar");
            verify(user).setLastName("Bar");

            userAdapter.setSingleAttribute(UserModel.EMAIL, "test@test.com");
            verify(user).setEmail(eq("test@test.com"), anyBoolean());

            userAdapter.setSingleAttribute(UserModel.USERNAME, "User");
            verify(user).setUsername("User");
        }
    }

    @Test
    void testSetSingleAttributeCustomNew() {
        String attrName = "custom";
        String attrValue = "value";

        try (MockedStatic<KeycloakModelUtils> mockedKeycloakUtils = mockStatic(KeycloakModelUtils.class);
             MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {

            mockedKeycloakUtils.when(() -> KeycloakModelUtils.toLowerCaseSafe(anyString())).thenAnswer(i -> i.getArgument(0));
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(false);

            List<UserAttributeEntity> attributes = new ArrayList<>();
            when(user.getAttributes()).thenReturn(attributes);
            
            userAdapter.setSingleAttribute(attrName, attrValue);

            verify(em).persist(any(UserAttributeEntity.class));
            assertEquals(1, attributes.size());
        }
    }

    @Test
    void testSetSingleAttributeCustomUpdateExistingWithHQL() {
        String attrName = "custom";
        String attrValue = "newValue";
        String attrId = "attr-id";

        UserAttributeEntity existingAttr = mock(UserAttributeEntity.class);
        when(existingAttr.getName()).thenReturn(attrName);
        when(existingAttr.getValue()).thenReturn("oldValue");
        when(existingAttr.getId()).thenReturn(attrId);

        UserAttributeEntity otherAttr = mock(UserAttributeEntity.class);
        when(otherAttr.getName()).thenReturn(attrName);
        when(otherAttr.getValue()).thenReturn("otherValue");

        List<UserAttributeEntity> attributes = new ArrayList<>(List.of(existingAttr, otherAttr));
        when(user.getAttributes()).thenReturn(attributes);

        try (MockedStatic<KeycloakModelUtils> mockedKeycloakUtils = mockStatic(KeycloakModelUtils.class);
             MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {

            mockedKeycloakUtils.when(() -> KeycloakModelUtils.toLowerCaseSafe(anyString())).thenAnswer(i -> i.getArgument(0));
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(false);

            Query query = mock(Query.class);
            when(em.createNamedQuery("deleteUserAttributesByNameAndUserOtherThan")).thenReturn(query);
            when(user.getId()).thenReturn("user-id");

            userAdapter.setSingleAttribute(attrName, attrValue);

            verify(existingAttr).setValue(attrValue);
            verify(query).setParameter("name", attrName);
            verify(query).setParameter("userId", "user-id");
            verify(query).setParameter("attrId", attrId);
            verify(query).executeUpdate();
            assertEquals(List.of(existingAttr), attributes);
        }
    }

    @Test
    void testSetSingleAttributeCustomNullRemovesAttribute() {
        String attrName = "custom";
        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(false);
            
            userAdapter.setSingleAttribute(attrName, null);
            
            verifyNoInteractions(em);
        }
    }

    @Test
    void testSetAttributeSpecial() {
        userAdapter.setAttribute(UserModel.FIRST_NAME, List.of("First"));
        verify(user).setFirstName("First");

        userAdapter.setAttribute(UserModel.LAST_NAME, List.of("Last"));
        verify(user).setLastName("Last");

        userAdapter.setAttribute(UserModel.EMAIL, List.of("email@test.com"));
        verify(user).setEmail(eq("email@test.com"), anyBoolean());

        userAdapter.setAttribute(UserModel.USERNAME, List.of("username"));
        verify(user).setUsername("username");
    }

    @Test
    void testSetAttributeCustom() {
        String attrName = "custom";
        List<String> values = List.of("v1", "v2");

        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(false);
            when(user.getAttributes()).thenReturn(new ArrayList<>());

            userAdapter.setAttribute(attrName, values);

            verify(em, times(2)).persist(any(UserAttributeEntity.class));
        }
    }

    @Test
    void testSetAttributeCustomEmptyRemovesAll() {
        String attrName = "custom";
        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.collectionEquals(anyList(), anyList())).thenReturn(false);
            
            when(user.getAttributes()).thenReturn(new ArrayList<>());

            userAdapter.setAttribute(attrName, Collections.emptyList());

            verifyNoInteractions(em);
        }
    }

    @Test
    void testRemoveAttributeSpecial() {
        userAdapter.removeAttribute(UserModel.FIRST_NAME);
        verify(user).setFirstName(null);

        userAdapter.removeAttribute(UserModel.LAST_NAME);
        verify(user).setLastName(null);

        userAdapter.removeAttribute(UserModel.EMAIL);
        verify(user).setEmail(isNull(), anyBoolean());

        userAdapter.removeAttribute(UserModel.USERNAME);
        verify(user, never()).setUsername(any());
    }

    @Test
    void testRemoveAttributeCustom() {
        String attrName = "custom";
        UserAttributeEntity attr = new UserAttributeEntity();
        attr.setName(attrName);
        attr.setValue("val");
        
        List<UserAttributeEntity> attributes = new ArrayList<>(List.of(attr));
        when(user.getAttributes()).thenReturn(attributes);
        when(user.getId()).thenReturn("user-id");

        Query query = mock(Query.class);
        when(em.createNamedQuery("deleteUserAttributesByNameAndUser")).thenReturn(query);
        when(query.setParameter(eq("user"), any())).thenReturn(query);
        when(query.setParameter(eq("groupId"), any())).thenReturn(query);

        userAdapter.removeAttribute(attrName);

        verify(query).setParameter("name", attrName);
        verify(query).setParameter("userId", "user-id");
        verify(query).executeUpdate();
        assertTrue(attributes.isEmpty());
    }

    @Test
    void testGetFirstAttribute() {
        when(user.getFirstName()).thenReturn("First");
        assertEquals("First", userAdapter.getFirstAttribute(UserModel.FIRST_NAME));

        UserAttributeEntity attr = new UserAttributeEntity();
        attr.setName("custom");
        attr.setValue("customVal");
        when(user.getAttributes()).thenReturn(List.of(attr));
        assertEquals("customVal", userAdapter.getFirstAttribute("custom"));

        assertNull(userAdapter.getFirstAttribute("nonexistent"));
    }

    @Test
    void testGetAttributeStream() {
        when(user.getEmail()).thenReturn("test@test.com");
        assertEquals("test@test.com", userAdapter.getAttributeStream(UserModel.EMAIL).findFirst().orElse(null));

        UserAttributeEntity attr = new UserAttributeEntity();
        attr.setName("custom");
        attr.setValue("val");
        when(user.getAttributes()).thenReturn(List.of(attr));
        assertEquals("val", userAdapter.getAttributeStream("custom").findFirst().orElse(null));
    }

    @Test
    void testGetAttributes() {
        UserAttributeEntity attr = new UserAttributeEntity();
        attr.setName("custom");
        attr.setValue("val");
        when(user.getAttributes()).thenReturn(List.of(attr));
        when(user.getFirstName()).thenReturn("First");
        when(user.getLastName()).thenReturn("Last");
        when(user.getEmail()).thenReturn("email");
        when(user.getUsername()).thenReturn("user");

        Map<String, List<String>> result = userAdapter.getAttributes();

        assertEquals(List.of("val"), result.get("custom"));
        assertEquals(List.of("First"), result.get(UserModel.FIRST_NAME));
        assertEquals(List.of("email"), result.get(UserModel.EMAIL));
        assertEquals(List.of("user"), result.get(UserModel.USERNAME));
    }
}
