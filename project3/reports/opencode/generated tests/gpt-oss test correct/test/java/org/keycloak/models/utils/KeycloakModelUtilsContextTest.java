package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;


import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsContextTest {

    @Test
    public void testCloneContextRealmClientToSession_nullContext() {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakSession targetSession = mock(KeycloakSession.class);
        KeycloakContext origContext = null;
        KeycloakModelUtils.cloneContextRealmClientToSession(origContext, targetSession);
        // verify no interactions
        verifyNoInteractions(session, targetSession);
    }

    @Test
    public void testCloneContextRealmClientToSession_withRealm() {
        KeycloakContext origContext = mock(KeycloakContext.class);
        RealmModel realmModel = mock(RealmModel.class);
        when(origContext.getRealm()).thenReturn(realmModel);
        when(realmModel.getId()).thenReturn("realm-1");

        KeycloakSession session = mock(KeycloakSession.class);
        RealmProvider realmProvider = mock(RealmProvider.class);
        when(session.realms()).thenReturn(realmProvider);
        when(realmProvider.getRealm("realm-1")).thenReturn(realmModel);
        KeycloakContext targetCtx = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(targetCtx);

        KeycloakModelUtils.cloneContextRealmClientToSession(origContext, session);
        verify(targetCtx).setRealm(realmModel);
    }

    @Test
    public void testCloneContextRealmClientToSession_withClient() {
        KeycloakContext origContext = mock(KeycloakContext.class);
        RealmModel realmModel = mock(RealmModel.class);
        ClientModel clientModel = mock(ClientModel.class);
        when(origContext.getRealm()).thenReturn(realmModel);
        when(origContext.getClient()).thenReturn(clientModel);
        when(clientModel.getRealm()).thenReturn(realmModel);
        when(realmModel.getId()).thenReturn("realm-1");

        KeycloakSession session = mock(KeycloakSession.class);
        RealmProvider realmProvider = mock(RealmProvider.class);
        when(session.realms()).thenReturn(realmProvider);
        when(realmProvider.getRealm("realm-1")).thenReturn(realmModel);
        ClientProvider clientProvider = mock(ClientProvider.class);
        when(session.clients()).thenReturn(clientProvider);
        when(clientProvider.getClientById(realmModel, "client-id")).thenReturn(clientModel);
        KeycloakContext targetCtx = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(targetCtx);
        when(clientModel.getId()).thenReturn("client-id");
        when(origContext.getClient()).thenReturn(clientModel);

        KeycloakModelUtils.cloneContextRealmClientToSession(origContext, session);
        verify(targetCtx).setClient(clientModel);
    }

}
