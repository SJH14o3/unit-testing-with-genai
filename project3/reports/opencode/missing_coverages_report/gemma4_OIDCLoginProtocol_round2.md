# Coverage Gaps

**File:** `OIDCLoginProtocol.java`

**Branches:** 64 / 72 covered — 8 missed

## Partially Covered Methods

### Line 227 — `authenticated(AuthenticationSessionModel,UserSessionModel,ClientSessionContext) : Response`

| Line | Missing coverage |
|---:|---|
| 295 | `true` branch |
| 318 | `false` branch |

### Line 398 — `buildErrorRedirectUri(String,String,Error,String) : OIDCRedirectUriBuilder`

| Line | Missing coverage |
|---:|---|
| 402 | `false` branch |
| 408 | `false` branch |
| 414 | `false` branch |

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
