package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.broker.provider.IdentityProviderFactory;
import org.keycloak.common.Profile;
import org.keycloak.models.*;
import org.keycloak.organization.OrganizationProvider;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class RepresentationToModelIdpOrgTest {

    @Test
    void testToModelIdentityProviderSuccess() {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        RealmModel realm = mock(RealmModel.class);
        AuthenticationFlowModel firstFlow = mock(AuthenticationFlowModel.class);
        AuthenticationFlowModel postFlow = mock(AuthenticationFlowModel.class);
        
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        
        IdentityProviderRepresentation rep = new IdentityProviderRepresentation();
        rep.setProviderId("google");
        rep.setAlias("google-idp");
        rep.setDisplayName("Google");
        rep.setEnabled(true);
        rep.setLinkOnly(false);
        rep.setHideOnLogin(false);
        rep.setTrustEmail(true);
        rep.setAuthenticateByDefault(true);
        rep.setStoreToken(true);
        rep.setAddReadTokenRoleOnCreate(true);
        rep.setConfig(new HashMap<>(Map.of("some-config", "some-value")));
        rep.setFirstBrokerLoginFlowAlias("first-flow");
        rep.setPostBrokerLoginFlowAlias("post-flow");
        
        // Mocking the provider factory
        IdentityProviderFactory idpFactory = mock(IdentityProviderFactory.class);
        when(sessionFactory.getProviderFactory(eq(org.keycloak.broker.provider.IdentityProvider.class), eq("google")))
                .thenReturn(idpFactory);
        
        IdentityProviderModel idpModel = new IdentityProviderModel();
        idpModel.setConfig(new HashMap<>());
        when(idpFactory.createConfig()).thenReturn(idpModel);
        
        // Mock flows
        when(realm.getFlowByAlias("first-flow")).thenReturn(firstFlow);
        when(firstFlow.getId()).thenReturn("first-flow-id");
        when(realm.getFlowByAlias("post-flow")).thenReturn(postFlow);
        when(postFlow.getId()).thenReturn("post-flow-id");

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.ORGANIZATION)).thenReturn(false);

            IdentityProviderModel result = RepresentationToModel.toModel(realm, rep, session);

            assertNotNull(result);
            assertEquals("google", result.getProviderId());
            assertEquals("google-idp", result.getAlias());
            assertEquals("Google", result.getDisplayName());
            assertTrue(result.isEnabled());
            assertEquals("some-value", result.getConfig().get("some-config"));
            assertEquals("first-flow-id", result.getFirstBrokerLoginFlowId());
            assertEquals("post-flow-id", result.getPostBrokerLoginFlowId());
        }
    }

    @Test
    void testToModelIdentityProviderInvalidId() {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        RealmModel realm = mock(RealmModel.class);
        
        when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getProviderFactory(any(), eq("invalid-id"))).thenReturn(null);

        IdentityProviderRepresentation rep = new IdentityProviderRepresentation();
        rep.setProviderId("invalid-id");

        assertThrows(IllegalArgumentException.class, () -> RepresentationToModel.toModel(realm, rep, session));
    }

    @Test
    void testToModelOrganization() {
        OrganizationRepresentation rep = new OrganizationRepresentation();
        rep.setName("My Org");
        rep.setAlias("my-org");
        rep.setEnabled(true);
        rep.setRedirectUrl("http://redirect.com");
        rep.setDescription("Org Description");
        rep.setAttributes(Map.of("key", List.of("val")));
        
        OrganizationDomainRepresentation domainRep = new OrganizationDomainRepresentation();
        domainRep.setName("example.com");
        domainRep.setVerified(true);
        rep.addDomain(domainRep);

        OrganizationModel model = mock(OrganizationModel.class);
        
        OrganizationModel result = RepresentationToModel.toModel(rep, model);

        assertNotNull(result);
        assertSame(model, result);
        verify(model).setName("My Org");
        verify(model).setAlias("my-org");
        verify(model).setEnabled(true);
        verify(model).setRedirectUrl("http://redirect.com");
        verify(model).setDescription("Org Description");
        verify(model).setAttributes(Map.of("key", List.of("val")));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<OrganizationDomainModel>> domains = ArgumentCaptor.forClass(Set.class);
        verify(model).setDomains(domains.capture());
        assertEquals(1, domains.getValue().size());
        OrganizationDomainModel domain = domains.getValue().iterator().next();
        assertEquals("example.com", domain.getName());
        assertTrue(domain.isVerified());
    }

    @Test
    void testUpdateOrganizationBrokerSuccess() {
        KeycloakSession session = mock(KeycloakSession.class);
        IdentityProviderRepresentation rep = new IdentityProviderRepresentation();
        rep.setProviderId("google");
        rep.setAlias("google-idp");
        rep.setConfig(new HashMap<>(Map.of(org.keycloak.models.OrganizationModel.ORGANIZATION_DOMAIN_ATTRIBUTE, "example.com")));

        IdentityProviderModel existingIdp = mock(IdentityProviderModel.class);
        when(existingIdp.getOrganizationId()).thenReturn("org-id");

        IdentityProviderStorageProvider idpProvider = mock(IdentityProviderStorageProvider.class);
        when(session.identityProviders()).thenReturn(idpProvider);
        when(idpProvider.getByAlias("google-idp")).thenReturn(existingIdp);

        OrganizationProvider orgProvider = mock(OrganizationProvider.class);
        OrganizationModel org = mock(OrganizationModel.class);
        when(session.getProvider(OrganizationProvider.class)).thenReturn(orgProvider);
        when(orgProvider.getById("org-id")).thenReturn(org);
        
        // Mock domain check
        when(org.getDomains()).thenReturn(Stream.of(new OrganizationDomainModel("example.com", true)));

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.ORGANIZATION)).thenReturn(true);

            // We need to invoke the private method via a public one that calls it if possible, 
            // but updateOrganizationBroker is private. 
            // However, toModel for IdentityProvider calls it.
            // Let's use a workaround to test it if I can't change visibility.
            // Since I cannot change the source, I will try to test it via toModel.
            
            // Re-setup for toModel call which invokes updateOrganizationBroker
            // Note: updateOrganizationBroker is private. It's called in toModel(RealmModel, IdentityProviderRepresentation, KeycloakSession)
            // But toModel for IdentityProvider is actually: public static IdentityProviderModel toModel(RealmModel realm, IdentityProviderRepresentation representation, KeycloakSession session)
            
            // Let's re-mock the IdentityProviderFactory for toModel
            KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
            when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
            IdentityProviderFactory idpFactory = mock(IdentityProviderFactory.class);
            when(sessionFactory.getProviderFactory(eq(org.keycloak.broker.provider.IdentityProvider.class), anyString())).thenReturn(idpFactory);
            IdentityProviderModel idpModel = new IdentityProviderModel();
            idpModel.setConfig(new HashMap<>());
            when(idpFactory.createConfig()).thenReturn(idpModel);
            RealmModel realm = mock(RealmModel.class);
            RepresentationToModel.toModel(realm, rep, session);

            assertEquals("org-id", rep.getOrganizationId());
            verify(orgProvider).getById("org-id");
        }
    }

    @Test
    void testUpdateOrganizationBrokerInvalidOrg() {
        KeycloakSession session = mock(KeycloakSession.class);
        IdentityProviderRepresentation rep = new IdentityProviderRepresentation();
        rep.setAlias("google-idp");
        rep.setOrganizationId("non-existent-org");
        rep.setConfig(new HashMap<>());

        IdentityProviderModel existingIdp = mock(IdentityProviderModel.class);
        when(existingIdp.getOrganizationId()).thenReturn(null);

        IdentityProviderStorageProvider idpProvider = mock(IdentityProviderStorageProvider.class);
        when(session.identityProviders()).thenReturn(idpProvider);
        when(idpProvider.getByAlias("google-idp")).thenReturn(existingIdp);

        OrganizationProvider orgProvider = mock(OrganizationProvider.class);
        when(orgProvider.getById("non-existent-org")).thenReturn(null);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Profile.Feature.ORGANIZATION)).thenReturn(true);

            KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
            when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
            IdentityProviderFactory idpFactory = mock(IdentityProviderFactory.class);
            when(sessionFactory.getProviderFactory(any(), anyString())).thenReturn(idpFactory);
            when(idpFactory.createConfig()).thenReturn(new IdentityProviderModel());
            RealmModel realm = mock(RealmModel.class);

            assertThrows(IllegalArgumentException.class, () -> RepresentationToModel.toModel(realm, rep, session));
        }
    }
}
