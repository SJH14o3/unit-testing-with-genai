import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;
import org.mockito.MockedStatic;

public class UpdateClientPropertiesTest {
    private ClientModel clientMock;
    private ClientRepresentation rep;

    @BeforeEach
    public void setUp() {
        clientMock = mock(ClientModel.class);
    }

    @Test
    public void updateClientProperties_allFieldsPresent() {
        rep = new ClientRepresentation();
        rep.setClientId("clientX");
        rep.setEnabled(true);
        rep.setProtocol("openid-connect");
        rep.setAttributes(new HashMap<>());
        RepresentationToModel.updateClientProperties(clientMock, rep, true);
        // Verify core setters called
        verify(clientMock).setName(rep.getName());
        verify(clientMock).setEnabled(true);
        verify(clientMock).setProtocol("openid-connect");
    }

    @Test
    public void updateClientProperties_missingOptionalFields() {
        rep = new ClientRepresentation();
        rep.setClientId("clientY");
        // only mandatory fields
        RepresentationToModel.updateClientProperties(clientMock, rep, false);
        verify(clientMock).setClientId("clientY");
    }

    @Test
    public void updateClientProperties_defaultValuesApplied() {
        // When protocol is null, default should be applied via DEFAULT_PROTOCOL
        rep = new ClientRepresentation();
        rep.setClientId("clientZ");
        try (MockedStatic<Profile> profile = mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.CLIENT_TYPES)).thenReturn(false);
            RepresentationToModel.updateClientProperties(clientMock, rep, false);
        }
        verify(clientMock).setProtocol("openid-connect");
    }
}
