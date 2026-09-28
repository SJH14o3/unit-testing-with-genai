# Coverage Gaps

**File:** `TokenManager.java`

**Branches:** 110 / 342 covered — 232 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 433 | `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel) : ClientSessionContext` |
| 437 | `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel,boolean) : ClientSessionContext` |
| 622 | `isValidScope(KeycloakSession,String,AuthorizationRequestContext,ClientModel) : boolean` |
| 753 | `isValidScope(KeycloakSession,String,ClientModel) : boolean` |
| 757 | `isValidScope(KeycloakSession,String,ClientModel,UserModel) : boolean` |
| 770 | `retrieveSessionConsents(AuthenticatedClientSessionModel) : Set<String>` |
| 780 | `isGrantedConsent(ClientModel,UserModel,UserConsentModel,ClientScopeModel,String,Set<String>) : boolean` |
| 803 | `verifyConsentStillAvailable(KeycloakSession,UserModel,ClientModel,AuthenticatedClientSessionModel,String) : boolean` |
| 842 | `transformAccessTokenResponse(KeycloakSession,AccessTokenResponse,UserSessionModel,ClientSessionContext) : AccessTokenResponse` |
| 847 | `applyMapper(AccessTokenResponse,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessTokenResponse` |
| 854 | `transformUserInfoAccessToken(KeycloakSession,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 859 | `transformUserInfoAccessToken(KeycloakSession,AccessToken,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 864 | `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken` |
| 871 | `transformIntrospectionAccessToken(KeycloakSession,AccessToken,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 876 | `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken` |
| 1011 | `transformIDToken(KeycloakSession,IDToken,UserSessionModel,ClientSessionContext) : IDToken` |
| 1015 | `applyMapper(IDToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : IDToken` |
| 1021 | `initToken(KeycloakSession,RealmModel,ClientModel,UserModel,UserSessionModel,ClientSessionContext,boolean) : AccessToken` |
| 1064 | `getTokenExpiration(RealmModel,ClientModel,UserSessionModel,AuthenticatedClientSessionModel,boolean) : Long` |
| 1108 | `responseBuilder(RealmModel,ClientModel,EventBuilder,KeycloakSession,UserSessionModel,ClientSessionContext) : AccessTokenResponseBuilder` |
| 1138 | `AccessTokenResponseBuilder(RealmModel,ClientModel,EventBuilder,KeycloakSession,UserSessionModel,ClientSessionContext,TokenManager)` |
| 1150 | `getAccessToken() : AccessToken` |
| 1154 | `getRefreshToken() : RefreshToken` |
| 1158 | `getIdToken() : IDToken` |
| 1162 | `getClientSessionCtx() : ClientSessionContext` |
| 1166 | `code(OAuth2Code) : AccessTokenResponseBuilder` |
| 1171 | `requestRefreshToken(RefreshToken) : AccessTokenResponseBuilder` |
| 1176 | `accessToken(AccessToken) : AccessTokenResponseBuilder` |
| 1182 | `removeRefreshToken() : AccessTokenResponseBuilder` |
| 1187 | `responseTokenType(String) : AccessTokenResponseBuilder` |
| 1192 | `offlineToken(boolean) : AccessTokenResponseBuilder` |
| 1197 | `generateAccessToken() : AccessTokenResponseBuilder` |
| 1204 | `generateRefreshToken() : AccessTokenResponseBuilder` |
| 1218 | `generateRefreshToken(RefreshToken,AuthenticatedClientSessionModel) : AccessTokenResponseBuilder` |
| 1241 | `generateRefreshToken(boolean) : void` |
| 1272 | `createOrUpdateOfflineSession() : void` |
| 1282 | `getRefreshTokenConfirmation(AuthenticatedClientSessionModel,AccessToken) : AccessToken.Confirmation` |
| 1306 | `generateIDToken() : AccessTokenResponseBuilder` |
| 1310 | `generateIDToken(boolean) : AccessTokenResponseBuilder` |
| 1337 | `generateAccessTokenHash() : AccessTokenResponseBuilder` |
| 1342 | `generateCodeHash(String) : AccessTokenResponseBuilder` |
| 1349 | `generateStateHash(String) : AccessTokenResponseBuilder` |
| 1354 | `isOfflineToken() : boolean` |
| 1358 | `invokeTokenPostProcessors() : void` |
| 1367 | `build() : AccessTokenResponse` |
| 1448 | `generateOIDCHash(String) : String` |
| 1461 | `formatTokenType(ClientModel,AccessToken) : String` |
| 1470 | `restrictRequestedAudience(AccessToken,Set<String>) : AccessToken` |
| 1488 | `test(JsonWebToken) : boolean` |
| 1528 | `TokenRevocationCheck(KeycloakSession)` |
| 1532 | `test(JsonWebToken) : boolean` |
| 1538 | `verifyLogoutToken(KeycloakSession,String) : LogoutTokenValidationContext` |
| 1579 | `toLogoutToken(String) : Optional<LogoutToken>` |
| 1589 | `validateLogoutTokenAgainstIdpProvider(Stream<OIDCIdentityProvider>,String) : Stream<OIDCIdentityProvider>` |
| 1602 | `getOIDCIdentityProviders(LogoutToken,KeycloakSession) : Stream<OIDCIdentityProvider>` |
| 1624 | `checkLogoutTokenForEvents(LogoutToken) : boolean` |
| 1637 | `validateSelectedOrganization(KeycloakSession,JsonWebToken,UserModel) : void` |

## Partially Covered Methods

### Line 173 — `validateToken(KeycloakSession,UriInfo,ClientConnection,RealmModel,RefreshToken,HttpHeaders,String) : TokenValidation`

| Line | Missing coverage |
|---:|---|
| 185 | `true` branch |
| 207 | `true` branch |
| 224 | `true` branch |
| 230 | `true` branch |

### Line 325 — `verifyRefreshToken(KeycloakSession,RealmModel,ClientModel,HttpRequest,String,boolean) : RefreshToken`

| Line | Missing coverage |
|---:|---|
| 352 | `false` branch |
| 360 | `false` branch |
| 370 | `false` branch |
| 376 | `false` branch |

### Line 398 — `toRefreshToken(KeycloakSession,String) : RefreshToken`

| Line | Missing coverage |
|---:|---|
| 400 | `true` branch |

### Line 418 — `verifyIDTokenSignature(KeycloakSession,String) : IDToken`

| Line | Missing coverage |
|---:|---|
| 420 | `false` branch |

### Line 442 — `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel,Set<String>,boolean) : ClientSessionContext`

| Line | Missing coverage |
|---:|---|
| 448 | `true` branch |
| 451 | `false` branch |
| 452 | `true` branch |
| 465 | `true` branch |

### Line 492 — `detachClientSession(AuthenticatedClientSessionModel) : void`

| Line | Missing coverage |
|---:|---|
| 494 | `true` branch |

### Line 502 — `getAccess(UserModel,ClientModel,Stream<ClientScopeModel>) : Set<RoleModel>`

| Line | Missing coverage |
|---:|---|
| 505 | `false` branch |
| 506 | `true` branch |
| 517 | `true` branch |
| 517 | `false` branch |

### Line 540 — `getRequestedClientScopes(KeycloakSession,String,ClientModel,UserModel) : Stream<ClientScopeModel>`

| Line | Missing coverage |
|---:|---|
| 541 | `true` branch |
| 550 | `true` branch |
| 570 | `true` branch |
| 576 | `true` branch |
| 576 | `false` branch |

### Line 589 — `tryResolveOrganizationScope(KeycloakSession,String,UserModel) : OrganizationScope`

| Line | Missing coverage |
|---:|---|
| 593 | `false` branch |
| 597 | `true` branch |
| 597 | `false` branch |

### Line 607 — `tryResolveOrganizationClientScope(KeycloakSession,UserModel,OrganizationScope,String) : ClientScopeModel`

| Line | Missing coverage |
|---:|---|
| 608 | `false` branch |

### Line 626 — `isValidScope(KeycloakSession,String,AuthorizationRequestContext,ClientModel,UserModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 627 | `true` branch |
| 634 | `true` branch |
| 638 | `true` branch |
| 638 | `false` branch |
| 642 | `true` branch |
| 642 | `false` branch |
| 648 | `true` branch |
| 652 | `true` branch |
| 658 | `false` branch |
| 665 | `true` branch |
| 665 | `false` branch |
| 672 | `true` branch |
| 684 | `true` branch |
| 689 | `false` branch |
| 693 | `true` branch |
| 693 | `false` branch |
| 695 | `true` branch |
| 695 | `false` branch |
| 708 | `true` branch |
| 708 | `false` branch |
| 711 | `true` branch |
| 711 | `false` branch |
| 714 | `true` branch |
| 714 | `false` branch |
| 720 | `true` branch |
| 720 | `false` branch |
| 727 | `true` branch |
| 727 | `false` branch |
| 731 | `true` branch |
| 731 | `false` branch |

### Line 740 — `isRepeatableScope(KeycloakSession,ClientScopeModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 742 | `true` branch |
| 746 | `false` branch |
| 750 | `true` branch |

### Line 828 — `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken`

| Line | Missing coverage |
|---:|---|
| 834 | `true` branch |

### Line 883 — `generateUserInfoClaims(AccessToken,UserModel) : Map<String, Object>`

| Line | Missing coverage |
|---:|---|
| 885 | `true` branch |
| 886 | `true` branch |
| 889 | `true` branch |
| 892 | `false` branch |
| 895 | `true` branch |
| 898 | `true` branch |
| 901 | `true` branch |
| 904 | `true` branch |
| 907 | `true` branch |
| 910 | `true` branch |
| 913 | `true` branch |
| 916 | `true` branch |
| 919 | `true` branch |
| 922 | `false` branch |
| 925 | `true` branch |
| 928 | `true` branch |
| 931 | `true` branch |
| 934 | `true` branch |
| 937 | `true` branch |
| 940 | `false` branch |
| 943 | `true` branch |
| 946 | `false` branch |
| 949 | `true` branch |
| 954 | `true` branch |
| 960 | `true` branch |

### Line 1497 — `forModel(ClientModel) : NotBeforeCheck`

| Line | Missing coverage |
|---:|---|
| 1498 | `false` branch |

### Line 1510 — `forModel(RealmModel) : NotBeforeCheck`

| Line | Missing coverage |
|---:|---|
| 1511 | `true` branch |

### Line 1514 — `forModel(KeycloakSession,RealmModel,UserModel) : NotBeforeCheck`

| Line | Missing coverage |
|---:|---|
| 1515 | `true` branch |
