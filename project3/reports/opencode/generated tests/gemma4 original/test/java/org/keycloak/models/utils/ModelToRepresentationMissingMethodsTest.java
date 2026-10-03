package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.PermissionTicket;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.model.Scope;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.UserVerifiableCredentialModel;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.OrganizationDomainModel;
import org.keycloak.models.OrganizationModel;
import org.keycloak.models.RequiredActionConfigModel;
import org.keycloak.models.UserConsentModel;
import org.keycloak.models.UserVerifiableCredentialModel;
import org.keycloak.models.oid4vci.IssuedVerifiableCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.representations.idm.organization.OrganizationDomainRepresentation;
import org.keycloak.representations.idm.organization.OrganizationRepresentation;
import org.keycloak.representations.idm.oid4vci.IssuedVerifiableCredentialRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelToRepresentationMissingMethodsTest {

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private GroupModel group;

    @Mock
    private IdentityProviderModel idpModel;

    @Mock
    private IdentityProviderMapperModel idpMapper;

    @Mock
    private AuthorizationProvider authProvider;

    @Mock
    private Policy policy;

    @Mock
    private Resource resource;

    @Mock
    private ResourceServer resourceServer;

    @Mock
    private PermissionTicket permissionTicket;

    @Mock
    private UserConsentModel userConsentModel;

    @Mock
    private UserVerifiableCredentialModel userVerifiableCredentialModel;

    @Mock
    private IssuedVerifiableCredentialModel issuedVerifiableCredentialModel;

    @Mock
    private OrganizationModel organizationModel;

    @Mock
    private OrganizationDomainModel organizationDomainModel;

    @Mock
    private AuthenticationExecutionModel authExecutionModel;

    @Mock
    private RequiredActionConfigModel requiredActionConfigModel;

    @Mock
    private Scope scope;

    @Mock
    private ComponentModel componentModel;

    @Mock
    private ProviderConfigProperty providerConfigProperty;

    @Mock
    private ClientModel clientModel;

    @Mock
    private RealmRepresentation realmRep;

    @Test
    void testGroupHierarchyAndExport() {
        // Testing toGroupHierarchy (all variants via coverage)
        when(group.getId()).thenReturn("g1");
        when(group.getName()).thenReturn("group1");
        when(group.getSubGroupsStream()).thenReturn(Stream.empty());
        
        GroupRepresentation rep = ModelToRepresentation.toGroupHierarchy(group, true);
        assertNotNull(rep);
        assertEquals("g1", rep.getId());

        // Testing exportGroups
        ModelToRepresentation.exportGroups(session, realm, realmRep);
        verify(realm, atLeastOnce()).getDefaultGroupsStream();
    }

    @Test
    void testIdentityProviderMethods() {
        // toRepresentation(KeycloakSession, RealmModel, IdentityProviderModel)
        when(idpModel.getInternalId()).thenReturn("idp-int");
        when(idpModel.getProviderId()).thenReturn("idp-prov");
        when(idpModel.getAlias()).thenReturn("alias");
        when(idpModel.getDisplayName()).thenReturn("display");
        when(idpModel.isEnabled()).thenReturn(true);
        when(idpModel.isLinkOnly()).thenReturn(false);
        when(idpModel.isHideOnLogin()).thenReturn(false);
        when(idpModel.isStoreToken()).thenReturn(false);
        when(idpModel.isTrustEmail()).thenReturn(false);
        when(idpModel.isAuthenticateByDefault()).thenReturn(false);
        when(idpModel.getConfig()).thenReturn(new HashMap<>());
        when(idpModel.isAddReadTokenRoleOnCreate()).thenReturn(false);

        IdentityProviderRepresentation idpRep = ModelToRepresentation.toRepresentation(session, realm, idpModel);
        assertNotNull(idpRep);
        assertEquals("idp-int", idpRep.getInternalId());

        // toRepresentation(IdentityProviderMapperModel)
        when(idpMapper.getName()).thenReturn("mapper-name");
        when(idpMapper.getConfig()).thenReturn(new HashMap<>());
        IdentityProviderMapperRepresentation mapperRep = ModelToRepresentation.toRepresentation(idpMapper);
        assertNotNull(mapperRep);
        assertEquals("mapper-name", mapperRep.getName());
    }

    @Test
    void testAuthorizationMethods() {
        // Policy
        when(policy.getId()).thenReturn("p-id");
        when(policy.getName()).thenReturn("p-name");
        PolicyRepresentation policyRep = ModelToRepresentation.toRepresentation(policy, authProvider);
        assertNotNull(policyRep);

        // Resource
        when(resource.getId()).thenReturn("r-id");
        when(resource.getName()).thenReturn("r-name");
        ResourceRepresentation resRep = ModelToRepresentation.toRepresentation(resource, resourceServer, authProvider);
        assertNotNull(resRep);

        // PermissionTicket
        when(permissionTicket.getId()).thenReturn("pt-id");
        PermissionTicketRepresentation ptRep = ModelToRepresentation.toRepresentation(permissionTicket, authProvider);
        assertNotNull(ptRep);

        // ResourceServer
        when(clientModel.getId()).thenReturn("c-id");
        ResourceServerRepresentation rsRep = ModelToRepresentation.toResourceServerRepresentation(session, clientModel);
        assertNotNull(rsRep);
    }

    @Test
    void testUserAndOrganizationMethods() {
        // UserConsentModel
        when(userConsentModel.getId()).thenReturn("uc-id");
        UserConsentRepresentation ucRep = ModelToRepresentation.toRepresentation(userConsentModel);
        assertNotNull(ucRep);

        // UserVerifiableCredentialModel
        when(userVerifiableCredentialModel.getId()).thenReturn("uvc-id");
        UserVerifiableCredentialRepresentation uvcRep = ModelToRepresentation.toRepresentation(userVerifiableCredentialModel, "some-string");
        assertNotNull(uvcRep);

        // IssuedVerifiableCredentialModel
        when(issuedVerifiableCredentialModel.getId()).thenReturn("ivc-id");
        IssuedVerifiableCredentialRepresentation ivcRep = ModelToRepresentation.toRepresentation(issuedVerifiableCredentialModel, "some-string");
        assertNotNull(ivcRep);

        // OrganizationModel
        when(organizationModel.getId()).thenReturn("org-id");
        when(organizationModel.getName()).thenReturn("org-name");
        OrganizationRepresentation orgRep = ModelToRepresentation.toRepresentation(organizationModel);
        assertNotNull(orgRep);

        // OrganizationDomainModel
        when(organizationDomainModel.getId()).thenReturn("od-id");
        OrganizationDomainRepresentation odRep = ModelToRepresentation.toRepresentation(organizationDomainModel);
        assertNotNull(odRep);
    }

    @Test
    void testOtherModels() {
        // AuthenticationExecutionModel
        when(authExecutionModel.getId()).thenReturn("ae-id");
        AuthenticationExecutionExportRepresentation aeRep = ModelToRepresentation.toRepresentation(authExecutionModel);
        assertNotNull(aeRep);

        // RequiredActionConfigModel
        when(requiredActionConfigModel.getId()).thenReturn("rac-id");
        RequiredActionConfigRepresentation racRep = ModelToRepresentation.toRepresentation(requiredActionConfigModel);
        assertNotNull(racRep);

        // Scope
        when(scope.getId()).thenReturn("s-id");
        when(scope.getName()).thenReturn("s-name");
        ScopeRepresentation scopeRep = ModelToRepresentation.toRepresentation(scope);
        assertNotNull(scopeRep);

        // ComponentModel
        when(componentModel.getId()).thenReturn("comp-id");
        when(componentModel.getManufacturerId()).thenReturn("m-id");
        ComponentRepresentation compRep = ModelToRepresentation.toRepresentation(componentModel, true);
        assertNotNull(compRep);

        // ProviderConfigProperty
        when(providerConfigProperty.getName()).thenReturn("prop-name");
        when(providerConfigProperty.getValue()).thenReturn("prop-value");
        ConfigPropertyRepresentation propRep = ModelToRepresentation.toRepresentation(providerConfigProperty);
        assertNotNull(propRep);
    }
}
