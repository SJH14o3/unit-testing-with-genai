import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class ImportRolesExceptionTest {

    @Test
    public void importRoles_missingClient() {
        RealmModel realmMock = mock(RealmModel.class);
        RolesRepresentation rolesRep = new RolesRepresentation();
        Map<String, List<RoleRepresentation>> clientRoles = new HashMap<>();
        clientRoles.put("nonexistentClient", List.of(new RoleRepresentation()));
        rolesRep.setClient(clientRoles);
        Exception ex = assertThrows(RuntimeException.class, () ->
                RepresentationToModel.importRoles(rolesRep, realmMock));
        assertTrue(ex.getMessage().contains("nonexistentClient"));
    }

    @Test
    public void importRoles_compositeRealmRoleMissing() {
        RealmModel realmMock = mock(RealmModel.class);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("parentRole");
        roleRep.setId("id1");
        // Add composite realm role that doesn't exist
        RoleCompositeRepresentation composite = new RoleCompositeRepresentation();
        composite.setRealm(List.of("missingRealmRole"));
        roleRep.setComposites(composite);
        // mock getRole returns null for missing role
        when(realmMock.getRole("missingRealmRole")).thenReturn(null);

        RolesRepresentation rolesRep = new RolesRepresentation();
        rolesRep.setRealm(List.of(roleRep));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                RepresentationToModel.importRoles(rolesRep, realmMock));
        assertTrue(ex.getMessage().contains("Unable to find composite realm role"));
    }

    @Test
    public void importRoles_compositeClientRoleMissing() {
        RealmModel realmMock = mock(RealmModel.class);
        // Client exists
        ClientModel clientMock = mock(ClientModel.class);
        when(realmMock.getClientByClientId("clientA")).thenReturn(clientMock);
        // client addRole returns null to simulate missing? Actually client.getRole returns null
        when(clientMock.getRole("missingClientRole")).thenReturn(null);

        RoleRepresentation roleRep = new RoleRepresentation();
        RoleCompositeRepresentation composite = new RoleCompositeRepresentation();
        Map<String, List<String>> clientMap = new HashMap<>();
        clientMap.put("clientA", List.of("missingClientRole"));
        composite.setClient(clientMap);
        roleRep.setComposites(composite);

        RolesRepresentation rolesRep = new RolesRepresentation();
        rolesRep.setRealm(List.of(roleRep));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                RepresentationToModel.importRoles(rolesRep, realmMock));
        assertTrue(ex.getMessage().contains("Unable to find composite client role"));
    }
}
