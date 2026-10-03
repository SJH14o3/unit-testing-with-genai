import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class AddCompositesTest {

    @Test
    public void addComposites_realmOnly() {
        RoleModel role = mock(RoleModel.class);
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("parent");
        rep.setId("id1");
        Set<RoleRepresentation> compositesSet = new HashSet<>();
        RoleRepresentation child = new RoleRepresentation();
        child.setName("child");
        compositesSet.add(child);
        RoleCompositeRepresentation comps = new RoleCompositeRepresentation();
        comps.setRealm(Collections.singletonList("child"));
        rep.setComposites(comps);

        RealmModel realm = mock(RealmModel.class);
        RoleModel childRole = mock(RoleModel.class);
        when(realm.getRole("child")).thenReturn(childRole);

        RepresentationToModel.addComposites(role, rep, realm);
        verify(role).addCompositeRole(childRole);
    }

    @Test
    public void addComposites_clientComposites() {
        RoleModel role = mock(RoleModel.class);
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("parent");
        RoleCompositeRepresentation comps = new RoleCompositeRepresentation();
        Map<String, List<String>> clientMap = new HashMap<>();
        clientMap.put("clientX", List.of("roleC"));
        comps.setClient(clientMap);
        rep.setComposites(comps);

        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        RoleModel clientRole = mock(RoleModel.class);
        when(realm.getClientByClientId("clientX")).thenReturn(client);
        when(client.getRole("roleC")).thenReturn(clientRole);

        RepresentationToModel.addComposites(role, rep, realm);
        verify(role).addCompositeRole(clientRole);
    }

    @Test
    public void addComposites_missingRealmRole_throws() {
        RoleModel role = mock(RoleModel.class);
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("parent");
        RoleCompositeRepresentation comps = new RoleCompositeRepresentation();
        comps.setRealm(List.of("notExist"));
        rep.setComposites(comps);

        RealmModel realm = mock(RealmModel.class);
        when(realm.getRole("notExist")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                RepresentationToModel.addComposites(role, rep, realm));
        assertTrue(ex.getMessage().contains("Unable to find composite realm role"));
    }

    @Test
    public void addComposites_missingClient_throws() {
        RoleModel role = mock(RoleModel.class);
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("parent");
        RoleCompositeRepresentation comps = new RoleCompositeRepresentation();
        Map<String, List<String>> clientMap = new HashMap<>();
        clientMap.put("badClient", List.of("roleC"));
        comps.setClient(clientMap);
        rep.setComposites(comps);

        RealmModel realm = mock(RealmModel.class);
        when(realm.getClientByClientId("badClient")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                RepresentationToModel.addComposites(role, rep, realm));
        assertTrue(ex.getMessage().contains("App doesn't exist in role definitions"));
    }
}
