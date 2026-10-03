package org.keycloak.services.managers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.models.AbstractKeycloakTransaction;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserSessionProvider;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.storage.StoreSyncEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerRemoveTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider provider;
    @Mock RealmModel realm;
    @Mock RealmModel adminRealm;
    @Mock ClientModel masterClient;
    @Mock UserSessionProvider userSessionProvider;
    @Mock AuthenticationSessionProvider authSessionProvider;
    @Mock KeycloakTransactionManager transactionManager;
    @Mock ClientProvider clientProvider;
    @Mock KeycloakSessionFactory sessionFactory;

    @BeforeEach
    void setup() {
        when(session.realms()).thenReturn(provider);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.clients()).thenReturn(clientProvider);
        when(session.sessions()).thenReturn(userSessionProvider);
        when(session.authenticationSessions()).thenReturn(authSessionProvider);
        when(provider.removeRealm(anyString())).thenReturn(true);
        when(realm.getMasterAdminClient()).thenReturn(masterClient);
        when(realm.getId()).thenReturn("realm-id");
        when(masterClient.getId()).thenReturn("admin-client-id");
        when(provider.getRealmByName(Config.getAdminRealm())).thenReturn(adminRealm);
    }

    @Test
    void removeRealm_successfulRemovesClientsAndPublishesEvent() {
        RealmManager manager = new RealmManager(session);
        boolean removed = manager.removeRealm(realm);
        assertTrue(removed);
        verify(provider).removeRealm("realm-id");
        verify(clientProvider).removeClient(eq(adminRealm), eq("admin-client-id"));
        verify(userSessionProvider).onRealmRemoved(realm);
        verify(authSessionProvider).onRealmRemoved(realm);
        // Capture transaction and trigger commit
        ArgumentCaptor<AbstractKeycloakTransaction> captor = ArgumentCaptor.forClass(AbstractKeycloakTransaction.class);
        verify(transactionManager).enlistAfterCompletion(captor.capture());
        captor.getValue().begin();
        captor.getValue().commit();
        verify(sessionFactory).publish(any(StoreSyncEvent.class));
    }

    @Test
    void removeRealm_notRemoved_noSideEffects() {
        when(provider.removeRealm(anyString())).thenReturn(false);
        RealmManager manager = new RealmManager(session);
        boolean removed = manager.removeRealm(realm);
        assertFalse(removed);
        verify(clientProvider, never()).removeClient(any(), anyString());
        verify(transactionManager, never()).enlistAfterCompletion(any());
    }
}
