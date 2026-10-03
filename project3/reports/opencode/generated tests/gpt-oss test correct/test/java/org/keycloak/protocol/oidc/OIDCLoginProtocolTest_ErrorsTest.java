package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.models.*;
import org.keycloak.events.EventBuilder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTest_ErrorsTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock
    UriInfo uriInfo;
    @Mock
    HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock
    AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock ClientModel client;
    @Mock KeycloakContext keycloakContext;
    @Mock KeycloakUriInfo keycloakUriInfo;
    @Mock RootAuthenticationSessionModel rootAuthSession;

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
        when(realm.getName()).thenReturn("test-realm");
        // Ensure a known client for build error redirect
        when(session.getContext()).thenReturn(keycloakContext);
        when(keycloakContext.getClient()).thenReturn(client);
        when(keycloakContext.getRealm()).thenReturn(realm);
        when(keycloakContext.getUri()).thenReturn(keycloakUriInfo);
        when(keycloakUriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:8080"));
        // The tab id has to be removed from the parent authentication session
        when(authSession.getParentSession()).thenReturn(rootAuthSession);
        when(rootAuthSession.getAuthenticationSessions())
                .thenReturn(Map.of("other-tab", authSession));
    }

    @Test
    @DisplayName("sendError maps CANCELLED_AIA to ACCESS_DENIED")
    void testCancelAIAError() {
        Response res = protocol.sendError(authSession, LoginProtocol.Error.CANCELLED_AIA, "cancelled");
        assertNotNull(res, "Response should not be null");
        String loc = String.valueOf(res.getMetadata().getFirst("Location"));
        assertNotNull(loc, "Location header should exist");
        assertTrue(loc.contains("error=access_denied"), "Error must be access_denied");
        assertTrue(loc.contains("error_description=User+cancelled+application-initiated+action."), "Description should match");
    }

    @Test
    @DisplayName("error PASIVE_INTERACTION_REQUIRED maps to interaction_required")
    void testPassiveInteraction() {
        Response res = protocol.sendError(authSession, LoginProtocol.Error.PASSIVE_INTERACTION_REQUIRED, "ignored");
        String loc = String.valueOf(res.getMetadata().getFirst("Location"));
        assertTrue(loc.contains("error=interaction_required"), "Error mapping incorrect");
    }
}
