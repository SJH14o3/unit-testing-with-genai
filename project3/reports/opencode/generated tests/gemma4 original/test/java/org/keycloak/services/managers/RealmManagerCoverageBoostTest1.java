package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.utils.DefaultAuthenticationFlows;
import org.keycloak.models.utils.DefaultClientScopes;
import org.keycloak.models.utils.DefaultRequiredActions;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.services.clientregistration.policy.DefaultClientRegistrationPolicies;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.sessions.UserSessionProvider;
import org.keycloak.transactions.TransactionManager;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmManagerCoverageBoostTest1 {

    private KeycloakSession session;
    private RealmProvider model;
    private RealmManager realmManager;
    private KeycloakSessionFactory sessionFactory;
    private TransactionManager transactionManager;
    private UserSessionProvider userSessionProvider;
    private AuthenticationSessionProvider authSessionProvider;
    private ClientPolicyManager clientPolicyManager;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        model = mock(RealmProvider.class);
        sessionFactory = mock(KeycloakSessionFactory.class);
        transactionManager = mock(TransactionManager.class);
        userSessionProvider = mock(UserSessionProvider.class);
        authSessionProvider = mock(AuthenticationSessionProvider.class);
        clientPolicyManager = mock(ClientPolicyManager.class);

        when(session.realms()).thenReturn(model);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.sessions()).thenReturn(userSessionProvider);
        when(session.authenticationSessions()).thenReturn(authSessionProvider);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);

        realmManager = new RealmManager(session);
    }

    @Test
    void testCreateRealm_WithId() {
        String id = "test-id";
        String name = "test-name";
        RealmModel realm = mock(RealmModel.class);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<ReservedCharValidator> validatorMock = Mockito.mockStatic(ReservedCharValidator.class)) {
            
            when(model.createRealm(id, name)).thenReturn(realm);

            RealmManager spyManager = spy(realmManager);
            // Stubbing setup methods to avoid side effects and focus on createRealm
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
            assertEquals(realm, result);
            verify(validatorMock).validate(id);
            verify(validatorMock).validate(name);
            verify(model).createRealm(id, name);
            verify(realm).setName(name);
            verify(spyManager).fireRealmPostCreate(realm);
        }
    }

    @Test
    void testSetupOfflineTokens_WithRep() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel offlineRole = mock(RoleModel.class);
        RealmRepresentation realmRep = mock(RealmRepresentation.class);
        RoleRepresentation roleRep = mock(RoleRepresentation.class);
        
        // Mocking the role container for realmRep.getRoles().getRealm()
        RoleRepresentation.RoleRepresentationContainer container = mock(RoleRepresentation.RoleRepresentationContainer.class);
        List<RoleRepresentation> rolesList = new ArrayList<>();
        rolesList.add(roleRep);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<DefaultClientScopes> scopesMock = Mockito.mockStatic(DefaultClientScopes.class)) {
            
            utilsMock.when(() -> KeycloakModelUtils.setupOfflineRole(realm)).thenReturn(offlineRole);
            when(realmRep.getRoles()).thenReturn(container);
            when(container.getRealm()).thenReturn(rolesList);
            when(roleRep.getName()).thenReturn(Constants.OFFLINE_ACCESS_ROLE);

            // Mock hasClientScope to return false so it enters the second if
            // But since hasClientScope is private, we'll check what happens
            // Actually, the code uses hasClientScope(realmRep, Constants.OFFLINE_ACCESS_ROLE)
            // We can't easily mock it without reflection or spy.
            // Let's use reflection to mock the private method or just use a spy.
            
            RealmManager spyManager = spy(realmManager);
            // We can't easily spy on private methods to return true/false without reflection.
            // Let's try to satisfy hasClientScope(realmRep, Constants.OFFLINE_ACCESS_ROLE)
            // by providing a client scope in the rep.
            
            ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
            scopeRep.setName(Constants.OFFLINE_ACCESS_ROLE);
            when(realmRep.getClientScopes()).thenReturn(Collections.singletonList(scopeRep));

            realmManager.setupOfflineTokens(realm, realmRep);

            verify(container).remove(roleRep);
            // Because we provided the scope, the second if (line 183) should be false.
            scopesMock.verify(() -> DefaultClientScopes.createOfflineAccessClientScope(any(), any()), never());
        }
    }

    @Test
    void testSetupBrokerService_Enabled() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        RoleModel role = mock(RoleModel.class);

        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.IDENTITY_BROKERING_API_V1)).thenReturn(true);
            when(realm.getClientByClientId(Constants.BROKER_SERVICE_CLIENT_ID)).thenReturn(null);
            utilsMock.when(() -> KeycloakModelUtils.createManagementClient(eq(realm), eq(Constants.BROKER_SERVICE_CLIENT_ID)))
                    .thenReturn(client);
            when(client.addRole(anyString())).thenReturn(role);

            realmManager.setupBrokerService(realm);

            verify(client).setEnabled(true);
            verify(client, atLeastOnce()).addRole(anyString());
        }
    }

    @Test
    void testSetupAccountManagement() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel accountClient = mock(ClientModel.class);
        RoleModel role = mock(RoleModel.class);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            when(realm.getClientByClientId(Constants.ACCOUNT_MANAGEMENT_CLIENT_ID)).thenReturn(accountClient);
            when(accountClient.addRole(anyString())).thenReturn(role);
            when(accountClient.getRole(anyString())).thenReturn(role);

            realmManager.setupAccountManagement(realm);

            verify(accountClient).setRootUrl(Constants.AUTH_BASE_URL_PROP);
            verify(accountClient, atLeastOnce()).addRole(anyString());
        }
    }

    @Test
    void testSetupRealmAdminManagement() {
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
            verify(realmAdminClient, atLeastOnce()).addRole(anyString());
        }
    }

    @Test
    void testSetDefaultsForNewRealm() {
        RealmModel realm = mock(RealmModel.class);
        
        realmManager.setDefaultsForNewRealm(realm);

        verify(realm).setBrowserSecurityHeaders(BrowserSecurityHeaders.realmDefaultHeaders);
        verify(realm).setBruteForceProtected(false);
        verify(realm).setSslRequired(SslRequired.EXTERNAL);
        verify(realm).setEventsListeners(Collections.singleton("jboss-logging"));
    }

    @Test
    void testFireRealmPostCreate() {
        RealmModel realm = mock(RealmModel.class);
        
        realmManager.fireRealmPostCreate(realm);

        verify(sessionFactory).publish(any(RealmModel.RealmPostCreateEvent.class));
    }
}
