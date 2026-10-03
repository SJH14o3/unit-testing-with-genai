package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.common.util.Profile;
import org.keycloak.common.util.Profile.Feature;
import org.keycloak.models.*;
import org.keycloak.models.organization.OrganizationDomainModel;
import org.keycloak.models.organization.OrganizationModel;
import org.keycloak.models.organization.OrganizationProvider;
import org.keycloak.models.identityproviders.IdentityProviderModel;
import org.keycloak.models.identityproviders.IdentityProviderRepresentation;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepresentationToModelIdpOrgTest {

    @Test
    void testToModelIdentityProviderSuccess() {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        RealmModel realm = mock(RealmModel.class);
        AuthenticationFlowModel flow = mock(AuthenticationFlowModel.class);
        
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
        org.keycloak.authorization.IdentityProviderFactory idpFactory = mock(org.keycloak.authorization.IdentityProviderFactory.class);
        when(sessionFactory.getProviderFactory(eq(org.keycloak.broker.provider.IdentityProvider.class), eq("google")))
                .thenReturn(idpFactory);
        
        IdentityProviderModel idpModel = new IdentityProviderModel();
        idpModel.setConfig(new HashMap<>());
        when(idpFactory.createConfig()).thenReturn(idpModel);
        
        // Mock flows
        when(realm.getFlowByAlias("first-flow")).thenReturn(flow);
        when(flow.getId()).thenReturn("first-flow-id");
        when(realm.getFlowByAlias("post-flow")).thenReturn(flow);
        when(flow.getId()).thenReturn("post-flow-id");

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
        rep.setAttributes(Map.of("key", "val"));
        
        OrganizationDomainRepresentation domainRep = new OrganizationDomainRepresentation();
        domainRep.setName("example.com");
        domainRep.setVerified(true);
        rep.setDomains(Set.of(domainRep));

        OrganizationModel model = new OrganizationModel();
        
        OrganizationModel result = RepresentationToModel.toModel(rep, model);

        assertNotNull(result);
        assertEquals("My Org", result.getName());
        assertEquals("my-org", result.getAlias());
        assertTrue(result.isEnabled());
        assertEquals("http://redirect.com", result.getRedirectUrl());
        assertEquals("Org Description", result.getDescription());
        assertEquals("val", result.getAttributes().get("key"));
        assertEquals(1, result.getDomains().size());
        assertTrue(result.getDomains().iterator().next().isVerified());
    }

    @Test
    void testUpdateOrganizationBrokerSuccess() {
        KeycloakSession session = mock(KeycloakSession.class);
        IdentityProviderRepresentation rep = new IdentityProviderRepresentation();
        rep.setAlias("google-idp");
        rep.setConfig(new HashMap<>(Map.of(org.keycloak.models.OrganizationModel.ORGANIZATION_DOMAIN_ATTRIBUTE, "example.com")));

        IdentityProviderModel existingIdp = mock(IdentityProviderModel.class);
        when(existingIdp.getOrganizationId()).thenReturn("org-id");
        
        org.keycloak.models.identityproviders.IdentityProviderProvider idpProvider = mock(org.keycloak.models.identityproviders.IdentityProviderProvider.class);
        when(session.identityProviders()).thenReturn(idpProvider);
        when(idpProvider.getByAlias("google-idp")).thenReturn(existingIdp);

        OrganizationProvider orgProvider = mock(OrganizationProvider.class);
        OrganizationModel org = mock(OrganizationModel.class);
        when(session.getProvider(org.class)).thenReturn(orgProvider);
        when(orgProvider.getById("org-id")).thenReturn(org);
        
        // Mock domain check
        when(org.getDomains()).thenReturn(Set.of(new OrganizationDomainModel("example.com", true)));

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Feature.ORGANIZATION)).thenReturn(true);

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
            org.keycloak.authorization.IdentityProviderFactory idpFactory = mock(org.keycloak.authorization.IdentityProviderFactory.class);
            when(sessionFactory.getProviderFactory(eq(org.keycloak.broker.provider.IdentityProvider.class), anyString())).thenReturn(idpFactory);
            IdentityProviderModel idpModel = new IdentityProviderModel();
            idpModel.setConfig(new HashMap<>());
            when(idpFactory.createConfig()).thenReturn(idpModel);

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
        
        org.keycloak.models.identityproviders.IdentityProviderProvider idpProvider = mock(org.keycloak.models.identityproviders.IdentityProviderProvider.class);
        when(session.identityProviders()).thenReturn(idpProvider);
        when(idpProvider.getByAlias("google-idp")).thenReturn(existingIdp);

        OrganizationProvider orgProvider = mock(OrganizationProvider.class);
        when(session.getProvider(org.class)).thenReturn(orgProvider);
        when(orgProvider.getById("non-existent-org")).thenReturn(null);

        try (MockedStatic<Profile> profile = Mockito.mockStatic(Profile.class)) {
            profile.when(() -> Profile.isFeatureEnabled(Feature.ORGANIZATION)).thenReturn(true);

            KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
            when(session.getKeycloakSessionFactory()).thenReturn(sessionFactory);
            org.keycloak.authorization.IdentityProviderFactory idpFactory = mock(org.keycloak.authorization.IdentityProviderFactory.class);
            when(sessionFactory.getProviderFactory(any(), anyString())).thenReturn(idpFactory);
            when(idpFactory.createConfig()).thenReturn(new IdentityProviderModel());

            assertThrows(IllegalArgumentException.class, () -> RepresentationToModel.toModel(realm, rep, session));
        }
    }
}
