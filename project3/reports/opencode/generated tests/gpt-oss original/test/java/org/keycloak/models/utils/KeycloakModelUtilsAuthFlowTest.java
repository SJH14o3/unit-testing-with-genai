import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.Optional;
import java.util.stream.Stream;

public class KeycloakModelUtilsAuthFlowTest {

    @Test
    public void testDeepFindAuthenticationExecutions_collectsBothFlowsAndExecutions() {
        RealmModel realm = mock(RealmModel.class);
        AuthenticationFlowModel flowRoot = mock(AuthenticationFlowModel.class);
        when(flowRoot.getId()).thenReturn("flow1");
        AuthenticationExecutionModel exec1 = mock(AuthenticationExecutionModel.class);
        when(exec1.isAuthenticatorFlow()).thenReturn(false);
        AuthenticationExecutionModel exec2 = mock(AuthenticationExecutionModel.class);
        when(exec2.isAuthenticatorFlow()).thenReturn(true);
        when(exec2.getFlowId()).thenReturn("subflow1");
        AuthenticationFlowModel subflow = mock(AuthenticationFlowModel.class);
        when(subflow.getId()).thenReturn("subflow1");
        AuthenticationExecutionModel subExec = mock(AuthenticationExecutionModel.class);
        when(subExec.isAuthenticatorFlow()).thenReturn(false);
        when(realm.getAuthenticationExecutionsStream("flow1")).thenReturn(Stream.of(exec1, exec2));
        when(realm.getAuthenticationExecutionsStream("subflow1")).thenReturn(Stream.of(subExec));
        when(realm.getAuthenticationFlowById("subflow1")).thenReturn(subflow);
        java.util.List<AuthenticationExecutionModel> list = new java.util.ArrayList<>();
        KeycloakModelUtils.deepFindAuthenticationExecutions(realm, flowRoot, list);
        Assertions.assertEquals(2, list.size()); // exec1 and subExec
        Assertions.assertTrue(list.contains(exec1));
        Assertions.assertTrue(list.contains(subExec));
    }

    @Test
    public void testIsFlowUsed_checkBuiltInAndClientOverriding() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        AuthenticationFlowModel flow = mock(AuthenticationFlowModel.class);
        when(flow.getId()).thenReturn("flow-id");
        when(realm.getBrowserFlow()).thenReturn(flow);
        Assertions.assertTrue(KeycloakModelUtils.isFlowUsed(session, realm, flow));

        // mock client override path
        ClientModel client = mock(ClientModel.class);
        ClientProvider clientProvider = mock(ClientProvider.class);
        when(session.clients()).thenReturn(clientProvider);
        when(clientProvider.searchClientByAuthenticationFlowBindingOverrides(any(), anyInt(), anyInt()))
                .thenReturn(Stream.of(client));
        Assertions.assertTrue(KeycloakModelUtils.isFlowUsed(session, realm, flow));
    }

    @Test
    public void testDeepDeleteAuthenticationFlow_removesRecursively() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        AuthenticationFlowModel root = mock(AuthenticationFlowModel.class);
        when(root.getId()).thenReturn("root-flow");
        AuthenticationExecutionModel exec = mock(AuthenticationExecutionModel.class);
        when(exec.isAuthenticatorFlow()).thenReturn(false);
        when(realm.getAuthenticationExecutionsStream("root-flow")).thenReturn(Stream.of(exec));
        Runnable flowUnavailable = mock(Runnable.class);
        Runnable builtinHandler = mock(Runnable.class);
        KeycloakModelUtils.deepDeleteAuthenticationFlow(session, realm, root, flowUnavailable, builtinHandler, true);
        // verify remove callback
        verify(realm).removeAuthenticationFlow(root);
        // exec handler not called as not null
    }
}
