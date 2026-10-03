package org.keycloak.protocol.oidc.endpoints;

/*  --------------------  Dependency Imports  ---------------------- */
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakUriInfo;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/*  --------------------  Keycloak Imports  ---------------------- */
import org.keycloak.events.EventBuilder;
import org.keycloak.events.Errors;
import org.keycloak.events.Details;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.endpoints.request.AuthorizationEndpointRequestParserProcessor;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.context.PreAuthorizationRequestContext;
import org.keycloak.services.ErrorPageException;

import java.lang.reflect.Method;

/*  ----------------------------------------------------------------------------------- */

@ExtendWith(MockitoExtension.class)
public class AuthorizationEndpointClientCheckTest {

    @Mock
    private org.keycloak.models.KeycloakSession session;

    @Mock
    private EventBuilder event;

    @Mock
    private RealmModel realm;

    @Mock
    private ClientModel client;

    @Mock
    private AuthenticationSessionModel authSession;

    @Mock
    private MultivaluedMap<String, String> params;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(realm.getName()).thenReturn("demo-realm");
        when(session.getContext()).thenReturn(mock(KeycloakContext.class));
        when(session.getContext().getUri()).thenReturn(mock(KeycloakUriInfo.class));
    }
}