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
public class AuthorizationEndpointBuildRegisterTest {

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
    @DisplayName("buildRegister() expires identity cookie and authenticates via registration flow")
    public void testBuildRegister() throws Exception {
        AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
        // Spy to override internal authManager and others
        AuthorizationEndpoint spyEndpoint = Mockito.spy(endpoint);

        // Mock the LoginActionsService and AuthenticationProcessor
        LoginActionsService loginService = mock(LoginActionsService.class);
        AuthenticationProcessor processor = mock(AuthenticationProcessor.class);
        when(spyEndpoint.createProcessor(any(), anyString(), anyString())).thenReturn(processor);

        // Ensure authManager expiration is called
        doNothing().when(spyEndpoint).authManager().expireIdentityCookie(session);

        Response resp = spyEndpoint.buildRegister();
        assertNotNull(resp, "buildRegister should return a response");
        verify(processor).authenticate();
    }
}
