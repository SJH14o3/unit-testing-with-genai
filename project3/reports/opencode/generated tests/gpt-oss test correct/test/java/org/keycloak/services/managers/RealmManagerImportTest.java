package org.keycloak.services.managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.ModelException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.DefaultAuthenticationFlows;
import org.keycloak.models.utils.DefaultClientScopes;
import org.keycloak.models.utils.DefaultRequiredActions;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.storage.DatastoreProvider;
import org.keycloak.storage.ExportImportManager;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerImportTest {

    @Mock KeycloakSession session;
    @Mock RealmProvider realmProvider;
    @Mock RealmModel realm;
    @Mock RealmModel adminRealm;
    @Mock RoleModel role;
    @Mock ClientModel client;
    @Mock ClientModel masterAdminClient;
    @Mock KeycloakSessionFactory sessionFactory;
    @Mock KeycloakContext context;
    @Mock ClientProvider clientProvider;
    @Mock KeycloakTransactionManager transactionManager;
    @Mock ClientPolicyManager clientPolicyManager;
    @Mock DatastoreProvider datastoreProvider;
    @Mock ExportImportManager exportImportManager;

    @BeforeEach
    void setUp() {
        when(session.realms()).thenReturn(realmProvider);
        when(realmProvider.createRealm(anyString(), anyString())).thenReturn(realm);
        when(realmProvider.getRealmByName(Config.getAdminRealm())).thenReturn(adminRealm);
        when(realm.getId()).thenReturn("realm-id");
        when(realm.getName()).thenReturn("demo");
        when(realm.getRole(anyString())).thenReturn(role);
        when(realm.addRole(anyString())).thenReturn(role);
        when(realm.getClientByClientId(anyString())).thenReturn(null);
        when(realm.getClientByClientId(eq("realm-management"))).thenReturn(client);
        when(realm.getAuthenticationFlowsStream()).thenReturn(Stream.empty());
        when(realm.getRequiredActionProvidersStream()).thenReturn(Stream.empty());
        when(realm.getComponentsStream(anyString(), anyString())).thenReturn(Stream.empty());
        when(client.addRole(anyString())).thenReturn(role);
        when(client.getRole(anyString())).thenReturn(role);
        when(adminRealm.getClientByClientId(anyString())).thenReturn(masterAdminClient);
        when(adminRealm.getRole(anyString())).thenReturn(role);
        when(masterAdminClient.addRole(anyString())).thenReturn(role);
        when(session.getContext()).thenReturn(context);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.clients()).thenReturn(clientProvider);
        when(clientProvider.getClientByClientId(any(), anyString())).thenReturn(client);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);
        when(session.getProvider(DatastoreProvider.class)).thenReturn(datastoreProvider);
        when(datastoreProvider.getExportImportManager()).thenReturn(exportImportManager);
    }

    @Test
    void importRealm_missingId_generatesId() throws Exception {
        RealmRepresentation rep = mock(RealmRepresentation.class);
        when(rep.getId()).thenReturn(" ");
        when(rep.getRealm()).thenReturn("demo");
        when(rep.getClientScopes()).thenReturn(null);
        when(rep.getClients()).thenReturn(Collections.emptyList());

        try (MockedStatic<KeycloakModelUtils> kmu = mockStatic(KeycloakModelUtils.class);
             MockedStatic<Profile> profile = mockStatic(Profile.class);
             MockedStatic<DefaultClientScopes> dcs = mockStatic(DefaultClientScopes.class);
             MockedStatic<DefaultAuthenticationFlows> daf = mockStatic(DefaultAuthenticationFlows.class);
             MockedStatic<DefaultRequiredActions> dra = mockStatic(DefaultRequiredActions.class);
             MockedStatic<ProtocolMapperUtils> pmu = mockStatic(ProtocolMapperUtils.class);
             MockedStatic<EntityManagers> em = mockStatic(EntityManagers.class)) {
            kmu.when(() -> KeycloakModelUtils.generateId()).thenReturn("generated-id");
            kmu.when(() -> KeycloakModelUtils.getMasterRealmAdminManagementClientId(anyString()))
                    .thenAnswer(inv -> inv.getArgument(0, String.class) + "-realm");
            kmu.when(() -> KeycloakModelUtils.setupOfflineRole(any())).thenReturn(role);
            kmu.when(() -> KeycloakModelUtils.createPublicClient(any(), anyString())).thenReturn(client);
            kmu.when(() -> KeycloakModelUtils.createManagementClient(any(), anyString())).thenReturn(client);
            profile.when(() -> Profile.isFeatureEnabled(any())).thenReturn(false);
            pmu.when(() -> ProtocolMapperUtils.findLocaleMapper(any())).thenReturn(null);

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
        when(realmProvider.getRealmByName("demo")).thenReturn(realm);

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
