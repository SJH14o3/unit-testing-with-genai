package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.keycloak.models.jpa.entities.UserEntity;

/**
 * Tests covering trivial getter and setter forwarding in {@link UserAdapter}.
 * These tests exercise the "simple accessors" that delegate directly to the underlying {@link UserEntity}.
 */
public class UserAdapterSimpleAccessorsTest extends UserAdapterTestBase {

    @Test
    public void testIdGetterDelegatesToUser() {
        when(user.getId()).thenReturn("user-123");
        assertEquals("user-123", adapter.getId());
    }

    @Test
    public void testUsernameGetterDelegates() {
        when(user.getUsername()).thenReturn("alice");
        assertEquals("alice", adapter.getUsername());
    }

    @Test
    public void testCreatedTimestampGetterDelegates() {
        when(user.getCreatedTimestamp()).thenReturn(100L);
        assertEquals(100L, adapter.getCreatedTimestamp());
    }

    @Test
    public void testSetCreatedTimestampDelegates() {
        adapter.setCreatedTimestamp(200L);
        verify(user).setCreatedTimestamp(200L);
    }

    @Test
    public void testLastModifiedTimestampGetterDelegates() {
        when(user.getLastModifiedTimestamp()).thenReturn(300L);
        assertEquals(300L, adapter.getLastModifiedTimestamp());
    }

    @Test
    public void testSetLastModifiedTimestampDelegates() {
        adapter.setLastModifiedTimestamp(400L);
        verify(user).setLastModifiedTimestamp(400L);
    }

    @Test
    public void testIsEnabledGetterDelegates() {
        when(user.isEnabled()).thenReturn(true);
        assertTrue(adapter.isEnabled());
    }

    @Test
    public void testSetEnabledDelegates() {
        adapter.setEnabled(false);
        verify(user).setEnabled(false);
    }

    @Test
    public void testFirstNameGetterDelegates() {
        when(user.getFirstName()).thenReturn("John");
        assertEquals("John", adapter.getFirstName());
    }

    @Test
    public void testSetFirstNameDelegates() {
        adapter.setFirstName("Jane");
        verify(user).setFirstName("Jane");
    }

    @Test
    public void testLastNameGetterDelegates() {
        when(user.getLastName()).thenReturn("Doe");
        assertEquals("Doe", adapter.getLastName());
    }

    @Test
    public void testSetLastNameDelegates() {
        adapter.setLastName("Smith");
        verify(user).setLastName("Smith");
    }

    @Test
    public void testEmailGetterDelegates() {
        when(user.getEmail()).thenReturn("test@example.com");
        assertEquals("test@example.com", adapter.getEmail());
    }

    @Test
    public void testSetEmailDelegatesWhenBlank() {
        // empty string should be treated as null
        adapter.setEmail("");
        verify(user).setEmail(null, true);
    }

    @Test
    public void testSetEmailDelegatesWhenNotBlank() {
        when(realm.isDuplicateEmailsAllowed()).thenReturn(false);
        adapter.setEmail("USER@EXAMPLE.COM");
        verify(user).setEmail("user@example.com", false);
    }

    @Test
    public void testEmailVerifiedGetterDelegates() {
        when(user.isEmailVerified()).thenReturn(true);
        assertTrue(adapter.isEmailVerified());
    }

    @Test
    public void testSetEmailVerifiedDelegates() {
        adapter.setEmailVerified(false);
        verify(user).setEmailVerified(false);
    }
}
