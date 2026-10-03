import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.RealmRepresentation;

public class UpdateRealmTest {
    @Test
    public void updateRealm_delegates() {
        KeycloakSession session = mock(KeycloakSession.class);
        DatastoreProvider ds = mock(DatastoreProvider.class);
        ExportImportManager mgr = mock(ExportImportManager.class);
        when(session.getProvider(DatastoreProvider.class)).thenReturn(ds);
        when(ds.getExportImportManager()).thenReturn(mgr);

        RealmModel realm = mock(RealmModel.class);
        RealmRepresentation rep = new RealmRepresentation();

        RepresentationToModel.updateRealm(rep, realm, session);
        verify(mgr).updateRealm(rep, realm);
    }
}
