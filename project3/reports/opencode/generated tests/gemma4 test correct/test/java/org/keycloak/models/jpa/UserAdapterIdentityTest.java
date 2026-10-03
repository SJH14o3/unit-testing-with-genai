package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserAdapterIdentityTest {

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
    void testGetId() {
        when(user.getId()).thenReturn("test-id");
        assertEquals("test-id", userAdapter.getId());
    }

    @Test
    void testGetUsername() {
        when(user.getUsername()).thenReturn("test-user");
        assertEquals("test-user", userAdapter.getUsername());
    }

    @Test
    void testSetUsername() {
        userAdapter.setUsername("TestUser");
        verify(user).setUsername(anyString());
    }

    @Test
    void testGetFirstName() {
        when(user.getFirstName()).thenReturn("First");
        assertEquals("First", userAdapter.getFirstName());
    }

    @Test
    void testSetFirstName() {
        userAdapter.setFirstName("NewFirst");
        verify(user).setFirstName("NewFirst");
    }

    @Test
    void testGetLastName() {
        when(user.getLastName()).thenReturn("Last");
        assertEquals("Last", userAdapter.getLastName());
    }

    @Test
    void testSetLastName() {
        userAdapter.setLastName("NewLast");
        verify(user).setLastName("NewLast");
    }

    @Test
    void testGetEmail() {
        when(user.getEmail()).thenReturn("test@example.com");
        assertEquals("test@example.com", userAdapter.getEmail());
    }

    @Test
    void testSetEmail() {
        when(realm.isDuplicateEmailsAllowed()).thenReturn(true);
        userAdapter.setEmail("TEST@EXAMPLE.COM");
        verify(user).setEmail(anyString(), eq(true));
    }

    @Test
    void testSetEmailNull() {
        when(realm.isDuplicateEmailsAllowed()).thenReturn(false);
        userAdapter.setEmail(null);
        verify(user).setEmail(isNull(), eq(false));
    }

    @Test
    void testIsEmailVerified() {
        when(user.isEmailVerified()).thenReturn(true);
        assertTrue(userAdapter.isEmailVerified());
    }

    @Test
    void testSetEmailVerified() {
        userAdapter.setEmailVerified(true);
        verify(user).setEmailVerified(true);
    }

    @Test
    void testGetCreatedTimestamp() {
        when(user.getCreatedTimestamp()).thenReturn(12345L);
        assertEquals(12345L, userAdapter.getCreatedTimestamp());
    }

    @Test
    void testSetCreatedTimestamp() {
        userAdapter.setCreatedTimestamp(12345L);
        verify(user).setCreatedTimestamp(12345L);
    }

    @Test
    void testGetLastModifiedTimestamp() {
        when(user.getLastModifiedTimestamp()).thenReturn(67890L);
        assertEquals(67890L, userAdapter.getLastModifiedTimestamp());
    }

    @Test
    void testSetLastModifiedTimestamp() {
        userAdapter.setLastModifiedTimestamp(67890L);
        verify(user).setLastModifiedTimestamp(67890L);
    }

    @Test
    void testIsEnabled() {
        when(user.isEnabled()).thenReturn(true);
        assertTrue(userAdapter.isEnabled());
    }

    @Test
    void testSetEnabled() {
        userAdapter.setEnabled(false);
        verify(user).setEnabled(false);
    }

    @Test
    void testGetFederationLink() {
        when(user.getFederationLink()).thenReturn("link");
        assertEquals("link", userAdapter.getFederationLink());
    }

    @Test
    void testSetFederationLink() {
        userAdapter.setFederationLink("new-link");
        verify(user).setFederationLink("new-link");
    }

    @Test
    void testGetServiceAccountClientLink() {
        when(user.getServiceAccountClientLink()).thenReturn("client-link");
        assertEquals("client-link", userAdapter.getServiceAccountClientLink());
    }

    @Test
    void testSetServiceAccountClientLink() {
        userAdapter.setServiceAccountClientLink("new-client-link");
        verify(user).setServiceAccountClientLink("new-client-link");
    }

    @Test
    void testEqualsAndHashCode() {
        UserEntity user1 = mock(UserEntity.class);
        UserEntity user2 = mock(UserEntity.class);
        when(user1.getId()).thenReturn("id1");
        when(user2.getId()).thenReturn("id1");

        UserAdapter adapter1 = new UserAdapter(session, realm, em, user1);
        UserAdapter adapter2 = new UserAdapter(session, realm, em, user2);

        assertEquals(adapter1, adapter2);
        assertEquals(adapter1.hashCode(), adapter2.hashCode());

        when(user2.getId()).thenReturn("id2");
        UserAdapter adapter3 = new UserAdapter(session, realm, em, user2);
        assertNotEquals(adapter1, adapter3);
        assertNotEquals(adapter1.hashCode(), adapter3.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        assertNotEquals(null, userAdapter);
        assertNotEquals("not a usermodel", userAdapter);
    }
}
