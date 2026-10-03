package org.keycloak.models.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class CreateClientScopeTest {

    @Test
    public void createClientScope_regular() {
        RealmModel realm = mock(RealmModel.class);
        ClientScopeRepresentation rep = new ClientScopeRepresentation();
        rep.setId("cs1");
        rep.setName("scope1");
        rep.setDescription("desc");
        rep.setProtocol("openid-connect");
        List<ProtocolMapperRepresentation> mappers = new ArrayList<>();
        rep.setProtocolMappers(mappers);
        ClientScopeModel scopeMock = mock(ClientScopeModel.class);
        when(realm.addClientScope("cs1", "scope1")).thenReturn(scopeMock);
        // two separate streams: the mapper removal pass and the migration pass
        when(scopeMock.getProtocolMappersStream()).thenReturn(Stream.empty(), Stream.empty());
        ClientScopeModel scope = RepresentationToModel.createClientScope(realm, rep);
        assertNotNull(scope);
        assertSame(scopeMock, scope);
        verify(realm).addClientScope("cs1", "scope1");
        verify(scopeMock).setName("scope1");
        verify(scopeMock).setDescription("desc");
        verify(scopeMock).setProtocol("openid-connect");
    }
}
