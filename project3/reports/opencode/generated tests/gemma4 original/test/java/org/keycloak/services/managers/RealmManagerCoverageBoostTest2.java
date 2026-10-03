package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmManagerCoverageBoostTest2 {

    private KeycloakSession session;
    private RealmProvider model;
    private RealmManager realmManager;
    private KeycloakSessionFactory sessionFactory;
    private TransactionManager transactionManager;
    private UserSessionProvider userSessionProvider;
    private AuthenticationSessionProvider authSessionProvider;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        model = mock(RealmProvider.class);
        sessionFactory = mock(KeycloakSessionFactory.class);
        transactionManager = mock(TransactionManager.class);
        userSessionProvider = mock(UserSessionProvider.class);
        authSessionProvider = mock(AuthenticationSessionProvider.class);

        when(session.realms()).thenReturn(model);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.sessions()).thenReturn(userSessionProvider);
        when(session.authenticationSessions()).thenReturn(authSessionProvider);

        realmManager = new RealmManager(session);
    }

    @Test
    void testImportRealm_FullFlow_Success() throws Exception {
        RealmRepresentation rep = new RealmRepresentation();
        rep.setRealm("new-realm");
        rep.setId("new-id");
        
        RealmModel realm = mock(RealmModel.class);
        when(model.createRealm("new-id", "new-realm")).thenReturn(realm);
        
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);

        try (MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class);
             MockedStatic<ReservedCharValidator> validatorMock = Mockito.mockStatic(ReservedCharValidator.class);
             MockedStatic<SMTPUtil> smtpMock = Mockito.mockStatic(org.keycloak.utils.SMTPUtil.class);
             MockedStatic<RepresentationToModel> repToModelMock = Mockito.mockStatic(RepresentationToModel.class)) {

            // Mocking branches
            when(rep.getAttributesOrEmpty()).thenReturn(Collections.emptyMap());
            when(rep.getClientScopes()).thenReturn(Collections.emptyList());
            when(rep.getAdminPermissionsClient()).thenReturn(null);
            when(rep.isAdminPermissionsEnabled()).thenReturn(false);
            when(rep.getRoles()).thenReturn(null);
            when(rep.getKeycloakVersion()).thenReturn("25.0.0");

            // Spy to skip heavy/complex internal calls during import flow test
            RealmManager spyManager = spy(realmManager);
            doNothing().when(spyManager).setupRealmDefaults(any(), any());
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
            doNothing().when(spyManager).setupClientServiceAccountsAndAuthorizationOnImport(any(), any());
            doNothing().when(spyManager).fireRealmPostCreate(any());
            doNothing().when(spyManager).checkMasterAdminManagementRoles(any());
            doNothing().when(spyManager).checkRealmAdminManagementRoles(any());

            RealmModel result = spyManager.importRealm(rep, () -> {});

            assertNotNull(result);
            verify(model).createRealm("new-id", "new-realm");
        }
    }

    @Test
    void testDetermineDefaultRoleName_WithConflicts() throws Exception {
        RealmRepresentation rep = new RealmRepresentation();
        rep.setRealm("conflict-realm");
        
        // Mocking hasRealmRole via reflection or spy
        RealmManager spyManager = spy(realmManager);
        
        // Case 1: No conflict
        doReturn(false).when(spyManager).hasRealmRole(any(), anyString());
        String name1 = (String) invokePrivateMethod("determineDefaultRoleName", rep);
        assertEquals("default-", name1.substring(0, 8)) || assertTrue(name1.contains("default-conflict-realm"));

        // Case 2: Conflict exists
        doReturn(true).when(spyManager).hasRealmRole(any(), eq("default-conflict-realm"));
        // We need to mock the subsequent checks in the loop
        doReturn(true).when(spyManager).hasRealmRole(any(), eq("default-conflict-realm-1"));
        doReturn(false).when(spyManager).hasRealmRole(any(), eq("default-conflict-realm-2"));

        String name2 = (String) invokePrivateMethod("determineDefaultRoleName", rep);
        assertEquals("default-conflict-realm-2", name2);
    }

    @Test
    void testHasClient_AllTypes() throws Exception {
        RealmRepresentation rep = new RealmRepresentation();
        
        // 1. Clients
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("c1");
        rep.setClients(List.of(client));
        assertTrue((Boolean) invokePrivateMethod("hasClient", rep, "c1"));

        // 2. Applications
        ApplicationRepresentation app = new ApplicationRepresentation();
        app.setName("a1");
        rep.setApplications(List.of(app));
        assertTrue((Boolean) invokePrivateMethod("hasClient", rep, "a1"));

        // 3. OAuth Clients
        OAuthClientRepresentation oauth = new OAuthClientRepresentation();
        oauth.setName("o1");
        rep.setOauthClients(List.of(oauth));
        assertTrue((Boolean) invokePrivateMethod("hasClient", rep, "o1"));

        // 4. Not found
        assertFalse((Boolean) invokePrivateMethod("hasClient", rep, "none"));
    }

    @Test
    void testUpdateRealmEventsConfig_FullBranching() throws Exception {
        RealmModel realm = mock(RealmModel.class);
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        
        // Test branch: Events enabled, expiration provided, listeners provided
        rep.setEventsEnabled(true);
        rep.setEventsExpiration(100);
        rep.setEventsListeners(List.of("listener1"));
        rep.setEnabledEventTypes(List.of("TYPE1"));
        rep.setAdminEventsEnabled(true);
        rep.setAdminEventsDetailsEnabled(true);

        // Mock provider factory for listener
        EventListenerProviderFactory mockFactory = mock(EventListenerProvider.class, withSettings().extraInterfaces(EventListenerProviderFactory.class));
        when(session.getKeycloakSessionFactory().getProviderFactory(eq(org.keycloak.events.EventListenerProvider.class), eq("listener1")))
                .thenReturn((EventListenerProviderFactory) mockFactory);
        when(mockFactory.isGlobal()).thenReturn(false);

        realmManager.updateRealmEventsConfig(rep, realm);

        verify(realm).setEventsEnabled(true);
        verify(realm).setEventsExpiration(100);
        verify(realm).setEventsListeners(anySet());
        verify(realm).setEnabledEventTypes(anySet());
        verify(realm).setAdminEventsEnabled(true);
        verify(realm).setAdminEventsDetailsEnabled(true);
    }

    @Test
    void testSetupClientServiceAccountsAndAuthorizationOnImport_Success() throws Exception {
        RealmRepresentation rep = new RealmRepresentation();
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId("client-id");
        clientRep.setAuthorizationServicesEnabled(true);
        rep.setClients(List.of(clientRep));

        RealmModel realm = mock(RealmModel.class);
        ClientModel clientModel = mock(ClientModel.class);
        UserModel serviceAccount = mock(UserModel.class);
        
        when(model.getRealmByName(anyString())).thenReturn(realm);
        when(realm.getClientByClientId("client-id")).thenReturn(clientModel);
        when(clientModel.isServiceAccountsEnabled()).thenReturn(true);
        when(session.users().getServiceAccount(clientModel)).thenReturn(serviceAccount);

        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class);
             MockedStatic<RepresentationToModel> repToModelMock = Mockito.mockStatic(RepresentationToModel.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.AUTHORIZATION)).thenReturn(true);

            realmManager.setupClientServiceAccountsAndAuthorizationOnImport(rep, false);

            verify(clientModel, atLeastOnce()).isServiceAccountsEnabled();
        }
    }

    private Object invokePrivateMethod(String methodName, Object... args) throws Exception {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof RealmRepresentation) parameterTypes[i] = RealmRepresentation.class;
            else if (args[i] instanceof String) parameterTypes[i] = String.class;
            else if (args[i] instanceof Boolean) parameterTypes[i] = boolean.class;
            else parameterTypes[i] = args[i].getClass();
        }
        
        // Manual correction for primitive boolean if needed
        if (methodName.equals("postponeMasterClientSetup") || methodName.equals("hasRealmAdminManagementClient") || 
            methodName.equals("hasAccountManagementClient") || methodName.equals("hasBrokerClient") || 
            methodName.equals("hasAdminConsoleClient") || methodName.equals("hasAdminCliClient")) {
            parameterTypes[0] = RealmRepresentation.class;
        }

        Method method = RealmManager.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(realmManager, args);
    }
}
