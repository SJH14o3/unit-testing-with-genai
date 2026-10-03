package org.keycloak.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.Profile;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.connections.jpa.support.EntityManagers;
import org.keycloak.models.*;
import org.keycloak.models.utils.DefaultClientScopes;
import org.keycloak.models.utils.DefaultKeyProviders;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.services.clientregistration.policy.DefaultClientRegistrationPolicies;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class RealmManagerCoverageBoost1Test {

    private KeycloakSession session;
    private RealmProvider model;
    private RealmManager realmManager;
    private KeycloakSessionFactory sessionFactory;
    private KeycloakTransactionManager transactionManager;
    private UserSessionProvider userSessionProvider;
    private AuthenticationSessionProvider authSessionProvider;
    private ClientPolicyManager clientPolicyManager;

    @BeforeEach
    void setUp() {
        session = mock(KeycloakSession.class);
        model = mock(RealmProvider.class);
        sessionFactory = mock(KeycloakSessionFactory.class);
        transactionManager = mock(KeycloakTransactionManager.class);
        userSessionProvider = mock(UserSessionProvider.class);
        authSessionProvider = mock(AuthenticationSessionProvider.class);
        clientPolicyManager = mock(ClientPolicyManager.class);

        when(session.realms()).thenReturn(model);
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(session.getTransactionManager()).thenReturn(transactionManager);
        when(session.sessions()).thenReturn(userSessionProvider);
        when(session.authenticationSessions()).thenReturn(authSessionProvider);
        when(session.clientPolicy()).thenReturn(clientPolicyManager);

        realmManager = new RealmManager(session);
    }

    @Test
    void testSetupBrokerService_Enabled() {
        RealmModel realm = mock(RealmModel.class);
        ClientModel client = mock(ClientModel.class);
        RoleModel role = mock(RoleModel.class);

        try (MockedStatic<Profile> profileMock = Mockito.mockStatic(Profile.class);
             MockedStatic<KeycloakModelUtils> utilsMock = Mockito.mockStatic(KeycloakModelUtils.class)) {
            
            profileMock.when(() -> Profile.isFeatureEnabled(Profile.Feature.IDENTITY_BROKERING_API_V1)).thenReturn(true);
            when(realm.getClientByClientId(Constants.BROKER_SERVICE_CLIENT_ID)).thenReturn(null);
            utilsMock.when(() -> KeycloakModelUtils.createManagementClient(eq(realm), eq(Constants.BROKER_SERVICE_CLIENT_ID)))
                    .thenReturn(client);
            when(client.addRole(anyString())).thenReturn(role);

            realmManager.setupBrokerService(realm);

            verify(client).setEnabled(true);
            verify(client, atLeastOnce()).addRole(anyString());
        }
    }

    private static ClientModel newClientMock() {
        ClientModel client = mock(ClientModel.class);
        when(client.addRole(anyString())).thenAnswer(inv -> mock(RoleModel.class));
        when(client.getRole(anyString())).thenAnswer(inv -> mock(RoleModel.class));
        return client;
    }
}
