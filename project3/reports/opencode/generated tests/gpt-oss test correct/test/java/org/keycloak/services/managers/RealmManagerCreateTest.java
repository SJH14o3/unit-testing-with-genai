package org.keycloak.services.managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.DefaultAuthenticationFlows;
import org.keycloak.models.utils.DefaultClientScopes;
import org.keycloak.models.utils.DefaultRequiredActions;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.utils.ReservedCharValidator;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerCreateTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock RealmModel adminRealm;
    @Mock RoleModel role;
    @Mock ClientModel client;
    @Mock ClientModel masterAdminClient;
    @Mock KeycloakSessionFactory sessionFactory;
    @Mock ClientProvider clientProvider;
    @Mock ClientPolicyManager clientPolicyManager;

    @BeforeEach
    void setUp() {
        when(session.realms()).thenReturn(realmProvider);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.clients()).thenReturn(clientProvider);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        when(realmProvider.createRealm(anyString(), anyString())).thenReturn(realm);
        when(realmProvider.getRealmByName(Config.getAdminRealm())).thenReturn(adminRealm);
        when(realm.getId()).thenReturn("realm-id");
        when(realm.getName()).thenReturn("testRealm");
        when(realm.getRole(anyString())).thenReturn(role);
        when(realm.addRole(anyString())).thenReturn(role);
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        when(realm.getComponentsStream(anyString(), anyString())).thenReturn(Stream.empty());
        when(realm.getClientByClientId(any())).thenReturn(null);
        when(realm.getClientByClientId(eq("realm-management"))).thenReturn(client);
        when(client.addRole(anyString())).thenReturn(role);
        when(client.getRole(anyString())).thenReturn(role);
        when(adminRealm.getClientByClientId(any())).thenReturn(masterAdminClient);
        when(adminRealm.getRole(anyString())).thenReturn(role);
        when(masterAdminClient.getRole(anyString())).thenReturn(null);
        when(masterAdminClient.addRole(anyString())).thenReturn(role);
        when(clientProvider.getClientByClientId(any(), anyString())).thenReturn(client);
    }

    @Test
    void createRealm_withNullId_generatesIdAndSetsName() {
        String realmName = "testRealm";
        try (MockedStatic<ReservedCharValidator> rcv = mockStatic(ReservedCharValidator.class);
             MockedStatic<KeycloakModelUtils> kmu = mockStatic(KeycloakModelUtils.class);
             MockedStatic<Profile> profile = mockStatic(Profile.class);
             MockedStatic<DefaultClientScopes> dcs = mockStatic(DefaultClientScopes.class);
             MockedStatic<DefaultAuthenticationFlows> daf = mockStatic(DefaultAuthenticationFlows.class);
             MockedStatic<DefaultRequiredActions> dra = mockStatic(DefaultRequiredActions.class);
             MockedStatic<ProtocolMapperUtils> pmu = mockStatic(ProtocolMapperUtils.class);
             MockedStatic<EntityManagers> em = mockStatic(EntityManagers.class)) {
            rcv.when(() -> ReservedCharValidator.validate(anyString())).thenAnswer(inv -> null);
            kmu.when(() -> KeycloakModelUtils.generateId()).thenReturn("generated-id");
            kmu.when(() -> KeycloakModelUtils.getMasterRealmAdminManagementClientId(anyString()))
                    .thenAnswer(inv -> inv.getArgument(0, String.class) + "-realm");
            kmu.when(() -> KeycloakModelUtils.setupOfflineRole(any())).thenReturn(role);
            kmu.when(() -> KeycloakModelUtils.createPublicClient(any(), anyString())).thenReturn(client);
            kmu.when(() -> KeycloakModelUtils.createManagementClient(any(), anyString())).thenReturn(client);
            profile.when(() -> Profile.isFeatureEnabled(any())).thenReturn(false);
            pmu.when(() -> ProtocolMapperUtils.findLocaleMapper(any())).thenReturn(null);

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
