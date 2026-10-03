package org.keycloak.protocol.oidc.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.common.ClientConnection;
import org.keycloak.common.Profile;
import org.keycloak.constants.AdapterConstants;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.HttpRequest;
import org.keycloak.http.Uri;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SslRequired;
import org.keycloak.organization.utils.Organizations;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.messages.Messages;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
class AuthorizationEndpointTestStage1 {

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

    private AuthorizationEndpoint endpoint;
    private MockedStatic<Profile> profileMock;
    private MockedStatic<Organizations> organizationsMock;
    private MockedStatic<AuthorizationEndpointRequestParserProcessor> parserMock;

    @BeforeEach
    void setUp() {
        // Setup the complex hierarchy in AuthorizationEndpointBase
        lenient().when(session.getContext()).thenReturn(context);
        lenient().when(context.getRealm()).thenReturn(realm);
        lenient().when(context.getHttpRequest()).thenReturn(httpRequest);
        lenient().when(context.getConnection()).thenReturn(clientConnection);
        lenient().when(context.getUri()).thenReturn(uri);
        lenient().when(uri.getBaseUri()).thenReturn(URI.create("https://localhost/auth"));
        
        // Initialize endpoint
        endpoint = new AuthorizationEndpoint(session, event);

        // Setup static mocks
        profileMock = mockStatic(Profile.class);
        organizationsMock = mockStatic(Organizations.class);
        parserMock = mockStatic(AuthorizationEndpointRequestParserProcessor.class);
    }

    @AfterEach
    void tearDown() {
        profileMock.close();
        organizationsMock.close();
        parserMock.close();
    }

    @Test
    void authorizeDevice_FeatureDisabled_ReturnsNull() {
        profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.DEVICE_FLOW)).thenReturn(false);
        
        Object result = endpoint.authorizeDevice();
        
        assertNull(result);
    }

    @Test
    void authorizeDevice_FeatureEnabled_ReturnsEndpoint() {
        profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.DEVICE_FLOW)).thenReturn(true);
        
        Object result = endpoint.authorizeDevice();
        
        assertNotNull(result);
    }

    @Test
    void checkClient_MissingClientId_ThrowsException() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(null);

        assertThrows(ErrorPageException.class, () -> {
            // We simulate buildGet calling process which calls checkClient
            lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
            endpoint.buildGet();
        });
        
        verify(event).error(Errors.INVALID_REQUEST);
    }

    @Test
    void checkClient_ClientNotFound_ThrowsException() {
        String clientId = "unknown-client";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);
        
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        when(realm.getClientByClientId(clientId)).thenReturn(null);

        assertThrows(ErrorPageException.class, () -> {
            lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
            endpoint.buildGet();
        });

        verify(event).error(Errors.CLIENT_NOT_FOUND);
    }

    @Test
    void checkClient_ClientDisabled_ThrowsException() {
        String clientId = "disabled-client";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);
        
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(false);

        assertThrows(ErrorPageException.class, () -> {
            lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
            endpoint.buildGet();
        });

        verify(event).error(Errors.CLIENT_DISABLED);
    }

    @Test
    void checkClient_BearerOnly_ThrowsException() {
        String clientId = "bearer-only-client";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);
        
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(true);
        when(client.isBearerOnly()).thenReturn(true);

        assertThrows(ErrorPageException.class, () -> {
            lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
            endpoint.buildGet();
        });

        verify(event).error(Errors.NOT_ALLOWED);
    }

    @Test
    void checkClient_WrongProtocol_ThrowsException() {
        String clientId = "wrong-protocol-client";
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);
        
        parserMock.when(() -> AuthorizationEndpointRequestParserProcessor.getClientId(any(), any(), any())).thenReturn(clientId);
        when(realm.getClientByClientId(clientId)).thenReturn(client);
        when(client.isEnabled()).thenReturn(true);
        when(client.isBearerOnly()).thenReturn(false);
        when(client.getProtocol()).thenReturn("saml");

        assertThrows(ErrorPageException.class, () -> {
            lenient().when(context.getUri().getQueryParameters()).thenReturn(params);
            endpoint.buildGet();
        });

        verify(event).error(Errors.INVALID_CLIENT);
    }

    @Test
    void register_RegistrationNotAllowed_ThrowsException() {
        organizationsMock.when(() -> Organizations.isRegistrationAllowed(any(), any())).thenReturn(false);

        assertThrows(ErrorPageException.class, () -> {
            endpoint.register("some-token");
        });
        
        // Check if error code is REGISTRATION_NOT_ALLOWED
        // Since ErrorPageException doesn't expose status easily in constructor without context, 
        // we verify the logic flow.
    }

    @Test
    void forgotCredentials_ResetPasswordNotAllowed_ThrowsException() {
        when(realm.isResetPasswordAllowed()).thenReturn(false);

        assertThrows(ErrorPageException.class, () -> {
            endpoint.forgotCredentials();
        });
    }

    @Test
    void forgotCredentials_Success_ReturnsEndpoint() {
        when(realm.isResetPasswordAllowed()).thenReturn(true);

        AuthorizationEndpoint result = endpoint.forgotCredentials();
        
        assertEquals(endpoint, result);
    }
}
