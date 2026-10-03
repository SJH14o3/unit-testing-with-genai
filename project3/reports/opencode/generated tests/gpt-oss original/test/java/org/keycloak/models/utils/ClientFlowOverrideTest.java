import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class ClientFlowOverrideTest {

    private KeycloakSession session;
    private RealmModel realmMock;
    private ClientModel clientMock;
    private AuthenticationFlowModel flowMock;

    @BeforeEach
    public void setUp() {
        session = mock(KeycloakSession.class);
        realmMock = mock(RealmModel.class);
        clientMock = mock(ClientModel.class);
        flowMock = mock(AuthenticationFlowModel.class);
        when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
        when(session.getContext().getRealm()).thenReturn(realmMock);
    }

    @Test
    public void createClient_withMappedFlow() {
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("clientId");
        Map<String, String> overrides = new HashMap<>();
        overrides.put("flowIdOriginal", "flowIdMapped");
        rep.setAuthenticationFlowBindingOverrides(overrides);

        Map<String, String> mappedFlows = new HashMap<>();
        mappedFlows.put("flowIdOriginal", "flowIdMapped");

        when(realmMock.getAuthenticationFlowById("flowIdMapped")).thenReturn(flowMock);

        // Mock client creation
        when(realmMock.addClient("clientId")).thenReturn(clientMock);
        // After createClient, verify that binding override set
        RepresentationToModel.createClient(session, realmMock, rep, mappedFlows);

        verify(clientMock).setAuthenticationFlowBindingOverride("flowIdOriginal", "flowIdMapped");
    }

    @Test
    public void updateClient_withFlowMapping() {
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("clientId");
        Map<String, String> overrides = new HashMap<>();
        overrides.put("flowId", "newFlowId");
        rep.setAuthenticationFlowBindingOverrides(overrides);

        when(realmMock.getAuthenticationFlowById("newFlowId")).thenReturn(flowMock);
        when(clientMock.getRealm()).thenReturn(realmMock);

        RepresentationToModel.updateClient(rep, clientMock, session);
        verify(clientMock).setAuthenticationFlowBindingOverride("flowId", "newFlowId");
    }
}
