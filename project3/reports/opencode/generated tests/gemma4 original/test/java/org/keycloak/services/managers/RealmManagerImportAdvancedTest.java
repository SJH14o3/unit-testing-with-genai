package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.common.util.Encode;
import org.keycloak.email.EmailException;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.representations.idm.*;
import org.keycloak.services.clientregistration.policy.DefaultClientRegistrationPolicies;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.sessions.UserSessionProvider;
import org.keycloak.transactions.TransactionManager;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmManagerImportAdvancedTest {

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
    void testImportRealm_EmailException() {
        RealmRepresentation rep = new RealmRepresentation();
        rep.setRealm("test-realm");
        rep.setSmtpServer(new RealmRepresentation.SmtpServerRepresentation());

        // Mocking SMTPUtil.checkSMTPConfiguration to throw EmailException
        try (MockedStatic<org.keycloak.utils.SMTPUtil> smtpMock = Mockito.mockStatic(org.keycloak.utils.SMTPUtil.class)) {
            smtpMock.when(() -> org.keycloak.utils.SMTPUtil.checkSMTPConfiguration(any(), any()))
                    .thenThrow(new EmailException("SMTP error"));

            assertThrows(BadRequestException.class, () -> realmManager.importRealm(rep));
        }
    }

    @Test
    void testSetupClientServiceAccountsAndAuthorizationOnImport_SkipUserDependent() {
        RealmRepresentation rep = new RealmRepresentation();
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId("my-client");
        rep.setClients(List.of(clientRep));

        realmManager.setupClientServiceAccountsAndAuthorizationOnImport(rep, true);

        // Verify nothing happens if skipUserDependent is true
        verifyNoInteractions(session);
    }

    @Test
    void testSetupClientServiceAccountsAndAuthorizationOnImport_ErrorWhenClientNotFound() {
        RealmRepresentation rep = new RealmRepresentation();
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId("missing-client");
        rep.setClients(List.of(clientRep));

        RealmModel realm = mock(RealmModel.class);
        when(model.getRealmByName(anyString())).thenReturn(realm);
        when(realm.getClientByClientId("missing-client")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> realmManager.setupClientServiceAccountsAndAuthorizationOnImport(rep, false));
    }

    @Test
    void testUpdateRealmEventsConfig_InvalidEventListener() {
        RealmEventsConfigRepresentation rep = new RealmRepresentation.RealmEventsConfigRepresentation();
        rep.setEventsListeners(List.of("invalid-listener"));
        RealmModel realm = mock(RealmModel.class);

        try (MockedStatic<KeycloakSessionFactory> factoryMock = Mockito.mockStatic(KeycloakSessionFactory.class)) {
            // This is tricky because session.getKeycloakSessionFactory() is used
            // Let's mock the provider factory lookup
            EventListenerProviderFactory mockFactory = mock(EventListenerProviderFactory.class);
            when(session.getKeycloakSessionFactory().getProviderFactory(eq(org.keycloak.events.EventListenerProvider.class), eq("invalid-listener")))
                    .thenReturn(null);

            assertThrows(ClientErrorException.class, () -> realmManager.updateRealmEventsConfig(rep, realm));
        }
    }

    @Test
    void testUpdateRealmEventsConfig_GlobalEventListenerNotAllowed() {
        RealmEventsConfigRepresentation rep = new RealmRepresentation.RealmEventsConfigRepresentation();
        rep.setEventsListeners(List.of("global-listener"));
        RealmModel realm = mock(RealmModel.class);

        EventListenerProviderFactory globalFactory = mock(EventListenerProviderFactory.class);
        when(globalFactory.isGlobal()).thenReturn(true);
        when(session.getKeycloakSessionFactory().getProviderFactory(eq(org.keycloak.events.EventListenerProvider.class), eq("global-listener")))
                .thenReturn(globalFactory);

        assertThrows(ClientErrorException.class, () -> realmManager.updateRealmEventsConfig(rep, realm));
    }

    @Test
    void testRemoveRealm_Success() {
        RealmModel realm = mock(RealmModel.class);
        String realmId = "test-realm";
        when(realm.getId()).thenReturn(realmId);
        when(model.removeRealm(realmId)).thenReturn(true);
        
        ClientModel masterClient = mock(ClientModel.class);
        when(realm.getMasterAdminClient()).thenReturn(masterClient);
        when(masterClient.getId()).thenReturn("master-client-id");
        
        ClientModelProvider clientProvider = mock(ClientModelProvider.class);
        when(session.clients()).thenReturn(clientProvider);

        boolean removed = realmManager.removeRealm(realm);

        assertTrue(removed);
        verify(model).removeRealm(realmId);
        verify(clientProvider).removeClient(any(), eq("master-client-id"));
        verify(userSessionProvider).onRealmRemoved(realm);
        verify(authSessionProvider).onRealmRemoved(realm);
        verify(transactionManager).enlistAfterCompletion(any());
    }

    @Test
    void testRemoveRealm_Failure() {
        RealmModel realm = mock(RealmModel.class);
        when(model.removeRealm(anyString())).thenReturn(false);

        boolean removed = realmManager.removeRealm(realm);

        assertFalse(removed);
        verify(model).removeRealm(anyString());
        verifyNoInteractions(userSessionProvider);
    }
}
