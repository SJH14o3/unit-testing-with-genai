import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.keycloak.common.Profile;
import org.keycloak.models.ClientModel;
import org.keycloak.representations.idm.ClientRepresentation;

public class UpdateClientPropertiesEdgeTest {
    @Test
    public void clientTypeExceptionAggregation() {
        ClientModel client = mock(ClientModel.class);
        ClientRepresentation rep = new ClientRepresentation();
        // force two setters to throw ClientTypeException
        doThrow(new org.keycloak.authorization.model.ClientTypeException("err1", new String[]{"a"})).when(client).setEnabled(anyBoolean());
        doThrow(new org.keycloak.authorization.model.ClientTypeException("err2", new String[]{"b"})).when(client).setStandardFlowEnabled(anyBoolean());

        Exception ex = assertThrows(org.keycloak.authorization.model.ClientTypeException.class, () ->
                RepresentationToModel.updateClientProperties(client, rep, true));

        String msg = ex.getMessage();
        assertTrue(msg.contains("err1") && msg.contains("err2"));
    }

    @Test
    public void defaultFullScopeAllowedLogic() {
        ClientModel client = mock(ClientModel.class);
        when(client.isFullScopeAllowed()).thenReturn(true);
        when(client.isConsentRequired()).thenReturn(true);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setFullScopeAllowed(null); // to trigger default
        // Use mocked Profile feature false to avoid client type paths
        try (MockedStatic<Profile> ps = mockStatic(Profile.class)) {
            ps.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);
            RepresentationToModel.updateClientProperties(client, rep, true);
        }
        verify(client).setFullScopeAllowed(anyBoolean());
    }

    @Test
    public void defaultNodeReRegistrationTimeoutLogic() {
        ClientModel client = mock(ClientModel.class);
        when(client.getNodeReRegistrationTimeout()).thenReturn(0);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setNodeReRegistrationTimeout(null);
        try (MockedStatic<Profile> ps = mockStatic(Profile.class)) {
            ps.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);
            RepresentationToModel.updateClientProperties(client, rep, true);
        }
        verify(client).setNodeReRegistrationTimeout(-1);
    }

    @Test
    public void defaultWebOriginsFallback() {
        ClientModel client = mock(ClientModel.class);
        when(client.getRedirectUris()).thenReturn(List.of("http://example.com/callback"));
        when(client.getWebOrigins()).thenReturn(null);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setWebOrigins(null);
        try (MockedStatic<Profile> ps = mockStatic(Profile.class)) {
            ps.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);
            RepresentationToModel.updateClientProperties(client, rep, true);
        }
        verify(client).setWebOrigins(anySet());
    }
}
