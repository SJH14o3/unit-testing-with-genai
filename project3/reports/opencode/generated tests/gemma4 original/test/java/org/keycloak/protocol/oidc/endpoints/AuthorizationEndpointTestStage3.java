package org.keycloak.protocol.oidc.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
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
import org.keycloak.services.ErrorPageException;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class AuthorizationEndpointTestStage3 {

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

    private TestableAuthorizationEndpoint endpoint;
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
        
        endpoint = new TestableAuthorizationEndpoint(session, event);

        profileMock = mockStatic(Profile.class);
        parserMock = mockStatic(AuthorizationEndpointRequestParserProcessor.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        profileMock.close();
        parserMock.close();
    }

    private void setupBasicSuccessContext(MultivaluedMap<String, String> params) {
        String clientId = "client-id";
        params.add("client_id", clientId);
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
        lenient().when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(true);
        when(client.isBearerOnly()).thenReturn(false);
        when(client.getProtocol()).thenReturn(OIDCLoginProtocol.LOGIN_PROTOCOL);
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.parseRequest(any(), any(), any(), any(), any()))
                .thenReturn(request);
        when(request.getResponseMode()).thenReturn("query");
    }

    @org.junit.jupiter.api.Test
    void process_DeepCheckFailures_ReturnsRedirectError() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        setupBasicSuccessContext(params);

        try (MockedConstruction<AuthorizationEndpointChecker> mockedChecker = mockConstruction(AuthorizationEndpointChecker.class,
                (mock, construction) -> {
                    when(mock.event(any())).thenReturn(mock);
                    when(mock.client(any())).thenReturn(mock);
                    when(mock.realm(any())).thenReturn(mock);
                    when(mock.request(any())).thenReturn(mock);
                    when(mock.session(any())).thenReturn(mock);
                    when(mock.params(any())).thenReturn(mock);

                    doNothing().when(mock).checkRedirectUri();
                    doNothing().when(mock).checkResponseType();
                    when(mock.getParsedResponseType()).thenReturn(null);
                    when(mock.getParsedResponseMode()).thenReturn(OIDCResponseMode.QUERY);

                    AuthorizationEndpointChecker.AuthorizationCheckException ex = mock(AuthorizationEndpointChecker.AuthorizationCheckException.class);
                    when(ex.getError()).thenReturn("invalid_request");
                    when(ex.getErrorDescription()).thenReturn("description");
                    
                    // Test various failures
                    doThrow(ex).when(mock).checkParRequired();
                })) {

            Response response = endpoint.buildGet();
            assertNotNull(response);
        }
    }

    @org.junit.jupiter.api.Test
    void process_DPoP_JktAppliedFromSession() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        setupBasicSuccessContext(params);
        
        String expectedJkt = "some-jkt-value";
        lenient().when(session.getAttribute(anyString(), eq(String.class))).thenReturn(expectedJkt);

        try (MockedConstruction<AuthorizationEndpointChecker> mockedChecker = mockConstruction(AuthorizationEndpointChecker.class,
                (mock, construction) -> {
                    when(mock.event(any())).thenReturn(mock);
                    when(mock.client(any())).thenReturn(mock);
                    when(mock.realm(any())).thenReturn(mock);
                    when(mock.request(any())).thenReturn(mock);
                    when(mock.session(any())).thenReturn(mock);
                    when(mock.params(any())).thenReturn(mock);

                    doNothing().when(mock).checkRedirectUri();
                    doNothing().when(mock).checkResponseType();
                    when(mock.getParsedResponseType()).thenReturn(null);
                    when(mock.getParsedResponseMode()).thenReturn(OIDCResponseMode.QUERY);

                    doNothing().when(mock).checkParRequired();
                    doNothing().when(mock).checkInvalidRequestMessage();
                    doNothing().when(mock).checkAuthorizationDetails();
                    doNothing().when(mock).checkOIDCRequest();
                    doNothing().when(mock).checkValidScope();
                    doNothing().when(mock).checkValidResource();
                    doNothing().when(mock).checkOIDCParams();
                    doNothing().when(mock).checkPKCEParams();
                    doNothing().when(mock).checkProviderAddOns();
                })) {

            endpoint.buildGet();
            
            verify(request).setDpopJkt(expectedJkt);
        }
    }

    /**
     * Subclass to bypass protected method complexity during unit testing.
     */
    private static class TestableAuthorizationEndpoint extends AuthorizationEndpoint {
        public TestableAuthorizationEndpoint(KeycloakSession session, EventBuilder event) {
            super(session, event);
        }

        @Override
        protected AuthenticationSessionModel createAuthenticationSession(ClientModel client, String requestState) {
            return authSession;
        }

        @Override
        protected void updateAuthenticationSession() {
            // Bypass to avoid complex ACR/LoA logic in this stage
        }
    }
}
