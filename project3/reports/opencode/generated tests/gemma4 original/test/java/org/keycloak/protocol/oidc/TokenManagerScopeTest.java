package org.keycloak.protocol.oidc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.keycloak.models.utils.RoleUtils;
import org.keycloak.protocol.oidc.scope.DefaultScopeType;
import org.keycloak.organization.protocol.mappers.oidc.OrganizationMembershipMapper;
import org.keycloak.organization.protocol.mappers.oidc.OrganizationScope;
import org.keycloak.organization.utils.Organizations;
import org.keycloak.services.util.AuthorizationContextUtil;
import org.keycloak.common.Profile;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerScopeTest {

    @Mock
    KeycloakSession session;
    @Mock
    RealmModel realm;
    @Mock
    ClientModel client;
    @Mock
    UserModel user;
    @Mock
    ClientScopeModel clientScope;
    @Mock
    RoleModel role;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager = new TokenManager();
    }

    @Test
    void parseScopeParameter_Success() {
        Stream<String> result = TokenManager.parseScopeParameter("openid profile email openid");
        List<String> list = result.collect(Collectors.toList());
        assertEquals(3, list.size());
        assertTrue(list.contains("openid"));
        assertTrue(list.contains("profile"));
        assertTrue(list.contains("email"));
    }

    @Test
    void getAccess_FullScopeAllowed_Success() {
        when(client.isFullScopeAllowed()).thenReturn(true);
        Set<RoleModel> userRoles = Set.of(role);
        
        try (MockedStatic<RoleUtils> roleUtilsMock = mockStatic(RoleUtils.class)) {
            roleUtilsMock.when(() -> RoleUtils.getDeepUserRoleMappings(user)).thenReturn(userRoles);
            
            Set<RoleModel> result = TokenManager.getAccess(user, client, Stream.empty());
            
            assertEquals(userRoles, result);
        }
    }

    @Test
    void getAccess_FullScopeNotAllowed_Success() {
        when(client.isFullScopeAllowed()).thenReturn(false);
        Set<RoleModel> userRoles = Set.of(role);
        Set<RoleModel> scopeRoles = Set.of(role);
        
        try (MockedStatic<RoleUtils> roleUtilsMock = mockStatic(RoleUtils.class)) {
            roleUtilsMock.when(() -> RoleUtils.getDeepUserRoleMappings(user)).thenReturn(userRoles);
            roleUtilsMock.when(() -> RoleUtils.expandCompositeRolesStream(any())).thenReturn(scopeRoles.stream());

            when(client.getRolesStream()).thenReturn(scopeRoles.stream());

            Set<RoleModel> result = TokenManager.getAccess(user, client, Stream.of(clientScope));
            
            // Note: roleMappings.retainAll(scopeMappings.collect(...)) modifies the set returned by RoleUtils.
            // Since we mocked it to return a specific set, we check the intersection.
            assertTrue(result.contains(role));
        }
    }

    @Test
    void getRequestedClientScopes_NoParam_Success() {
        ClientScopeModel defaultScope = mock(ClientScopeModel.class);
        Map<String, ClientScopeModel> defaultScopes = Map.of("default", defaultScope);
        
        when(client.getClientScopes(true)).thenReturn(defaultScopes);
        when(client.getId()).thenReturn("client-id");

        Stream<ClientScopeModel> result = TokenManager.getRequestedClientScopes(session, null, client, user);
        List<ClientScopeModel> list = result.collect(Collectors.toList());

        assertTrue(list.contains(defaultScope));
        assertTrue(list.contains(client));
    }

    @Test
    void isValidScope_NullScopes_ReturnsTrue() {
        assertTrue(TokenManager.isValidScope(session, null, client));
    }

    @Test
    void isValidScope_EmptyScopes_ReturnsTrue() {
        assertTrue(TokenManager.isValidScope(session, "", client));
    }

    @Test
    void isValidScope_InvalidScope_ReturnsFalse() {
        when(client.isConsentRequired()).thenReturn(false);
        // Mocking getRequestedClientScopes to return nothing
        // Since it's static and called inside isValidScope, we might need to mock it if we want to isolate.
        // But it's easier to control via client and session mocks.
        
        // Let's try a different approach: providing a client that doesn't have the scope.
        when(client.getParameterizedClientScope(anyString())).thenReturn(null);
        
        // We need to mock TokenManager.parseScopeParameter or the behavior of getRequestedClientScopes.
        // Since getRequestedClientScopes is static, we can use mockStatic.
        try (MockedStatic<TokenManager> tokenManagerMock = mockStatic(TokenManager.class, CALLS_REAL_METHODS)) {
            tokenManagerMock.when(() -> TokenManager.getRequestedClientScopes(any(), anyString(), any(), any()))
                    .thenReturn(Stream.empty());

            assertFalse(TokenManager.isValidScope(session, "invalid-scope", client));
        }
    }

    @Test
    void isValidScope_OrganizationScopeMixing_ReturnsFalse() {
        try (MockedStatic<Organizations> orgsMock = mockStatic(Organizations.class)) {
            orgsMock.when(() -> Organizations.isEnabled(session)).thenReturn(true);
            
            // We need to mock OrganizationScope.valueOfScope to return different types
            try (MockedStatic<OrganizationScope> orgScopeMock = mockStatic(OrganizationScope.class)) {
                orgScopeMock.when(() -> OrganizationScope.valueOfScope(eq(session), eq("org-any")))
                        .thenReturn(mock(OrganizationScope.class)); // This is just a placeholder
                orgScopeMock.when(() -> OrganizationScope.valueOfScope(eq(session), eq("org-specific")))
                        .thenReturn(mock(OrganizationScope.class)); // This is just a placeholder
                
                // Actually, the check is orgScopeTypes.size() > 1.
                // We need to ensure they are different instances or behave differently in the Set.
                // Since they are mocks, they are different by default.
                
                // However, the loop in isValidScope calls OrganizationScope.valueOfScope for each scope.
                // If we return two different mock objects, size will be 2.
                
                assertFalse(TokenManager.isValidScope(session, "org-any org-specific", client));
            }
        }
    }

    @Test
    void isRepeatableScope_FromAttribute_ReturnsTrue() {
        when(clientScope.getAttribute(ClientScopeModel.IS_REPEATABLE_SCOPE)).thenReturn("true");
        assertTrue(TokenManager.isRepeatableScope(session, clientScope));
    }

    @Test
    void isRepeatableScope_FromAttribute_ReturnsFalse() {
        when(clientScope.getAttribute(ClientScopeModel.IS_REPEATABLE_SCOPE)).thenReturn("false");
        assertFalse(TokenManager.isRepeatableScope(session, clientScope));
    }

    @Test
    void isRepeatableScope_ProviderFallback_ReturnsTrue() {
        when(clientScope.getAttribute(ClientScopeModel.IS_REPEATABLE_SCOPE)).thenReturn(null);
        when(clientScope.getAttribute(ClientScopeModel.PARAMETERIZED_SCOPE_TYPE)).thenReturn(DefaultScopeType.TYPE);
        
        ParameterizedScopeTypeProvider provider = mock(ParameterizedScopeTypeProvider.class);
        when(session.getProvider(eq(ParameterizedScopeTypeProvider.class), eq(DefaultScopeType.TYPE))).thenReturn(provider);
        when(provider.isRepeatable()).thenReturn(true);

        assertTrue(TokenManager.isRepeatableScope(session, clientScope));
    }
}

// Mock interface since it's not in the snippet but used in the code
interface ParameterizedScopeTypeProvider {
    boolean isRepeatable();
}
