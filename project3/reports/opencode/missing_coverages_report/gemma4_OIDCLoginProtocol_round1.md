# Coverage Gaps

**File:** `OIDCLoginProtocol.java`

**Branches:** 51 / 72 covered — 21 missed

## Methods Not Entered

| Line | Method |
|---:|---|
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
| 291 | `false` branch |
| 295 | `true` branch |
| 299 | `true` branch |
| 305 | `true` branch |
| 318 | `false` branch |
| 326 | `false` branch |
| 330 | `true` branch |
| 337 | `true` branch |

### Line 378 — `sendError(AuthenticationSessionModel,Error,String) : Response`

| Line | Missing coverage |
|---:|---|
| 380 | `true` branch |

### Line 398 — `buildErrorRedirectUri(String,String,Error,String) : OIDCRedirectUriBuilder`

| Line | Missing coverage |
|---:|---|
| 402 | `false` branch |
| 408 | `false` branch |
| 414 | `false` branch |

### Line 485 — `frontchannelLogout(UserSessionModel,AuthenticatedClientSessionModel) : Response`

| Line | Missing coverage |
|---:|---|
| 487 | `false` branch |

### Line 498 — `finishBrowserLogout(UserSessionModel,AuthenticationSessionModel) : Response`

| Line | Missing coverage |
|---:|---|
| 510 | `true` branch |

### Line 527 — `isAuthTimeExpired(UserSessionModel,AuthenticationSessionModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 537 | `true` branch |

### Line 549 — `isReAuthRequiredForKcAction(UserSessionModel,AuthenticationSessionModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 557 | `true` branch |
