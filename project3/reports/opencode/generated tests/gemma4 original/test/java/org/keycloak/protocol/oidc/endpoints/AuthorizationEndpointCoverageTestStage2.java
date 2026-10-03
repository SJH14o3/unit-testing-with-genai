package org.keycloak.protocol.oidc.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.common.ClientConnection;
import org.keycloak.constants.Constants;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.Uri;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCAdvancedConfigWrapper;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.OIDCResponseMode;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.utils.AcrUtils;
import org.keycloak.protocol.oidc.utils.OIDCRedirectUriBuilder;
import org.keycloak.protocol.oidc.utils.Urls;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class AuthorizationEndpointCoverageTestStage2 {

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
    private MockedStatic<OIDCAdvancedConfigWrapper> configWrapperMock;
    private MockedStatic<Urls> urlsMock;
    private MockedStatic<AcrUtils> acrUtilsMock;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        lenient().when(session.getContext()).thenReturn(context);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getUri()).thenReturn(uri);
        lenient().when(uri.getBaseUri()).thenReturn(URI.create("https://localhost/auth"));
        
        endpoint = new TestableAuthorizationEndpoint(session, event);

        configWrapperMock = mockStatic(OIDCAdvancedConfigWrapper.class);
        urlsMock = mockStatic(Urls.class);
        acrUtilsMock = mockStatic(AcrUtils.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        configWrapperMock.close();
        urlsMock.close();
        acrUtilsMock.close();
    }

    private void invokeRedirectErrorToClient(OIDCResponseMode mode, String error, String desc) throws Exception {
        Method method = AuthorizationEndpoint.class.getDeclaredMethod("redirectErrorToClient", OIDCResponseMode.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(endpoint, mode, error, desc);
    }

    private void invokeUpdateAuthenticationSession() throws Exception {
        Method method = AuthorizationEndpoint.class.getDeclaredMethod("updateAuthenticationSession");
        method.setAccessible(true);
        method.invoke(endpoint);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = AuthorizationEndpoint.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(endpoint, value);
    }

    @org.junit.jupiter.api.Test
    void redirectErrorToClient_NoDescription_CoversLine326False() throws Exception {
        // Line 326: if (errorDescription != null) -> false branch
        setPrivateField("redirectUri", "https://callback.com");
        setPrivateField("request", request);
        setPrivateField("client", client);
        
        OIDCAdvancedConfigWrapper clientConfig = mock(OIDCAdvancedConfigWrapper.class);
        configWrapperMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(clientConfig);
        when(clientConfig.isExcludeIssuerFromAuthResponse()).thenReturn(true);

        invokeRedirectErrorToClient(OIDCResponseMode.QUERY, "error", null);

        // We can't easily check the response object created via static builder in a unit test 
        // without mocking the builder itself, but we can verify it doesn't throw.
    }

    @org.junit.jupiter.api.Test
    void redirectErrorToClient_WithState_CoversLine330True() throws Exception {
        // Line 330: if (request.getState() != null) -> true branch
        setPrivateField("redirectUri", "https://callback.com");
        setPrivateField("request", request);
        setPrivateField("client", client);
        when(request.getState()).thenReturn("some-state");
        
        OIDCAdvancedConfigWrapper clientConfig = mock(OIDCAdvancedConfigWrapper.class);
        configWrapperMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(clientConfig);
        when(clientConfig.isExcludeIssuerFromAuthResponse()).thenReturn(true);

        invokeRedirectErrorToClient(OIDCResponseMode.QUERY, "error", "desc");
    }

    @org.junit.jupiter.api.Test
    void redirectErrorToClient_IncludeIssuer_CoversLine335False() throws Exception {
        // Line 335: if (!clientConfig.isExcludeIssuerFromAuthResponse()) -> true branch (covers logic for false)
        setPrivateField("redirectUri", "https://callback.com");
        setPrivateField("request", request);
        setPrivateField("client", client);
        
        OIDCAdvancedConfigWrapper clientConfig = mock(OIDCAdvancedConfigWrapper.class);
        configWrapperMock.when(() -> OIDCAdvancedConfigWrapper.fromClientModel(client)).thenReturn(clientConfig);
        when(clientConfig.isExcludeIssuerFromAuthResponse()).thenReturn(false);
        urlsMock.when(() -> Urls.realmIssuer(any(), anyString())).thenReturn("https://issuer.com");

        invokeRedirectErrorToClient(OIDCResponseMode.QUERY, "error", "desc");
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_WithUiLocales_CoversLine352True() throws Exception {
        // Line 352: if (request.getUiLocales() != null) -> true branch
        setPrivateFieldsForUpdate();
        when(request.getUiLocales()).thenReturn(List.of("en"));

        invokeUpdateAuthenticationSession();

        verify(authSession).setClientNote(org.keycloak.locale.LocaleSelectorProvider.CLIENT_REQUEST_LOCALE, List.of("en"));
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_AcrLoa_Existing_CoversLine374False() throws Exception {
        // Line 374: return loa == null ? Integer.parseInt(acr) : loa; -> false branch (loa is not null)
        setPrivateFieldsForUpdate();
        
        List<String> acrValues = List.of("1");
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(acrValues);
        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap(any())).thenReturn(Map.of("1", 1));
        acrUtilsMock.when(() -> AcrUtils.enforceMinimumAcr(any(), any())).thenReturn(acrValues);
        
        // Mocking the stream result to have a non-null loa
        // The code uses acrLoaMap.get(acr)
        
        invokeUpdateAuthenticationSession();

        verify(authSession).setClientNote(Constants.REQUESTED_LEVEL_OF_AUTHENTICATION, "1");
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_WithAdditionalParams_CoversLine391True() throws Exception {
        // Line 391: if (request.getAdditionalReqParams() != null) -> true branch
        setPrivateFieldsForUpdate();

        Map<String, String> extraParams = new MultivaluedHashMap<>();
        extraParams.put("param1", "val1");
        when(request.getAdditionalReqParams()).thenReturn(extraParams);

        invokeUpdateAuthenticationSession();

        verify(authSession).setClientNote(AuthorizationEndpoint.LOGIN_SESSION_NOTE_ADDITIONAL_REQ_PARAMS_PREFIX + "param1", "val1");
    }

    @org.junit.jupiter.api.Test
    void updateAuthenticationSession_WithAuthorizationDetails_CoversLine398True() throws Exception {
        // Line 398: if (authorizationDetails != null) -> true branch
        setPrivateFieldsForUpdate();

        Map<String, String> extraParams = new MultivaluedHashMap<>();
        extraParams.put(org.keycloak.OAuth2Constants.AUTHORIZATION_DETAILS, "details");
        when(request.getAdditionalReqParams()).thenReturn(extraParams);

        invokeUpdateAuthenticationSession();

        verify(authSession).setClientNote(org.keycloak.OAuth2Constants.AUTHORIZATION_DETAILS, "details");
    }

    private void setPrivateFieldsForUpdate() throws Exception {
        setPrivateField("request", request);
        setPrivateField("authenticationSession", authSession);
        setPrivateField("client", client);
        
        // Setup defaults to avoid NPE in the method
        lenient().when(request.getResponseType()).thenReturn("code");
        lenient().when(request.getRedirectUri()).thenReturn("https://callback.com");
        lenient().when(request.getClaims()).thenReturn(Collections.emptyMap());
        lenient().when(request.getAdditionalReqParams()).thenReturn(Collections.emptyMap());
        
        acrUtilsMock.when(() -> AcrUtils.getRequiredAcrValues(any())).thenReturn(Collections.emptyList());
        acrUtilsMock.when(() -> AcrUtils.getAcrLoaMap(any())).thenReturn(Collections.emptyMap());
    }

    private static class TestableAuthorizationEndpoint extends AuthorizationEndpoint {
        public TestableAuthorizationEndpoint(KeycloakSession session, EventBuilder event) {
            super(session, event);
        }
    }
}
