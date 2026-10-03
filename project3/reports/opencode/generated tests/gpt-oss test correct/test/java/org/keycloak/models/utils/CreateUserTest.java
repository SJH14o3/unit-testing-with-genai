package org.keycloak.models.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;
import org.keycloak.storage.DatastoreProvider;
import org.keycloak.storage.ExportImportManager;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateUserTest {
    @Test
    public void createUser_basic() {
        KeycloakSession session = mock(KeycloakSession.class);
        DatastoreProvider ds = mock(DatastoreProvider.class);
        ExportImportManager mgr = mock(ExportImportManager.class);
        when(session.getProvider(DatastoreProvider.class)).thenReturn(ds);
        when(ds.getExportImportManager()).thenReturn(mgr);

        RealmModel realm = mock(RealmModel.class);
        UserRepresentation rep = new UserRepresentation();
        rep.setUsername("bob");

        UserModel expected = mock(UserModel.class);
        when(mgr.createUser(realm, rep)).thenReturn(expected);

        UserModel created = RepresentationToModel.createUser(session, realm, rep);
        assertNotNull(created);
        assertSame(expected, created);
        verify(mgr).createUser(eq(realm), eq(rep));
    }
}
