package org.keycloak.models.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.FederatedIdentityEntity;
import org.keycloak.models.jpa.entities.UserConsentEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JpaUserProviderStage2Test {

    @Mock
    private KeycloakSession session;

    @Mock
    private EntityManager em;

    @Mock
    private RealmModel realm;

    @Mock
    private UserModel userModel;

    @Mock
    private UserEntity userEntity;

    @Mock
    private ClientModel clientModel;

    @Mock
    private TypedQuery<UserConsentEntity> consentTypedQuery;

    @Mock
    private TypedQuery<FederatedIdentityEntity> federatedTypedQuery;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JpaUserProvider(session, em);
    }

    @Test
    void testAddFederatedIdentity() {
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        when(realm.getId()).thenReturn("realm-id");
        when(em.getReference(UserEntity.class, userModel.getId())).thenReturn(userEntity);

        provider.addFederatedIdentity(realm, userModel, identity);

        verify(em).persist(any(FederatedIdentityEntity.class));
        verify(em).flush();
    }

    @Test
    void testUpdateFederatedIdentity_exists() {
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        FederatedIdentityEntity federatedEntity = new FederatedIdentityEntity();
        
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setParameter(anyString(), any())).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.getResultList()).thenReturn(List.of(federatedEntity));

        provider.updateFederatedIdentity(realm, userModel, identity);

        verify(em).persist(federatedEntity);
        verify(em).flush();
    }

    @Test
    void testUpdateFederatedIdentity_notExists() {
        FederatedIdentityModel identity = new FederatedIdentityModel("idp", "id", "user", "token");
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setParameter(anyString(), any())).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        provider.updateFederatedIdentity(realm, userModel, identity);

        verify(em, never()).persist(any());
    }

    @Test
    void testRemoveFederatedIdentity_exists() {
        String idp = "idp";
        FederatedIdentityEntity federatedEntity = new FederatedIdentityEntity();

        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setParameter(anyString(), any())).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.getResultList()).thenReturn(List.of(federatedEntity));

        boolean removed = provider.removeFederatedIdentity(realm, userModel, idp);

        assertTrue(removed);
        verify(em).remove(federatedEntity);
        verify(em).flush();
    }

    @Test
    void testRemoveFederatedIdentity_notExists() {
        String idp = "idp";
        when(em.createNamedQuery(eq("findFederatedIdentityByUserAndProvider"), eq(FederatedIdentityEntity.class))).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setParameter(anyString(), any())).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(federatedTypedQuery);
        when(federatedTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        boolean removed = provider.removeFederatedIdentity(realm, userModel, idp);

        assertFalse(removed);
    }

    @Test
    void testAddConsent_localClient() {
        UserConsentModel consent = mock(UserConsentModel.class);
        ClientModel client = mock(ClientModel.class);
        when(consent.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("local-client-id");
        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        provider.addConsent(realm, "user-id", consent);

        verify(em).persist(any(UserConsentEntity.class));
        verify(em, times(2)).flush();
    }

    @Test
    void testAddConsent_externalClient() {
        UserConsentModel consent = mock(UserConsentModel.class);
        ClientModel client = mock(ClientModel.class);
        // Assuming a clientId with a provider prefix simulates external client
        when(consent.getClient()).thenReturn(client);
        when(client.getId()).thenReturn("provider-id:external-id");
        when(em.getReference(UserEntity.class, "provider-id:external-id")).thenReturn(userEntity);

        provider.addConsent(realm, "user-id", consent);

        verify(em).persist(any(UserConsentEntity.class));
        verify(em, times(2)).flush();
    }

    @Test
    void testGetConsentByClient_exists() {
        String clientId = "client-id";
        UserConsentEntity consentEntity = new UserConsentEntity();
        consentEntity.setClientId(clientId);
        
        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(List.of(consentEntity));
        when(realm.getClientById(clientId)).thenReturn(clientModel);

        UserConsentModel result = provider.getConsentByClient(realm, "user-id", clientId);

        assertNotNull(result);
    }

    @Test
    void testGetConsentsStream_exists() {
        String userId = "user-id";
        UserConsentEntity consentEntity = new UserConsentEntity();
        
        when(em.createNamedQuery(eq("userConsentsByUser"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultStream()).thenReturn(Stream.of(consentEntity));
        when(realm.getClientById(anyString())).thenReturn(clientModel);

        Stream<UserConsentModel> stream = provider.getConsentsStream(realm, userId);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testUpdateConsent_exists() {
        String clientId = "client-id";
        UserConsentModel consentModel = mock(UserConsentModel.class);
        when(consentModel.getClient()).thenReturn(clientModel);
        when(clientModel.getId()).thenReturn(clientId);
        
        UserConsentEntity consentEntity = new UserConsentEntity();
        consentEntity.setGrantedClientScopes(Collections.emptyList());

        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(List.of(consentEntity));

        provider.updateConsent(realm, "user-id", consentModel);

        verify(em, atLeastOnce()).flush();
    }

    @Test
    void testUpdateConsent_notExists() {
        String clientId = "client-id";
        UserConsentModel consentModel = mock(UserConsentModel.class);
        when(consentModel.getClient()).thenReturn(clientModel);
        when(clientModel.getId()).thenReturn(clientId);

        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        assertThrows(ModelException.class, () -> provider.updateConsent(realm, "user-id", consentModel));
    }

    @Test
    void testRevokeConsentForClient_exists() {
        String clientId = "client-id";
        UserConsentEntity consentEntity = new UserConsentEntity();

        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(List.of(consentEntity));

        boolean removed = provider.revokeConsentForClient(realm, "user-id", clientId);

        assertTrue(removed);
        verify(em).remove(consentEntity);
        verify(em).flush();
    }

    @Test
    void testRevokeConsentForClient_notExists() {
        String clientId = "client-id";
        when(em.createNamedQuery(eq("userConsentByUserAndClient"), eq(UserConsentEntity.class))).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setParameter(anyString(), any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.setLockMode(any())).thenReturn(consentTypedQuery);
        when(consentTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        boolean removed = provider.revokeConsentForClient(realm, "user-id", clientId);

        assertFalse(removed);
    }
}
