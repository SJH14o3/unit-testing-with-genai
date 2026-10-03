package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.utils.ReservedCharValidator;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testCreateRealmNameOnly() {
        String realmName = "new-realm";
        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<ReservedCharValidator> validatorMock = Mockito.mockStatic(ReservedCharValidator.class)) {
            
            RealmModel realm = mock(RealmModel.class);
            when(model.createRealm(anyString(), eq(realmName))).thenReturn(realm);

            RealmManager spyManager = spy(realmManager);
            doNothing().when(spyManager).setupRealmDefaults(any());
            doNothing().when(spyManager).setupMasterAdminManagement(any());
            doNothing().when(spyManager).setupRealmAdminManagement(any());
            doNothing().when(spyManager).setupAccountManagement(any());
            doNothing().when(spyManager).setupBrokerService(any());
            doNothing().when(spyManager).setupAdminConsole(any());
            doNothing().when(spyManager).setupAdminConsoleLocaleMapper(any());
            doNothing().when(spyManager).setupAdminCli(any());
            doNothing().when(spyManager).setupImpersonationService(any());
            doNothing().when(spyManager).setupAuthenticationFlows(any());
            doNothing().when(spyManager).setupRequiredActions(any());
            doNothing().when(spyManager).setupOfflineTokens(any(), any());
            doNothing().when(spyManager).createDefaultClientScopes(any());
            doNothing().when(spyManager).setupAuthorizationServices(any());
            doNothing().when(spyManager).setupClientRegistrations(any());
            doNothing().when(spyManager).fireRealmPostCreate(any());

            RealmModel result = spyManager.createRealm(realmName);

            assertNotNull(result);
            verify(model).createRealm(anyString(), eq(realmName));
            verify(realm).setName(realmName);
            verify(spyManager).setupRealmDefaults(realm);
            verify(spyManager).fireRealmPostCreate(realm);
        }
    }

    @Test
    void testCreateRealmWithId() {
        String id = "test-id";
        String name = "test-name";
        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<ReservedCharValidator> validatorMock = Mockito.mockStatic(ReservedCharValidator.class)) {
            
            RealmModel realm = mock(RealmModel.class);
            when(model.createRealm(id, name)).thenReturn(realm);

            RealmManager spyManager = spy(realmManager);
            doNothing().when(spyManager).setupRealmDefaults(any());
            doNothing().when(spyManager).setupMasterAdminManagement(any());
            doNothing().when(spyManager).setupRealmAdminManagement(any());
            doNothing().when(spyManager).setupAccountManagement(any());
            doNothing().when(spyManager).setupBrokerService(any());
            doNothing().when(spyManager).setupAdminConsole(any());
            doNothing().when(spyManager).setupAdminConsoleLocaleMapper(any());
            doNothing().when(spyManager).setupAdminCli(any());
            doNothing().when(spyManager).setupImpersonationService(any());
            doNothing().when(spyManager).setupAuthenticationFlows(any());
            doNothing().when(spyManager).setupRequiredActions(any());
            doNothing().when(spyManager).setupOfflineTokens(any(), any());
            doNothing().when(spyManager).createDefaultClientScopes(any());
            doNothing().when(spyManager).setupAuthorizationServices(any());
            doNothing().when(spyManager).setupClientRegistrations(any());
            doNothing().when(spyManager).fireRealmPostCreate(any());

            RealmModel result = spyManager.createRealm(id, name);

            assertNotNull(result);
            verify(validatorMock).validate(id);
            verify(validatorMock).validate(name);
            verify(model).createRealm(id, name);
            verify(spyManager).fireRealmPostCreate(realm);
        }
    }

    @Test
    void testCreateRealmGeneratesIdWhenNull() {
        String name = "test-name";
        String generatedId = "generated-id";
        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<ReservedCharValidator> validatorMock = Mockito.mockStatic(ReservedCharValidator.class)) {
            
            utilsMock.when(KeycloakModelUtils::generateId).thenReturn(generatedId);
            RealmModel realm = mock(RealmModel.class);
            when(model.createRealm(eq(generatedId), eq(name))).thenReturn(realm);

            RealmManager spyManager = spy(realmManager);
            doNothing().when(spyManager).setupRealmDefaults(any());
            doNothing().when(spyManager).setupMasterAdminManagement(any());
            doNothing().when(spyManager).setupRealmAdminManagement(any());
            doNothing().when(spyManager).setupAccountManagement(any());
            doNothing().when(spyManager).setupBrokerService(any());
            doNothing().when(spyManager).setupAdminConsole(any());
            doNothing().when(spyManager).setupAdminConsoleLocaleMapper(any());
            doNothing().when(spyManager).setupAdminCli(any());
            doNothing().when(spyManager).setupImpersonationService(any());
            doNothing().when(spyManager).setupAuthenticationFlows(any());
            doNothing().when(spyManager).setupRequiredActions(any());
            doNothing().when(spyManager).setupOfflineTokens(any(), any());
            doNothing().when(spyManager).createDefaultClientScopes(any());
            doNothing().when(spyManager).setupAuthorizationServices(any());
            doNothing().when(spyManager).setupClientRegistrations(any());
            doNothing().when(spyManager).fireRealmPostCreate(any());

            RealmModel result = spyManager.createRealm(null, name);

            assertNotNull(result);
            verify(model).createRealm(generatedId, name);
        }
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

        boolean removed = realmManager.removeRealm(realm);

        assertTrue(removed);
        verify(model).removeRealm(realmId);
    }
}
