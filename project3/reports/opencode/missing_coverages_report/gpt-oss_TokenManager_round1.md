# Coverage Gaps

**File:** `TokenManager.java`

**Branches:** 59 / 342 covered — 283 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 406 | `verifyIDToken(KeycloakSession,RealmModel,String) : IDToken` |
| 418 | `verifyIDTokenSignature(KeycloakSession,String) : IDToken` |
| 426 | `createClientAccessToken(KeycloakSession,RealmModel,ClientModel,UserModel,UserSessionModel,ClientSessionContext,boolean) : AccessToken` |
| 433 | `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel) : ClientSessionContext` |
| 437 | `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel,boolean) : ClientSessionContext` |
| 442 | `attachAuthenticationSession(KeycloakSession,UserSessionModel,AuthenticationSessionModel,Set<String>,boolean) : ClientSessionContext` |
| 492 | `detachClientSession(AuthenticatedClientSessionModel) : void` |
| 502 | `getAccess(UserModel,ClientModel,Stream<ClientScopeModel>) : Set<RoleModel>` |
| 622 | `isValidScope(KeycloakSession,String,AuthorizationRequestContext,ClientModel) : boolean` |
| 626 | `isValidScope(KeycloakSession,String,AuthorizationRequestContext,ClientModel,UserModel) : boolean` |
| 740 | `isRepeatableScope(KeycloakSession,ClientScopeModel) : boolean` |
| 753 | `isValidScope(KeycloakSession,String,ClientModel) : boolean` |
| 757 | `isValidScope(KeycloakSession,String,ClientModel,UserModel) : boolean` |
| 770 | `retrieveSessionConsents(AuthenticatedClientSessionModel) : Set<String>` |
| 780 | `isGrantedConsent(ClientModel,UserModel,UserConsentModel,ClientScopeModel,String,Set<String>) : boolean` |
| 803 | `verifyConsentStillAvailable(KeycloakSession,UserModel,ClientModel,AuthenticatedClientSessionModel,String) : boolean` |
| 824 | `transformAccessToken(KeycloakSession,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 828 | `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken` |
| 842 | `transformAccessTokenResponse(KeycloakSession,AccessTokenResponse,UserSessionModel,ClientSessionContext) : AccessTokenResponse` |
| 847 | `applyMapper(AccessTokenResponse,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessTokenResponse` |
| 854 | `transformUserInfoAccessToken(KeycloakSession,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 859 | `transformUserInfoAccessToken(KeycloakSession,AccessToken,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 864 | `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken` |
| 871 | `transformIntrospectionAccessToken(KeycloakSession,AccessToken,AccessToken,UserSessionModel,ClientSessionContext) : AccessToken` |
| 876 | `applyMapper(AccessToken,Map.Entry<ProtocolMapperModel, ProtocolMapper>) : AccessToken` |
| 883 | `generateUserInfoClaims(AccessToken,UserModel) : Map<String, Object>` |
| 978 | `TokenCollector(T)` |
| 982 | `supplier() : Supplier<TokenCollector<T>>` |
| 987 | `finisher() : Function<TokenCollector<T>, T>` |
| 992 | `characteristics() : Set<Collector.Characteristics>` |
| 997 | `combiner() : BinaryOperator<TokenCollector<T>>` |
| 1002 | `accumulator() : BiConsumer<TokenCollector<T>, Map.Entry<ProtocolMapperModel, ProtocolMapper>>` |
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
| 182 | `false` branch |
| 185 | `true` branch |
| 203 | `true` branch |
| 207 | `true` branch |
| 224 | `true` branch |
| 230 | `true` branch |

### Line 325 — `verifyRefreshToken(KeycloakSession,RealmModel,ClientModel,HttpRequest,String,boolean) : RefreshToken`

| Line | Missing coverage |
|---:|---|
| 352 | `false` branch |
| 360 | `false` branch |
| 365 | `false` branch |
| 370 | `true` branch |
| 370 | `false` branch |
| 376 | `false` branch |

### Line 398 — `toRefreshToken(KeycloakSession,String) : RefreshToken`

| Line | Missing coverage |
|---:|---|
| 400 | `true` branch |

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
| 590 | `true` branch |
| 593 | `true` branch |
| 593 | `false` branch |
| 597 | `true` branch |
| 597 | `false` branch |

### Line 607 — `tryResolveOrganizationClientScope(KeycloakSession,UserModel,OrganizationScope,String) : ClientScopeModel`

| Line | Missing coverage |
|---:|---|
| 608 | `false` branch |

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
