import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.representations.idm.UserRepresentation;

public class ModelToRepresentationUserAttributesTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private UserModel user;

    @Mock
    private UserCredentialManager credMgr;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("toRepresentation setUserAttributes=false excludes username/email/etc")
    public void testNoAttributes() {
        when(user.getId()).thenReturn("user-no-attr");
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(false);
        when(user.getFederationLink()).thenReturn("link");
        when(user.getNotBefore()).thenReturn(0);
        when(user.getRequiredActionsStream()).thenReturn(Stream.empty());
        when(user.getAttributes()).thenReturn(Collections.emptyMap());
        when(user.getUsername()).thenReturn("ignored");
        when(user.getFirstName()).thenReturn("ignored");
        when(user.getLastName()).thenReturn("ignored");
        when(user.getEmail()).thenReturn("ignored");
        when(user.credentialManager()).thenReturn(credMgr);
        when(credMgr.isConfiguredFor(org.keycloak.models.OTPCredentialModel.TYPE)).thenReturn(false);
        when(credMgr.getDisableableCredentialTypesStream()).thenReturn(Stream.of("type1"));

        UserRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(session, realm, user, new UserRepresentation(), false);
        assertNull(rep.getUsername());
        assertNull(rep.getFirstName());
        assertNull(rep.getLastName());
        assertNull(rep.getEmail());
        assertEquals(1, rep.getDisableableCredentialTypes().size());
        assertFalse(rep.isTotp());
    }

    @Test
    @DisplayName("toRepresentation setUserAttributes=true includes username/email/etc but filters reserved keys")
    public void testIncludeAttributes() {
        when(user.getId()).thenReturn("user-include");
        when(user.getUsername()).thenReturn("alice");
        when(user.getFirstName()).thenReturn("Alice");
        when(user.getLastName()).thenReturn("Smith");
        when(user.getEmail()).thenReturn("alice@example.com");
        when(user.getAttributes()).thenReturn(new HashMap<>() {{
            put("custom", Collections.singletonList("value"));
            put("username", Collections.singletonList("alice"));
        }});
        when(user.isEnabled()).thenReturn(true);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getFederationLink()).thenReturn("link");
        when(user.getNotBefore()).thenReturn(0);
        when(user.getRequiredActionsStream()).thenReturn(Stream.empty());
        when(user.credentialManager()).thenReturn(credMgr);
        when(credMgr.isConfiguredFor(org.keycloak.models.OTPCredentialModel.TYPE)).thenReturn(false);
        when(credMgr.getDisableableCredentialTypesStream()).thenReturn(Stream.of("type1"));

        UserRepresentation rep = org.keycloak.models.utils.ModelToRepresentation.toRepresentation(session, realm, user, new UserRepresentation(), true);
        assertEquals("alice", rep.getUsername());
        assertEquals("Alice", rep.getFirstName());
        assertEquals("Smith", rep.getLastName());
        assertEquals("alice@example.com", rep.getEmail());
        assertEquals(1, rep.getAttributes().size());
        assertTrue(rep.getAttributes().containsKey("custom"));
        assertFalse(rep.getAttributes().containsKey("username"));
    }
}
