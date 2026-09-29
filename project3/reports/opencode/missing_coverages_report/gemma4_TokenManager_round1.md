# Coverage Gaps

**File:** `TokenManager.java`

**Branches:** 115 / 342 covered — 227 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 426 | `createClientAccessToken(KeycloakSession,RealmModel,ClientModel,UserModel,UserSessionModel,ClientSessionContext,boolean) : AccessToken` |
| 433 | `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel) : ClientSessionContext` |
| 492 | `detachClientSession(AuthenticatedClientSessionModel) : void` |
| 622 | `isValidScope(KeycloakSession,String,AuthorizationRequestContext,ClientModel) : boolean` |
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

## Partially Covered Methods

### Line 173 — `validateToken(KeycloakSession,UriInfo,ClientConnection,RealmModel,RefreshToken,HttpHeaders,String) : TokenValidation`

| Line | Missing coverage |
|---:|---|
| 178 | `true` branch |
| 182 | `true` branch |
| 182 | `false` branch |
| 185 | `true` branch |
| 185 | `false` branch |
| 203 | `true` branch |
| 211 | `true` branch |
| 220 | `true` branch |
| 224 | `true` branch |
| 230 | `true` branch |
| 235 | `true` branch |
| 239 | `true` branch |
| 244 | `true` branch |

### Line 282 — `lookupUserFromStatelessToken(KeycloakSession,RealmModel,AccessToken) : UserModel`

| Line | Missing coverage |
|---:|---|
| 290 | `false` branch |

### Line 299 — `validateTokenReuse(KeycloakSession,RealmModel,AccessToken,AuthenticatedClientSessionModel,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 310 | `false` branch |

### Line 325 — `verifyRefreshToken(KeycloakSession,RealmModel,ClientModel,HttpRequest,String,boolean) : RefreshToken`

| Line | Missing coverage |
|---:|---|
| 336 | `false` branch |
| 352 | `true` branch |
| 365 | `true` branch |
| 365 | `false` branch |
| 370 | `true` branch |
| 370 | `false` branch |
| 376 | `true` branch |

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

### Line 502 — `getAccess(UserModel,ClientModel,Stream<ClientScopeModel>) : Set<RoleModel>`

| Line | Missing coverage |
|---:|---|
| 506 | `true` branch |
| 517 | `false` branch |

### Line 540 — `getRequestedClientScopes(KeycloakSession,String,ClientModel,UserModel) : Stream<ClientScopeModel>`

| Line | Missing coverage |
|---:|---|
| 570 | `true` branch |
| 576 | `true` branch |
| 576 | `false` branch |

### Line 589 — `tryResolveOrganizationScope(KeycloakSession,String,UserModel) : OrganizationScope`

| Line | Missing coverage |
|---:|---|
| 590 | `true` branch |
| 593 | `true` branch |
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
| 638 | `false` branch |
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
| 746 | `true` branch |
| 750 | `true` branch |

### Line 757 — `isValidScope(KeycloakSession,String,ClientModel,UserModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 758 | `true` branch |

### Line 770 — `retrieveSessionConsents(AuthenticatedClientSessionModel) : Set<String>`

| Line | Missing coverage |
|---:|---|
| 771 | `false` branch |
| 773 | `false` branch |

### Line 780 — `isGrantedConsent(ClientModel,UserModel,UserConsentModel,ClientScopeModel,String,Set<String>) : boolean`

| Line | Missing coverage |
|---:|---|
| 782 | `false` branch |
| 785 | `true` branch |
| 786 | `true` branch |
| 786 | `false` branch |
| 792 | `true` branch |

### Line 803 — `verifyConsentStillAvailable(KeycloakSession,UserModel,ClientModel,AuthenticatedClientSessionModel,String) : boolean`

| Line | Missing coverage |
|---:|---|
| 812 | `false` branch |

### Line 828 — `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken`

| Line | Missing coverage |
|---:|---|
| 834 | `true` branch |

### Line 859 — `transformUserInfoAccessToken(KeycloakSession,AccessToken,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken`

| Line | Missing coverage |
|---:|---|
| 861 | `true` branch |

### Line 871 — `transformIntrospectionAccessToken(KeycloakSession,AccessToken,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken`

| Line | Missing coverage |
|---:|---|
| 873 | `true` branch |

### Line 883 — `generateUserInfoClaims(AccessToken,UserModel) : Map<String, Object>`

| Line | Missing coverage |
|---:|---|
| 885 | `true` branch |
| 886 | `false` branch |
| 889 | `false` branch |
| 892 | `false` branch |
| 895 | `false` branch |
| 898 | `false` branch |
| 901 | `true` branch |
| 904 | `true` branch |
| 907 | `false` branch |
| 910 | `true` branch |
| 913 | `true` branch |
| 916 | `true` branch |
| 919 | `false` branch |
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
| 960 | `false` branch |

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

### Line 1637 — `validateSelectedOrganization(KeycloakSession,JsonWebToken,UserModel) : void`

| Line | Missing coverage |
|---:|---|
| 1638 | `false` branch |
| 1645 | `true` branch |
| 1645 | `false` branch |
| 1647 | `true` branch |
| 1647 | `false` branch |
| 1649 | `true` branch |
| 1649 | `false` branch |
| 1653 | `true` branch |
| 1653 | `false` branch |
| 1657 | `true` branch |
| 1657 | `false` branch |
