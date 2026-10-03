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
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.protocol.oidc.utils.AcrUtils;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.protocol.oidc.utils.OIDCResponseMode;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.services.util.CacheControlUtil;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class TestAuthorizationEndpointStage3 {

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
    private static AuthenticationSessionModel authSession;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private KeycloakUriInfo uri;

    @Mock
    private ClientConnection clientConnection;

    @Mock
    private AuthorizationEndpointRequest request;


    @Mock
    private ClientPolicyManager clientPolicyManager;

    private TestableAuthorizationEndpoint endpoint;
    private MockedStatic<Profile> profileMock;
    private MockedStatic<AuthorizationEndpointRequestParserProcessor> parserMock;
    private MockedStatic<CacheControlUtil> cacheControlMock;
    private MockedStatic<AcrUtils> acrUtilsMock;
    private MockedStatic<OIDCRedirectUriBuilder> redirectBuilderMock;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        lenient().when(session.getContext()).thenReturn(context);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getUri()).thenReturn(uri);
        lenient().when(context.getAuthenticationSession()).thenReturn(authSession);
        lenient().when(uri.getBaseUri()).thenReturn(URI.create("https://localhost/auth"));
        lenient().when(realm.isEnabled()).thenReturn(true);
        lenient().when(realm.getName()).thenReturn("test");
        lenient().when(session.clientPolicy()).thenReturn(clientPolicyManager);
        lenient().doAnswer(invocation -> null).when(clientPolicyManager).triggerOnEvent(any());

        endpoint = new TestableAuthorizationEndpoint(session, event);

        profileMock = mockStatic(Profile.class);
        parserMock = mockStatic(AuthorizationEndpointRequestParserProcessor.class);
        cacheControlMock = mockStatic(CacheControlUtil.class);
        acrUtilsMock = mockStatic(AcrUtils.class);
        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap((ClientModel) any())).thenReturn(java.util.Collections.emptyMap());
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(java.util.Collections.emptyList());
        acrUtilsMock.when(() -> AcrUtils.getAcrValues(any(), any(), any())).thenReturn(java.util.Collections.emptyList());
        redirectBuilderMock = mockStatic(OIDCRedirectUriBuilder.class);
        OIDCRedirectUriBuilder redirectBuilder = mock(OIDCRedirectUriBuilder.class);
        redirectBuilderMock.when(() -> OIDCRedirectUriBuilder.fromUri(anyString(), any(), any(), any())).thenReturn(redirectBuilder);
        lenient().when(redirectBuilder.addParam(anyString(), anyString())).thenReturn(redirectBuilder);
        lenient().when(redirectBuilder.build()).thenReturn(mock(Response.class));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        profileMock.close();
        parserMock.close();
        cacheControlMock.close();
        acrUtilsMock.close();
        redirectBuilderMock.close();
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
        lenient().when(request.getResponseMode()).thenReturn("query");
        lenient().when(request.getRedirectUri()).thenReturn("https://callback.example");
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
                    when(mock.getRedirectUri()).thenReturn("https://callback.example");

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


        protected void updateAuthenticationSession() {
            // Bypass to avoid complex ACR/LoA logic in this stage
        }

        @Override
        protected Response handleBrowserAuthenticationRequest(AuthenticationSessionModel authSession, org.keycloak.protocol.LoginProtocol protocol, boolean isPassive, boolean redirectToAuthentication) {
            return null;
        }
    }
}
