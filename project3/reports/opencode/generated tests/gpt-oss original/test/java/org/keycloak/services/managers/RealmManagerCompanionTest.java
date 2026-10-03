import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.authorization.fgap.AdminPermissionsSchema;
import org.keycloak.common.Profile;
import org.keycloak.models.*;
import org.keycloak.representations.idm.*;
import org.keycloak.events.*;
import org.keycloak.protocol.ProtocolMapperUtils;

@ExtendWith(MockitoExtension.class)
class RealmManagerCompanionTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider provider;
    @Mock KeycloakSessionFactory sessionFactory;
    @Mock RealmModel realm;
    @Mock RealmRepresentation rep;

    @BeforeEach
    void setup() {
        when(session.realms()).thenReturn(provider);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(rep.getRealm()).thenReturn("demo");
        when(rep.getAdminPermissionsClient()).thenReturn(null);
        when(rep.isAdminPermissionsEnabled()).thenReturn(false);
        when(rep.getClientScopes()).thenReturn(null);
        when(rep.getClients()).thenReturn(null);
    }

    @Test
    void setupRealmDefaults_createsCredentialOfferRole_whenMissing() {
        // Spy a subclass to expose protected method
        class TestRealmManager extends RealmManager {
            TestRealmManager(KeycloakSession s){super(s);}
            @Override protected void setupRealmDefaults(RealmModel r, RealmRepresentation rep){super.setupRealmDefaults(r, rep);} }

        TestRealmManager mgr = new TestRealmManager(session);
        RealmSecurityHeaders headers = BROWSER_SECURITY_HEADERS;
        // Mock profile flag true
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class)) {
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.OID4VC_VCI_REST_CREDENTIAL_OFFER)).thenReturn(true);
            // Ensure role not present
            when(rep.getRoles()).thenReturn(new RealmRepresentation.RealmRoleContainer(){
                @Override public List<RoleRepresentation> getRealm(){return List.of();}
            });
            // Verify expected calls
            mgr.setupRealmDefaults(realm, rep);
            // Role creation should be called via realm.addRole
            verify(realm).addRole(eq(AdminPermissionsSchema.SCHEMA.getName()));
        }
    }

    @Test
    void importRealm_adminFineGrainedAuthzInitializesSchema() {
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class);
             MockedStatic<AdminPermissionsSchema> adminMock = mockStatic(AdminPermissionsSchema.class)) {
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.ADMIN_FINE_GRAINED_AUTHZ_V2)).thenReturn(true);
            when(rep.isAdminPermissionsEnabled()).thenReturn(true);
            when(rep.getAdminPermissionsClient()).thenReturn(null);
            RealmManager mgr = new RealmManager(session);
            mgr.importRealm(rep, () -> {});
            // Schema init should be invoked
            adminMock.verify(() -> AdminPermissionsSchema.SCHEMA.init(eq(session), eq(realm)));
        }
    }

    @Test
    void importRealm_postponeMasterAdminSetup_masterRealm() {
        when(rep.getRealm()).thenReturn(Config.getAdminRealm());
        when(rep.getClients()).thenReturn(List.of(new ClientRepresentation(){
            @Override public String getClientId(){return Config.getMasterClientId();}
        }));
        when(rep.getAdminPermissionsClient()).thenReturn(null);

        // Create spy to capture call count of createMasterAdminManagement
        class SpyRealmManager extends RealmManager {
            SpyRealmManager(KeycloakSession s){super(s);}
            int masterCallCount = 0;
            @Override protected void createMasterAdminManagement(RealmModel r){masterCallCount++; super.createMasterAdminManagement(r);} }
        SpyRealmManager mgr = new SpyRealmManager(session);
        mgr.importRealm(rep, () -> {});
        assertEquals(0, mgr.masterCallCount); // should be postponed and called later
    }

    @Test
    void importRealm_defaultRoleMissing_callsSetupDefaultRole() {
        try (MockedStatic<Profile> profileMock = mockStatic(Profile.class);
             MockedStatic<KeycloakModelUtils> utilsMock = mockStatic(KeycloakModelUtils.class)) {
            profileMock.when(() -> Profile.isFeatureEnabled(any())).thenReturn(false);
            when(rep.getDefaultRole()).thenReturn(null);
            // Ensure default role not present
            when(rep.getRealm()).thenReturn("app");
            RealmManager mgr = new RealmManager(session);
            mgr.importRealm(rep, () -> {});
            utilsMock.verify(() -> KeycloakModelUtils.setupDefaultRole(eq(realm), anyString()));
        }
    }
}
