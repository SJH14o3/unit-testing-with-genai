package org.keycloak.protocol.oidc.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.constants.Constants;
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
import org.keycloak.protocol.oidc.utils.AcrUtils;
import org.keycloak.services.ClientPolicyException;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.services.clientpolicy.context.AuthorizationRequestContext;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class AuthorizationEndpointTestStage4 {

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
    private MockedStatic<AcrUtils> acrUtilsMock;
    private MockedStatic<AuthenticationSessionManager> authSessionManagerMock;

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
        acrUtilsMock = mockStatic(AcrUtils.class);
        authSessionManagerMock = mockStatic(AuthenticationSessionManager.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        profileMock.close();
        parserMock.close();
        acrUtilsMock.close();
        authSessionManagerMock.close();
    }

    private void invokeUpdateAuthenticationSession() throws Exception {
        Method method = AuthorizationEndpoint.class.getDeclaredMethod("updateAuthenticationSession");
        method.setAccessible(true);
        method.invoke(endpoint);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = AuthorizationEndpoint.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(endpoint, value);
    }

    @org.junit.jupiter.api.Test
    void process_AuthorizationRequestContextPolicyError_ThrowsErrorPageExceptionAndRemovesSession() {
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

        ClientPolicyException cpe = mock(ClientPolicyException.class);
        when(cpe.getError()).thenReturn("policy-error");
        when(cpe.getErrorDetail()).thenReturn("policy-detail");
        when(cpe.getErrorStatus()).thenReturn(Response.Status.FORBIDDEN);

        ClientPolicyManager clientPolicyManager = mock(ClientPolicyManager.class);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        doThrow(cpe).when(clientPolicyManager).triggerOnEvent(any(AuthorizationRequestContext.class));

        AuthenticationSessionManager mockManager = mock(AuthenticationSessionManager.class);
        authSessionManagerMock.when(() -> new AuthenticationSessionManager(session)).thenReturn(mockManager);

        assertThrows(ErrorPageException.class, () -> endpoint.buildGet());

        verify(event).detail(Details.REASON, Details.CLIENT_POLICY_ERROR);
        verify(event).error("policy-error");
        verify(mockManager).removeAuthenticationSession(eq(realm), any(), eq(false));
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_Success() throws Exception {
        setupFields();

        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap(any())).thenReturn(Collections.emptyMap());
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(Collections.emptyList());

        invokeUpdateAuthenticationSession();

        verify(authSession).setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        verify(authSession).setRedirectUri(any());
        verify(authSession).setAction(AuthenticationSessionModel.Action.AUTHENTICATE.name());
        verify(authSession).setClientNote(OIDCLoginProtocol.RESPONSE_TYPE_PARAM, request.getResponseType());
        verify(authSession).setClientNote(OIDCLoginProtocol.REDIRECT_URI_PARAM, request.getRedirectUri());
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_ACRFailure_EssentialClaim_ThrowsException() throws Exception {
        setupFields();

        List<String> acrValues = List.of("low-acr");
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(acrValues);
        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap(any())).thenReturn(Collections.emptyMap());
        acrUtilsMock.when(() -> AcrUtils.enforceMinimumAcr(eq(acrValues), any())).thenReturn(List.of("high-acr"));

        assertThrows(Exception.class, this::invokeUpdateAuthenticationSession);
        verify(event).error(Errors.INVALID_REQUEST);
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_UnknownACR_NonEssential_FallsBackToMinLoa() throws Exception {
        setupFields();

        List<String> acrValues = List.of("unknown-acr");
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(acrValues);
        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap(any())).thenReturn(Collections.emptyMap());
        acrUtilsMock.when(() -> AcrUtils.enforceMinimumAcr(any(), any())).thenReturn(acrValues);

        invokeUpdateAuthenticationSession();

        verify(authSession).setClientNote(Constants.REQUESTED_LEVEL_OF_AUTHENTICATION, String.valueOf(Constants.MINIMUM_LOA));
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_UnknownACR_Essential_ThrowsException() throws Exception {
        setupFields();

        List<String> acrValues = List.of("unknown-acr");
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(acrValues);
        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap(any())).thenReturn(Collections.emptyMap());
        acrUtilsMock.when(() -> AcrUtils.enforceMinimumAcr(any(), any())).thenReturn(acrValues);
        when(authSession.getClientNote(Constants.FORCE_LEVEL_OF_AUTHENTICATION)).thenReturn("true");

        assertThrows(Exception.class, this::invokeUpdateAuthenticationSession);
        verify(event).error(Errors.INVALID_REQUEST);
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_AdditionalParams_StoredWithPrefix() throws Exception {
        setupFields();

        MultivaluedMap<String, String> extraParams = new MultivaluedHashMap<>();
        extraParams.put("my_custom_param", "custom_value");
        when(request.getAdditionalReqParams()).thenReturn(extraParams);

        invokeUpdateAuthenticationSession();

        verify(authSession).setClientNote(AuthorizationEndpoint.LOGIN_SESSION_NOTE_ADDITIONAL_REQ_PARAMS_PREFIX + "my_custom_param", "custom_value");
    }

    private void setupFields() throws Exception {
        setPrivateField("request", request);
        setPrivateField("authenticationSession", authSession);
        setPrivateField("client", client);

        lenient().when(request.getResponseType()).thenReturn("code");
        lenient().when(request.getRedirectUri()).thenReturn("https://callback.com");
        lenient().when(request.getMaxAge()).thenReturn(null);
        lenient().when(request.getUiLocales()).thenReturn(null);
        lenient().when(request.getClaims()).thenReturn(Collections.emptyMap());
        lenient().when(request.getAcr()).thenReturn(null);
        lenient().when(request.getAdditionalReqParams()).thenReturn(Collections.emptyMap());
    }

    private static class TestableAuthorizationEndpoint extends AuthorizationEndpoint {
        public TestableAuthorizationEndpoint(KeycloakSession session, EventBuilder event) {
            super(session, event);
        }

        @Override
        protected AuthenticationSessionModel createAuthenticationSession(ClientModel client, String requestState) {
            return null;
        }
    }
}
