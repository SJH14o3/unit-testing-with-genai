# Coverage Gaps

**File:** `OIDCLoginProtocol.java`

**Branches:** 36 / 72 covered — 36 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 193 | `setSession(KeycloakSession) : OIDCLoginProtocol` |
| 199 | `setRealm(RealmModel) : OIDCLoginProtocol` |
| 205 | `setUriInfo(UriInfo) : OIDCLoginProtocol` |
| 211 | `setHttpHeaders(HttpHeaders) : OIDCLoginProtocol` |
| 217 | `setEventBuilder(EventBuilder) : OIDCLoginProtocol` |
| 421 | `getClientData(AuthenticationSessionModel) : ClientData` |
| 475 | `backchannelLogout(UserSessionModel,AuthenticatedClientSessionModel) : Response` |
| 485 | `frontchannelLogout(UserSessionModel,AuthenticatedClientSessionModel) : Response` |
| 498 | `finishBrowserLogout(UserSessionModel,AuthenticationSessionModel) : Response` |
| 595 | `close() : void` |

## Partially Covered Methods

### Line 227 — `authenticated(AuthenticationSessionModel,UserSessionModel,ClientSessionContext) : Response`

| Line | Missing coverage |
|---:|---|
| 247 | `false` branch |
| 250 | `false` branch |
| 258 | `true` branch |
| 260 | `true` branch |
| 260 | `false` branch |
| 268 | `false` branch |
| 291 | `false` branch |
| 295 | `false` branch |
| 299 | `false` branch |
| 305 | `false` branch |
| 318 | `true` branch |
| 318 | `false` branch |
| 326 | `false` branch |
| 330 | `false` branch |
| 337 | `true` branch |

### Line 373 — `isIdTokenAsDetachedSignature(ClientModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 374 | `true` branch |

### Line 398 — `buildErrorRedirectUri(String,String,Error,String) : OIDCRedirectUriBuilder`

| Line | Missing coverage |
|---:|---|
| 408 | `false` branch |
| 414 | `false` branch |

### Line 527 — `isAuthTimeExpired(UserSessionModel,AuthenticationSessionModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 528 | `true` branch |
| 537 | `true` branch |
| 540 | `false` branch |

### Line 549 — `isReAuthRequiredForKcAction(UserSessionModel,AuthenticationSessionModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 550 | `false` branch |
| 553 | `true` branch |
| 557 | `true` branch |
