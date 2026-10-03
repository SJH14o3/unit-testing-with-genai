import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.sessions.*;
import org.keycloak.storage.*;
import org.keycloak.events.*;
import org.keycloak.models.*;
import org.keycloak.models.utils.*;
import org.keycloak.storage.StoreSyncEvent;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RealmManagerRemoveTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider provider;
    @Mock RealmModel realm;
    @Mock ClientModel masterClient;
    @Mock UserSessionProvider userSessionProvider;
    @Mock AuthenticationSessionProvider authSessionProvider;
    @Mock TransactionManager transactionManager;
    @Mock ClientProvider clientProvider;
    @Mock KeycloakSessionFactory sessionFactory;

    @BeforeEach
    void setup() {
        when(session.realms()).thenReturn(provider);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.clients()).thenReturn(clientProvider);
        when(provider.removeRealm(anyString())).thenReturn(true);
        when(realm.getMasterAdminClient()).thenReturn(masterClient);
        when(realm.getId()).thenReturn("realm-id");
        when(masterClient.getId()).thenReturn("admin-client-id");
    }

    @Test
    void removeRealm_successfulRemovesClientsAndPublishesEvent() {
        RealmManager manager = new RealmManager(session);
        boolean removed = manager.removeRealm(realm);
        assertTrue(removed);
        verify(provider).removeRealm("realm-id");
        verify(clientProvider).removeClient(eq(realm), eq("admin-client-id"));
        // Capture transaction and trigger commit
        ArgumentCaptor<AbstractKeycloakTransaction> captor = ArgumentCaptor.forClass(AbstractKeycloakTransaction.class);
        verify(transactionManager).enlistAfterCompletion(captor.capture());
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
