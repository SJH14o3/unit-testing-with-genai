import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.models.*;
import org.keycloak.models.utils.*;
import org.keycloak.representations.idm.*;
import org.keycloak.protocol.*;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RealmManagerImportTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock RoleModel defaultRole;
    @Mock KeycloakSessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        when(session.realms()).thenReturn(realmProvider);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(realmProvider.createRealm(anyString(), anyString())).thenReturn(realm);
        when(realm.setName(anyString())).thenAnswer(inv -> realm);
        when(session.getContext()).thenReturn(mock(KeycloakContext.class));
    }

    @Test
    void importRealm_missingId_generatesId() throws Exception {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getId()).thenReturn(" ");
        when(rep.getRealm()).thenReturn("demo");
        when(rep.getClientScopes()).thenReturn(null);
        when(rep.getClients()).thenReturn(Collections.emptyList());

        try (MockedStatic<ReservedCharValidator> rcv = mockStatic(ReservedCharValidator.class);
             MockedStatic<KeycloakModelUtils> kmu = mockStatic(KeycloakModelUtils.class);
             MockedStatic<Profile> profile = mockStatic(Profile.class);
             MockedStatic<DefaultClientScopes> dcs = mockStatic(DefaultClientScopes.class);
             MockedStatic<DefaultAuthenticationFlows> daf = mockStatic(DefaultAuthenticationFlows.class);
             MockedStatic<DefaultRequiredActions> dra = mockStatic(DefaultRequiredActions.class);
             MockedStatic<AdminPermissionsSchema> ap = mockStatic(AdminPermissionsSchema.class)) {
            rcv.when(() -> ReservedCharValidator.validate(anyString())).thenAnswer(inv->{});
            kmu.when(() -> KeycloakModelUtils.generateId()).thenReturn("generated-id");
            kmu.when(() -> KeycloakModelUtils.setupDefaultRole(any(), anyString())).thenAnswer(inv->{});
            profile.when(() -> Profile.isFeatureEnabled(any())).thenReturn(false);
            dcs.when(() -> DefaultClientScopes.createDefaultClientScopes(any(), any(), anyBoolean())).thenAnswer(inv->{});
            daf.when(() -> DefaultAuthenticationFlows.addFlows(any())).thenAnswer(inv->{});
            dra.when(() -> DefaultRequiredActions.addActions(any())).thenAnswer(inv->{});
            ap.when(() -> AdminPermissionsSchema.SCHEMA.init(any(), any())).thenAnswer(inv->{});

            RealmManager manager = new RealmManager(session);
            RealmModel created = manager.importRealm(rep, () -> {});
            assertNotNull(created);
            verify(realmProvider).createRealm(eq("generated-id"), eq("demo"));
            verify(realm).setName("demo");
        }
    }

    @Test
    void importRealm_duplicateRealm_throws() {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getRealm()).thenReturn("demo");
        when(rep.getId()).thenReturn("id123");

        when(session.realms().getRealmByName("demo")).thenReturn(realm);
        RealmManager manager = new RealmManager(session);
        assertThrows(ModelDuplicateException.class, () -> manager.importRealm(rep, () -> {}));
    }

    @Test
    void importRealm_emptyName_throws() {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getRealm()).thenReturn(" ");
        RealmManager manager = new RealmManager(session);
        assertThrows(ModelException.class, () -> manager.importRealm(rep, () -> {}));
    }
}
