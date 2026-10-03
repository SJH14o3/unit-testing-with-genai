package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsComponentTest {

    @Test
    public void testComponentModelGetter_returnsFunction() {
        java.util.function.Function<KeycloakSessionFactory, ComponentModel> fn = KeycloakModelUtils.componentModelGetter("realm1", "comp1");
        Assertions.assertNotNull(fn);
    }

    @Test
    public void testGetComponentModel_null() {
        KeycloakSessionFactory factory = mock(KeycloakSessionFactory.class);
        KeycloakSession session = mock(KeycloakSession.class);
        RealmProvider realmProvider = mock(RealmProvider.class);
        when(factory.create()).thenReturn(session);
        doReturn(mock(KeycloakTransactionManager.class)).when(session).getTransactionManager();
        when(session.realms()).thenReturn(realmProvider);
        // no realm with that id, so no component model can be resolved
        when(realmProvider.getRealm("realm1")).thenReturn(null);

        ComponentModel result = KeycloakModelUtils.getComponentModel(factory, "realm1", "comp1");
        Assertions.assertNull(result);
    }
}
