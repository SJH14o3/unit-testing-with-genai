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
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.models.*;
import org.keycloak.protocol.ClientData;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.OAuth2ErrorRepresentation;
import org.keycloak.services.Urls;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.ImplicitHybridTokenResponse;
import org.keycloak.protocol.oidc.endpoints.checker.AuthorizationEndpointChecker;
import org.keycloak.protocol.oidc.endpoints.checker.AuthorizationEndpointChecker.AuthorizationCheckException;
import org.keycloak.protocol.oidc.utils.OIDCProviderConfig;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.protocol.oidc.utils.OIDCResponseType;

@ExtendWith(MockitoExtension.class)
class OIDCLoginProtocolTest_ErrorClientData {
    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UriInfo uriInfo;
    @Mock HttpHeaders headers;
    @Mock EventBuilder event;
    @Mock ClientModel client;
    @Mock ClientData clientData;
    @Mock AuthenticationSessionModel authSession;
    @Mock UserSessionModel userSession;
    @Mock ClientSessionContext clientSessionCtx;

    OIDCLoginProtocol protocol;

    @BeforeEach
    void init() {
        protocol = new OIDCLoginProtocol(session, realm, uriInfo, headers, event);
        when(clientData.getRedirectUri()).thenReturn("http://redirect.uri");
        when(clientData.getResponseType()).thenReturn(OIDCResponseType.CODE.toString());
        when(clientData.getResponseMode()).thenReturn(OIDCResponseMode.QUERY.toString());
        when(clientData.getState()).thenReturn("state123");
        when(session.getProvider(eq(org.keycloak.connections.httpclient.HttpClientProvider.class)))
                .thenReturn(mock(org.keycloak.connections.httpclient.HttpClientProvider.class));
        // Stub session context client for client checker
        when(session.getContext()).thenReturn(mock(KeycloakSessionContext.class));
        when(session.getContext().getClient()).thenReturn(client);
    }

    @Test
    @DisplayName("sendError with clientData uses checker and returns error params")
    void testSendErrorWithClientData() {
        // Mock AuthorizationEndpointChecker to succeed
        try (var checkerMock = mockStatic(AuthorizationEndpointChecker.class)) {
            checkerMock.when(() -> AuthorizationEndpointChecker.isValid(any()))
                    .thenReturn(true);
            // Also mock static OIDCAdvancedConfigWrapper.fromClientModel to return default config that does not exclude issuer
            try (var configMock = mockStatic(OIDCAdvancedConfigWrapper.class)) {
                OIDCAdvancedConfigWrapper fakeConfig = mock(OIDCAdvancedConfigWrapper.class);
                when(fakeConfig.isExcludeIssuerFromAuthResponse()).thenReturn(false);
                configMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(any(ClientModel.class)))
                        .thenReturn(fakeConfig);

                // Execute
                Response res = protocol.sendError(client, clientData, Error.INVALID_CLIENT_AIA);
                assertNotNull(res, "Response should not be null");
                String loc = res.getMetadata().getFirst("Location");
                assertNotNull(loc, "Location header should exist");
                assertTrue(loc.contains("error=invalid_client"), "Error mapping should be invalid_client");
                assertTrue(loc.contains("error_description=User+cancelled+application-initiated+action."), "Description expected for CANCELLED_AIA policy instead of default");
            }
        }
    }

    @Test
    @DisplayName("sendError with clientData throws checker exception leads to error page")
    void testSendErrorCheckerException() {
        try (var checkerMock = mockStatic(AuthorizationEndpointChecker.class)) {
            checkerMock.when(() -> AuthorizationEndpointChecker.checkResponseType(any())).thenThrow(new AuthorizationCheckException("bad type"));
            // Expect the exception to be caught and forwarded to error page
            Response res = protocol.sendError(client, clientData, Error.SERVER_ERROR);
            assertNotNull(res, "Response should not be null");
            String loc = res.getMetadata().getFirst("Location");
            assertTrue(loc.contains("error=server_error"), "error param should be server_error");
            // Assertion that error_description present but may be null
        }
    }
}
