# Coverage Gaps

**File:** `AuthorizationEndpoint.java`

**Branches:** 43 / 60 covered — 17 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 111 | `buildPost() : Response` |
| 422 | `buildRegister() : Response` |
| 435 | `buildForgotCredential() : Response` |

## Partially Covered Methods

### Line 135 — `process(MultivaluedMap<String, String>) : Response`

| Line | Missing coverage |
|---:|---|
| 178 | `false` branch |
| 182 | `false` branch |
| 231 | `true` branch |
| 232 | `true` branch |
| 232 | `false` branch |

### Line 250 — `register(String) : AuthorizationEndpoint`

| Line | Missing coverage |
|---:|---|
| 254 | `true` branch |
| 257 | `true` branch |
| 257 | `false` branch |
| 262 | `false` branch |

### Line 280 — `checkClient(String) : void`

| Line | Missing coverage |
|---:|---|
| 306 | `true` branch |

### Line 320 — `redirectErrorToClient(OIDCResponseMode,String,String) : Response`

| Line | Missing coverage |
|---:|---|
| 326 | `false` branch |
| 330 | `true` branch |
| 335 | `false` branch |

### Line 342 — `updateAuthenticationSession() : void`

| Line | Missing coverage |
|---:|---|
| 352 | `true` branch |
| 374 | `false` branch |
| 391 | `false` branch |
| 398 | `true` branch |
