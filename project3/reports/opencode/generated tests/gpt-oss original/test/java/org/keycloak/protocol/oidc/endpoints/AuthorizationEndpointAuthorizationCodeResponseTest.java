package org.keycloak.protocol.oidc.endpoints;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.events.Errors;
import org.keycloak.events.Details;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.services.clientpolicy.context.PreAuthorizationRequestContext;
import org.keycloak.services.clientpolicy.context.AuthorizationRequestContext;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.util.CacheControlUtil;
import org.keycloak.services.util.LocaleUtil;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.Urls;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.util.TokenUtil;
import org.keycloak.services.LoginActionsService;
import org.keycloak.models.sessions.AuthenticationSessionModel;
import org.keycloak.models.ClientModel;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.organization.utils.Organizations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationEndpointAuthorizationCodeResponseTest {

    @Mock
    private org.keycloak.models.KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthenticationSessionModel authenticationSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(session.getRealm()).thenReturn(realm);
        when(session.getContext()).thenReturn(mock(org.keycloak.services.context.HttpFacade.RequestContext.class));
        when(session.getContext().getUri()).thenReturn(mock(jakarta.ws.rs.core.UriInfo.class));
    }

    @Test
    @DisplayName("buildAuthorizationCodeAuthorizationResponse handles PAR request redirect")
    public void testParRequestRedirect() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        endpoint.parsedResponseMode = OIDCLoginProtocol.PROMPT_VALUE_CREATE; // placeholder
        endpoint.redirectUri = "https://demo.com/callback";

        // Inject a dummy request with a request_uri
        AuthorizationEndpointRequest req = mock(AuthorizationEndpointRequest.class);
        when(req.getRequestUri()).thenReturn("par-uri");
        when(req.getPrompt()).thenReturn(OIDCLoginProtocol.PROMPT_VALUE_NONE);
        when(req.getState()).thenReturn("state123");
        when(req.getResponseType()).thenReturn("code");
        java.lang.reflect.Field reqField = AuthorizationEndpoint.class.getDeclaredField("request");
        reqField.setAccessible(true);
        reqField.set(endpoint, req);

        // Use reflection to invoke private method
        java.lang.reflect.Method method = AuthorizationEndpoint.class.getDeclaredMethod("buildAuthorizationCodeAuthorizationResponse", String.class);
        method.setAccessible(true);
        Response resp = (Response) method.invoke(endpoint, "par-uri");
        assertNotNull(resp, "Response should not be null for PAR request");
    }
}
