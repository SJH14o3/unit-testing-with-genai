import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.common.Profile;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;
import org.keycloak.events.*;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.utils.ReservedCharValidator;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RealmManagerEntryPointTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock KeycloakSessionFactory factory;
    @Mock TransactionManager txManager;
    @Mock RoleModel role;

    @BeforeEach
    void setUp() {
        when(session.realms()).thenReturn(realmProvider);
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        when(session.getTransactionManager()).thenReturn(txManager);
        // Provide a default realm name for tests
        when(realm.getName()).thenReturn("demoRealm");
    }

    //--- 1. getSession -----------------------------------------------------
    @Test
    void getSession_returnsSession() {
        RealmManager manager = new RealmManager(session);
        assertSame(session, manager.getSession());
    }

    //--- 2. isAdministrationRealm ------------------------------------------
    @Test
    void isAdministrationRealm_trueWhenAdmin() {
        try (MockedStatic<Config> cfg = mockStatic(Config.class)) {
            cfg.when(Config::getAdminRealm).thenReturn("adminRealm");
            when(realm.getName()).thenReturn("adminRealm");
            assertTrue(RealmManager.isAdministrationRealm(realm));
        }
    }
    @Test
    void isAdministrationRealm_falseWhenNotAdmin() {
        try (MockedStatic<Config> cfg = mockStatic(Config.class)) {
            cfg.when(Config::getAdminRealm).thenReturn("adminRealm");
            when(realm.getName()).thenReturn("otherRealm");
            assertFalse(RealmManager.isAdministrationRealm(realm));
        }
    }

    //--- 3. getRealm -------------------------------------------------------
    @Test
    void getRealm_forwardsCallToProvider() {
        String id = "realm-id-1";
        when(realmProvider.getRealm(id)).thenReturn(realm);
        RealmManager manager = new RealmManager(session);
        assertSame(realm, manager.getRealm(id));
        verify(realmProvider).getRealm(id);
    }

    //--- 4. createMasterAdminManagement -------------------------------------
    static class TestRealmManager extends RealmManager {
        TestRealmManager(KeycloakSession s){super(s);}
        void invokeCreateMasterAdminManagement(RealmModel r){super.createMasterAdminManagement(r);}
    }

    @Test
    void createMasterAdminManagement_adminRealm_noClientCreation() {
        try (MockedStatic<Config> cfg = mockStatic(Config.class)) {
            cfg.when(Config::getAdminRealm).thenReturn("adminRealm");
            when(realm.getName()).thenReturn("adminRealm");
            TestRealmManager manager = new TestRealmManager(session);
            manager.invokeCreateMasterAdminManagement(realm);
            // should create a role ADMIN and ADD create role
            verify(realm).addRole(AdminRoles.ADMIN);
            verify(realm).addRole(AdminRoles.CREATE_REALM);
            // No client creation
            verify(session, never()).realms();
        }
    }

    @Test
    void createMasterAdminManagement_nonAdminRealm_createsClient() {
        try (MockedStatic<Config> cfg = mockStatic(Config.class);
             MockedStatic<KeycloakModelUtils> utils = mockStatic(KeycloakModelUtils.class)) {
            cfg.when(Config::getAdminRealm).thenReturn("adminRealm");
            when(realm.getName()).thenReturn("normalRealm");
            // Expect client creation
            when(utils.callStaticMethod(any(), any())).thenAnswer(inv -> {
                // Simplify: return mock client
                return client;
            });
            // Need to stub specific call: KeycloakModelUtils.createManagementClient
            utils.when(() -> KeycloakModelUtils.createManagementClient(any(), anyString())).thenReturn(client);
            TestRealmManager manager = new TestRealmManager(session);
            manager.invokeCreateMasterAdminManagement(realm);
            utils.verify(() -> KeycloakModelUtils.createManagementClient(eq(realm), eq("normalRealm-realm-management")));
            // Ensure role addition via client
            verify(client, atLeastOnce()).addRole(anyString());
        }
    }

    //--- 5. checkMasterAdminManagementRoles ---------------------------------
    @Test
    void checkMasterAdminManagementRoles_missingRolesAdded() {
        try (MockedStatic<Config> cfg = mockStatic(Config.class)) {
            cfg.when(Config::getAdminRealm).thenReturn("adminRealm");
            when(realm.getName()).thenReturn("normalRealm");
            // mock client returned by realm.getClientByClientId
            when(session.clients()).thenReturn(mock(ClientProvider.class));
            when(session.clients().getClientByClientId(anyString())) .thenReturn(client);
            // mock existing roles
            when(client.getRole(AdminRoles.ADMIN)).thenReturn(role);
            when(role.hasRole(anyString())).thenReturn(true); // has composite but missing child
            doNothing().when(client).addRole(anyString());
            when(role.addCompositeRole(any())).thenAnswer(inv -> null);
            TestRealmManager manager = new TestRealmManager(session);
            // invoke protected method via reflection
            class Reflect {
                void run() {manager.checkMasterAdminManagementRoles(realm);} }
            new Reflect().run();
            // Verify each admin role added as composite
            for (String r : AdminRoles.ALL_REALM_ROLES) {
                verify(client).addRole(eq(r));
                verify(role).addCompositeRole(any(RoleModel.class));
            }
        }
    }

    //--- 6. checkRealmAdminManagementRoles ---------------------------------
    @Test
    void checkRealmAdminManagementRoles_nonMaster_missingRoles() {
        try (MockedStatic<Config> cfg = mockStatic(Config.class)) {
            cfg.when(Config::getAdminRealm).thenReturn("adminRealm");
            when(realm.getName()).thenReturn("normalRealm");
            // mock client returned by realm.getClientByClientId
            when(session.clients()).thenReturn(mock(ClientProvider.class));
            when(session.clients().getClientByClientId(anyString())) .thenReturn(client);
            // parent role mock
            RoleModel adminRole = mock(RoleModel.class);
            when(client.getRole(AdminRoles.REALM_ADMIN)).thenReturn(adminRole);
            // Stub missing roles
            doNothing().when(client).addRole(anyString());
            when(adminRole.addCompositeRole(any())).thenAnswer(inv -> null);
            TestRealmManager manager = new TestRealmManager(session);
            new Reflect(){ void run() {manager.checkRealmAdminManagementRoles(realm);} }().run();
            for (String r : AdminRoles.ALL_REALM_ROLES) {
                verify(client).addRole(eq(r));
                verify(adminRole).addCompositeRole(any(RoleModel.class));
            }
        }
    }

    //--- 7. importRealm(RealmRepresentation) (deprecated overload) ----------
    @Test
    void importRealm_deprecated_overload_calls_main() {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getRealm()).thenReturn("importRealm");
        when(rep.getId()).thenReturn(null);
        // stub transaction manager to avoid actual commit logic
        doNothing().when(txManager).enlistAfterCompletion(any());
        RealmManager manager = new RealmManager(session);
        RealmModel result = manager.importRealm(rep);
        assertEquals(realm, result);
    }

    //--- 8. setDefaultsForNewRealm ------------------------------------------
    @Test
    void setDefaultsForNewRealm_invokesAllHelpers() {
        RealmManager manager = spy(new RealmManager(session));
        // mock helper methods to no-op
        doNothing().when(manager).setupRealmDefaults(any(), any());
        doNothing().when(manager).setupDefaultRole(any(), anyString());
        doNothing().when(manager).setupRealmAdminManagement(any());
        doNothing().when(manager).setupAccountManagement(any());
        doNothing().when(manager).setupBrokerService(any());
        doNothing().when(manager).setupAdminConsole(any());
        doNothing().when(manager).setupAdminConsoleLocaleMapper(any());
        doNothing().when(manager).setupAdminCli(any());
        doNothing().when(manager).setupAuthenticationFlows(any());
        doNothing().when(manager).setupRequiredActions(any());
        doNothing().when(manager).setupOfflineTokens(any(), any());
        doNothing().when(manager).createDefaultClientScopes(any());
        doNothing().when(manager).setupAuthorizationServices(any());
        doNothing().when(manager).setupClientRegistrations(any());
        doNothing().when(manager).clientPolicy().setupClientPoliciesOnCreatedRealm(any());
        manager.setDefaultsForNewRealm(realm);
        verify(manager).setupRealmDefaults(eq(realm), eq(null));
        verify(manager).setupDefaultRole(eq(realm), anyString());
        verify(manager).setupRealmAdminManagement(realm);
        verify(manager).setupAccountManagement(realm);
        verify(manager).setupBrokerService(realm);
        verify(manager).setupAdminConsole(realm);
        verify(manager).setupAdminConsoleLocaleMapper(realm);
        verify(manager).setupAdminCli(realm);
        verify(manager).setupAuthenticationFlows(realm);
        verify(manager).setupRequiredActions(realm);
        verify(manager).setupOfflineTokens(realm, null);
        verify(manager).createDefaultClientScopes(realm);
        verify(manager).setupAuthorizationServices(realm);
        verify(manager).setupClientRegistrations(realm);
        verify(manager).clientPolicy();
    }

    //--- 9. getCreatedRealm / getKeycloakSession via event capture ---------
    @Test
    void importRealm_publishEvent_returnsCreatedRealmAndSession() {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getRealm()).thenReturn("eventRealm");
        when(rep.getId()).thenReturn("idE");
        // Setup factory publish to capture event
        ArgumentCaptor<RealmModel.RealmPostCreateEvent> eventCaptor = ArgumentCaptor.forClass(RealmModel.RealmPostCreateEvent.class);
        doNothing().when(factory).publish(eventCaptor.capture());
        RealmManager manager = new RealmManager(session);
        manager.importRealm(rep, () -> {});
        RealmModel.RealmPostCreateEvent evt = eventCaptor.getValue();
        assertSame(realm, evt.getCreatedRealm());
        assertSame(session, evt.getKeycloakSession());
        // Now test helper methods
        assertSame(realm, evt.getCreatedRealm());
        assertSame(session, evt.getKeycloakSession());
    }
}
