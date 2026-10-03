package org.keycloak.protocol.oidc.endpoints;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.Profile;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.events.Errors;
import org.keycloak.events.Details;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakUriInfo;
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
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.util.TokenUtil;
import org.keycloak.models.ClientModel;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.organization.utils.Organizations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthorizationEndpointRegisterTest {

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

    @Mock
    private KeycloakContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);
        when(context.getUri()).thenReturn(mock(KeycloakUriInfo.class));

        LoginFormsProvider loginForms = mock(LoginFormsProvider.class, RETURNS_SELF);
        when(loginForms.createErrorPage(any(Response.Status.class))).thenReturn(mock(Response.class));
        when(session.getProvider(LoginFormsProvider.class)).thenReturn(loginForms);
    }

    @Test
    @DisplayName("register() throws ErrorPageException when registration not allowed")
    public void testRegisterNotAllowed() {
        try (var staticOrg = mockStatic(Organizations.class); var staticProfile = mockStatic(Profile.class)) {
            staticOrg.when(() -> Organizations.isRegistrationAllowed(session, realm)).thenReturn(false);
            staticProfile.when(() -> Profile.isFeatureEnabled(Profile.Feature.ORGANIZATION)).thenReturn(false);
            AuthorizationEndpoint endpoint = new AuthorizationEndpoint(session, event);
            assertThrows(ErrorPageException.class, () -> endpoint.register(null));
        }
    }

    /* helper to read private field via reflection */
    private Object getFieldValue(Object target, String fieldName) {
        try {
            java.lang.reflect.Field f = AuthorizationEndpoint.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
