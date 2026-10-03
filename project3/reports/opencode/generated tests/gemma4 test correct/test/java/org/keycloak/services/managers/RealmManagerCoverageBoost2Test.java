package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.Profile;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.*;
import org.keycloak.models.utils.RepresentationToModel;
import org.keycloak.representations.idm.*;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerCoverageBoost2Test {

    private KeycloakSession session;
    private RealmProvider model;
    private RealmManager realmManager;
    private KeycloakSessionFactory sessionFactory;
    private KeycloakTransactionManager transactionManager;
    private UserSessionProvider userSessionProvider;
    private AuthenticationSessionProvider authSessionProvider;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        model = mock(RealmProvider.class);
        sessionFactory = mock(KeycloakSessionFactory.class);
        transactionManager = mock(KeycloakTransactionManager.class);
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
    void testHasClient_AllTypes() throws Exception {
        // 1. Clients
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("c1");

        // 2. Applications
        ApplicationRepresentation app = new ApplicationRepresentation();
        app.setName("a1");

        // 3. OAuth Clients
        OAuthClientRepresentation oauth = new OAuthClientRepresentation();
        oauth.setName("o1");

        // RealmRepresentation has no setters for the legacy applications/oauthClients lists, so stub them.
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getClients()).thenReturn(List.of(client));
        when(rep.getApplications()).thenReturn(List.of(app));
        when(rep.getOauthClients()).thenReturn(List.of(oauth));

        assertTrue((Boolean) invokePrivateMethod("hasClient", rep, "c1"));
        assertTrue((Boolean) invokePrivateMethod("hasClient", rep, "a1"));
        assertTrue((Boolean) invokePrivateMethod("hasClient", rep, "o1"));

        // 4. Not found
        assertFalse((Boolean) invokePrivateMethod("hasClient", rep, "none"));

        // 5. Null clientId in a client representation is skipped
        ClientRepresentation noId = new ClientRepresentation();
        when(rep.getClients()).thenReturn(List.of(noId));
        assertFalse((Boolean) invokePrivateMethod("hasClient", rep, "none"));
    }

    @Test
    void testUpdateRealmEventsConfig_FullBranching() throws Exception {
        RealmModel realm = mock(RealmModel.class);
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        
        // Test branch: Events enabled, expiration provided, listeners provided
        rep.setEventsEnabled(true);
        rep.setEventsExpiration(100L);
        rep.setEventsListeners(List.of("listener1"));
        rep.setEnabledEventTypes(List.of("TYPE1"));
        rep.setAdminEventsEnabled(true);
        rep.setAdminEventsDetailsEnabled(true);

        // Mock provider factory for listener
        EventListenerProviderFactory mockFactory = mock(EventListenerProviderFactory.class);
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
        rep.setRealm("test-realm");
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId("client-id");
        clientRep.setAuthorizationServicesEnabled(true);
        rep.setClients(List.of(clientRep));

        RealmModel realm = mock(RealmModel.class);
        ClientModel clientModel = mock(ClientModel.class);
        UserModel serviceAccount = mock(UserModel.class);

        when(model.getRealmByName("test-realm")).thenReturn(realm);
        when(realm.getClientByClientId("client-id")).thenReturn(clientModel);
        when(clientModel.isServiceAccountsEnabled()).thenReturn(true);
        UserProvider users = mock(UserProvider.class);
        when(session.users()).thenReturn(users);
        when(users.getServiceAccount(clientModel)).thenReturn(serviceAccount);

        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class);
             MockedStatic<RepresentationToModel> repToModelMock = Mockito.mockStatic(RepresentationToModel.class)) {

            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.AUTHORIZATION)).thenReturn(true);

            realmManager.setupClientServiceAccountsAndAuthorizationOnImport(rep, false);

            verify(clientModel, atLeastOnce()).isServiceAccountsEnabled();
            repToModelMock.verify(() -> RepresentationToModel.createResourceServer(clientModel, session, false));
            repToModelMock.verify(() -> RepresentationToModel.importAuthorizationSettings(clientRep, clientModel, session));
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
