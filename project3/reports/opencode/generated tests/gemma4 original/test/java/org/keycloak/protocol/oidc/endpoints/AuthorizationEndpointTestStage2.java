package org.keycloak.protocol.oidc.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.Uri;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.OIDCResponseMode;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.services.ClientPolicyException;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.services.clientpolicy.context.PreAuthorizationRequestContext;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class AuthorizationEndpointTestStage2 {

    @Mock
    private KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private KeycloakContext context;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthenticationSessionModel authSession;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private Uri uri;

    @Mock
    private ClientConnection clientConnection;

    @Mock
    private AuthorizationEndpointRequest request;

    private AuthorizationEndpoint endpoint;
    private MockedStatic<Profile> profileMock;
    private MockedStatic<AuthorizationEndpointRequestParserProcessor> parserMock;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        lenient().when(session.getContext()).thenReturn(context);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getUri()).thenReturn(uri);
        lenient().when(uri.getBaseUri()).thenReturn(URI.create("https://localhost/auth"));
        
        endpoint = new AuthorizationEndpoint(session, event);

        profileMock = mockStatic(Profile.class);
        parserMock = mockStatic(AuthorizationEndpointRequestParserProcessor.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        profileMock.close();
        parserMock.close();
    }

    @org.junit.jupiter.api.Test
    void process_ClientPolicyException_ThrowsErrorPageException() {
        String clientId = "client-id";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        
        ClientPolicyException cpe = mock(ClientPolicyException.class);
        when(cpe.getError()).thenReturn("policy-error");
        when(cpe.getErrorDetail()).thenReturn("policy-detail");
        when(cpe.getErrorStatus()).thenReturn(Response.Status.FORBIDDEN);

        ClientPolicyManager clientPolicyManager = mock(ClientPolicyManager.class);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        doThrow(cpe).when(clientPolicyManager).triggerOnEvent(any(PreAuthorizationRequestContext.class));

        lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
        lenient().when(realm.getClientByClientId(clientId)).thenReturn(client);

        assertThrows(ErrorPageException.class, () -> endpoint.buildGet());

        verify(event).detail(Details.REASON, Details.CLIENT_POLICY_ERROR);
        verify(event).error("policy-error");
    }

    @org.junit.jupiter.api.Test
    void process_CheckRedirectUriFailure_ThrowsErrorPageException() {
        String clientId = "client-id";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
        lenient().when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(true);
        when(client.isBearerOnly()).thenReturn(false);
        when(client.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.parseRequest(any(), any(), any(), any(), any()))
                .thenReturn(request);

        try (MockedConstruction<AuthorizationEndpointChecker> mockedChecker = mockConstruction(AuthorizationEndpointChecker.class,
                (mock, construction) -> {
                    when(mock.event(any())).thenReturn(mock);
                    when(mock.client(any())).thenReturn(mock);
                    when(mock.realm(any())).thenReturn(mock);
                    when(mock.request(any())).thenReturn(mock);
                    when(mock.session(any())).thenReturn(mock);
                    when(mock.params(any())).thenReturn(mock);
                    
                    doThrow(new AuthorizationEndpointChecker.AuthorizationCheckException(
                            OIDCResponseMode.QUERY, "error", "description"))
                            .when(mock).checkRedirectUri();
                })) {

            assertThrows(ErrorPageException.class, () -> endpoint.buildGet());
        }
    }

    @org.junit.jupiter.api.Test
    void process_CheckResponseTypeInvalid_ReturnsRedirectError() {
        String clientId = "client-id";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
        lenient().when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(true);
        when(client.isBearerOnly()).thenReturn(false);
        when(client.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.parseRequest(any(), any(), any(), any(), any()))
                .thenReturn(request);
        
        when(request.getResponseMode()).thenReturn("invalid-mode");

        try (MockedConstruction<AuthorizationEndpointChecker> mockedChecker = mockConstruction(AuthorizationEndpointChecker.class,
                (mock, construction) -> {
                    when(mock.event(any())).thenReturn(mock);
                    when(mock.client(any())).thenReturn(mock);
                    when(mock.realm(any())).thenReturn(mock);
                    when(mock.request(any())).thenReturn(mock);
                    when(mock.session(any())).thenReturn(mock);
                    when(mock.params(any())).thenReturn(mock);

                    doNothing().when(mock).checkRedirectUri();
                    
                    AuthorizationEndpointChecker.AuthorizationCheckException ex = mock(AuthorizationEndpointChecker.AuthorizationCheckException.class);
                    when(ex.getError()).thenReturn("invalid_response_type");
                    when(ex.getErrorDescription()).thenReturn("description");
                    doThrow(ex).when(mock).checkResponseType();
                    
                    when(mock.isInvalidResponseType(ex)).thenReturn(true);
                    when(mock.getParsedResponseMode()).thenReturn(null);
                })) {

            Response response = endpoint.buildGet();
            assertNotNull(response);
        }
    }

    @org.junit.jupiter.api.Test
    void process_CheckResponseTypeFallbackToQueryMode_ReturnsRedirectError() {
        String clientId = "client-id";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
        lenient().when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(true);
        when(client.isBearerOnly()).thenReturn(false);
        when(client.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);

        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.parseRequest(any(), any(), any(), any(), any()))
                .thenReturn(request);

        try (MockedConstruction<AuthorizationEndpointChecker> mockedChecker = mockConstruction(AuthorizationEndpointChecker.class,
                (mock, construction) -> {
                    when(mock.event(any())).thenReturn(mock);
                    when(mock.client(any())).thenReturn(mock);
                    when(mock.realm(any())).thenReturn(mock);
                    when(mock.request(any())).thenReturn(mock);
                    when(mock.session(any())).thenReturn(mock);
                    when(mock.params(any())).thenReturn(mock);

                    doNothing().when(mock).checkRedirectUri();
                    
                    AuthorizationEndpointChecker.AuthorizationCheckException ex = mock(AuthorizationEndpointChecker.AuthorizationCheckException.class);
                    when(ex.getError()).thenReturn("invalid_response_type");
                    when(ex.getErrorDescription()).thenReturn("description");
                    doThrow(ex).when(mock).checkResponseType();
                    
                    when(mock.isInvalidResponseType(ex)).thenReturn(false);
                    when(mock.getParsedResponseMode()).thenReturn(OIDCResponseMode.QUERY);
                })) {

            Response response = endpoint.buildGet();
            assertNotNull(response);
        }
    }
}
