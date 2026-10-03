package org.keycloak.models.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.jpa.JpaHashUtils;
import org.keycloak.util.JsonSerialization;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class JpaUserProviderIdentityConsentTest {
    @Mock
    private KeycloakSession session;
    @Mock
    private EntityManager em;
    @Mock
    private RealmModel realm;
    @Mock
    private UserModel user;
    @Mock
    private ClientModel client;
    @Mock
    private FederatedIdentityModel fedIdentityModel;
    @Mock
    private FederatedIdentityEntity fedEntity;
    @Mock
    private UserConsentEntity consentEntity;
    @Mock
    private ClientScopeModel clientScope;

    private ArgumentCaptor<UserConsentEntity> consentEntityCaptor = ArgumentCaptor.forClass(UserConsentEntity.class);

    @Test
    public void testAddConsent_localClient() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentModel consentModel = mock(UserConsentModel.class);
        when(client.getId()).thenReturn("local-client");
        when(consentModel.getClient()).thenReturn(client);
        UserConsentEntity captured = new UserConsentEntity();
        // Use ArgumentCaptor
        ArgumentCaptor<UserConsentEntity> captor = ArgumentCaptor.forClass(UserConsentEntity.class);
        provider.addConsent(realm, "userId", consentModel);
        verify(em).persist(captor.capture());
        UserConsentEntity entity = captor.getValue();
        assertEquals("local-client", entity.getClientId());
        assertNull(entity.getClientStorageProvider());
    }

    @Test
    public void testAddConsent_externalClient() {
        JpaUserProvider provider = new JpaUserProvider(session, em);
        UserConsentModel consentModel = mock(UserConsentModel.class);
        when(client.getId()).thenReturn("f:provider:client-id");
        when(consentModel.getClient()).thenReturn(client);
        ArgumentCaptor<UserConsentEntity> captor = ArgumentCaptor.forClass(UserConsentEntity.class);
        provider.addConsent(realm, "userId", consentModel);
        verify(em).persist(captor.capture());
        UserConsentEntity entity = captor.getValue();
        assertEquals("provider", entity.getClientStorageProvider());
        assertEquals("client-id", entity.getExternalClientId());
        assertNull(entity.getClientId());
    }
}
