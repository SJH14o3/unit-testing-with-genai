import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.OAuth2Constants;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.ClientData;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.Details;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolTest_Errors {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock ClientModel client;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void init() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(authSession.getClient()).thenReturn(client);
        when(authSession.getRedirectUri()).thenReturn("http://redirect.uri");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_TYPE_PARAM)))
                .thenReturn("code");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.RESPONSE_MODE_PARAM)))
                .thenReturn("query");
        when(authSession.getClientNote(eq(OIDCLoginProtocol.STATE_PARAM)))
                .thenReturn("state123");
        // Ensure a known client for build error redirect
        when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
        when(session.getContext().getClient()).thenReturn(client);
        when(session.getContext().getUri()).thenReturn(mock(UriInfo.class));
    }

    @Test
    @DisplayName("sendError maps CANCELLED_AIA to ACCESS_DENIED")
    void testCancelAIAError() {
        Response res = protocol.sendError(authSession, Error.CANCELLED_AIA, "cancelled");
        assertNotNull(res, "Response should not be null");
        String loc = res.getMetadata().getFirst("Location");
        assertNotNull(loc, "Location header should exist");
        assertTrue(loc.contains("error=access_denied"), "Error must be access_denied");
        assertTrue(loc.contains("error_description=User+cancelled+application-initiated+action."), "Description should match");
    }

    @Test
    @DisplayName("error PASIVE_INTERACTION_REQUIRED maps to interaction_required")
    void testPassiveInteraction() {
        Response res = protocol.sendError(authSession, Error.PASSIVE_INTERACTION_REQUIRED, "ignored");
        String loc = res.getMetadata().getFirst("Location");
        assertTrue(loc.contains("error=interaction_required"), "Error mapping incorrect");
    }
}
