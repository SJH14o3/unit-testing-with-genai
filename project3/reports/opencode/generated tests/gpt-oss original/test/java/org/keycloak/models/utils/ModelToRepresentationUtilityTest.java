import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.models.RealmModel;
import org.keycloak.models.listeners.ResourceServerListener;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.AuthDetailsRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.models.utils.ModelToRepresentation;
import org.keycloak.representations.idm.authorization.CredentialMetadataRepresentation;
import org.keycloak.representations.idm.authorization.CredentialRepresentation;
import org.keycloak.models.CredentialModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.CredentialMetadata;
import org.keycloak.models.CredentialMetadata.LocalizedMessage;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.AuthorizationProvider;
import org.keycloak.authorization.AuthorizationStore;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.store.ResourceServerStore;

public class ModelToRepresentationUtilityTest {

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("toRepresentation(Event) copies all fields")
    public void testEventRepresentation() {
        Event evt = mock(Event.class);
        when(evt.getId()).thenReturn("e1");
        when(evt.getTime()).thenReturn(1234L);
        when(evt.getType()).thenReturn(org.keycloak.events.EventType.LOGIN);
        when(evt.getRealmId()).thenReturn("realm");
        when(evt.getClientId()).thenReturn("client");
        when(evt.getUserId()).thenReturn("user");
        when(evt.getSessionId()).thenReturn("sess");
        when(evt.getIpAddress()).thenReturn("127.0.0.1");
        when(evt.getError()).thenReturn("err");
        when(evt.getDetails()).thenReturn(Collections.emptyMap());
        EventRepresentation rep = ModelToRepresentation.toRepresentation(evt);
        assertEquals("e1", rep.getId());
        assertEquals(1234L, rep.getTime());
        assertEquals("LOGIN", rep.getType());
        assertEquals("realm", rep.getRealmId());
    }

    @Test
    @DisplayName("toRepresentation(AdminEvent) handles null details and authDetails")
    public void testAdminEventRepresentation() {
        AdminEvent admin = mock(AdminEvent.class);
        when(admin.getId()).thenReturn("a1");
        when(admin.getTime()).thenReturn(5678L);
        when(admin.getRealmId()).thenReturn("realm");
        when(admin.getAuthDetails()).thenReturn(null);
        when(admin.getOperationType()).thenReturn(org.keycloak.events.admin.OperationType.CREATE);
        when(admin.getResourceTypeAsString()).thenReturn("resource");
        when(admin.getResourcePath()).thenReturn("/path");
        when(admin.getRepresentation()).thenReturn("rep");
        when(admin.getError()).thenReturn("err");
        when(admin.getDetails()).thenReturn(Collections.emptyMap());

        AdminEventRepresentation rep = ModelToRepresentation.toRepresentation(admin);
        assertEquals("a1", rep.getId());
        assertEquals(5678L, rep.getTime());
        assertEquals("CREATE", rep.getOperationType());
        assertEquals("resource", rep.getResourceType());
    }

    @Test
    @DisplayName("toRepresentation(AuthDetails) correct mapping")
    public void testAuthDetails() {
        AuthDetails details = mock(AuthDetails.class);
        when(details.getRealmId()).thenReturn("realm");
        when(details.getClientId()).thenReturn("client");
        when(details.getUserId()).thenReturn("user");
        when(details.getIpAddress()).thenReturn("1.1.1.1");
        AuthDetailsRepresentation rep = ModelToRepresentation.toRepresentation(details);
        assertEquals("realm", rep.getRealmId());
        assertEquals("client", rep.getClientId());
    }

    @Test
    @DisplayName("toEventsConfigReprensetation respects expiration flag")
    public void testEventsConfig() {
        RealmModel realm = mock(RealmModel.class);
        when(realm.isEventsEnabled()).thenReturn(true);
        when(realm.getEventsExpiration()).thenReturn(42);
        when(realm.getEventsListenersStream()).thenReturn(Collections.emptyStream());
        when(realm.getEnabledEventTypesStream()).thenReturn(Collections.emptyStream());
        when(realm.isAdminEventsEnabled()).thenReturn(false);
        when(realm.isAdminEventsDetailsEnabled()).thenReturn(false);
        RealmEventsConfigRepresentation rep = ModelToRepresentation.toEventsConfigReprensetation(realm);
        assertTrue(rep.isEventsEnabled());
        assertEquals(42, rep.getEventsExpiration());
    }

    @Test
    @DisplayName("filterValidRepresentations skips invalid models")
    public void testFilterValid() {
        java.util.stream.Stream<String> stream = java.util.stream.Stream.of("ok", "bad");
        java.util.stream.Stream<String> result = ModelToRepresentation.filterValidRepresentations(stream, s -> {
            if("bad".equals(s)) throw new org.keycloak.models.ModelIllegalStateException("bad");
            return s.toUpperCase();
        });
        java.util.List<String> list = result.toList();
        assertTrue(list.contains("OK"));
        assertFalse(list.contains("BAD"));
    }

    @Test
    @DisplayName("toRepresentation(CredentialMetadata) handles nested structures")
    public void testCredentialMetadata() {
        CredentialModel credModel = mock(CredentialModel.class);
        when(credModel.getId()).thenReturn("id");
        when(credModel.getType()).thenReturn("password");
        when(credModel.getUserLabel()).thenReturn("label");
        when(credModel.getCreatedDate()).thenReturn(1000L);
        when(credModel.getSecretData()).thenReturn("secret");
        when(credModel.getCredentialData()).thenReturn("data");
        when(credModel.getFederationLink()).thenReturn("link");
        CredentialMetadata metadata = mock(CredentialMetadata.class);
        when(metadata.getCredentialModel()).thenReturn(credModel);
        LocalizedMessage info = new LocalizedMessage("key", new String[]{"p1"});
        when(metadata.getInfoMessage()).thenReturn(info);
        when(metadata.getInfoProperties()).thenReturn(Collections.emptyList());
        when(metadata.getWarningMessageDescription()).thenReturn(info);
        when(metadata.getWarningMessageTitle()).thenReturn(info);
        when(metadata.getIconLight()).thenReturn("light");
        when(metadata.getIconDark()).thenReturn("dark");

        CredentialMetadataRepresentation rep = ModelToRepresentation.toRepresentation(metadata);
        assertEquals("password", rep.getInfoMessage().getKey());
    }
}
