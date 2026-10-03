package org.keycloak.services.managers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.ws.rs.ClientErrorException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RealmManagerUpdateEventsTest {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock KeycloakSessionFactory sessionFactory;
    @Mock EventListenerProviderFactory elpf;

    @Test
    void updateRealmEventsConfig_updatesAllProperties() {
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getProviderFactory(eq(EventListenerProvider.class), anyString())).thenReturn(elpf);
        when(elpf.isGlobal()).thenReturn(false);

        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        rep.setEventsEnabled(true);
        rep.setEnabledEventTypes(List.of("login", "logout"));
        rep.setEventsListeners(List.of("listener1", "listener2"));
        rep.setEventsExpiration(200L);
        rep.setAdminEventsEnabled(true);
        rep.setAdminEventsDetailsEnabled(true);

        RealmManager manager = new RealmManager(session);
        manager.updateRealmEventsConfig(rep, realm);

        // Validate projected changes
        verify(realm).setEventsEnabled(true);
        verify(realm).setEventsExpiration(eq(200L));
        verify(realm).setEventsListeners(eq(new java.util.HashSet<>(List.of("listener1", "listener2"))));
        verify(realm).setEnabledEventTypes(eq(new java.util.HashSet<>(List.of("login", "logout"))));
        verify(realm).setAdminEventsEnabled(true);
        verify(realm).setAdminEventsDetailsEnabled(true);
    }

    @Test
    void updateRealmEventsConfig_globalListener_throws() {
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getProviderFactory(EventListenerProvider.class, "jboss-logging"))
            .thenReturn(elpf);
        when(elpf.isGlobal()).thenReturn(true);
        RealmManager manager = new RealmManager(session);
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        rep.setEventsListeners(List.of("jboss-logging"));
        assertThrows(ClientErrorException.class, () -> manager.updateRealmEventsConfig(rep, realm));
    }

    @Test
    void updateRealmEventsConfig_unknownListener_throws() {
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getProviderFactory(EventListenerProvider.class, "unknown-listener"))
            .thenReturn(null);
        RealmManager manager = new RealmManager(session);
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        rep.setEventsListeners(List.of("unknown-listener"));
        assertThrows(ClientErrorException.class, () -> manager.updateRealmEventsConfig(rep, realm));
    }
}
