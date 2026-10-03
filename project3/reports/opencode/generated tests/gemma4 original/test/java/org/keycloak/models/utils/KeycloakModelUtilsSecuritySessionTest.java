package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.sessions.KeycloakSession;
import org.keycloak.transaction.JtaTransactionManagerLookup;
import org.keycloak.transaction.TransactionManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakModelUtilsSecuritySessionTest {

    @Mock
    KeycloakSessionFactory factory;

    @Mock
    KeycloakSession session;

    @Mock
    KeycloakContext context;

    @Mock
    TransactionManager transactionManager;

    @Mock
    JtaTransactionManagerLookup jtaLookup;

    @Mock
    RealmModel realm;

    @Mock
    ClientModel client;

    @Mock
    IdentityProviderModel idp;

    @Mock
    KeycloakSessionTask task;

    @Test
    void testGetPublicKey_Null() {
        assertNull(KeycloakModelUtils.getPublicKey(null));
    }

    @Test
    void testGetCertificate_Null() {
        assertNull(KeycloakModelUtils.getCertificate(null));
    }

    @Test
    void testGetPrivateKey_Null() {
        assertNull(KeycloakModelUtils.getPrivateKey(null));
    }

    @Test
    void testGetSecretKey_Null() {
        assertNull(KeycloakModelUtils.getSecretKey(null));
    }

    @Test
    void testGetSecretKey_Valid() {
        String secret = "secret";
        Key key = KeycloakModelUtils.getSecretKey(secret);
        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }

    @Test
    void testGetMasterRealmAdminManagementClientId() {
        assertEquals("myrealm-realm", KeycloakModelUtils.getMasterRealmAdminManagementClientId("myrealm"));
    }

    @Test
    void testSetTransactionLimit_Jta() throws Exception {
        when(factory.getProviderFactory(JtaTransactionManagerLookup.class)).thenReturn(jtaLookup);
        when(jtaLookup.getTransactionManager()).thenReturn(transactionManager);

        KeycloakModelUtils.setTransactionLimit(factory, 10);

        verify(transactionManager).setTransactionTimeout(10);
        assertEquals(Optional.of(10), KeycloakModelUtils.getTransactionLimit());

        KeycloakModelUtils.setTransactionLimit(factory, 0);
        assertEquals(Optional.empty(), KeycloakModelUtils.getTransactionLimit());
    }

    @Test
    void testGetTransactionLimit_Empty() {
        assertEquals(Optional.empty(), KeycloakModelUtils.getTransactionLimit());
    }

    @Test
    void testRunJobInTransactionWithTimeout() {
        when(factory.create()).thenReturn(session);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(factory.getProviderFactory(JtaTransactionManagerLookup.class)).thenReturn(jtaLookup);
        when(jtaLookup.getTransactionManager()).thenReturn(transactionManager);

        KeycloakModelUtils.runJobInTransactionWithTimeout(factory, task, 5);

        verify(transactionManager).setTransactionTimeout(5);
        verify(task).run(session);
        verify(transactionManager).setTransactionTimeout(0);
    }

    @Test
    void testGetComponentModel() {
        when(factory.create()).thenReturn(session);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.realms()).thenReturn(mock(RealmProvider.class));
        when(session.realms().getRealm("realm-id")).thenReturn(realm);
        when(realm.getComponent("comp-id")).thenReturn(mock(ComponentModel.class));

        ComponentModel result = KeycloakModelUtils.getComponentModel(factory, "realm-id", "comp-id");

        assertNotNull(result);
    }

    @Test
    void testComponentModelGetter() {
        when(factory.create()).thenReturn(session);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.realms()).thenReturn(mock(RealmProvider.class));
        when(session.realms().getRealm("realm-id")).thenReturn(realm);
        when(realm.getComponent("comp-id")).thenReturn(mock(ComponentModel.class));

        Function<KeycloakSessionFactory, ComponentModel> getter = KeycloakModelUtils.componentModelGetter("realm-id", "comp-id");
        ComponentModel result = getter.apply(factory);

        assertNotNull(result);
    }

    @Test
    void testGetIdentityProviderDisplayName_Success() {
        when(idp.getDisplayName()).thenReturn("Real Name");
        assertEquals("Real Name", KeycloakModelUtils.getIdentityProviderDisplayName(session, idp));
    }

    @Test
    void testGetIdentityProviderDisplayName_Fallback() {
        when(idp.getDisplayName()).thenReturn(null);
        when(idp.getAlias()).thenReturn("alias");
        when(idp.getProviderId()).thenReturn("social");
        when(session.getKeycloakSessionFactory()).thenReturn(factory);
        
        SocialIdentityProviderFactory<?> socialFactory = mock(SocialIdentityProviderFactory.class);
        when(factory.getProviderFactory(eq(SocialIdentityProvider.class), eq("social"))).thenReturn(socialFactory);
        when(socialFactory.getName()).thenReturn("Social Name");

        assertEquals("Social Name", KeycloakModelUtils.getIdentityProviderDisplayName(session, idp));
    }

    @Test
    void testCloneContextToSession_NullContext() {
        assertDoesNotThrow(() -> KeycloakModelUtils.cloneContextRealmClientToSession(null, session));
    }
}
