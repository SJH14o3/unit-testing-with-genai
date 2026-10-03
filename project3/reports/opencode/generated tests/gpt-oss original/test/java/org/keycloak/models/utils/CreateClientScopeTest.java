import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
        ClientScopeModel scope = RepresentationToModel.createClientScope(realm, rep);
        assertNotNull(scope);
        verify(realm).addClientScope("cs1", "scope1");
    }
}
