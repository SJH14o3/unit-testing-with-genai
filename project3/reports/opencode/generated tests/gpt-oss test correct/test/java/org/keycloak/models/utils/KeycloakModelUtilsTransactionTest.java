package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;

import java.util.Optional;

public class KeycloakModelUtilsTransactionTest {

    @Test
    public void testRunJobInTransaction_wrapsTask() {
        KeycloakSessionFactory factory = mock(KeycloakSessionFactory.class);
        KeycloakSession session = mock(KeycloakSession.class);
        when(factory.create()).thenReturn(session);
        // stubbed with doReturn(..) so that the stubbing itself is not counted as an invocation
        doReturn(mock(KeycloakTransactionManager.class)).when(session).getTransactionManager();
        KeycloakSessionTask task = mock(KeycloakSessionTask.class);

        KeycloakModelUtils.runJobInTransaction(factory, task);

        verify(factory).create();
        verify(session).getTransactionManager();
        verify(task).run(session);
    }

    @Test
    public void testGetTransactionLimit_default() {
        Optional<Integer> opt = KeycloakModelUtils.getTransactionLimit();
        Assertions.assertFalse(opt.isPresent());
    }
}
