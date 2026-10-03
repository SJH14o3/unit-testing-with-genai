package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.IdentityProviderProvider;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.organization.OrganizationModel;
import org.keycloak.models.organization.OrganizationProvider;
import org.keycloak.sessions.KeycloakSession;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakModelUtilsFlowIdpTest {

    @Mock
    KeycloakSession session;

    @Mock
    RealmModel realm;

    @Mock
    AuthenticationFlowModel flow;

    @Mock
    AuthenticationExecutionModel execution;

    @Mock
    IdentityProviderModel idp;

    @Mock
    IdentityProviderProvider idpProvider;

    @Mock
    OrganizationProvider orgProvider;

    @Mock
    OrganizationModel organization;

    @Mock
    IdentityProviderMapperModel mapper;

    @Test
    void testDeepFindAuthenticationExecutions_Recursive() {
        // Setup: flow has one execution which is another flow
        when(realm.getAuthenticationExecutionsStream(flow.getId())).thenReturn(Stream.of(execution));
        when(execution.isAuthenticatorFlow()).thenReturn(true);
        when(execution.getFlowId()).thenReturn("sub-flow-id");
        
        AuthenticationFlowModel subFlow = mock(AuthenticationFlowModel.class);
        when(subFlow.getId()).thenReturn("sub-flow-id");
        when(realm.getAuthenticationFlowById("sub-flow-id")).thenReturn(subFlow);
        
        // subFlow has one execution which is a leaf
        AuthenticationExecutionModel leafExecution = mock(AuthenticationExecutionModel.class);
        when(leafExecution.isAuthenticatorFlow()).thenReturn(false);
        when(subFlow.getAuthenticationExecutionsStream(subFlow.getId())).thenReturn(Stream.of(leafExecution));

        List<AuthenticationExecutionModel> result = new java.util.ArrayList<>();
        KeycloakModelUtils.deepFindAuthenticationExecutions(realm, flow, result);

        assertEquals(1, result.size());
        assertEquals(leafExecution, result.get(0));
    }

    @Test
    void testIsFlowUsed_BrowserFlow() {
        AuthenticationFlowModel browserFlow = mock(AuthenticationFlowModel.class);
        when(browserFlow.getId()).thenReturn("browser-flow-id");
        when(realm.getBrowserFlow()).thenReturn(browserFlow);
        
        when(flow.getId()).thenReturn("browser-flow-id");

        assertTrue(KeycloakModelUtils.isFlowUsed(session, realm, flow));
    }

    @Test
    void testIsFlowUsed_RegistrationFlow() {
        AuthenticationFlowModel regFlow = mock(AuthenticationFlowModel.class);
        when(regFlow.getId()).thenReturn("reg-flow-id");
        when(realm.getRegistrationFlow()).thenReturn(regFlow);
        
        when(flow.getId()).thenReturn("reg-flow-id");

        assertTrue(KeycloakModelUtils.isFlowUsed(session, realm, flow));
    }

    @Test
    void testIsFlowUsed_ClientFlow() {
        AuthenticationFlowModel clientFlow = mock(AuthenticationFlowModel.class);
        when(clientFlow.getId()).thenReturn("client-flow-id");
        when(realm.getClientAuthenticationFlow()).thenReturn(clientFlow);
        
        when(flow.getId()).thenReturn("client-flow-id");

        assertTrue(KeycloakModelUtils.isFlowUsed(session, realm, flow));
    }

    @Test
    void testIsFlowUsed_NotFound() {
        when(realm.getBrowserFlow()).thenReturn(null);
        when(realm.getRegistrationFlow()).thenReturn(null);
        when(realm.getClientAuthenticationFlow()).thenReturn(null);
        when(realm.getDirectGrantFlow()).thenReturn(null);
        when(realm.getResetCredentialsFlow()).thenReturn(null);
        when(realm.getDockerAuthenticationFlow()).thenReturn(null);
        when(realm.getFirstBrokerLoginFlow()).thenReturn(null);
        
        // Mock searchClientByAuthenticationFlowBindingOverrides to return empty
        when(realm.searchClientByAuthenticationFlowBindingOverrides(anyMap(), anyInt(), anyInt())).thenReturn(Stream.empty());
        
        // Mock identityProviders().getByFlow to return empty
        when(session.identityProviders()).thenReturn(mock(IdentityProviderProvider.class));
        when(session.identityProviders().getByFlow(anyString(), any(), anyInt(), anyInt())).thenReturn(Stream.empty());

        assertFalse(KeycloakModelUtils.isFlowUsed(session, realm, flow));
    }

    @Test
    void testGetIdentityProviderDisplayName_WithFactory() {
        when(idp.getDisplayName()).thenReturn(null);
        when(idp.getAlias()).thenReturn("my-alias");
        when(idp.getProviderId()).thenReturn("social-provider");
        
        KeycloakSessionFactory factory = mock(KeycloakSessionFactory.class);
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        
        SocialIdentityProviderFactory<?> factoryMock = mock(SocialIdentityProviderFactory.class);
        when(factory.getProviderFactory(eq(SocialIdentityProvider.class), eq("social-provider"))).thenReturn(factoryMock);
        when(factoryMock.getName()).thenReturn("Social Name");

        assertEquals("Social Name", KeycloakModelUtils.getIdentityProviderDisplayName(session, idp));
    }

    @Test
    void testGetIdentityProviderDisplayName_FallbackToAlias() {
        when(idp.getDisplayName()).thenReturn("");
        when(idp.getAlias()).thenReturn("my-alias");
        when(session.getKeycloakSessionFactory()).thenReturn(mock(KeycloakSessionFactory.class));

        assertEquals("my-alias", KeycloakModelUtils.getIdentityProviderDisplayName(session, idp));
    }

    @Test
    void testDeepDeleteAuthenticationFlow_NullFlow() {
        Runnable unavailableHandler = mock(Runnable.class);
        KeycloakModelUtils.deepDeleteAuthenticationFlow(session, realm, null, unavailableHandler, mock(Runnable.class), false);
        verify(unavailableHandler).run();
    }

    @Test
    void testDeepDeleteAuthenticationFlow_BuiltIn() {
        Runnable builtinHandler = mock(Runnable.class);
        when(realm.getAuthenticationExecutionsStream(flow.getId())).thenReturn(Stream.empty());

        KeycloakModelUtils.deepDeleteAuthenticationFlow(session, realm, flow, mock(Runnable.class), builtinHandler, true);

        verify(builtinHandler).run();
        verify(realm).removeAuthenticationFlow(flow);
    }
}
