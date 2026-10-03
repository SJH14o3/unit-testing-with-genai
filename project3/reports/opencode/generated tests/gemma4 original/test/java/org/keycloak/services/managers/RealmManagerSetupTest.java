package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.common.util.Encode;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.utils.DefaultAuthenticationFlows;
import org.keycloak.models.utils.DefaultClientScopes;
import org.keycloak.models.utils.DefaultRequiredActions;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.oidc.OIDCConfigAttributes;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.OIDCLoginProtocolFactory;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.services.clientregistration.policy.DefaultClientRegistrationPolicies;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.utils.StringUtil;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmManagerSetupTest {

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
    void testSetupRealmDefaults() {
        RealmModel realm = mock(RealmModel.class);
        RealmRepresentation realmRep = null;

        realmManager.setupRealmDefaults(realm, realmRep);

        verify(realm).setBrowserSecurityHeaders(BrowserSecurityHeaders.realmDefaultHeaders);
        verify(realm).setBruteForceProtected(false);
        verify(realm).setSslRequired(SslRequired.EXTERNAL);
        verify(realm).setOTPPolicy(OTPPolicy.DEFAULT_POLICY);
        verify(realm).setLoginWithEmailAllowed(true);
        verify(realm).setEventsListeners(Collections.singleton("jboss-logging"));
    }

    @Test
    void testSetupRealmDefaultsWithFeatureEnabled() {
        RealmModel realm = mock(RealmModel.class);
        RealmRepresentation realmRep = mock(RealmRepresentation.class);
        
        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.OID4VC_VCI_REST_CREDENTIAL_OFFER)).thenReturn(true);
            when(realmRep.getRoles()).thenReturn(mock(RoleRepresentation.RoleRepresentationContainer.class)); // This is not right, need to mock properly
            // Let's fix the mock for realmRep.getRoles().getRealm()
            RoleRepresentation.RoleRepresentationContainer roleContainer = mock(RoleRepresentation.RoleRepresentationContainer.class);
            when(realmRep.getRoles()).thenReturn(roleContainer);
            when(roleContainer.getRealm()).thenReturn(Collections.emptyList());
            when(realm.getRole(anyString())).thenReturn(null);
            
            realmManager.setupRealmDefaults(realm, realmRep);
            
            verify(realm).addRole(anyString());
        }
    }

    @Test
    void testSetupAuthenticationFlows() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<DefaultAuthenticationFlows> flowsMock = Mockito.mockStatic(DefaultAuthenticationFlows.class)) {
            when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
            
            realmManager.setupAuthenticationFlows(realm);
            
            flowsMock.verify(() -> DefaultAuthenticationFlows.addFlows(realm));
        }
    }

    @Test
    void testSetupRequiredActions() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<DefaultRequiredActions> actionsMock = Mockito.mockStatic(DefaultRequiredActions.class)) {
            when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
            
            realmManager.setupRequiredActions(realm);
            
            actionsMock.verify(() -> DefaultRequiredActions.addActions(realm));
        }
    }

    @Test
    void testSetupOfflineTokens_NoRep() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel offlineRole = mock(RoleModel.class);
        
        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<DefaultClientScopes> scopesMock = Mockito.mockStatic(DefaultClientScopes.class)) {
            
            utilsMock.when(() -> KeycloakModelUtils.setupOfflineRole(realm)).thenReturn(offlineRole);
            
            realmManager.setupOfflineTokens(realm, null);
            
            verify(utilsMock).setupOfflineRole(realm);
            scopesMock.verify(() -> DefaultClientScopes.createOfflineAccessClientScope(realm, offlineRole));
        }
    }

    @Test
    void testSetupOfflineTokens_WithRepAndRoleRemoved() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel offlineRole = mock(RoleModel.class);
        RealmRepresentation realmRep = mock(RealmRepresentation.class);
        RoleRepresentation roleRep = mock(RoleRepresentation.class);
        RoleRepresentation.RoleRepresentationContainer roleContainer = mock(RoleRepresentation.RoleRepresentationContainer.class);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<DefaultClientScopes> scopesMock = Mockito.mockStatic(DefaultClientScopes.class)) {
            
            utilsMock.when(() -> KeycloakModelUtils.setupOfflineRole(realm)).thenReturn(offlineRole);
            when(realmRep.getRoles()).thenReturn(roleContainer);
            when(roleContainer.getRealm()).thenReturn(List.of(roleRep));
            when(roleRep.getName()).thenReturn(Constants.OFFLINE_ACCESS_ROLE);
            
            // If it has the role, it falls into the first if block (lines 172-181)
            // and then the second if block (lines 183) might or might not be entered.
            // If it has the role, it won't trigger the second if unless it doesn't have the client scope.
            when(realmRep.getClientScopes()).thenReturn(Collections.emptyList());

            realmManager.setupOfflineTokens(realm, realmRep);

            verify(roleContainer).remove(roleRep);
            scopesMock.verify(() -> DefaultClientScopes.createOfflineAccessClientScope(realm, offlineRole));
        }
    }

    @Test
    void testCreateDefaultClientScopes() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<EntityManagers> entityManagersMock = Mockito.mockStatic(EntityManagers.class);
             MockedStatic<DefaultClientScopes> clientScopesMock = Mockito.mockStatic(DefaultClientScopes.class)) {
            
            entityManagersMock.when(() -> EntityManagers.runInBatch(eq(session), any(), eq(false)))
                    .thenAnswer(invocation -> {
                        Runnable runnable = invocation.getArgument(1);
                        runnable.run();
                        return null;
                    });

            realmManager.createDefaultClientScopes(realm);
            clientScopesMock.verify(() -> DefaultClientScopes.createDefaultClientScopes(session, realm, true));
        }
    }

    @Test
    void testSetupAdminConsole_CreateNew() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel adminConsole = mock(ClientModel.class);
        
        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            when(realm.getClientByClientId(Constants.ADMIN_CONSOLE_CLIENT_ID)).thenReturn(null);
            utilsMock.when(() -> KeycloakModelUtils.createPublicClient(eq(realm), eq(Constants.ADMIN_CONSOLE_CLIENT_ID)))
                    .thenReturn(adminConsole);

            realmManager.setupAdminConsole(realm);

            verify(adminConsole).setName("${client_" + Constants.ADMIN_CONSOLE_CLIENT_ID + "}");
            verify(adminConsole).setEnabled(true);
            verify(adminConsole).setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        }
    }

    @Test
    void testSetupAdminConsole_Existing() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel adminConsole = mock(ClientModel.class);
        when(realm.getClientByClientId(Constants.ADMIN_CONSOLE_CLIENT_ID)).thenReturn(adminConsole);

        realmManager.setupAdminConsole(realm);

        verify(adminConsole).setName("${client_" + Constants.ADMIN_CONSOLE_CLIENT_ID + "}");
        verify(adminConsole).setEnabled(true);
    }

    @Test
    void testSetupAdminConsoleLocaleMapper_New() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel adminConsole = mock(ClientModel.class);
        ProtocolMapperModel localeMapper = mock(ProtocolMapperModel.class);
        
        // Need to mock session.clients()
        ClientModelProvider clientsProvider = mock(ClientModelProvider.class);
        when(session.clients()).thenReturn(clientsProvider);
        when(clientsProvider.getClientByClientId(realm, Constants.ADMIN_CONSOLE_CLIENT_ID)).thenReturn(adminConsole);
        when(adminConsole.getProtocolMapperByName(anyString(), anyString())).thenReturn(null);

        try (MockedStatic<ProtocolMapperUtils> utilsMock = Mockito.mockStatic(ProtocolMapperUtils.class);
             MockedStatic<KeycloakSessionFactory> factoryMock = Mockito.mockStatic(KeycloakSessionFactory.class)) {
            
            utilsMock.when(() -> ProtocolMapperUtils.findLocaleMapper(session)).thenReturn(localeMapper);

            realmManager.setupAdminConsoleLocaleMapper(realm);

            verify(adminConsole).addProtocolMapper(localeMapper);
        }
    }

    @Test
    void testSetupAdminCli_CreateNew() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel adminCli = mock(ClientModel.class);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            when(realm.getClientByClientId(Constants.ADMIN_CLI_CLIENT_ID)).thenReturn(null);
            utilsMock.when(() -> KeycloakModelUtils.createPublicClient(eq(realm), eq(Constants.ADMIN_CLI_CLIENT_ID)))
                    .thenReturn(adminCli);

            realmManager.setupAdminCli(realm);

            verify(adminCli).setEnabled(true);
            verify(adminCli).setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        }
    }

    @Test
    void testSetupBrokerService_Disabled() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class)) {
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.IDENTITY_BROKERING_API_V1)).thenReturn(false);

            realmManager.setupBrokerService(realm);

            verifyNoInteractions(realm);
        }
    }

    @Test
    void testSetupBrokerService_EnabledAndCreate() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);

        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.IDENTITY_BROKERING_API_V1)).thenReturn(true);
            when(realm.getClientByClientId(Constants.BROKER_SERVICE_CLIENT_ID)).thenReturn(null);
            utilsMock.when(() -> KeycloakModelUtils.createManagementClient(eq(realm), eq(Constants.BROKER_SERVICE_CLIENT_ID)))
                    .thenReturn(client);
            
            RoleModel role = mock(RoleModel.class);
            when(client.addRole(anyString())).thenReturn(role);

            realmManager.setupBrokerService(realm);

            verify(client).setEnabled(true);
            verify(client).setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
            verify(client, atLeastOnce()).addRole(anyString());
        }
    }

    @Test
    void testSetupAccountManagement_CreateNew() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel accountClient = mock(ClientModel.class);
        RoleModel role = mock(RoleModel.class);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class)) {
            
            when(realm.getClientByClientId(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID)).thenReturn(null);
            utilsMock.when(() -> KeycloakModelUtils.createPublicClient(eq(realm), eq(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID)))
                    .thenReturn(accountClient);
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.OID4VC_VCI)).thenReturn(false);
            
            when(accountClient.addRole(anyString())).thenReturn(role);
            when(accountClient.getRole(anyString())).thenReturn(role);

            realmManager.setupAccountManagement(realm);

            verify(accountClient).setRootUrl(Constants.AUTH_BASE_URL_PROP);
            verify(accountClient).addRole(AccountRoles.MANAGE_ACCOUNT_LINKS);
        }
    }

    @Test
    void testSetupImpersonationService() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<ImpersonationConstants> impersonationMock = Mockito.mockStatic(ImpersonationConstants.class)) {
            realmManager.setupImpersonationService(realm);
            impersonationMock.verify(() -> ImpersonationConstants.setupImpersonationService(session, realm));
        }
    }

    @Test
    void testSetupAuthorizationServices() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            realmManager.setupAuthorizationServices(realm);
            utilsMock.verify(() -> KeycloakModelUtils.setupAuthorizationServices(realm));
        }
    }

    @Test
    void testSetupClientRegistrations() {
        RealmModel realm = mock(RealmModel.class);
        try (MockedStatic<DefaultClientRegistrationPolicies> policiesMock = Mockito.mockStatic(DefaultClientRegistrationPolicies.class)) {
            realmManager.setupClientRegistrations(realm);
            policiesMock.verify(() -> DefaultClientRegistrationPolicies.addDefaultPolicies(realm));
        }
    }
}
