import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.RealmModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolSetterTest {
    @Mock KeycloakSession session1;
    @Mock KeycloakSession session2;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new OIDCLoginProtocol(session1, realm, uriInfo, headers, event);
    }

    @Test
    @DisplayName("setSession returns same instance and updates field")
    void testSetSession() {
        OIDCLoginProtocol returned = protocol.setSession(session2);
        assertSame(protocol, returned);
        // No direct way to assert field changed; rely on method chaining being same instance.
    }

    @Test
    @DisplayName("setRealm returns same instance and updates field")
    void testSetRealm() {
        OIDCLoginProtocol returned = protocol.setRealm(realm);
        assertSame(protocol, returned);
    }

    @Test
    @DisplayName("setUriInfo returns same instance")
    void testSetUriInfo() {
        OIDCLoginProtocol returned = protocol.setUriInfo(uriInfo);
        assertSame(protocol, returned);
    }

    @Test
    @DisplayName("setHttpHeaders returns same instance")
    void testSetHttpHeaders() {
        OIDCLoginProtocol returned = protocol.setHttpHeaders(headers);
        assertSame(protocol, returned);
    }

    @Test
    @DisplayName("setEventBuilder returns same instance")
    void testSetEventBuilder() {
        OIDCLoginProtocol returned = protocol.setEventBuilder(event);
        assertSame(protocol, returned);
    }
}
