package org.keycloak.models.jpa;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.models.jpa.entities.IssuedVerifiableCredentialEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.jpa.entities.UserVerifiableCredentialEntity;
import org.keycloak.userprofile.Attributes;
import org.keycloak.userprofile.UserProfile;
import org.keycloak.userprofile.UserProfileContext;
import org.keycloak.userprofile.UserProfileProvider;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JpaUserProviderStage3Test {

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
    private UserVerifiableCredentialEntity vcEntity;

    @Mock
    private IssuedVerifiableCredentialEntity issuedVcEntity;

    @Mock
    private TypedQuery<UserVerifiableCredentialEntity> vcTypedQuery;

    @Mock
    private TypedQuery<IssuedVerifiableCredentialEntity> issuedVcTypedQuery;

    @Mock
    private UserProfileProvider profileProvider;

    @Mock
    private UserProfile profile;

    @Mock
    private KeycloakContext context;

    @Mock
    private Attributes attributes;

    private JpaUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JpaUserProvider(session, em);
        when(session.getContext()).thenReturn(context);
    }

    @Test
    void testAddVerifiableCredential_success() {
        String userId = "user-id";
        String clientScopeId = "scope-id";
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel(userId, clientScopeId);
        model.setRevision("rev-1");

        when(em.getReference(UserEntity.class, userId)).thenReturn(userEntity);
        when(em.find(UserEntity.class, userId)).thenReturn(userEntity);
        when(context.getRealm()).thenReturn(realm);
        when(realm.getId()).thenReturn("realm-id");
        when(userEntity.getRealmId()).thenReturn("realm-id");
        when(session.getProvider(UserProfileProvider.class)).thenReturn(profileProvider);
        when(profileProvider.create(eq(UserProfileContext.USER_API), any(UserModel.class))).thenReturn(profile);
        when(profile.getAttributes()).thenReturn(attributes);
        when(attributes.getReadable()).thenReturn(Collections.emptyMap());

        UserVerifiableCredentialModel result = provider.addVerifiableCredential(userId, model);

        assertNotNull(result);
        verify(em).persist(any(UserVerifiableCredentialEntity.class));
        verify(em).flush();
    }

    @Test
    void testAddVerifiableCredential_noScope() {
        UserVerifiableCredentialModel model = new UserVerifiableCredentialModel("user-id", null);
        assertThrows(ModelException.class, () -> provider.addVerifiableCredential("user-id", model));
    }

    @Test
    void testRemoveVerifiableCredential_exists() {
        String userId = "user-id";
        String scopeId = "scope-id";
        jakarta.persistence.Query deleteQuery = mock(jakarta.persistence.Query.class);
        when(em.createNamedQuery("deleteIssuedVcsByUserAndType")).thenReturn(deleteQuery);
        when(deleteQuery.setParameter(anyString(), any())).thenReturn(deleteQuery);

        when(em.createNamedQuery(eq("verifiableCredentialsByUser"), eq(UserVerifiableCredentialEntity.class))).thenReturn(vcTypedQuery);
        when(vcTypedQuery.getResultStream()).thenReturn(Stream.of(vcEntity));
        when(vcEntity.getClientScopeId()).thenReturn(scopeId);

        boolean removed = provider.removeVerifiableCredential(userId, scopeId);

        assertTrue(removed);
        verify(em).remove(vcEntity);
        verify(em).flush();
    }

    @Test
    void testRemoveVerifiableCredential_notExists() {
        String userId = "user-id";
        String scopeId = "scope-id";

        when(em.createNamedQuery(eq("verifiableCredentialsByUser"), eq(UserVerifiableCredentialEntity.class))).thenReturn(vcTypedQuery);
        when(vcTypedQuery.getResultStream()).thenReturn(Stream.empty());

        boolean removed = provider.removeVerifiableCredential(userId, scopeId);

        assertFalse(removed);
    }

    @Test
    void testGetVerifiableCredentialsByUser_exists() {
        String userId = "user-id";
        when(em.createNamedQuery(eq("verifiableCredentialsByUser"), eq(UserVerifiableCredentialEntity.class))).thenReturn(vcTypedQuery);
        when(vcTypedQuery.getResultStream()).thenReturn(Stream.of(vcEntity));
        when(vcEntity.getId()).thenReturn("vc-id");
        when(vcEntity.getClientScopeId()).thenReturn("scope-id");

        Stream<UserVerifiableCredentialModel> stream = provider.getVerifiableCredentialsByUser(userId);

        assertNotNull(stream);
        assertEquals(1, stream.count());
    }

    @Test
    void testGetVerifiableCredentialById_exists() {
        String vcId = "vc-id";
        when(em.find(UserVerifiableCredentialEntity.class, vcId)).thenReturn(vcEntity);
        when(vcEntity.getId()).thenReturn(vcId);
        when(vcEntity.getClientScopeId()).thenReturn("scope-id");

        UserVerifiableCredentialModel result = provider.getVerifiableCredentialById(vcId);

        assertNotNull(result);
        assertEquals("scope-id", result.getClientScopeId());
    }

    @Test
    void testGetVerifiableCredentialById_notExists() {
        when(em.find(UserVerifiableCredentialEntity.class, "missing")).thenReturn(null);
        assertNull(provider.getVerifiableCredentialById("missing"));
    }

    @Test
    void testAddIssuedVerifiableCredential() {
        IssuedVerifiableCredentialModel model = new IssuedVerifiableCredentialModel();
        model.setVerifiableCredentialId("vc-id");
        model.setUserId("user-id");
        model.setClientId("client-id");
        model.setRevision("rev-1");

        when(em.getReference(UserEntity.class, "user-id")).thenReturn(userEntity);

        IssuedVerifiableCredentialModel result = provider.addIssuedVerifiableCredential(model);

        assertNotNull(result);
        verify(em).persist(any(IssuedVerifiableCredentialEntity.class));
        verify(em).flush();
    }

    @Test
    void testRemoveIssuedVerifiableCredential_exists() {
        String vcId = "issued-vc-id";
        when(em.find(IssuedVerifiableCredentialEntity.class, vcId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(issuedVcEntity);
        
        jakarta.persistence.Query mockQuery = mock(jakarta.persistence.Query.class);
        when(em.createNamedQuery(eq("deleteIssuedVcsByUserAndType"))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);

        boolean removed = provider.removeIssuedVerifiableCredential(vcId);

        assertTrue(removed);
        verify(em).remove(issuedVcEntity);
        verify(em).flush();
    }

    @Test
    void testRemoveIssuedVerifiableCredential_notExists() {
        when(em.find(IssuedVerifiableCredentialEntity.class, "none", LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        assertFalse(provider.removeIssuedVerifiableCredential("none"));
    }

    @Test
    void testRemoveExpiredIssuedVerifiableCredentials() {
        jakarta.persistence.Query mockQuery = mock(jakarta.persistence.Query.class);
        when(em.createNamedQuery("deleteExpiredIssuedVcs")).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyLong())).thenReturn(mockQuery);

        provider.removeExpiredIssuedVerifiableCredentials();

        verify(mockQuery).executeUpdate();
    }
}
