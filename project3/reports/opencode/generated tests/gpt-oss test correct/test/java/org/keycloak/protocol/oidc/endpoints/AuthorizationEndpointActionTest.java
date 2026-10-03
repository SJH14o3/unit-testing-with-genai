package org.keycloak.protocol.oidc.endpoints;

/*  --------------------  Dependencies  ---------------------- */
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.events.Errors;
import org.keycloak.events.Details;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakUriInfo;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequest;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.clientpolicy.context.AuthorizationRequestContext;
import org.keycloak.services.clientpolicy.context.PreAuthorizationRequestContext;
import org.keycloak.services.util.CacheControlUtil;
import org.keycloak.services.util.LocaleUtil;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.Urls;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.util.TokenUtil;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

/*  ----------------------------------------------------------------------------------- */

@ExtendWith(MockitoExtension.class)
public class AuthorizationEndpointActionTest {

    @Mock
    private org.keycloak.models.KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private MultivaluedMap<String, String> params;

    @Mock
    private AuthorizationEndpointRequest request;

    /* ------------  common setup  ------------------------------ */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(session.getContext()).thenReturn(mock(KeycloakContext.class));
        when(session.getContext().getUri()).thenReturn(mock(KeycloakUriInfo.class));
        when(session.getContext().getUri().getBaseUri()).thenReturn(URI.create("https://demo.com/"));
    }

}