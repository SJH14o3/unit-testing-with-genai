package org.keycloak.services.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.keycloak.constants.OID4VCIConstants.CREDENTIAL_OFFER_CREATE;

import jakarta.transaction.TransactionManager;
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
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.services.clientregistration.policy.DefaultClientRegistrationPolicies;
import org.keycloak.storage.*;
import org.keycloak.utils.ReservedCharValidator;
import org.keycloak.utils.StringUtil;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerConditionalBranchTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock ClientModel client;
    @Mock RoleModel role;
    @Mock ClientProvider clientProvider;
    @Mock KeycloakSessionFactory factory;
    @Mock
    KeycloakTransactionManager txManager;
    @Mock RealmRepresentation rep;
    @Mock ClientRepresentation clientRep;
    @Mock ClientScopeRepresentation scopeRep;
    @Mock KeycloakContext keycloakContext;
    @Mock ClientPolicyManager clientPolicyManager;
    
    @BeforeEach
    void setUp(){
        when(session.realms()).thenReturn(realmProvider);
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        when(session.getTransactionManager()).thenReturn(txManager);
        when(realmProvider.createRealm(anyString(), anyString())).thenReturn(realm);
        when(realm.getName()).thenReturn("demoRealm");
    }

    // 7. setupRealmDefaults profile flag
    @Test
    void setupRealmDefaults_oid4vcEnabled_trueBranch() {
        when(rep.getSupportedLocales()).thenReturn(null);
        when(rep.getBrowserSecurityHeaders()).thenReturn(null);
        when(rep.getClientScopes()).thenReturn(null);
        when(realm.addRole(anyString())).thenReturn(role);
        RealmManager manager = new RealmManager(session);
        try (MockedStatic<Profile> prof = mockStatic(Profile.class)) {
            prof.when(() -> Profile.isFeatureEnabled(any())).thenReturn(true);
            manager.setupRealmDefaults(realm, rep);
            verify(realm).addRole(CREDENTIAL_OFFER_CREATE.getName());
            verify(role).setDescription(CREDENTIAL_OFFER_CREATE.getDescription());
            verify(realm).setEventsListeners(eq(Collections.singleton("jboss-logging")));
        }
    }

    // 9. updateRealmEventsConfig false branches
    @Test
    void updateRealmEventsConfig_nullFields() {
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        RealmManager manager = new RealmManager(session);
        manager.updateRealmEventsConfig(rep, realm);
        // a fresh representation carries no events expiration and events disabled
        verify(realm).setEventsEnabled(false);
        verify(realm).setEventsExpiration(0);
        verify(realm, never()).setEventsListeners(any());
    }

    // 10. setupMasterAdminManagement adminRealm branch
    @Test
    void setupMasterAdminManagement_adminRealm_skipCreation() {
        when(realm.getName()).thenReturn(Config.getAdminRealm());
        when(realmProvider.getRealmByName(anyString())).thenReturn(realm);
        when(realm.getClientByClientId(anyString())).thenReturn(client);
        RealmManager manager = new RealmManager(session);
        manager.setupMasterAdminManagement(realm);
        // the master admin client already exists, so it is only attached to the realm
        verify(realm).getClientByClientId(KeycloakModelUtils.getMasterRealmAdminManagementClientId(Config.getAdminRealm()));
        verify(realm).setMasterAdminClient(client);
    }

    // 14. importRealm various branches: skipUserDependent false, has adminPermissions
    @Test
    void importRealm_adminPermissionsClient() {
        when(rep.getRealm()).thenReturn("demo");
        when(rep.getAdminPermissionsClient()).thenReturn(mock(ClientRepresentation.class));
        RealmManager manager = new RealmManager(session);
        RealmModel imported = runImport(() -> manager.importRealm(rep, () -> {}), Profile.Feature.ADMIN_FINE_GRAINED_AUTHZ_V2);
        // Expect client creation logic called
        assertSame(realm, imported);
        verify(realm).setAdminPermissionsClient(client);
    }

    /**
     * Runs an import with the static default-setup helpers stubbed out, so that only the
     * RealmManager code itself is exercised. Everything the manager pulls off the session,
     * the realm and the clients is provided by the mocks below. The listed features are
     * reported as enabled, all the other ones stay disabled.
     */
    private RealmModel runImport(Supplier<RealmModel> importCall, Profile.Feature... enabledFeatures) {
        when(session.getContext()).thenReturn(keycloakContext);
        when(session.clients()).thenReturn(clientProvider);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        when(clientProvider.getClientByClientId(any(RealmModel.class), anyString())).thenReturn(client);
        when(realmProvider.getRealmByName(Config.getAdminRealm())).thenReturn(realm);
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
            repToModel.when(() -> RepresentationToModel.createClient(any(), any(), any())).thenReturn(client);
            protocolMappers.when(() -> ProtocolMapperUtils.findLocaleMapper(any())).thenReturn(new ProtocolMapperModel());
            for (Profile.Feature feature : enabledFeatures) {
                profile.when(() -> Profile.isFeatureEnabled(feature)).thenReturn(true);
            }

            return importCall.get();
        }
    }
}
