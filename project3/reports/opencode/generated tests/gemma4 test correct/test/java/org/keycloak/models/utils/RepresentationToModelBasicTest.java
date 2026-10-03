package org.keycloak.models.utils;

import org.junit.jupiter.api.Test;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.oid4vc.UserVerifiableCredentialRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class RepresentationToModelBasicTest {

    @Test
    void testRemoveEmptyString() {
        Map<String, String> input = new HashMap<>();
        input.put("key1", "value1");
        input.put("key2", "");
        input.put("key3", null);
        input.put("key4", "  ");

        Map<String, String> result = RepresentationToModel.removeEmptyString(input);

        assertNotNull(result);
        // only null and "" entries are dropped, blank-but-not-empty values are kept
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertFalse(result.containsKey("key2"));
        assertFalse(result.containsKey("key3"));
        assertEquals("  ", result.get("key4"));

        assertNull(RepresentationToModel.removeEmptyString(null));
    }

    @Test
    void testToModelAuthenticationFlow() {
        AuthenticationFlowRepresentation rep = new AuthenticationFlowRepresentation();
        rep.setId("flow-id");
        rep.setAlias("flow-alias");
        rep.setBuiltIn(true);
        rep.setTopLevel(true);
        rep.setProviderId("provider-id");
        rep.setDescription("flow-description");

        AuthenticationFlowModel model = RepresentationToModel.toModel(rep);

        assertEquals("flow-id", model.getId());
        assertTrue(model.isBuiltIn());
        assertTrue(model.isTopLevel());
        assertEquals("provider-id", model.getProviderId());
        assertEquals("flow-alias", model.getAlias());
        assertEquals("flow-description", model.getDescription());
    }

    @Test
    void testToModelAuthenticatorConfig() {
        AuthenticatorConfigRepresentation rep = new AuthenticatorConfigRepresentation();
        rep.setId("config-id");
        rep.setAlias("config-alias");
        Map<String, String> config = new HashMap<>();
        config.put("key", "value");
        rep.setConfig(config);

        AuthenticatorConfigModel model = RepresentationToModel.toModel(rep);

        assertEquals("config-id", model.getId());
        assertEquals("config-alias", model.getAlias());
        assertEquals("value", model.getConfig().get("key"));
    }

    @Test
    void testToModelCredential() {
        CredentialRepresentation rep = new CredentialRepresentation();
        rep.setId("cred-id");
        rep.setType("password");
        rep.setUserLabel("user-label");
        rep.setSecretData("secret");
        rep.setCredentialData("data");
        rep.setCreatedDate(12345L);

        CredentialModel model = RepresentationToModel.toModel(rep);

        assertEquals("cred-id", model.getId());
        assertEquals("password", model.getType());
        assertEquals("user-label", model.getUserLabel());
        assertEquals("secret", model.getSecretData());
        assertEquals("data", model.getCredentialData());
        assertEquals(12345L, model.getCreatedDate());
    }

    @Test
    void testToModelIdentityProviderMapper() {
        IdentityProviderMapperRepresentation rep = new IdentityProviderMapperRepresentation();
        rep.setId("mapper-id");
        rep.setName("mapper-name");
        rep.setIdentityProviderAlias("idp-alias");
        rep.setIdentityProviderMapper("mapper-type");
        Map<String, String> config = new HashMap<>();
        config.put("k", "v");
        rep.setConfig(config);

        IdentityProviderMapperModel model = RepresentationToModel.toModel(rep);

        assertEquals("mapper-id", model.getId());
        assertEquals("mapper-name", model.getName());
        assertEquals("idp-alias", model.getIdentityProviderAlias());
        assertEquals("mapper-type", model.getIdentityProviderMapper());
        assertEquals("v", model.getConfig().get("k"));
    }

    @Test
    void testToModelOrganizationDomain() {
        OrganizationDomainRepresentation rep = new OrganizationDomainRepresentation();
        rep.setName("domain-name");
        rep.setVerified(true);

        OrganizationDomainModel model = RepresentationToModel.toModel(rep);

        assertEquals("domain-name", model.getName());
        assertTrue(model.isVerified());
    }

    @Test
    void testToModelUserVerifiableCredential() {
        UserVerifiableCredentialRepresentation rep = new UserVerifiableCredentialRepresentation();
        rep.setCredentialScopeName("scope");
        rep.setRevision("1");
        rep.setCreatedDate(100L);
        rep.setUpdatedDate(200L);
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("attr", List.of("val"));
        rep.setUserAttributes(attrs);

        RealmModel realm = mock(RealmModel.class);
        ClientScopeModel scope = mock(ClientScopeModel.class);
        when(scope.getId()).thenReturn("scope-id");

        try (MockedStatic<KeycloakModelUtils> utilities = Mockito.mockStatic(KeycloakModelUtils.class)) {
            utilities.when(() -> KeycloakModelUtils.getClientScopeByName(realm, "scope")).thenReturn(scope);

            UserVerifiableCredentialModel model = RepresentationToModel.toModel(rep, realm);

            assertEquals("scope-id", model.getClientScopeId());
            assertEquals("1", model.getRevision());
            assertEquals(100L, model.getCreatedDate());
            assertEquals(200L, model.getUpdatedDate());
            assertEquals(List.of("val"), model.getUserAttributes().get("attr"));
        }
    }

    @Test
    void testCreateRole() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel role = mock(RoleModel.class);
        RoleRepresentation rep = new RoleRepresentation();
        rep.setId("role-id");
        rep.setName("role-name");
        rep.setDescription("role-desc");
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("attr", Collections.singletonList("val"));
        rep.setAttributes(attrs);

        when(realm.addRole("role-id", "role-name")).thenReturn(role);

        RoleModel result = RepresentationToModel.createRole(realm, rep);

        verify(realm).addRole("role-id", "role-name");
        verify(role).setDescription("role-desc");
        verify(role).setAttribute("attr", Collections.singletonList("val"));
        assertEquals(role, result);
    }

    @Test
    void testCreateRoleWithoutId() {
        RealmModel realm = mock(RealmModel.class);
        RoleModel role = mock(RoleModel.class);
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName("role-name");
        rep.setDescription("role-desc");

        when(realm.addRole("role-name")).thenReturn(role);

        RoleModel result = RepresentationToModel.createRole(realm, rep);

        verify(realm).addRole("role-name");
        verify(role).setDescription("role-desc");
        assertEquals(role, result);
    }
}
