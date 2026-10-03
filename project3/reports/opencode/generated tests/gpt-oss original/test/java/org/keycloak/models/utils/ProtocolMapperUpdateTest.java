import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class ProtocolMapperUpdateTest {

    @Test
    public void updateClientProtocolMappers_addNewMapper() {
        ClientModel client = mock(ClientModel.class);
        // No existing mappers returned
        when(client.getProtocolMappersStream()).thenReturn(java.util.stream.Stream.of());

        ProtocolMapperRepresentation rep = new ProtocolMapperRepresentation();
        rep.setName("newMapper");
        rep.setProtocol("openid-connect");
        rep.setProtocolMapper("mapperClass");

        List<ProtocolMapperRepresentation> list = List.of(rep);
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setProtocolMappers(list);

        // Capture added mapper via argument captor
        ArgumentCaptor<ProtocolMapperModel> cap = ArgumentCaptor.forClass(ProtocolMapperModel.class);
        RepresentationToModel.updateClientProtocolMappers(clientRep, client);

        verify(client, atLeastOnce()).addProtocolMapper(cap.capture());
        ProtocolMapperModel added = cap.getValue();
        assertEquals("newMapper", added.getName());
    }

    @Test
    public void updateClientProtocolMappers_updateExistingMapper() {
        ProtocolMapperModel existing = mock(ProtocolMapperModel.class);
        when(existing.getName()).thenReturn("existingMapper");
        when(existing.getProtocol()).thenReturn("openid-connect");
        when(existing.getId()).thenReturn("id1");
        List<ProtocolMapperModel> existingList = List.of(existing);
        ClientModel client = mock(ClientModel.class);
        when(client.getProtocolMappersStream()).thenReturn(existingList.stream());

        ProtocolMapperRepresentation rep = new ProtocolMapperRepresentation();
        rep.setName("existingMapper");
        rep.setProtocol("openid-connect");
        rep.setProtocolMapper("mapperClass");
        List<ProtocolMapperRepresentation> list = List.of(rep);
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setProtocolMappers(list);

        RepresentationToModel.updateClientProtocolMappers(clientRep, client);
        verify(client).updateProtocolMapper(any(ProtocolMapperModel.class));
        verify(client, never()).removeProtocolMapper(any());
    }

    @Test
    public void updateClientProtocolMappers_removeOrphanedMapper() {
        ProtocolMapperModel orphan = mock(ProtocolMapperModel.class);
        when(orphan.getName()).thenReturn("orphanMapper");
        when(orphan.getProtocol()).thenReturn("openid-connect");
        List<ProtocolMapperModel> list = List.of(orphan);
        ClientModel client = mock(ClientModel.class);
        when(client.getProtocolMappersStream()).thenReturn(list.stream());

        ClientRepresentation rep = new ClientRepresentation();
        rep.setProtocolMappers(new ArrayList<>()); // empty list

        RepresentationToModel.updateClientProtocolMappers(rep, client);
        verify(client).removeProtocolMapper(orphan);
    }
}
