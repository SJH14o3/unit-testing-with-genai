# Coverage Gaps

**File:** `AuthorizationEndpoint.java`

**Branches:** 25 / 60 covered — 35 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 404 | `buildAuthorizationCodeAuthorizationResponse(String) : Response` |
| 422 | `buildRegister() : Response` |
| 435 | `buildForgotCredential() : Response` |

## Partially Covered Methods

### Line 135 — `process(MultivaluedMap<String, String>) : Response`

| Line | Missing coverage |
|---:|---|
| 175 | `false` branch |
| 178 | `true` branch |
| 178 | `false` branch |
| 182 | `true` branch |
| 182 | `false` branch |
| 202 | `true` branch |
| 202 | `false` branch |
| 231 | `true` branch |
| 231 | `false` branch |
| 232 | `true` branch |
| 232 | `false` branch |

### Line 250 — `register(String) : AuthorizationEndpoint`

| Line | Missing coverage |
|---:|---|
| 254 | `true` branch |
| 257 | `true` branch |
| 257 | `false` branch |
| 262 | `false` branch |

### Line 269 — `forgotCredentials() : AuthorizationEndpoint`

| Line | Missing coverage |
|---:|---|
| 273 | `false` branch |

### Line 280 — `checkClient(String) : void`

| Line | Missing coverage |
|---:|---|
| 281 | `true` branch |
| 290 | `true` branch |
| 295 | `true` branch |
| 300 | `true` branch |
| 306 | `true` branch |
| 310 | `true` branch |

### Line 320 — `redirectErrorToClient(OIDCResponseMode,String,String) : Response`

| Line | Missing coverage |
|---:|---|
| 335 | `false` branch |

### Line 342 — `updateAuthenticationSession() : void`

| Line | Missing coverage |
|---:|---|
| 351 | `true` branch |
| 352 | `true` branch |
| 357 | `false` branch |
| 362 | `true` branch |
| 362 | `false` branch |
| 374 | `true` branch |
| 374 | `false` branch |
| 378 | `true` branch |
| 378 | `false` branch |
| 391 | `false` branch |
| 398 | `true` branch |

### Line 447 — `performActionOnParameters(AuthorizationEndpointRequest,BiConsumer<String, String>) : void`

| Line | Missing coverage |
|---:|---|
| 452 | `true` branch |
