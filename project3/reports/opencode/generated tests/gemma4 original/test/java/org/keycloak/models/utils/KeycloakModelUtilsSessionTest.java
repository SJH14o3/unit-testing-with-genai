package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.models.organization.OrganizationModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.transaction.TransactionManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakModelUtilsSessionTest {

    @Mock
    KeycloakSessionFactory factory;

    @Mock
    KeycloakSession session;

    @Mock
    KeycloakContext context;

    @Mock
    TransactionManager transactionManager;

    @Mock
    RealmModel realm;

    @Mock
    ClientModel client;

    @Mock
    AuthenticationSessionModel authSession;

    @Mock
    RootAuthenticationSessionModel rootAuthSession;

    @Mock
    KeycloakSessionTask task;

    @Mock
    KeycloakSessionTaskWithResult<String> taskWithResult;

    @Test
    void testRunJobInTransaction_Success() {
        when(factory.create()).thenReturn(session);
        when(session.getTransactionManager()).thenReturn(transactionManager);

        assertDoesNotThrow(() -> KeycloakModelUtils.runJobInTransaction(factory, task));

        verify(transactionManager).begin();
        verify(task).run(session);
    }

    @Test
    void testRunJobInTransaction_Failure() {
        when(factory.create()).thenReturn(session);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        doThrow(new RuntimeException("Task failed")).when(task).run(session);

        assertThrows(RuntimeException.class, () -> KeycloakModelUtils.runJobInTransaction(factory, task));

        verify(transactionManager).begin();
        verify(transactionManager).setRollbackOnly();
    }

    @Test
    void testRunJobInTransactionWithResult_Success() {
        when(factory.create()).thenReturn(session);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(taskWithResult.run(session)).thenReturn("Success");

        String result = KeycloakModelUtils.runJobInTransactionWithResult(factory, taskWithResult);

        assertEquals("Success", result);
        verify(transactionManager).begin();
    }

    @Test
    void testRunOnRealm_SameRealm() {
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);
        
        Function<KeycloakSession, String> operation = s -> "ok";
        
        String result = KeycloakModelUtils.runOnRealm(session, realm, operation);
        
        assertEquals("ok", result);
        verify(context, never()).setRealm(any());
    }

    @Test
    void testRunOnRealm_DifferentRealm() {
        RealmModel targetRealm = mock(RealmModel.class);
        when(session.getContext()).thenReturn(context);
        when(context.getRealm()).thenReturn(realm);
        
        Function<KeycloakSession, String> operation = s -> "switched";
        
        String result = KeycloakModelUtils.runOnRealm(session, targetRealm, operation);
        
        assertEquals("switched", result);
        verify(context).setRealm(targetRealm);
        verify(context).setRealm(realm);
    }

    @Test
    void testCreateComponentModel() {
        String name = "test-component";
        String parentId = "parent-id";
        String providerId = "provider-id";
        String providerType = "type";
        String[] config = {"key1", "val1", "key2", "val2"};

        ComponentModel result = KeycloakModelUtils.createComponentModel(name, parentId, providerId, providerType, config);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(parentId, result.getParentId());
        assertEquals(providerId, result.getProviderId());
        assertEquals(providerType, result.getProviderType());
        assertEquals("val1", result.getConfig().get("key1"));
        assertEquals("val2", result.getConfig().get("key2"));
    }

    @Test
    void testCreateComponentModel_InvalidConfig() {
        String[] invalidConfig = {"key1"}; // Missing value
        assertThrows(IllegalStateException.class, () -> 
            KeycloakModelUtils.createComponentModel("name", "p", "pr", "t", invalidConfig)
        );
    }

    @Test
    void testGetAcceptedClientScopeProtocols() {
        // Case 1: openid-connect
        when(client.getProtocol()).thenReturn("openid-connect");
        var res1 = KeycloakModelUtils.getAcceptedClientScopeProtocols(client);
        assertTrue(res1.contains("openid-connect"));

        // Case 2: null
        when(client.getProtocol()).thenReturn(null);
        var res2 = KeycloakModelUtils.getAcceptedClientScopeProtocols(client);
        assertTrue(res2.contains("openid-connect"));

        // Case 3: other
        when(client.getProtocol()).thenReturn("saml");
        var res3 = KeycloakModelUtils.getAcceptedClientScopeProtocols(client);
        assertEquals(1, res3.size());
        assertTrue(res3.contains("saml"));
    }

    @Test
    void testGetSecretLengthByAuthenticationType() {
        int length = KeycloakModelUtils.getSecretLengthByAuthenticationType("client-secret", "HS512");
        assertTrue(length > 0);
    }
}
