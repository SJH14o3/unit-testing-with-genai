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
import org.keycloak.models.KeycloakSessionFactory;
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

    private static MockedStatic<Profile> profileMock;

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
        // Group/role membership events fired by UserAdapter are published through the session factory
        when(session.getKeycloakSessionFactory()).thenReturn(mock(KeycloakSessionFactory.class));
    }

    /**
     * Stub common static methods used by UserAdapter.
     */
    @BeforeAll
    public static void initStaticStubs() {
        // Disable features that may affect test logic
        profileMock = mockStatic(Profile.class);
        profileMock.when(() -> Profile.isFeatureEnabled(Feature.ORGANIZATION)).thenReturn(false);
    }

    /**
     * A static mock can only be registered once per thread, so it has to be released again
     * before the next test class registers its own one.
     */
    @AfterAll
    public static void closeStaticStubs() {
        if (profileMock != null) {
            profileMock.close();
            profileMock = null;
        }
    }
}
