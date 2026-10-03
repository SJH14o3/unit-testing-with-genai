import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.keycloak.*;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.common.util.Encode;
import org.keycloak.events.*;
import org.keycloak.models.*;
import org.keycloak.models.utils.*;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.representations.idm.*;
import org.keycloak.services.clientregistration.policy.*;
import org.keycloak.storage.*;
import org.keycloak.utils.ReservedCharValidator;
import org.keycloak.utils.StringUtil;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class RealmManagerCreateTest {

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
    }

    @Test
    void createRealm_withNullId_generatesIdAndSetsName() throws Exception {
        String realmName = "testRealm";
        try (MockedStatic<ReservedCharValidator> rcv = mockStatic(ReservedCharValidator.class);
             MockedStatic<KeycloakModelUtils> kmu = mockStatic(KeycloakModelUtils.class);
             MockedStatic<Profile> profile = mockStatic(Profile.class);
             MockedStatic<DefaultClientScopes> dcs = mockStatic(DefaultClientScopes.class);
             MockedStatic<DefaultAuthenticationFlows> daf = mockStatic(DefaultAuthenticationFlows.class);
             MockedStatic<DefaultRequiredActions> dra = mockStatic(DefaultRequiredActions.class);
             MockedStatic<ProtocolMapperUtils> pmu = mockStatic(ProtocolMapperUtils.class);
             MockedStatic<EntityManagers> em = mockStatic(EntityManagers.class);
             MockedStatic<KeycloakModelUtils> kmu2 = mockStatic(KeycloakModelUtils.class)) {
            rcv.when(() -> ReservedCharValidator.validate(anyString())).thenAnswer(inv -> {});
            kmu2.when(() -> KeycloakModelUtils.generateId()).thenReturn("generated-id");
            kmu2.when(() -> KeycloakModelUtils.setupDefaultRole(any(), anyString())).thenAnswer(inv -> {});
            profile.when(() -> Profile.isFeatureEnabled(any())).thenReturn(false);
            dcs.when(() -> DefaultClientScopes.createDefaultClientScopes(any(), any(), eq(true))).thenAnswer(inv -> {});
            daf.when(() -> DefaultAuthenticationFlows.addFlows(any())).thenAnswer(inv -> {});
            dra.when(() -> DefaultRequiredActions.addActions(any())).thenAnswer(inv -> {});
            pmu.when(() -> ProtocolMapperUtils.findLocaleMapper(any())).thenAnswer(inv -> null);
            em.when(() -> EntityManagers.runInBatch(any(), any(), anyBoolean())).thenAnswer(inv -> {});
            kmu2.when(() -> KeycloakModelUtils.createPublicClient(any(), anyString())).thenReturn(mock(ClientModel.class));

            RealmManager manager = new RealmManager(session);
            RealmModel created = manager.createRealm(realmName);
            assertNotNull(created);
            verify(realmProvider).createRealm(eq("generated-id"), eq(realmName));
            verify(realm).setName(realmName);
        }
    }

    @Test
    void createRealm_withProvidedId_andInvalidId_throwsException() {
        String invalidId = "bad id";
        try (MockedStatic<ReservedCharValidator> rcv = mockStatic(ReservedCharValidator.class)) {
            rcv.when(() -> ReservedCharValidator.validate(invalidId)).thenThrow(new IllegalArgumentException("invalid"));
            RealmManager manager = new RealmManager(session);
            assertThrows(IllegalArgumentException.class, () -> manager.createRealm(invalidId, "realm"));
        }
    }

    @Test
    void createRealm_withNameContainingReservedChars_throwsException() {
        String nameWithBadChars = "bad/realm";
        try (MockedStatic<ReservedCharValidator> rcv = mockStatic(ReservedCharValidator.class)) {
            rcv.when(() -> ReservedCharValidator.validate(nameWithBadChars)).thenThrow(new IllegalArgumentException("invalid"));
            RealmManager manager = new RealmManager(session);
            assertThrows(IllegalArgumentException.class, () -> manager.createRealm("id", nameWithBadChars));
        }
    }
}
