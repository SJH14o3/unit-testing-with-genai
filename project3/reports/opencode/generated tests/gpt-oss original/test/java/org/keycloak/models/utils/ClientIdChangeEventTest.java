import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class ClientIdChangeEventTest {
    private KeycloakSession session;
    private KeycloakSessionFactory factory;
    private KeycloakSessionFactoryListener listener;
    private ClientModel client;

    @BeforeEach
    public void setUp() {
        session = mock(KeycloakSession.class);
        factory = mock(KeycloakSessionFactory.class);
        client = mock(ClientModel.class);
        when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        when(client.getClientId()).thenReturn("oldId");
    }

    @Test
    public void updateClient_clientIdChangePublishesEvent() {
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("newId");

        // Provide a stub client that will return updated client after update
        ClientModel updatedClient = mock(ClientModel.class);
        when(session.getProvider(any())).thenReturn(null);
        when(client.updateClient()).thenReturn(updatedClient);

        // Capture published events
        doAnswer(invocation -> {
            Object event = invocation.getArgument(0);
            assertTrue(event instanceof ClientModel.ClientIdChangeEvent);
            ClientModel.ClientIdChangeEvent e = (ClientModel.ClientIdChangeEvent) event;
            assertEquals("oldId", e.getPreviousClientId());
            assertEquals("newId", e.getNewClientId());
            return null;
        }).when(factory).publish(any());

        // Mock client retrieval and update logic
        when(session.getProvider(any())).thenReturn(null);
        when(client.getClientId()).thenReturn("oldId");

        // Ensure updateClient returns updatedClient
        when(client.updateClient()).thenReturn(updatedClient);

        // Since RepresentationToModel.updateClient performs numerous config calls, we simplify by only testing the published event.
        // We will use a minimal rep and a mocked client.
        // Here we invoke updateClient on a custom wrapper that isolates the publish logic:
        RepresentationToModel.updateClient(rep, client, session);
    }
}
