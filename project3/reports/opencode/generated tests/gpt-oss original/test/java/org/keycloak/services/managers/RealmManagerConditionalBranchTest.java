import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.authorization.fgap.AdminPermissionsSchema;
import org.keycloak.common.Profile;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.common.util.Encode;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.events.*;
import org.keycloak.models.*;
import org.keycloak.models.utils.*;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.representations.idm.*;
import org.keycloak.storage.*;
import org.keycloak.utils.ReservedCharValidator;
import org.keycloak.utils.StringUtil;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RealmManagerConditionalBranchTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock RoleModel role;
    @Mock ClientProvider clientProvider;
    @Mock KeycloakSessionFactory factory;
    @Mock TransactionManager txManager;
    @Mock EmailException emailException;
    @Mock RealmRepresentation rep;
    @Mock ClientRepresentation clientRep;
    @Mock ClientScopeRepresentation scopeRep;
    
    @BeforeEach
    void setUp(){
        when(session.realms()).thenReturn(realmProvider);
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        when(session.getTransactionManager()).thenReturn(txManager);
        when(realmProvider.createRealm(anyString(), anyString())).thenReturn(realm);
        when(realm.getName()).thenReturn("demoRealm");
    }

    // 1. setupAuthenticationFlows false branch
    @Test
    void setupAuthenticationFlows_existingFlowsNotAdded() {
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.of(new Object()));
        RealmManager manager = new RealmManager(session);
        // capture that DefaultAuthenticationFlows.addFlows not called
        try (MockedStatic<DefaultAuthenticationFlows> daf = mockStatic(DefaultAuthenticationFlows.class)) {
            manager.invokeSetupAuthenticationFlows(realm); // via subclass
            daf.verify(() -> DefaultAuthenticationFlows.addFlows(any()), never());
        }
    }

    // 2. setupRequiredActions false branch
    @Test
    void setupRequiredActions_existingActionsNotAdded() {
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.of(new Object()));
        RealmManager manager = new RealmManager(session);
        try (MockedStatic<DefaultRequiredActions> dra = mockStatic(DefaultRequiredActions.class)) {
            manager.invokeSetupRequiredActions(realm);
            dra.verify(() -> DefaultRequiredActions.addActions(any()), never());
        }
    }

    // 3. setupOfflineTokens multiple branches
    @Test
    void setupOfflineTokens_hasRole_and_hasScope() {
        RealmManager manager = new RealmManager(session);
        // case: realmRep not null, hasRealmRole true, hasClientScope false
        when(realm.getClientByClientId(anyString())).thenReturn(client);
        when(rep.getRoles()).thenReturn(new RealmRepresentation.RealmRoleContainer(){
            @Override public List<RoleRepresentation> getRealm(){return List.of(new RoleRepresentation(){{setName(Constants.OFFLINE_ACCESS_ROLE);}});} });
        when(rep.getClientScopes()).thenReturn(List.of());
        try (MockedStatic<DefaultClientScopes> dcs = mockStatic(DefaultClientScopes.class);
             MockedStatic<KeycloakModelUtils> kmu = mockStatic(KeycloakModelUtils.class)) {
            kmu.when(() -> KeycloakModelUtils.setupOfflineRole(any())).thenReturn(role);
            dcs.when(() -> DefaultClientScopes.createOfflineAccessClientScope(any(), any())).thenAnswer(inv -> {});
            manager.invokeSetupOfflineTokens(realm, rep);
            // expect client role created
            verify(defaultRole).setName(anyString());
        }
    }

    // 4. setupAdminConsole true/false
    @Test
    void setupAdminConsole_createAdminConsole() {
        when(realm.getClientByClientId(Constants.ADMIN_CONSOLE_CLIENT_ID)).thenReturn(null);
        try (MockedStatic<KeycloakModelUtils> utils = mockStatic(KeycloakModelUtils.class)) {
            ClientModel clientMock = mock(ClientModel.class);
            utils.when(() -> KeycloakModelUtils.createPublicClient(any(), anyString())).thenReturn(clientMock);
            RealmManager manager = new RealmManager(session);
            manager.invokeSetupAdminConsole(realm);
            utils.verify(() -> KeycloakModelUtils.createPublicClient(eq(realm), eq(Constants.ADMIN_CONSOLE_CLIENT_ID)));
        }
    }

    @Test
    void setupAdminConsole_useExistingAdminConsole() {
        when(realm.getClientByClientId(Constants.ADMIN_CONSOLE_CLIENT_ID)).thenReturn(client);
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupAdminConsole(realm);
        verify(client, never()).setRootUrl(anyString());
    }

    // 5. setupAdminConsoleLocaleMapper branches
    @Test
    void setupAdminConsoleLocaleMapper_addsMapperWhenNull() {
        when(client.getProtocolMapperByName(anyString(), anyString())).thenReturn(null);
        MockedStatic<ProtocolMapperUtils> pmu = mockStatic(ProtocolMapperUtils.class);
        pmu.when(() -> ProtocolMapperUtils.findLocaleMapper(any())).thenReturn(mock(ProtocolMapperModel.class));
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupAdminConsoleLocaleMapper(realm);
        pmu.verify(() -> ProtocolMapperUtils.findLocaleMapper(any()));
    }

    @Test
    void setupAdminConsoleLocaleMapper_skipsWhenMapperExists() {
        when(client.getProtocolMapperByName(anyString(), anyString())).thenReturn(mock(ProtocolMapperModel.class));
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupAdminConsoleLocaleMapper(realm);
        verify(client, never()).addProtocolMapper(any());
    }

    // 6. setupAdminCli branches
    @Test
    void setupAdminCli_createWhenNull() {
        when(realm.getClientByClientId(Constants.ADMIN_CLI_CLIENT_ID)).thenReturn(null);
        MockedStatic<KeycloakModelUtils> utils = mockStatic(KeycloakModelUtils.class);
        ClientModel cliMock = mock(ClientModel.class);
        utils.when(() -> KeycloakModelUtils.createPublicClient(any(), anyString())).thenReturn(cliMock);
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupAdminCli(realm);
        utils.verify(() -> KeycloakModelUtils.createPublicClient(eq(realm), eq(Constants.ADMIN_CLI_CLIENT_ID)));
    }

    @Test
    void setupAdminCli_skipWhenExists() {
        when(realm.getClientByClientId(Constants.ADMIN_CLI_CLIENT_ID)).thenReturn(client);
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupAdminCli(realm);
        verify(client, never()).setName(anyString());
    }

    // 7. setupRealmDefaults profile flag
    @Test
    void setupRealmDefaults_oid4vcEnabled_trueBranch() {
        when(rep.getSupportedLocales()).thenReturn(null);
        when(rep.getBrowserSecurityHeaders()).thenReturn(null);
        when(rep.getClientScopes()).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        try (MockedStatic<Profile> prof = mockStatic(Profile.class)) {
            prof.when(() -> Profile.isFeatureEnabled(any())).thenReturn(true);
            manager.setupRealmDefaults(realm, rep);
            verify(realm).setEventsListeners(eq(Collections.singleton("jboss-logging")));
        }
    }

    @Test
    void setupRealmDefaults_oid4vcDisabled_falseBranch() {
        when(rep.getSupportedLocales()).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        try (MockedStatic<Profile> prof = mockStatic(Profile.class)) {
            prof.when(() -> Profile.isFeatureEnabled(any())).thenReturn(false);
            manager.setupRealmDefaults(realm, rep);
            verify(realm, never()).setRole(anyString());
        }
    }

    // 8. removeRealm masterAdminClient null branch
    @Test
    void removeRealm_noMasterAdminClient_shouldNotCallRemoveClient() {
        when(realm.getMasterAdminClient()).thenReturn(null);
        when(realmProvider.removeRealm(anyString())).thenReturn(true);
        RealmManager manager = new RealmManager(session);
        manager.removeRealm(realm);
        verify(client, never()).removeClient(any(), anyString());
    }

    // 9. updateRealmEventsConfig false branches
    @Test
    void updateRealmEventsConfig_nullFields() {
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        RealmManager manager = new RealmManager(session);
        manager.updateRealmEventsConfig(rep, realm);
        verify(realm, never()).setEventsEnabled(anyBoolean());
        verify(realm, never()).setEventsExpiration(anyInt());
        verify(realm, never()).setEventsListeners(any());
    }

    // 10. setupMasterAdminManagement adminRealm branch
    @Test
    void setupMasterAdminManagement_adminRealm_skipCreation() {
        when(realm.getName()).thenReturn(Config.getAdminRealm());
        RealmManager manager = new RealmManager(session);
        manager.setupMasterAdminManagement(realm);
        verify(session, never()).realms();
    }

    // 11. setupRealmAdminManagement adminRealm skip
    @Test
    void setupRealmAdminManagement_adminRealm_noOps() {
        when(realm.getName()).thenReturn(Config.getAdminRealm());
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupRealmAdminManagement(realm);
        verify(client, never()).addRole(anyString());
    }

    // 12. setupAccountManagement accountClient null/ features
    @Test
    void setupAccountManagement_createClientAndAddRoles() {
        when(Right.existFlag).thenReturn(false);
        when(realm.getClientByClientId(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID)).thenReturn(null);
        MockedStatic<KeycloakModelUtils> utils = mockStatic(KeycloakModelUtils.class);
        ClientModel accountMock = mock(ClientModel.class);
        utils.when(() -> KeycloakModelUtils.createPublicClient(any(), anyString())).thenReturn(accountMock);
        RealmManager manager = new RealmManager(session);
        manager.invokeSetupAccountManagement(realm);
        utils.verify(() -> KeycloakModelUtils.createPublicClient(eq(realm), eq(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID)));
    }

    @Test
    void setupAccountManagement_oid4vcFeature_true() {
        when(realm.getClientByClientId(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID)).thenReturn(mock(ClientModel.class));
        // Stub Profile to enable feature
        try (MockedStatic<Profile> prof = mockStatic(Profile.class)) {
            prof.when(() -> Profile.isFeatureEnabled(Profile.Feature.OID4VC_VCI)).thenReturn(true);
            RealmManager manager = new RealmManager(session);
            manager.invokeSetupAccountManagement(realm);
        }
    }

    // 13. setupBrokerService profile disabled
    @Test
    void setupBrokerService_featureDisabled() {
        when(realm.getClientByClientId(Constants.BROKER_SERVICE_CLIENT_ID)).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        try (MockedStatic<Profile> prof = mockStatic(Profile.class)) {
            prof.when(() -> Profile.isFeatureEnabled(Profile.Feature.IDENTITY_BROKERING_API_V1)).thenReturn(false);
            manager.invokeSetupBrokerService(realm);
            verify(client, never()).addRole(anyString());
        }
    }

    // 14. importRealm various branches: skipUserDependent false, has adminPermissions
    @Test
    void importRealm_adminPermissionsClient() {
        when(rep.getRealm()).thenReturn("demo");
        when(rep.getAdminPermissionsClient()).thenReturn(mock(ClientRepresentation.class));
        RealmManager manager = new RealmManager(session);
        manager.importRealm(rep, () -> {});
        // Expect client creation logic called
        verify(session, atLeastOnce()).realms();
    }

    // 15. determineDefaultRoleName distinct paths
    @Test
    void determineDefaultRoleName_newRole() {
        when(rep.getRealm()).thenReturn("NewRealm");
        when(rep.getRoles()).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        String roleName = manager.determineDefaultRoleName(rep);
        assertEquals("${Default-roles-role-prefix}-newrealm", roleName);
    }

    @Test
    void determineDefaultRoleName_existingRole() {
        RoleRepresentation role = new RoleRepresentation(); role.setName("${Default-roles-role-prefix}-demo");
        RealmRepresentation.RoleContainer container = new RealmRepresentation.RoleContainer();
        container.setRealm(List.of(role));
        when(rep.getRealm()).thenReturn("Demo");
        when(rep.getRoles()).thenReturn(container);
        RealmManager manager = new RealmManager(session);
        String roleName = manager.determineDefaultRoleName(rep);
        assertTrue(roleName.startsWith("${Default-roles-role-prefix}-demo"));
    }

    // 16. postponeMasterClientSetup
    @Test
    void postponeMasterClientSetup_false() {
        when(rep.getRealm()).thenReturn("nonAdmin");
        RealmManager manager = new RealmManager(session);
        assertFalse(manager.postponeMasterClientSetup(rep));
    }

    // 17. hasRealmAdminManagementClient true branch
    @Test
    void hasRealmAdminManagementClient_true() {
        ClientRepresentation cr = mock(ClientRepresentation.class);
        when(cr.getClientId()).thenReturn("adminRealm-management");
        when(rep.getClients()).thenReturn(List.of(cr));
        RealmManager manager = new RealmManager(session);
        assertTrue(manager.hasRealmAdminManagementClient(rep));
    }

    // 18. hasClient true/false branches
    @Test
    void hasClient_returnsTrueForClientRep() {
        ClientRepresentation cron = mock(ClientRepresentation.class);
        when(cron.getClientId()).thenReturn("someClient");
        when(rep.getClients()).thenReturn(List.of(cron));
        RealmManager manager = new RealmManager(session);
        assertTrue(manager.hasClient(rep, "someClient"));
    }

    @Test
    void hasClient_returnsFalseWhenNoMatch() {
        when(rep.getClients()).thenReturn(Collections.emptyList());
        RealmManager manager = new RealmManager(session);
        assertFalse(manager.hasClient(rep, "unknown");
    }

    // 19. hasRealmRole true/false
    @Test
    void hasRealmRole_true() {
        RoleRepresentation role = new RoleRepresentation(); role.setName("offline_access");
        RoleContainer rc = new RealmRepresentation.RoleContainer(); rc.setRealm(List.of(role));
        when(rep.getRoles()).thenReturn(rc);
        RealmManager manager = new RealmManager(session);
        assertTrue(manager.hasRealmRole(rep, "offline_access"));
    }

    @Test
    void hasRealmRole_false() {
        when(rep.getRoles()).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        assertFalse(manager.hasRealmRole(rep, "pop"));
    }

    // 20. hasClientScope true/false
    @Test
    void hasClientScope_true() {
        ClientScopeRepresentation sc = new ClientScopeRepresentation(); sc.setName("offline_access");
        when(rep.getClientScopes()).thenReturn(List.of(sc));
        RealmManager manager = new RealmManager(session);
        assertTrue(manager.hasClientScope(rep, "offline_access"));
    }

    @Test
    void hasClientScope_false() {
        when(rep.getClientScopes()).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        assertFalse(manager.hasClientScope(rep, "any");
    }

    // 21. setupClientServiceAccountsAndAuthorizationOnImport skipUserDependent
    @Test
    void setupClientServiceAccountsAndAuthorization_skipWhenNoUserDependent() {
        when(rep.getClients()).thenReturn(Collections.emptyList());
        RealmManager manager = new RealmManager(session);
        manager.setupClientServiceAccountsAndAuthorizationOnImport(rep, true);
        verify(session, never()).models();
    }
}
