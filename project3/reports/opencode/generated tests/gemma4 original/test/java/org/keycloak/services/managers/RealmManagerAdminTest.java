package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmManagerAdminTest {

    private KeycloakSession session;
    private RealmProvider model;
    private RealmManager realmManager;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        model = mock(RealmProvider.class);
        when(session.realms()).thenReturn(model);
        realmManager = new RealmManager(session);
    }

    @Test
    void testSetupMasterAdminManagement_NewClient() {
        RealmModel realm = mock(RealmModel.class);
        RealmModel adminRealm = mock(RealmModel.class);
        ClientModel masterApp = mock(ClientModel.class);
        
        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            configMock.when(Config::getAdminRealm).thenReturn("master");
            when(model.getRealmByName("master")).thenReturn(adminRealm);
            when(realm.getName()).thenReturn("test-realm");
            when(adminRealm.getClientByClientId(anyString())).thenReturn(null);
            
            // KeycloakModelUtils.getMasterRealmAdminManagementClientId(realm.getName())
            utilsMock.when(() -> KeycloakModelUtils.getMasterRealmAdminManagementClientId("test-realm"))
                    .thenReturn("master-admin-client");
            
            utilsMock.when(() -> KeycloakModelUtils.createManagementClient(eq(adminRealm), eq("master-admin-client")))
                    .thenReturn(masterApp);
            
            // Mocking the creation of roles and composite roles
            RoleModel adminRole = mock(RoleModel.class);
            RoleModel createRealmRole = mock(RoleModel.class);
            when(adminRealm.getRole(AdminRoles.ADMIN)).thenReturn(adminRole);
            when(adminRealm.addRole(AdminRoles.CREATE_REALM)).thenReturn(createRealmRole);
            when(adminRole.addCompositeRole(createRealmRole)).thenReturn(adminRole);
            
            when(masterApp.addRole(anyString())).thenReturn(mock(RoleModel.class));

            realmManager.setupMasterAdminManagement(realm);

            verify(realm).setMasterAdminClient(masterApp);
        }
    }

    @Test
    void testSetupMasterAdminManagement_ExistingClient() {
        RealmModel realm = mock(RealmModel.class);
        RealmModel adminRealm = mock(RealmModel.class);
        ClientModel masterApp = mock(ClientModel.class);
        
        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            configMock.when(Config::getAdminRealm).thenReturn("master");
            when(model.getRealmByName("master")).thenReturn(adminRealm);
            when(realm.getName()).thenReturn("test-realm");
            
            utilsMock.when(() -> KeycloakModelUtils.getMasterRealmAdminManagementClientId("test-realm"))
                    .thenReturn("master-admin-client");
            when(adminRealm.getClientByClientId("master-admin-client")).thenReturn(masterApp);

            realmManager.setupMasterAdminManagement(realm);

            verify(realm).setMasterAdminClient(masterApp);
        }
    }

    @Test
    void testSetupRealmAdminManagement_NotMaster() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel realmAdminClient = mock(ClientModel.class);
        RoleModel adminRole = mock(RoleModel.class);
        
        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            configMock.when(Config::getAdminRealm).thenReturn("master");
            when(realm.getName()).thenReturn("test-realm");
            when(realm.getClientByClientId(anyString())).thenReturn(null);
            
            utilsMock.when(() -> KeycloakModelUtils.createManagementClient(eq(realm), anyString()))
                    .thenReturn(realmAdminClient);
            
            when(realmAdminClient.addRole(AdminRoles.REALM_ADMIN)).thenReturn(adminRole);
            when(realmAdminClient.addRole(anyString())).thenReturn(mock(RoleModel.class));

            realmManager.setupRealmAdminManagement(realm);

            verify(realmAdminClient).setBearerOnly(true);
            verify(realmAdminClient).setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        }
    }

    @Test
    void testAddQueryCompositeRoles() {
        ClientModel client = mock(ClientModel.class);
        RoleModel viewClients = mock(RoleModel.class);
        RoleModel queryClients = mock(RoleModel.class);
        RoleModel queryUsers = mock(RoleModel.class);
        RoleModel queryGroups = mock(RoleModel.class);
        RoleModel queryOrganizations = mock(RoleModel.class);
        RoleModel viewUsers = mock(RoleModel.class);
        RoleModel viewOrganizations = mock(RoleModel.class);

        when(client.getRole(AdminRoles.VIEW_CLIENTS)).thenReturn(viewClients);
        when(client.getRole(AdminRoles.QUERY_CLIENTS)).thenReturn(queryClients);
        when(client.getRole(AdminRoles.VIEW_USERS)).thenReturn(viewUsers);
        when(client.getRole(AdminRoles.QUERY_USERS)).thenReturn(queryUsers);
        when(client.getRole(AdminRoles.QUERY_GROUPS)).thenReturn(queryGroups);
        when(client.getRole(AdminRoles.VIEW_ORGANIZATIONS)).thenReturn(viewOrganizations);
        when(client.getRole(AdminRoles.QUERY_ORGANIZATIONS)).thenReturn(queryOrganizations);

        realmManager.addQueryCompositeRoles(client);

        verify(viewClients).addCompositeRole(queryClients);
        verify(viewUsers).addCompositeRole(queryUsers);
        verify(viewUsers).addCompositeRole(queryGroups);
        verify(viewOrganizations).addCompositeRole(queryOrganizations);
    }

    @Test
    void testCheckRealmAdminManagementRoles() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel realmAdminClient = mock(ClientModel.class);
        RoleModel adminRole = mock(RoleModel.class);
        RoleModel missingRole = mock(RoleModel.class);

        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class)) {
            configMock.when(Config::getAdminRealm).thenReturn("master");
            when(realm.getName()).thenReturn("test-realm");
            when(realm.getClientByClientId(anyString())).thenReturn(realmAdminClient);
            when(realmAdminClient.getRole(AdminRoles.REALM_ADMIN)).thenReturn(adminRole);
            
            // Mock that one role is missing
            when(realmAdminClient.getRole(AdminRoles.VIEW_CLIENTS)).thenReturn(null);
            when(realmAdminClient.getRole(AdminRoles.VIEW_USERS)).thenReturn(mock(RoleModel.class));
            when(realmAdminClient.getRole(AdminRoles.VIEW_GROUPS)).thenReturn(mock(RoleModel.class));
            when(realmAdminClient.getRole(AdminRoles.VIEW_ORGANIZATIONS)).thenReturn(mock(RoleModel.class));
            
            // For the loop in checkRealmAdminManagementRoles
            when(realmAdminClient.addRole(AdminRoles.VIEW_CLIENTS)).thenReturn(missingRole);

            // We need to test the method via reflection or by making it package-private if possible, 
            // but since it's private, we test it by calling checkRealmAdminManagementRoles if it were public.
            // However, I can't change the source. I'll assume I'm testing the logic that is accessible.
            // Actually, I can use reflection to call private methods in tests.
            
            java.lang.reflect.Method method = RealmManager.class.getDeclaredMethod("checkRealmAdminManagementRoles", RealmModel.class);
            method.setAccessible(true);
            method.invoke(realmManager, realm);

            verify(realmAdminClient).addRole(AdminRoles.VIEW_CLIENTS);
            verify(missingRole).setDescription("${role_" + AdminRoles.VIEW_CLIENTS + "}");
            verify(adminRole).addCompositeRole(missingRole);
        } catch (Exception e) {
            fail(e);
        }
    }
}
