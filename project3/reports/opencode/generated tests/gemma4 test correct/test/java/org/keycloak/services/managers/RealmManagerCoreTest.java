package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.utils.ReservedCharValidator;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerCoreTest {

    private KeycloakSession session;
    private RealmProvider model;
    private ClientPolicyManager clientPolicyManager;
    private RealmManager realmManager;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        model = mock(RealmProvider.class);
        clientPolicyManager = mock(ClientPolicyManager.class);
        
        when(session.realms()).thenReturn(model);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        
        realmManager = new RealmManager(session);
    }

    @Test
    void testGetSession() {
        assertEquals(session, realmManager.getSession());
    }

    @Test
    void testGetKeycloakAdministrationRealm() {
        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class)) {
            configMock.when(Config::getAdminRealm).thenReturn("master");
            RealmModel adminRealm = mock(RealmModel.class);
            when(model.getRealmByName("master")).thenReturn(adminRealm);

            assertEquals(adminRealm, realmManager.getKeycloakAdministrationRealm());
        }
    }

    @Test
    void testIsAdministrationRealm() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class)) {
            configMock.when(Config::getAdminRealm).thenReturn("master");
            
            when(realm.getName()).thenReturn("master");
            assertTrue(RealmManager.isAdministrationRealm(realm));

            when(realm.getName()).thenReturn("other");
            assertFalse(RealmManager.isAdministrationRealm(realm));
        }
    }

    @Test
    void testGetRealm() {
        String realmId = "test-realm";
        RealmModel realm = mock(RealmModel.class);
        when(model.getRealm(realmId)).thenReturn(realm);

        assertEquals(realm, realmManager.getRealm(realmId));
    }

    @Test
    void testGetRealmByName() {
        String realmName = "test-realm";
        RealmModel realm = mock(RealmModel.class);
        when(model.getRealmByName(realmName)).thenReturn(realm);

        assertEquals(realm, realmManager.getRealmByName(realmName));
    }

    @Test
    void testGetRealmAdminClientId() {
        assertEquals(Constants.REALM_MANAGEMENT_CLIENT_ID, realmManager.getRealmAdminClientId(mock(RealmModel.class)));
        assertEquals(Constants.REALM_MANAGEMENT_CLIENT_ID, realmManager.getRealmAdminClientId(mock(RealmRepresentation.class)));
    }

    @Test
    void testRemoveRealmSuccess() {
        RealmModel realm = mock(RealmModel.class);
        String realmId = "test-realm";
        when(realm.getId()).thenReturn(realmId);
        when(realm.getMasterAdminClient()).thenReturn(null);
        when(model.removeRealm(realmId)).thenReturn(true);
        when(session.getTransactionManager()).thenReturn(mock(KeycloakTransactionManager.class));
        when(session.getKeycloakSessionFactory()).thenReturn(mock(KeycloakSessionFactory.class));

        boolean removed = realmManager.removeRealm(realm);

        assertTrue(removed);
        verify(model).removeRealm(realmId);
    }
}
