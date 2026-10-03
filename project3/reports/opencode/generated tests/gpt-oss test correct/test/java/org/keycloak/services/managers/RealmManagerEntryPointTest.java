package org.keycloak.services.managers;

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
import org.keycloak.models.utils.*;
import org.keycloak.representations.idm.*;
import org.keycloak.events.*;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.services.clientregistration.policy.DefaultClientRegistrationPolicies;
import org.keycloak.utils.ReservedCharValidator;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerEntryPointTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock KeycloakSessionFactory factory;
    @Mock KeycloakTransactionManager txManager;
    @Mock RoleModel role;
    @Mock KeycloakContext keycloakContext;
    @Mock ClientProvider clientProvider;
    @Mock ClientPolicyManager clientPolicyManager;

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
    }

    //--- 7. importRealm(RealmRepresentation) (deprecated overload) ----------
    @Test
    void importRealm_deprecated_overload_calls_main() {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getRealm()).thenReturn("importRealm");
        when(rep.getId()).thenReturn(null);
        RealmManager manager = new RealmManager(session);
        RealmModel result = runImport(() -> manager.importRealm(rep));
        assertEquals(realm, result);
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
        runImport(() -> manager.importRealm(rep, () -> {}));
        RealmModel.RealmPostCreateEvent evt = eventCaptor.getValue();
        assertSame(realm, evt.getCreatedRealm());
        assertSame(session, evt.getKeycloakSession());
        // Now test helper methods
        assertSame(realm, evt.getCreatedRealm());
        assertSame(session, evt.getKeycloakSession());
    }

    /**
     * Runs an import with the static default-setup helpers stubbed out, so that only the
     * RealmManager code itself is exercised. Everything the manager pulls off the session,
     * the realm and the clients is provided by the mocks below.
     */
    private RealmModel runImport(Supplier<RealmModel> importCall) {
        when(session.getContext()).thenReturn(keycloakContext);
        when(session.clients()).thenReturn(clientProvider);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        when(clientProvider.getClientByClientId(any(RealmModel.class), anyString())).thenReturn(client);
        when(realmProvider.getRealmByName(Config.getAdminRealm())).thenReturn(realm);
        when(realmProvider.createRealm(anyString(), anyString())).thenReturn(realm);
        when(realm.getRole(anyString())).thenReturn(role);
        when(realm.addRole(anyString())).thenReturn(role);
        when(realm.addClient(anyString())).thenReturn(client);
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        when(client.getRole(anyString())).thenReturn(role);
        when(client.addRole(anyString())).thenReturn(role);

        try (MockedStatic<KeycloakModelUtils> modelUtils = mockStatic(KeycloakModelUtils.class);
                MockedStatic<DefaultClientScopes> clientScopes = mockStatic(DefaultClientScopes.class);
                MockedStatic<DefaultAuthenticationFlows> authFlows = mockStatic(DefaultAuthenticationFlows.class);
                MockedStatic<DefaultRequiredActions> requiredActions = mockStatic(DefaultRequiredActions.class);
                MockedStatic<DefaultClientRegistrationPolicies> regPolicies = mockStatic(DefaultClientRegistrationPolicies.class);
                MockedStatic<ImpersonationConstants> impersonation = mockStatic(ImpersonationConstants.class);
                MockedStatic<RepresentationToModel> repToModel = mockStatic(RepresentationToModel.class);
                MockedStatic<ProtocolMapperUtils> protocolMappers = mockStatic(ProtocolMapperUtils.class);
                MockedStatic<Profile> profile = mockStatic(Profile.class)) {

            modelUtils.when(KeycloakModelUtils::generateId).thenReturn("generated-realm-id");
            modelUtils.when(() -> KeycloakModelUtils.getMasterRealmAdminManagementClientId(anyString())).thenReturn("master-realm-demo");
            modelUtils.when(() -> KeycloakModelUtils.createManagementClient(any(), nullable(String.class))).thenReturn(client);
            modelUtils.when(() -> KeycloakModelUtils.createPublicClient(any(), nullable(String.class))).thenReturn(client);
            protocolMappers.when(() -> ProtocolMapperUtils.findLocaleMapper(any())).thenReturn(new ProtocolMapperModel());

            return importCall.get();
        }
    }
}
