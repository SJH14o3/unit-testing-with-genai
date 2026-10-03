package org.keycloak.models.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.RepresentationToModel;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.storage.DatastoreProvider;
import org.keycloak.storage.ExportImportManager;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ImportRealmTest {

    @Test
    public void importRealm_delegatesToExportImportManager() {
        KeycloakSession session = mock(KeycloakSession.class);
        DatastoreProvider dsProvider = mock(DatastoreProvider.class);
        ExportImportManager manager = mock(ExportImportManager.class);

        RealmModel newRealm = mock(RealmModel.class);
        Runnable importHook = mock(Runnable.class);
        RealmRepresentation rep = new RealmRepresentation();

        when(session.getProvider(DatastoreProvider.class)).thenReturn(dsProvider);
        when(dsProvider.getExportImportManager()).thenReturn(manager);

        RepresentationToModel.importRealm(session, rep, newRealm, importHook);

        verify(manager).importRealm(rep, newRealm, importHook);
    }
}
