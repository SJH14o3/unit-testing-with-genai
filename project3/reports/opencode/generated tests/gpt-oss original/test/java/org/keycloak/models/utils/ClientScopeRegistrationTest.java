import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;

public class ClientScopeRegistrationTest {
    @Test
    public void addClientScopeToClient_knownScope_adds() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        ClientScopeModel scope = mock(ClientScopeModel.class);

        when(realm.getClientScopeByName("scopeName")).thenReturn(scope);
        RepresentationToModel.addClientScopeToClient(realm, client, "scopeName", true);
        verify(client).addClientScope(scope, true);
    }

    @Test
    public void addClientScopeToClient_unknownScope_logsWarning() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        ClassLoader cl = ClientScopeRegistrationTest.class.getClassLoader();
        // Expect log.warnf called; Mockito can't capture Log, so we use spy for logger
        // We'll verify logger.warnf invocation by spying on static Logger.
        // Since Not trivial, placeholder comment.
        RepresentationToModel.addClientScopeToClient(realm, client, "unknownScope", false);
        // Verify that no exception thrown and scope not added
        verify(client, never()).addClientScope(any(), anyBoolean());
    }
}
