import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.events.*;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

@ExtendWith(MockitoExtension.class)
class RealmManagerUpdateEventsTest {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock KeycloakSessionFactory sessionFactory;
    @Mock EventListenerProviderFactory elpf;

    @BeforeEach
    void setup() {
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
    }

    @Test
    void updateRealmEventsConfig_updatesAllProperties() {
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        rep.setEventsEnabled(true);
        rep.setEnabledEventTypes(Set.of("login", "logout"));
        rep.setEventsListeners(List.of("listener1", "listener2"));
        rep.setEventsExpiration(200L);
        rep.setAdminEventsEnabled(true);
        rep.setAdminEventsDetailsEnabled(true);

        RealmManager manager = new RealmManager(session);
        manager.updateRealmEventsConfig(rep, realm);

        // Validate projected changes
        verify(realm).setEventsEnabled(true);
        verify(realm).setEventsExpiration(eq(200L));
        verify(realm).setEventsListeners(anySet());
        verify(realm).setEnabledEventTypes(anySet());
        verify(realm).setAdminEventsEnabled(true);
        verify(realm).setAdminEventsDetailsEnabled(true);
    }

    @Test
    void updateRealmEventsConfig_globalListener_throws() {
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        rep.setEventsListeners(List.of("jboss-logging");
        when(sessionFactory.getProviderFactory(EventListenerProvider.class, "jboss-logging"))
            .thenReturn(elpf);
        when(elpf.isGlobal()).thenReturn(true);
        RealmManager manager = new RealmManager(session);
        assertThrows(ClientErrorException.class, () -> manager.updateRealmEventsConfig(rep, realm));
    }

    @Test
    void updateRealmEventsConfig_unknownListener_throws() {
        RealmEventsConfigRepresentation rep = new RealmEventsConfigRepresentation();
        rep.setEventsListeners(List.of("unknown-listener"));
        when(sessionFactory.getProviderFactory(EventListenerProvider.class, "unknown-listener"))
            .thenReturn(null);
        RealmManager manager = new RealmManager(session);
        assertThrows(ClientErrorException.class, () -> manager.updateRealmEventsConfig(rep, realm));
    }
}
