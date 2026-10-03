package org.keycloak.protocol.oidc;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.keycloak.*;
import org.keycloak.common.Profile;
import org.keycloak.common.Profile.Feature;
import org.keycloak.common.Profile.ProfileName;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.representations.AccessToken;
import org.keycloak.common.VerificationException;
import org.keycloak.models.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenManagerStage2Test {

    @Mock KeycloakSession session;
    @Mock RealmModel realm;
    @Mock UserModel user;
    @Mock UserProvider userProvider;
    @Mock AccessToken token;

    TokenManager tokenManager = new TokenManager();

    @BeforeAll
    static void initProfile() {
        Map<Feature, Boolean> features = new EnumMap<>(Feature.class);
        for (Feature feature : Feature.values()) {
            features.put(feature, false);
        }
        Profile.init(ProfileName.DEFAULT, features);
    }

    @BeforeEach
    void setUp() {
        when(session.users()).thenReturn(userProvider);
    }

    /**
     * {@code isUserValid} delegates to {@code validateUserNotBefore}, which builds a
     * {@link TokenVerifier} via the static {@code TokenVerifier.createWithoutSignature}. The verifier
     * mock answers {@code RETURNS_SELF} so the fluent {@code withChecks(..)} chain keeps returning the
     * same mock whatever number of predicates is passed.
     */
    private MockedStatic<TokenVerifier> mockTokenVerifier() {
        TokenVerifier<AccessToken> verifier = mock(TokenVerifier.class, Answers.RETURNS_SELF);
        MockedStatic<TokenVerifier> tokenVerifier = mockStatic(TokenVerifier.class);
        tokenVerifier.when(() -> TokenVerifier.createWithoutSignature(token)).thenReturn(verifier);
        return tokenVerifier;
    }

    @Test
    void isUserValid_nullUser() {
        assertFalse(tokenManager.isUserValid(session, realm, token, null));
    }

    @Test
    void isUserValid_disabledUser() {
        when(user.isEnabled()).thenReturn(false);
        assertFalse(tokenManager.isUserValid(session, realm, token, user));
    }

    @Test
    void isUserValid_validUser() {
        when(user.isEnabled()).thenReturn(true);
        // The "not before" check performed by validateUserNotBefore passes
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            assertTrue(tokenManager.isUserValid(session, realm, token, user));
        }
    }

    @Test
    void validateUserNotBefore_validToken() throws VerificationException {
        try (MockedStatic<TokenVerifier> tvStatic = mockTokenVerifier()) {
            assertTrue(tokenManager.validateUserNotBefore(session, realm, token, user));
        }
    }

    @Test
    void validateUserNotBefore_notBeforeFails() throws VerificationException {
        TokenVerifier<AccessToken> verifier = mock(TokenVerifier.class, Answers.RETURNS_SELF);
        doThrow(new VerificationException("Not before")).when(verifier).verify();
        try (MockedStatic<TokenVerifier> tvStatic = mockStatic(TokenVerifier.class)) {
            tvStatic.when(() -> TokenVerifier.createWithoutSignature(token)).thenReturn(verifier);
            assertFalse(tokenManager.validateUserNotBefore(session, realm, token, user));
        }
    }
}
