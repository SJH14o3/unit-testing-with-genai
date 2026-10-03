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
import org.keycloak.OAuth2Constants;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.ClientData;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.AuthenticationSessionModel;
import org.keycloak.models.ClientSessionContext;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolGetClientDataTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock AuthenticationSessionModel authSession;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void setup() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
    }

    @Test
    @DisplayName("getClientData returns correct fields when notes present")
    void testGetClientData() {
        String redirect = "http://redirect";
        String respType = "code";
        String respMode = "query";
        String state = "xyz";
        when(authSession.getRedirectUri()).thenReturn(redirect);
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)).thenReturn(respType);
        when(authSession.getClientNote(OIDCLoginProtocol.RESPONSE_MODE_PARAM)).thenReturn(respMode);
        when(authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM)).thenReturn(state);
        ClientData data = protocol.getClientData(authSession);
        assertEquals(redirect, data.getRedirectUri());
        assertEquals(respType, data.getResponseType());
        assertEquals(respMode, data.getResponseMode());
        assertEquals(state, data.getState());
    }
}
