package org.keycloak.protocol.oidc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventBuilder;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.models.*;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OIDCLoginProtocolTest_ErrorClientDataTest {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock
    UriInfo uriInfo;
    @Mock
    HttpHeaders headers;
    @Mock
    EventBuilder event;
    @Mock ClientModel client;
    @Mock ClientData clientData;
    @Mock
    AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;
    @Mock KeycloakContext keycloakContext;
    @Mock KeycloakUriInfo keycloakUriInfo;
    @Mock OIDCProviderConfig providerConfig;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void init() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(clientData.getRedirectUri()).thenReturn("http://redirect.uri");
        when(clientData.getResponseType()).thenReturn(OIDCResponseType.CODE.toString());
        when(clientData.getResponseMode()).thenReturn(OIDCResponseMode.QUERY.value());
        when(clientData.getState()).thenReturn("state123");
        // Stub the client so that AuthorizationEndpointChecker accepts the request
        when(client.getClientId()).thenReturn("my-client");
        when(client.isStandardFlowEnabled()).thenReturn(true);
        when(client.getRedirectUris()).thenReturn(Set.of("http://redirect.uri"));
        when(realm.getName()).thenReturn("test-realm");
        // isAllowOidcParamsInRedirectUris() resolves the protocol config from the session
        when(providerConfig.isAllowOidcParamsInRedirectUris()).thenReturn(false);
        when(session.getProvider(eq(LoginProtocol.class), eq(OIDCLoginProtocol.LOGIN_PROTOCOL)))
                .thenReturn(new OIDCLoginProtocol(providerConfig));
        // Stub session context client for client checker
        when(session.getContext()).thenReturn(keycloakContext);
        when(keycloakContext.getClient()).thenReturn(client);
        when(keycloakContext.getRealm()).thenReturn(realm);
        when(keycloakContext.getUri()).thenReturn(keycloakUriInfo);
        when(keycloakUriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:8080"));
    }

    @Test
    @DisplayName("sendError with clientData uses checker and returns error params")
    void testSendErrorWithClientData() {
        // Also mock static OIDCAdvancedConfigWrapper.fromClientModel to return default config that does not exclude issuer
        try (var configMock = mockStatic(OIDCAdvancedConfigWrapper.class)) {
            OIDCAdvancedConfigWrapper fakeConfig = mock(OIDCAdvancedConfigWrapper.class);
            when(fakeConfig.isExcludeIssuerFromAuthResponse()).thenReturn(false);
            configMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(any(ClientModel.class)))
                    .thenReturn(fakeConfig);

            // Execute
            Response res = protocol.sendError(client, clientData, LoginProtocol.Error.LOA_INVALID);
            assertNotNull(res, "Response should not be null");
            String loc = String.valueOf(res.getMetadata().getFirst("Location"));
            assertNotNull(loc, "Location header should exist");
            assertTrue(loc.contains("error=access_denied"), "Error mapping should be access_denied");
            // LOA_INVALID is translated without an error description
            assertFalse(loc.contains("error_description="), "No error_description expected for LOA_INVALID");
            assertTrue(loc.contains("state=state123"), "State should be propagated");
        }
    }

    @Test
    @DisplayName("sendError with clientData for CANCELLED_AIA_SILENT stays silent")
    void testSendErrorCheckerException() {
        Response res = protocol.sendError(client, clientData, LoginProtocol.Error.CANCELLED_AIA_SILENT);
        assertNotNull(res, "Response should not be null");
        String loc = String.valueOf(res.getMetadata().getFirst("Location"));
        // CANCELLED_AIA_SILENT is translated to an empty error representation
        assertFalse(loc.contains("error="), "No error param expected for a silent cancel");
        assertFalse(loc.contains("error_description="), "No error_description expected for a silent cancel");
        assertTrue(loc.contains("state=state123"), "State should be propagated");
    }
}
