package org.keycloak.models.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.common.Profile;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class CreateClientTest {

    @BeforeAll
    public static void initProfile() {
        if (Profile.getInstance() == null) {
            Profile.configure();
        }
    }

    @Test
    public void createClient_basic() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        ClientRepresentation rep = new ClientRepresentation();
        rep.setClientId("client1");
        when(realm.addClient("client1")).thenReturn(mock(ClientModel.class));
        ClientModel client = RepresentationToModel.createClient(session, realm, rep);
        assertNotNull(client);
    }

    // disabled client type test placeholder

}