package org.keycloak.models.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.common.Profile;
import org.keycloak.common.Profile.Feature;
import org.keycloak.common.util.ObjectUtil;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.MockedStatic;

/**
 * Base test fixture providing common mocks and utilities for {@link UserAdapter} tests.
 */
public abstract class UserAdapterTestBase {
    protected KeycloakSession session;
    protected RealmModel realm;
    protected EntityManager em;
    protected UserEntity user;
    protected UserAdapter adapter;

    /**
     * Common setup executed before each test.
     */
    @BeforeEach
    public void setUp() {
        session = mock(KeycloakSession.class);
        realm = mock(RealmModel.class);
        em = mock(EntityManager.class);
        user = mock(UserEntity.class);
        adapter = new UserAdapter(session, realm, em, user);
    }

    /**
     * Stub common static methods used by UserAdapter.
     */
    @BeforeAll
    public static void initStaticStubs() {
        // Disable features that may affect test logic
        MockedStatic<Profile> profileMock = mockStatic(Profile.class);
        profileMock.when(() -> Profile.isFeatureEnabled(Feature.ORGANIZATION)).thenReturn(false);
        // Keep the MockedStatic alive for the duration of all tests
        profileMock.clear();
    }
}
